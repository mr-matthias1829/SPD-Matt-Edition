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

// ArmorAugment.java
// place in: items/armor/augment/ArmorAugment.java
package com.shatteredpixel.shatteredpixeldungeon.items.armor.augments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.HashSet;

public abstract class ArmorAugment implements Bundlable {

    public int evasionFactor(int level) {
        return 0;
    }

    public int defenseFactor(int level) {
        return 0;
    }

    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        return damage;
    }

    public String name() {
        return Messages.get(this, "name");
    }

    public String desc() {
        return Messages.get(this, "desc");
    }

    public String statModificationInfo() {
        return "";
    }

    public ItemSprite.Glowing glowing() {
        return null;
    }

    public HashSet<Char.Property> augmentProperties(Hero hero) {
        return new HashSet<>();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {}

    @Override
    public void storeInBundle(Bundle bundle) {}
}