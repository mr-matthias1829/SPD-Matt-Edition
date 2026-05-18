package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;

// This is the entrance room that appears in Branch 2
// It connects back to the IceCavesEntrance skeleton door in Branch 0
public class IceCavesBranchEntrance extends CaveRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{1, 0, 0}; // Same as MineEntrance
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

        // Fill with walls and empty floor
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        // Set regular doors for connections
        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        // Get center point for the entrance
        Point c = center();

        // Create entrance structure
        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, Terrain.WALL);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, Terrain.EMPTY_SP);

        // Set the center cell as entrance
        int entranceCell = level.pointToCell(c);
        Painter.set(level, entranceCell, Terrain.ENTRANCE);

        // Create the level transition back to Branch 0
        // This connects to the IceCavesEntrance room's skeleton door
        LevelTransition entrance = new LevelTransition(
                level,
                entranceCell,
                LevelTransition.Type.BRANCH_ENTRANCE,
                Dungeon.depth, // Same depth in branch 0
                0, // Back to main branch
                LevelTransition.Type.BRANCH_EXIT,
                "ice_caves_branch_entrance", // Custom name for the transition
                "ice_caves_entrance"
        );
        entrance.top--;
        entrance.left--;
        entrance.right++;
        level.transitions.add(entrance);
    }
}