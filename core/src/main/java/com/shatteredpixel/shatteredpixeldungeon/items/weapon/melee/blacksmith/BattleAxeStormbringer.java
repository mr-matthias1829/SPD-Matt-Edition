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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BattleAxeStormbringer extends BlacksmithWeapon {

	{
		image = ItemSpriteSheet.STORMBRINGER_BATTLE_AXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;
	}

    // "but why is the lightning damage so loowwwwwww"
    // dude, this can chain technically onto a infinite amount of enemies
    // thats why its damage is as low as is. as compensation, its considered magic damage
    // also matt dum. he thought no hammer existed in vanilla pd so just took the battle axe
    // little did he know, that vanilla has a battle hammer :)
    // so lore-wise? a hammer. code/game-wise? a battle axe.

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        damage = super.proc(attacker, defender, damage);

        if (attacker instanceof Hero) {
            ArrayList<Char> affected = new ArrayList<>();
            ArrayList<Lightning.Arc> arcs = new ArrayList<>();

            affected.add(defender); // defender already hit, don't hit again

            // try to arc outward from defender
            arcLightning(defender, affected, arcs, Math.round(damage * 0.33f), attacker);

            // only fire if something was actually hit by the ricochet
            if (arcs.size() > 0) {
                attacker.sprite.parent.addToFront(new Lightning(arcs, null, false));
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
            }
        }

        return damage;
    }

    private void arcLightning(Char from, ArrayList<Char> affected, ArrayList<Lightning.Arc> arcs, int dmg, Char attacker) {
        PathFinder.buildDistanceMap(from.pos, BArray.not(Dungeon.level.solid, null), 2);

        ArrayList<Char> hitThisArc = new ArrayList<>();
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch != null && ch != attacker && !affected.contains(ch)
                        && ch.alignment == Char.Alignment.ENEMY) {
                    hitThisArc.add(ch);
                }
            }
        }

        affected.addAll(hitThisArc);
        for (Char ch : hitThisArc) {
            arcs.add(new Lightning.Arc(from.sprite.center(), ch.sprite.center()));
            ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            ch.sprite.flash();
            ch.damage(dmg, new Electricity());
            arcLightning(ch, affected, arcs, dmg, attacker); // ricochet further
        }
    }

    public static class Electricity {}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		//+(5+1.5*lvl) damage, roughly +40% base dmg, +50% scaling
		int dmgBoost = augment.damageFactor(5 + Math.round(1.5f*buffedLvl()));
		Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = levelKnown ? 5 + Math.round(1.5f*buffedLvl()) : 5;
		if (levelKnown){
			return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
		} else {
			return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 5 + Math.round(1.5f*level);
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
