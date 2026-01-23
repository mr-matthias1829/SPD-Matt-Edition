package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class GreedOLD extends Buff implements Hero.Doom {

    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    int ticks = 0;

    @Override
    public boolean act() {
        if (target instanceof Hero
                && target.buff(TimekeepersHourglass.timeStasis.class) == null
                && target.buff(TimeStasis.class) == null
                && Dungeon.depth % 5 != 0) {

            if (Dungeon.depth >= 2) {
                int goldLoss = calculateGoldLoss();

                if (Dungeon.gold >= goldLoss) {
                    Dungeon.gold -= goldLoss;
                    ticks = 0;
                } else if (ticks % 2 == 1) {
                    int damage = calculateHealthLoss();
                    Dungeon.gold = 0;

                    Hero hero = (Hero) target;
                    hero.damage(damage, this);
                    ticks = 0;

                    //target.damage(damage, this);
                }
            }
        }
        spend(TICK);
        ticks++;
        return true;
    }

    private int calculateGoldLoss() {
        // 1 base + region level
        if (Dungeon.branch == 0){
            return 1 + (int)(Math.floor((double) Dungeon.depth / 5));
        } else {
            return (int)(Math.floor((double) Dungeon.depth / 10));
        }
    }
    private int calculateHealthLoss() {
        int goldLoss = calculateGoldLoss();
        if (Dungeon.gold >= goldLoss) {
            return 0;
        } else {
            int damage = (int) Math.ceil((goldLoss - Dungeon.gold) / 2f);
            if (damage < 1) damage = 1;
            return damage;
        }
    }


    @Override
    public String desc() {
        if (Dungeon.depth < 2) {
            return "Until you descend deeper, greed will not affect you...";
        }
        if (Dungeon.depth % 5 == 0 && Dungeon.branch == 0) {
            return "At this floor, greed will leave you be... for now.";
        }

        String add = "";
        if (Dungeon.branch != 0) {
            add = "\n\nBeing no longer in the main dungeon, greed's power over you is reduced.";
        }
        int loss = calculateGoldLoss();

        if (Dungeon.gold >= loss) {
            return "Losing " + loss + " gold per turn. " +
                    "Turns remaining until you have no gold left: " + Dungeon.gold/loss + "."
                    + add;
        } else {
            loss = calculateHealthLoss();
            return "Losing " + loss + " health every other turn. " +
                    "You have no gold! You will take damage every other turn!"
                    + add;
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.GREED;
    }

    @Override
    public void onDeath() {
        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }
}
