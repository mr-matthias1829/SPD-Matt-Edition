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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.augments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class IcyCoreWeapon extends WeaponAugment {

    private static final float CHILL_CHANCE = 0.12f; // higher than armor since armor averagely procs more
    private static final int CHILL_DURATION = 2;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (defender != null && Dungeon.level.adjacent(defender.pos, attacker.pos)) {
            if (Random.Float() < CHILL_CHANCE) {
                int add = weapon.level() / 5; // every 5 levels on a weapon adds 1 turn to chilling
                Buff.affect(defender, Chill.class, CHILL_DURATION+add);
            }
        }
        return damage;
    }

    /*
    @Override
    public HashSet<Char.Property> augmentProperties(Hero hero) {
        HashSet<Char.Property> props = new HashSet<>();
        props.add(Char.Property.ICY);
        return props;
    }
     */

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing(0x88CCFF);
    }
}