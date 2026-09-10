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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollBaby;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class Mod_Changes {

    public static void addAllChanges(ArrayList<ChangeInfo> changeInfos){

        // Notes section at top
        ChangeInfo changes = new ChangeInfo("Notes", true, "");
        changes.hardlight(0xCCCCCC);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Important Information",
                "This mod does _NOT_ auto-update or have visible in-game news.\n\n" +
                        "Download the latest github release for new updates and changelogs. you can find a _link_ to the github on _the about page!_\n\n\n" +
                        "This does NOT have a separate release for each version. Often one release (update) contains 2 or more new versions\n\n" +
                        "This mod _tries_ to stay up to date with vanilla SPD"));


        changes = new ChangeInfo("v1.401.0 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(new ImpSprite()), "v1.404.0: Shattered 4.0.0",
                "_Changes:_\n" +
                        "_1._ updated to Shattered Pixel Dungeon 4.0.0\n" +
                        "_2._ fixed a bug where ghouls would gain increasingly more hp each revive, even beyond max hp\n" +
                        "_3._ fixed keys of the same depth being usable on other branches (i think?)\n" +
                        "_4._ fixed some bugs caused by merging with 4.0.0\n" +
                        "_5._ updated some sprites to work with some of 4.0.0 new systems\n" +

                        "\n_notes:_\n" +
                        "_1._ i am fairly certain that many bugs are still present\n" +
                        "_2._ this version will not have a release\n"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.BADGES), "v1.401.5: crashing hotfix",
                "_Changes:_\n" +
                        "_1._ fixed a very common crash case where the game would crash directly after a game over\n" +
                        "_2._ fixed badge 'against all odds', no longer requiring enabled challenges to be achieved (oops)\n" +
                        "_3._ fixed badges displayed when achieved for the first time displaying the wrong sprite (was only a visual bug)\n" +
                        "_4._ badge 'unlocked warrior' no longer gets achieved every game over after already having unlocked it\n" +
                        "_5._ fixed badges added in 1.401.0 showing up too early\n" +
                        "_6._ updated the hero unlock badges to better reflect their rarity\n"
        ));

        changes.addButton(new ChangeButton(new Image(new SnatcherSprite()), "v1.401.0: fixes, small new content, and more fixes",
                "_Changes:_\n" +
                        "_1._ Returned all languages, as requested by someone\n" +
                        "_2._ renamed 'darkness katana' to 'dark katana', name unchanged in code\n" +
                        "_3._ badge 'deja vu' can now only be awarded in the proper region instead of any region, no longer spoils in badge description.\n" +
                        "_4._ actually added loot to the gold chest in the thieves guild vault (oops), and increased overall loot reward\n" +
                        "_5._ badges that validate based on depth no longer get rewarded if you are in the wrong branch\n" +
                        "_6._ added 3 new badges related to the new branching paths from 1.400.0\n" +
                        "_7._ sane thief's room now always spawns a iron key on the floor\n" +
                        "_8._ burglar and snatcher only attack once per turn now, snatcher chance to steal equipment reduced\n" +
                        "_9._ fixed ice caves transitions being hooked up wrong in one case\n" +
                        "_10._ ice caves can no longer generate pitfall traps"
        ));


        // ===== reworks (v1400) =====
        changes = new ChangeInfo("The Rework (v1.400.0)", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARMOR_WARRIOR), "Items",
                "_Changes:_\n" +
                        "_1._ level vs magic level reducing each others effect on armor reduced (30% -> 18%)\n" +
                        "_2._ all armor tiers now have a higher base max DR\n" +
                        "_3._ removed the global -7% damage nerf to all melee weapons, it's instead applied to missile weapons now and increased to -10%\n" +
                        "_4._ the tier based nerfs for weapons have been significantly reduced, melee's gain a 5% more damage boost overall\n" +
                        "_5._ increased energy cost for creating the wand of elements through alchemy (21 -> 30)\n" +
                        "_6._ glyph of antimagic has a much better chance to proc, but less effect now\n" +
                        "_7._ glyph of thorns now has less chance to proc\n" +
                        "_8._ all armor's scale slightly less based on upgrade level"
                ));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_LAGUZ), "Other items",
                "_Changes:_\n" +
                        "_1._ potion of levitation now applies levitation with more duration (default -> default*2)\n" +
                        "_2._ natural gold spawn quantity nerf reduced (0.65 -> 0.8), and more base gold in general\n" +
                        "_3._ (randomized) gold quantity now gains a very small boost based on depth, scaling faster the deeper you go up to a x3 multiplier\n" +
                        "_4._ potion of mastery (potion of strength exotic) can no longer be obtained\n" +
                        "_5._ eye of newt (trinket) base mind vision tiles increased (1 -> 2), reduces vision less with each upgrade (12.5% -> 15%), first level vision reduction reduced (12.5% -> 10%)\n",

                        "\n_Rings:_\n" +
                        "_1._ nerfed ring of accuracy effect (15% -> 10%)\n" +
                        "_2._ nerfed ring of arcana effect (12.5% -> 10%)\n" +
                        "_3._ buffed ring of evasion effect (1% -> 10%)\n" +
                        "_4._ nerfed ring of furor effect (6% -> 5%)\n" +
                        "_5._ nerfed ring of haste effect (8% -> 5%)\n" +
                        "_6._ ring of might now has a base strength bonus of 1 instead of 0.5, making the first level more effective\n" +
                        "_7._ ring of sharpshooting now has a base level increase of 1 instead of 0.5\n" +
                        "_8._ buffed ring of tenacity effect (6% -> 10%)\n" +
                        "_9._ ring of wealth has been restored to vanilla behavior and stats and has been completely renerfed (less nerfed than before)\n"

                ));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.TENGU, 0, 0, 14, 16), "Bosses",
                "\n_Goo:_\n" +
                        "_1._ reduced goopling hp (9:7 -> 5) and no longer increases if the challenge _stronger bosses_ is active\n" +
                        "_2._ goo hp reduced (160:120 -> 140:120)\n" +
                        "_3._ goopling spawn cooldown increased if challenge _stronger bosses_ is active (8 -> 11) to compensate for double goopling spawns\n" +
                        "_4._ initial goopling spawn cooldown increased (2 -> 5)\n",

                         "\n_Tengu:_\n" +
                        "_1._ changed tengu's dialog a bit\n" +
                        "_2._ tengu's hp reduced (350:300 -> 285:225)\n" +
                        "_3._ tengu's base damage reduced (6,14 -> 5,12)\n" +
                        "_4._ tengu in phase 2 starts using traps earlier\n" +
                        "_5._ in challenge _stronger bosses_ tengu still uses the fire trap alongside using any other trap, but at a 40% instead of 100%\n" +
                        "_6._ electricity trap is now rarer, unchanged in challenge _stronger bosses_\n" +
                        "_7._ tengu's last stand now lasts shorter in challenge _stronger bosses_ (25 -> 18)\n"
                ));

        changes.addButton(new ChangeButton(new Image(new RatSprite()), "Enemies",
                        "\n_Sewers:_\n" +
                        "_1._ snakes can drop seeds again at a lower chance (vanilla 25% -> 10%)\n" +
                        "_2._ gnolls award xp for longer (max lvl 4 -> 8)\n" +
                        "_3._ crabs award more xp (2 -> 3), award xp for longer (max lvl 5 -> 9), speed increased (1 -> 1.5), reduced DR (0,4 -> 0,3), reduced damage (3,8 -> 3,7)\n" +
                        "_4._ swarm hp increased (30 -> 50), damage reduced (3,7 -> 2,5)\n" +
                        "_5._ slime (sewers) damage reduced (4,6 -> 2,5)\n" +
                        "_6._ fetid rat (sewers quest) reduced attack proc damage (3,7 -> 2,5), reduced hp (24 -> 20)\n" +
                        "_7._ gnoll trickster (sewer quest) xp reduced (7 -> 5), hp reduced (24 -> 18)\n" +
                        "_8._ great crab (sewer quest) hp reduced (32 -> 26)\n",

                        "\n_Prison:_\n" +
                        "_1._ slime (prison) damage reduced (5,8 -> 3,6)\n" +
                        "_2._ skeleton damage increased (3,9 -> 3,11), bone explosion damage reduced (6,12 -> 5,9)\n" +
                        "_3._ thief awards more xp (1 -> 4), speed in caves increased (0.9 -> 1), increased DR (0,3 -> 1,4), less chance to steal equipped equipment (40% -> 32%)\n" +
                        "_4._ dm100 (and dm151) damage reduced (3,6 -> 2,6), to increase incentive to use magic upgrades on armor\n" +
                        "_5._ guard hp reduced (60 -> 40), defensive increased (1 -> 5), damage increased (5,10 -> 5,14), bash dmg multiplier reduced (x1.25 -> x1.1), lowered bash cooldown (12 -> 10), increased DR (0,4 -> 2,7)\n" +
                        "_6._ necromancer was fixed, and will now use it's abilities correctly and more often\n",

                        "\n_Caves:_\n" +
                        "_1._ fetid rat (caves) now has a max level cap instead of giving xp forever\n" +
                        "_2._ gnoll trickster (caves) now has a max level cap instead of giving xp forever, loot chance reduced (18% -> 8%)\n" +
                        "_3._ elder gnoll hp increased (16 -> 30), reworked behavior ai to be better at fleeing and can now wander around like any other mob\n" +
                                "_4._ bat lifesteal a bit reduced, damage reduced (7,18 -> 5,18), increased hp (30 -> 38)\n" +
                                "_5._ brute enrage shield increased (33% -> 50%)\n" +
                                "_6._ gnoll baby hp increased (8 -> 14), defensive skill increased (24 -> 32), damage increased (1,5 -> 2,7)\n" +
                                "_7._ dm200 reduced loot chance (20% -> 12%), reduced gas exhaust cooldown (30 -> 20)\n" +

                        "\n_Ice Caves:_\n" +
                        "_1._ dm100f DR reduced (0,6 -> 0,4), chill proc chance reduced (100% -> 33%), procs for 2 turns instead of 3 now\n" +
                        "_2._ ice snake chill proc chance reduced (100% -> 33%), procs for 2 turns instead of 3 now\n" +
                        "_3._ frozen swarm hp increased (105 -> 150)\n",

                                "\n_Ruins:_\n" + // yep, we're doing this NOW
                                "_1._ ghoul now gets up with more hp (10% -> 33%), but decreases the more times its been downed, take shorter to respawn (base 5 -> 4), reduced defensive (20 -> 16), reduced hp (45 -> 36)\n" +
                                "_2._ bandit blindness and cripple duration upon stealing reduced (5 -> 3), hp reduced (45 -> 36)\n" +
                                "_3._ elementals... actually they remain unchanged surprisingly\n" +
                                "_4._ warlock now has the spirit necromancer as it's rare variant, hp reduced (70 -> 60), chance to degrade reduced (50% -> 25%), damage reduced (12,18 -> 6,13)),will now always cast magic projectile even if adjacent\n" +
                                "_5._ monk cooldown to be focussed reduced (6,7 -> 4,6), hp reduced (70 -> 55), reduced loot drop chance (8.3% -> 5%)\n" +
                                "_6._ golem self teleport cooldown reduced (30 -> 15), are now twice as likely to shoot a teleport bolt, damage reduced (25,30 -> 12,20), DR reduced (0,12 -> 0,5), hp reduced (120 -> 75)\n" +
                                "_7._ spirit necromancer added\n",

                        "\n_Rare:_\n" +
                        "_1._ albino rat hp reduced (30 -> 15) and awards xp (0 -> 2), bleed inflicted is now equal to half of damage dealt\n" +
                        "_2._ hermit crab speed increased (0.9 -> 1)\n" +
                        "_3._ dm151 now seeks out the hero\n" +


                        "\n_Global:_\n" +
                        "_1._ piranha speed reduced (3 -> 2), now deals +1 minimum damage\n" +
                        "_2._ mimic's no longer reward xp\n" +
                        "_3._ statue defensive skill reduced (depth*2 -> depth), hp reduced (depth*7 -> depth*6)"
                ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "Bug and error fixes",
                "_Fixes:_\n" +
                        "_1._ tengu's defeat message should now correctly show\n" +
                        "_2._ stone of enchantment's sprite is no longer blurry (how the hell did it get blurry in the first place???)\n" +
                        "_3._ ring of sharpshooting now correctly applies durability bonus, before granting more than it should have and than is stated\n" +
                        "_4._ rings of sharpshooting and might direct stats (missile level and additional strength) are no longer deceiving and fixed, being 100% accurate now\n" +
                        "_5._ dungeon seeds now actually work, always having the same dungeon/item generation for the same seed. this was broken and hidden away before\n",
                        "_6._ blacksmith now has a greeting for the peasant and a in-code default, should no longer crash\n" +
                        "_7._ challenge _I hate myself_ now no longer identifies scrolls. previously, it mistakenly did\n" +
                        "_8._ fixed a handful of out-of-date or weird text\n" +
                        "_9._ FINALLY fixed scroll of magic upgrade after so long... the fix was strangely easy too\n" +
                        "_10._ sin of gluttony now affects passive health regen like it should. before it didn't\n" +
                        "_11._ fixed a case where the game would crash when trying to pick up the amulet with at least one or more sins active at once\n"

        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY_LAND), "Visuals and UI",
                "_Changes:_\n" +
                        "_1._ small changes to ui like 'boss defeated'\n" +
                        "_2._ talents tab is no longer visible for the peasant, including during rankings\n" +
                        "_3._ removed all languages apart from english\n" +
                        "_4._ updated some messages to be up to date with some changes (ex: POS only saying it gives 1 STR, etc)\n" +
                        "_5._ scroll of magic upgrade and debuff _consuming greed_ now have their own distinct icons\n" +
                        "_6._ removed the 'unused enemies' tab in the enemy bestiary. They still exist, but will be properly added when their time comes\n" +
                        "_7._ updated some guidebook pages to be somewhat more up to date\n" +
                        "_8._ readded the option to input a generation seed\n" +
                        "_9._ changed some sprites like the elder gnoll sprite and many others slightly\n",

                        "_10._ moved ice caves enemies into the new 'branching' sort\n" +
                        "_11._ added the new misc settings tab, which will have some settings that don't fit in the other tabs\n" +
                        "_12._ added a new setting that speeds up enemy combat related animations up to 4 times their speed. Please use this, it took me like 6+ hours to properly make this.\n" +
                        "_13._ if the music ever changes in-game, it's now announced in the game log with the name of the track and it's source or creator\n" +
                        "_14._ added a new tab in the alchemy guide for equipment crafting\n" +
                        "_15._ changes scene now has a better split between SPD and ME changes\n" +
                        "_16._ shortened version name and such, and the code wants you to use dots and yadayadayade... just deal with it\n" +
                        "_17._ a brand new proper title screen change! it sure as heck doesn't look perfect, and i sure as heck won't fix it!"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "Challenges and sins",
                "_Challenges:_\n" +
                        "_1._ removed challenges _back to origins_, _guns blazing_, and _wonky step_, mostly for being unfair\n" +
                        "_2._ challenge _I hate myself_ now disables the creation of notes for items and depths\n" +

                        "\n_Sins:_\n" +
                        "_1._ nerfed sloth turns gain per floor, sloth damage scales slower\n" +
                        "_2._ nerfed sin of desire, no longer being guaranteed +1, and chance to upgrade is no longer a set 33% up to +4. instead now has a increased chance to be upgraded, with the chance still increasing each upgrade\n" +
                        "_3._ gluttony has a better description and heals for more (10% +2 -> 12% +5)\n",

                        "\n_Challenge Consuming Greed:_\n" +
                        "_1._ now gains a buff in it's difficulty if you have the sin of greed active\n" +
                        "_2._ disabled in this version until later notice. is currently in a stasis of whenever it should stay or not\n"

        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS), "Mechanics and other changes",
                "_Changes:_\n" +
                        "_1._ the peasant now starts with cloth armor as the only hero, but lacks strength to use it\n" +
                        "_2._ the warrior needs less enemies defeated in one game to be unlocked (75 -> 60), and now has a optional secondary method to be unlocked\n" +
                        "_3._ levitation buff default duration is now the same as other buffs (30 -> 20)\n" +
                        "_4._ rare enemies spawn chance reduced, becoming twice as rare (1/20 -> 1/40)\n" +
                        "_5._ alchemy guide pages now drop in a more customized order and some now have a minimum region to appear in\n"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.STAIRS), "Generation and traps",
                "_Changes:_\n" +
                        "_1._ ice caves floor now has different odds for it's traps, hopefully offering a bit more variety\n" +
                        "_2._ ice caves floor now has its generation adjusted and generates a bit smaller on floor 14\n" +
                        "_3._ ice caves now has a optional path stretching down from floor 14 to floor 18\n" +
                        "_4._ fixed issues with exits/entrances overlapping (including for blacksmith) and some generation generating the wrong types of level\n" +
                        "_5._ catalysts floors to spawn on changed (1-3 -> 2-4)\n" +
                        "_6._ ice caves no longer generates essential items (scrolls of upgrade, potions of strength, etc)\n" +
                        "_7._ branches should no longer generate alchemy rooms\n" +
                        "_8._ ice caves item generation was adjusted\n"

        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CATALOG), "Enemy and item spawning",
                "_Enemies:_\n" +
                        "_1._ floor 6 and 7 can no longer spawn snake\n" +
                        "_2._ reduced slime spawn chance on floor 8\n" +
                        "_3._ floor 18 spawns 1 less monk, and can now spawn spirit necromancer\n" +
                        "_4._ floor 19 spawns 1 less golem, and can now spawn spirit necromancer\n" +

                        "\n_Items:_\n" +
                        "_1._ scroll of identify is a little more common\n" +
                        "_2._ scroll of remove curse is a little more common and has a chance to spawn in more cases\n" +
                        "_3._ the following rings are now rarer compared to other rings: wealth, might, haste, and furor. Energy is made rarer too, but less so\n" +
                        "_4._ the following artifacts are now rarer compared to other artifacts: chalice of blood and ethereal chains\n" +
                        "_5._ the following artifacts are now a bit more common compared to other artifacts: talisman of foresight and unstable spellbook\n" +
                        "_6._ a potion of strength will now always spawn on floor 1\n",

                        "\n_Item tier distribution:_\n" +
                        "_short version:_ tier distribution is much more manageable\n" +
                        "_1._ sewers: much more likely to spawn T2 or T3 items, can still not spawn T4 or T5 items\n" +
                        "_2._ prison: less likely to spawn T1 items, and more likely for T3, T4 slightly increased as well\n" +
                        "_3._ caves: slightly less likely to spawn T2 items, T4 and T5 a bit more likely, T3 much more likely\n" +
                        "_4._ city: less likely to spawn T2 items, and more likely for T3 and T5\n" +
                        "_5._ halls: can no longer spawn T2 items, significantly more likely for T3, T4, and T5\n"

        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.BADGES), "Badges",
                "_Changes:_\n" +
                        "_1._ fixed badge 'against everything and more' being rewarded at 1+ challenges instead of 3+\n" +
                        "_2._ badge 'against everything and more' is now forever hidden unless achieved\n" +
                        "_3._ removed the victory with X sins badges\n" +
                        "_4._ stacked all victory with X challenges badges\n" +
                        "_5._ fixed some badges\n" +
                        "_6._ more badges stack now (including some vanilla badges)\n" +
                        "_7._ added some new badges for collective stacking\n" +
                        "_8._ added a new badge\n" +
                        "_9._ added 3 new badges that are almost directly linked to progression and this mod's exclusive stuff\n" +
                        "_10._ added a handful more badges that are all secret (not visible unless achieved), they have a '[secret badge]' in their description. How many can you find?\n"
                ));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.ARROW_SIGN), "brand new content",
                "_Additions:_\n" +
                        "_1._ replaced potion of mastery with the potion of vitality, which increases max hp\n" +
                        "_2._ added exotic variant to scroll of magic upgrade, the scroll of pure magic\n" +
                        "_3._ added 9 new weapons, only obtainable through the blacksmith. some of these are references to other games!\n" +
                        // while you're here anyway: here are the exact references
                        // shadow = not based on a reference
                        // purge = not based on a reference
                        // emerald shield = not (directly) based on a reference
                        // midas sword = not (directly) based on a reference
                        // firecracker = based on "experienced pixel dungeon"'s firing snapper
                        // stormbringer = not (directly) based on a reference, but inspired by thor's hammer
                        // bloodletter = reference to the mobile game "backpack heroes", where a weapon with the same name, nearly same behavior, and (almost) same look exists
                        // amethyst piercer = not (directly) based on a reference, but vaguely inspired by the minecraft mace
                        // arrow sign = reference to the roblox game "super cube cavern", where in it's shops the "helpful arrow" can be bought and used. in that game it's a unusual weapon, and in here it's too.
                        "_4._ the blacksmith has a new option for you to spend favor on: extract. this will destroy the item, but give you half of their upgrades as scrolls\n" +
                        "_5._ the blacksmith has a new option for you to spend favor on: liquidize. this will destroy the item, but give you some favor in return, can only be done once per run. max favor (3000 -> 3800)\n" +
                        "_6._ added a new curse for both weapons and armor: the curse of binding. this curse has no effect on it's own, but can't be removed using scrolls of upgrade\n",
                        "_7._ the ice caves now has a additional optional path. a new enemy spawns in said path, the ice golem. at the end of the path, a new unique rewards awaits you\n" +
                        "_8._ reworked wand of elements to be much less random and added a battlemage effect to it\n" +
                        "_9._ added wand of explosion, and wand of fireball. fireball is made in alchemy while explosion can be found in the dungeon\n" +
                        "_10._ reworked augments to be more versatile and added icy core\n" +
                        "_11._ added throwing chains, a weaker consumable version of the ethereal chains, purchasable in shops starting from the caves\n" +
                        "_12._ you love thieves? no? too bad! added the thieves guild in the prisons! not gonna explain the details here because have fun!\n"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Notes",
                "_Overall:_\n" +
                        "_1._ this rework has mostly focussed on rebalancing the difficulty\n" +
                        "_2._ the secondary focus, and future focus will be on adding new content or changing existing content, and less about making the game harder\n" +
                        "_3._ compared to vanilla, the difficulty will likely stay a bit higher, but still at a point where it's bearable\n",

                        "\n_Clean ups and fixes:_\n" +
                        "_1._ there were a handful of messes than needed a cleanup, mainly challenges, badges, and the title screen\n" +
                        "_2._ the title screen was removed of it's random rainbow effect, replaced by a more fitting color that from now on will be used more frequently as the style color\n" +
                        "_3._ badges were (mostly) cleaned up, being much more stackable to leave some space for other badges\n" +
                        "_4._ as for language, i removed them all except english. reason being that existing languages would continue using old information and was just very inconvenient to maintain\n",

                        "\n_New content:_\n" +
                                "_1._ a handful of content was added, a huge amount for this mod in one version to be exact\n" +
                                "_2._ this new content mostly delves into some new exploration and rewards for them\n" +
                                "_3._ as per usual: will need some rebalancing in the future, nothing starts off perfect\n" +

                        "\n_Balancing:_\n" +
                                "_1._ maybe you've noticed if you played since the start (or read every changelog), but balancing is hard\n" +
                                "_2._ i'm not quite satisfied yet with the third, fourth, and fifth region balance, and that will likely be tackled soon\n" +
                                "_3._ another thing i will look at is the overall power climbing of the hero throughout a run, feeling a little too powerful later on\n"

                ));

        changes.addButton( new ChangeButton(Icons.get(Icons.SHPX), "Dev and debug changes",
                "_NOTE:_ these changes _ONLY_ matter to those using a debug version of the game or working with the code\n" +
                "\n_Changes:_\n" +
                        "_1._ running debug now automatically unlocks the full catalog and bestiary (upon starting a run that is). note that this will unlock it on your savefile and not just for debug\n" +
                        "_2._ removed duplicate entries for cheat mode setting for debug. it can now only be found in the new misc settings\n" + // in the future, likely make a special misc settings tab for cases like these
                        "_3._ some weird or unclean code i have created now has some comments explaining why it is as it is or a TODO on how to fix it. in the future i will likely clean them up myself\n" +
                        "_4._ added some more debug settings to make testing a handful easier. cheat mode will now also start with many of the most useful consumables in their inventory.\n"
                ));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null), "I'm not reading all that",
                "The short list of important changes:\n" +
                        "_1._ overall difficulty reworked, aiming towards a better balance\n" +
                        "_2._ blacksmith has new rewards waiting for you\n" +
                        "_3._ ice caves now has a new optional path expansion\n" +
                        "_4._ prison has something new for you to uncover\n" +
                        "_5._ rings 'rebalanced', some new items, and handful of item changes\n" +
                        "_6._ (currently changed) bosses reworked to be a bit easier and more consistent\n" +
                        "_7._ new setting\n" +
                        "_8._ handful of fixes, clean ups, and new badges\n"
        ));



        // ===== v1300 and up =====
        changes = new ChangeInfo("v1300 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(new DM200Sprite()), "v1333: Final changes before rework",
                "_NOTE:_\n" +
                        "I've had a revision of this mod, one I wish to turn to. For that reason, any future versions will be towards turning the difficulty more fairly and easier. _This is the end of the 'difficult' mod_, the future will no longer revolve around making this a harder SPD." +
                        "_\n\nChanges:_\n" +
                        "1. DM300 hp increased (300:400 -> 400:500)\n" +
                        "2. DM300 has a new attack, allowing them to move a tile and break walls. Has a chance to happen if the hero is unreachable\n" +
                        "3. DM300 now shoots one of four random gasses. dangerous gasses become more common the lower their hp is\n" +
                        "4. updated floor 15 tile sprites to match the ice palette\n" +
                        "5. updated to vanilla 3.3.8\n"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS), "v1324: Return of balances",
                "_Changes:_\n" +
                        "1. Updated version code to be a bit more readable\n" +
                        "2. the sin of sloth now makes you start with more time, and you gain more time overall\n" +
                        "3. the sin of gluttony now heals you a bit when you eat food. Still makes you hungry faster, but less so now. Bulky effect lasts shorter (30 -> 20)\n" +
                        "4. returned legacy greed effect, but as a challenge instead of a sin, and nerfed effects\n" +
                        "5. challenge 'I hate myself' now also bans scrolls of divination (which you could obtain in some scenario's)\n" +
                        "6. the sin of greed now is a bit softer with it's timed gold multipliers. You're still expected to collect at a fast rate though\n" +
                        "\n_Notes:_\n" +
                        "I know this whole thing went on a straight hiatus for over 2 months, but don't think i forgot about it!"

        ));

        changes.addButton( new ChangeButton(new Image(new WandmakerSprite()), "v1320: Small QOL rebalances + new wandmaker quest",
                "_Changes:_\n" +
                        "1. ALL accuracy is increased by +15%, for both hero's, mobs, and anything in between. evasion unchanged\n" +
                        "2. elder gnoll hp reduced (25 -> 16)\n" +
                        "3. if the elder gnoll fails to spawn, a skeleton key is randomly spawned on the floor as a fallback (this will only occur in super rare cases)\n" +
                        "4. reduced wraith hp (5 -> 3)\n" +
                        "\n_Wandmaker Quest Changes:_\n" +
                        "1. newborn fire elemental hp reduced (90 -> 70), ranged cooldown increased (1,4 -> 2,5)\n" +
                        "2. the corpse dust quest is back! while holding the item, wraiths will spawn much more frequently compared to vanilla\n" +
                        "3. wraith spawned by the corpse dust have random hp compared to normal wraiths (either 1, 2, or 3 hp)\n"
        ));


        changes.addButton(new ChangeButton(new Image(new ElderGnollSprite()), "v1318: Respect your elders and updated goo",
                "_Changes:_\n" +
                        "1. gooplings should now spawn in the 'wandering' state instead of spawning asleep\n" +
                        "2. goopling hp reduced (12:9 -> 9:7), damage increased (2,4:5 -> 2,4:6), inflicted ooze lasts 33% shorter\n" +
                        "3. goo should no longer instantly spawn a goopling when it awakes\n" +
                        "4. goo now loses a bit of hp each time it spawns a goopling\n" +
                        "5. increased gnoll baby defensive skill (16 -> 24), increased hp (5 -> 8)\n" +
                        "6. worn key no longer naturally spawns on floor 14, instead being replaced by the new enemy, the elder gnoll, who now drops the worn key\n" +
                        "7. note: the elder gnoll can spawn on ANY valid tile with no further conditions. this means it can spawn next to the entrance, on the floor like normal, or behind a locked door!\n" +
                        "8. note 2: the game will LITERALLY error out if it fails to spawn the elder gnoll (due to him not spawning makes beating the game impossible). a fallback might be added in the future!\n" +
                        "_Fixes:_\n" +
                        "1. removed a duplicate entry that caused frost trap spawnrate to nearly double its intended spawnrate in the ice caves\n" +
                        "2. (hopefully) fixed a weird interaction when you ascend from the ice caves back to the main floor 14 without ever unlocking the floor 14 exit door\n" +
                        "3. fixed the ice caves tilesheet having some colors modified that shouldn't be quite modified (mainly just the locks being off-color)\n"
                        ));


        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY_PORT), "v1315: Better Android",
                "_Changes:_\n" +
                        "1. fixed a ancient old crash on the android version where exiting the settings tab while on the languages sub-tab and reopening settings would completely crash the game\n" +
                        "2. added a second instance of the cheat mode setting on the mobile interface settings tab. this info only matters to those that debug this game.\n" +
                        "_Notes:_\n" +
                        "1. yes i am aware that some of the changelogs are... going very much offscreen on mobile. might fix this in the future by making scrollboxes, but don't expect this to be fixed anytime soon at all\n"));

        changes.addButton(new ChangeButton(new ItemSprite(new WandOfElements()), "v1314: Smoothing edges",
                "_Changes:_\n" +
                        "1. default language is now english, this somehow broke at one point\n" +
                        "2. overhauled this page of changes. it's no longer one big list of text but instead nicely sorted buttons\n" +
                        "3. fixed the badge 'against EVERYTHING and more' showing up too early\n" +
                        "4. badge 'out of time' now counts towards the 'yet another sad death' badges\n" +
                        "5. added 2 new badges related to winning a game with X sins enabled\n" +
                        "6. all items have a reduced chance to be cursed for the first 3 floors of the dungeon. chance starts halved, increasing to full chance by floor 4\n" +
                        "7. added the wand of elements, which can be obtained through alchemy (check the alchemy guide, the recipe is in there)\n" +
                        "8. changed a few sprites (kinda minimal, you probably wouldn't even notice)\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.SHPX), "v1312: Version Upgrade",
                "_Changes:_\n" +
                        "1. changed the icon color for when you have a sin enabled, and for when you have sin and challenge enabled.\n" +
                        "2. changed some badge sprites a little\n" +
                        "3. being well-fed now increases passive hp regen, being hungry now decreases passive hp regen\n" +
                        "4. added some new unused OST for a future update\n" +
                        "5. updated the vanilla SPD version from 3.3.0 to 3.3.5\n" +
                        "6. updated some mod to match the newly updated code\n" +
                        "7. attempt to fix a few bugs and errors that instantly showed up after updating the version\n" +
                        "8. let's hope there are no random crashes or bugs!\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.SIN_COLOR), "v1309: Sinning Update",
                "_NEW CONTENT:_\n" +
                        "1. added the new sins modifier, with 7 sins to start\n" +
                        "2. sins will give a powerful buff, while also adding a terrible debuff along with it\n" +
                        "3. added a handful of new buffs and debuffs, most of which have a neutral effect\n" +
                        "\n_BOSSES:_\n" +
                        "1. tengu's last stand now lasts longer (15:10 -> 25:15)\n" +
                        "\n_ENEMIES:_\n" +
                        "1. gnoll shaman's ranged proc chance increased (50% -> 75%)\n" +
                        "2. frozen swarm now only loses half of the clone's hp when cloning\n" +
                        "\n_SHOPS:_\n" +
                        "1. skeleton key no longer naturally spawns, instead only being obtainable through the caves shop\n" +
                        "2. wonderous resin no longer naturally spawns, instead only being obtainable through the prisons shop\n" +
                        "\n_BADGES:_\n" +
                        "1. badge 'against everything and more' required challenges reduced (5 -> 3)\n" +
                        "2. added 2 new badges related to sins\n" +
                        "3. added 4 new badges related to damage in a single hit\n" +
                        "4. solved some badge clutter by making them progressive/stack\n" +
                        "\n_UI STUFF:_\n" +
                        "1. rankings now show sins if the run had any enabled\n" +
                        "2. 'about' button on the title screen now has a new icon... _:)_\n" +
                        "3. the option to play on a seeded run is removed, as i somehow managed to break seeded runs, and it'll be a while before i ever fix that\n" +
                        "4. daily runs MIGHT be broken too... but i'm uncertain about that one so it stays for now\n" +
                        "5. removed the 'special' section in the bestiary and added it's enemies to the other existing categories\n" +
                        "6. added the 'special unused' section in the bestiary, where a duplicate entry of enemies that can't be found in-game yet are added\n" +
                        "\n_FIXES:_\n" +
                        "1. fixed a mistake in the code that caused the hero to earn less gold (you earn 15% more gold now)\n" +
                        "2. fishing rod now actually uses the gold quantity formula, though earns 20% less gold for balancing\n" +
                        "3. completely redid scroll of magic upgrade for the 1000th time to fix a bug\n" +
                        "4. whenever a game ends, it will now always go to ranks. (i removed edge case if statements that sometimes broke and invalidated your game for no reason)\n" +
                        "5. sins are now correctly displayed and saved in ranks (not quite a bug, since i introduced sins this version, BUT MAN DID I BREAK MY MIND OVER THIS)\n" +
                        "6. fixed the game crashing when you obtain the amulet with at least one challenge enabled\n" +
                        "7. fixed the order priority for the newly added challenge badges\n" +
                        "8. hopefully fixed the issue where the frozen swarm would spawn a normal swarm when hit\n" +
                        "\n_OTHER:_\n" +
                        "1. centralized the spawning level, curse chance, and enchanted chance for items\n" +
                        "2. challenge 'i hate myself' now applies to thrown weapons and wands as well\n" +
                        "3. curse chance for thrown weapons and wands increased by +10%\n"));

        changes.addButton(new ChangeButton(new Image(new GnollBabySprite()), "v1305: Direct Annoyance",
                "_Changes:_\n" +
                        "1. added the gnoll baby, which can spawn in the caves\n" +
                        "2. yeah... that's all...\n" +
                        "3. you probably have no idea how long it took me to properly make that gnoll baby\n" +
                        "4. oh yeah, i also reverted all the stuff i changed for testing tengu reasons from the last version\n"));

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.TENGU, 0, 0, 14, 16), "v1303: After the Storm",
                "_FIXES:_\n" +
                        "1. lots of small fixes not worth listing... trust me bro\n" +
                        "\n_BOSSES:_\n" +
                        "1. goo hp increased (125:100 -> 160:120), defensive increased (4 -> 6), goopling spawn cooldown reduced (10 -> 8)\n" +
                        "2. goo now spawns a additional goopling every time he spawns gooplings in the harder bosses challenge\n" +
                        "3. tengu hp increased (300:250 -> 350:300), damage incrased (6,12 -> 6,14), phase 2 abilities start earlier (75% -> 80%)\n" +
                        "4. tengu now has a last stand phase after beating him in phase 2 (he becomes invulnerable for 10 turns, and gets faster, but dies on his own after)\n" +
                        "\n_ITEMS:_\n" +
                        "1. spirit bow levels now count twice towards strength requirement\n" +
                        "2. spirit bow damage increased (base 1,3 -> 1,4), scaling increased (/10,/5 -> /7,/4)\n" +
                        "3. armor max DR scaling reduced a bit (too complex to state here)\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), "v1300: Stability Changes",
                "_CHANGES:_\n" +
                        "1. cheating mode is removed unless you are running the game from the actual source code\n" +
                        "2. cheating mode is now disabled by default if it's not visible in the options\n" +
                        "3. challenges are unlocked by default once more\n" +
                        "4. double checked if some changes were correctly implemented\n" +
                        "5. fairly certain that at least 90%, if not everything from 3.3.0 is now correctly implemented\n" +
                        "\n_BALANCES:_\n" +
                        "1. buffed gun.\n" +
                        "2. nerfed fishing rod loot chances a bit, made it easier for the rod to level up.\n" +
                        "3. armored brute hp increased (40 -> 55), reduced enrage shield (=hp -> =hp/2).\n" +
                        "4. brute enrage shield is now based on total hp instead of last hp before enrage\n" +
                        "5. bat damage increased (5,18 -> 7,18)\n" +
                        "6. gnoll trickster (caves only) speed reduced (1.2 -> 1), deals more damage, has less chance to miss, and needs a higher combo to inflict debuffs\n" +
                        "7. fetid rat (caves only) no longer unfairly reduces quest score, deals more damage, and paralysis debuff from stench gas lasts shorter\n" +
                        "8. fetid rat and gnoll trickster (both caves only) will no longer seek out the hero\n" +
                        "9. guaranteed armor spawn is now always on floor 3 instead of 50/50 floor 2 or 3\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.WARNING), "v1299: THE HORRIBLE MERGE",
                "_CHANGES:_\n" +
                        "1. a horrible and terrible attempt at trying to merge this mod with the new SPD 3.3.0\n" +
                        "2. i... tried my best ok :( (there's likely a ton of issues and changes missing because i merged it VERY weirdly)\n" +
                        "3. wonderous resin (trinket) can be obtained again (it remains the same still... FOR NOW)\n" +
                        "4. the game now (hopefully) saves the fact that you are cheating if you are per savefile\n"));

        // ===== v1250 and up =====
        changes = new ChangeInfo("v1250 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new Image(new DM100FSprite()), "v1297: Ice Caves",
                "_MECHANIC CHANGES_\n" +
                        "1. you are now significantly less likely to find a hidden trap or door without searching the deeper you get\n" +
                        "\n_FLOORS_\n" +
                        "F14. no longer directly connects to floor 15\n" +
                        "B2F14. added new ice cave floor, which connects to F14 and F15\n" +
                        "F15. now uses ice cave palette\n" +
                        "\n_ENEMIES_\n" +
                        "1. added the ice snake, DM100F, and frozen swarm\n" +
                        "2. spinner defensive reduced (17 -> 14), hp reduced (50 -> 40)\n" +
                        "3. armored brute armor drop tier reduced, has more shield when it dies (50% -> 100%)\n" +
                        "4. brute defensive reduced (15 -> 10), has more shield when it dies (50% -> 66%), " +
                        "unraged damage reduced (5,25 -> 5,18), raged damage reduced (15,40 -> 10,30)\n" +
                        "5. shaman debuff duration is shorter (100% -> 50%), but now have a chance to inflict debuff on melee attacks (20%)\n" +
                        "\n_ITEMS:_\n" +
                        "1. melee weapons now start with less strength requirement (oops)\n" +
                        "2. added the fishing rod, a new 'artifact' used to fish for loot\n" +
                        "3. gun.\n" +
                        "\n_SHOP:_\n" +
                        "1. first shop (floor 6) no longer sells tipped darts\n" +
                        "2. first shop (floor 6) now sells fishing rod\n" +
                        "3. second shop (floor 11) now sells 'gun.'\n" +
                        "\n_FIXES:_\n" +
                        "1. fixed DM151 wandering (hopefully)\n" +
                        "2. fixed scroll of magic upgrade reading animation triggering twice and too early\n" +
                        "3. hardcoded the scroll of magic upgrade to not show the armor weight increasing when in reality it doesn't increase when applied\n" +
                        "4. further fixed the scroll of magic upgrade in general\n" +
                        "\n_NOTES:_\n" +
                        "1. caves lacked a bit of that 'HMPF!', if you know what i mean\n" +
                        "2. hence the addition of the ice caves, one big floor with a bit less dangerous enemies\n" +
                        "3. the difficulty there becomes resource management, and finding the exit quick enough\n" +
                        "4. also, in my testing i REALLY hated the spinners in the ice caves, hence why they don't spawn there anymore _:)_\n" +
                        "\n_WHAT'S NEXT?:_\n" +
                        "1. changes to the caves boss (haven't quite gotten to him yet)\n" +
                        "2. changes to the blacksmith quest enemies (decided to not do this yet)\n" +
                        "3. rebalances to the new items and cave enemies if required\n" +
                        "4. potentially making the ice cave floor smaller\n" +
                        "5. addition of more custom enemies or new rooms in the generation\n" +
                        "\n_SNEAKPEEK?:_\n" +
                        "1. i plan to make the dwarven city more... interesting.\n" +
                        "2. and if you think you are finally done dealing with necromancers, thieves, and what not... think again\n"));

        changes.addButton(new ChangeButton(new ItemSprite(new ClothArmor()), "v1289: Less RNG",
                "_MECHANIC CHANGES:_\n" +
                        "1. at least one tier 1 armor now always spawns on floor 3, 50% on floor 2.\n" +
                        "2. let's hope this doesn't cause crashes!\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS), "v1288: General Rebalance",
                "_ITEMS:_\n" +
                        "1. reduced nerf to all melee weapons (-18% -> -7%)\n" +
                        "2. reduced tier nerf to all melee weapons\n" +
                        "3. armor STR req based on level scales slower\n" +
                        "4. weapon STR req now increases based on level\n" +
                        "\n_ENEMIES:_\n" +
                        "1. thieves are slower in the sewer (0.8 -> 0.75) and have less hp (32 -> 20), unchanged in prison\n" +
                        "2. fetid rat base hp reduced (30 -> 24)\n" +
                        "3. gnoll trickster speed reduced (1.2 -> 1), unchanged in caves\n" +
                        "4. crab damage reduced (4,8 -> 3,8)\n"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARMOR_PLATE), "v1285: Prison Rebalance",
                "_ITEMS:_\n" +
                        "1. overhauled armor physical DR scaling, higher tiers are now noticeably better\n" +
                        "2. upgrades on armor now get compoundingly better each upgrade\n" +
                        "3. armor now gains a higher strength requirement as you upgrade it\n" +
                        "\n_ENEMIES:_\n" +
                        "1. necromancer defensive reduced (14 -> 10). hp reduced (45 -> 40)\n" +
                        "2. guard bash damage multi reduced (x1.5 -> x1.25)\n" +
                        "3. skeleton damage reduced (3,11 -> 3,9), defensive reduced (16 -> 12)\n" +
                        "\n_FLOORS:_\n" +
                        "8. 1 less thief\n" +
                        "9. 1 less DM100\n" +
                        "\n_QUESTS:_\n" +
                        "1. wandmaker quest rewards cursed % reduced (50% -> 25%)\n" +
                        "2. newborn fire elemental damage reduced (8,15 -> 8,12)\n" +
                        "\n_NOTES:_\n" +
                        "1. yes, you observe correctly: I have YET to change floors 11 to 25... be patient\n"));

        changes.addButton( new ChangeButton(new Image(new WandmakerSprite()), "v1280: Item Rebalance",
                "_HERO:_\n" +
                        "1. peasant now starts with stones quickslotted... most insane change fr\n" +
                        "\n_ITEMS:_\n" +
                        "1. slightly rebalanced the nerf from previous version for the better\n" +
                        "2. ALL melee weapons are now 18% weaker despite tier\n" +
                        "3. tier nerf effect on melee weapons now also affects throwable weapons (missile type weapons)\n" +
                        "4. 1 less upgrade and magic upgrade scrolls spawn each region (4 upg and 2 M-upg now)\n" +
                        "5. wand of corrosion no longer naturally spawns\n" +
                        "6. scroll of recharge has less effect on wands (0.25 -> 0.19)\n" +
                        "7. wands take more base turns to recharge (10 -> 20)\n" +
                        "8. wands take longer to recharge (more complex math stuff, just trust me bro)\n" +
                        "9. all wands that deal direct damage deal 10% less damage, debuff excluded\n" +
                        "\n_GLYPHS:_\n" +
                        "1. glyph of potential has a rarer chance to proc now and gives less charge\n" +
                        "\n_UI:_\n" +
                        "1. upgrade window now correctly shows weapon stats\n" +
                        "\n_BADGES:_\n" +
                        "1. unlock mage badge now only requires 14 scrolls instead of 20\n" +
                        "2. strength badges requirements increased\n" +
                        "3. new badge for reaching 30 strength\n" +
                        "\n_BOSSES:_\n" +
                        "1. reduced goo hp (125/150 -> 100/125)\n" +
                        "2. reduced tengu hp (300/350 -> 250/300)\n" +
                        "\n_QUESTS:_\n" +
                        "1. wandmaker now always gives out the newborn fire elemental quest _(will likely be changed in future)_\n" +
                        "2. wandmaker now always guaranteed has either wand of blastwave or wand of corrosion as a quest reward\n" +
                        "3. newborn fire elemental is considered fiery again, as well as icy now\n" +
                        "4. newborn fire elemental ranged cooldown decreased (3,5) -> (1,4), takes less turns to charge attack (*3 -> *2)\n" +
                        "5. newborn fire elemental ranged attack fixed (hopefully)\n" +
                        "6. newborn fire elemental damage reduced (14,18 -> 8,15)\n"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.SWORD), "v1277: Tier Nerf",
                "_ITEMS:_\n" +
                        "1. nerfed weapons with a tier above 1, more so on items tiered above 2\n" +
                        "2. nerfed armor damage blocking scaling per level on higher tiers\n" +
                        "\n_NOTES:_\n" +
                        "1. this nerf was done because upgrade effect scaling was nuts.\n" +
                        "2. chances are that now they are too weak though and will need to be fixed later\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY_PORT), "v1274: UI Fix",
                "_UI:_\n" +
                        "1. fixed the github going offscreen in the about section on mobile\n" +
                        "2. fixed mod tab on the changes menu to be positioned better\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY_PORT), "v1271: Prettier UI, Nerfed Armor",
                "_UI:_\n" +
                        "1. about tab now includes a link to the github of the mod\n" +
                        "2. made the changelog of the mod in the changes tab _prettier_\n" +
                        "\n_ITEMS:_\n" +
                        "1. magic DR on armor is now equal to normal DR during the 'faith is my armor' challenge\n" +
                        "2. armor physical vs magical DR effect increased (0.2 -> 0.3)\n" +
                        "3. changed how armor magic DR scales per magic level for the worse\n" +
                        "4. stylus no longer stacks... why must i be evil like this?\n" +
                        "\n_ENEMIES:_\n" +
                        "1. swarm damage increased (2,5 -> 3,7)\n" +
                        "2. necromancers and spiritual necromancers can summon less minions (4 -> 3)\n" +
                        "3. great crab moves before skipping a move increased (3 -> 6), speed increased (1 -> 1.2)\n" +
                        "\n_SPAWNING FLOORS:_\n" +
                        "3. can now spawn crab again\n" +
                        "\n_BADGES:_\n" +
                        "1. any badges requiring all classes to beat X no longer require or list peasant\n" +
                        "2. added a new badge (David and Goliath)\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), "v1268: Side-Fundamental Changes",
                "_LANGUAGES:_\n" +
                        "1. default starting language is now english\n" +
                        "2. added a note in the language settings tab\n\n" +
                        "\n_SETTINGS:_\n" +
                        "1. removed connectivity tab\n" +
                        "2. all settings that used to be in the connectivity are now always turned off (false) in the code\n" +
                        "\n_FIXES:_\n" +
                        "1. challenges SHOULD REALLY start unlocked now no matter what FOR REAL THIS TIME\n" +
                        "\n_CHANGES UI MENU THING:_\n" +
                        "1. added all the changes from the txt changelog to here... if you are reading this in-game... _HI!_\n" +
                        "2. Rankings info tab challenges are now scrollable to account for more challenges\n" +
                        "3. Updated the about section\n" +
                        "\n_VANITY:_\n" +
                        "1. changed peasant rankings icon to a more fitting icon"));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.PEASANT, 2), "v1264: Everyone Starts as Nobody",
                "_HERO:_\n" +
                        "1. added new hero class: the peasant\n\n" +
                        "_FIXES:_\n" +
                        "1. wonky step now actually works\n" +
                        "2. wonky step now isnt always enabled, even when the challenge is disabled\n" +
                        "3. newly added badges should work properly now\n" +
                        "4. challenge 'back to origins' should no longer have a starving threshold thats way higher than intended\n\n" +
                        "_BADGES:_\n" +
                        "1. 3 badges (Total nightmare, Absolute torture, Impossibly possible) are no longer considered global\n" +
                        "2. added new hero class badges, named old ones legacy\n" +
                        "3. new requirements to unlock each hero class\n\n" +
                        "_BOSSES:_\n" +
                        "1. rebalanced tengu phase 2 a bit more\n" +
                        "2. tengu now has a bit less hp in stronger bosses (450 -> 350)\n" +
                        "3. tengu should no longer use phase 2 traps in phase 1\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. changed requirements to unlock each class. warrior now has a requirement too"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_GREY), "v1261: 1 Line Changed, Literally",
                "_MECHANIC CHANGES:_\n" +
                        "1. challenges SHOULD be unlocked by default now, no longer requiring a win to be unlocked"));

        changes.addButton( new ChangeButton(new Image(new BanditSprite()), "v1260: Final Changes (Hopefully)",
                "_ENEMIES:_\n" +
                        "1. guard defensive skill reduced (10 -> 1)\n" +
                        "2. guard DR reduced (0,5 -> 0,4)\n" +
                        "3. guard damage reduced (5,14 -> 5,10)\n" +
                        "4. bandit no longer inflicts poison, but can now inflict cripple\n" +
                        "5. thief and bandit can no longer steal cursed items\n" +
                        "6. thief and bandit can steal equipped items regardless of their level now\n\n" +
                        "_QUEST ENEMIES:_\n" +
                        "1.gnoll trickster damage reduced (quest only) (2,7 -> 2,5)\n\n" +
                        "_BADGES:_\n" +
                        "1. added 3 new badges (Total nightmare, Absolute torture, Impossibly possible)\n\n" +
                        "_CHALLENGES:_\n" +
                        "1. guns blazing:\n" +
                        "1.1 base % for enemies to react to the alarm is reduced to 20%\n" +
                        "1.2 % for enemies to react to the alarm increases by 2.5% based on the floor the player is on (+2.5% each floor)\n" +
                        "2. i hate myself:\n" +
                        "2.1 no longer bans dewdrops, pasty, or stylus\n" +
                        "2.2 now bans stone of intuition\n" +
                        "2.3 curse chance reduced to 70%\n" +
                        "3. added new challenge: wonky step:\n" +
                        "3.1 adds a 2.5% chance to 'trip' every time you walk, making you take 5% of max hp as damage\n" +
                        "3.2 whenever you trip, theres a 20% to be crippled and take an additional 10% max hp damage\n" +
                        "3.3 whenever you trip, and hit the previous 20%, theres a 10% to be crippled MUCH further and take an additional 205 max hp damage ONTOP OF EVERYTHING SO FAR"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "v1256: Error Fixes",
                "_FIXES:_\n" +
                        "1. gnoll trickster should no longer crash the game when it dies\n" +
                        "2. guard attacking a mirror image should no longer crash the game\n\n" +
                        "_CHANGES:_\n" +
                        "1. warrior now starts with their unique item in their inv"));

        changes.addButton( new ChangeButton(new Image(new GuardSprite()), "v1254: Weaker Tanks & Gear",
                "_ENEMIES:_\n" +
                        "1. guard bash ability damage reduced (x2 -> x1.5)\n" +
                        "2. guard bash ability cooldown increased (5 -> 12)\n" +
                        "3. guard DR reduced (0,7 -> 0,5)\n\n" +
                        "_ITEMS:_\n" +
                        "1. upgrades and magic upgrades only reduce each others effects by 20% instead of 50% now (on armor)\n" +
                        "2. scrolls of magic upgrade are now limited drops per region just like upgrade scrolls. amount per region is 3\n" +
                        "3. wand of blast wave can no longer naturally spawn (can still be obtained from wandmaker)\n\n" +
                        "_TRINKETS:_\n" +
                        "1. exotic Crystals base effect chance reduced (12.5% -> 5%), and effect per level reduced (+12.5% -> +2.5%)\n" +
                        "2. eye of newt base mindvision range reduced (2 -> 1)\n" +
                        "3. parchment scrap curse multiplier base starts less, but no longer decreases after 1 level, up to 100% curse chance\n" +
                        "4. petrified seed has less chance to replace with runestone now for each level. grass drop chance scaling reduced\n" +
                        "5. shard of oblivion luck boost % per unidentified item decreased (20% -> 10%)\n" +
                        "6. trap mechanism costs more energy to upgrade, reduced chance for traps to be visible\n" +
                        "7. wonderous resin can no longer naturally spawn\n\n" +
                        "_RINGS:_\n" +
                        "1. ring of accuracy has less effect each level (1.3 -> 1.15)\n" +
                        "2. ring of arcana has less effect each level (1.175 -> 1.125)\n" +
                        "3. ring of elements has less effect each level (0.825 -> 0.925)\n" +
                        "4. ring of energy has less effect each level (1.175 -> 1.1)\n" +
                        "5. ok, you get the point now. next: evasion (1.125 -> 1.1)\n" +
                        "6. force: half as effective past 14 STR and effect starts at tier 1 instead of 2\n" +
                        "7. furor (1.09051 (wth is that number??) -> 1.06)\n" +
                        "8. haste (1.175 -> 1.08)\n" +
                        "9. might: STR (+1 -> +0.5), HP (1.035 -> 1.025)\n" +
                        "10. sharpshooting: level (+1 -> +0.5), durability (1.2 -> 1.09)\n" +
                        "11. tenacity (0.85 -> 0.94)\n" +
                        "12. wealth... OH BOI HERE WE GO:\n" +
                        "12.1 min level for equipment drops scales twice as slow and starts at +0 instead of +1. can now also be cursed\n" +
                        "12.2 ring level is 33% as effective for increasing floorset item luck\n" +
                        "12.3 each ring level decreases less % from low tier drops (-4% -> -1%), base % increased (60% -> 80%)\n" +
                        "12.4 each ring level decreases less % from mid tier drops (-2% -> -0.5%)\n" +
                        "12.5 less effect each level (raw increase in drop chance)(1.2 -> 1.06)\n" +
                        "12.6 increased counter for drops (0,20 -> 40,80), and increased drops to equipment (5,10 -> 16,28)\n" +
                        "12.7 NOTE: ring of wealth was nerfed THIS MUCH because people figured out in vanilla, that you can farm the HELL out of this and basically become god if you waste enough hours (yes people REALLY did this)\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. gold gain from gold piles is reduced by 35%\n\n" +
                        "_QUESTS:_\n" +
                        "1. wandmaker now has a additional 20% to have one of his rewards to be the wand of blast wave, rolled once for each reward option (effectively making it a more common quest reward)\n" +
                        "2. wandmaker can no longer give out the corpse dust quest (reason: wraiths are buffed and the quest isnt incredibly special)\n\n" +
                        "_QUEST ENEMIES:_\n" +
                        "1. newborn fire elemental hp increased (60 -> 90), damage increased (10,12 -> 14, 18), no longer considered fiery\n" +
                        "2. rotheart hp increased (80 -> 125), DR increased (0,5 -> 1,5), no longer instantly dies when on fire\n" +
                        "3. rotlasher loot chance reduced (75% -> 20%), now heals 10 hp instead of 5 on idle, now spends only half a tick when seeing a player (instead of 1 tick), damage reduced (10,20 -> 6,15),  no longer instantly dies when on fire, lose less quest score when hit by a rotlasher (-100 -> -15)"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "v1252: Fixes & Balancing",
                "_VANITY:_\n" +
                        "1. added exotic variant of GYKANA scroll\n" +
                        "2. updated main menu logo\n" +
                        "3. updated .ICO\n\n" +
                        "_FIXES:_\n" +
                        "1. listed exotic variant of GYKANA scroll, which should solve a error\n\n" +
                        "_ENEMIES:_\n" +
                        "1. DM151 zaps per turn reduced (3 -> 2.25)\n" +
                        "2. guard now has a new ability 'bash', that deals x2 damage with a 5 turn cooldown\n" +
                        "3. necromancer hp increased (32 -> 45)\n" +
                        "4. DM100 damage reduced (4,8 -> 3,6)\n\n" +
                        "_BOSSES:_\n" +
                        "1. tengu phase 2 starts later (65% -> 40%)\n" +
                        "2. tengu phase 2 abilities start earlier (65% -> 75%), and ability cooldowns go down quicker based on hp (not fully playtested but should be fine)\n" +
                        "3. goo defensive skill reduced (8 -> 4)\n\n" +
                        "_NEW CONTENT:_\n" +
                        "1. added warden enemy (currently unused)"));

        // ===== v1200 and up =====
        changes = new ChangeInfo("v1200 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "v1249: Massive Bug Fix",
                "_FIXES:_\n" +
                        "1. no longer dupe armor when equipping it\n" +
                        "2. magic DR now actually works\n" +
                        "3. fixed scroll of magic upgrade\n" +
                        "4. fixed some text not being displayed\n" +
                        "5. fixed misalignment of magic upgrade text on item icons"));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_GYKANA), "v1245: Magical Armor",
                "_NEW CONTENT:_\n" +
                        "1. added scroll of magic upgrade (1.5 average should spawn each rotation)\n" +
                        "2. armor now has a 2nd level variant: magic level\n" +
                        "3. ALL armor now has magic DR based on magic level\n" +
                        "4. armor normal level reduces magic DR bonus, and magic level reduces normal DR bonus\n" +
                        "5. glyph of antimagic changed: now has a chance to proc and reduce incoming magic damage by 75% (ONLY magic damage)\n" +
                        "6. wands can now ONLY be upgraded with scroll of magic upgrade\n\n" +
                        "_ENEMIES:_\n" +
                        "1. DM100 damage increased (3,7 -> 4,8)\n" +
                        "2. DM151 loot chance decreased (25% -> 15%)\n\n" +
                        "_VANITY:_\n" +
                        "1. armor now shows magic DR in desc\n" +
                        "2. armor now shows magic level ontop of level\n\n" +
                        "_CHALLENGES:_\n" +
                        "1. guns blazing:\n" +
                        "1.1 whenever alarming enemies, each enemy now has 40% to react to it (should hopefully make it more possible)"));

        changes.addButton( new ChangeButton(new Image(new GhostSprite()), "v1242: Weaker Quest Rewards",
                "_CHANGES:_\n" +
                        "1. sad ghost reward now pulls all stat odds from the item generation. enchant chance is a flat 10%\n" +
                        "2. wandmaker rewards are no longer level +1 and have a 33% to be cursed"));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "v1241: Guns Blazing",
                "_CHALLENGES:_\n" +
                        "1. added guns blazing challenge:\n" +
                        "1.1 whenever you enter any floor, you will trigger the effect of the alarm trap\n"));

        changes.addButton(new ChangeButton(Icons.get(Icons.BUFFS), "v1240: The Balance Fix",
                "_VANITY:_\n" +
                        "1. changed DM151 sprite to be slightly more unique\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "9. changed one DM100 to a DM151, Always guarantee'ing a DM151.\n\n" +
                        "_ENEMIES:_\n" +
                        "1. DM151 speed decreased (1.2 -> 1), hp increased (15 -> 24), loot chance decreased (40% -> 25%)\n" +
                        "2. DM100 loot chance decreased (40% -> 10%)\n" +
                        "3. swarm base loot chance decreased (16.67% -> 5%)\n" +
                        "4. guard base loot chance decreased (20% -> 6%)\n" +
                        "5. crab base loot chance decreased (16.67% -> 8%)\n" +
                        "6. necromancer hp increased (20 -> 32), xp reduced (7 -> 5)\n" +
                        "7. slime base loot chance decreased (20% -> 8%)\n\n" +
                        "_ITEMS:_\n" +
                        "1. armor strength scaling now scales 50% more\n" +
                        "2. weapon strength scaling now scales 50% more\n" +
                        "3. tier 1 items can now spawn in the dungeon\n" +
                        "4. adjusted tier propabilities again (for the worse v2)\n" +
                        "5. healing potions are a bit worse now\n\n" +
                        "_ENEMIES PART 2:_\n" +
                        "1. goopling ooze proc chance decreased (10% -> 5%)\n" +
                        "2. crab damage increased (2,6 -> 4,8)\n" +
                        "3. slime damage increased (2,5 -> 4,6)\n" +
                        "4. slime damage increased, but only on depths 6 and lower: (4,6 -> 5,8)\n" +
                        "5. guard damage increased (4,12 -> 5,14)\n" +
                        "6. skeleton damage increased (3,7 -> 3,11)\n" +
                        "7. necroskeleton damage increased (3,7 -> 3,11), now has a 50% to spawn with a additional 5 hp (stacks twice)\n" +
                        "8. thief speed increased (0.75 -> 0.8), damage increased (1,10 -> 4,12). speed increased, but only on depths 6 and lower: (0.8 -> 0.9)\n" +
                        "9. DM100 and DM151 damage decreased (3,10 -> 3,7)\n" +
                        "10. bandit speed increased (0.85 -> 1)\n" +
                        "11. fetid rat base hp increased (24 -> 30), base xp reduced (6 -> 3), base damage increased (3,7 -> 3,7)\n" +
                        "12. hermit crab hp increased (31 -> 45), now has damage reduction like slime with value 6+\n" +
                        "13. great crab hp increased (25 -> 32), only drops 1 mystery meat instead of 2 now\n" +
                        "14. newborn fire elemental hp increased (??? -> 60), and now deals direct damage when zapping\n" +
                        "15. mimic hp increased as if it was 4 floors deeper, base defense increased (2 -> 5)\n\n" +
                        "_BOSSES:_\n" +
                        "1. goo damage increased (1,8/12 -> 4,8/12)\n" +
                        "2. goo ooze proc chance decreased (33% -> 25%)\n\n" +
                        "_FIXES:_\n" +
                        "1. fetid rat level now properly saved/loaded on saved games\n" +
                        "2. gnoll trickster level now properly saved/loaded on saved games\n" +
                        "3. thief steal attempts (and steal %) now properly saved/loaded on saved games"));

        changes.addButton( new ChangeButton(new Image(new SpiritualNecromancerSprite()), "v1.232: A Little Extra",
                "_NEW CONTENT:_\n" +
                        "1. added spiritual necromancer (currently unused)\n\n" +
                        "_ENEMIES:_\n" +
                        "1. guard hp increased (50 -> 60) (they do be tanky now)"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), "v1.233: First Release",
                "_CHANGES:_\n" +
                        "1. setup a actual github repository\n" +
                        "2. created first usable release\n" +
                        "3. i am in active pain\n" +
                        "4. the github repo is NOT public at this time yet"));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), "v1.23: Cheating No More",
                "_CHANGES:_\n" +
                        "1. dying or winning in a run with the cheat mode option active will no longer save the run on the rankings or award badges\n" +
                        "2. starting a run in cheat mode will store the run in cheat mode, theres no escaping. turning cheat mode on during a run (should) not do anything\n\n" +
                        "_ENEMIES:_\n" +
                        "1. goopling now considered acidic\n" +
                        "2. goopling can now inflict ooze (like goo) at a 10% chance every hit (goo has 33%)\n" +
                        "3. goopling damage increased (1,3 -> 2,4), because lots of upgrade scrolls spawn\n" +
                        "4. goopling hp decreased (12 -> 9), damage reduction increased (4+ -> 3+), still takes 3 hits to kill with minimal damage\n\n" +
                        "_BOSSES:_\n" +
                        "1. goo turns to spawn a goopling increased (8 -> 10), because gooplings have pretty high damage reduction\n" +
                        "2. goo can now only have 8 gooplings alive at any one time\n\n" +
                        "_CHALLENGES:_\n" +
                        "1. stronger bosses:\n" +
                        "1.1 goo max gooplings increased to 12\n" +
                        "1.2 gooplings damage increased (2,4 -> 2,5), and hp increased (9-> 12)\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. demon spawners can now spawn all enemies from the halls region based on depth (starting 21 with 1 type, ending at 24 with all types), fallback is the default spawning enemy\n" +
                        "2. rare chance to spawn mob from 1 region lower for each floor before a boss (4, 9, 14, etc) increased (2.5% -> 10%)\n" +
                        "3. changed mob to spawn as rare chance from 1 region lower for floor 4 (thief -> DM100) because thief always spawns on floor 4 now\n\n" +
                        "_NEW CONTENT:_\n" +
                        "1. added mythic gnoll enemy\n" +
                        "2. mythic gnoll may spawn on floors 2, 3 or 4 as a *additional* enemy at 2%\n\n" +
                        "_VANITY:_\n" +
                        "1. changed bandit sprite a bit"));

        changes.addButton(new ChangeButton(Icons.get(Icons.DISPLAY_LAND), "v1.22: Mod != SPD",
                "_CHANGES:_\n" +
                        "1. the modded version no longer shares data with vanilla SPD\n" +
                        "2. changed app icon"));

        changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_COLOR), "v1.21: IS IT HARD ENOUGH?!",
                "_NEW CONTENT:_\n" +
                        "1. added new challenge: Back to origins:\n" +
                        "1.1 Starting hp is back to 20\n" +
                        "1.2 Strength potions give only +1 STR\n" +
                        "1.3 Food no longer gives extra energy\n" +
                        "1.4 Hunger and starving thresholds are back to default\n\n" +
                        "2. added new challenge: I hate myself:\n" +
                        "2.1 ALL armors, weapons, and rings have a 99% to be cursed\n" +
                        "2.2 Dewdrops, Stylusses, Scrolls of identify, and Pasties are banned\n" +
                        "2.3 Potions and scrolls are no longer identified when used\n" +
                        "2.4 Sidenote:_ If this is too easy for you, try pairing this challenge with 'Back to origins' :)"));

        changes.addButton( new ChangeButton(new Image(new DM151Sprite()), "v1.2: Clean-up & Variants",
                "_ENEMIES:_\n" +
                        "1. gnoll exile damage increased (1,10 -> 3,10), damage reduction increased (0,1 -> 1,2), and hp increased (24 -> 25)\n" +
                        "2. bandit is no longer a rare variant of thief, but rather a now normal enemy\n" +
                        "3. bandit stats are no longer equal to thief. hp = 45, defense = 22, speed = 0.85, xp = 3, maxlvl = 19, damage = (6,12). still causes blindness and poison, but no more cripple. same stealing stats as thief.\n" +
                        "4. thief and bandit base stealing chance reduced (25% -> 12.5%)\n" +
                        "5. piranha base defensive skill reduced (10 -> 5), and base max damage reduced (4 -> 2)\n" +
                        "6. golden mimics buffed a bit (1.33 -> 1.4)\n" +
                        "7. ebony mimics buffed ALOT (1 -> 1.5)\n" +
                        "8. wraiths defense reduced (attack skill * 5 -> attack skill * 3), hp increased (3 -> 5)\n" +
                        "9. DM151 no longer considered flying, inherits hp, defense, xp, loot chance, and damage from DM100 now\n" +
                        "10. DM151 is now a rare variant of DM100, with a bit more speed and a additional zap when attacking\n" +
                        "11. hermit crab hp increased (25 -> 31), speed decreased (1 -> 0.9) still inherits damage from crab\n\n" +
                        "_BOSSES:_\n" +
                        "1. tengu now has more hp in tougher bosses challenge (400 -> 450)\n" +
                        "2. tougher bosses challenge changes to tengu phase 2 no longer apply, though tengu will only use 2 of the 3 abilities like usual\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. rare enemy base chance increased (2% -> 5%)\n\n" +
                        "_NEW CONTENT:_\n" +
                        "1. DM166 (currently unused) (planned as a halls enemy)\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "11. no longer spawns dm100\n" +
                        "12. no longer spawns necromancer\n" +
                        "16. can now spawn bandit\n" +
                        "17. can now spawn bandit\n" +
                        "18. can now spawn bandit\n\n" +
                        "_NOTES:_\n" +
                        "almost done with floors 1 to 10... hope you're ready for the floors coming after :)"));


        // ===== v1150 and up =====
        /* too few versions to justify a whole section for it, will just add the changes to the v1100 section
        changes = new ChangeInfo("v1150 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
         */


        // ===== v1100 and up =====
        changes = new ChangeInfo("v1100 and up", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new Image(Assets.Sprites.SENTRY), "v1.145: All About Vanity",
                "_ROOMS:_\n" +
                        "1. sentry in sentry room now has more accuracy (20+[depth*2] -> 25+[depth*3]), ad deals more BASE damage (2,4 -> 3,6)\n" +
                        "2. fetid rat accuracy and damage now scales with other stats\n" +
                        "3. gnoll trickster now scales in stats based on region, in the exact same way as fetid rat. guaranteed drop when killed in sewers/jails, 18% otherwise\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "11. can no longer spawn fetid rat\n" +
                        "13. can now spawn fetid rat\n" +
                        "14. can now spawn gnoll trickster\n\n" +
                        "_UI/VANITY/MAIN MENU/JOURNAL:_\n" +
                        "1. changed look of title a bit\n" +
                        "2. added new bestairy entries\n" +
                        "3. updated some bestairy entries\n" +
                        "4. added new bestairy categories (special and recurring)\n\n" +
                        "_SETTINGS:_\n" +
                        "1. added cheat mode, which when enabled, makes your hero start with 999999 hp and 50 strength. this mode is mainly made for testing new stuff\n\n" +
                        "_FUTURE PLANS:_\n" +
                        "1. make it so no badges can be earned in cheat mode, and the run will not be stored on your rankings"));


        changes.addButton( new ChangeButton(new Image(Assets.Sprites.TENGU, 0, 0, 14, 16), "v1.14: Stronger Tengu & Jails",
                "_ENEMIES:_\n" +
                        "1. crab hp decreased again (26 -> 20)\n" +
                        "2. fetid rat stats now scale a bit based on depth (level 0 = <5, lvl 1 = <15, lvl 2 otherwise) (see spawning floors)\n\n" +
                        "_BOSSES:_\n" +
                        "1. tengu hp increased (200 -> 300, 250 -> 400 if challenge)\n" +
                        "2. tengu bug whereby he starts spamming in phase 2 (should) be fixed\n" +
                        "3. tengu's abilities in phase 2 are now based on turns passed instead of jumps/hp (this might need some balancing still)\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "6. unchanged\n" +
                        "7. 1 less skeleton, additional slime and thief\n" +
                        "8. 1 less skeleton, additional slime and thief\n" +
                        "9. no longer spawns skeleton, additional DM-100 (for a total of 4!), and additional necromancer\n" +
                        "11. can now spawn fetid rat as a normal enemy, can also spawn dm100 and necromancer\n" +
                        "12. can now spawn fetid rat as a normal enemy, can also spawn necromancer"));

        changes.addButton( new ChangeButton(new Image(new GooplingSprite()), "v1.13: Stronger Goo",
                "_NEW CONTENT:_\n" +
                        "1. added goopling enemy (12 hp, 5 defensive, 1-3 damage, damage reduction like slime: 4+)\n" +
                        "2. added DM151 enemy (currently unused)\n\n" +
                        "_ENEMIES:_\n" +
                        "1. slime hp reduced (32 -> 20)\n" +
                        "2. necromancer summon time reduced (7 -> 5)\n" +
                        "3. DM-100 no longer has Flying status\n\n" +
                        "_BOSSES:_\n" +
                        "1. goo now spawns gooplings every now and then (no limit)\n" +
                        "2. goo now spawns gooplings whenever it did a successful pump attack (again, no limit)\n" +
                        "3. goo now spawns 3 gooplings whenever it dies\n" +
                        "4. increased goo hp (100 -> 125, 120 -> 150 if challenge)\n" +
                        "5. tengu phase 2 starts earlier (50% hp -> 65% hp)\n" +
                        "6. tengu now heals to full hp after entering phase 2"));

        changes.addButton( new ChangeButton(new Image(new SwarmSprite()), "v1.12: Toned Down Enemies",
                "_ENEMIES:_\n" +
                        "1. swarm damage reduced (3,8 -> 2,5)\n" +
                        "2. crab defensive skill reduced (9 -> 3), hp increased (18 -> 26)\n" +
                        "3. thief now has a 40% to steal equipped item, steals non-equipped otherwise. now has a chance to steal (base 25% instead of guaranteed), but each fail gives +12.5% to succeed, resets when successful steal. speed increased (0.75 -> 0.8), hp increased (25 -> 32)\n" +
                        "4. mimic speed reduced (1.5 -> 1.25), decreased hp scaling per depth (from *8 to *5), deals more damage when first hit (2+2*depth -> 4+2*depth), damage changed (1+depth, 2+[2*depth] -> depth, round[2.5*depth])\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "3. can no longer spawn crab\n" +
                        "7. can no longer spawn crab\n\n" +
                        "_FUTURE IDEA'S?:_\n" +
                        "1. maybe add a stronger variant of thief named bandit, which spawns deeper, works about the same and is stronger\n" +
                        "2. give goo the ability to summon weaker variants of the slime during battle?\n" +
                        "3. allow quest enemies to naturally spawn on some floors…? (note: give them stat scaling based on depth if you do this)"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER), "v1.11: Less Luck, Better Resources",
                "_ITEMS:_\n" +
                        "1. potion of strength now gives 2 instead of 1\n" +
                        "2. levitation effect lasts longer (20 -> 30)\n" +
                        "3. additional cloth and leather armour added to spawn pool\n\n" +
                        "_ARMOUR GLYPHS:_\n" +
                        "1. slightly buffed chance to proc for thorns\n" +
                        "2. buffed swiftness glyph scaling with level\n" +
                        "3. buffed base effect of obfuscation glyph\n" +
                        "4. slightly buffed glyph of potential\n" +
                        "5. lowered proc chance of glyph of repulsion\n" +
                        "6. lowered proc chance of affection glyph\n\n" +
                        "_ENEMIES:_\n" +
                        "1. fetid rat hp reduced (36 -> 24)\n" +
                        "2. gnoll defensive skill reduced (10 -> 6), damage reduced (1,6 -> 1,5)\n" +
                        "3. gnoll trickster hp reduced (36 -> 24), effect proc's 1 combo later\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. energy multiplier gain from food increased (x1.25 -> 1.6)\n" +
                        "2. changed hunger/starving thresholds (300,450 -> 500, 700)\n" +
                        "3. search now takes more turns (2 -> 3)\n" +
                        "4. hero's no longer start with any armour\n" +
                        "5. hero's now start with more hp (20 -> 30)\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "2. can no longer spawn crab"));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.MASTERY, null), "v1.1: Less Luck, More Skill",
                "_ITEMS:_\n" +
                        "1. weapon and armour cursed chance decreased (75% -> 40%)\n" +
                        "2. ring cursed chance decreased (90% -> 50%)\n\n" +
                        "_ENEMIES:_\n" +
                        "1. crab damage decreased (4,10 -> 2,7), decreased max level (9 -> 5), decreased xp (3 -> 2), increased defensive skill (5 -> 9), hp increased (15 -> 18)\n" +
                        "2. dm100 now tries to be 1 tile away instead of 2\n" +
                        "3. snake hp decreased (4 -> 3), increased defensive skill (32 -> 36)\n" +
                        "4. thief speed decreased (0.8 -> 0.75)\n\n" +
                        "_QUEST ENEMIES:_\n" +
                        "1. fetid rat damage increased (1,4 -> 2,5), hp increased (20-> 36), increased defensive skill (5 -> 8), increased xp (4 -> 6)\n" +
                        "2. gnoll trickster xp increased (5 -> 7), hp increased (20 -> 36), damage increased (1,6 -> 2,7), speed increased (1 -> 1.2)\n\n" +
                        "_UNIVERSAL ENEMIES:_\n" +
                        "1. statue hp increased (15+[depth*5] -> 25+[depth*7]), defensive skill increased (4+depth -> 8+[depth*2])\n" +
                        "2. wraiths now have damage reduction like the slime, hp increased (1 -> 3), base damage increased (1,2 -> 1,4) (damage still scales with depth)\n" +
                        "3. piranha loot drop chance decreased (100% -> 70%), hp decreased (10+[depth*5] -> 4+[depth*4]), speed increased (2 -> 3)\n" +
                        "4. mimic hp increased ([1+depth]*6 -> [1+depth]*8), speed increased (1 -> 1.5), now give xp (0 -> 1+round[depth/2])\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "4. can now spawn snake\n" +
                        "6. can now spawn snake\n" +
                        "7. can now spawn snake\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. base enemy limit increased (3 -> 5)\n" +
                        "2. large floors enemy limit multiplier increased (+33% -> +50%)\n" +
                        "3. enemy respawn cooldown changed (/10, /2 -> /5, /2) (its base higher now)\n" +
                        "4. even faster enemy respawn cooldown on floor 1 if amulet (/25 -> /40)"));

        // ===== V1 =====
        changes = new ChangeInfo("V1 (V1000)", true, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.REMAINS, null), "V1: Cursed Nightmare",
                "_ITEMS:_\n" +
                        "1. weapon and armour cursed chance increased (30% -> 75%)\n" +
                        "2. ring cursed chance increased (30% -> 90%)\n" +
                        "3. armour naturally inscribed chance decreased (15% -> 5%)\n" +
                        "4. stylus use time increased (2 -> 25)\n" +
                        "5. upgrade scroll now has a CHANCE to remove/weaken a curse (chance decreases the higher lvl the item)\n" +
                        "6. item tier chances per region have been changed (for the worse)\n" +
                        "7. 5 guaranteed upgrade scrolls spawn per region now instead of 3\n" +
                        "8. keys now take more turns to use (1 -> 5)\n" +
                        "9. changed num of identification and remove curse scrolls (they are rarer now)\n" +
                        "10. dewdrops now heal a bit less (20 drops = 60% instead of 100%)\n" +
                        "11. dropped gold gives less gold when collected\n" +
                        "12. swapped around some of the tier numbers for some armours (for trolling muehehheheeh)\n" +
                        "13. huntress spirit bow now starts weaker and scales slower\n\n" +
                        "_SEWER:_\n" +
                        "1. albino xp changed (2 -> 0) and increased hp (15 -> 30), bleed chance increased (33% -> 50%)\n" +
                        "2. crab speed lowered (2 -> 1), xp changed (4 -> 3), increased damage (1,7 -> 4,10) (BE SCAREDDDD)\n" +
                        "3. rat xp added? (??? -> 1), hp increased (8 -> 10)\n" +
                        "4. snake no longer drops loot, increased defensive skill (supposedly dodge? 25 -> 32)\n" +
                        "5. gnoll increased defensive skill (4 -> 10), reduced gnoll hp (12 -> 10), reduced max level xp (8 -> 4)\n" +
                        "6. swarm hp reduced (50 -> 30), damage increased (1,4 -> 3,8) (BE SCAREDDD)\n\n" +
                        "_JAIL:_\n" +
                        "1. slime now has doubled damage reduction, increased hp (20 -> 32), lowered xp (4 -> 3)\n" +
                        "2. skeleton xp lowered (5 -> 3), increased defensive skill (9 -> 16), changed damage (2,10 -> 3,7), lowered default loot chance (16.67% -> 5%)\n" +
                        "3. thief speed reduced (1 -> 0.8), xp reduced (5 -> 1), will now steal EQUIPPED items before unequipped items, cannot steal items that are leveled above 4\n" +
                        "4. DM100 now considered flying, hp reduced (20 -> 15), loot chance increased (25% -> 40%), decreased time to zap (1 -> 0.75), decreased zap damage (3,10 -> 1,5), now Always tries to be 1+ tiles away from the player's reach.\n" +
                        "5. guard hp increased (40 -> 50)\n" +
                        "6. necromancer can now summon up to 4 skeletons instead of 1, health decreased (40 -> 20), summon time increased (2 -> 7)\n\n" +
                        "_SPAWNING FLOORS:_\n" +
                        "1. unchanged\n" +
                        "2. can now spawn crab\n" +
                        "3. unchanged\n" +
                        "4. can now spawn thief\n" +
                        "6. can now spawn crab and slime\n" +
                        "7. can now spawn crab and slime\n" +
                        "8. can now spawn slime\n" +
                        "9. spawns a extra DM100\n\n" +
                        "_MECHANIC CHANGES:_\n" +
                        "1. hunger gain each turn is halved\n" +
                        "2. eating food gives 25% more energy"));
    }
}