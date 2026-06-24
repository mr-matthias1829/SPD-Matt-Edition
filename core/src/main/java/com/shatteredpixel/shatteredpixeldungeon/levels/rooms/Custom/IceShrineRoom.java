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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.IcyCore;
import com.shatteredpixel.shatteredpixeldungeon.levels.IceCavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class IceShrineRoom extends CaveRoom {

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0, 3, 2};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public int minConnections(int direction) {
        return 0;
    }

    @Override
    public int maxConnections(int direction) {
        return 0;
    }
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 26);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 26);
    }

    private Item randomLootItem() {
        switch (Random.chances(new float[]{5f, 3f, 2f})) {
            case 0:
                return Generator.random(Generator.Category.WAND);
            case 1:
                return Generator.random(Generator.Category.RING);
            default:
                return Generator.random(Generator.Category.ARTIFACT);
        }
    }

    @Override
    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);

        int cx = center().x;
        int cy = center().y;

        int radius = Math.min(width(), height()) / 2;

        // circular carve
        for (int x = left + 1; x < right; x++) {
            for (int y = top + 1; y < bottom; y++) {

                int dx = x - cx;
                int dy = y - cy;

                float dist = (dx*dx)/(float)(radius*radius)
                        + (dy*dy)/(float)(radius*radius);

                if (dist <= 1.0f) {
                    Painter.set(level, x, y, Terrain.EMPTY);
                }
            }
        }

        // central metallic shrine platform
        Painter.fill(level, cx-2, cy-2, 5, 5, Terrain.EMPTY_SP);

        // icy water pools
        for (int x = left + 2; x < right - 1; x++) {
            for (int y = top + 2; y < bottom - 1; y++) {

                int dx = x - cx;
                int dy = y - cy;

                float dist = (dx*dx)/(float)((radius-2)*(radius-2))
                        + (dy*dy)/(float)((radius-2)*(radius-2));

                if (dist > 0.38f && dist < 0.88f) {

                    int cell = level.pointToCell(new Point(x, y));

                    if (level.map[cell] == Terrain.EMPTY) {

                        // base chance
                        if (Random.Float() < 0.38f) {

                            Painter.set(level, x, y, Terrain.WATER);

                            // spread to neighbors
                            for (int ox = -1; ox <= 1; ox++) {
                                for (int oy = -1; oy <= 1; oy++) {

                                    if (Random.Float() < 0.35f) {

                                        int nx = x + ox;
                                        int ny = y + oy;

                                        int ncell = level.pointToCell(new Point(nx, ny));

                                        if (level.map[ncell] == Terrain.EMPTY) {
                                            Painter.set(level, nx, ny, Terrain.WATER);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // tiny random debris
        for (int i = 0; i < 100; i++) {

            int x = Random.IntRange(left+3, right-3);
            int y = Random.IntRange(top+3, bottom-3);


            if (level.map[level.pointToCell(new Point(x, y))] == Terrain.EMPTY
                    && distance(x, y, cx, cy) > 3) {

                Painter.set(level, x, y, Terrain.WALL);
            }
        }

        // pedestals
        Point p1 = new Point(cx-2, cy);
        Point p2 = new Point(cx+2, cy);
        // new empty pedestal above center
        Point p3 = new Point(cx, cy-2);

        Painter.set(level, p1, Terrain.PEDESTAL);
        Painter.set(level, p2, Terrain.PEDESTAL);
        Painter.set(level, p3, Terrain.PEDESTAL);

        level.drop(new IcyCore(), level.pointToCell(p3));
        level.drop(randomLootItem(), level.pointToCell(p2));
        /*
        level.drop(Generator.random(Random.oneOf(
                Generator.Category.SCROLL,
                Generator.Category.POTION,
                Generator.Category.GOLD)),
                level.pointToCell(p1));
         */
        level.drop(Generator.random(Generator.Category.WAND), level.pointToCell(p1));

        // return exit opposite of top pedestal
        Point exitPoint = new Point(cx, cy+2);

        // random entrance position near outer edge
        Point ent;

        switch (Random.Int(4)) {

            default:
            case 0: // top
                ent = new Point(
                        Random.IntRange(cx-3, cx+3),
                        top + 2
                );
                break;

            case 1: // bottom
                ent = new Point(
                        Random.IntRange(cx-3, cx+3),
                        bottom - 2
                );
                break;

            case 2: // left
                ent = new Point(
                        left + 2,
                        Random.IntRange(cy-3, cy+3)
                );
                break;

            case 3: // right
                ent = new Point(
                        right - 2,
                        Random.IntRange(cy-3, cy+3)
                );
                break;
        }

        // ensure entrance area is open
        for (int ox = -1; ox <= 1; ox++) {
            for (int oy = -1; oy <= 1; oy++) {

                int nx = ent.x + ox;
                int ny = ent.y + oy;

                if (nx > left && nx < right
                        && ny > top && ny < bottom) {

                    Painter.set(level, nx, ny, Terrain.EMPTY);
                }
            }
        }

        Painter.set(level, ent, Terrain.ENTRANCE);
        Painter.set(level, exitPoint, Terrain.EXIT);

        level.transitions.add(new LevelTransition(
                level,
                level.pointToCell(ent),
                LevelTransition.Type.REGULAR_ENTRANCE
        ));

        // custom transition back to ice caves main floor
        LevelTransition exitTrans = new LevelTransition(
                level,
                level.pointToCell(exitPoint),
                LevelTransition.Type.REGULAR_EXIT,
                IceCavesLevel.mainFloor,
                2,
                LevelTransition.Type.BRANCH_EXIT, // we place the hero back at origin IC floor branch exit
                "ice_shrine_exit", // custom name for the transition
                "ice_caves_deep_exit" // custom name for the destination tile
        );

        exitTrans.destDepth = IceCavesLevel.mainFloor;
        level.transitions.add(exitTrans);
    }

    private void placeChunk(Level level, int x, int y, int w, int h) {

        for (int ix = 0; ix < w; ix++) {
            for (int iy = 0; iy < h; iy++) {

                if (Random.Float() < 0.72f) {
                    Painter.set(level, x + ix, y + iy, Terrain.WALL);
                }
            }
        }
    }

    private int distance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return (int)Math.sqrt(dx*dx + dy*dy);
    }
}