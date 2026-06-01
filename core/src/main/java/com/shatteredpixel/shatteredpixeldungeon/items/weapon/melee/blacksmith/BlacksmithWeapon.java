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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;

/**
 * Base class for unique blacksmith-only weapons.
 *
 * These weapons:
 * - inherit all normal melee weapon behavior
 * - share a fixed tier
 * - are intended to be unique rewards only
 */
public abstract class BlacksmithWeapon extends MeleeWeapon {

    {
        // all blacksmith weapons use the same tier
        tier = 3;

        // these are intended as unique items
        unique = true;

        // optional: usually these should start identified
        levelKnown = true;
        cursedKnown = true;
    }

    @Override
    public int value() {
        return 125;
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public int max(int lvl) {
        return  3*(tier+1) +    // 3*4 = 12 base
                lvl*(tier);   // +3 per level, at lvl 6: 30 max
    }

    @Override
    public String statsInfo() {
        // fallback if subclasses don't define their own stats_desc
        return super.statsInfo();
    }


    public void callBeforeAbilityUsed(Hero hero, Char target) {
        beforeAbilityUsed(hero, target);
    }

    public void callAfterAbilityUsed(Hero hero) {
        afterAbilityUsed(hero);
    }
}