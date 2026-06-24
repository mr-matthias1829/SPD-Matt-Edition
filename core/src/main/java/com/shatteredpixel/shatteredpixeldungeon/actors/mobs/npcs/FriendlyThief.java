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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.FriendlyThiefRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FriendlyThiefSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FriendlyThief extends NPC {

    {
        spriteClass = FriendlyThiefSprite.class;
        properties.add(Property.IMMOVABLE);
    }

    @Override
    public boolean interact(Char c) {

        sprite.turnTo(pos, c.pos);

        if (c != Dungeon.hero) return true;

        if (!Quest.spawned) return true;

        boolean hasMask = Dungeon.hero.belongings.getItem(TengusMask.class) != null;

        if (!hasMask) {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndQuest(FriendlyThief.this,
                            Messages.get(FriendlyThief.this, "get_lost")));
                }
            });
            return true;
        }

        if (Quest.entranceOpen) {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    GameScene.show(new WndQuest(FriendlyThief.this,
                            Messages.get(FriendlyThief.this, "entrance_already_open")));
                }
            });
            return true;
        }

        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                GameScene.show(new WndOptions(
                        sprite(),
                        Messages.titleCase(Messages.get(FriendlyThief.this, "name")),
                        Messages.get(FriendlyThief.this, "you_may_enter"),
                        Messages.get(FriendlyThief.this, "enter_yes"),
                        Messages.get(FriendlyThief.this, "enter_no")
                ) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            openEntrance();
                        }
                    }
                });
            }
        });

        return true;
    }
    private void openEntrance() {
        Quest.entranceOpen = true;

        if (Quest.entranceCell != -1) {
            Dungeon.level.map[Quest.entranceCell] = Terrain.EXIT;

            Dungeon.level.transitions.add(new LevelTransition(
                    Dungeon.level,
                    Quest.entranceCell,
                    LevelTransition.Type.BRANCH_EXIT,
                    Dungeon.depth,
                    2,
                    LevelTransition.Type.BRANCH_ENTRANCE,
                    "guild_branch_entrance",
                    "guild_entrance"));

            Dungeon.level.buildFlagMaps();
            Dungeon.level.cleanWalls();
            GameScene.updateMap(Quest.entranceCell);
            GameScene.discoverTile(Quest.entranceCell, Dungeon.level.map[Quest.entranceCell]);

            CellEmitter.center(Quest.entranceCell).burst(Speck.factory(Speck.DISCOVER), 1);
            Sample.INSTANCE.play(Assets.Sounds.SECRET);
        }

        GLog.h(Messages.get(FriendlyThief.class, "entrance_opened"));
    }

    public static class Quest {

        static boolean spawned      = false;
        static boolean entranceOpen = false;
        static boolean accessed     = false;
        static int     entranceCell = -1;

        private static boolean questRoomSpawned = false;

        private static final String NODE          = "friendlythief";
        private static final String SPAWNED       = "spawned";
        private static final String ENTRANCE_OPEN = "entrance_open";
        private static final String ACCESSED      = "accessed";
        private static final String ENTRANCE_CELL = "entrance_cell";

        public static void reset() {
            spawned          = false;
            entranceOpen     = false;
            accessed         = false;
            entranceCell     = -1;
            questRoomSpawned = false;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(SPAWNED,       spawned);
            node.put(ENTRANCE_OPEN, entranceOpen);
            node.put(ACCESSED,      accessed);
            node.put(ENTRANCE_CELL, entranceCell);
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull() && node.getBoolean(SPAWNED)) {
                spawned      = true;
                entranceOpen = node.getBoolean(ENTRANCE_OPEN);
                accessed     = node.getBoolean(ACCESSED);
                entranceCell = node.getInt(ENTRANCE_CELL);
            } else {
                reset();
            }
        }

        // Public accessors used by FriendlyThiefRoom
        public static boolean spawned()       { return spawned; }
        public static boolean entranceOpen()  { return entranceOpen; }
        public static boolean accessed()      { return accessed; }
        public static int     getEntranceCell() { return entranceCell; }

        public static void setEntranceCell(int cell) { entranceCell = cell; }
        public static void markSpawned()             { spawned = true; }

        public static ArrayList<Room> spawnRoom(ArrayList<Room> rooms) {
            questRoomSpawned = false;
            if (!spawned && !accessed) {
                // depth 8: 2/3 chance, depth 9: 1/3 chance (if didn't spawn on 8)
                if ((Dungeon.depth == 8 && Random.Int(3) != 0)
                        || Dungeon.depth == 9) {
                    rooms.add(new FriendlyThiefRoom());
                    questRoomSpawned = true;
                    spawned = true;
                }
            }
            return rooms;
        }

        public static void spawnFriendlyThief(Level level) {
            if (!questRoomSpawned) return;
            questRoomSpawned = false;

            if (entranceCell == -1) return;

            FriendlyThief npc = new FriendlyThief();

            boolean validPos;
            int tries = 0;
            int dist  = 3;

            do {
                validPos = true;

                if (tries > 20 && dist > 1) {
                    tries = 0;
                    dist--;
                }

                int offset = PathFinder.NEIGHBOURS8[Random.Int(8)];
                int candidate = entranceCell + offset * (1 + Random.Int(dist));

                npc.pos = candidate;

                if (level.solid[npc.pos] || !level.passable[npc.pos]) {
                    validPos = false;
                } else {
                    for (int n : PathFinder.NEIGHBOURS4) {
                        if (level.map[npc.pos + n] == Terrain.DOOR
                                || level.map[npc.pos + n] == Terrain.LOCKED_DOOR) {
                            validPos = false;
                            break;
                        }
                    }
                }

                if (level.traps.get(npc.pos) != null) validPos = false;
                if (level.findMob(npc.pos)    != null) validPos = false;

                tries++;

            } while (!validPos && tries < 40);

            if (!validPos) return;

            level.mobs.add(npc);
        }

        public static void markAccessed() {
            accessed = true;

            if (Dungeon.level != null) {
                for (com.shatteredpixel.shatteredpixeldungeon.actors.Actor a
                        : com.shatteredpixel.shatteredpixeldungeon.actors.Actor.all()) {
                    if (a instanceof FriendlyThief) {
                        ((FriendlyThief) a).destroy();
                        break;
                    }
                }
            }

            if (Dungeon.level != null && entranceCell != -1) {
                Dungeon.level.map[entranceCell] = Terrain.PEDESTAL;
                Dungeon.level.buildFlagMaps();
                Dungeon.level.cleanWalls();
            }
        }

        // NEW: only safe to call once the hero is physically back on the cell
        public static void cleanupTransition() {
            if (Dungeon.level != null && entranceCell != -1) {
                final int cell = entranceCell;
                Dungeon.level.transitions.removeIf(t -> t.cell() == cell);
            }
        }
    }

    @Override public int defenseSkill(Char enemy)     { return INFINITE_EVASION; }
    @Override public void damage(int dmg, Object src) { /*I AM GOD*/ }
    @Override public boolean add(Buff buff)           { return false; }
    @Override public boolean reset()                  { return true; }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
    }
}