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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100F;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.IceSnake;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StorageRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.MusicAnnouncer;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;

public class IceCavesLevel extends CavesLevel {
    // tbh no idea why we extend caves level when we basically change and override everything

    // NOTE: in some pit/chasm class (ngl, i forgot the name), we hardcoded changed the floor it falls to
    // branch 2 floor 14 falls onto itself,
    // prevents ultra softlocking because B2F15 to B2F14 puts you at deep exit, which is a puzzle room you can softlock yourself out of
    // B0F14 falls to B2F14, since that makes sense and prevents skipping ice caves
    // i have a feeling that i gotta fix this at one point :D

    // also, this class mixes the main floor and the branch floors, making this class quite crowded

    public static final int mainFloor = 14; // dynamic, so if it ever changes
    public static final int lastFloor = 18; // the floor where the optional part of ice caves ends
                                    // you'd still need to modify it in dungeon.java
    {
        // reduced vision because... honestly no clue
        viewDistance = Math.min(5, viewDistance);
        splashRegionOverride = 3;

        color1 = 0x48a4c9; // Icy blue
        color2 = 0xe8f7ff; // Light ice blue

        canSpawnFood = canSpawnItems = Dungeon.depth != lastFloor;
        canSpawnGurItems = false; // no SOU and such, would make no sense to even have here

        if (Dungeon.depth == mainFloor) {
            setItemsToSpawn = 5 + Random.Int(3); // these are random items, always 5 + 0-2
        } else if (Dungeon.depth == lastFloor){
            setItemsToSpawn = 0;
        } else {

            // while there is branch loot, we keep this sparse
            // mainly because some more rewarding loot lies at the final floor
            // though, whether players find the final floor loot even good is up to them
            setItemsToSpawn = Dungeon.depth - lastFloor;
            setItemsToSpawn += 1;

            setWeaponArmorCount = 1; // always 1 weapon/armor
            setScrollPotionCount = setItemsToSpawn -1; // rest is always a scroll or potion
            if (Dungeon.depth == lastFloor-1) {
                setItemsToSpawn += 1;
                setMissileCount = 1; // missile weapon on floor before last flor
            }
        }

        canFeeling = Dungeon.depth != mainFloor; // only branch floors can have feelings
    }

    private float sizeDiv = 1f+(0.75f*(Dungeon.depth - mainFloor));// gets smaller the deeper you go
    protected Room roomBranchExit;
    private ArrayList<Class<? extends Mob>> iceRotation;

    @Override
    public void playLevelMusic() {
        if (Statistics.amuletObtained || Dungeon.depth == mainFloor) {
            MusicAnnouncer.play(Assets.Music.ICE_CAVES_TENSE, true);
        } else {
            MusicAnnouncer.play(Assets.Music.ICE_CAVES, true);
        }
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();

        // Add entrance and exit rooms first
        // These are unique as they properly transfer branch
        if (Dungeon.depth == mainFloor) {
            // rooms for proper branching
            initRooms.add(roomEntrance = new IceCavesBranchEntrance());
            initRooms.add(roomBranchExit = new IceCavesBranchExit());
            // rooms that descends deeper into ice caves
            initRooms.add(roomExit = new IceCavesDeepExit());
        } else {
            // default entrance and exit rooms for non-main ice caves
            initRooms.add(roomEntrance = EntranceRoom.createEntrance(14));
            initRooms.add(roomExit = ExitRoom.createExit(14));
        }

        // Add standard rooms
        int standards = (int)(standardRooms(false)*(2.5/sizeDiv));
        if (Dungeon.depth == lastFloor-1) {
            standards += 1; // additional standard rooms to make up for no special rooms
        }
        //int standards = 27;
        for (int i = 0; i < standards; i++) {
            StandardRoom s = StandardRoom.createRoom(14);
            s.setSizeCat();
            initRooms.add(s);
        }

        // Add special rooms - DON'T FORGET TO INITIALIZE!
        if (Dungeon.depth != lastFloor-1) {
            // floor before last floor has no special rooms or secret rooms
            SpecialRoom.initForFloor();
            int specials = (int) (specialRooms(false) * (1.5 / sizeDiv));
            for (int i = 0; i < specials; i++) {
                SpecialRoom s = SpecialRoom.createRoom();
                // reroll if we get a StorageRoom, ice caves has its own barricade room
                while (s instanceof StorageRoom) {
                    s = SpecialRoom.createRoom();
                }
                initRooms.add(s);
            }
        } else {
            return initRooms;
        }

        // Add secret rooms
        int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
        //int secrets = 2;
        for (int i = 0; i < secrets; i++) {
            initRooms.add(SecretRoom.createRoom());
        }

        return initRooms;
    }


