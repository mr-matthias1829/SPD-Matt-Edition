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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Sins;
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

            // making this formula, i ASSUME you'll spend around 1200 turns per region
            // for demon halls: thats total -1800 gold, if you can safely gobble that up, you must be insane
            // also im not taking bosses into account, this is why im working on the sword of midas

            // but wait! what if the sin of greed is active as well?
            // welllll... we tick 1 turn faster, turning the total gold loss into:
            // 1-5: 1/4 = 0.25
            // 6-10: 2/3 = 0.66
            // 11-15: 2/2 = 1
            // 16-20: 2/1 = 2
            // 21+: 3/1 = 3
            // you want to greed? go ahead buddy, i'm not stopping you :)
            // would also ruin your chances for buying anything in any shop, but hey, you do you

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
        int bonusReduction = 0;
        if (Dungeon.isSinActive(Sins.GREED)){
            bonusReduction = 1;
        }
        if (Dungeon.depth <= 5) {
            return 5-bonusReduction;
        }
        if (Dungeon.depth <= 10) {
            return 4-bonusReduction;
        }
        if (Dungeon.depth <= 15) {
            return 3-bonusReduction;
        }
        if (Dungeon.depth <= 27) {
            return 2-bonusReduction;
        }
        return 1; // fallback
    }


    @Override
    public String desc() { // suprise suprise, the description here is actually super dynamic
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
        return BuffIndicator.CONSUMING_GREED;
    }

    @Override
    public void onDeath() {
        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }
}
