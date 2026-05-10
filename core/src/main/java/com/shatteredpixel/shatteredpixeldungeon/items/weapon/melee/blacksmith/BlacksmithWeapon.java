package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.blacksmith;

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
    public String statsInfo() {
        // fallback if subclasses don't define their own stats_desc
        return super.statsInfo();
    }
}