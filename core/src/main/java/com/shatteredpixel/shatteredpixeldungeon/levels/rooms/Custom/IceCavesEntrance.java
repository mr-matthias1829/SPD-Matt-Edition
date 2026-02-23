package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ElderGnoll;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class IceCavesEntrance extends CaveRoom {

    private int entranceCell = -1; // Store the locked door position

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
        entranceCell = level.pointToCell(c);
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
        //level.addItemToSpawn(new WornKey(Dungeon.depth));

        // Spawn Elder Gnoll - use terrain-based check since passable[] isn't built yet
        spawnElderGnoll(level);
    }

    // Custom positioning when hero arrives from Ice Caves
    // This is called by RegularLevel or wherever you handle hero placement
    public int getCustomArrivalPosition(Level level) {
        // Check if the door is still locked
        if (entranceCell != -1 && level.map[entranceCell] == Terrain.LOCKED_EXIT) {
            // Door is locked - place hero one tile BELOW the locked door
            // The tile at entranceCell is the transition, below is safe ground
            int belowDoor = entranceCell + level.width();

            // Verify it's a valid position
            if (belowDoor < level.length() &&
                    level.passable[belowDoor] &&
                    !level.solid[belowDoor]) {
                return belowDoor;
            }
        }

        // Door is unlocked or position invalid - use default
        return -1;
    }

    private void spawnElderGnoll(Level level) {
        ArrayList<Integer> candidates = new ArrayList<>();

        // Search for walkable terrain types across the entire level
        for (int i = 0; i < level.length(); i++) {
            int terrain = level.map[i];

            // Check for terrain types that are walkable
            if (terrain == Terrain.EMPTY ||
                    terrain == Terrain.EMPTY_DECO ||
                    terrain == Terrain.GRASS ||
                    terrain == Terrain.EMBERS ||
                    terrain == Terrain.EMPTY_SP) {

                // Skip cells that are transitions (manually check without using inside())
                // Transitions are typically on ENTRANCE, EXIT, or LOCKED_EXIT terrain
                if (terrain != Terrain.ENTRANCE &&
                        terrain != Terrain.EXIT &&
                        terrain != Terrain.LOCKED_EXIT) {
                    candidates.add(i);
                }
            }
        }

        //GLog.w("Elder Gnoll spawn candidates: " + candidates.size());

        if (!candidates.isEmpty()) {
            int cell = Random.element(candidates);

            ElderGnoll mob = new ElderGnoll();
            mob.pos = cell;
            level.mobs.add(mob);

            //GLog.w("Elder Gnoll spawned at cell " + cell + " (total mobs: " + level.mobs.size() + ")");
        } else {
            // Ok... so we failed to properly spawn the Elder Gnoll...
            // this should be sort of expected since we were never meant to spawn enemies this way to begin with
            // to still make the run winnable, we will just randomly spawn the item that the elder drops onto the floor
            // sad that the player won't get to deal with the elder, but so be it

            // Add worn key to the level
            level.addItemToSpawn(new WornKey(Dungeon.depth));

            /*
            GLog.w("WARNING: No valid spawn locations found for Elder Gnoll!");
            throw new IllegalStateException("No valid spawn locations found for Elder Gnoll, making it impossible to complete the game!");
             */
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("entrance_cell", entranceCell);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        entranceCell = bundle.getInt("entrance_cell");
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