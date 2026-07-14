/*
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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.FriendlyThief;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FriendlyThiefRoom extends SpecialRoom {

    @Override public int minWidth()  { return Math.max(super.minWidth(),  5); }
    @Override public int minHeight() { return Math.max(super.minHeight(), 5); }
    @Override public int maxWidth()  { return 6; }
    @Override public int maxHeight() { return 6; }

    @Override
    public int maxConnections(int direction) {
        return 1;
    }

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        for (Room.Door door : connected.values()) {
            door.set(Room.Door.Type.LOCKED);
        }

        int entranceCell = level.pointToCell(center());

        boolean accessed = FriendlyThief.Quest.accessed();
        boolean open     = FriendlyThief.Quest.entranceOpen();

        if (accessed) {
            // permanently completed state
            Painter.set(level, entranceCell, Terrain.PEDESTAL);
        } else if (open) {
            // active guild exit
            Painter.set(level, entranceCell, Terrain.EXIT);

            // ensure transition exists once
            boolean exists = false;
            for (LevelTransition t : level.transitions) {
                if (t.cell() == entranceCell) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                level.transitions.add(new LevelTransition(
                        level,
                        entranceCell,
                        LevelTransition.Type.BRANCH_EXIT,
                        Dungeon.depth,
                        2,
                        LevelTransition.Type.BRANCH_ENTRANCE,
                        "guild_branch_entrance",
                        "guild_entrance"
                ));
            }

        } else {
            // default state (or after returning from guild)
            Painter.set(level, entranceCell, Terrain.PEDESTAL);
        }


        FriendlyThief.Quest.setEntranceCell(entranceCell);
        FriendlyThief.Quest.markSpawned();

        if (!FriendlyThief.Quest.accessed()) {
            spawnNPC(level, entranceCell);
        }

        level.addItemToSpawn( new IronKey( Dungeon.depth ) );
    }

    private void spawnNPC(Level level, int entranceCell) {

        int npcCell = -1;

        for (int attempt = 0; attempt < 20; attempt++) {

            int rx = left + 1 + Random.Int(width() - 2);
            int ry = top  + 1 + Random.Int(height() - 2);

            int candidate = level.pointToCell(new Point(rx, ry));

            if (candidate == entranceCell) continue;

            int dist = Math.abs(rx - (left + width() / 2))
                    + Math.abs(ry - (top + height() / 2));

            if (dist >= 1 && dist <= 3
                    && level.map[candidate] == Terrain.EMPTY
                    && level.findMob(candidate) == null) {
                npcCell = candidate;
                break;
            }
        }

        if (npcCell == -1) {
            outer:
            for (int y = top + 1; y < bottom; y++) {
                for (int x = left + 1; x < right; x++) {

                    int cell = level.pointToCell(new Point(x, y));

                    if (cell != entranceCell
                            && level.map[cell] == Terrain.EMPTY
                            && level.findMob(cell) == null) {
                        npcCell = cell;
                        break outer;
                    }
                }
            }
        }

        if (npcCell == -1) return;

        FriendlyThief npc = new FriendlyThief();
        npc.pos = npcCell;
        level.mobs.add(npc);
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return l.pointToCell(p) != FriendlyThief.Quest.getEntranceCell()
                && super.canPlaceCharacter(p, l);
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return l.pointToCell(p) != FriendlyThief.Quest.getEntranceCell()
                && super.canPlaceItem(p, l);
    }
}