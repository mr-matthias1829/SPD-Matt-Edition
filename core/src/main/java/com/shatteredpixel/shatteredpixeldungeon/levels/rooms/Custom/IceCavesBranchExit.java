package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.IceCavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Point;

// Room 3: The FORWARD exit in Branch 2
// This exits the ice caves and continues to the next floor in Branch 0
// Branch 2, Floor X → Branch 0, Floor X+1
public class IceCavesBranchExit extends CaveRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{1, 0, 0}; // Same as MineEntrance
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
    public boolean isExit() {
        return false;
    }

    @Override
    public void paint(Level level) {

        // Fill with walls and empty floor
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        // Set regular doors for connections
        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        // Get center point for the exit
        Point c = center();

        // Create exit structure
        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, Terrain.WALL);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, Terrain.EMPTY_SP);

        // Set the center cell as exit
        int exitCell = level.pointToCell(c);
        Painter.set(level, exitCell, Terrain.EXIT);

        // Create the level transition to NEXT floor in Branch 0
        // This progresses the dungeon - depth + 1
        LevelTransition exit = new LevelTransition(
                level,
                exitCell,
                LevelTransition.Type.BRANCH_EXIT,
                Dungeon.depth + 1,
                0,
                LevelTransition.Type.BRANCH_ENTRANCE,
                "icecaves_branch_exit",
                "caves_boss_entrance"
        );
        exit.top--;
        exit.left--;
        exit.right++;
        level.transitions.add(exit);
    }
}