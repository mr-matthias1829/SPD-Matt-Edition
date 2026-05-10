package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Sloth extends Buff implements Hero.Doom {

    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    int turns = 0;
    int bestDepth = 1;

    @Override
    public boolean act() {
        if (target instanceof Hero // Check if target is hero and if time is moving (this buff has no effect on enemies)
                && target.buff(TimekeepersHourglass.timeStasis.class) == null
                && target.buff(TimeStasis.class) == null){

            if (Dungeon.depth > bestDepth){ // Update best depth, since we may backtrack later
                bestDepth = Dungeon.depth;
            }

            if (Dungeon.branch != 0){ // Are we on the main branch?
                if (turns % 2 == 0){
                    turns++; // Only tick every other turn on side branches
                }
            } else {
                if (Dungeon.depth != 26) { // Do not tick on the ending level, a win should stay a win
                    turns++; // Tick
                }
            }

            if (calculateTurnsBehind() > 0 && calculateTurnsBehind() % 2 == 0) { // Are we behind? and on a even turn?
                int damage = calculateHealthLoss();

                Hero hero = (Hero) target;
                hero.damage(damage, this); // Damage based on how many turns behind we are

                if (!hero.isAlive()){
                    Badges.validateDeathFromOutOfTime();
                }
            }
        }
        spend(TICK);
        return true;
    }

    private int calculateTurnsBehind() { // returns negative number if ahead of expected
        int depth = bestDepth;
        int expectedTurns = 300 + (depth * 300);
        int bosses = (int)(Math.floor((double) (depth-1) / 5)); // overcomplicated because i trust this more

        expectedTurns += bosses * 500; // for every boss defeated, add 500 turns

        if (Dungeon.hero.buff(AscensionChallenge.class) != null){
            expectedTurns += 3000; // extra time when ascending... you'll still need to go quite fast
        }

        return turns - expectedTurns;
    }
    private int calculateHealthLoss() {
        int behind = calculateTurnsBehind();
        if (behind <= 0) {
            return 0;
        } else {
            return 1 + (int)(Math.floor((double) behind / 15)); // +1 damage for every 15 turns behind
        }
    }


    @Override
    public String desc() { // Inform hero of status on sloth
        String add = "";
        if (Dungeon.branch != 0) {
            add = "\n\nBeing no longer in the main dungeon, sloth's power over you is reduced, and the timer ticks slower.";
        } else if (Dungeon.depth == 26) {
            add = "\n\nBeing at the very bottom of the dungeon, sloth is leaving you be.";
        }
        int turnsBehind = calculateTurnsBehind();

        if (turnsBehind <= 0) {
            return "You're keeping up with your expected pace... " +
                    "You currently have _" + (-turnsBehind) + " turns_ remaining before you start falling behind."
                    + add;
        } else {
            int damage = calculateHealthLoss();
            return "Currently _losing " + damage + " health_ every turn. " +
                    "You are _" + turnsBehind + " turns_ behind your expected pace. " +
                    "You will continue to take more damage if you don't _speed up!_"
                    + add;
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.SLOTH;
    }

    @Override
    public void onDeath() {
        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }

    @Override
    public String iconTextDisplay() { // Display turns ahead or danger. Convenient for quick reference
        int T = calculateTurnsBehind();
        if (T > 0) {
            return "!!!";
        } else {
            return Integer.toString(-T);
        }
    }



    private static final String TURNS = "turns";
    private static final String BESTDEPTH = "bestDepth";

    @Override
    public void storeInBundle( Bundle bundle ) {

        super.storeInBundle(bundle);
        bundle.put( TURNS, turns );
        bundle.put( BESTDEPTH, bestDepth );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        turns = bundle.getInt( TURNS );
        bestDepth = bundle.getInt( BESTDEPTH );
    }
}
