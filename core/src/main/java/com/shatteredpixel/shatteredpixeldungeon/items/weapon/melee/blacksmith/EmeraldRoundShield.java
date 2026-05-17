/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

// this class name is different compared to the rest since this is effectively the exact item name unlike the rest
public class EmeraldRoundShield extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.EMERALD_ROUND_SHIELD;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
	}

    // sorry to tell you, but your reflection code is in another file!
    // no but really, its declared in hero.java, inside the damage(...) method
    // why? theres no defenseproc for weapons and i cant be bothered to implement it for this single case
    // ugly? yes. might need fixing later? yes. does it work for now? hell yes.

    // any code below is (nearly) identical to roundshield
    @Override
    public int max(int lvl) {
        return  super.max(lvl-1); // lower effective lvl in exchange for some DR
    }
	@Override
	public int defenseFactor( Char owner ) {
		return DRMax();
	}

	public int DRMax(){
		return DRMax(buffedLvl());
	}

	//4 extra defence, plus 1 per level
	public int DRMax(int lvl){
		return 4 + lvl;
	}
	
	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", 4+buffedLvl());
		} else {
			return Messages.get(this, "typical_stats_desc", 4);
		}
	}

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        guardAbility(hero, 5 + buffedLvl()); // no wep argument needed anymore
    }

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 5+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 5);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(5 + level);
	}

    public void guardAbility(Hero hero, int duration){
        this.beforeAbilityUsed(hero, null); // 'this' is an EmeraldRoundShield, which IS a MeleeWeapon
        Buff.prolong(hero, GuardTracker.class, duration).hasBlocked = false;
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(Actor.TICK);
        this.afterAbilityUsed(hero);
    }

	public static class GuardTracker extends FlavourBuff {

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		public boolean hasBlocked = false;

		@Override
		public int icon() {
			return BuffIndicator.DUEL_GUARD;
		}

		@Override
		public void tintIcon(Image icon) {
			if (hasBlocked){
				icon.tint(0x651f66, 0.5f);
			} else {
				icon.resetColor();
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (5 - visualcooldown()) / 5);
		}

		private static final String BLOCKED = "blocked";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(BLOCKED, hasBlocked);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			hasBlocked = bundle.getBoolean(BLOCKED);
		}
	}
}