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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Greed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.*;

public class Badges {

	public enum BadgeType {
		HIDDEN, //internal badges used for data tracking
		LOCAL,  //unlocked on a per-run basis and added to overall player profile
		GLOBAL, //unlocked for the save profile only, usually over multiple runs
		JOURNAL, //profile-based and also tied to the journal, which means they even unlock in seeded runs
        SECRET    // hidden away until achieved
	}

	public enum Badge {
		MASTERY_WARRIOR,
		MASTERY_MAGE,
		MASTERY_ROGUE,
		MASTERY_HUNTRESS,
		MASTERY_DUELIST,
		MASTERY_CLERIC,
		FOUND_RATMOGRIFY,

		//bronze
        UNLOCK_WARRIOR_A                ( 0 ),
        UNLOCK_MAGE_A                 ( 1 ),
        UNLOCK_ROGUE_A                ( 2 ),
        UNLOCK_HUNTRESS_A             ( 3 ),
        UNLOCK_DUELIST_A              ( 4 ),
        UNLOCK_CLERIC_A               ( 5 ),

		UNLOCK_MAGE                 ( 1 ),
		UNLOCK_ROGUE                ( 2 ),
		UNLOCK_HUNTRESS             ( 3 ),
		UNLOCK_DUELIST              ( 4 ),
		UNLOCK_CLERIC               ( 5 ),

        UNLOCK_ALL_HERO_CLASSES     ( 200, BadgeType.SECRET ),

		MONSTERS_SLAIN_1            ( 6 ),
		MONSTERS_SLAIN_2            ( 7 ),
		GOLD_COLLECTED_1            ( 8 ),
		GOLD_COLLECTED_2            ( 9 ),
		ITEM_LEVEL_1                ( 10 ),
		LEVEL_REACHED_1             ( 11 ),
		STRENGTH_ATTAINED_1         ( 12 ),
		FOOD_EATEN_1                ( 13 ),
		ITEMS_CRAFTED_1             ( 14 ),
		BOSS_SLAIN_1                ( 15 ),
		CATALOG_ONE_EQUIPMENT       ( 16, BadgeType.JOURNAL ),
		DEATH_FROM_FIRE             ( 17 ),
		DEATH_FROM_POISON           ( 18 ),
		DEATH_FROM_GAS              ( 19 ),
		DEATH_FROM_HUNGER           ( 20 ),
		DEATH_FROM_FALLING          ( 21 ),
		RESEARCHER_1                ( 22, BadgeType.JOURNAL ),
		GAMES_PLAYED_1              ( 23, BadgeType.GLOBAL ),
		HIGH_SCORE_1                ( 24 ),

		//silver
		NO_MONSTERS_SLAIN           ( 32 ),
		BOSS_SLAIN_REMAINS          ( 33 ),
		MONSTERS_SLAIN_3            ( 34 ),
		MONSTERS_SLAIN_4            ( 35 ),
		GOLD_COLLECTED_3            ( 36 ),
		GOLD_COLLECTED_4            ( 37 ),
		ITEM_LEVEL_2                ( 38 ),
		ITEM_LEVEL_3                ( 39 ),
		LEVEL_REACHED_2             ( 40 ),
		LEVEL_REACHED_3             ( 41 ),
		STRENGTH_ATTAINED_2         ( 42 ),
		STRENGTH_ATTAINED_3         ( 43 ),
		FOOD_EATEN_2                ( 44 ),
		FOOD_EATEN_3                ( 45 ),
		ITEMS_CRAFTED_2             ( 46 ),
		ITEMS_CRAFTED_3             ( 47 ),
		BOSS_SLAIN_2                ( 48 ),
		BOSS_SLAIN_3                ( 49 ),
		ALL_POTIONS_IDENTIFIED      , //still exists internally for pre-2.5 saves
		ALL_SCROLLS_IDENTIFIED      , //still exists internally for pre-2.5 saves
		CATALOG_POTIONS_SCROLLS     ( 50 ),
		DEATH_FROM_ENEMY_MAGIC      ( 51 ),
		DEATH_FROM_FRIENDLY_MAGIC   ( 52 ),
		DEATH_FROM_SACRIFICE        ( 53 ),
		BOSS_SLAIN_1_WARRIOR,
		BOSS_SLAIN_1_MAGE,
		BOSS_SLAIN_1_ROGUE,
		BOSS_SLAIN_1_HUNTRESS,
		BOSS_SLAIN_1_DUELIST,
		BOSS_SLAIN_1_CLERIC,
		BOSS_SLAIN_1_ALL_CLASSES    ( 54, BadgeType.GLOBAL ),
		RESEARCHER_2                ( 55, BadgeType.JOURNAL ),
		GAMES_PLAYED_2              ( 56, BadgeType.GLOBAL ),
		HIGH_SCORE_2                ( 57 ),

		//gold
		ENEMY_HAZARDS               ( 64 ),
		PIRANHAS                    ( 65 ),
		GRIM_WEAPON                 ( 66 ),
		BAG_BOUGHT_VELVET_POUCH,
		BAG_BOUGHT_SCROLL_HOLDER,
		BAG_BOUGHT_POTION_BANDOLIER,
		BAG_BOUGHT_MAGICAL_HOLSTER,
		ALL_BAGS_BOUGHT             ( 67 ),

        GEARED_UP                   (273, BadgeType.SECRET),
		MASTERY_COMBO               ( 68 ),
        MASTERY_COMBO_2             ( 270 ),
		MONSTERS_SLAIN_5            ( 69 ),
		GOLD_COLLECTED_5            ( 70 ),
		ITEM_LEVEL_4                ( 71 ),
		LEVEL_REACHED_4             ( 72 ),
		STRENGTH_ATTAINED_4         ( 73 ),
		STRENGTH_ATTAINED_5         ( 74 ),

        STRENGTH_ATTAINED_6         ( 74 ),
		FOOD_EATEN_4                ( 75 ),
		FOOD_EATEN_5                ( 76 ),
		ITEMS_CRAFTED_4             ( 77 ),
		ITEMS_CRAFTED_5             ( 78 ),
		BOSS_SLAIN_4                ( 79 ),
		ALL_RINGS_IDENTIFIED        , //still exists internally for pre-2.5 saves
		ALL_ARTIFACTS_IDENTIFIED    , //still exists internally for pre-2.5 saves
		ALL_RARE_ENEMIES            ( 80, BadgeType.JOURNAL ), //no longer all, just 10 as of v3.1
		DEATH_FROM_GRIM_TRAP        ( 81 ), //also disintegration traps
		VICTORY                     ( 82 ),
		BOSS_CHALLENGE_1            ( 83 ),
		BOSS_CHALLENGE_2            ( 84 ),
		RESEARCHER_3                ( 85, BadgeType.JOURNAL ),
		GAMES_PLAYED_3              ( 86, BadgeType.GLOBAL ),
		HIGH_SCORE_3                ( 87 ),

