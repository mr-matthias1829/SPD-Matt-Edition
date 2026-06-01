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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ArrowSign extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.ARROW_SIGN;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;

		tier = 6;
	}

    // effectively a war hammer reskin with some different stats
    // buuuttt i will treat this as a brand new weapon even tho it basically isn't

    // this weapon effectively needs ALOT of strength to use
    // but of course: the damage is VERY good
	@Override
	public int max(int lvl) { // compared to other BS weapons, this is actually INSANE damage
		return  5*(tier) +
				lvl*(tier);
	}

    @Override
    public int STRReq(int lvl) {
        int req = STRReq(tier, lvl, getClass()); // tier 6, but needs STR of tier 7
        if (masteryPotionBonus){
            req -= 2;
        }
        return req;
    }

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(6+1.5*lvl) damage, roughly +40% base dmg, +45% scaling
		int dmgBoost = augment.damageFactor(6 + Math.round(1.5f*buffedLvl()));
		Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 6 + Math.round(1.5f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 6 + Math.round(1.5f*buffedLvl()) : 6;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

}
