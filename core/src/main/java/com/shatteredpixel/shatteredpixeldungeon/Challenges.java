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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
	public static final int CHAMPION_ENEMIES	= 128;
	public static final int STRONGER_BOSSES 	= 256;

    public static final int BACK_TO_ORIGINS    = 512; // Next power of 2
    public static final int I_HATE_MYSELF           = 1024;

	public static final int MAX_VALUE           = 2047;

	public static final String[] NAME_IDS = {
			"champion_enemies",
			"stronger_bosses",
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls",
            "back_to_origins",
            "i_hate_myself"
	};

	public static final int[] MASKS = {
			CHAMPION_ENEMIES, STRONGER_BOSSES, NO_FOOD, NO_ARMOR,
            NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS,
            NO_SCROLLS,

            BACK_TO_ORIGINS, I_HATE_MYSELF
	};

	public static int activeChallenges(){
		int chCount = 0;
		for (int ch : Challenges.MASKS){
			if ((Dungeon.challenges & ch) != 0) chCount++;
		}
		return chCount;
	}

	public static boolean isItemBlocked( Item item ){

		if (Dungeon.isChallenged(NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

        if (Dungeon.isChallenged(Challenges.I_HATE_MYSELF) && item instanceof Dewdrop) {
            return true;
        }
        if (Dungeon.isChallenged(Challenges.I_HATE_MYSELF) && item instanceof Stylus) {
            return true;
        }
        if (Dungeon.isChallenged(Challenges.I_HATE_MYSELF) && (item instanceof ScrollOfIdentify)) {
            return true;
        }
        if (Dungeon.isChallenged(Challenges.I_HATE_MYSELF) && item instanceof Pasty) {
            return true;
        }

		return false;

	}

}