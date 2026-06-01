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
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhoulSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpiritualNecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SpiritNecromancer extends Mob {

    {
        spriteClass = SpiritualNecromancerSprite.class;

        HP = HT = 65;
        defenseSkill = 26;

        EXP = 7;
        maxLvl = 20;

        loot = PotionOfHealing.class;
        lootChance = 0.15f;

        properties.add(Property.INORGANIC);

        HUNTING = new Hunting();
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    protected boolean firstSummon = true;

    // Relocates existing ghouls when they become stuck or out of FOV
    public NecroGhoul teleportTarget = null;

    protected ArrayList<NecroGhoul> myGhouls = new ArrayList<>();
    protected ArrayList<Integer> storedGhoulIDs = new ArrayList<>();
    private int maxGhouls = 3;

    @Override
    protected boolean act() {
        if (summoning && state != HUNTING) {
            summoning = false;
            teleportTarget = null;
            if (sprite instanceof SpiritualNecromancerSprite) {
                ((SpiritualNecromancerSprite) sprite).cancelSummoning();
            }
        }
        return super.act();
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);

        for (NecroGhoul ghoul : myGhouls) {
            if (ghoul != null && ghoul.isAlive()
                    && Dungeon.level.mobs.contains(ghoul)
                    && ghoul.alignment == alignment) {
                ghoul.aggro(ch);
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
        // Ghouls are left entirely alive to handle their own native
        // behavior/revive cycles when their master dies.
        myGhouls.clear();
        storedGhoulIDs.clear();
        super.die(cause);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    private static final String SUMMONING      = "summoning";
    private static final String FIRST_SUMMON   = "first_summon";
    private static final String SUMMONING_POS  = "summoning_pos";
    private static final String MY_GHOULS      = "my_ghouls";
    private static final String MAX_GHOULS     = "max_ghouls";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(SUMMONING, summoning);
        bundle.put(FIRST_SUMMON, firstSummon);

        if (summoning) {
            bundle.put(SUMMONING_POS, summoningPos);
        }

        ArrayList<Integer> ghoulIDs = new ArrayList<>();
        for (NecroGhoul ghoul : myGhouls) {
            if (ghoul != null) ghoulIDs.add(ghoul.id());
        }
        ghoulIDs.addAll(storedGhoulIDs);

        int[] arr = new int[ghoulIDs.size()];
        for (int i = 0; i < ghoulIDs.size(); i++) {
            arr[i] = ghoulIDs.get(i);
        }

        bundle.put(MY_GHOULS, arr);
        bundle.put(MAX_GHOULS, maxGhouls);
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

        if (bundle.contains(MY_GHOULS)) {
            int[] ids = bundle.getIntArray(MY_GHOULS);
            if (ids != null) {
                for (int id : ids) {
                    storedGhoulIDs.add(id);
                }
            }
        }

        if (bundle.contains(MAX_GHOULS)) {
            maxGhouls = bundle.getInt(MAX_GHOULS);
        }
    }

    public void onZapComplete() {
        NecroGhoul target = null;

        for (NecroGhoul g : myGhouls) {
            if (g == null || !g.isActive() || g.sprite == null) continue;
            if (!fieldOfView[g.pos]) continue;

            if (target == null || g.HP < target.HP) {
                target = g;
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
                    blocker.damage(Random.NormalIntRange(2, 10), new Necromancer.SummoningBlockDamage());

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

        if (teleportTarget != null && teleportTarget.isAlive()) {
            ScrollOfTeleportation.appear(teleportTarget, summoningPos);
            teleportTarget = null;
            summoning = false;
            ((SpiritualNecromancerSprite) sprite).finishSummoning();
            return;
        }

        summoning = firstSummon = false;

        NecroGhoul newGhoul = new NecroGhoul();
        newGhoul.pos = summoningPos;

        GameScene.add(newGhoul);
        Dungeon.level.occupyCell(newGhoul);

        myGhouls.add(newGhoul);

        for (Buff b : buffs()) {
            if (b.revivePersists) {
                Buff.affect(newGhoul, b.getClass());
            }
        }

        ((SpiritualNecromancerSprite) sprite).finishSummoning();
    }

    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;

            if (enemySeen) {
                target = enemy.pos;
            }

            if (!storedGhoulIDs.isEmpty()) {
                ArrayList<Integer> remove = new ArrayList<>();

                for (int id : storedGhoulIDs) {
                    Actor ch = Actor.findById(id);
                    if (ch instanceof NecroGhoul) {
                        myGhouls.add((NecroGhoul) ch);
                        remove.add(id);
                    }
                }

                storedGhoulIDs.removeAll(remove);
            }

            if (summoning) {
                summonMinion();
                return true;
            }

            // Prune dead ghouls or ghouls that broke alignment rules while master lived
            myGhouls.removeIf(g -> g == null
                    || (!g.isAlive() && !g.beingLifeLinked)
                    || (!g.isActive() && !g.beingLifeLinked)
                    || (!Dungeon.level.mobs.contains(g) && !g.isActive() && !g.beingLifeLinked)
                    || (Dungeon.level.mobs.contains(g) && g.alignment != alignment));

            // --- Summon a new ghoul if under the cap and enemy is close ---
            if (enemySeen && Dungeon.level.distance(pos, enemy.pos) <= 4
                    && myGhouls.size() < maxGhouls) {

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

                    spend(firstSummon ? 2*TICK : 7 * TICK); // takes longer to summon than normal necro
                } else {
                    spend(TICK);
                }

                return true;
            }

            // --- Manage existing ghouls when enemy is in sight ---
            if (enemySeen && !myGhouls.isEmpty()) {
                spend(TICK);

                for (NecroGhoul g : myGhouls) {
                    if (g == null || !g.isAlive()) continue;

                    boolean needsTeleport = false;

                    if (!fieldOfView[g.pos]) {
                        needsTeleport = true;
                    } else if (!g.canAttack(enemy)) {
                        PathFinder.Path ghoulPath = Dungeon.findPath(
                                g, enemy.pos, Dungeon.level.passable, fieldOfView, true);

                        if (ghoulPath == null
                                || ghoulPath.size() > 2 * Dungeon.level.distance(pos, enemy.pos)) {
                            needsTeleport = true;
                        }
                    }

                    if (needsTeleport && !Dungeon.level.adjacent(g.pos, enemy.pos)) {
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
                            teleportTarget = g;
                            sprite.zap(telePos);

                            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]) {
                                Dungeon.hero.interrupt();
                            }

                            spend(TICK);
                        }

                        return true;
                    }
                }

                NecroGhoul targetGhoul = null;

                for (NecroGhoul g : myGhouls) {
                    if (g != null && g.isAlive() && fieldOfView[g.pos]) {
                        if (g.HP < g.HT || g.buff(Adrenaline.class) == null) {
                            targetGhoul = g;
                            break;
                        }
                    }
                }

                if (targetGhoul != null) {
                    if (sprite.visible) {
                        sprite.zap(targetGhoul.pos);
                        return false;
                    } else {
                        onZapComplete();
                        return true;
                    }
                }

                return true;
            }

            return super.act(enemyInFOV, justAlerted);
        }
    }

    public static class NecroGhoul extends Ghoul {

        {
            state = WANDERING;
            spriteClass = NecroGhoulSprite.class;
            maxLvl = -5;

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

        public static class NecroGhoulSprite extends GhoulSprite {

            public NecroGhoulSprite() {
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