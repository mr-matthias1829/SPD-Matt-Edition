package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ConsumingGreed extends Buff implements Hero.Doom {

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
                    if (ticks % turnsPerConsumption() == 0) {
                        Dungeon.gold -= goldLoss;
                        ticks = 0;
                    }
                } else if (ticks % turnsPerConsumption() == 0) {
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
        // 1 base + (region level -1)
        if (Dungeon.branch == 0){
            //int gold = 1+ ((int)(Math.floor((double) Dungeon.depth / 5))-1);
            int gold = 1; // 1
            if (Dungeon.depth > 5) gold += 1; //2
            if (Dungeon.depth > 20) gold += 1; //3

            // total gold loss each turn by region:
            // 1-5: 1/5 = 0.2
            // 6-10: 2/4 = 0.5
            // 11-15: 2/3 = 0.66
            // 16-20: 2/2 = 1
            // 21+: 3/2 = 1.5 (steep, but its demon halls)

            if (gold < 1) gold = 1;
            return gold;
        } else {
            return (int)(Math.floor((double) Dungeon.depth / 20));
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

    private int turnsPerConsumption() {
        if (Dungeon.depth <= 5) {
            return 5;
        }
        if (Dungeon.depth <= 10) {
            return 4;
        }
        if (Dungeon.depth <= 15) {
            return 3;
        }
        if (Dungeon.depth <= 27) {
            return 2;
        }
        return 1; // fallback
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
        int tpc = turnsPerConsumption();

        if (Dungeon.gold >= loss) {
            int consumptions = Dungeon.gold / loss;
            int turns = consumptions * tpc;


            return "Losing " + loss + " gold per turn. " +
                    "Turns remaining until you have no gold left: " + turns + "."
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
