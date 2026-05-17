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