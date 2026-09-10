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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastShopLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;
import com.watabou.utils.SparseArray;

public class TerrainFeaturesTilemap extends DungeonTilemap {

    private static TerrainFeaturesTilemap instance;

    private SparseArray<Plant> plants;
    private SparseArray<Trap> traps;

    // Lazy-loaded so a missing custom PNG doesn't crash levels that never use it.
    private static TextureFilm customFeatures;

    private static TextureFilm customFeatures() {
        if (customFeatures == null) {
            customFeatures = new TextureFilm(
                    Assets.Environment.CUSTOM_FEATURES, SIZE, SIZE);
        }
        return customFeatures;
    }

    /*
     * The row to draw from, for whichever sheet is active.
     *
     * If the level overrides featuresRow(), that override wins and we sample
     * the custom sheet at that row.
     * Otherwise we fall back to the depth-based vanilla row.
     */
    private static int activeRow() {
        if (Dungeon.level != null) {
            int r = Dungeon.level.featuresRow();
            if (r >= 0) return r+7;
        }
        return featuresRow(-1);
    }

    public TerrainFeaturesTilemap(SparseArray<Plant> plants, SparseArray<Trap> traps) {
        super(Assets.Environment.TERRAIN_FEATURES);

        this.plants = plants;
        this.traps = traps;

        if (Dungeon.level != null) {
            map(Dungeon.level.map, Dungeon.level.width());
        }

        instance = this;
    }

    /*
     * row == -1:
     *     Automatically use the normal region/stage based on dungeon depth.
     *
     * row >= 0:
     *     Use the custom features asset and select the given row.
     */
    private static int featuresRow(int row) {
        if (row >= 0) {
            return row;
        }

        int stage = (Dungeon.depth - 1) / 5;

        if (Dungeon.depth == 21 && Dungeon.level instanceof LastShopLevel) {
            stage--;
        }

        return Math.min(stage, 4);
    }

    /*
     * The render path used by DungeonTilemap.updateMap()/updateMapCell().
     * This is what actually draws the floor decorations, so the sheet
     * decision has to happen here.
     */
    protected int getTileVisual(int pos, int tile, boolean flat) {

        // Plants and traps ALWAYS use the normal vanilla feature sheet.
        if (traps.get(pos) != null) {
            Trap trap = traps.get(pos);

            if (!trap.visible) {
                return -1;
            } else {
                return (trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16);
            }
        }

        if (plants.get(pos) != null) {
            return plants.get(pos).image + 7 * 16;
        }

        int row = activeRow();

        if (tile == Terrain.HIGH_GRASS) {
            if (DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_HIGH_GRASS, pos)
                    == DungeonTileSheet.RAISED_HIGH_GRASS_ALT) {

                return 128 + 16 * row + 1;

            } else {
                return 128 + 16 * row;
            }

        } else if (tile == Terrain.FURROWED_GRASS) {
            if (DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_FURROWED_GRASS, pos)
                    == DungeonTileSheet.RAISED_FURROWED_ALT) {

                return 130 + 16 * row + 1;

            } else {
                return 130 + 16 * row;
            }

        } else if (tile == Terrain.GRASS) {
            if (DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.GRASS, pos)
                    == DungeonTileSheet.GRASS_ALT) {

                return 132 + 16 * row + 1;

            } else {
                return 132 + 16 * row;
            }

        } else if (tile == Terrain.BARRICADE) {
            return 134 + 16 * row;

        } else if (tile == Terrain.ALCHEMY) {
            return 135 + 16 * row;

        } else if (tile == Terrain.STATUE || tile == Terrain.STATUE_SP) {
            return 136 + 16 * row;

        } else if (tile == Terrain.REGION_DECO) {
            return 137 + 16 * row;

        } else if (tile == Terrain.REGION_DECO_ALT) {
            return 138 + 16 * row;

            /*
             * Everything below this point ALWAYS uses the normal
             * TERRAIN_FEATURES asset.
             */
        } else if (tile == Terrain.EMBERS) {
            if (DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.EMBERS, pos)
                    == DungeonTileSheet.EMBERS_ALT) {

                return 208 + 1;

            } else {
                return 208;
            }

        } else if (tile == Terrain.MINE_CRYSTAL) {
            int vis = DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_MINE_CRYSTAL_BLUE_1, pos);

            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_RED_2) {
                return 210 + 5;
            } else if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_RED_1) {
                return 210 + 4;
            } else if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_GREEN_2) {
                return 210 + 3;
            } else if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_GREEN_1) {
                return 210 + 2;
            } else if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_BLUE_2) {
                return 210 + 1;
            } else {
                return 210;
            }

        } else if (tile == Terrain.MINE_BOULDER) {
            int vis = DungeonTileSheet.getVisualWithAlts(
                    DungeonTileSheet.RAISED_MINE_BOULDER, pos);

            if (vis == DungeonTileSheet.RAISED_MINE_BOULDER_ALT_2) {
                return 216 + 2;
            } else if (vis == DungeonTileSheet.RAISED_MINE_BOULDER_ALT) {
                return 216 + 1;
            } else {
                return 216;
            }
        }

        return -1;
    }
    public static Image getTrapVisual(Trap trap) {
        if (instance == null) {
            instance = new TerrainFeaturesTilemap(null, null);
        }

        RectF uv = instance.tileset.get(
                (trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16)
        );

        if (uv == null) {
            return null;
        }

        Image img = new Image(Assets.Environment.TERRAIN_FEATURES);
        img.frame(uv);
        return img;
    }

    public static Image getPlantVisual(Plant plant) {
        if (instance == null) {
            instance = new TerrainFeaturesTilemap(null, null);
        }

        RectF uv = instance.tileset.get(plant.image + 7 * 16);

        if (uv == null) {
            return null;
        }

        Image img = new Image(Assets.Environment.TERRAIN_FEATURES);
        img.frame(uv);
        return img;
    }

    /*
     * One-off image for a single cell. Used by growPlant() and callers that
     * want a standalone sprite rather than a tilemap entry.
     */
    public static Image tile(int pos, int tile) {
        if (instance == null) {
            instance = new TerrainFeaturesTilemap(null, null);
        }

        int visual = instance.getTileVisual(pos, tile, true);

        if (visual < 0) {
            return null;
        }

        RectF uv = instance.tileset.get(visual);
        if (uv == null) {
            return null;
        }

        Image img = new Image(Assets.Environment.TERRAIN_FEATURES);
        img.frame(uv);
        return img;
    }

    public void growPlant(final int pos) {
        final Image plant = tile(pos, map[pos]);

        if (plant == null) {
            return;
        }

        plant.origin.set(8, 12);
        plant.scale.set(0);
        plant.point(DungeonTilemap.tileToWorld(pos));

        parent.add(plant);

        parent.add(new ScaleTweener(plant, new PointF(1, 1), 0.2f) {
            protected void onComplete() {
                plant.killAndErase();
                killAndErase();
                updateMapCell(pos);
            }
        });
    }
}