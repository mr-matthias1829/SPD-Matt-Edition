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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MusicAnnouncer {
    // welcome to awesome QOL class
    // genuinely one of the files i actually love
    // the idea is simple: if a new track plays, announce it + credits
    // will help prevent copyright issues in the future

    // whichever name you put, put AT LEAST the original name in the credits and the alias you gave it + creator
    // some licenses don't force you to credit them, but we do so anyway

    // ── Track IDs ─────────────────────────────────────────────────────────────
    public static final int NONE              = -1;
    public static final int SEWERS            = 1;
    public static final int SEWERS_TENSE      = 2;
    public static final int SEWERS_BOSS       = 3;
    public static final int PRISON            = 4;
    public static final int PRISON_TENSE      = 5;
    public static final int PRISON_BOSS       = 6;
    public static final int CAVES             = 7;
    public static final int CAVES_TENSE       = 8;
    public static final int CAVES_BOSS        = 9;
    public static final int METROPOLIS        = 10;
    public static final int METROPOLIS_TENSE  = 11;
    public static final int METROPOLIS_BOSS   = 12;
    public static final int DEMON_HALLS       = 13;
    public static final int DEMON_HALLS_TENSE = 14;
    public static final int DEMON_HALLS_BOSS  = 15;
    public static final int FINALE            = 16;
    public static final int TITLE             = 17; // pretty sure this will never be used, but since we COULD, list it anyway


    public static final int ICE_CAVES_TENSE   = 18; // created (and used) before calm one, so its above
    public static final int ICE_CAVES    = 19;

    // ── filename → track ID ───────────────────────────────────────────────────
    private static final HashMap<String, Integer> FILE_TO_ID = new HashMap<>();
    static {
        FILE_TO_ID.put("none",                 NONE);

        FILE_TO_ID.put("music/sewers_1.ogg",            SEWERS);
        FILE_TO_ID.put("music/sewers_2.ogg",            SEWERS);
        FILE_TO_ID.put("music/sewers_3.ogg",            SEWERS);
        FILE_TO_ID.put("music/sewers_tense.ogg",        SEWERS_TENSE);
        FILE_TO_ID.put("music/sewers_boss.ogg",         SEWERS_BOSS);

        FILE_TO_ID.put("music/prison_1.ogg",            PRISON);
        FILE_TO_ID.put("music/prison_2.ogg",            PRISON);
        FILE_TO_ID.put("music/prison_3.ogg",            PRISON);
        FILE_TO_ID.put("music/prison_tense.ogg",        PRISON_TENSE);
        FILE_TO_ID.put("music/prison_boss.ogg",         PRISON_BOSS);

        FILE_TO_ID.put("music/caves_1.ogg",             CAVES);
        FILE_TO_ID.put("music/caves_2.ogg",             CAVES);
        FILE_TO_ID.put("music/caves_3.ogg",             CAVES);
        FILE_TO_ID.put("music/caves_tense.ogg",         CAVES_TENSE);
        FILE_TO_ID.put("music/caves_boss.ogg",          CAVES_BOSS);
        FILE_TO_ID.put("music/caves_boss_finale.ogg",   CAVES_BOSS);

        FILE_TO_ID.put("music/city_1.ogg",              METROPOLIS);
        FILE_TO_ID.put("music/city_2.ogg",              METROPOLIS);
        FILE_TO_ID.put("music/city_3.ogg",              METROPOLIS);
        FILE_TO_ID.put("music/city_tense.ogg",          METROPOLIS_TENSE);
        FILE_TO_ID.put("music/city_boss.ogg",           METROPOLIS_BOSS);
        FILE_TO_ID.put("music/city_boss_finale.ogg",    METROPOLIS_BOSS);

        FILE_TO_ID.put("music/halls_1.ogg",             DEMON_HALLS);
        FILE_TO_ID.put("music/halls_2.ogg",             DEMON_HALLS);
        FILE_TO_ID.put("music/halls_3.ogg",             DEMON_HALLS);
        FILE_TO_ID.put("music/halls_tense.ogg",         DEMON_HALLS_TENSE);
        FILE_TO_ID.put("music/halls_boss.ogg",          DEMON_HALLS_BOSS);
        FILE_TO_ID.put("music/halls_boss_finale.ogg",   DEMON_HALLS_BOSS);

        FILE_TO_ID.put("music/theme_finale.ogg",        FINALE);
        FILE_TO_ID.put("music/theme_1.ogg",             TITLE);
        FILE_TO_ID.put("music/theme_2.ogg",             TITLE);

        FILE_TO_ID.put("music/icecaves.ogg",            ICE_CAVES_TENSE);
        FILE_TO_ID.put("music/icecaves_calm.ogg",      ICE_CAVES);
    }

    // ── track ID → display name ───────────────────────────────────────────────
    private static final HashMap<Integer, String> ID_TO_NAME = new HashMap<>();
    static {
        // the "help i technically have no category" category
        ID_TO_NAME.put(NONE,              "No Music");

        // vanilla spd
        ID_TO_NAME.put(SEWERS,            "Sewers - SPD");
        ID_TO_NAME.put(SEWERS_TENSE,      "Sewers Tense - SPD");
        ID_TO_NAME.put(SEWERS_BOSS,       "Goo - SPD");
        ID_TO_NAME.put(PRISON,            "Prison - SPD");
        ID_TO_NAME.put(PRISON_TENSE,      "Prison Tense - SPD");
        ID_TO_NAME.put(PRISON_BOSS,       "Tengu - SPD");
        ID_TO_NAME.put(CAVES,             "Caves - SPD");
        ID_TO_NAME.put(CAVES_TENSE,       "Caves Tense - SPD");
        ID_TO_NAME.put(CAVES_BOSS,        "DM300 - SPD");
        ID_TO_NAME.put(METROPOLIS,        "Ruins - SPD");
        ID_TO_NAME.put(METROPOLIS_TENSE,  "Ruins Tense - SPD");
        ID_TO_NAME.put(METROPOLIS_BOSS,   "Dwarf King - SPD");
        ID_TO_NAME.put(DEMON_HALLS,       "Halls - SPD");
        ID_TO_NAME.put(DEMON_HALLS_TENSE, "Halls Tense - SPD");
        ID_TO_NAME.put(DEMON_HALLS_BOSS,  "Yog Dzewa - SPD");
        ID_TO_NAME.put(FINALE,            "Game Finale - SPD");
        ID_TO_NAME.put(TITLE,             "Main Theme - SPD");

        // this modded version
        ID_TO_NAME.put(ICE_CAVES_TENSE,   "Ice Caves Tense - Matt Edition");

        // literally any other music
        ID_TO_NAME.put(ICE_CAVES,          "Cold Arctic Ambient - Lux-aeterna");
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private static int lastAnnouncedId = Integer.MIN_VALUE;
    private static String  pendingAnnounce = null;
    private static String  currentFile     = null;
    private static boolean wasPlaying      = false;
    private static int     graceFrames     = 0;

    private static String[] activeList    = null;
    private static float[]  activeChances = null;
    private static boolean  activeShuffle = false;
    private static final ArrayList<String> queue = new ArrayList<>();

    // ── Public API ────────────────────────────────────────────────────────────

    public static synchronized void playTracks(String[] tracks, float[] chances, boolean shuffle) {
        if (tracks == null || tracks.length == 0 || tracks.length != chances.length) {
            end(); return;
        }
        if (activeList != null && Arrays.equals(tracks, activeList)) return;

        activeList    = tracks.clone();
        activeChances = chances.clone();
        activeShuffle = shuffle;
        queue.clear();
        buildQueue();
        startNext(true);
    }

    public static synchronized void play(String track, boolean looping) {
        if (track == null) { end(); return; }
        activeList = null;
        queue.clear();
        startSingle(track, looping);
        maybeAnnounce(track);
    }

    public static synchronized void end() {
        activeList = null;
        currentFile = null;
        wasPlaying = false;
        graceFrames = 0;
        pendingAnnounce = null;
        queue.clear();
        Music.INSTANCE.end(); // must stay as Music.INSTANCE

        // immediate "No Music" announcement; update() also handles delayed silence detection
        maybeAnnounce("none");
    }

    public static synchronized void fadeOut(float duration, Callback onComplete) {
        activeList = null;
        currentFile = null;
        queue.clear();

        Music.INSTANCE.fadeOut(duration, onComplete); // must stay as Music.INSTANCE
    }

    public static synchronized void update() {
        // flush any announcement that was queued before the log was ready
        if (pendingAnnounce != null) {
            GLog.h(pendingAnnounce);
            pendingAnnounce = null;
        }

        if (graceFrames > 0) { graceFrames--; wasPlaying = Music.INSTANCE.isPlaying(); return; }

        boolean nowPlaying = Music.INSTANCE.isPlaying();

        if (!nowPlaying && !Music.INSTANCE.paused()) {
            if (activeList != null) {
                startNext(false);
            } else if (lastAnnouncedId != NONE) {
                maybeAnnounce("none");
            }
        }

        wasPlaying = nowPlaying;
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static void buildQueue() {
        queue.clear();
        for (int i = 0; i < activeList.length; i++) {
            if (Random.Float() < activeChances[i]) queue.add(activeList[i]);
        }
        if (queue.isEmpty()) queue.add(activeList[Random.Int(activeList.length)]);
        if (activeShuffle) Collections.shuffle(queue);
    }

    private static void startNext(boolean forceAnnounce) {
        if (queue.isEmpty()) buildQueue();
        String next = queue.remove(0);
        startSingle(next, false);
        if (forceAnnounce) lastAnnouncedId = NONE;
        maybeAnnounce(next);
    }

    private static void startSingle(String file, boolean looping) {
        currentFile = file;
        wasPlaying  = false;
        graceFrames = 4;
        Music.INSTANCE.play(file, looping); // must stay as Music.INSTANCE
    }

    private static void maybeAnnounce(String file) {
        int id = FILE_TO_ID.containsKey(file) ? FILE_TO_ID.get(file) : NONE;
        if (id != lastAnnouncedId) {
            lastAnnouncedId = id;
            String name = ID_TO_NAME.containsKey(id) ? ID_TO_NAME.get(id) : file;
            pendingAnnounce = "Now playing music: " + name;
        }
    }
}