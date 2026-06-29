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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM100FSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class DM100F extends DM100 {

    {
        spriteClass = DM100FSprite.class;
        HP = HT = 45; //15
        baseSpeed = 1f;

        defenseSkill = 12;

        loot = Generator.Category.SCROLL;
        lootChance = 0f;

        EXP = 7;
        maxLvl = 15;
        properties.add( Property.ICY );
        properties.add(Property.ELECTRIC);
        properties.add(Property.INORGANIC);
    }

    // custom zap delay (instance-based, not static)
    protected float TIME_TO_ZAP_151 = 1f / 2f;

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 12);
    }

    @Override
    public int attackSkill( Char target ) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
    }

    @Override
    protected boolean doAttack(Char enemy) {
        // If adjacent, try to move away
        if (Dungeon.level.adjacent(pos, enemy.pos)) {
            if (getFurther(enemy.pos)) {
                spend(1 / speed());
                return true;
            } else {
                spend(TICK);
                return true;
            }
        }

        // Otherwise, zap
        spend(TIME_TO_ZAP_151);
        Invisibility.dispel(this);

        if (hit(this, enemy, true)) {
            int dmg = damageRoll();
            dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
            enemy.damage(dmg, new LightningBolt());

            if (Random.Int(3) == 0) {
                Buff.prolong(enemy, Chill.class, 2f);
            }

            if (enemy.sprite.visible) {
                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                enemy.sprite.flash();
            }

            if (enemy == Dungeon.hero) {
                PixelScene.shake(2, 0.3f);
                if (!enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "zap_kill"));
                }
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }

        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap(enemy.pos);
            return false;
        } else {
            return true;
        }
    }
}
