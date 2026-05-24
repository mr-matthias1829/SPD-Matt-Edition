/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.AlchemyPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LaboratoryRoom extends SpecialRoom {

    // page name → minimum chapter required (1=sewers, 2=prison, 3=caves, 4=city, 5=halls)
    private static final String[] PAGE_ORDER = {
            "Potions",
            "Stones",
            "Energy_Food",
            "Exotic_Potions",
            "Exotic_Scrolls",
            "Bombs",
            "Weapons",
            "Brews_Elixirs",
            "Spells",
            "Weapon_Crafting"
    };
    private static final int[] PAGE_CHAPTER = {
            1,  // Potions        — sewers+
            1,  // Stones         — sewers+
            1,  // Energy_Food    — sewers+
            2,  // Exotic_Potions — prison+
            2,  // Exotic_Scrolls — prison+
            2,  // Bombs          — prison+
            3,  // Weapons        — caves+
            4,  // Brews_Elixirs  — city+
            4,  // Spells         — city+
            3   // Weapon_Crafting  — caves+
    };

    public void paint( Level level ) {

        Painter.fill( level, this, Terrain.WALL );
        Painter.fill( level, this, 1, Terrain.EMPTY_SP );

        Door entrance = entrance();

        Point pot = null;
        if (entrance.x == left) {
            pot = new Point( right-1, Random.Int( 2 ) == 0 ? top + 1 : bottom - 1 );
        } else if (entrance.x == right) {
            pot = new Point( left+1, Random.Int( 2 ) == 0 ? top + 1 : bottom - 1 );
        } else if (entrance.y == top) {
            pot = new Point( Random.Int( 2 ) == 0 ? left + 1 : right - 1, bottom-1 );
        } else if (entrance.y == bottom) {
            pot = new Point( Random.Int( 2 ) == 0 ? left + 1 : right - 1, top+1 );
        }
        Painter.set( level, pot, Terrain.ALCHEMY );

        int chapter = 1 + Dungeon.depth/5;
        Blob.seed( pot.x + level.width() * pot.y, 1, Alchemy.class, level );

        int pos;
        do {
            pos = level.pointToCell(random());
        } while (
                level.map[pos] != Terrain.EMPTY_SP ||
                        level.heaps.get( pos ) != null);
        level.drop( new EnergyCrystal().quantity(5), pos );

        int n = Random.NormalIntRange( 1, 2 );
        for (int i=0; i < n; i++) {
            do {
                pos = level.pointToCell(random());
            } while (
                    level.map[pos] != Terrain.EMPTY_SP ||
                            level.heaps.get( pos ) != null);
            level.drop( prize( level ), pos );
        }

        // guide pages — drop the earliest eligible unfound page
        ArrayList<String> eligiblePages = new ArrayList<>();
        for (int i = 0; i < PAGE_ORDER.length; i++) {
            String page = PAGE_ORDER[i];
            if (!Document.ALCHEMY_GUIDE.isPageFound(page) && chapter >= PAGE_CHAPTER[i]) {
                eligiblePages.add(page);
            }
        }

        int pagesToDrop = Math.min(eligiblePages.size(), 3);
        for (int i = 0; i < pagesToDrop; i++) {
            AlchemyPage p = new AlchemyPage();
            p.page(eligiblePages.get(i));
            do {
                pos = level.pointToCell(random());
            } while (
                    level.map[pos] != Terrain.EMPTY_SP ||
                            level.heaps.get(pos) != null);
            level.drop(p, pos);
        }

        entrance.set( Door.Type.LOCKED );
        level.addItemToSpawn( new IronKey( Dungeon.depth ) );

    }

    private static Item prize( Level level ) {

        Item prize = level.findPrizeItem( TrinketCatalyst.class );
        if (prize == null){
            prize = level.findPrizeItem( PotionOfStrength.class );
            if (prize == null) {
                prize = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.STONE));
            }
        }

        return prize;
    }
}