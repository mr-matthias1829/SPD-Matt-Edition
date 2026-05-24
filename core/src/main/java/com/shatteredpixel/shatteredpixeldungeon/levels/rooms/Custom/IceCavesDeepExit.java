package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ElderGnoll;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class IceCavesDeepExit extends SpecialRoom {


    // actually it is a exit, we just dont want it to generate like one
    // if we did, it would generate as far as possible from entrance (mostly)
    // and if you have multiple exits, you get funny conflict and one just generates NEXT to the entrance
    // this room is a mix of a special room with a exit, so false here is correct and fine
    // when using this room, make sure it's not the only exit since it's still a puzzle room you can softlock yourself out of

    // also: note that this room will NOT guarantee the floor with a liquid flame potion
    @Override
    public boolean isExit() { return false; }

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);  // normal ground everywhere

        Point c = center();

        // ring of metal floor around the exit
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMPTY_SP);

        int exitCell = level.pointToCell(c);
        Painter.set(level, exitCell, Terrain.EXIT);

        LevelTransition exit = new LevelTransition(level, exitCell, LevelTransition.Type.REGULAR_EXIT);
        level.transitions.add(exit);

        entrance().set(Door.Type.BARRICADE);
        //level.addItemToSpawn(new PotionOfLiquidFlame());
    }
}