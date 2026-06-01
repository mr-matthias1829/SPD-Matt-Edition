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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.round;

public class FetidRat extends Rat {


	{
		spriteClass = FetidRatSprite.class;

        setLevel( Dungeon.scalingDepth() );
	}

    public void setLevel( int depth ){
        int lvl = 0;
        if (depth < 5) {
            lvl = 0;
            WANDERING = new Wandering();
            state = WANDERING;
        } else if (depth < 15){
            lvl = 1;
        } else {
            lvl = 2;
        }
        this.level = lvl;
        adjustStats(level);
    }
    public void adjustStats( int level ) {
        HP = HT = (int) (20 * (1+level * 0.65));// was a set value before //36 //20
        defenseSkill = 8 * (1+level); //5

        EXP = (int) (3 * (1+level*0.25)); //4
        properties.add(Property.DEMONIC);

        maxLvl = 10 + level * 5;

        if (level == 0) {
            properties.add(Property.MINIBOSS);
        }
    }

	@Override
	public int attackSkill( Char target ) {
		return 12 * (1+level);
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 2);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		//damage = super.attackProc( enemy, damage );
        //damage = Random.NormalIntRange( 2, 5 ); // inherited before, was 1,4
        damage = Random.NormalIntRange(2 * (1+level), (int) (5 * (1+level * 1.3)));
		if (Random.Int(3) == 0) {
			Buff.affect(enemy, Ooze.class).set( Ooze.DURATION );
			//score loss is on-hit instead of on-attack because it's tied to ooze
			if (enemy == Dungeon.hero && !Dungeon.level.water[enemy.pos]){
                if (level == 0) {
                    Statistics.questScores[0] -= 50;
                }
			}
		}

		return damage;
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		GameScene.add(Blob.seed(pos, 20, StenchGas.class));

		return super.defenseProc(enemy, damage);
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
	}

	protected class Wandering extends Mob.Wandering{
		@Override
		protected int randomDestination() {
			//of two potential wander positions, picks the one closest to the hero
			int pos1 = super.randomDestination();
			int pos2 = super.randomDestination();
			PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
			if (PathFinder.distance[pos2] < PathFinder.distance[pos1]){
				return pos2;
			} else {
				return pos1;
			}
		}
	}
	
	{
		immunities.add( StenchGas.class );
	}


    private int level;
    private static final String LEVEL	= "level";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEVEL, level );
    }
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        level = bundle.getInt( LEVEL );
        adjustStats(level);
        super.restoreFromBundle(bundle);
    }

}