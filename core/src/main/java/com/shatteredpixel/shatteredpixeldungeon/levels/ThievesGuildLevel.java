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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.MusicAnnouncer;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Burglar;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snatcher;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.FriendlyThief;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.GuildVaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesBranchExit;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Custom.IceCavesDeepExit;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StorageRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.utils.Random;

import java.util.ArrayList;
public class ThievesGuildLevel extends PrisonLevel {

    {
        color1 = 0x6a723d;
        color2 = 0x88924c;


        canSpawnItems = canSpawnFood = true;

        canFeeling = false;
        canSpawnGurItems = false; // shouldnt matter since you generated f10 before this, but still
    }


    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<>();

        // Add entrance
        initRooms.add(roomEntrance = EntranceRoom.createEntrance(9));
        GuildVaultRoom vault = new GuildVaultRoom();
        initRooms.add(vault);

        // Add standard rooms
        int standards = standardRooms(false);
        for (int i = 0; i < standards; i++) {
            StandardRoom s = StandardRoom.createRoom(9);
            initRooms.add(s);
        }

        /*
        SpecialRoom.initForFloor();
        int specials = (specialRooms(false));
        for (int i = 0; i < specials; i++) {
            SpecialRoom s = SpecialRoom.createRoom();
            initRooms.add(s);
        }

        // add secret rooms
        int secrets = SecretRoom.secretsForFloor(Dungeon.depth);

        for (int i = 0; i < secrets; i++) {
            initRooms.add(SecretRoom.createRoom());
        }
         */


        // SAFE CLEANSE: Replace any PitRoom with a safe alternative
        for (int i = 0; i < initRooms.size(); i++) {
            Room r = initRooms.get(i);

            // Check for the dedicated Special PitRoom
            boolean isPitRoom = r instanceof PitRoom;

            // Check for standard rooms that generate chasms/pits by default
            // We check their class type names directly
            String className = r.getClass().getSimpleName();
            boolean isChasmStandardRoom = className.equals("FissureRoom")
                    || className.equals("PlatformRoom")
                    || className.equals("ChasmRoom");

            if (isPitRoom || isChasmStandardRoom) {
                // Replace it completely with a perfectly flat standard room
                StandardRoom replacement = StandardRoom.createRoom(14);
                replacement.setSizeCat();
                initRooms.set(i, replacement);
            }
        }

        ((FigureEightBuilder)builder).setLandmarkRoom(vault);

        return initRooms;
    }


    @Override
    protected Builder builder() {
        return new FigureEightBuilder()
                .setPathLength(0.7f, new float[]{1})
                .setTunnelLength(new float[]{1}, new float[]{1});
    }


    @Override
    protected int standardRooms(boolean forceMax) {
        return super.standardRooms(false) + 2;
        //return forceMax ? 16 : Random.NormalIntRange(15, 16);
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        // haha NO
        return 0;
    }


    @Override
    public void create() {
        super.create();

        FriendlyThief.Quest.snapshotGoldOnEntry();

        // replace all chasms after generation
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.CHASM) {
                map[i] = Terrain.EMPTY;
            }
        }

        buildFlagMaps();

        LevelTransition oldEntrance = null;

        for (LevelTransition t : transitions) {
            if (t.type == LevelTransition.Type.REGULAR_ENTRANCE) {
                oldEntrance = t;
                break;
            }
        }

        if (oldEntrance != null) {

            int entranceCell = oldEntrance.cell();

            transitions.remove(oldEntrance);

            transitions.add(new LevelTransition(
                    this,
                    entranceCell,
                    LevelTransition.Type.BRANCH_ENTRANCE,
                    Dungeon.depth,
                    0,
                    LevelTransition.Type.BRANCH_EXIT,
                    "guild_entrance",
                    "guild_branch_entrance"
            ));
        } else {
            GLog.w("GuildLevel: No entrance found");
            GLog.w("Entrance might not work as expected");
        }
    }


    private ArrayList<Class<? extends Mob>> mobRotation;

    private void buildMobRotation() {

        mobRotation = new ArrayList<>();

        for (int i = 0; i < 4; i++) // 40%
            mobRotation.add(Thief.class);

        for (int i = 0; i < 3; i++) // 30%
            mobRotation.add(Burglar.class);

        for (int i = 0; i < 3; i++) // 30%
            mobRotation.add(Snatcher.class);

        Random.shuffle(mobRotation);
    }

    @Override
    protected void createMobs() {
        buildMobRotation();
        super.createMobs();
    }

    @Override
    public Mob createMob() {

        if (mobRotation == null || mobRotation.isEmpty()) {
            buildMobRotation();
        }

        Class<? extends Mob> type = mobRotation.remove(0);
        return (Mob) com.watabou.utils.Reflection.newInstance(type);
    }

    @Override
    public int mobLimit() {
        return (int) (super.mobLimit() * 1.5);
        //return 25;
    }

    @Override
    public float respawnCooldown() {
        return TIME_TO_RESPAWN * 2.2f; // quite long for... well, everyone's sanity
    }

    @Override
    protected Painter painter() {
        return new PrisonPainter()
                .setWater(0.2f, 4)
                .setGrass(0.15f, 3)
                .setTraps(0, new Class[0], new float[0]);
    }

    @Override
    public void playLevelMusic() {
        MusicAnnouncer.play(Assets.Music.PRISON_TENSE, true);
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_THIEF_GUILD;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_PRISON;
    }

    public int featuresRow() {
        return 1;
    }


    @Override
    public String tileName( int tile ) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(ThievesGuildLevel.class, "water_name");
            case Terrain.REGION_DECO:
                return Messages.get(ThievesGuildLevel.class, "region_deco_name");
            case Terrain.REGION_DECO_ALT:
                return Messages.get(ThievesGuildLevel.class, "region_deco_alt_name");
            default:
                return super.tileName( tile );
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(ThievesGuildLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(ThievesGuildLevel.class, "bookshelf_desc");
            case Terrain.REGION_DECO:
                return Messages.get(ThievesGuildLevel.class, "region_deco_desc");
            case Terrain.REGION_DECO_ALT:
                return Messages.get(ThievesGuildLevel.class, "region_deco_alt_desc");
            default:
                return super.tileDesc( tile );
        }
    }


    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.BRANCH_ENTRANCE
                && transition.destBranch == 0
                && !FriendlyThief.Quest.accessed()) {

            FriendlyThief.Quest.markAccessed();
        }
        return super.activateTransition(hero, transition);
    }
}