/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class FishingRod extends Artifact {

    {
        image = ItemSpriteSheet.FISHING_ROD;

        levelCap = 10;

        charge = 0; // 0 = no bait, 1 = has bait
        chargeCap = 1;

        defaultAction = AC_USE;
    }

    @Override
    public int value() {
        return 28 * quantity;
    }

    private int storedExp = 0;

    public static final String AC_USE = "USE";
    public static final String AC_BAIT = "BAIT";

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (hero.buff(MagicImmune.class) != null) return actions;

        if (isEquipped( hero ) && !cursed) {
            if (charge > 0) {
                actions.add(AC_USE);
            }
            if (charge == 0) {
                actions.add(AC_BAIT);
            }
        }
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_USE)) {

            if (!isEquipped(hero)) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            } else if (cursed) {
                GLog.w( Messages.get(this, "cursed") );
            } else if (charge == 0) {
                GLog.i( Messages.get(this, "no_food") );
            } else {
                // Check if hero is on water tile
                int terrain = Dungeon.level.map[hero.pos];
                if (terrain != Terrain.WATER) {
                    GLog.w( Messages.get(this, "no_water") );
                    return;
                }

                // Perform fishing
                doFish(hero);
            }

        } else if (action.equals(AC_BAIT)) {

            if (!isEquipped(hero)) {
                GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            } else if (cursed) {
                GLog.w( Messages.get(this, "cursed") );
            } else if (charge > 0) {
                GLog.i( Messages.get(this, "full") );
            } else {
                GameScene.selectItem(itemSelector);
            }

        }
    }

    private void doFish(Hero hero) {
        // Consume bait
        charge = 0;

        // Visual effect
        Splash.at(hero.pos, 0x2F6496, 10);
        hero.sprite.operate(hero.pos);
        hero.busy();
        hero.spend(6f);
        Sample.INSTANCE.play(Assets.Sounds.WATER);

        // Generate loot based on depth and rod level
        Item loot = generateLoot();

        if (loot != null) {
            if (loot.doPickUp(hero)) {
            } else {
                Dungeon.level.drop(loot, hero.pos).sprite.drop();
            }

            if (loot instanceof Gold){
                GLog.p(Messages.get(this, "fish_caught_gold", loot.quantity(), loot.name()));
            } else {
                GLog.p(Messages.get(this, "fish_caught", loot.name()));
            }


            // Gain exp based on loot rarity - stored in a temporary variable during generation
            gainExp(lastCatchRarity);
        } else {
            // Caught nothing
            GLog.w( Messages.get(this, "nothing_caught") );
        }

        updateQuickslot();
    }

    // Tracks the last catch's rarity for exp purposes
    private int lastCatchRarity = 0;

    private void gainExp(int rarity) {
        if (level() >= levelCap) return;

        // Exp values: common=1, uncommon=3, rare=8, legendary=16
        int expGain = rarity;
        storedExp += expGain;

        int expNeeded = getExpNeeded(level());

        while (storedExp >= expNeeded && level() < levelCap) {
            storedExp -= expNeeded;
            upgrade();
            Catalog.countUse(FishingRod.class);

            if (level() == levelCap) {
                storedExp = 0;
                GLog.p( Messages.get(this, "maxlevel") );
            } else {
                GLog.p( Messages.get(this, "levelup") );
            }

            expNeeded = getExpNeeded(level());
        }
    }

    // Calculate exp needed for a given level
    // Level 0->1: 1 exp
    // Level 1->2: 2 exp
    // Level 2->3: 3 exp
    // Level 3->4: 4 exp
    // Level 4->5: 5 exp
    // Level 5->6: 7 exp (increment goes up by 1)
    // Level 6->7: 9 exp
    // etc.
    private int getExpNeeded(int currentLevel) {
        if (currentLevel >= levelCap) return Integer.MAX_VALUE;

        double multiplier = 1.0 + currentLevel * 0.05; // 5% per level
        return (int) Math.round((currentLevel + 1) * multiplier);
    }

    private Item generateLoot() {
        int depth = Dungeon.depth;
        int rodLevel = level();

        // Chance to catch nothing decreases with rod level
        // Level 0-4: has a chance to catch nothing
        // Level 5+: always catches something
        if (rodLevel < 5) {
            float nothingChance = 0.35f - (rodLevel * 0.04f); // 30%, 24%, 18%, 12%, 6%
            if (Random.Float() < nothingChance) {
                lastCatchRarity = 0; // No exp for catching nothing
                return null; // Caught nothing
            }
        }

        // Luck bonus from rod level (1% per level)
        float luckBonus = rodLevel * 0.01f;

        // Loot table using Generator - Roll from best to worst!
        float roll = Random.Float();

        // Legendary items (5% base + luck bonus)
        if (roll < 0.05f + luckBonus) {
            lastCatchRarity = 16; // 16 exp for legendary
            // Rings or wands
            if (Random.Int(2) == 0) {
                return Generator.random(Generator.Category.RING);
            } else {
                return Generator.random(Generator.Category.WAND);
            }
        }
        // Rare items (10% base + luck bonus)
        else if (roll < 0.10f + (luckBonus * 2)) {
            lastCatchRarity = 8; // 8 exp for rare
            // Armor or melee weapons
            if (Random.Int(2) == 0) {
                return Generator.randomArmor();
            } else {
                return Generator.randomWeapon();
            }
        }
        // Uncommon items (25% base)
        else if (roll < 0.25f + (luckBonus * 2)) {
            lastCatchRarity = 3; // 3 exp for uncommon
            // Potions or scrolls
            if (Random.Int(2) == 0) {
                return Generator.random(Generator.Category.POTION);
            } else {
                return Generator.random(Generator.Category.SCROLL);
            }
        }
        // Common items (60% base, fills the rest)
        else {
            lastCatchRarity = 1; // 1 exp for common
            // Gold, seeds, or runestones
            int type = Random.Int(3);
            switch (type) {
                case 0: return new Gold(Random.IntRange(depth * 5, depth * 15));
                case 1: return Generator.random(Generator.Category.SEED);
                default: return Generator.random(Generator.Category.STONE);
            }
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new fishingBuff();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if ( isEquipped( Dungeon.hero ) ){
            if (!cursed) {
                desc += "\n\n" + Messages.get(this, "desc_hint");
                desc += "\n\n" + Messages.get(this, "desc_level", level(), levelCap);
                //desc += "\n" + Messages.get(this, "desc_luck", level() * 0.05); // Display as decimal
            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    @Override
    public String status() {
        if (!isIdentified() || cursed) {
            return null;
        }

        if (charge > 0) {
            return Messages.get(this, "status_ready");
        } else {
            return Messages.get(this, "status_no_bait");
        }
    }

    private static final String CHARGE_STATE = "charge_state";
    private static final String STORED_EXP = "stored_exp";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE_STATE, charge);
        bundle.put(STORED_EXP, storedExp);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge = bundle.getInt(CHARGE_STATE);
        storedExp = bundle.getInt(STORED_EXP);
    }

    public class fishingBuff extends ArtifactBuff {
        // Passive luck bonus when equipped
        // You can implement this further if you want passive effects
    }

    protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(FishingRod.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Food;
        }

        @Override
        public void onSelect( Item item ) {
            if (item != null && item instanceof Food) {
                if (item instanceof Blandfruit && ((Blandfruit) item).potionAttrib == null){
                    GLog.w( Messages.get(FishingRod.class, "reject") );
                } else {
                    Hero hero = Dungeon.hero;
                    hero.sprite.operate( hero.pos );
                    hero.busy();
                    hero.spend( Food.TIME_TO_EAT );

                    // Set bait
                    ((FishingRod)curItem).charge = 1;
                    ((FishingRod)curItem).updateQuickslot();

                    GLog.i( Messages.get(FishingRod.class, "bait_set") );
                    item.detach(hero.belongings.backpack);
                }
            }
        }
    };
}