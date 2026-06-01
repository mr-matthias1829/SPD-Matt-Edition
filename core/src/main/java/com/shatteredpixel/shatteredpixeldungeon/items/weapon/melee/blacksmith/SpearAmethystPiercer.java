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
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class SpearAmethystPiercer extends BlacksmithWeapon {

    {
        image = ItemSpriteSheet.AMETHYST_SPEAR;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.9f;

        DLY = 1.5f;
        RCH = 2;
    }

    // by original designs, this was already by far the most complex unique weapon yet
    // even then, most unique weapons were actually implemented differently and more simple
    // in the same way, this weapon got simplified a lot, yet remains the most complex
    // the idea remains the same, but now requires alot less brain power to properly use
    // yet as a result: a bit much config variables to mess with
    private int focusedEnemyID = -1;
    private float damageMulti = 1f;
    private int penaltyTurnsLeft = 0;

    private static final float STACK_RATE       = 0.075f; // +7.5% per hit
    private static final float STACK_RATE_BOSS  = 0.04f;  // +4% per hit vs bosses
    private static final float MAX_DAMAGE_MULTI = 1.5f;   // cap at +50% so it stays within reason

    private static final float PENALTY_MULTI    = 0.7f;   // -30% on switch
    private static final float KILL_MULTI       = 0.85f;  // -15% on kill
    private static final int   PENALTY_TURNS    = 5;

    private static final String FOCUSED_ID      = "focused_enemy_id";
    private static final String DAMAGE_MULTI    = "damage_multi";
    private static final String PENALTY_TURNS_L = "penalty_turns_left";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FOCUSED_ID, focusedEnemyID);
        bundle.put(DAMAGE_MULTI, damageMulti);
        bundle.put(PENALTY_TURNS_L, penaltyTurnsLeft);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        focusedEnemyID = bundle.getInt(FOCUSED_ID);
        damageMulti = bundle.getFloat(DAMAGE_MULTI);
        penaltyTurnsLeft = bundle.getInt(PENALTY_TURNS_L);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {

        // tick down penalty duration
        if (penaltyTurnsLeft > 0) {
            penaltyTurnsLeft--;
            if (penaltyTurnsLeft <= 0) {
                damageMulti = 1f;
            }
        }

        // target died -> apply reduced penalty
        if (!defender.isAlive()) {
            applyKillPenalty();
            return super.proc(attacker, defender, damage);
        }

        // first target ever: no penalty
        if (focusedEnemyID == -1) {
            focusedEnemyID = defender.id();
            damageMulti = Math.max(damageMulti, 1f);
        }

        // same target -> stack
        else if (defender.id() == focusedEnemyID) {

            float rate =
                    (defender.properties().contains(Char.Property.BOSS)
                            || defender.properties().contains(Char.Property.MINIBOSS))
                            ? STACK_RATE_BOSS
                            : STACK_RATE;

            damageMulti = Math.min(MAX_DAMAGE_MULTI, damageMulti + rate);

        }

        // switched target -> full penalty
        else {
            applySwitchPenalty();
            focusedEnemyID = defender.id();
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public int min(int lvl) {
        return Math.round(super.min(lvl) * damageMulti);
    }

    @Override
    public int max(int lvl) {
        return Math.round(super.max(lvl) * damageMulti);
    }

    private void applySwitchPenalty() {
        focusedEnemyID = -1;
        damageMulti = PENALTY_MULTI;
        penaltyTurnsLeft = PENALTY_TURNS;
    }

    private void applyKillPenalty() {
        focusedEnemyID = -1;
        damageMulti = KILL_MULTI;
        penaltyTurnsLeft = PENALTY_TURNS;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = augment.damageFactor(9 + Math.round(2f * buffedLvl()));
        SpearAmethystPiercer.spikeAbility(hero, target, 1, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 9 + Math.round(2f * buffedLvl()) : 9;

        if (levelKnown) {
            return Messages.get(
                    this,
                    "ability_desc",
                    augment.damageFactor(min() + dmgBoost),
                    augment.damageFactor(max() + dmgBoost)
            );
        } else {
            return Messages.get(
                    this,
                    "typical_ability_desc",
                    min(0) + dmgBoost,
                    max(0) + dmgBoost
            );
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 9 + Math.round(2f * level);
        return augment.damageFactor(min(level) + dmgBoost)
                + "-"
                + augment.damageFactor(max(level) + dmgBoost);
    }

    public static void spikeAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep) {
        if (target == null) return;

        Char enemy = Actor.findChar(target);

        if (enemy == null
                || enemy == hero
                || hero.isCharmedBy(enemy)
                || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = wep;

        if (!hero.canAttack(enemy) || Dungeon.level.adjacent(hero.pos, enemy.pos)) {
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
                int oldPos = enemy.pos;

                if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {

                    if (enemy.isAlive()
                            && enemy.pos == oldPos
                            && !Pushing.pushingExistsForChar(enemy)) {

                        Ballistica trajectory =
                                new Ballistica(hero.pos, enemy.pos, Ballistica.STOP_TARGET);

                        trajectory =
                                new Ballistica(
                                        trajectory.collisionPos,
                                        trajectory.path.get(trajectory.path.size() - 1),
                                        Ballistica.PROJECTILE
                                );

                        WandOfBlastWave.throwChar(enemy, trajectory, 1, true, false, hero);

                    } else if (!enemy.isAlive()) {
                        wep.onAbilityKill(hero, enemy);
                    }

                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());

                ((BlacksmithWeapon) wep).callAfterAbilityUsed(hero);
            }
        });
    }
}