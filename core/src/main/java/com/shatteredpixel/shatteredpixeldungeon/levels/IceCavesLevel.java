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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchExit;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
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

        viewDistance = Math.min( 5, viewDistance );

        color1 = 0x48a4c9; // Icy blue
        color2 = 0xe8f7ff; // Light ice blue
    }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.ICE_CAVES, true);
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();

        // Add entrance and exit rooms first
        initRooms.add(roomEntrance = new IceCavesBranchEntrance());
        initRooms.add(roomExit = new IceCavesBranchExit());

        // Add standard rooms
        int standards = standardRooms(false)*4;
        //int standards = 27;
        for (int i = 0; i < standards; i++) {
            StandardRoom s = StandardRoom.createRoom();
            s.setSizeCat();
            initRooms.add(s);
        }

        // Add special rooms - DON'T FORGET TO INITIALIZE!
        SpecialRoom.initForFloor();
        int specials = (int)(specialRooms(false)*2);
        //int specials = 5;
        for (int i = 0; i < specials; i++) {
            SpecialRoom s = SpecialRoom.createRoom();
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
    public int mobLimit() {
        // more than usual because the floor is bigger
        //return (int)(super.mobLimit()*1.8f);
        return super.mobLimit()*3;
    }

    @Override
    public Mob createMob() {
        // If we're in ice caves, always use our custom mob rotation
        ArrayList<Class<? extends Mob>> iceRotation = new ArrayList<>();


        iceRotation.add(Elemental.FrostElemental.class);

        // 3
        for (int i = 0; i < 3; i++) {
            iceRotation.add(DM200.class);
            iceRotation.add(Bat.class);
            iceRotation.add(FrozenSwarm.class);
        }

        // 7, so they are slightly more common
        for (int i = 0; i < 7; i++) {
            iceRotation.add(DM100F.class);
            iceRotation.add(IceSnake.class);
        }

        // Repeat to fill out rotation
        iceRotation.addAll(new ArrayList<>(iceRotation));
        Collections.shuffle(iceRotation);

        // Use our ice rotation instead of the default depth-based one
        Mob m = Reflection.newInstance(iceRotation.remove(0));
        ChampionEnemy.rollForChampion(m);
        return m;
    }

    // And simplify your createMobs() to just call super
    @Override
    protected void createMobs() {
        // Let the parent class handle spawning logic,
        // but it will use our overridden createMob() method
        super.createMobs();
    }


    @Override
    public float respawnCooldown() {
        //normal enemies respawn slower here
        return 2*TIME_TO_RESPAWN;
    }

    @Override
    protected Builder builder() {
        // Use LoopBuilder instead of FigureEightBuilder
        return new LoopBuilder()
                .setLoopShape(2, Random.Float(0.3f, 0.7f), Random.Float(0f, 0.5f));
    }
    @Override
    protected Painter painter() {
        return new CavesPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
                .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
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
                GrippingTrap.class, RockfallTrap.class, GuardianTrap.class,
                ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class };
    }

    @Override
    protected float[] trapChances() {
        return new float[]{
                8, 6, 4, 4,
                2, 2, 2,
                1, 1, 1, 1 };
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
