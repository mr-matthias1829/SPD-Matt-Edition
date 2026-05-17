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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GlovesPurge extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.PURGE_GLOVES;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.3f;

        // todo: either return the 2x speed in a balanced way or maybe buff damage instead
		//DLY = 0.5f; //2x speed
	}

    int cooldown = 0;

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(3+0.75*lvl) damage, roughly +100% base damage, +100% scaling
		int dmgBoost = augment.damageFactor(3 + buffedLvl());
		Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 2 + buffedLvl() : 2;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
		}
	}

	public String upgradeAbilityStat(int level){
		return "+" + augment.damageFactor(3 + level);
	}




    @Override
    public int proc(Char attacker, Char defender, int damage) {
        damage = super.proc(attacker, defender, damage);

        // bosses/minibosses are immune
        if (defender.properties().contains(Char.Property.BOSS)
                || defender.properties().contains(Char.Property.MINIBOSS)) {
            return damage;
        }

        if (cooldown > 0){
            cooldown--;
            return damage;
        }

        // we killed the enemy, no need to attempt to paralyze them
        if (defender.HP <= damage && attacker instanceof Hero) {
            return damage;
        }

        // chance decreases each time the enemy has been stunned
        int stunCount = defender.buff(StunTracker.class) != null
                ? defender.buff(StunTracker.class).count : 0;
        float chance = 0.25f / (1 + stunCount);

        if (Random.Float() < chance) {
            float dur = 2f;
            if (stunCount > 0){
                dur = 1f;
            }
            Buff.prolong(defender, Paralysis.class, dur); // Paralysis = stun in SPD
            StunTracker tracker = Buff.affect(defender, StunTracker.class);
            tracker.count++;
            cooldown = Math.max(4 - level(), 0); // cooldown starts at 4 turns, down to 0 turns at lvl 4+
        }

        return damage;
    }

    private static final String CD = "cd";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CD, cooldown);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        cooldown = bundle.getInt(CD);
    }



    public static class StunTracker extends Buff {
        public int count = 0;

        // persists on the enemy, no duration — just tracks how many times stunned
        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }
        private static final String COUNT = "count";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COUNT, count);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            count = bundle.getInt(COUNT);
        }
    }
}
