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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HermitCrabSprite;

public class HermitCrab extends Crab {

	{
		spriteClass = HermitCrabSprite.class;

		HP = HT = 45; //31 // 25
		baseSpeed = 1f; //1

		//3x more likely to drop meat, and drops a guaranteed armor
		lootChance = 0.5f;
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();

		if (Dungeon.hero.lvl <= maxLvl + 2){
			Dungeon.level.drop(Generator.randomArmor(), pos).sprite.drop();
		}
	}

	@Override
	public int drRoll() {
		return super.drRoll() + 2; //2-6 DR total, up from 0-4
	}

    @Override
    public void damage(int dmg, Object src) {
        int DMGRDC = 5;

        float scaleFactor = AscensionChallenge.statModifier(this);
        int scaledDmg = Math.round(dmg/scaleFactor);

        if (scaledDmg >= DMGRDC+1){
            scaledDmg = DMGRDC + (int)(Math.sqrt((DMGRDC*2)*(scaledDmg - DMGRDC) + 1) - 1)/2;
        }

        dmg = (int)(scaledDmg*AscensionChallenge.statModifier(this));
        super.damage(dmg, src);
    }

}
