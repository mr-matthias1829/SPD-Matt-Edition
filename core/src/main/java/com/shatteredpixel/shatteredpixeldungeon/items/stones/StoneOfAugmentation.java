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
import com.shatteredpixel.shatteredpixeldungeon.items.armor.augments.ArmorAugment;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.augments.DefenseAugment;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.augments.EvasionAugment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.augments.DamageAugment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.augments.SpeedAugment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.augments.WeaponAugment;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class StoneOfAugmentation extends InventoryStone {
	
	{
		preferredBag = Belongings.Backpack.class;
		image = ItemSpriteSheet.STONE_AUGMENTATION;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ScrollOfEnchantment.enchantable(item);
	}

	@Override
	protected void onItemSelected(Item item) {
		
		GameScene.show(new WndAugment( item));
		
	}

    public void apply(Weapon weapon, WeaponAugment augment) {
        weapon.applyAugment(augment);
		useAnimation();
		ScrollOfUpgrade.upgrade(curUser);
		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}
	}

    public void apply(Armor armor, ArmorAugment augment) {
        armor.applyAugment(augment);
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
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 5 * quantity;
	}
	
	public class WndAugment extends Window {
		
		private static final int WIDTH			= 120;
		private static final int MARGIN 		= 2;
		private static final int BUTTON_WIDTH	= WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT	= 20;
		
		public WndAugment( final Item toAugment ) {
			super();
			
			IconTitle titlebar = new IconTitle( toAugment );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock tfMesage = PixelScene.renderTextBlock( Messages.get(this, "choice"), 8 );
			tfMesage.maxWidth(WIDTH - MARGIN * 2);
			tfMesage.setPos(MARGIN, titlebar.bottom() + MARGIN);
			add( tfMesage );
			
			float pos = tfMesage.top() + tfMesage.height();

            if (toAugment instanceof Weapon){

                WeaponAugment[] augments = {
                        new SpeedAugment(),
                        new DamageAugment()
                };

                for (final WeaponAugment aug : augments){

                    if (!((Weapon) toAugment).augment.getClass().equals(aug.getClass())){

                        RedButton btn = new RedButton(aug.name()) {
                            @Override
                            protected void onClick() {
                                hide();
                                StoneOfAugmentation.this.apply((Weapon) toAugment, aug);
                            }
                        };

                        btn.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                        add(btn);

                        pos = btn.bottom();
                    }
                }

            } else if (toAugment instanceof Armor){

                ArmorAugment[] augments = {
                        new EvasionAugment(),
                        new DefenseAugment()
                };

             for (final ArmorAugment aug : augments){
                 if (!((Armor) toAugment).augment.getClass().equals(aug.getClass())){

                     RedButton btn = new RedButton(aug.name()) {
                         @Override
                            protected void onClick() {
                             hide();
                              StoneOfAugmentation.this.apply((Armor) toAugment, aug);
                            }
                        };

                     btn.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                        add(btn);

                     pos = btn.bottom();
                    }
                }
            }
			
			RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
				@Override
				protected void onClick() {
					hide();
					if (!anonymous) StoneOfAugmentation.this.collect();
				}
			};
			btnCancel.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
			add( btnCancel );
			
			resize( WIDTH, (int)btnCancel.bottom() + MARGIN );
		}
		
		@Override
		public void onBackPressed() {
			if (!anonymous) StoneOfAugmentation.this.collect();
			super.onBackPressed();
		}
	}
}
