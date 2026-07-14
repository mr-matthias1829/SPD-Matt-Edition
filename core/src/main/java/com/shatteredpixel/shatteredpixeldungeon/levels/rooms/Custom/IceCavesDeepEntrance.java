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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.watabou.utils.Point;

// one-off entrance room, used ONLY on mainFloor+1 (B2F15).
// functions exactly like a normal EntranceRoom EXCEPT it targets the
// BRANCH_EXIT transition (IceCavesDeepExit) on the floor above instead of
// the REGULAR_EXIT transition (IceCavesBranchExit).
//
// why this exists: on mainFloor (B2F14) there are TWO forward transitions
// (REGULAR_EXIT -> branch 0, BRANCH_EXIT -> branch 2 deeper)
// the stock EntranceRoom always expects to pair with a REGULAR_EXIT on the previous
// depth, so using it here silently reconnects to the wrong transition
// (IceCavesBranchExit) instead of the one that actually leads here
// (IceCavesDeepExit). this room exists purely to fix that one pairing.
public class IceCavesDeepEntrance extends CaveRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{1, 0, 0};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public void paint(Level level) {

        // fill with walls and empty floor
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        // set regular doors for connections
        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        // get center point for the entrance
        Point c = center();

        // create entrance structure (matches IceCavesBranchEntrance's look)
        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, Terrain.WALL);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, Terrain.EMPTY_SP);

        // set the center cell as entrance
        int entranceCell = level.pointToCell(c);
        Painter.set(level, entranceCell, Terrain.ENTRANCE);

        // create the level transition back to mainFloor (B2F14), but
        // explicitly pair with the BRANCH_EXIT (IceCavesDeepExit) rather
        // than the REGULAR_EXIT (IceCavesBranchExit) that a stock
        // EntranceRoom would default to.
        LevelTransition entrance = new LevelTransition(
                level,
                entranceCell,
                LevelTransition.Type.REGULAR_ENTRANCE,
                Dungeon.depth - 1, // mainFloor (B2F14)
                2,                 // same branch, stay in branch 2
                LevelTransition.Type.BRANCH_EXIT, // match IceCavesDeepExit, not IceCavesBranchExit
                "ice_caves_deep_entrance",
                "ice_caves_deep_exit"
        );
        entrance.top--;
        entrance.left--;
        entrance.right++;
        level.transitions.add(entrance);
    }
}