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
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GuildVaultRoom extends StandardRoom {
    // this is a pretty large room
    // in the center is the "inner" room, which acts as the vault
    // in the vault is gold and a special item
    // need crystal and iron key to get to gold + gold key to get the special item

    // we don't give too much variance in room size:
    // 1. it is already quite big
    // 2. any bigger and it might take up more than half of the whole floor
    // mainly has to do with the inner room alone being a 7x7 and then the outer room needing space to walk around it
    @Override
    public int minWidth()  { return 13; }
    @Override
    public int minHeight() { return 13; }
    @Override
    public int maxWidth()  { return 15; }
    @Override
    public int maxHeight() { return 15; }

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        // set all connected doors to REGULAR so they render correctly
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }

        // centre coordinates of the outer room
        int cx = (left + right)  / 2;
        int cy = (top  + bottom) / 2;

        int wl = cx - 3;  // wall block left
        int wr = cx + 3;  // wall block right
        int wt = cy - 3;  // wall block top
        int wb = cy + 3;  // wall block bottom

        int il = cx - 1;  // inner floor left
        int ir = cx + 1;  // inner floor right
        int it = cy - 1;  // inner floor top
        int ib = cy + 1;  // inner floor bottom

        // fill 7x7 as wall, then punch out the 3x3 interior
        Painter.fill(level, wl, wt, wr - wl + 1, wb - wt + 1, Terrain.WALL);
        Painter.fill(level, il, it, 3, 3, Terrain.EMPTY);

        // two tiles of wall thickness on the north side are at y = wt and y = wt+1.
        //   y = wt     = outer wall tile  = CRYSTAL_DOOR  (needs CrystalKey)
        //   y = wt + 1 = inner wall tile  = LOCKED_DOOR   (needs IronKey)
        Painter.set(level, cx, wt,     Terrain.CRYSTAL_DOOR);
        Painter.set(level, cx, wt + 1, Terrain.LOCKED_DOOR);

        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        level.addItemToSpawn(new IronKey(Dungeon.depth));

        // locked chest on the south inner wall (opposite the doorway)
        // TODO: replace Gold(1) placeholder with actual special item
        Item chestContents = new Gold(1);
        level.drop(chestContents, level.pointToCell(new Point(cx, ib)))
                .type = com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type.LOCKED_CHEST;

        level.addItemToSpawn(new GoldenKey(Dungeon.depth));

        // 3x3 = 9 tiles, minus the chest tile = 8 gold tiles
        // total gold is ALWAYS exactly 650, distributed randomly per generation
        ArrayList<Point> goldTiles = new ArrayList<>();
        for (int x = il; x <= ir; x++) {
            for (int y = it; y <= ib; y++) {
                if (x == cx && y == ib) continue;
                goldTiles.add(new Point(x, y));
            }
        }

        int total = 650;
        int n = goldTiles.size(); // 8
        int[] weights = new int[n];
        int weightSum = 0;
        for (int i = 0; i < n; i++) {
            weights[i] = Random.IntRange(1, 10);
            weightSum += weights[i];
        }

        int distributed = 0;
        for (int i = 0; i < n - 1; i++) {
            int remaining = total - distributed;
            int slotsLeft = n - i;
            int share = Math.round((float) weights[i] / weightSum * total);
            share = Math.max(1, Math.min(share, remaining - (slotsLeft - 1)));
            distributed += share;
            level.drop(new Gold(share), level.pointToCell(goldTiles.get(i)));
        }
        int remainder = total - distributed;
        if (remainder > 0) {
            level.drop(new Gold(remainder), level.pointToCell(goldTiles.get(n - 1)));
        }
    }


    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        // suprise suprise, we do not want enemies in the inner room
        // for one, it wouldn't make sense. and for seconds, it would make a very unfunny jumpscare
        int cx = (left + right) / 2;
        int cy = (top  + bottom) / 2;
        // exclude the inner 3x3 vault floor
        if (p.x >= cx - 1 && p.x <= cx + 1 &&
                p.y >= cy - 1 && p.y <= cy + 1) {
            return false;
        }
        return super.canPlaceCharacter(p, l);
    }
}