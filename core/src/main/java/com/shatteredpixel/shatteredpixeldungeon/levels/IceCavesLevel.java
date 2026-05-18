// java
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100F;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.IceSnake;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesDeepExit;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchExit;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StorageRoom;
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
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;

public class IceCavesLevel extends CavesLevel {
    {

        // reduced vision because... honestly no clue
        viewDistance = Math.min(5, viewDistance);

        color1 = 0x48a4c9; // Icy blue
        color2 = 0xe8f7ff; // Light ice blue
    }

    private int mainFloor = 14; // dynamic, so if it ever changes
    private int lastFloor = 18; // the floor where the optional part of ice caves ends
    private float sizeDiv = Math.max(1.0f, 0.75f*(Dungeon.depth - mainFloor));
    protected Room roomBranchExit;
    private ArrayList<Class<? extends Mob>> iceRotation;

    @Override
    public void playLevelMusic() {
        // TODO: this music fits, but is a bit too intense maybe? perhaps make another so it will follow the format of the other regions.
        Music.INSTANCE.play(Assets.Music.ICE_CAVES, true);
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
            initRooms.add(roomExit = new IceCavesDeepExit());  // replaces ExitRoom.createExit()
        } else if (Dungeon.depth == lastFloor) {
            // only add a entrance, we shouldnt go any deeper
            initRooms.add ( roomEntrance = EntranceRoom.createEntrance());
            initRooms.add (StandardRoom.createRoom(14));
            // TODO: this lacks a ending room. add a special room here with a reward.
            return initRooms;
        } else {
            // default entrance and exit rooms for non-main ice caves
            initRooms.add(roomEntrance = EntranceRoom.createEntrance(14));
            initRooms.add(roomExit = ExitRoom.createExit(14));
        }

        // Add standard rooms
        int standards = (int)(standardRooms(false)*(2.5/sizeDiv));
        //int standards = 27;
        for (int i = 0; i < standards; i++) {
            StandardRoom s = StandardRoom.createRoom(14);
            s.setSizeCat();
            initRooms.add(s);
        }

        // Add special rooms - DON'T FORGET TO INITIALIZE!
        SpecialRoom.initForFloor();
        int specials = (int)(specialRooms(false)*(1.5/sizeDiv));
        for (int i = 0; i < specials; i++) {
            SpecialRoom s = SpecialRoom.createRoom();
            // reroll if we get a StorageRoom, ice caves has its own barricade room
            while (s instanceof StorageRoom) {
                s = SpecialRoom.createRoom();
            }
            initRooms.add(s);
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
        if (!super.build()) return false;

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
        return (int)(super.mobLimit()*(3/sizeDiv));
    }

    @Override
    protected void createMobs() {

        iceRotation = new ArrayList<>();

        iceRotation.add(Elemental.FrostElemental.class);

        for (int i = 0; i < 3; i++) {
            iceRotation.add(DM200.class);
            iceRotation.add(Bat.class);
            iceRotation.add(FrozenSwarm.class);
        }

        for (int i = 0; i < 7; i++) {
            iceRotation.add(DM100F.class);
            iceRotation.add(IceSnake.class);
        }

        Collections.shuffle(iceRotation);

        super.createMobs();
    }

    @Override
    public Mob createMob() {
        if (iceRotation == null || iceRotation.isEmpty()) {
            return super.createMob();
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
        return new LoopBuilder()
                .setLoopShape(1, 0.25f, Random.Float(0f, 0.5f));
    }
    @Override
    protected Painter painter() {
        return new CavesPainter()
                .setWater(feeling == Feeling.WATER ? 0.60f : 0.30f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.45f : 0.15f, 3)
                .setTraps(nTraps(), trapClasses(), trapChances());
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
