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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Pride extends Buff {

    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    private int consecutiveHits = 0;
    private int turnsSinceLastHit = 0;

    @Override
    public boolean act() {
        if (target instanceof Hero
                && target.buff(TimekeepersHourglass.timeStasis.class) == null
                && target.buff(TimeStasis.class) == null){

            // Increment turns since last hit
            turnsSinceLastHit++;
        }

        spend(TICK);
        return true;
    }

    // Call this when the hero successfully hits an enemy
    public void onSuccessfulHit() {
        consecutiveHits++;
        // Cap at 12 hits
        consecutiveHits = Math.min(consecutiveHits, 12);
    }

    // Call this when the hero misses an attack
    public void onMissedAttack() {
        consecutiveHits = 0;
    }

    // Call this when the hero gets hit
    public void onHeroGotHit() {
        turnsSinceLastHit = 0;
    }

    // Returns the damage multiplier for hero's attacks (up to +30%)
    public float getDamageMultiplier() {
        return 1f + (consecutiveHits * 0.025f);
    }

    // Returns the damage multiplier for incoming damage (up to +200%)
    public float getIncomingDamageMultiplier() {
        // +200% max means 3x damage total
        float multiplier = 1f + Math.min(turnsSinceLastHit / 250f, 2f);
        return multiplier;
    }

    @Override
    public int icon() {
        return BuffIndicator.NONE;
    }

    @Override
    public String desc() {
        return "";
    }

    private static final String CONSECUTIVE_HITS = "consecutiveHits";
    private static final String TURNS_SINCE_HIT = "turnsSinceLastHit";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CONSECUTIVE_HITS, consecutiveHits);
        bundle.put(TURNS_SINCE_HIT, turnsSinceLastHit);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        consecutiveHits = bundle.getInt(CONSECUTIVE_HITS);
        turnsSinceLastHit = bundle.getInt(TURNS_SINCE_HIT);
    }
}