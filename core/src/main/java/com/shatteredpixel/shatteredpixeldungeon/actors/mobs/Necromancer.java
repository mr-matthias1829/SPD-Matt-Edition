/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2026 Evan Debenham
 *
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Necromancer extends Mob {

    {
        spriteClass = NecromancerSprite.class;

        HP = HT = 40;
        defenseSkill = 10;

        EXP = 5;
        maxLvl = 14;

        loot = PotionOfHealing.class;
        lootChance = 0.2f;

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    protected boolean firstSummon = true;

    // When non-null, summonMinion() will teleport this skeleton instead of spawning a new one
    public NecroSkeleton teleportTarget = null;

    protected ArrayList<NecroSkeleton> mySkeletons = new ArrayList<>();
    protected ArrayList<Integer> storedSkeletonIDs = new ArrayList<>();
    private int maxSkeletons = 3;

    @Override
    protected boolean act() {
        if (summoning && state != HUNTING) {
            summoning = false;
            teleportTarget = null;
            if (sprite instanceof NecromancerSprite) {
                ((NecromancerSprite) sprite).cancelSummoning();
            }
        }
        return super.act();
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);

        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null && skeleton.isAlive()
                    && Dungeon.level.mobs.contains(skeleton)
                    && skeleton.alignment == alignment) {
                skeleton.aggro(ch);
            }
        }
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * ((6f - Dungeon.LimitedDrops.NECRO_HP.count) / 6f);
    }

    @Override
    public Item createLoot() {
        Dungeon.LimitedDrops.NECRO_HP.count++;
        return super.createLoot();
    }

    @Override
    public void die(Object cause) {
        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null && skeleton.isAlive()) {
                skeleton.die(null);
            }
        }
        mySkeletons.clear();
        storedSkeletonIDs.clear();
        super.die(cause);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    private static final String SUMMONING      = "summoning";
    private static final String FIRST_SUMMON   = "first_summon";
    private static final String SUMMONING_POS  = "summoning_pos";
    private static final String MY_SKELETONS   = "my_skeletons";
    private static final String MAX_SKELETONS  = "max_skeletons";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(SUMMONING, summoning);
        bundle.put(FIRST_SUMMON, firstSummon);

        if (summoning) {
            bundle.put(SUMMONING_POS, summoningPos);
        }

        ArrayList<Integer> skeletonIDs = new ArrayList<>();
        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null) skeletonIDs.add(skeleton.id());
        }
        skeletonIDs.addAll(storedSkeletonIDs);

        int[] arr = new int[skeletonIDs.size()];
        for (int i = 0; i < skeletonIDs.size(); i++) {
            arr[i] = skeletonIDs.get(i);
        }

        bundle.put(MY_SKELETONS, arr);
        bundle.put(MAX_SKELETONS, maxSkeletons);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        summoning = bundle.getBoolean(SUMMONING);
        if (bundle.contains(FIRST_SUMMON)) {
            firstSummon = bundle.getBoolean(FIRST_SUMMON);
        }

        if (summoning) {
            summoningPos = bundle.getInt(SUMMONING_POS);
        }

        if (bundle.contains(MY_SKELETONS)) {
            int[] ids = bundle.getIntArray(MY_SKELETONS);
            if (ids != null) {
                for (int id : ids) {
                    storedSkeletonIDs.add(id);
                }
            }
        }

        if (bundle.contains(MAX_SKELETONS)) {
            maxSkeletons = bundle.getInt(MAX_SKELETONS);
        }
    }

    public void onZapComplete() {
        System.out.println("ZAP COMPLETE");
        // FIX: only consider skeletons that are visible / in line of sight
        NecroSkeleton target = null;

        for (NecroSkeleton s : mySkeletons) {
            if (s == null || !s.isAlive() || s.sprite == null) continue;

            // Skip skeletons we have no sight line to
            if (!fieldOfView[s.pos]) continue;

            if (target == null || s.HP < target.HP) {
                target = s;
            }
        }

        if (target == null) {
            next();
            return;
        }

        if (target.HP < target.HT) {
            if (sprite.visible || target.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), target.sprite.center(), true));
                Sample.INSTANCE.play(Assets.Sounds.RAY);
            }

            target.HP = Math.min(target.HP + target.HT / 5, target.HT);

            if (target.sprite.visible) {
                target.sprite.showStatusWithIcon(
                        CharSprite.POSITIVE,
                        Integer.toString(target.HT / 5),
                        FloatingText.HEALING
                );
            }

        } else if (target.buff(Adrenaline.class) == null) {
            if (sprite.visible || target.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), target.sprite.center(), true));
                Sample.INSTANCE.play(Assets.Sounds.RAY);
            }

            Buff.affect(target, Adrenaline.class, 3f);
        }

        System.out.println("HIT NEXT");
        next();
    }

    public void summonMinion() {

        if (Actor.findChar(summoningPos) != null || !Dungeon.level.passable[summoningPos]) {

            int pushPos = pos;

            for (int c : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(summoningPos + c) == null
                        && Dungeon.level.passable[summoningPos + c]
                        && Dungeon.level.trueDistance(pos, summoningPos + c)
                        > Dungeon.level.trueDistance(pos, pushPos)) {
                    pushPos = summoningPos + c;
                }
            }

            if (pushPos != pos) {

                Char ch = Actor.findChar(summoningPos);

                if (ch == null || Char.hasProp(ch, Property.IMMOVABLE)) {
                    summoningPos = pushPos;
                } else {
                    Actor.add(new Pushing(ch, ch.pos, pushPos));
                    ch.pos = pushPos;
                    Dungeon.level.occupyCell(ch);
                }

            } else {

                Char blocker = Actor.findChar(summoningPos);
                if (blocker != null && blocker.alignment != alignment) {
                    blocker.damage(Random.NormalIntRange(2, 10), new SummoningBlockDamage());

                    if (blocker == Dungeon.hero && !blocker.isAlive()) {
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail(this);
                        GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
                    }
                }

                spend(TICK);
                return;
            }
        }

        // FIX: if we flagged a skeleton for teleporting, move it instead of spawning
        if (teleportTarget != null && teleportTarget.isAlive()) {
            ScrollOfTeleportation.appear(teleportTarget, summoningPos);
            teleportTarget = null;
            summoning = false;
            ((NecromancerSprite) sprite).finishSummoning();
            return;
        }

        summoning = firstSummon = false;

        NecroSkeleton newSkeleton = new NecroSkeleton();
        newSkeleton.pos = summoningPos;

        GameScene.add(newSkeleton);
        Dungeon.level.occupyCell(newSkeleton);

        mySkeletons.add(newSkeleton);

        for (Buff b : buffs()) {
            if (b.revivePersists) {
                Buff.affect(newSkeleton, b.getClass());
            }
        }

        ((NecromancerSprite) sprite).finishSummoning();
    }

    public static class SummoningBlockDamage {}

    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            enemySeen = enemyInFOV;

            if (enemySeen) {
                target = enemy.pos;
            }

            // Restore skeletons from saved IDs after a load
            if (!storedSkeletonIDs.isEmpty()) {
                ArrayList<Integer> remove = new ArrayList<>();

                for (int id : storedSkeletonIDs) {
                    Actor ch = Actor.findById(id);
                    if (ch instanceof NecroSkeleton) {
                        mySkeletons.add((NecroSkeleton) ch);
                        remove.add(id);
                    }
                }

                storedSkeletonIDs.removeAll(remove);
            }

            if (summoning) {
                summonMinion();
                return true;
            }

            // Prune dead / missing skeletons
            mySkeletons.removeIf(s -> s == null || !s.isAlive());

            // --- Summon a new skeleton if we're under the cap and enemy is in range ---
            if (enemySeen && Dungeon.level.distance(pos, enemy.pos) <= 4
                    && mySkeletons.size() < maxSkeletons) {

                summoningPos = -1;

                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                BArray.or(Dungeon.level.passable, passable, passable);

                PathFinder.buildDistanceMap(pos, passable,
                        Dungeon.level.distance(pos, enemy.pos) + 3);

                for (int c : PathFinder.NEIGHBOURS8) {
                    int cell = enemy.pos + c;

                    if (Actor.findChar(cell) == null
                            && PathFinder.distance[cell] != Integer.MAX_VALUE
                            && Dungeon.level.passable[cell]
                            && Dungeon.level.heroFOV[cell]) {

                        summoningPos = cell;
                        break;
                    }
                }

                if (summoningPos != -1) {
                    summoning = true;
                    sprite.zap(summoningPos);

                    if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]) {
                        Dungeon.hero.interrupt();
                    }

                    spend(firstSummon ? TICK : 5 * TICK);
                } else {
                    spend(TICK);
                }

                return true;
            }

            // --- Manage existing skeletons when enemy is in sight ---
            if (enemySeen && !mySkeletons.isEmpty()) {

                spend(TICK);

                // FIX: check each skeleton — teleport any that are stuck or out of FOV
                for (NecroSkeleton s : mySkeletons) {
                    if (s == null || !s.isAlive()) continue;

                    boolean needsTeleport = false;

                    if (!fieldOfView[s.pos]) {
                        // Skeleton is completely out of our sight — teleport it
                        needsTeleport = true;
                    } else if (!s.canAttack(enemy)) {
                        // Skeleton can't reach the enemy — check path length
                        PathFinder.Path skelePath = Dungeon.findPath(
                                s, enemy.pos, Dungeon.level.passable, fieldOfView, true);

                        if (skelePath == null
                                || skelePath.size() > 2 * Dungeon.level.distance(pos, enemy.pos)) {
                            needsTeleport = true;
                        }
                    }

                    if (needsTeleport && !Dungeon.level.adjacent(s.pos, enemy.pos)) {

                        int telePos = -1;
                        for (int c : PathFinder.NEIGHBOURS8) {
                            int cell = enemy.pos + c;
                            if (Actor.findChar(cell) == null
                                    && Dungeon.level.passable[cell]
                                    && fieldOfView[cell]
                                    && Dungeon.level.trueDistance(pos, cell)
                                    < Dungeon.level.trueDistance(pos, telePos)) {
                                telePos = cell;
                            }
                        }

                        if (telePos != -1 && sprite != null && sprite.visible) {
                            summoning = true;
                            summoningPos = telePos;
                            teleportTarget = s; // remember which skeleton we're relocating
                            sprite.zap(telePos);

                            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]) {
                                Dungeon.hero.interrupt();
                            }

                            spend(TICK);
                        }

                        return true;
                    }
                }

                // FIX: only zap a skeleton we actually have line of sight to
                NecroSkeleton targetSkeleton = null;

                for (NecroSkeleton s : mySkeletons) {
                    if (s != null && s.isAlive() && fieldOfView[s.pos]) {
                        if (s.HP < s.HT || s.buff(Adrenaline.class) == null) {
                            targetSkeleton = s;
                            break;
                        }
                    }
                }

                if (targetSkeleton != null) {
                    if (sprite.visible) {
                        sprite.zap(targetSkeleton.pos);
                        return false;
                    } else {
                        onZapComplete();
                        return true;
                    }
                }

                return true;
            }

            // Default hunting behaviour (move toward enemy, etc.)
            return super.act(enemyInFOV, justAlerted);
        }
    }

    public static class NecroSkeleton extends Skeleton {

        {
            state = WANDERING;
            spriteClass = NecroSkeletonSprite.class;
            maxLvl = -5;

            // funny variance
            if (Random.Int(2) == 0) {
                HP = HT = 25;
                if (Random.Int(3) == 0) {
                    HP = HT = 30;
                }
            } else {
                HP = HT = 20;
            }
        }

        @Override
        public float spawningWeight() {
            return 0;
        }

        public static class NecroSkeletonSprite extends SkeletonSprite {

            public NecroSkeletonSprite() {
                super();
                brightness(0.75f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                brightness(0.75f);
            }
        }
    }
}