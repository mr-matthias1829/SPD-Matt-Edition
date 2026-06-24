/*
 *  Matt Edition
 *  Copyright (C) 2025-2026 Dum Matt
 *
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ThrowingChains extends Item {

    public static final int MAX_RANGE = 5;
    public static final String AC_USE = "USE";

    {
        image = ItemSpriteSheet.CHAINS;
        defaultAction = AC_USE;
        stackable = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)) {
            curUser = hero;

            if (hero.rooted) {
                GLog.w(Messages.get(ThrowingChains.class, "rooted"));
                return;
            }

            GameScene.selectCell(targetSelector);
        }
    }

    private CellSelector.Listener targetSelector = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {
            if (target == null) return;

            if (Dungeon.level.trueDistance(curUser.pos, target) > MAX_RANGE) {
                GLog.w(Messages.get(ThrowingChains.class, "too_far"));
                return;
            }

            PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            if (PathFinder.distance[curUser.pos] == Integer.MAX_VALUE) {
                GLog.w(Messages.get(ThrowingChains.class, "no_path"));
                return;
            }

            if (Actor.findChar(target) != null) {
                GLog.w(Messages.get(ThrowingChains.class, "hit_char"));
                return;
            }

            final Ballistica chain = new Ballistica(curUser.pos, target, Ballistica.STOP_SOLID);

            // block if a wall was hit before reaching the target
            if (Dungeon.level.solid[chain.collisionPos] || !chain.subPath(1, chain.dist).contains(target)) {
                GLog.w(Messages.get(ThrowingChains.class, "blocked"));
                return;
            }

            boolean consumed = grapple(target, curUser);

            if (consumed) {
                detach(curUser.belongings.backpack);
                updateQuickslot();
                Sample.INSTANCE.play(Assets.Sounds.MISS);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(ThrowingChains.class, "prompt");
        }
    };

    private boolean grapple(final int landing, final Hero hero) {
        if (Dungeon.level.solid[landing]) {
            GLog.w(Messages.get(ThrowingChains.class, "blocked"));
            return false;
        }

        hero.busy();
        hero.sprite.parent.add(new Chains(
                hero.sprite.center(),
                DungeonTilemap.raisedTileCenterToWorld(landing),
                Effects.Type.CHAIN,
                new Callback() {
                    public void call() {
                        Actor.add(new Pushing(hero, hero.pos, landing, new Callback() {
                            public void call() {
                                Dungeon.level.occupyCell(hero);
                            }
                        }));
                        hero.pos = landing;
                        hero.spendAndNext(1f);
                        Dungeon.observe();
                        GameScene.updateFog();
                    }
                },
                false
        ));
        return true;
    }

    @Override
    public int value() {
        return Math.round(18f * quantity);
    }
}