        //platinum
        MANY_BUFFS                  ( 96 ),
        ITEM_LEVEL_5                ( 97 ),
        LEVEL_REACHED_5             ( 98 ),
        HAPPY_END                   ( 99 ),
        VICTORY_RANDOM              ( 100 ),
        HAPPY_END_REMAINS           ( 101 ),
        RODNEY                      ( 102, BadgeType.JOURNAL ),
        ALL_WEAPONS_IDENTIFIED      , //still exists internally for pre-2.5 saves
        ALL_ARMOR_IDENTIFIED        , //still exists internally for pre-2.5 saves
        ALL_WANDS_IDENTIFIED        , //still exists internally for pre-2.5 saves
        ALL_ITEMS_IDENTIFIED        , //still exists internally for pre-2.5 saves
        VICTORY_WARRIOR,
        VICTORY_MAGE,
        VICTORY_ROGUE,
        VICTORY_HUNTRESS,
        VICTORY_DUELIST,
        VICTORY_CLERIC,
        VICTORY_ALL_CLASSES         ( 103, BadgeType.GLOBAL ),
        DEATH_FROM_ALL              ( 104, BadgeType.GLOBAL ),
        BOSS_SLAIN_3_GLADIATOR,
        BOSS_SLAIN_3_BERSERKER,
        BOSS_SLAIN_3_WARLOCK,
        BOSS_SLAIN_3_BATTLEMAGE,
        BOSS_SLAIN_3_FREERUNNER,
        BOSS_SLAIN_3_ASSASSIN,
        BOSS_SLAIN_3_SNIPER,
        BOSS_SLAIN_3_WARDEN,
        BOSS_SLAIN_3_CHAMPION,
        BOSS_SLAIN_3_MONK,
        BOSS_SLAIN_3_PRIEST,
        BOSS_SLAIN_3_PALADIN,
        BOSS_SLAIN_3_ALL_SUBCLASSES ( 105, BadgeType.GLOBAL ),
        BOSS_CHALLENGE_3            ( 106 ),
        BOSS_CHALLENGE_4            ( 107 ),
        RESEARCHER_4                ( 108, BadgeType.JOURNAL ),
        GAMES_PLAYED_4              ( 109, BadgeType.GLOBAL ),
        HIGH_SCORE_4                ( 110 ),
        CHAMPION_1                  ( 111 ),

        //diamond
        PACIFIST_ASCENT             ( 120 ),
        TAKING_THE_MICK             ( 121 ), //This might be the most obscure game reference I've made
        BOSS_CHALLENGE_5            ( 122 ),
        RESEARCHER_5                ( 123, BadgeType.JOURNAL ),
        GAMES_PLAYED_5              ( 124, BadgeType.GLOBAL ),
        HIGH_SCORE_5                ( 125 ),
        CHAMPION_2                  ( 126 ),
        CHAMPION_3                  ( 127 ),


        // victory with X chal
        VICTORY_WITH_8_CHALLENGES(325),
        VICTORY_WITH_10_CHALLENGES(326),
        VICTORY_WITH_ALL_CHALLENGES(327),

        // damage in one hit
        STRONG (265),
        MIGHTY (267),
        POWERFUL (268),
        OBLIVION (269),


        // misc kinda badges
        OUT_OF_TIME (217),
        WHAT_DID_IT_COST_EVERYTHING (218),
        NO_UPGRADE_BOSS3 (296),
        AGAINST_ALL_ODDS (206),
        AGAINST_EVERYTHING_AND_MORE (207, BadgeType.SECRET),
        TO_HELL_AND_BACK (272, BadgeType.SECRET),
        AWKWARD_DM151_UNBALANCE(208, BadgeType.SECRET), // super rare occurance, hence why its hidden

        // PROGRESSIVE misc badges
        KILL_TITLE_ENEMY     ( 208 ),
        ICE_CAVE_TOURIST     ( 209 ),
        THIEF_DEJA_VU     ( 210 ),

        YENDORS_KEY         (323, BadgeType.SECRET), // unob as of now. ALOT will need to be done for this one


        // impossible badge, used for some debug testing
        // also means i dont have to move the ; part, super nice
        IMPOSSIBLE ( 1, BadgeType.SECRET );

		public boolean meta;

		public int image;
		public BadgeType type;

		Badge(){
			this(-1, BadgeType.HIDDEN);
		}

		Badge( int image ) {
			this( image, BadgeType.LOCAL );
		}

		Badge( int image, BadgeType type ) {
			this.image = image;
			this.type = type;
		}

		public String title(){
			return Messages.get(this, name()+".title");
		}

		public String desc(){
			return Messages.get(this, name()+".desc");
		}
	}
	
	private static HashSet<Badge> global;
	private static HashSet<Badge> local = new HashSet<>();
	
	private static boolean saveNeeded = false;


    private static boolean badgesDisabled = false;
    public static boolean badgesDisabled() {
        return badgesDisabled;
    }

    public static void setBadgesDisabled(boolean disabled) {
        badgesDisabled = disabled;
    }

	public static void reset() {
		local.clear();
		loadGlobal();
	}
	
	public static final String BADGES_FILE	= "badges.dat";
	private static final String BADGES		= "badges";
	
	private static final HashSet<String> removedBadges = new HashSet<>();
	static{
		//no removed badges currently
	}

	private static final HashMap<String, String> renamedBadges = new HashMap<>();
	static{
		//no renamed badges currently
	}

