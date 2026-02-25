/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BanditSprite;
import com.watabou.utils.Random;

public class Bandit extends Thief {
	
	public Item item;
	
	{
		spriteClass = BanditSprite.class;

		//guaranteed first drop, then 1/3, 1/9, etc.
		//lootChance = 1f;



        HP = HT = 45;
        defenseSkill = 22;
        baseSpeed = 1f; // 0.85f

        EXP = 3;
        maxLvl = 19;

        loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
        lootChance = 0.03f; //initially, see lootChance()

        properties.add(Property.UNDEAD);
	}

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 6, 12 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 20;
    }
	
	@Override
	protected boolean steal( Hero hero ) {
		if (super.steal( hero )) {
			
			Buff.prolong( hero, Blindness.class, Blindness.DURATION/2f );
			//Buff.affect( hero, Poison.class ).set(Random.IntRange(5, 6) );
			Buff.prolong( hero, Cripple.class, Cripple.DURATION/2f );
			Dungeon.observe();
			
			return true;
		} else {
			return false;
		}
	}


    @Override
    public float lootChance() {
        //each drop makes future drops 1/3 as likely
        // so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
        return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.THEIF_MISC.count);
    }
	
}
