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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.RegionLorePage;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.CrackedSpyglass;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class RegularLevel extends Level {

    protected ArrayList<Room> rooms;

    protected Builder builder;

    protected Room roomEntrance;
    protected Room roomExit;

    @Override
    protected boolean build() {

        builder = builder();

        ArrayList<Room> initRooms = initRooms();
        Random.shuffle(initRooms);

        do {
            for (Room r : initRooms){
                r.neigbours.clear();
                r.connected.clear();
            }
            rooms = builder.build((ArrayList<Room>)initRooms.clone());
        } while (rooms == null);

        return painter().paint(this, rooms);

    }

    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();
        initRooms.add ( roomEntrance = EntranceRoom.createEntrance());

        // TODO: dude just, just like move this to cave level class. it takes less than 5 mins to do so
        if (Dungeon.depth == 14 && Dungeon.branch == 0) { // i love hardcoding in all the wrong places
            initRooms.add(roomExit = new IceCavesEntrance());
        } else {
            initRooms.add(roomExit = ExitRoom.createExit());
        }


        //force max standard rooms and multiple by 1.5x for large levels
        int standards = standardRooms(feeling == Feeling.LARGE);
        if (feeling == Feeling.LARGE){
            standards = (int)Math.ceil(standards * 1.5f);
        }
        for (int i = 0; i < standards; i++) {
            StandardRoom s;
            do {
                s = StandardRoom.createRoom();
            } while (!s.setSizeCat( standards-i ));
            i += s.sizeFactor()-1;
            initRooms.add(s);
        }

        if (Dungeon.shopOnLevel())
            initRooms.add(new ShopRoom());

        //force max special rooms and add one more for large levels
        int specials = specialRooms(feeling == Feeling.LARGE);
        if (feeling == Feeling.LARGE){
            specials++;
        }
        SpecialRoom.initForFloor();
        for (int i = 0; i < specials; i++) {
            SpecialRoom s = SpecialRoom.createRoom();
            if (s instanceof PitRoom) specials++;
            initRooms.add(s);
        }

        int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
        //one additional secret for secret levels
        if (feeling == Feeling.SECRETS) secrets++;
        for (int i = 0; i < secrets; i++) {
            initRooms.add(SecretRoom.createRoom());
        }

        return initRooms;
    }

    protected int standardRooms(boolean forceMax){
        return 0;
    }

    protected int specialRooms(boolean forceMax){
        return 0;
    }

    protected Builder builder(){
        if (Random.Int(2) == 0){
            return new LoopBuilder()
                    .setLoopShape( 2 ,
                            Random.Float(0f, 0.65f),
                            Random.Float(0f, 0.50f));
        } else {
            return new FigureEightBuilder()
                    .setLoopShape( 2 ,
                            Random.Float(0.3f, 0.8f),
                            0f);
        }

    }

    protected abstract Painter painter();

    protected int nTraps() {
        return Random.NormalIntRange( 2, 3 + (Dungeon.depth/5) );
    }

    protected Class<?>[] trapClasses(){
        return new Class<?>[]{WornDartTrap.class};
    }

    protected float[] trapChances() {
        return new float[]{1};
    }

    @Override
    public int mobLimit() {
        if (Dungeon.depth <= 1){
            if (!Statistics.amuletObtained) return 0;
            else                            return 10;
        }

        int mobs = 5 + Dungeon.depth % 5 + Random.Int(3);
        if (feeling == Feeling.LARGE){
            mobs = (int)Math.ceil(mobs * 1.5f);
        }
        return mobs;
    }

    @Override
    protected void createMobs() {
        //on floor 1, 8 pre-set mobs are created so the player can get level 2.
        int mobsToSpawn = Dungeon.depth == 1 ? 8 : mobLimit();

        ArrayList<Room> stdRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room instanceof StandardRoom) {
                for (int i = 0; i < ((StandardRoom) room).mobSpawnWeight(); i++) {
                    stdRooms.add(room);
                }
            }
        }
        Random.shuffle(stdRooms);
        Iterator<Room> stdRoomIter = stdRooms.iterator();

        //enemies cannot be within a 8-tile FOV or 8-tile open space walk from the entrance
        boolean[] entranceFOV = new boolean[length()];
        Point c = cellToPoint(entrance());
        ShadowCaster.castShadow(c.x, c.y, width(), entranceFOV, losBlocking, 8);

        boolean[] entranceWalkable = BArray.not(solid, null);

        //doors within the entrance room are ignored for this walk, but doors on the edge are not
        for (int y = roomEntrance.top+1; y < roomEntrance.bottom; y++){
            for (int x = roomEntrance.left+1; x < roomEntrance.right; x++){
                int cell = x + y*width();
                if (passable[cell]){
                    entranceWalkable[cell] = true;
                }
            }
        }

        PathFinder.buildDistanceMap(entrance(), entranceWalkable, 8);

        Mob mob = null;
        while (mobsToSpawn > 0) {
            if (mob == null) mob = createMob();
            Room roomToSpawn;

            if (!stdRoomIter.hasNext()) {
                stdRoomIter = stdRooms.iterator();
            }
            roomToSpawn = stdRoomIter.next();

            int tries = 30;
            do {
                mob.pos = pointToCell(roomToSpawn.random());
                tries--;
            } while (tries >= 0 && (findMob(mob.pos) != null
                    || entranceFOV[mob.pos] || PathFinder.distance[mob.pos] != Integer.MAX_VALUE
                    || !passable[mob.pos]
                    || solid[mob.pos]
                    || !roomToSpawn.canPlaceCharacter(cellToPoint(mob.pos), this)
                    || mob.pos == exit()
                    || traps.get(mob.pos) != null || plants.get(mob.pos) != null
                    || (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

            if (tries >= 0) {
                mobsToSpawn--;
                mobs.add(mob);
                mob = null;

                //chance to add a second mob to this room, except on floor 1
                if (Dungeon.depth > 1 && mobsToSpawn > 0 && Random.Int(4) == 0){
                    mob = createMob();

                    tries = 30;
                    do {
                        mob.pos = pointToCell(roomToSpawn.random());
                        tries--;
                    } while (tries >= 0 && (findMob(mob.pos) != null
                            || entranceFOV[mob.pos] || PathFinder.distance[mob.pos] != Integer.MAX_VALUE
                            || !passable[mob.pos]
                            || solid[mob.pos]
                            || !roomToSpawn.canPlaceCharacter(cellToPoint(mob.pos), this)
                            || mob.pos == exit()
                            || traps.get(mob.pos) != null || plants.get(mob.pos) != null
                            || (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE))));

                    if (tries >= 0) {
                        mobsToSpawn--;
                        mobs.add(mob);
                        mob = null;
                    }
                }
            }
        }

        for (Mob m : mobs){
            if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
                map[m.pos] = Terrain.GRASS;
                losBlocking[m.pos] = false;
            }

        }

    }

    @Override
    public int randomRespawnCell( Char ch ) {
        int count = 0;
        int cell = -1;

        while (true) {

            if (++count > 30) {
                return -1;
            }

            Room room = randomRoom( StandardRoom.class );
            if (room == null || room == roomEntrance) {
                continue;
            }

            cell = pointToCell(room.random(1));
            if (!heroFOV[cell]
                    && Actor.findChar( cell ) == null
                    && passable[cell]
                    && !solid[cell]
                    && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])
                    && room.canPlaceCharacter(cellToPoint(cell), this)
                    && cell != exit()) {
                return cell;
            }

        }
    }

    @Override
    public int randomDestination( Char ch ) {

        int count = 0;
        int cell = -1;

        while (true) {

            if (++count > 30) {
                return -1;
            }

            Room room = Random.element( rooms );
            if (room == null) {
                continue;
            }

            ArrayList<Point> points = room.charPlaceablePoints(this);
            if (!points.isEmpty()){
                cell = pointToCell(Random.element(points));
                if (passable[cell] && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])) {
                    return cell;
                }
            }

        }
    }

    @Override
    protected void createItems() {
        if (!canSpawnItems) return;

        // ── Determine how many random items to generate ───────────────────────────
        // setItemsToSpawn > 0: use that count exactly (ignores additionalItemsToSpawn).
        // Otherwise: use the normal weighted-random formula plus additionalItemsToSpawn.
        int nItems;
        if (setItemsToSpawn >= 0) {
            nItems = setItemsToSpawn;
        } else {
            // drops 3/4/5 items 60%/30%/10% of the time
            nItems = 3 + Random.chances(new float[]{6, 3, 1});
            if (feeling == Feeling.LARGE) {
                nItems += 2;
            }
        }

        // ── Build the item list for this run ─────────────────────────────────────
        // When setItemsToSpawn is active we respect the per-category overrides;
        // any remaining slots after categories are filled use fully-random items.
        ArrayList<Item> randomItems = new ArrayList<>();

        if (setItemsToSpawn >= 0) {
            int remaining = nItems;

            // Scroll / potion slots (highest priority)
            if (setScrollPotionCount > 0 && remaining > 0) {
                int spCap = Math.min(setScrollPotionCount, remaining);
                for (int i = 0; i < spCap; i++) {
                    Item it = (i % 2 == 0)
                            ? Generator.random(Generator.Category.SCROLL)
                            : Generator.random(Generator.Category.POTION);
                    if (it != null) randomItems.add(it);
                }
                remaining -= spCap;
            }

            // Weapon / armor slots
            if (setWeaponArmorCount > 0 && remaining > 0) {
                int waCap = Math.min(setWeaponArmorCount, remaining);
                for (int i = 0; i < waCap; i++) {
                    Item it = (i % 2 == 0)
                            ? Generator.random(Generator.Category.WEAPON)
                            : Generator.random(Generator.Category.ARMOR);
                    if (it != null) randomItems.add(it);
                }
                remaining -= waCap;
            }

            // Missile / thrown weapon slots
            if (setMissileCount > 0 && remaining > 0) {
                int mCap = Math.min(setMissileCount, remaining);
                for (int i = 0; i < mCap; i++) {
                    Item it = Generator.random(Generator.Category.MISSILE);
                    if (it != null) randomItems.add(it);
                }
                remaining -= mCap;
            }

            // Wand slots
            if (setWandCount > 0 && remaining > 0) {
                int wCap = Math.min(setWandCount, remaining);
                for (int i = 0; i < wCap; i++) {
                    Item it = Generator.random(Generator.Category.WAND);
                    if (it != null) randomItems.add(it);
                }
                remaining -= wCap;
            }

            // Ring slots
            if (setRingCount > 0 && remaining > 0) {
                int rCap = Math.min(setRingCount, remaining);
                for (int i = 0; i < rCap; i++) {
                    Item it = Generator.random(Generator.Category.RING);
                    if (it != null) randomItems.add(it);
                }
                remaining -= rCap;
            }

            // Artifact slots
            if (setArtifactCount > 0 && remaining > 0) {
                int aCap = Math.min(setArtifactCount, remaining);
                for (int i = 0; i < aCap; i++) {
                    Item it = Generator.random(Generator.Category.ARTIFACT);
                    if (it != null) randomItems.add(it);
                }
                remaining -= aCap;
            }

            // Fill any remaining slots with fully-random items
            for (int i = 0; i < remaining; i++) {
                Item it = Generator.random();
                if (it != null) randomItems.add(it);
            }


        } else {
            // Normal path: just generate nItems random items
            for (int i = 0; i < nItems; i++) {
                randomItems.add(Generator.random());
            }
        }

        // ── Place each item on the level ──────────────────────────────────────────
        for (Item toDrop : randomItems) {
            if (toDrop == null) continue;

            if (Item.shouldDesireDestroy(toDrop)) {
                continue;
            }

            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }

            Heap.Type type = null;
            switch (Random.Int(20)) {
                case 0:
                    type = Heap.Type.SKELETON;
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    // base mimic chance is 1/20, regular chest is 4/20
                    if (Random.Float() < (MimicTooth.mimicChanceMultiplier() - 1f) / 4f && findMob(cell) == null) {
                        mobs.add(Mimic.spawnAt(cell, toDrop));
                        continue;
                    }
                    type = Heap.Type.CHEST;
                    break;
                case 5:
                    if (Dungeon.depth > 1 && findMob(cell) == null) {
                        mobs.add(Mimic.spawnAt(cell, toDrop));
                        continue;
                    }
                    type = Heap.Type.CHEST;
                    break;
                default:
                    type = Heap.Type.HEAP;
                    break;
            }

            if ((toDrop instanceof Artifact && Random.Int(2) == 0) ||
                    (toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0)) {

                float mimicChance = 1 / 10f * MimicTooth.mimicChanceMultiplier();
                if (Dungeon.depth > 1 && Random.Float() < mimicChance && findMob(cell) == null) {
                    mobs.add(Mimic.spawnAt(cell, GoldenMimic.class, toDrop));
                } else {
                    Heap dropped = drop(toDrop, cell);
                    if (heaps.get(cell) == dropped) {
                        dropped.type = Heap.Type.LOCKED_CHEST;
                        addItemToSpawn(new GoldenKey(Dungeon.depth));
                    }
                }
            } else {
                Heap dropped = drop(toDrop, cell);
                dropped.type = type;
                if (type == Heap.Type.SKELETON) {
                    dropped.setHauntedIfCursed();
                }
            }
        }


        // GUARANTEED TIER 1 ARMOR ON FLOOR 3
        // TODO: this works, but isn't very clean to be in here. perhaps use Level.addItemToSpawn() method?
        Random.pushGenerator(Random.Long());
        if (Dungeon.depth == 3) {
            Item tier1Armor = null;
            try {
                tier1Armor = Generator.randomArmor(1);
            } catch (Exception e) {
                tier1Armor = Generator.random();
            }

            if (tier1Armor != null) {
                int cell = randomDropCell();
                if (cell != -1) {
                    if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                        map[cell] = Terrain.GRASS;
                        losBlocking[cell] = false;
                    }
                    drop(tier1Armor, cell).type = Heap.Type.HEAP;
                }
            }
        }
        Random.popGenerator();


        for (Item item : itemsToSpawn) {
            int cell = randomDropCell();
            if (item instanceof TrinketCatalyst) {
                drop(item, cell).type = Heap.Type.LOCKED_CHEST;
                int keyCell = randomDropCell();
                drop(new GoldenKey(Dungeon.depth), keyCell).type = Heap.Type.HEAP;
                if (map[keyCell] == Terrain.HIGH_GRASS || map[keyCell] == Terrain.FURROWED_GRASS) {
                    map[keyCell] = Terrain.GRASS;
                    losBlocking[keyCell] = false;
                }
            } else {
                drop(item, cell).type = Heap.Type.HEAP;
            }
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
        }

        //use separate generator(s) for this to prevent held items, meta progress, and talents from affecting levelgen
        Random.pushGenerator(Random.Long());
        if (Dungeon.isChallenged(Challenges.DARKNESS)) {
            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            drop(new Torch(), cell);
            //add a second torch to help with the larger floor
            if (feeling == Feeling.LARGE) {
                cell = randomDropCell();
                if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                    map[cell] = Terrain.GRASS;
                    losBlocking[cell] = false;
                }
                drop(new Torch(), cell);
            }
        }
        Random.popGenerator();

        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            for (Item i : bonesItems) {
                drop(i, cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();

        Random.pushGenerator( Random.Long() );
        DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
        if (rose != null && rose.isIdentified() && !rose.cursed && Ghost.Quest.completed()){
            //aim to drop 1 petal every 2 floors
            int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

            for (int i=1; i <= petalsNeeded; i++) {
                //the player may miss a single petal and still max their rose.
                if (rose.droppedPetals < 11) {
                    Item item = new DriedRose.Petal();
                    int cell = randomDropCell();
                    drop( item, cell ).type = Heap.Type.HEAP;
                    if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                        map[cell] = Terrain.GRASS;
                        losBlocking[cell] = false;
                    }
                    rose.droppedPetals++;
                }
            }
        }
        Random.popGenerator();

        //cached rations try to drop in a special room on floors 2/4/7, to a max of 2/3
        //we increment dropped by 2 for compatibility with old saves, when the talent dropped 4/6 items
        Random.pushGenerator( Random.Long() );
        if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)){
            Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
            int targetFloor = (int)(2 + dropped.count());
            if (dropped.count() > 4) targetFloor++;
            if (Dungeon.depth >= targetFloor && dropped.count() < 2 + 2*Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS)){
                int cell;
                int tries = 100;
                boolean valid;
                do {
                    cell = randomDropCell(SpecialRoom.class);
                    valid = cell != -1 && !(room(cell) instanceof SecretRoom)
                            && !(room(cell) instanceof ShopRoom)
                            && map[cell] != Terrain.EMPTY_SP
                            && map[cell] != Terrain.WATER
                            && map[cell] != Terrain.PEDESTAL;
                } while (tries-- > 0 && !valid);
                if (valid) {
                    if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                        map[cell] = Terrain.GRASS;
                        losBlocking[cell] = false;
                    }
                    drop(new SupplyRation(), cell).type = Heap.Type.CHEST;
                    dropped.countUp(2);
                }
            }
        }
        Random.popGenerator();

        // ── Guide pages: only drop on branch 0 ───────────────────────────────────
        if (Dungeon.branch == 0) {
            Random.pushGenerator(Random.Long());
            Collection<String> allPages = Document.ADVENTURERS_GUIDE.pageNames();
            ArrayList<String> missingPages = new ArrayList<>();
            for (String page : allPages) {
                if (!Document.ADVENTURERS_GUIDE.isPageFound(page)) {
                    missingPages.add(page);
                }
            }

            //a total of 6 pages drop randomly, the rest are specially dropped or are given at the start
            missingPages.remove(Document.GUIDE_SEARCHING);

            //chance to find a page is 0/25/50/75/100% for floors 1/2/3/4/5+
            float dropChance = 0.25f * (Dungeon.depth - 1);
            if (!missingPages.isEmpty() && Random.Float() < dropChance) {
                GuidePage p = new GuidePage();
                p.page(missingPages.get(0));
                int cell = randomDropCell();
                if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                    map[cell] = Terrain.GRASS;
                    losBlocking[cell] = false;
                }
                drop(p, cell);
            }
            Random.popGenerator();
        } else {
            // Still consume the RNG slot so seeded runs stay consistent
            // regardless of whether this level is on a branch or not.
            Random.pushGenerator(Random.Long());
            Random.popGenerator();
        }

        // ── Lore pages ────────────────────────────────────────────────────────────
        // TODO a fair bit going on here, I might want to refactor/externalize this in the future
        Random.pushGenerator( Random.Long() );
        if (Document.ADVENTURERS_GUIDE.allPagesFound()){

            int region = 1+(Dungeon.depth-1)/5;

            Document regionDoc;
            switch( region ){
                default: regionDoc = null; break;
                case 1: regionDoc = Document.SEWERS_GUARD; break;
                case 2: regionDoc = Document.PRISON_WARDEN; break;
                case 3: regionDoc = Document.CAVES_EXPLORER; break;
                case 4: regionDoc = Document.CITY_WARLOCK; break;
                case 5: regionDoc = Document.HALLS_KING; break;
            }

            if (regionDoc != null && !regionDoc.allPagesFound()) {

                Dungeon.LimitedDrops limit = limitedDocs.get(regionDoc);

                if (limit == null || !limit.dropped()) {

                    float totalPages = 0;
                    float pagesFound = 0;
                    String pageToDrop = null;
                    for (String page : regionDoc.pageNames()) {
                        totalPages++;
                        if (!regionDoc.isPageFound(page)) {
                            if (pageToDrop == null) {
                                pageToDrop = page;
                            }
                        } else {
                            pagesFound++;
                        }
                    }
                    float percentComplete = pagesFound / totalPages;

                    // initial value is the first floor in a region
                    int targetFloor = 5*(region-1) + 1;
                    targetFloor += Math.round(3*percentComplete);

                    //TODO maybe drop last page in boss floor with custom logic?
                    if (Dungeon.depth >= targetFloor){
                        DocumentPage page = RegionLorePage.pageForDoc(regionDoc);
                        page.page(pageToDrop);
                        int cell = randomDropCell();
                        if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                            map[cell] = Terrain.GRASS;
                            losBlocking[cell] = false;
                        }
                        drop(page, cell);
                        if (limit != null) limit.drop();
                    }

                }

            }

        }
        Random.popGenerator();

        //ebony mimics >:)
        Random.pushGenerator(Random.Long());
        if (Random.Float() < MimicTooth.ebonyMimicChance()){
            ArrayList<Integer> candidateCells = new ArrayList<>();
            if (Random.Int(2) == 0){
                for (Heap h : heaps.valueList()){
                    if (h.type == Heap.Type.HEAP
                            && !(room(h.pos) instanceof SpecialRoom)
                            && findMob(h.pos) == null){
                        candidateCells.add(h.pos);
                    }
                }
            }

            if (candidateCells.isEmpty()) {
                if (Random.Int(5) == 0 && findMob(exit()) == null) {
                    candidateCells.add(exit());
                } else {
                    for (int i = 0; i < length(); i++) {
                        if (map[i] == Terrain.DOOR && findMob(i) == null) {
                            candidateCells.add(i);
                        }
                    }
                }
            }

            int pos = Random.element(candidateCells);
            mobs.add(Mimic.spawnAt(pos, EbonyMimic.class, false));
        }
        Random.popGenerator();


        //extra spyglass loot
        Random.pushGenerator(Random.Long());
        int items = (int)(Random.Float() + CrackedSpyglass.extraLootChance());
        for (int i = 0; i < items; i++){
            int cell = randomDropCell();
            if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
                map[cell] = Terrain.GRASS;
                losBlocking[cell] = false;
            }
            drop( Generator.randomUsingDefaults(), cell).hidden = true;
        }
        Random.popGenerator();

    }

    private static HashMap<Document, Dungeon.LimitedDrops> limitedDocs = new HashMap<>();
    static {
        limitedDocs.put(Document.SEWERS_GUARD, Dungeon.LimitedDrops.LORE_SEWERS);
        limitedDocs.put(Document.PRISON_WARDEN, Dungeon.LimitedDrops.LORE_PRISON);
        limitedDocs.put(Document.CAVES_EXPLORER, Dungeon.LimitedDrops.LORE_CAVES);
        limitedDocs.put(Document.CITY_WARLOCK, Dungeon.LimitedDrops.LORE_CITY);
        limitedDocs.put(Document.HALLS_KING, Dungeon.LimitedDrops.LORE_HALLS);
    }

    public ArrayList<Room> rooms() {
        return new ArrayList<>(rooms);
    }

    protected Room randomRoom( Class<?extends Room> type ) {
        Random.shuffle( rooms );
        return room( type );
    }

    public Room room (Class<?extends Room> type){
        for (Room r : rooms) {
            if (type.isInstance(r)) {
                return r;
            }
        }

        return null;
    }

    public Room room( int pos ) {
        for (Room room : rooms) {
            if (room.inside( cellToPoint(pos) )) {
                return room;
            }
        }

        return null;
    }

    protected int randomDropCell(){
        return randomDropCell(StandardRoom.class);
    }

    protected int randomDropCell( Class<?extends Room> roomType ) {
        int tries = 100;
        while (tries-- > 0) {
            Room room = randomRoom( roomType );
            if (room == null){
                return -1;
            }
            if (room != roomEntrance) {
                int pos = pointToCell(room.random());
                if (passable[pos] && !solid[pos]
                        && pos != exit()
                        && heaps.get(pos) == null
                        && room.canPlaceItem(cellToPoint(pos), this)
                        && findMob(pos) == null) {

                    Trap t = traps.get(pos);

                    //items cannot spawn on traps which destroy items
                    if (t == null ||
                            ! (t instanceof BurningTrap || t instanceof BlazingTrap
                                    || t instanceof ChillingTrap || t instanceof FrostTrap
                                    || t instanceof ExplosiveTrap || t instanceof DisintegrationTrap
                                    || t instanceof PitfallTrap)) {

                        return pos;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public int fallCell( boolean fallIntoPit ) {
        if (fallIntoPit) {
            for (Room room : rooms) {
                if (room instanceof PitRoom) {
                    ArrayList<Integer> candidates = new ArrayList<>();
                    for (Point p : room.getPoints()){
                        int cell = pointToCell(p);
                        if (passable[cell] &&
                                findMob(cell) == null){
                            candidates.add(cell);
                        }
                    }

                    if (!candidates.isEmpty()){
                        return Random.element(candidates);
                    }
                }
            }
        }

        return super.fallCell( fallIntoPit );
    }

    @Override
    public float levelExplorePercent( int depth ) {
        //A room is considered not explored if:
        HashSet<Room> missedRooms = new HashSet<>();

        //There are levelgen heaps which are undiscovered, in an openable container, or which contain keys
        for (Heap h : heaps.valueList()){
            if (h.autoExplored) continue;

            //we ignore crystal chests too as not all are openable
            if (!h.seen || (h.type != Heap.Type.HEAP && h.type != Heap.Type.FOR_SALE && h.type != Heap.Type.CRYSTAL_CHEST)){
                missedRooms.add(room(h.pos));
            } else {
                for (Item i : h.items){
                    if (i instanceof Key){
                        missedRooms.add(room(h.pos));
                        break;
                    }
                }
            }
        }

        //There is magical fire (blocks items) or sacrificial fire (contains items) in it
        for (Blob b : blobs.values()){
            if (b.volume > 0) {
                if (b instanceof MagicalFireRoom.EternalFire) {
                    missedRooms.add(room(MagicalFireRoom.class));
                } else if (b instanceof SacrificialFire) {
                    missedRooms.add(room(SacrificeRoom.class));
                }
            }
        }

        //There are undefeated statues or mimics in it
        for (Mob m : mobs.toArray(new Mob[0])){
            if (m.alignment != Char.Alignment.ALLY){
                if (m instanceof Statue && ((Statue) m).levelGenStatue){
                    missedRooms.add(room(StatueRoom.class)); //use room the statue came from
                } else if (m instanceof Mimic){
                    missedRooms.add(room(m.pos));
                }
            }
        }

        //it contains a barricade, locked door (player locked doors are fine though), or hidden door
        for (int i = 0; i < length; i++){
            if (map[i] == Terrain.BARRICADE || map[i] == Terrain.LOCKED_DOOR || map[i] == Terrain.SECRET_DOOR){
                //we use adjacent cells to find the room this is connected to
                // we ignore connection rooms and prefer rooms already missed
                // note that if the tile borders two non-connection rooms, it only counts one
                Room candidate = null;
                for (int j : PathFinder.NEIGHBOURS4){
                    if (room(i+j) != null){
                        if (candidate == null || !missedRooms.contains(candidate)){
                            candidate = room(i+j);
                        }
                    }
                }
                if (candidate != null) {
                    missedRooms.add(candidate);
                }

            }
        }

        //There are unused crystal keys for this room (only one crystal key room can be on each floor)
        // we ignore regular and golden keys as earlier checks would have already caught them
        for (Notes.KeyRecord rec : Notes.getRecords(Notes.KeyRecord.class)){
            if (rec.depth() == depth && rec.type() == CrystalKey.class){
                for (Room r : rooms()){
                    if (SpecialRoom.CRYSTAL_KEY_SPECIALS.contains(r.getClass())){
                        missedRooms.add(r);
                    }
                }
            }
        }

        //Note that it is NOT required for the player to see every tile or discover every trap.

        //score is reduced by 50%/30%/20% for each room missed
        // at 3 rooms missed this gives a score of 0 for the floor.
        switch (missedRooms.size()){
            case 0:
                return 1f;
            case 1:
                return 0.5f;
            case 2:
                return 0.2f;
            default:
                return 0f;
        }
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( "rooms", rooms );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );

        rooms = new ArrayList<>( (Collection<Room>) ((Collection<?>) bundle.getCollection( "rooms" )) );
        for (Room r : rooms) {
            r.onLevelLoad( this );
            if (r.isEntrance()){
                roomEntrance = r;
            } else if (r.isExit()){
                roomExit = r;
            }
        }
    }

}