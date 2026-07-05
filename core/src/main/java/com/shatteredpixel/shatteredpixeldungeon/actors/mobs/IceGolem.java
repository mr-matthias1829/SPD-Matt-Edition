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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.IceGolemSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class IceGolem extends Mob {

    private boolean skipNextTurn = false;

    {
        spriteClass = IceGolemSprite.class;

        HP = HT = 100;
        defenseSkill = 16;

        EXP = 0;
        maxLvl = 1;

        properties.add(Property.ICY);
        properties.add(Property.INORGANIC);

        baseSpeed = 1f;
    }

    @Override
    protected boolean act() {
        if (skipNextTurn) {
            skipNextTurn = false;
            spend(1f);
            return true;
        }
        return super.act();
    }

    @Override
    public void move(int step, boolean travelling) {
        super.move(step, travelling);

        if (Dungeon.level.map[step] == Terrain.DOOR
                || Dungeon.level.map[step] == Terrain.OPEN_DOOR) {
            if (Math.random() < 0.5) { // doorway costs extra, 50/50 for a full turn or most of a turn
                spend (1f);
            } else {
                spend(0.75f);
            }
        }
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Random.Int(3) == 0) {
            Buff.prolong(enemy, Chill.class, 4f);
        }

        skipNextTurn = true;
        return super.doAttack(enemy);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 36);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(2, 8);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        damage = damage / 2;

        return super.defenseProc(enemy, damage);
    }

    private static final String SKIP = "skip";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SKIP, skipNextTurn);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        skipNextTurn = bundle.getBoolean(SKIP);
    }
}