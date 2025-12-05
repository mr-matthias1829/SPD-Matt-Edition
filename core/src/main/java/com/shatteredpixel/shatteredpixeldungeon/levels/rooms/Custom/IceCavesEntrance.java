package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;

public class IceCavesEntrance extends CaveRoom {

    @Override
    public boolean isExit() {
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

        // Get center point for the locked entrance
        Point c = center();

        // Create the locked entrance structure (similar to sewer boss exit)
        // This creates a 3-wide structure with the locked exit in the center
        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, Terrain.WALL);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, Terrain.EMPTY_SP);

        // Set the center cell as locked exit (requires skeleton key)
        int entranceCell = level.pointToCell(c);
        Painter.set(level, entranceCell, Terrain.LOCKED_EXIT);

        // Create the level transition for the branch entrance
        LevelTransition entrance = new LevelTransition(
                level,
                entranceCell,
                LevelTransition.Type.BRANCH_EXIT,
                Dungeon.depth,
                2, // target branch id (your ice caves branch)
                LevelTransition.Type.BRANCH_ENTRANCE
        );
        // Expand the transition area to match the visual size
        entrance.top--;
        entrance.left--;
        entrance.right++;
        level.transitions.add(entrance);

        // Add skeleton key to the level
        level.addItemToSpawn(new WornKey(Dungeon.depth));
    }

    public static class BranchEntrance extends CustomTilemap {
        {
            texture = Assets.Environment.CAVES_QUEST;
            tileW = 3;
            tileH = 3;
        }

        private static final int[] layout = new int[]{
                0, 1, 2,
                3, 4, 5,
                6, 7, 8
        };

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(layout, 3);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name");
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc");
        }
    }
}