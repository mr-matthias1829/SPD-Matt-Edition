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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class SickleBloodLetter extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.BLOODLETTER_SICKLE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1f;

		ACC = 0.68f; // 32% penalty to accuracy here is kept
	}

	@Override
	public int max(int lvl) {
		return  super.max(lvl-1); // this weapon has alot of dmg potential, lower lvl + lower acc
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Always inflict bleed equal to a third of damage dealt
        Bleeding existing = defender.buff(Bleeding.class);
        int bleedAmt = damage / 2;
        if (existing != null) {
            existing.set(existing.level() + bleedAmt);
        } else {
            Buff.affect(defender, Bleeding.class).set(bleedAmt);
        }

        // If this hit kills the defender and they were bleeding, heal the attacker
        /* actually this was scrapped
        if (!defender.isAlive() && attacker instanceof Hero) {
            Bleeding bleed = defender.buff(Bleeding.class);
            if (bleed != null) {
                int healAmt = Math.round(defender.buff(Bleeding.class).level() * 0.1f);
                if (healAmt > 0) {
                    ((Hero) attacker).HP = Math.min(((Hero) attacker).HT, ((Hero) attacker).HP + healAmt);
                    attacker.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    GLog.p(Messages.get(SickleBloodLetter.class, "on_heal", healAmt));
                }
            }
        }
         */

        return super.proc(attacker, defender, damage);
    }

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//replaces damage with 15+2.5*lvl bleed, roughly 138% avg base dmg, 125% avg scaling
		int bleedAmt = augment.damageFactor(Math.round(15f + 2.5f*buffedLvl()));
		SickleBloodLetter.harvestAbility(hero, target, 0f, bleedAmt, this);
	}

	@Override
	public String abilityInfo() {
		int bleedAmt = levelKnown ? Math.round(15f + 2.5f*buffedLvl()) : 15;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(bleedAmt));
		} else {
			return Messages.get(this, "typical_ability_desc", bleedAmt);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(augment.damageFactor(Math.round(15f + 2.5f*level)));
	}

	public static void harvestAbility(Hero hero, Integer target, float bleedMulti, int bleedBoost, MeleeWeapon wep){

		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(wep, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = wep;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(wep, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
                ((BlacksmithWeapon) wep).callBeforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);

				Buff.affect(enemy, HarvestBleedTracker.class, 0);
				if (hero.attack(enemy, bleedMulti, bleedBoost, Char.INFINITE_ACCURACY)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}

				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				if (!enemy.isAlive()){
					wep.onAbilityKill(hero, enemy);
				}
                ((BlacksmithWeapon) wep).callAfterAbilityUsed(hero);
			}
		});

	}

	public static class HarvestBleedTracker extends FlavourBuff{};

}
