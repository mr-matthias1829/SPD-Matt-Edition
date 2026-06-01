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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.augments.IcyCoreArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.augments.IcyCoreWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class IcyCore extends InventoryStone {

    {
        preferredBag = Belongings.Backpack.class;
        image = ItemSpriteSheet.ICYCORE;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return ScrollOfEnchantment.enchantable(item);
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndIcyCore(item));
    }

    private void apply(Item item) {
        if (item instanceof Weapon) {
            ((Weapon) item).applyAugment(new IcyCoreWeapon());
        } else if (item instanceof Armor) {
            ((Armor) item).applyAugment(new IcyCoreArmor());
        }
        useAnimation();
        ScrollOfUpgrade.upgrade(curUser);
        if (!anonymous) {
            curItem.detach(curUser.belongings.backpack);
            Catalog.countUse(getClass());
            Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
        }
    }

    @Override
    public int value() {
        return 60 * quantity;
    }

    @Override
    public int energyVal() {
        return 10 * quantity;
    }

    public class WndIcyCore extends Window {

        private static final int WIDTH         = 120;
        private static final int MARGIN        = 2;
        private static final int BUTTON_WIDTH  = WIDTH - MARGIN * 2;
        private static final int BUTTON_HEIGHT = 20;

        public WndIcyCore(final Item toAugment) {
            super();

            IconTitle titlebar = new IconTitle(toAugment);
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);

            RenderedTextBlock tfMessage = PixelScene.renderTextBlock(Messages.get(this, "choice"), 8);
            tfMessage.maxWidth(WIDTH - MARGIN * 2);
            tfMessage.setPos(MARGIN, titlebar.bottom() + MARGIN);
            add(tfMessage);

            float pos = tfMessage.top() + tfMessage.height();

            RedButton btnApply = new RedButton(Messages.get(this, "apply")) {
                @Override
                protected void onClick() {
                    hide();
                    IcyCore.this.apply(toAugment);
                }
            };
            btnApply.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
            add(btnApply);
            pos = btnApply.bottom();

            RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
                @Override
                protected void onClick() {
                    hide();
                    if (!anonymous) IcyCore.this.collect();
                }
            };
            btnCancel.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
            add(btnCancel);

            resize(WIDTH, (int) btnCancel.bottom() + MARGIN);
        }

        @Override
        public void onBackPressed() {
            if (!anonymous) IcyCore.this.collect();
            super.onBackPressed();
        }
    }
}