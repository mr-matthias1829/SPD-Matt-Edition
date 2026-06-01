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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElderGnollSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ElderGnoll extends Mob {

    {
        spriteClass = ElderGnollSprite.class;

        HP = HT = 30;
        defenseSkill = 8;

        EXP = 12;
        maxLvl = 16;

        // IMMOVABLE: if he dies falling into a pit the key won't be reachable,
        // soft-locking the player from going deeper.
        properties.add(Property.IMMOVABLE);
    }

    // -------------------------------------------------------------------------
    // Flee timer
    // -------------------------------------------------------------------------

    private int fleeTimer = 0;

    private static final int FLEE_TIMER_ON_SIGHT  = 8;
    private static final int FLEE_TIMER_ON_DAMAGE = 6;

    // -------------------------------------------------------------------------
    // Push attack
    // -------------------------------------------------------------------------

    private int pushCooldown = 0;
    private static final int PUSH_COOLDOWN_MAX = 6;
    private static final int PUSH_DISTANCE     = 3;

    // =========================================================================
    // Core act loop — drive the flee timer, then hand off to super
    // =========================================================================

    @Override
    protected boolean act() {

        // Tick timers
        if (fleeTimer > 0) fleeTimer--;
        if (pushCooldown > 0) pushCooldown--;

        // Seeing the enemy resets the flee timer
        if (enemy != null && enemySeen) {
            fleeTimer = FLEE_TIMER_ON_SIGHT;
        }

        // Let the flee timer control the AI state directly.
        // The engine's built-in FLEEING state uses Dungeon.flee(), which handles
        // doors, tall grass, and all terrain correctly. We don't need to reimplement it.
        if (fleeTimer > 0) {
            state = FLEEING;
        } else if (state == FLEEING) {
            // Timer expired and we're still in FLEEING — return to HUNTING so
            // the mob behaves normally again (wandering, investigating, etc.)
            state = HUNTING;
        }

        return super.act();
    }

    // =========================================================================
    // Damage — ranged hits keep him running
    // =========================================================================

    @Override
    public int defenseProc(Char attacker, int damage) {
        fleeTimer = Math.max(fleeTimer, FLEE_TIMER_ON_DAMAGE);
        state = FLEEING; // immediately switch state so he runs this very turn
        return super.defenseProc(attacker, damage);
    }

    // =========================================================================
    // Combat AI — only fight when truly cornered
    // =========================================================================

    @Override
    protected boolean canAttack(Char enemy) {
        if (!Dungeon.level.adjacent(pos, enemy.pos)) return false;

        // While fleeing: only fight if there are no escape tiles at all
        if (fleeTimer > 0) {
            for (int i : PathFinder.NEIGHBOURS8) {
                int cell = pos + i;
                if (Dungeon.level.passable[cell]
                        && Actor.findChar(cell) == null
                        && Dungeon.level.distance(cell, enemy.pos) > Dungeon.level.distance(pos, enemy.pos)) {
                    return false; // escape route exists
                }
            }
        }

        return true;
    }

    // =========================================================================
    // Combat stats
    // =========================================================================

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(4, 8);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    // =========================================================================
    // Push attack
    // =========================================================================

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        if (pushCooldown == 0 && canPush(enemy)) {
            push(enemy);
            pushCooldown = PUSH_COOLDOWN_MAX;
            return damage / 2;
        }

        return damage;
    }

    private boolean canPush(Char enemy) {
        if (!Dungeon.level.adjacent(pos, enemy.pos)) return false;

        int dir = enemy.pos - pos;
        int cur = enemy.pos;

        for (int i = 0; i < PUSH_DISTANCE; i++) {
            int next = cur + dir;
            if (!Dungeon.level.passable[next] || Actor.findChar(next) != null) break;
            cur = next;
        }

        return cur != enemy.pos;
    }

    private void push(Char enemy) {
        int dir    = enemy.pos - pos;
        int newPos = enemy.pos;

        for (int i = 0; i < PUSH_DISTANCE; i++) {
            int next = newPos + dir;
            if (!Dungeon.level.passable[next] || Actor.findChar(next) != null) break;
            newPos = next;
        }

        if (newPos != enemy.pos) {
            int oldPos = enemy.pos;
            enemy.pos = newPos;
            Dungeon.level.occupyCell(enemy);
            Actor.addDelayed(new Pushing(enemy, oldPos, newPos), -1);
            enemy.sprite.move(oldPos, newPos);
            enemy.sprite.flash();
        }
    }

    // =========================================================================
    // Death
    // =========================================================================

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.drop(new WornKey(Dungeon.depth), pos).sprite.drop();
    }

    // =========================================================================
    // Persistence
    // =========================================================================

    private static final String KEY_FLEE_TIMER = "flee_timer";
    private static final String KEY_PUSH_CD    = "push_cooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(KEY_FLEE_TIMER, fleeTimer);
        bundle.put(KEY_PUSH_CD,   pushCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        fleeTimer    = bundle.getInt(KEY_FLEE_TIMER);
        pushCooldown = bundle.getInt(KEY_PUSH_CD);
    }
}