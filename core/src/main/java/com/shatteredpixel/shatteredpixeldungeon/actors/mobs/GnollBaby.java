/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollBabySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GnollBaby extends Mob {

    private boolean screamed = false;

    {
        spriteClass = GnollBabySprite.class;

        HP = HT = 8; //5
        defenseSkill = 24; //16

        EXP = 0; // don't give xp for... ethical reasons, for the few that care
        maxLvl = 1;

        viewDistance = 3;
    }

    @Override
    public void notice() {
        super.notice();

        if (!screamed && enemy == Dungeon.hero && Dungeon.level.heroFOV[pos]) {
            screamed = true;

            GLog.w("The gnoll baby screams loudly, echoing throughout the floor!");
            this.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 9 );
            Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);

            for (Mob mob : Dungeon.level.mobs) {
                mob.beckon(Dungeon.hero.pos);
            }

            spend(1f); // spend a whole turn just screaming
        }
    }

    @Override
    public int damageRoll() {
        int baseDmg = Random.NormalIntRange(1, 5);

        // Add enemy's DR to (mostly) ignore it (only if enemy exists)
        if (enemy != null) {
            baseDmg += enemy.drRoll();
        }

        return baseDmg;
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }
}

