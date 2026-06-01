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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class WhipFirecracker extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.FIRECRACKER_WHIP;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;
		RCH = 3;    //keep reach, fits this weapon perfectly
	}

	@Override
	public int max(int lvl) {
		return  super.max(lvl-1); // lower effective lvl because more range
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        damage = super.proc(attacker, defender, damage);

        int explodeDmg = Math.round(damage * 0.5f);
        if (explodeDmg > 0) {
            // center burst on main target
            if (Dungeon.level.heroFOV[defender.pos]) {
                CellEmitter.center(defender.pos).burst(BlastParticle.FACTORY, 8);
            }

            // all 8 surrounding tiles
            for (int n : PathFinder.NEIGHBOURS8) {
                int i = defender.pos + n;
                if (i < 0 || i >= Dungeon.level.length()) continue;

                if (Dungeon.level.heroFOV[i]) {
                    CellEmitter.get(i).burst(BlastParticle.FACTORY, 3);
                }

                Char ch = Actor.findChar(i);
                if (ch != null && ch != attacker) {
                    int dmg = explodeDmg - ch.drRoll();
                    if (dmg > 0) ch.damage(dmg, this);
                }
            }

            Sample.INSTANCE.play(Assets.Sounds.BLAST);
        }

        return damage;
    }

	@Override
	protected void duelistAbility(Hero hero, Integer target) {

		ArrayList<Char> targets = new ArrayList<>();
		Char closest = null;

		hero.belongings.abilityWeapon = this;
		for (Char ch : Actor.chars()){
			if (ch.alignment == Char.Alignment.ENEMY
					&& !hero.isCharmedBy(ch)
					&& Dungeon.level.heroFOV[ch.pos]
					&& hero.canAttack(ch)){
				targets.add(ch);
				if (closest == null || Dungeon.level.trueDistance(hero.pos, closest.pos) > Dungeon.level.trueDistance(hero.pos, ch.pos)){
					closest = ch;
				}
			}
		}
		hero.belongings.abilityWeapon = null;

		if (targets.isEmpty()) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		throwSound();
		Char finalClosest = closest;
		hero.sprite.attack(hero.pos, new Callback() {
			@Override
			public void call() {
				beforeAbilityUsed(hero, finalClosest);
				for (Char ch : targets) {
					//ability does no extra damage
					hero.attack(ch, 1, 0, Char.INFINITE_ACCURACY);
					if (!ch.isAlive()){
						onAbilityKill(hero, ch);
					}
				}
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()), augment.damageFactor(max()));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0), max(0));
		}
	}

	public String upgradeAbilityStat(int level){
		return augment.damageFactor(min(level)) + "-" + augment.damageFactor(max(level));
	}
}
