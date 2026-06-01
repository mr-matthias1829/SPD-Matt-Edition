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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith.ArrowSign;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith.SwordMidas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
public class WndBlacksmith extends Window {

    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 180;

    private static final int GAP  = 2;

    // -------------------------------------------------------------------------
    // Blacksmith weapon pool generation (WEP_BLACKSMITH category)
    // -------------------------------------------------------------------------

    /**
     * Generates 3 reward items from the WEP_BLACKSMITH pool.
     *
     * SwordMidas (index 3) is ALWAYS the first option when the Greed sin or
     * Consuming Greed challenge is active.  Otherwise its probability stays at 0
     * so it never appears naturally.
     */
    private static ArrayList<Item> generateBlacksmithRewards() {
        ArrayList<Item> rewards = new ArrayList<>();

        Generator.Category cat = Generator.Category.WEP_BLACKSMITH;

        // Work on a local copy so we never mutate the live deck.
        float[] probs = cat.defaultProbs.clone();

        // Index 3 == SwordMidas in the WEP_BLACKSMITH.classes array.
        final int MIDAS_INDEX = 3;
        boolean forceMidas =
                Dungeon.isSinActive(Sins.GREED) ||
                        Dungeon.isChallenged(Challenges.CONSUMING_GREED);

        if (forceMidas) {
            probs[MIDAS_INDEX] = 4; // give it actual weight
        }

        for (int slot = 0; slot < 3; slot++) {

            Item item;

            if (slot == 0 && forceMidas) {
                item = (Item) Reflection.newInstance(cat.classes[MIDAS_INDEX]);
                probs[MIDAS_INDEX] = Math.max(0, probs[MIDAS_INDEX] - 4);

            } else {
                int index = Random.chances(probs);

                if (index == -1) {
                    item = new ArrowSign();
                } else {
                    item = (Item) Reflection.newInstance(cat.classes[index]);
                    probs[index] = Math.max(0, probs[index] - 2);
                }
            }

            // generate base item
            item.random();

            // sanitize for blacksmith rewards
            item.level(0);
            item.cursed = false;
            item.cursedKnown = true;

            if (item instanceof Weapon) {
                ((Weapon) item).enchant(null);
            } else if (item instanceof Armor) {
                ((Armor) item).inscribe(null);
            }

            rewards.add(item);
        }

        return rewards;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public WndBlacksmith( Blacksmith troll, Hero hero ) {
        super();

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        IconTitle titlebar = new IconTitle();
        titlebar.icon( troll.sprite() );
        titlebar.label( Messages.titleCase( troll.name() ) );
        titlebar.setRect( 0, 0, width, 0 );
        add( titlebar );

        RenderedTextBlock message = PixelScene.renderTextBlock(
                Messages.get(this, "prompt", Blacksmith.Quest.favor), 6 );
        message.maxWidth( width );
        message.setPos(0, titlebar.bottom() + GAP);
        add( message );

        ArrayList<RedButton> buttons = new ArrayList<>();

        // ── Pickaxe ──────────────────────────────────────────────────────────
        int pickaxeCost = Blacksmith.Quest.freePickaxe ? 0 : 250;
        RedButton pickaxe = new RedButton(Messages.get(this, "pickaxe", pickaxeCost), 6) {
            @Override
            protected void onClick() {
                GameScene.show(new WndOptions(
                        troll.sprite(),
                        Messages.titleCase( troll.name() ),
                        Messages.get(WndBlacksmith.class, "pickaxe_verify")
                                + (pickaxeCost == 0 ? "\n\n" + Messages.get(WndBlacksmith.class, "pickaxe_free") : ""),
                        Messages.get(WndBlacksmith.class, "pickaxe_yes"),
                        Messages.get(WndBlacksmith.class, "pickaxe_no")
                ) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            if (Blacksmith.Quest.pickaxe.doPickUp( Dungeon.hero )) {
                                GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have",
                                        Blacksmith.Quest.pickaxe.name())) );
                            } else {
                                Dungeon.level.drop( Blacksmith.Quest.pickaxe, Dungeon.hero.pos ).sprite.drop();
                            }
                            Blacksmith.Quest.favor -= pickaxeCost;
                            Blacksmith.Quest.pickaxe = null;
                            WndBlacksmith.this.hide();

                            if (!Blacksmith.Quest.rewardsAvailable()) {
                                Notes.remove( Notes.Landmark.TROLL );
                            }
                        }
                    }
                });
            }
        };
        pickaxe.enable(Blacksmith.Quest.pickaxe != null && Blacksmith.Quest.favor >= pickaxeCost);
        buttons.add(pickaxe);

        // ── Reforge ──────────────────────────────────────────────────────────
        int reforgeCost = 500 + 1000 * Blacksmith.Quest.reforges;
        RedButton reforge = new RedButton(Messages.get(this, "reforge", reforgeCost), 6) {
            @Override
            protected void onClick() {
                GameScene.show(new WndReforge(troll, WndBlacksmith.this));
            }
        };
        reforge.enable(Blacksmith.Quest.favor >= reforgeCost);
        buttons.add(reforge);

        // ── Harden ───────────────────────────────────────────────────────────
        int hardenCost = 500 + 1000 * Blacksmith.Quest.hardens;
        RedButton harden = new RedButton(Messages.get(this, "harden", hardenCost), 6) {
            @Override
            protected void onClick() {
                GameScene.selectItem(new HardenSelector());
            }
        };
        harden.enable(Blacksmith.Quest.favor >= hardenCost);
        buttons.add(harden);

        // ── Upgrade ──────────────────────────────────────────────────────────
        int upgradeCost = 1000 + 1000 * Blacksmith.Quest.upgrades;
        RedButton upgrade = new RedButton(Messages.get(this, "upgrade", upgradeCost), 6) {
            @Override
            protected void onClick() {
                GameScene.selectItem(new UpgradeSelector());
            }
        };
        upgrade.enable(Blacksmith.Quest.favor >= upgradeCost);
        buttons.add(upgrade);

        // ── Smith (WEP_BLACKSMITH pool) ───────────────────────────────────────
        // Cost: 2000 favor.
        // Favor is deducted and smiths incremented HERE (before WndSmith opens),
        // so that if the game is closed during reward selection the save/reload
        // path in Blacksmith.interact() correctly reopens WndSmith directly.
        final int SMITH_COST = 2000;
        RedButton smith = new RedButton(Messages.get(this, "smith", SMITH_COST), 6) {
            @Override
            protected void onClick() {
                // Generate rewards from the blacksmith-exclusive pool.
                Blacksmith.Quest.smithRewards = generateBlacksmithRewards();
                // Deduct cost and track usage now — before the sub-window opens.
                Blacksmith.Quest.favor -= SMITH_COST;
                Blacksmith.Quest.smiths++;
                GameScene.show(new WndSmith(troll, hero));
                WndBlacksmith.this.hide();
            }
        };
        smith.enable(Blacksmith.Quest.favor >= SMITH_COST);
        buttons.add(smith);

        // ── Extract ──────────────────────────────────────────────────────────
        // Cost: 800 favor.  Destroy a levelled item; receive scrolls of upgrade
        // equal to floor(itemLevel / 2).
        final int EXTRACT_COST = 800 + 1200 * Blacksmith.Quest.extracts;
        RedButton extract = new RedButton(Messages.get(this, "extract", EXTRACT_COST), 6) {
            @Override
            protected void onClick() {
                GameScene.selectItem(new ExtractSelector(EXTRACT_COST));
            }
        };
        extract.enable(Blacksmith.Quest.favor >= EXTRACT_COST);
        buttons.add(extract);

        // ── Liquidize ────────────────────────────────────────────────────────
        // Can only be used once per run.  Destroy an item; gain favor based on
        // its value, level, and tier.  Tracked in Quest so it survives save/load.
        RedButton liquidize = new RedButton(Messages.get(this, "liquidize"), 6) {
            @Override
            protected void onClick() {
                GameScene.selectItem(new LiquidizeSelector());
            }
        };
        // No favor cost to activate — the "cost" is losing the item.
        // Only blocked by the once-per-run cap.
        liquidize.enable(Blacksmith.Quest.liquidizes < 1);
        buttons.add(liquidize);

        // ── Cash Out ─────────────────────────────────────────────────────────
        RedButton cashOut = new RedButton(Messages.get(this, "cashout"), 6) {
            @Override
            protected void onClick() {
                GameScene.show(new WndOptions(
                        troll.sprite(),
                        Messages.titleCase( troll.name() ),
                        Messages.get(WndBlacksmith.class, "cashout_verify", Blacksmith.Quest.favor),
                        Messages.get(WndBlacksmith.class, "cashout_yes"),
                        Messages.get(WndBlacksmith.class, "cashout_no")
                ) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            new Gold(Blacksmith.Quest.favor).doPickUp(Dungeon.hero, Dungeon.hero.pos);
                            Blacksmith.Quest.favor = 0;
                            WndBlacksmith.this.hide();
                        }
                    }
                });
            }
        };
        cashOut.enable(Blacksmith.Quest.favor > 0);
        buttons.add(cashOut);

        // ── Layout ───────────────────────────────────────────────────────────
        float pos = message.bottom() + 3 * GAP;
        for (RedButton b : buttons) {
            b.leftJustify = true;
            b.multiline    = true;
            b.setSize(width, b.reqHeight());
            b.setRect(0, pos, width, b.reqHeight());
            b.enable(b.active); // visually reflect enabled/disabled state
            add(b);
            pos = b.bottom() + GAP;
        }

        resize(width, (int) pos);
    }

    // =========================================================================
    // WndReforge — unchanged from original
    // =========================================================================

    protected static class WndReforge extends Window {

        private static final int WIDTH   = 120;
        private static final int BTN_SIZE = 32;
        private static final float GAP   = 2;
        private static final float BTN_GAP = 5;

        private ItemButton btnPressed;
        private ItemButton btnItem1;
        private ItemButton btnItem2;
        private RedButton  btnReforge;

        public WndReforge( Blacksmith troll, Window wndParent ) {
            super();

            IconTitle titlebar = new IconTitle();
            titlebar.icon( troll.sprite() );
            titlebar.label( Messages.titleCase( troll.name() ) );
            titlebar.setRect( 0, 0, WIDTH, 0 );
            add( titlebar );

            RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
            message.maxWidth( WIDTH );
            message.setPos(0, titlebar.bottom() + GAP);
            add( message );

            btnItem1 = new ItemButton() {
                @Override
                protected void onClick() {
                    btnPressed = btnItem1;
                    GameScene.selectItem( itemSelector );
                }
            };
            btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE,
                    message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
            add( btnItem1 );

            btnItem2 = new ItemButton() {
                @Override
                protected void onClick() {
                    btnPressed = btnItem2;
                    GameScene.selectItem( itemSelector );
                }
            };
            btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
            add( btnItem2 );

            btnReforge = new RedButton( Messages.get(this, "reforge") ) {
                @Override
                protected void onClick() {

                    Item first, second;
                    if (btnItem1.item().trueLevel() >= btnItem2.item().trueLevel()) {
                        first  = btnItem1.item();
                        second = btnItem2.item();
                    } else {
                        first  = btnItem2.item();
                        second = btnItem1.item();
                    }

                    Sample.INSTANCE.play( Assets.Sounds.EVOKE );
                    ScrollOfUpgrade.upgrade( Dungeon.hero );
                    Item.evoke( Dungeon.hero );

                    if (second.isEquipped( Dungeon.hero )) {
                        ((EquipableItem) second).doUnequip( Dungeon.hero, false );
                    }
                    second.detachAll( Dungeon.hero.belongings.backpack );

                    if (second instanceof Armor) {
                        BrokenSeal seal = ((Armor) second).checkSeal();
                        if (seal != null) Dungeon.level.drop( seal, Dungeon.hero.pos );
                    } else if (second instanceof MissileWeapon) {
                        Buff.affect(Dungeon.hero, MissileWeapon.UpgradedSetTracker.class)
                                .levelThresholds.put(((MissileWeapon) second).setID, Integer.MAX_VALUE);
                    }

                    if (first instanceof Weapon && ((Weapon) first).hasGoodEnchant()) {
                        ((Weapon) first).upgrade(true);
                    } else if (first instanceof Armor && ((Armor) first).hasGoodGlyph()) {
                        ((Armor) first).upgrade(true);
                    } else {
                        first.upgrade();
                    }
                    Badges.validateItemLevelAquired( first );
                    Item.updateQuickslot();

                    Blacksmith.Quest.favor -= 500 + 1000 * Blacksmith.Quest.reforges;
                    Blacksmith.Quest.reforges++;

                    if (!Blacksmith.Quest.rewardsAvailable()) {
                        Notes.remove( Notes.Landmark.TROLL );
                    }

                    hide();
                    if (wndParent != null) wndParent.hide();
                }
            };
            btnReforge.enable( false );
            btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
            add( btnReforge );

            resize( WIDTH, (int) btnReforge.bottom() );
        }

        protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

            @Override public String textPrompt() {
                return Messages.get(WndReforge.class, "prompt");
            }

            @Override public Class<? extends Bag> preferredBag() {
                return Belongings.Backpack.class;
            }

            @Override public boolean itemSelectable(Item item) {
                return item.isIdentified() && !item.cursed && item.isUpgradable();
            }

            @Override public void onSelect( Item item ) {
                if (item != null && btnPressed.parent != null) {
                    btnPressed.item(item);

                    Item item1 = btnItem1.item();
                    Item item2 = btnItem2.item();

                    if (item1 == null || item2 == null) {
                        btnReforge.enable(false);
                    } else if (item1.getClass() != item2.getClass()) {
                        btnReforge.enable(false);
                    } else if (item1 == item2) {
                        btnReforge.enable(false);
                    } else {
                        btnReforge.enable(true);
                    }
                }
            }
        };
    }

    // =========================================================================
    // HardenSelector — unchanged from original
    // =========================================================================

    private class HardenSelector extends WndBag.ItemSelector {

        @Override public String textPrompt() {
            return Messages.get(this, "prompt");
        }

        @Override public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override public boolean itemSelectable(Item item) {
            return item.isUpgradable()
                    && item.isIdentified() && !item.cursed
                    && ((item instanceof Weapon && !((Weapon) item).enchantHardened)
                    ||  (item instanceof Armor  && !((Armor)  item).glyphHardened));
        }

        @Override public void onSelect(Item item) {
            if (item != null) {
                if (item instanceof Weapon) {
                    ((Weapon) item).enchantHardened = true;
                } else if (item instanceof Armor) {
                    ((Armor) item).glyphHardened = true;
                }

                Blacksmith.Quest.favor -= 500 + 1000 * Blacksmith.Quest.hardens;
                Blacksmith.Quest.hardens++;

                WndBlacksmith.this.hide();
                Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                Item.evoke( Dungeon.hero );

                if (!Blacksmith.Quest.rewardsAvailable()) {
                    Notes.remove( Notes.Landmark.TROLL );
                }
            }
        }
    }

    // =========================================================================
    // UpgradeSelector — unchanged from original
    // =========================================================================

    private class UpgradeSelector extends WndBag.ItemSelector {

        @Override public String textPrompt() {
            return Messages.get(this, "prompt");
        }

        @Override public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override public boolean itemSelectable(Item item) {
            return item.isUpgradable()
                    && item.isIdentified()
                    && !item.cursed
                    && item.level() < 2;
        }

        @Override public void onSelect(Item item) {
            if (item != null) {
                item.upgrade();
                Blacksmith.Quest.favor -= 1000 + 1000 * Blacksmith.Quest.upgrades;
                Blacksmith.Quest.upgrades++;

                WndBlacksmith.this.hide();
                Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                ScrollOfUpgrade.upgrade( Dungeon.hero );
                Item.evoke( Dungeon.hero );

                Badges.validateItemLevelAquired( item );

                if (!Blacksmith.Quest.rewardsAvailable()) {
                    Notes.remove( Notes.Landmark.TROLL );
                }

                Catalog.countUse(item.getClass());
            }
        }
    }

    // =========================================================================
    // WndSmith — reward picker for the blacksmith weapon pool
    // =========================================================================

    public static class WndSmith extends Window {

        private static final int WIDTH    = 128;
        private static final int BTN_SIZE = 28;
        private static final int BTN_GAP  = 4;
        private static final int GAP      = 2;

        public WndSmith( Blacksmith troll, Hero hero ) {
            super();

            IconTitle titlebar = new IconTitle();
            titlebar.icon(troll.sprite());
            titlebar.label(Messages.titleCase(troll.name()));

            RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "prompt"), 6 );

            titlebar.setRect( 0, 0, WIDTH, 0 );
            add( titlebar );

            message.maxWidth(WIDTH);
            message.setPos(0, titlebar.bottom() + GAP);
            add( message );

            // smithRewards must already be set before this window opens.
            // If somehow it isn't (e.g. legacy save), generate a safe fallback
            // using the blacksmith pool rather than the old regular-weapon pool.
            if (Blacksmith.Quest.smithRewards == null || Blacksmith.Quest.smithRewards.isEmpty()) {
                Blacksmith.Quest.smithRewards = generateBlacksmithRewardsFallback();
            }

            int count = 0;
            for (Item i : Blacksmith.Quest.smithRewards) {
                count++;
                ItemButton btnReward = new ItemButton() {
                    @Override
                    protected void onClick() {
                        GameScene.show(new RewardWindow(troll, hero, item()));
                    }
                };
                btnReward.item( i );
                btnReward.setRect(
                        count * (WIDTH - BTN_GAP) / Blacksmith.Quest.smithRewards.size() - BTN_SIZE,
                        message.top() + message.height() + BTN_GAP,
                        BTN_SIZE, BTN_SIZE );
                add( btnReward );
            }

            resize(WIDTH, (int) message.bottom() + 2 * BTN_GAP + BTN_SIZE);
        }

        /** Called only as a last-resort fallback — should not normally be reached. */
        private static ArrayList<Item> generateBlacksmithRewardsFallback() {
            ArrayList<Item> fallback = new ArrayList<>();
            Generator.Category cat = Generator.Category.WEP_BLACKSMITH;
            float[] probs = cat.defaultProbs.clone();
            for (int slot = 0; slot < 3; slot++) {
                int index = Random.chances(probs);
                if (index == -1) {
                    fallback.add(new ArrowSign());
                } else {
                    Item item = (Item) Reflection.newInstance(cat.classes[index]);
                    item.random();
                    fallback.add(item);
                    probs[index] = Math.max(0, probs[index] - 2);
                }
            }
            return fallback;
        }

        @Override
        public void onBackPressed() {
            // Intentionally blocked — player must choose a reward.
        }

        private class RewardWindow extends WndInfoItem {

            public RewardWindow( Blacksmith troll, Hero hero, Item item ) {
                super(item);

                RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")) {
                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();

                        // Apply stored enchant / glyph if present.
                        if (item instanceof Weapon && Blacksmith.Quest.smithEnchant != null) {
                            ((Weapon) item).enchant(Blacksmith.Quest.smithEnchant);
                        } else if (item instanceof Armor && Blacksmith.Quest.smithGlyph != null) {
                            ((Armor) item).inscribe(Blacksmith.Quest.smithGlyph);
                        }

                        item.identify(false);
                        Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                        Item.evoke( Dungeon.hero );

                        if (item.doPickUp( Dungeon.hero )) {
                            GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );
                        } else {
                            Dungeon.level.drop( item, Dungeon.hero.pos ).sprite.drop();
                        }

                        // Clear reward state.
                        Blacksmith.Quest.smithRewards  = null;
                        Blacksmith.Quest.smithEnchant  = null;
                        Blacksmith.Quest.smithGlyph    = null;

                        WndSmith.this.hide();

                        if (!Blacksmith.Quest.rewardsAvailable()) {
                            Notes.remove( Notes.Landmark.TROLL );
                        }
                    }
                };
                btnConfirm.setRect(0, height + 2, width / 2 - 1, 16);
                add(btnConfirm);

                RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")) {
                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();
                    }
                };
                btnCancel.setRect(btnConfirm.right() + 2, height + 2, btnConfirm.width(), 16);
                add(btnCancel);

                resize(width, (int) btnCancel.bottom());
            }
        }
    }

    // =========================================================================
    // ExtractSelector
    // Destroy a levelled item; receive floor(level / 2) Scrolls of Upgrade.
    // =========================================================================

    private class ExtractSelector extends WndBag.ItemSelector {

        private final int cost;

        ExtractSelector(int cost) {
            this.cost = cost;
        }

        @Override public String textPrompt() {
            return Messages.get(this, "prompt");
        }

        @Override public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override public boolean itemSelectable(Item item) {
            // Must be at least +1, identified, and uncursed.
            return item.level() >= 1 && item.isIdentified() && !item.cursed;
        }

        @Override public void onSelect(Item item) {
            if (item == null) return;

            int scrollsGained = item.trueLevel() / 2;

            // trueLevel() could be 1 → 0 scrolls; nothing to do.
            if (scrollsGained <= 0) {
                GLog.w(Messages.get(WndBlacksmith.class, "extract_too_low"));
                return;
            }

            // Remove the item from the hero.
            if (item.isEquipped(Dungeon.hero)) {
                ((EquipableItem) item).doUnequip(Dungeon.hero, false);
            }
            item.detachAll(Dungeon.hero.belongings.backpack);

            // Grant the scrolls.
            for (int i = 0; i < scrollsGained; i++) {
                ScrollOfUpgrade scroll = new ScrollOfUpgrade();
                if (!scroll.doPickUp(Dungeon.hero)) {
                    Dungeon.level.drop(scroll, Dungeon.hero.pos);
                }
            }

            Blacksmith.Quest.extracts++;
            Blacksmith.Quest.favor -= cost;

            GLog.p(Messages.get(WndBlacksmith.class, "extract_result", scrollsGained));
            Sample.INSTANCE.play(Assets.Sounds.EVOKE);

            WndBlacksmith.this.hide();

            if (!Blacksmith.Quest.rewardsAvailable()) {
                Notes.remove(Notes.Landmark.TROLL);
            }
        }
    }

    // =========================================================================
    // LiquidizeSelector
    // Destroy an item; gain favor based on its value / level / tier.
    // Capped at once per run (tracked in Blacksmith.Quest.liquidizes).
    // =========================================================================

    private class LiquidizeSelector extends WndBag.ItemSelector {


        @Override public boolean itemSelectable(Item item) {
            // Only upgradable items can be liquidized (weapons, armor, wands, etc.)
            // Artifacts stay excluded because they're unique.
            return item.isUpgradable() && !(item instanceof Artifact);
        }
        @Override public String textPrompt() {
            return Messages.get(this, "prompt");
        }

        @Override public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override public void onSelect(Item item) {
            if (item == null) return;

            // ── Favor formula ────────────────────────────────────────────────
            // Base:      item.value() / 4         (so a 500g item → 125 favor)
            // Level:     trueLevel * 50 if upgradable (e.g. +3 item → +150)
            // Tier:      tier * 25 for melee/armor  (tier-4 weapon → +100)
            // Clamp:     [50, 800]                 (result: 375 + 50 =  425 favor)
            int favorGain = 50 + (item.value() / 4); // you'll always get at least 50

            if (item.isUpgradable()) {
                favorGain += item.trueLevel() * 50;
            }

            if (item instanceof MeleeWeapon) {
                favorGain += ((MeleeWeapon) item).tier * 25;
            } else if (item instanceof Armor) {
                favorGain += ((Armor) item).tier * 25;
            }

            // it's weird to have a random 8 favor or something
            // especially since all options are perfectly rounded to nearest 100th
            // plus the smallest you can normally gain is 50
            // so, to make it look better, round to nearest 10
            favorGain = Math.round(favorGain / 10f) * 10;

            favorGain = Math.min(800, Math.max(50, favorGain));

            // ── Destroy the item ─────────────────────────────────────────────
            if (item.isEquipped(Dungeon.hero)) {
                ((EquipableItem) item).doUnequip(Dungeon.hero, false);
            }
            item.detachAll(Dungeon.hero.belongings.backpack);

            // ── Apply reward and mark as used ────────────────────────────────
            Blacksmith.Quest.favor     += favorGain;
            Blacksmith.Quest.liquidizes++;  // persisted in Quest bundle; blocks further use

            GLog.p(Messages.get(WndBlacksmith.class, "liquidize_result", favorGain));
            Sample.INSTANCE.play(Assets.Sounds.GOLD);

            WndBlacksmith.this.hide();
        }
    }
}