	public static HashSet<Badge> restore( Bundle bundle ) {
		HashSet<Badge> badges = new HashSet<>();
		if (bundle == null) return badges;
		
		String[] names = bundle.getStringArray( BADGES );
		if (names == null) return badges;

		for (int i=0; i < names.length; i++) {
			try {
				if (renamedBadges.containsKey(names[i])){
					names[i] = renamedBadges.get(names[i]);
				}
				if (!removedBadges.contains(names[i])){
					badges.add( Badge.valueOf( names[i] ) );
				}
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}

		addReplacedBadges(badges);
	
		return badges;
	}
	
	public static void store( Bundle bundle, HashSet<Badge> badges ) {
		addReplacedBadges(badges);

		int count = 0;
		String names[] = new String[badges.size()];
		
		for (Badge badge:badges) {
			names[count++] = badge.name();
		}
		bundle.put( BADGES, names );
	}
	
	public static void loadLocal( Bundle bundle ) {
		local = restore( bundle );
	}
	
	public static void saveLocal( Bundle bundle ) {
		store( bundle, local );
	}
	
	public static void loadGlobal() {
		if (global == null) {
			try {
				Bundle bundle = FileUtils.bundleFromFile( BADGES_FILE );
				global = restore( bundle );

			} catch (IOException e) {
				global = new HashSet<>();
			}
		}
	}

	public static void saveGlobal(){
		saveGlobal(false);
	}

	public static void saveGlobal(boolean force) {
		if (saveNeeded || force) {
			
			Bundle bundle = new Bundle();
			store( bundle, global );
			
			try {
				FileUtils.bundleToFile(BADGES_FILE, bundle);
				saveNeeded = false;
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}
	}

	public static int totalUnlocked(boolean global){
		if (global) return Badges.global.size();
		else        return Badges.local.size();
	}

	public static void validateMonstersSlain() {
		Badge badge = null;
		
		if (!local.contains( Badge.MONSTERS_SLAIN_1 ) && Statistics.enemiesSlain >= 10) {
			badge = Badge.MONSTERS_SLAIN_1;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_2 ) && Statistics.enemiesSlain >= 50) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_2;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_3 ) && Statistics.enemiesSlain >= 100) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_3;
			local.add( badge );
		}
        if (!local.contains( Badge.UNLOCK_WARRIOR_A ) && Statistics.enemiesSlain >= 60) {
            if (badge != null) unlock(badge);
            badge = Badge.UNLOCK_WARRIOR_A;
            local.add( badge );
            validateAllHeroClassesUnlocked();
        }
		if (!local.contains( Badge.MONSTERS_SLAIN_4 ) && Statistics.enemiesSlain >= 250) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_4;
			local.add( badge );
		}
		if (!local.contains( Badge.MONSTERS_SLAIN_5 ) && Statistics.enemiesSlain >= 500) {
			if (badge != null) unlock(badge);
			badge = Badge.MONSTERS_SLAIN_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateGoldCollected() {
		Badge badge = null;
		
		if (!local.contains( Badge.GOLD_COLLECTED_1 ) && Statistics.goldCollected >= 250) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_2 ) && Statistics.goldCollected >= 1000) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_3 ) && Statistics.goldCollected >= 2500) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_4 ) && Statistics.goldCollected >= 7500) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.GOLD_COLLECTED_5 ) && Statistics.goldCollected >= 15_000) {
			if (badge != null) unlock(badge);
			badge = Badge.GOLD_COLLECTED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateLevelReached() {
		Badge badge = null;
		
		if (!local.contains( Badge.LEVEL_REACHED_1 ) && Dungeon.hero.lvl >= 6) {
			badge = Badge.LEVEL_REACHED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_2 ) && Dungeon.hero.lvl >= 12) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_3 ) && Dungeon.hero.lvl >= 18) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_4 ) && Dungeon.hero.lvl >= 24) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.LEVEL_REACHED_5 ) && Dungeon.hero.lvl >= 30) {
			if (badge != null) unlock(badge);
			badge = Badge.LEVEL_REACHED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateStrengthAttained() {
		Badge badge = null;
		
		if (!local.contains( Badge.STRENGTH_ATTAINED_1 ) && Dungeon.hero.STR >= 13) {
			badge = Badge.STRENGTH_ATTAINED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_2 ) && Dungeon.hero.STR >= 16) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_3 ) && Dungeon.hero.STR >= 19) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_4 ) && Dungeon.hero.STR >= 22) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.STRENGTH_ATTAINED_5 ) && Dungeon.hero.STR >= 25) {
			if (badge != null) unlock(badge);
			badge = Badge.STRENGTH_ATTAINED_5;
			local.add( badge );
		}
        if (!local.contains( Badge.STRENGTH_ATTAINED_6 ) && Dungeon.hero.STR >= 30) {
            if (badge != null) unlock(badge);
            badge = Badge.STRENGTH_ATTAINED_6;
            local.add( badge );
        }
		
		displayBadge( badge );
	}
	
	public static void validateFoodEaten() {
		Badge badge = null;
		
		if (!local.contains( Badge.FOOD_EATEN_1 ) && Statistics.foodEaten >= 10) {
			badge = Badge.FOOD_EATEN_1;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_2 ) && Statistics.foodEaten >= 20) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_2;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_3 ) && Statistics.foodEaten >= 30) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_3;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_4 ) && Statistics.foodEaten >= 40) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_4;
			local.add( badge );
		}
		if (!local.contains( Badge.FOOD_EATEN_5 ) && Statistics.foodEaten >= 50) {
			if (badge != null) unlock(badge);
			badge = Badge.FOOD_EATEN_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateItemsCrafted() {
		Badge badge = null;
		
		if (!local.contains( Badge.ITEMS_CRAFTED_1 ) && Statistics.itemsCrafted >= 3) {
			badge = Badge.ITEMS_CRAFTED_1;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_2 ) && Statistics.itemsCrafted >= 8) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_2;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_3 ) && Statistics.itemsCrafted >= 15) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_3;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_4 ) && Statistics.itemsCrafted >= 24) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_4;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEMS_CRAFTED_5 ) && Statistics.itemsCrafted >= 35) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEMS_CRAFTED_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}

	public static void validateHazardAssists() {
		if (!local.contains( Badge.ENEMY_HAZARDS ) && Statistics.hazardAssistedKills >= 10) {
			local.add( Badge.ENEMY_HAZARDS );
			displayBadge( Badge.ENEMY_HAZARDS );
		}
	}
	
	public static void validatePiranhasKilled() {
		Badge badge = null;
		
		if (!local.contains( Badge.PIRANHAS ) && Statistics.piranhasKilled >= 6) {
			badge = Badge.PIRANHAS;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateItemLevelAquired( Item item ) {
		
		// This method should be called:
		// 1) When an item is obtained (Item.collect)
		// 2) When an item is upgraded (ScrollOfUpgrade, ScrollOfWeaponUpgrade, ShortSword, WandOfMagicMissile)
		// 3) When an item is identified

		// Note that artifacts should never trigger this badge as they are alternatively upgraded
		if (!item.levelKnown || item instanceof Artifact) {
			return;
		}

		if (item instanceof MeleeWeapon){
			validateDuelistUnlock();
		}
		
		Badge badge = null;
		if (!local.contains( Badge.ITEM_LEVEL_1 ) && item.level() >= 3) {
			badge = Badge.ITEM_LEVEL_1;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_2 ) && item.level() >= 6) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_2;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_3 ) && item.level() >= 9) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_3;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_4 ) && item.level() >= 12) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_4;
			local.add( badge );
		}
		if (!local.contains( Badge.ITEM_LEVEL_5 ) && item.level() >= 15) {
			if (badge != null) unlock(badge);
			badge = Badge.ITEM_LEVEL_5;
			local.add( badge );
		}
		
		displayBadge( badge );
	}
	
	public static void validateAllBagsBought( Item bag ) {
		
		Badge badge = null;
		if (bag instanceof VelvetPouch) {
			badge = Badge.BAG_BOUGHT_VELVET_POUCH;
		} else if (bag instanceof ScrollHolder) {
			badge = Badge.BAG_BOUGHT_SCROLL_HOLDER;
		} else if (bag instanceof PotionBandolier) {
			badge = Badge.BAG_BOUGHT_POTION_BANDOLIER;
		} else if (bag instanceof MagicalHolster) {
			badge = Badge.BAG_BOUGHT_MAGICAL_HOLSTER;
		}
		
		if (badge != null) {
			
			local.add( badge );
			
			if (!local.contains( Badge.ALL_BAGS_BOUGHT ) &&
				local.contains( Badge.BAG_BOUGHT_VELVET_POUCH ) &&
				local.contains( Badge.BAG_BOUGHT_SCROLL_HOLDER ) &&
				local.contains( Badge.BAG_BOUGHT_POTION_BANDOLIER ) &&
				local.contains( Badge.BAG_BOUGHT_MAGICAL_HOLSTER )) {
						
					badge = Badge.ALL_BAGS_BOUGHT;
					local.add( badge );
					displayBadge( badge );
			}
		}
	}

	//several badges all tie into catalog completion
	public static void validateCatalogBadges(){

		int totalSeen = 0;
		int totalThings = 0;

		for (Catalog cat : Catalog.values()){
			totalSeen += cat.totalSeen();
			totalThings += cat.totalItems();
		}

		for (Bestiary cat : Bestiary.values()){
			totalSeen += cat.totalSeen();
			totalThings += cat.totalEntities();
		}

		for (Document doc : Document.values()){
			if (!doc.isLoreDoc()) {
				for (String page : doc.pageNames()){
					if (doc.isPageFound(page)) totalSeen++;
					totalThings++;
				}
			}
		}

		//overall unlock badges
		Badge badge = null;
		if (totalSeen >= 40) {
			badge = Badge.RESEARCHER_1;
		}
		if (totalSeen >= 80) {
			unlock(badge);
			badge = Badge.RESEARCHER_2;
		}
		if (totalSeen >= 160) {
			unlock(badge);
			badge = Badge.RESEARCHER_3;
		}
		if (totalSeen >= 320) {
			unlock(badge);
			badge = Badge.RESEARCHER_4;
		}
		if (totalSeen == totalThings) {
			unlock(badge);
			badge = Badge.RESEARCHER_5;
		}
		displayBadge( badge );

		//specific task badges

		boolean qualified = true;
		for (Catalog cat : Catalog.equipmentCatalogs) {
			if (cat != Catalog.ENCHANTMENTS && cat != Catalog.GLYPHS) {
				if (cat.totalSeen() == 0) {
					qualified = false;
					break;
				}
			}
		}
		if (qualified) {
			displayBadge(Badge.CATALOG_ONE_EQUIPMENT);
		}

		//doesn't actually use catalogs, but triggers at the same time effectively
		if (!local.contains(Badge.CATALOG_POTIONS_SCROLLS)
				&& Potion.allKnown() && Scroll.allKnown()
				&& Dungeon.hero != null && Dungeon.hero.isAlive()){
			local.add(Badge.CATALOG_POTIONS_SCROLLS);
			displayBadge(Badge.CATALOG_POTIONS_SCROLLS);
		}

		if (Bestiary.RARE.totalSeen() >= 10){
			displayBadge(Badge.ALL_RARE_ENEMIES);
		}

		if (Document.HALLS_KING.isPageRead(Document.KING_ATTRITION)){
			displayBadge(Badge.RODNEY);
		}

	}
	
	public static void validateDeathFromFire() {
		Badge badge = Badge.DEATH_FROM_FIRE;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromPoison() {
		Badge badge = Badge.DEATH_FROM_POISON;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromGas() {
		Badge badge = Badge.DEATH_FROM_GAS;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}
	
	public static void validateDeathFromHunger() {
		Badge badge = Badge.DEATH_FROM_HUNGER;
		local.add( badge );
		displayBadge( badge );
		
		validateDeathFromAll();
	}

	public static void validateDeathFromFalling() {
		Badge badge = Badge.DEATH_FROM_FALLING;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromEnemyMagic() {
		Badge badge = Badge.DEATH_FROM_ENEMY_MAGIC;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}
	
	public static void validateDeathFromFriendlyMagic() {
		Badge badge = Badge.DEATH_FROM_FRIENDLY_MAGIC;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromSacrifice() {
		Badge badge = Badge.DEATH_FROM_SACRIFICE;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

	public static void validateDeathFromGrimOrDisintTrap() {
		Badge badge = Badge.DEATH_FROM_GRIM_TRAP;
		local.add( badge );
		displayBadge( badge );

		validateDeathFromAll();
	}

    public static void validateDeathFromOutOfTime() {
        Badge badge = Badge.OUT_OF_TIME;
        local.add( badge );
        displayBadge( badge );
    }
	
	private static void validateDeathFromAll() {
		if (isUnlocked( Badge.DEATH_FROM_FIRE ) &&
				isUnlocked( Badge.DEATH_FROM_POISON ) &&
				isUnlocked( Badge.DEATH_FROM_GAS ) &&
				isUnlocked( Badge.DEATH_FROM_HUNGER) &&
				isUnlocked( Badge.DEATH_FROM_FALLING) &&
				isUnlocked( Badge.DEATH_FROM_ENEMY_MAGIC) &&
				isUnlocked( Badge.DEATH_FROM_FRIENDLY_MAGIC) &&
				isUnlocked( Badge.DEATH_FROM_SACRIFICE) &&
				isUnlocked( Badge.DEATH_FROM_GRIM_TRAP) &&
                isUnlocked( Badge.OUT_OF_TIME )) {

			Badge badge = Badge.DEATH_FROM_ALL;
			if (!isUnlocked( badge )) {
				displayBadge( badge );
			}
		}
	}

    public static void validateAllHeroClassesUnlocked() {

        if (!isUnlocked(Badge.UNLOCK_ALL_HERO_CLASSES)
                && isUnlocked(Badge.UNLOCK_WARRIOR_A)
                && isUnlocked(Badge.UNLOCK_MAGE_A)
                && isUnlocked(Badge.UNLOCK_ROGUE_A)
                && isUnlocked(Badge.UNLOCK_HUNTRESS_A)
                && isUnlocked(Badge.UNLOCK_DUELIST_A)
                && isUnlocked(Badge.UNLOCK_CLERIC_A)) {

            displayBadge(Badge.UNLOCK_ALL_HERO_CLASSES);
        }
    }

    public static void validateShopBadges( int goldSpent ) {
        // goldspend isnt the most useful right now, but for future badges it might be
        Badge badge = null;

        if (!local.contains( Badge.WHAT_DID_IT_COST_EVERYTHING ) && Dungeon.isSinActive(Sins.GREED)) {
            if (Dungeon.hero.buff(Greed.class) != null && Dungeon.hero.buff(Greed.class).getShopMultiplier() >= 5) {
                badge = Badge.WHAT_DID_IT_COST_EVERYTHING;
                local.add(badge);
            }
        }

        displayBadge( badge );
    }

	private static LinkedHashMap<HeroClass, Badge> firstBossClassBadges = new LinkedHashMap<>();
	static {
		firstBossClassBadges.put(HeroClass.WARRIOR, Badge.BOSS_SLAIN_1_WARRIOR);
		firstBossClassBadges.put(HeroClass.MAGE, Badge.BOSS_SLAIN_1_MAGE);
		firstBossClassBadges.put(HeroClass.ROGUE, Badge.BOSS_SLAIN_1_ROGUE);
		firstBossClassBadges.put(HeroClass.HUNTRESS, Badge.BOSS_SLAIN_1_HUNTRESS);
		firstBossClassBadges.put(HeroClass.DUELIST, Badge.BOSS_SLAIN_1_DUELIST);
		firstBossClassBadges.put(HeroClass.CLERIC, Badge.BOSS_SLAIN_1_CLERIC);
	}

	private static LinkedHashMap<HeroClass, Badge> victoryClassBadges = new LinkedHashMap<>();
	static {
		victoryClassBadges.put(HeroClass.WARRIOR, Badge.VICTORY_WARRIOR);
		victoryClassBadges.put(HeroClass.MAGE, Badge.VICTORY_MAGE);
		victoryClassBadges.put(HeroClass.ROGUE, Badge.VICTORY_ROGUE);
		victoryClassBadges.put(HeroClass.HUNTRESS, Badge.VICTORY_HUNTRESS);
		victoryClassBadges.put(HeroClass.DUELIST, Badge.VICTORY_DUELIST);
		victoryClassBadges.put(HeroClass.CLERIC, Badge.VICTORY_CLERIC);
	}

	private static LinkedHashMap<HeroSubClass, Badge> thirdBossSubclassBadges = new LinkedHashMap<>();
	static {
		thirdBossSubclassBadges.put(HeroSubClass.BERSERKER, Badge.BOSS_SLAIN_3_BERSERKER);
		thirdBossSubclassBadges.put(HeroSubClass.GLADIATOR, Badge.BOSS_SLAIN_3_GLADIATOR);
		thirdBossSubclassBadges.put(HeroSubClass.BATTLEMAGE, Badge.BOSS_SLAIN_3_BATTLEMAGE);
		thirdBossSubclassBadges.put(HeroSubClass.WARLOCK, Badge.BOSS_SLAIN_3_WARLOCK);
		thirdBossSubclassBadges.put(HeroSubClass.ASSASSIN, Badge.BOSS_SLAIN_3_ASSASSIN);
		thirdBossSubclassBadges.put(HeroSubClass.FREERUNNER, Badge.BOSS_SLAIN_3_FREERUNNER);
		thirdBossSubclassBadges.put(HeroSubClass.SNIPER, Badge.BOSS_SLAIN_3_SNIPER);
		thirdBossSubclassBadges.put(HeroSubClass.WARDEN, Badge.BOSS_SLAIN_3_WARDEN);
		thirdBossSubclassBadges.put(HeroSubClass.CHAMPION, Badge.BOSS_SLAIN_3_CHAMPION);
		thirdBossSubclassBadges.put(HeroSubClass.MONK, Badge.BOSS_SLAIN_3_MONK);
		thirdBossSubclassBadges.put(HeroSubClass.PRIEST, Badge.BOSS_SLAIN_3_PRIEST);
		thirdBossSubclassBadges.put(HeroSubClass.PALADIN, Badge.BOSS_SLAIN_3_PALADIN);
	}


    public static void validateDepth(){
        Badge badge = null;
        if (Dungeon.depth == 11 && !isUnlocked(Badge.UNLOCK_ROGUE_A)) {
            badge = Badge.UNLOCK_ROGUE_A;
            local.add(badge);
        }
        if (Statistics.thrownAttacks == 0 && Dungeon.depth == 11 && !isUnlocked(Badge.UNLOCK_HUNTRESS_A)){
            badge = Badge.UNLOCK_HUNTRESS_A;
            local.add(badge);
        }
        if (Dungeon.depth == 16 && !isUnlocked(Badge.UNLOCK_CLERIC_A)){
            badge = Badge.UNLOCK_CLERIC_A;
            local.add(badge);
        }

        validateAllHeroClassesUnlocked(); // safety... mostly
        if (Dungeon.depth == 16 && Statistics.upgradesUsed == 0 && !isUnlocked(Badge.NO_UPGRADE_BOSS3)){
            badge = Badge.NO_UPGRADE_BOSS3;
            local.add( badge );
        }

        if (badge != null) {
            displayBadge(badge);
        }
    }
	
	public static void validateBossSlain() {
		Badge badge = null;
		switch (Dungeon.depth) {
		case 5:
			badge = Badge.BOSS_SLAIN_1;
			break;
		case 10:
			badge = Badge.BOSS_SLAIN_2;
			break;
		case 15:
			badge = Badge.BOSS_SLAIN_3;
			break;
		case 20:
			badge = Badge.BOSS_SLAIN_4;
			break;
		}
		
		if (badge != null) {
			local.add( badge );
			displayBadge( badge );
			
			if (badge == Badge.BOSS_SLAIN_1) {
				badge = firstBossClassBadges.get(Dungeon.hero.heroClass);
				if (badge == null) return;
				local.add( badge );
				unlock(badge);

				boolean allUnlocked = true;
				for (Badge b : firstBossClassBadges.values()){
					if (!isUnlocked(b)){
						allUnlocked = false;
						break;
					}
				}
				if (allUnlocked) {
					
					badge = Badge.BOSS_SLAIN_1_ALL_CLASSES;
					if (!isUnlocked( badge )) {
						displayBadge( badge );
					}
				}
			} else if (badge == Badge.BOSS_SLAIN_3) {

				badge = thirdBossSubclassBadges.get(Dungeon.hero.subClass);
				if (badge == null) return;
				local.add( badge );
				unlock(badge);

				boolean allUnlocked = true;
				for (Badge b : thirdBossSubclassBadges.values()){
					if (!isUnlocked(b)){
						allUnlocked = false;
						break;
					}
				}
				if (allUnlocked) {
					badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES;
					if (!isUnlocked( badge )) {
						displayBadge( badge );
					}
				}

			}

			if (Statistics.qualifiedForBossRemainsBadge && Dungeon.hero.belongings.getItem(RemainsItem.class) != null){
				badge = Badge.BOSS_SLAIN_REMAINS;
				local.add( badge );
				displayBadge( badge );
			}

		}
	}

	public static void validateBossChallengeCompleted(){
		Badge badge = null;
		switch (Dungeon.depth) {
			case 5:
				badge = Badge.BOSS_CHALLENGE_1;
				break;
			case 10:
				badge = Badge.BOSS_CHALLENGE_2;
				break;
			case 15:
				badge = Badge.BOSS_CHALLENGE_3;
				break;
			case 20:
				badge = Badge.BOSS_CHALLENGE_4;
				break;
			case 25:
				badge = Badge.BOSS_CHALLENGE_5;
				break;
		}

		if (badge != null) {
			local.add(badge);
			displayBadge(badge);
		}
	}

    public static void validateDamageInOneHit( int dmg ) {
        Badge badge = null;
        if (dmg >= 15 && !isUnlocked(Badge.STRONG)) {
            badge = Badge.STRONG;
            local.add(badge);
        }
        if (dmg >= 35 && !isUnlocked(Badge.MIGHTY)) {
            badge = Badge.MIGHTY;
            local.add(badge);
        }
        if (dmg >= 60 && !isUnlocked(Badge.POWERFUL)) {
            badge = Badge.POWERFUL;
            local.add(badge);
        }
        if (dmg >= 99 && !isUnlocked(Badge.OBLIVION)) {
            badge = Badge.OBLIVION;
            local.add(badge);
        }

        if (badge != null) {
            displayBadge(badge);
        }
    }
	
	public static void validateMastery() {
		
		Badge badge = null;
		switch (Dungeon.hero.heroClass) {
            case PEASANT:
                break;
			case WARRIOR:
				badge = Badge.MASTERY_WARRIOR;
				break;
			case MAGE:
				badge = Badge.MASTERY_MAGE;
				break;
			case ROGUE:
				badge = Badge.MASTERY_ROGUE;
				break;
			case HUNTRESS:
				badge = Badge.MASTERY_HUNTRESS;
				break;
			case DUELIST:
				badge = Badge.MASTERY_DUELIST;
				break;
			case CLERIC:
				badge = Badge.MASTERY_CLERIC;
				break;
		}

        if (badge != null){
		unlock(badge);
        }
	}

	public static void validateRatmogrify(){
		unlock(Badge.FOUND_RATMOGRIFY);
	}
	
	public static void validateMageUnlock(){
		if (Statistics.upgradesUsed >= 1 && !isUnlocked(Badge.UNLOCK_MAGE)){
			displayBadge( Badge.UNLOCK_MAGE );
		}
        if (Statistics.upgradesUsed >= 14 && !isUnlocked(Badge.UNLOCK_MAGE_A)){
            displayBadge( Badge.UNLOCK_MAGE_A );
        }

        validateAllHeroClassesUnlocked();
	}
	
	public static void validateRogueUnlock(){
		if (Statistics.sneakAttacks >= 10 && !isUnlocked(Badge.UNLOCK_ROGUE)){
			displayBadge( Badge.UNLOCK_ROGUE );
		}

        validateAllHeroClassesUnlocked();
	}
	
	public static void validateHuntressUnlock(){
		if (Statistics.thrownAttacks >= 10 && !isUnlocked(Badge.UNLOCK_HUNTRESS)){
			displayBadge( Badge.UNLOCK_HUNTRESS );
		}

        validateAllHeroClassesUnlocked();
	}

	public static void validateDuelistUnlock(){
		if (!isUnlocked(Badge.UNLOCK_DUELIST) && Dungeon.hero != null
				&& Dungeon.hero.belongings.weapon instanceof MeleeWeapon
				&& ((MeleeWeapon) Dungeon.hero.belongings.weapon).tier >= 2
				&& ((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()){

			if (Dungeon.hero.belongings.weapon.isIdentified() &&
					((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()) {
				displayBadge(Badge.UNLOCK_DUELIST);

			} else if (!Dungeon.hero.belongings.weapon.isIdentified() &&
					((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq(0) <= Dungeon.hero.STR()){
				displayBadge(Badge.UNLOCK_DUELIST);
			}
		}

        if (!isUnlocked(Badge.UNLOCK_DUELIST_A) && Dungeon.hero != null
                && Dungeon.hero.belongings.weapon instanceof MeleeWeapon
                && ((MeleeWeapon) Dungeon.hero.belongings.weapon).tier >= 4
                && ((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()){

            if (Dungeon.hero.belongings.weapon.isIdentified() &&
                    ((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq() <= Dungeon.hero.STR()) {
                displayBadge(Badge.UNLOCK_DUELIST_A);

            } else if (!Dungeon.hero.belongings.weapon.isIdentified() &&
                    ((MeleeWeapon) Dungeon.hero.belongings.weapon).STRReq(0) <= Dungeon.hero.STR()){
                displayBadge(Badge.UNLOCK_DUELIST_A);
            }
        }

        validateAllHeroClassesUnlocked();
	}

	public static void validateClericUnlock(){
		if (!isUnlocked(Badge.UNLOCK_CLERIC)){
			displayBadge( Badge.UNLOCK_CLERIC );
		}

        validateAllHeroClassesUnlocked();
	}
	
	public static void validateMasteryCombo( int n ) {
		if (!local.contains( Badge.MASTERY_COMBO ) && n == 10) {
			Badge badge = Badge.MASTERY_COMBO;
			local.add( badge );
			displayBadge( badge );
		}
        if (!local.contains( Badge.MASTERY_COMBO_2 ) && n == 100) {
            Badge badge = Badge.MASTERY_COMBO_2;
            local.add( badge );
            displayBadge( badge );
        }
	}

    public static void validateVictory() {

        Badge badge = Badge.VICTORY;
        local.add( badge );
        displayBadge( badge );

        //technically player can also not spend talent points if they want for some reason
        if (Statistics.qualifiedForRandomVictoryBadge
                && Dungeon.hero.subClass != null
                && Dungeon.hero.armorAbility != null){
            badge = Badge.VICTORY_RANDOM;
            local.add( badge );
            displayBadge( badge );
        }


        badge = victoryClassBadges.get(Dungeon.hero.heroClass);
        if (badge == null) return;
        local.add( badge );
        unlock(badge);

        boolean allUnlocked = true;
        for (Badge b : victoryClassBadges.values()){
            if (!isUnlocked(b)){
                allUnlocked = false;
                break;
            }
        }
        if (allUnlocked){
            badge = Badge.VICTORY_ALL_CLASSES;
            displayBadge( badge );
        }
    }

	public static void validateTakingTheMick(Object cause){
		if ((cause == Dungeon.hero || cause instanceof Explosive.ExplosiveCurseBomb)
				&& Dungeon.hero.belongings.attackingWeapon() instanceof Pickaxe
				&& Dungeon.hero.belongings.attackingWeapon().level() >= 20){
			local.add( Badge.TAKING_THE_MICK );
			displayBadge(Badge.TAKING_THE_MICK);
		}
	}

	public static void validateNoKilling() {
		if (!local.contains( Badge.NO_MONSTERS_SLAIN ) && Statistics.completedWithNoKilling) {
			Badge badge = Badge.NO_MONSTERS_SLAIN;
			local.add( badge );
			displayBadge( badge );
			Statistics.completedWithNoKilling = false;
		}
	}
	
	public static void validateGrimWeapon() {
		if (!local.contains( Badge.GRIM_WEAPON )) {
			Badge badge = Badge.GRIM_WEAPON;
			local.add( badge );
			displayBadge( badge );
		}
	}

	public static void validateManyBuffs(){
		if (!local.contains( Badge.MANY_BUFFS )) {
			Badge badge = Badge.MANY_BUFFS;
			local.add( badge );
			displayBadge( badge );
		}
	}
	
	public static void validateGamesPlayed() {
		Badge badge = null;
        if (Rankings.INSTANCE.totalNumber >= 5) {
            badge = Badge.UNLOCK_WARRIOR_A;
            validateAllHeroClassesUnlocked();
        }
        unlock(badge);
        validateAllHeroClassesUnlocked();  // now it's safe to check

		if (Rankings.INSTANCE.totalNumber >= 10 || Rankings.INSTANCE.wonNumber >= 1) {
			badge = Badge.GAMES_PLAYED_1;
		}
		if (Rankings.INSTANCE.totalNumber >= 25 || Rankings.INSTANCE.wonNumber >= 3) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_2;
		}
		if (Rankings.INSTANCE.totalNumber >= 50 || Rankings.INSTANCE.wonNumber >= 5) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_3;
		}
		if (Rankings.INSTANCE.totalNumber >= 200 || Rankings.INSTANCE.wonNumber >= 10) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_4;
		}
		if (Rankings.INSTANCE.totalNumber >= 1000 || Rankings.INSTANCE.wonNumber >= 25) {
			unlock(badge);
			badge = Badge.GAMES_PLAYED_5;
		}
		
		displayBadge( badge );
	}

	public static void validateHighScore( int score ){
		Badge badge = null;
		if (score >= 5000) {
			badge = Badge.HIGH_SCORE_1;
			local.add( badge );
		}
		if (score >= 25_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_2;
			local.add( badge );
		}
		if (score >= 100_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_3;
			local.add( badge );
		}
		if (score >= 250_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_4;
			local.add( badge );
		}
		if (score >= 1_000_000) {
			unlock(badge);
			badge = Badge.HIGH_SCORE_5;
			local.add( badge );
		}

		displayBadge( badge );
	}
	
	public static void validateHappyEnd() {
		local.add( Badge.HAPPY_END );
		displayBadge( Badge.HAPPY_END );

		if( Dungeon.hero.belongings.getItem(RemainsItem.class) != null ){
			local.add( Badge.HAPPY_END_REMAINS );
			displayBadge( Badge.HAPPY_END_REMAINS );
		}

		if (AscensionChallenge.qualifiedForPacifist()) {
			local.add( Badge.PACIFIST_ASCENT );
			displayBadge( Badge.PACIFIST_ASCENT );
		}
	}

    public static void validateChampionSins( int sins){
        if (sins == 0) return;

        /*
        Badge badge = null;
        if (sins >= 1) {
            //unlock(badge);
            //badge = Badge.VICTORY_WITH_1_SIN;
        }
        if (sins >= 4) {
            //unlock(badge);
           // badge = Badge.VICTORY_WITH_4_SIN;
        }

        local.add(badge);
        displayBadge( badge );
        */
    }

	public static void validateChampion( int challenges, int sins ) {
        Badge badge = null;

        if (Dungeon.hero.heroClass == HeroClass.PEASANT && challenges >= 3){
            badge = Badge.AGAINST_ALL_ODDS;
            unlock(badge);
        }

		if (challenges == 0) return;

		if (challenges >= 1) {
			badge = Badge.CHAMPION_1;
		}
		if (challenges >= 3){
			unlock(badge);
			badge = Badge.CHAMPION_2;
		}
		if (challenges >= 6){
			unlock(badge);
			badge = Badge.CHAMPION_3;
		}

        int total = Challenges.totalChallenges();

        if (challenges >= 8) {
            unlock(badge);
            badge = Badge.VICTORY_WITH_8_CHALLENGES;
        }
        if (challenges >= 10) {
            unlock(badge);
            badge = Badge.VICTORY_WITH_10_CHALLENGES;
        }
        if (challenges >= total) {
            unlock(badge);
            badge = Badge.VICTORY_WITH_ALL_CHALLENGES;
        }

        if (Dungeon.hero.heroClass == HeroClass.PEASANT && challenges >= 3){
            unlock(badge);
            badge = Badge.AGAINST_EVERYTHING_AND_MORE;
        }
		local.add(badge);
		displayBadge( badge );
	}

    public static void validateModProgression (String S){
        // honestly, can't be bothered to use correct types so ima just use strings
        // we have a small order to this though, you can only get up to one each turn
        Badge badge = null;


        if (Objects.equals(S, "dm151") && !isUnlocked(Badge.KILL_TITLE_ENEMY)){
            badge = Badge.KILL_TITLE_ENEMY;
            local.add(badge);
        }
        if (Objects.equals(S, "dm151_sewer") && !isUnlocked(Badge.AWKWARD_DM151_UNBALANCE)){
            badge = Badge.AWKWARD_DM151_UNBALANCE;
            local.add(badge);
        }
        if (Objects.equals(S, "icecaves") && !isUnlocked(Badge.ICE_CAVE_TOURIST)){
            badge = Badge.ICE_CAVE_TOURIST;
            local.add(badge);
        }
        if (Objects.equals(S, "dejavu") && !isUnlocked(Badge.THIEF_DEJA_VU)){
            badge = Badge.THIEF_DEJA_VU;
            local.add(badge);
        }


        if (badge != null) {
            displayBadge(badge);
        }
    }
	
	private static void displayBadge( Badge badge ) {

        if (badgesDisabled) return;

		if (badge == null || (badge.type != BadgeType.JOURNAL && !Dungeon.customSeedText.isEmpty())) {
			return;
		}
		
		if (isUnlocked( badge )) {
			
			if (badge.type == BadgeType.LOCAL) {
				GLog.h( Messages.get(Badges.class, "endorsed", badge.title()) );
				GLog.newLine();
			}
			
		} else {
			
			unlock(badge);
			
			GLog.h( Messages.get(Badges.class, "new", badge.title() + " (" + badge.desc() + ")") );
			GLog.newLine();
			PixelScene.showBadge( badge );
		}
	}
	
	public static boolean isUnlocked( Badge badge ) {
		return global.contains( badge );
	}
	
	public static HashSet<Badge> allUnlocked(){
		loadGlobal();
		return new HashSet<>(global);
	}
	
	public static void disown( Badge badge ) {
		loadGlobal();
		global.remove( badge );
		saveNeeded = true;
	}
	
	public static void unlock( Badge badge ){
        if (badgesDisabled) return;

		if (!isUnlocked(badge) && (badge.type == BadgeType.JOURNAL || Dungeon.customSeedText.isEmpty())){
			global.add( badge );
			saveNeeded = true;
		}
	}

	public static List<Badge> filterReplacedBadges( boolean global ) {

		ArrayList<Badge> badges = new ArrayList<>(global ? Badges.global : Badges.local);

		Iterator<Badge> iterator = badges.iterator();
        while (iterator.hasNext()) {
            Badge badge = iterator.next();

            // hide secret badges unless unlocked
            if (badge.type == BadgeType.SECRET && !isUnlocked(badge)) {
                iterator.remove();
            }

            if ((!global && badge.type != BadgeType.LOCAL) || badge.type == BadgeType.HIDDEN) {
                iterator.remove();
            }
        }

		Collections.sort(badges);

		return filterReplacedBadges(badges);

	}

	//only show the highest unlocked and the lowest locked
	private static final Badge[][] tierBadgeReplacements = new Badge[][]{
			{Badge.MONSTERS_SLAIN_1, Badge.MONSTERS_SLAIN_2, Badge.MONSTERS_SLAIN_3, Badge.MONSTERS_SLAIN_4, Badge.MONSTERS_SLAIN_5},
			{Badge.GOLD_COLLECTED_1, Badge.GOLD_COLLECTED_2, Badge.GOLD_COLLECTED_3, Badge.GOLD_COLLECTED_4, Badge.GOLD_COLLECTED_5},
			{Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4, Badge.ITEM_LEVEL_5},
			{Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4, Badge.LEVEL_REACHED_5},
			{Badge.STRENGTH_ATTAINED_1, Badge.STRENGTH_ATTAINED_2, Badge.STRENGTH_ATTAINED_3, Badge.STRENGTH_ATTAINED_4, Badge.STRENGTH_ATTAINED_5, Badge.STRENGTH_ATTAINED_6},
			{Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4, Badge.FOOD_EATEN_5},
			{Badge.ITEMS_CRAFTED_1, Badge.ITEMS_CRAFTED_2, Badge.ITEMS_CRAFTED_3, Badge.ITEMS_CRAFTED_4, Badge.ITEMS_CRAFTED_5},
			{Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4, Badge.VICTORY, Badge.HAPPY_END},
			{Badge.RESEARCHER_1, Badge.RESEARCHER_2, Badge.RESEARCHER_3, Badge.RESEARCHER_4, Badge.RESEARCHER_5},
			{Badge.HIGH_SCORE_1, Badge.HIGH_SCORE_2, Badge.HIGH_SCORE_3, Badge.HIGH_SCORE_4, Badge.HIGH_SCORE_5},
			{Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4, Badge.GAMES_PLAYED_5},
			{Badge.CHAMPION_1, Badge.CHAMPION_2, Badge.CHAMPION_3, Badge.VICTORY_WITH_8_CHALLENGES, Badge.VICTORY_WITH_10_CHALLENGES, Badge.VICTORY_WITH_ALL_CHALLENGES},
            {Badge.STRONG, Badge.MIGHTY, Badge.POWERFUL, Badge.OBLIVION },
            {Badge.AGAINST_ALL_ODDS, Badge.AGAINST_EVERYTHING_AND_MORE},

            {Badge.MASTERY_COMBO, Badge.MASTERY_COMBO_2},
            {Badge.BOSS_SLAIN_REMAINS, Badge.HAPPY_END_REMAINS},
            {Badge.NO_MONSTERS_SLAIN, Badge.PACIFIST_ASCENT},

            // mod progression line
            // technically contains spoilers, but that's alright
            // cooouullddd make the badges hidden until rewarded, since you're super likely to get them, but nah
            {Badge.KILL_TITLE_ENEMY, Badge.ICE_CAVE_TOURIST, Badge.THIEF_DEJA_VU},


            // Legacy vs new hero class unlocks:
            // Warrior doesnt have a legacy unlock
            {Badge.UNLOCK_MAGE,    Badge.UNLOCK_MAGE_A},
            {Badge.UNLOCK_ROGUE,   Badge.UNLOCK_ROGUE_A},
            {Badge.UNLOCK_HUNTRESS, Badge.UNLOCK_HUNTRESS_A},
            {Badge.UNLOCK_DUELIST, Badge.UNLOCK_DUELIST_A},
            {Badge.UNLOCK_CLERIC,  Badge.UNLOCK_CLERIC_A}
	};

	//don't show the later badge if the earlier one isn't unlocked
	//we aren't too aggressive with this, mainly just want to prevent boss spoilers,
	// and all diamond tier badges must have a gold/plat prerequisite
	private static final Badge[][] prerequisiteBadges = new Badge[][]{
			{Badge.BOSS_SLAIN_1, Badge.BOSS_CHALLENGE_1},
			{Badge.BOSS_SLAIN_2, Badge.BOSS_CHALLENGE_2},
			{Badge.BOSS_SLAIN_3, Badge.BOSS_CHALLENGE_3},
			{Badge.BOSS_SLAIN_4, Badge.BOSS_CHALLENGE_4},
			{Badge.VICTORY,      Badge.BOSS_CHALLENGE_5},
			{Badge.HAPPY_END,    Badge.PACIFIST_ASCENT},
			{Badge.VICTORY,      Badge.TAKING_THE_MICK},
            {Badge.VICTORY,      Badge.VICTORY_RANDOM}, // Added this myself since Evan didn't, could have been a wrong merge? idk


            // evan might not be strict, i sure as hell am
            {Badge.BOSS_CHALLENGE_3, Badge.NO_UPGRADE_BOSS3},
            {Badge.VICTORY, Badge.TO_HELL_AND_BACK},
            {Badge.VICTORY, Badge.HAPPY_END},
            {Badge.HAPPY_END, Badge.HAPPY_END_REMAINS},
            {Badge.VICTORY, Badge.AGAINST_ALL_ODDS}
	};

	//If the summary badge is unlocked, don't show the component badges
	private static final Badge[][] summaryBadgeReplacements = new Badge[][]{
			{Badge.DEATH_FROM_FIRE, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_GAS, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_HUNGER, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_POISON, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_FALLING, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_ENEMY_MAGIC, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_FRIENDLY_MAGIC, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_SACRIFICE, Badge.DEATH_FROM_ALL},
			{Badge.DEATH_FROM_GRIM_TRAP, Badge.DEATH_FROM_ALL},
            {Badge.OUT_OF_TIME, Badge.DEATH_FROM_ALL},

			{Badge.ALL_WEAPONS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_ARMOR_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_WANDS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_RINGS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_ARTIFACTS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_POTIONS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},
			{Badge.ALL_SCROLLS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED},


            {Badge.ALL_BAGS_BOUGHT, Badge.GEARED_UP},
            {Badge.ALL_ITEMS_IDENTIFIED, Badge.GEARED_UP},


            {Badge.UNLOCK_WARRIOR_A, Badge.UNLOCK_ALL_HERO_CLASSES},
            {Badge.UNLOCK_MAGE_A, Badge.UNLOCK_ALL_HERO_CLASSES},
            {Badge.UNLOCK_ROGUE_A, Badge.UNLOCK_ALL_HERO_CLASSES},
            {Badge.UNLOCK_HUNTRESS_A, Badge.UNLOCK_ALL_HERO_CLASSES},
            {Badge.UNLOCK_DUELIST_A, Badge.UNLOCK_ALL_HERO_CLASSES},
            {Badge.UNLOCK_CLERIC_A, Badge.UNLOCK_ALL_HERO_CLASSES}
	};
	
	public static List<Badge> filterReplacedBadges( List<Badge> badges ) {

		for (Badge[] tierReplace : tierBadgeReplacements){
			leaveBest( badges, tierReplace );
		}

		for (Badge[] metaReplace : summaryBadgeReplacements){
			leaveBest( badges, metaReplace );
		}
		
		return badges;
	}
	
	private static void leaveBest( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static List<Badge> filterBadgesWithoutPrerequisites(List<Badges.Badge> badges ) {

		for (Badge[] prereqReplace : prerequisiteBadges){
			leaveWorst( badges, prereqReplace );
		}

		for (Badge[] tierReplace : tierBadgeReplacements){
			leaveWorst( badges, tierReplace );
		}

		Collections.sort( badges );

		return badges;
	}

	private static void leaveWorst( Collection<Badge> list, Badge...badges ) {
		for (int i=0; i < badges.length; i++) {
			if (list.contains( badges[i])) {
				for (int j=i+1; j < badges.length; j++) {
					list.remove( badges[j] );
				}
				break;
			}
		}
	}

	public static Collection<Badge> addReplacedBadges(Collection<Badges.Badge> badges ) {

		for (Badge[] tierReplace : tierBadgeReplacements){
			addLower( badges, tierReplace );
		}

		for (Badge[] metaReplace : summaryBadgeReplacements){
			addLower( badges, metaReplace );
		}

		return badges;
	}

	private static void addLower( Collection<Badge> list, Badge...badges ) {
		for (int i=badges.length-1; i > 0; i--) {
			if (list.contains( badges[i])) {
				for (int j=0; j < i; j++) {
					list.add( badges[j] );
				}
				break;
			}
		}
	}

	//used for badges with completion progress that would otherwise be hard to track
	public static String showCompletionProgress( Badge badge ){
		if (isUnlocked(badge)) return null;

		String result = "\n";

		if (badge == Badge.BOSS_SLAIN_1_ALL_CLASSES){
			for (HeroClass cls : HeroClass.values()){
                if (cls != HeroClass.PEASANT) {
                    result += "\n";
                    if (isUnlocked(firstBossClassBadges.get(cls)))
                        result += "_" + Messages.titleCase(cls.title()) + "_";
                    else result += Messages.titleCase(cls.title());
                }
			}

			return result;

		} else if (badge == Badge.VICTORY_ALL_CLASSES) {

			for (HeroClass cls : HeroClass.values()){
                if (cls != HeroClass.PEASANT) {
                    result += "\n";
                    if (isUnlocked(victoryClassBadges.get(cls))) result += "_" + Messages.titleCase(cls.title()) + "_";
                    else result += Messages.titleCase(cls.title());
                }
			}

			return result;

		} else if (badge == Badge.BOSS_SLAIN_3_ALL_SUBCLASSES){

			for (HeroSubClass cls : HeroSubClass.values()){
				if (cls == HeroSubClass.NONE) continue;
				result += "\n";
				if (isUnlocked(thirdBossSubclassBadges.get(cls)))   result += "_" + Messages.titleCase(cls.title()) + "_";
				else                                                result += Messages.titleCase(cls.title()) ;
			}

			return result;
		}

		return null;
	}
}
