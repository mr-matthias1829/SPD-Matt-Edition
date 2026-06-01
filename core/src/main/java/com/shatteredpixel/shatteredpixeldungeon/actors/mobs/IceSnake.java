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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.sprites.IceSnakeSprite;
import com.watabou.utils.Random;

public class IceSnake extends Snake {
	
	{
		spriteClass = IceSnakeSprite.class;
		
		HP = HT = 24;
		defenseSkill = 45;
		
		EXP = 5;
		maxLvl = 15;
        properties.add(Property.ICY);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 10, 16 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 18;
	}

    @Override
    protected boolean doAttack(Char enemy) {
        if (Random.Int(3) == 0) {
            Buff.prolong(enemy, Chill.class, 2f);
        }
        return super.doAttack(enemy);
    }
}
