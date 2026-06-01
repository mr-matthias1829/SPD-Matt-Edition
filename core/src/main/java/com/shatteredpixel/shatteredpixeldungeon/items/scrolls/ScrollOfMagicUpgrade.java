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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.watabou.utils.Random;

public class ScrollOfMagicUpgrade extends InventoryScroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_MUPGRADE;
        preferredBag = Belongings.Backpack.class;
        unique = true;
        talentFactor = 2f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Armor || item instanceof Wand;
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndUpgrade(this, item, identifiedByUse));
    }

    public void reShowSelector(boolean force) {
        identifiedByUse = force;
        curItem = this;
        GameScene.selectItem(itemSelector);
    }

    public WndBag.ItemSelector getSelector(boolean force){
        identifiedByUse = force;
        curItem = this;
        return itemSelector;
    }

    public Item upgradeItem(Item item) {
        magicUpgrade(curUser);

        Degrade.detach(curUser, Degrade.class);

        if (item instanceof Armor) {
            Armor a = (Armor) item;
            boolean wasCursed = a.cursed;
            boolean wasHardened = a.glyphHardened;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();

            // Increase magic level for armor
            a.magicLevel++;

            int uncurseChance = 4 + a.visiblyUpgraded();
            int weakenChance = 2 + a.visiblyUpgraded();

            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph() && Random.Int(uncurseChance) == 0) {
                removeCurse(Dungeon.hero);
            } else if (a.cursedKnown && wasCursed && !a.cursed && Random.Int(weakenChance) == 0) {
                weakenCurse(Dungeon.hero);
            }
            if (wasHardened && !a.glyphHardened) {
                GLog.w(Messages.get(Armor.class, "hardening_gone"));
            } else if (hadGoodGlyph && !a.hasGoodGlyph()) {
                GLog.w(Messages.get(Armor.class, "incompatible"));
            }

        } else if (item instanceof Wand) {
            boolean wasCursed = item.cursed;

            // Regular upgrade for wands
            item = item.upgrade();

            if (item.cursedKnown && wasCursed && !item.cursed) {
                removeCurse(Dungeon.hero);
            }
        }

        Badges.validateItemLevelAquired(item);
        Statistics.upgradesUsed++;
        Badges.validateMageUnlock();

        Catalog.countUse(item.getClass());

        return item;
    }

    public static void magicUpgrade(Hero hero) {
        hero.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
    }

    public static void weakenCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "weaken_curse"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 5);
    }

    public static void removeCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "remove_curse"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
        Badges.validateClericUnlock();
    }

    @Override
    public int value() {
        return isKnown() ? 50 * quantity : super.value();
    }

    @Override
    public int energyVal() {
        return isKnown() ? 5 * quantity : super.energyVal();
    }
}