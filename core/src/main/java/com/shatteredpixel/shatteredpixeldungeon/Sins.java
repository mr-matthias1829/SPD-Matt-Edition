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

package com.shatteredpixel.shatteredpixeldungeon;

public class Sins {

    public static final int GREED           = 1;
    public static final int SLOTH           = 2;
    public static final int WRATH           = 4;
    public static final int GLUTTONY        = 8;
    public static final int ENVY            = 16;
    public static final int PRIDE           = 32;
    public static final int DESIRE            = 64;

    // These sins are currently unused, originally planned to go with the other 7, but cut for time and complexity of the already big update
    //public static final int DEATH           = 128;
    //public static final int YENDOR           = 256;

    public static final int MAX_SINS        = 7;
    public static final int MAX_VALUE       = 511;

    public static final String[] NAME_IDS = {
            "greed",
            "sloth",
            "wrath",
            "gluttony",
            "envy",
            "pride",
            "desire"
            //"death"
            //"yendor"
    };

    public static final int[] MASKS = {
            GREED, SLOTH, WRATH, GLUTTONY, ENVY, PRIDE, DESIRE
            //DEATH, YENDOR
    };

    public static int activeSins(){
        return activeSins(Dungeon.sins);
    }

    public static int activeSins(int mask){
        int sinCount = 0;
        for (int sin : Sins.MASKS){
            if ((mask & sin) != 0) sinCount++;
        }
        return sinCount;
    }

    public static int totalSins() {
        return MASKS.length;
    }

    // Helper method to check if a specific sin is active
    /* already defined in dungeon.java so should be fine

    public static boolean isSinActive(int sinMask){
        return (Dungeon.sins & sinMask) != 0;
    }
     */

}