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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooplingSprite;
import com.watabou.utils.Random;

public class Goopling extends Mob {
	
	{
		spriteClass = GooplingSprite.class;
		
		HP = HT = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 12 : 9; //12
		defenseSkill = 5;

        properties.add(Property.ACIDIC);
		
		EXP = 0;
		maxLvl = 1;
	}
	
	@Override
	public int damageRoll() {
        if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
            return Random.NormalIntRange( 2, 5 );
        }
        return Random.NormalIntRange( 2, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

    @Override
    public int attackProc( Char enemy, int damage ) {
        if (Random.Int( 10 ) == 0) {
            Buff.affect( enemy, Ooze.class ).set( Ooze.DURATION );
            enemy.sprite.burst( 0x000000, 5 );
        }

        return super.attackProc( enemy, damage );
    }
	
	@Override
	public void damage(int dmg, Object src) {
        int DMGRDC = 2;

		float scaleFactor = AscensionChallenge.statModifier(this);
		int scaledDmg = Math.round(dmg/scaleFactor);

        if (scaledDmg >= DMGRDC+1){
            scaledDmg = DMGRDC + (int)(Math.sqrt((DMGRDC*2)*(scaledDmg - DMGRDC) + 1) - 1)/2;
        }

        /*
		if (scaledDmg >= 5){
			//takes 5/6/7/8/9/10 dmg at 5/7/10/14/19/25 incoming dmg
			scaledDmg = 4 + (int)(Math.sqrt(8*(scaledDmg - 4) + 1) - 1)/2;
		}

         */
		dmg = (int)(scaledDmg*AscensionChallenge.statModifier(this));
		super.damage(dmg, src);
	}
}
