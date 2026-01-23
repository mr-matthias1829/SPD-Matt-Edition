package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Greed extends Buff {

    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    // Tracks turns since last gold collection
    private int turnsSinceLastCollection = 0;
    private int ticks = 0;

    @Override
    public boolean act() {
        if (target instanceof Hero
                && target.buff(TimekeepersHourglass.timeStasis.class) == null
                && target.buff(TimeStasis.class) == null) {

            // Don't tick on boss floors
            if (Dungeon.depth % 5 != 0) {
                // Tick slower on non-main branch
                if (Dungeon.branch != 0) {
                    // Only tick every other turn on side branches
                    if (ticks % 2 == 0) {
                        turnsSinceLastCollection++;
                    }
                } else {
                    turnsSinceLastCollection++;
                }
            }
        }

        spend(TICK);
        ticks++;
        return true;
    }

    /**
     * Called when the hero collects gold.
     * Resets the turn counter.
     */
    public void onGoldCollected() {
        turnsSinceLastCollection = 0;
    }

    /**
     * Returns the gold multiplier based on how long it's been since last collection.
     */
    public float getGoldMultiplier() {
        // Boss floors always return 1x
        if (Dungeon.depth % 5 == 0) {
            return 1f;
        }

        // Main dungeon multiplier based on turns since last collection
        int turns = turnsSinceLastCollection;

        // Side branches use a reduced multiplier
        if (Dungeon.branch != 0) {
            if (turns <= 12) {
                return 1.8f;
            } else if (turns <= 25) {
                return 1.25f;
            } else if (turns <= 50) {
                return 1f;
            } else if (turns <= 100) {
                return 0.8f;
            }
        }

        // TODO: maybe just make this one formula instead of tiers?
        if (turns <= 8) {
            return 3f;
        } else if (turns <= 15) {
            return 2.5f;
        } else if (turns <= 20) {
            return 2f;
        } else if (turns <= 25) {
            return 1.5f;
        } else if (turns <= 45) {
            return 1f;
        } else if (turns <= 100) {
            return 0.7f;
        } else {
            return 0.5f;
        }
    }

    /* old
    public float getShopMultiplier() {
        int turns = ticks;
        return Math.min(1 + (turns / 1752f), 7.5f);
    }
     */

    public float getShopMultiplier() {
        int turns = ticks;

        // Each +x1 takes 20% longer than the previous
        float maxMultiplier = 8f;
        float currentMultiplier = 1; // Starter multiplier
        float remainingTurns = turns;
        float nextInterval = 1650; // Base turns needed for next +1

        while (remainingTurns >= nextInterval && currentMultiplier < maxMultiplier) {
            currentMultiplier += 1;
            remainingTurns -= nextInterval;
            nextInterval *= 1.2f; // Each interval 20% longer
        }

        // Add partial progress to next level
        if (currentMultiplier < maxMultiplier) {
            currentMultiplier += remainingTurns / nextInterval;
        }

        return Math.min(currentMultiplier, maxMultiplier);
    }

    /**
     * Returns the display multiplier for the buff description.
     * This accounts for side branches showing reduced values.
     */
    private float getDisplayMultiplier() {
        // Boss floors
        if (Dungeon.depth % 5 == 0) {
            return 1f;
        }

        // Always show 1x on side branches
        if (Dungeon.branch != 0) {
            return 1f;
        }

        return getGoldMultiplier();
    }

    @Override
    public String desc() {
        if (Dungeon.depth % 5 == 0 && Dungeon.branch == 0) {
            return Messages.get(this, "boss_floor");
        }

        String branchNote = "";
        if (Dungeon.branch != 0) {
            branchNote = "\n\n" + Messages.get(this, "side_branch");
        }

        float mult = getDisplayMultiplier();
        int turns = turnsSinceLastCollection;

        String multiplierText = String.format("%.1fx", mult);
        if (mult >= 3f) {
            return Messages.get(this, "excellent", multiplierText, turns) + branchNote;
        } else if (mult >= 2f) {
            return Messages.get(this, "good", multiplierText, turns) + branchNote;
        } else if (mult >= 1f) {
            return Messages.get(this, "normal", multiplierText, turns) + branchNote;
        } else {
            return Messages.get(this, "poor", multiplierText, turns) + branchNote;
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.GREED;
    }

    // Store state for save/load
    private static final String TURNS_SINCE_COLLECTION = "turns_since_collection";
    private static final String TICKS = "ticks";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURNS_SINCE_COLLECTION, turnsSinceLastCollection);
        bundle.put(TICKS, ticks);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsSinceLastCollection = bundle.getInt(TURNS_SINCE_COLLECTION);
        ticks = bundle.getInt(TICKS);
    }
}