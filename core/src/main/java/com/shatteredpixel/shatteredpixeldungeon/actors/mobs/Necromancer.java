/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

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
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Necromancer extends Mob {

    {
        spriteClass = NecromancerSprite.class;

        HP = HT = 40;//45 //32 //20 //40
        defenseSkill = 10; //14

        EXP = 5; //7
        maxLvl = 14;

        loot = PotionOfHealing.class;
        lootChance = 0.2f; //see lootChance()

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    protected boolean firstSummon = true;

    protected ArrayList<NecroSkeleton> mySkeletons = new ArrayList<>();
    protected ArrayList<Integer> storedSkeletonIDs = new ArrayList<>();
    private int maxSkeletons = 3; // Change this number for more skeletons!

    @Override
    protected boolean act() {
        if (summoning && state != HUNTING){
            summoning = false;
            if (sprite instanceof NecromancerSprite) ((NecromancerSprite) sprite).cancelSummoning();
        }
        return super.act();
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        // Update for multiple skeletons
        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null && skeleton.isAlive()
                    && Dungeon.level.mobs.contains(skeleton)
                    && skeleton.alignment == alignment){
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
    public Item createLoot(){
        Dungeon.LimitedDrops.NECRO_HP.count++;
        return super.createLoot();
    }


    @Override
    public void die(Object cause) {
        // Kill all skeletons when necromancer dies
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

    private static final String SUMMONING = "summoning";
    private static final String FIRST_SUMMON = "first_summon";
    private static final String SUMMONING_POS = "summoning_pos";
    private static final String MY_SKELETONS = "my_skeletons";
    private static final String MAX_SKELETONS = "max_skeletons";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( SUMMONING, summoning );
        bundle.put( FIRST_SUMMON, firstSummon );
        if (summoning){
            bundle.put( SUMMONING_POS, summoningPos);
        }

        // Store skeleton IDs
        ArrayList<Integer> skeletonIDs = new ArrayList<>();
        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null) {
                skeletonIDs.add(skeleton.id());
            }
        }
        skeletonIDs.addAll(storedSkeletonIDs);
        int[] skeletonIDArray = new int[skeletonIDs.size()];
        for (int i = 0; i < skeletonIDs.size(); i++) {
            skeletonIDArray[i] = skeletonIDs.get(i);
        }
        bundle.put( MY_SKELETONS, skeletonIDArray);
        bundle.put( MAX_SKELETONS, maxSkeletons );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summoning = bundle.getBoolean( SUMMONING );
        if (bundle.contains(FIRST_SUMMON)) firstSummon = bundle.getBoolean(FIRST_SUMMON);
        if (summoning){
            summoningPos = bundle.getInt( SUMMONING_POS );
        }

        if (bundle.contains( MY_SKELETONS )){
            int[] skeletonIDs = bundle.getIntArray( MY_SKELETONS );
            if (skeletonIDs != null) {
                for (int id : skeletonIDs) {
                    storedSkeletonIDs.add(id);
                }
            }
        }
        if (bundle.contains(MAX_SKELETONS)) {
            maxSkeletons = bundle.getInt(MAX_SKELETONS);
        }
    }

    public void onZapComplete(){
        // Heal/buff the most damaged skeleton
        NecroSkeleton targetSkeleton = null;
        for (NecroSkeleton skeleton : mySkeletons) {
            if (skeleton != null && skeleton.isAlive() && skeleton.sprite != null) {
                if (targetSkeleton == null || skeleton.HP < targetSkeleton.HP) {
                    targetSkeleton = skeleton;
                }
            }
        }

        if (targetSkeleton == null) {
            return;
        }

        //heal skeleton first
        if (targetSkeleton.HP < targetSkeleton.HT){
            if (sprite.visible || targetSkeleton.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), targetSkeleton.sprite.center()));
            }

            targetSkeleton.HP = Math.min(targetSkeleton.HP + targetSkeleton.HT/5, targetSkeleton.HT);
            if (targetSkeleton.sprite.visible) {
                targetSkeleton.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString( targetSkeleton.HT/5 ), FloatingText.HEALING );
            }

            //otherwise give it adrenaline
        } else if (targetSkeleton.buff(Adrenaline.class) == null) {
            if (sprite.visible || targetSkeleton.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), targetSkeleton.sprite.center()));
            }

            Buff.affect(targetSkeleton, Adrenaline.class, 3f);
        }

        next();
    }

    public void summonMinion(){
        if (Actor.findChar(summoningPos) != null) {
            int pushPos = pos;
            for (int c : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(summoningPos + c) == null
                        && Dungeon.level.passable[summoningPos + c]
                        && (Dungeon.level.openSpace[summoningPos + c] || !hasProp(Actor.findChar(summoningPos), Property.LARGE))
                        && Dungeon.level.trueDistance(pos, summoningPos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
                    pushPos = summoningPos + c;
                }
            }

			//push enemy, or wait a turn if there is no valid pushing position
			if (pushPos != pos) {

				//no push if char is immovable, move our skeleton instead
				if (Char.hasProp(Actor.findChar(summoningPos), Property.IMMOVABLE)){
					summoningPos = pushPos;
				} else {
				Char ch = Actor.findChar(summoningPos);
				Actor.add( new Pushing( ch, ch.pos, pushPos ) );

				ch.pos = pushPos;
				Dungeon.level.occupyCell(ch );
				}

            } else {

				//attempt to damage the blocker in addition to waiting
                Char blocker = Actor.findChar(summoningPos);
                if (blocker.alignment != alignment){
                    blocker.damage( Random.NormalIntRange(2, 10), new SummoningBlockDamage() );
                    if (blocker == Dungeon.hero && !blocker.isAlive()){
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail(this);
                        GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
                    }
                }
                spend(TICK);
                return;
            }
        }

        summoning = firstSummon = false;

        NecroSkeleton newSkeleton = new NecroSkeleton();
        newSkeleton.pos = summoningPos;
        GameScene.add( newSkeleton );
        Dungeon.level.occupyCell( newSkeleton );
        mySkeletons.add(newSkeleton);
        ((NecromancerSprite)sprite).finishSummoning();
		/*
        if (mySkeleton == null || !mySkeleton.isActive()) {
			mySkeleton = new NecroSkeleton();
			mySkeleton.pos = summoningPos;
			GameScene.add(mySkeleton);
			Dungeon.level.occupyCell(mySkeleton);

		 */

        for (Buff b : buffs()){
            if (b.revivePersists) {
                Buff.affect(newSkeleton, b.getClass());
            }
        }
		} else {
			ScrollOfTeleportation.appear(mySkeleton, summoningPos);
		}
		((NecromancerSprite)sprite).finishSummoning();
    }

    public static class SummoningBlockDamage{}

    private class Hunting extends Mob.Hunting{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;

            if (enemySeen){
                target = enemy.pos;
            }

            // Restore stored skeletons
            if (!storedSkeletonIDs.isEmpty()) {
                ArrayList<Integer> toRemove = new ArrayList<>();
                for (int id : storedSkeletonIDs) {
                    Actor ch = Actor.findById(id);
                    if (ch instanceof NecroSkeleton) {
                        mySkeletons.add((NecroSkeleton) ch);
                        toRemove.add(id);
                    }
                }
                storedSkeletonIDs.removeAll(toRemove);
            }

            if (summoning){
                summonMinion();
                return true;
            }

            // Clean up dead skeletons
            ArrayList<NecroSkeleton> toRemove = new ArrayList<>();
            for (NecroSkeleton skeleton : mySkeletons) {
                if (skeleton == null || !skeleton.isAlive()
                        || !Dungeon.level.mobs.contains(skeleton)
                        || skeleton.alignment != alignment) {
                    toRemove.add(skeleton);
                }
            }
            mySkeletons.removeAll(toRemove);

            //if enemy is seen, and enemy is within range, and we have less than max skeletons, summon a skeleton!
            if (enemySeen && Dungeon.level.distance(pos, enemy.pos) <= 4 && mySkeletons.size() < maxSkeletons){

                summoningPos = -1;

                //we can summon around blocking terrain, but not through it, except unlocked doors
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                BArray.or(Dungeon.level.passable, passable, passable);
                PathFinder.buildDistanceMap(pos, passable, Dungeon.level.distance(pos, enemy.pos)+3);

                for (int c : PathFinder.NEIGHBOURS8){
                    if (Actor.findChar(enemy.pos+c) == null
                            && PathFinder.distance[enemy.pos+c] != Integer.MAX_VALUE
                            && Dungeon.level.passable[enemy.pos+c]
                            && (!hasProp(Necromancer.this, Property.LARGE) || Dungeon.level.openSpace[enemy.pos+c])
                            && fieldOfView[enemy.pos+c]
                            && Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, summoningPos)){
                        summoningPos = enemy.pos+c;
                    }
                }

                if (summoningPos != -1){

                    summoning = true;
                    sprite.zap( summoningPos );

                    if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]){
                        Dungeon.hero.interrupt();
                    }

                    spend( firstSummon ? TICK : 5*TICK );
                } else {
                    //wait for a turn
                    spend(TICK);
                }

                return true;
                //otherwise, if enemy is seen, and we have skeletons...
            } else if (enemySeen && !mySkeletons.isEmpty()){

                spend(TICK);

                // Find a skeleton that needs healing/buffing
                NecroSkeleton targetSkeleton = null;
                for (NecroSkeleton skeleton : mySkeletons) {
                    if (skeleton != null && skeleton.isAlive()) {
                        if (skeleton.HP < skeleton.HT || skeleton.buff(Adrenaline.class) == null) {
                            targetSkeleton = skeleton;
                            break;
                        }
                    }
                }

                /*
                if (targetSkeleton != null && !fieldOfView[targetSkeleton.pos]){
				boolean teleporting = false;
				//teleport our skeleton to the enemy if..
				//we can't see it
				if (!fieldOfView[mySkeleton.pos]){
					teleporting = true;

				//it has a relatively long path to reach the hero (e.g. it's blocked in a tunnelway)
				} else if (!mySkeleton.canAttack(enemy)){
					PathFinder.Path skelePath = Dungeon.findPath(mySkeleton, enemy.pos, Dungeon.level.passable, fieldOfView, true);
					*/

					if (skelePath.size() > 2*Dungeon.level.distance(pos, enemy.pos)){
						teleporting = true;
					}
				}

				if (teleporting){

                    //teleport them to the closest spot next to the enemy that can be seen
                    if (!Dungeon.level.adjacent(targetSkeleton.pos, enemy.pos)){
                        int telePos = -1;
                        for (int c : PathFinder.NEIGHBOURS8){
                            if (Actor.findChar(enemy.pos+c) == null
                                    && Dungeon.level.passable[enemy.pos+c]
                                    && fieldOfView[enemy.pos+c]
                                    && (Dungeon.level.openSpace[enemy.pos+c] || !Char.hasProp(targetSkeleton, Property.LARGE))
                                    && Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, telePos)){
                                telePos = enemy.pos+c;
                            }
                        }

                        if (telePos != -1){
                            ScrollOfTeleportation.appear(targetSkeleton, telePos);
                            targetSkeleton.teleportSpend();

                            if (sprite != null && sprite.visible){
                                sprite.zap(telePos);
                                return false;
                            } else {
                                onZapComplete();
                            }

                            // TODO: below is Evan's version
                            // supposedly this fixes necro's not teleporting skele's?
                            // look at it and implement it maybe? since we use lists instead of single skele
                            /*
							if (sprite != null && sprite.visible) {
								summoning = true;
								summoningPos = telePos;
								sprite.zap(telePos);
								if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]) {
									Dungeon.hero.interrupt();
								}
								spend(TICK); //2 ticks total, it can't be the first summon
                            }
                        }
                    }

                    return true;

                } else if (targetSkeleton != null) {

                    //zap skeleton
                    if (sprite != null && sprite.visible){
                        sprite.zap(targetSkeleton.pos);
                        return false;
                    } else {
                        onZapComplete();
                    }
                }

                return true;

                //otherwise, default to regular hunting behaviour
            } else {
                return super.act(enemyInFOV, justAlerted);
            }
        }
    }

    public static class NecroSkeleton extends Skeleton {

        {
            state = WANDERING;

            spriteClass = NecroSkeletonSprite.class;

            //no loot or exp
            maxLvl = -5;

            //20/25/30 health to start
            if (Random.Int(2) == 0){
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

        public static class NecroSkeletonSprite extends SkeletonSprite{

            public NecroSkeletonSprite(){
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