    @Override
    protected boolean build() {

        if (Dungeon.depth == lastFloor) {
            setSize(48, 48);
            rooms = new ArrayList<>();
            IceShrineRoom shrine = new IceShrineRoom();
            shrine.set(
                    5,
                    5,
                    30,
                    30
            );
            roomEntrance = shrine;
            rooms.add(shrine);
            shrine.paint(this);
            return true;
        }
        if (!super.build()) return false;

        // methods that make sure both exits are far away enough
        // only goes for main floor
        if (roomEntrance != null && roomBranchExit != null) {
            if (distance(pointToCell(roomEntrance.center()), pointToCell(roomBranchExit.center())) < 12) {
                return false;
            }
        }
        if (roomEntrance != null && roomExit != null) {
            if (distance(pointToCell(roomEntrance.center()), pointToCell(roomExit.center())) < 12) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int mobLimit() {
        // more than usual because the floor is bigger
        //return (int)(super.mobLimit()*1.8f);
        if (Dungeon.depth == lastFloor){
            return 0; // no mobs on the last floor, its more of a reward floor with a special room
        }
        int add = Dungeon.depth - mainFloor;
        return (int)((super.mobLimit() + add)*(2/Math.max(1f, sizeDiv/2)));
    }

    private void buildMobRotation() {

        iceRotation = new ArrayList<>();

        int floor = Dungeon.depth - mainFloor;

        switch (floor) {
            case 3:
                for (int i = 0; i < 2; i++) {
                    iceRotation.add(Elemental.FrostElemental.class);
                }

                for (int i = 0; i < 5; i++) { // 50% of full pool
                    iceRotation.add(IceGolem.class);
                }

                iceRotation.add(FrozenSwarm.class);
                iceRotation.add(DM100F.class);
                iceRotation.add(IceSnake.class);
                break;
            case 2:
                    iceRotation.add(Elemental.FrostElemental.class);
                    iceRotation.add(FrozenSwarm.class);
                    iceRotation.add(DM100F.class);
                    iceRotation.add(IceSnake.class);

                for (int i = 0; i < 2; i++) { // 33% of full pool
                    iceRotation.add(IceGolem.class);
                }
                break;

            case 1:
                iceRotation.add(Elemental.FrostElemental.class);

                for (int i = 0; i < 2; i++) {
                    iceRotation.add(Bat.class);
                    iceRotation.add(FrozenSwarm.class);
                    iceRotation.add(DM100F.class);
                    iceRotation.add(IceSnake.class);
                    iceRotation.add(IceGolem.class); // ~18% of full pool
                }
                break;

            case 0:
            default:
                iceRotation.add(Elemental.FrostElemental.class);

                for (int i = 0; i < 3; i++) {
                    iceRotation.add(DM200.class);
                    iceRotation.add(Bat.class);
                    iceRotation.add(FrozenSwarm.class);
                }

                for (int i = 0; i < 5; i++) {
                    iceRotation.add(DM100F.class);
                    iceRotation.add(IceSnake.class);
                }
                break;
        }

        Collections.shuffle(iceRotation);
    }

    @Override
    protected void createMobs() {

        buildMobRotation();

        super.createMobs();
    }

    @Override
    public Mob createMob() {

        if (iceRotation == null || iceRotation.isEmpty()) {
            buildMobRotation();
        }

        Mob m = Reflection.newInstance(iceRotation.remove(0));

        ChampionEnemy.rollForChampion(m);

        return m;
    }


    @Override
    public float respawnCooldown() {
        //normal enemies respawn slower here
        return (int)(1.5*TIME_TO_RESPAWN);
    }

    @Override
    protected Builder builder() {
        // Use LoopBuilder instead of FigureEightBuilder
        if (Dungeon.depth == lastFloor) {
            return new Builder() {
                @Override
                public ArrayList<Room> build(ArrayList<Room> rooms) {
                    return rooms;
                }
            };
        } else {
            return new LoopBuilder()
                    .setLoopShape(1, 0.25f, Random.Float(0f, 0.5f));
        }
    }
    @Override
    protected Painter painter() {
        return new CavesPainter()
                .setWater(feeling == Feeling.WATER ? 0.60f : 0.30f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.45f : 0.15f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    protected int nTraps() {

        if (Dungeon.depth == lastFloor) {
            return 0;
        } else {
            return (int) (super.nTraps() * 1.15);
        }
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_ICE2;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_CAVES;
    }

    @Override
    protected Class<?>[] trapClasses() {
        // Ice caves has more frost/cold themed traps
        return new Class[]{
                FrostTrap.class, ChillingTrap.class, StormTrap.class, CorrosionTrap.class,
                GrippingTrap.class, RockfallTrap.class, GuardianTrap.class, WarpingTrap.class,
                ConfusionTrap.class, SummoningTrap.class, PitfallTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                4, 3, 3, 3,
                2, 2, 2, 2,
                1, 1, 1 };
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.GRASS:
                return Messages.get(IceCavesLevel.class, "grass_name");
            case Terrain.HIGH_GRASS:
                return Messages.get(IceCavesLevel.class, "high_grass_name");
            case Terrain.WATER:
                return Messages.get(IceCavesLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
            case Terrain.ENTRANCE_SP:
                return Messages.get(IceCavesLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(IceCavesLevel.class, "exit_desc");
            case Terrain.HIGH_GRASS:
                return Messages.get(IceCavesLevel.class, "high_grass_desc");
            case Terrain.WALL_DECO:
                return Messages.get(IceCavesLevel.class, "wall_deco_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CavesLevel.addCavesVisuals(this, visuals);
        return visuals;
    }
}
