/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicInfuse;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfPureMagic extends ExoticScroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_PUREMG;
    }

    @Override
    public void doRead() {
        if (curUser.buff(MagicImmune.class) != null) {
            // we act nice here and actually save their scroll from being used
            // i might be evil, but never tell me i never did anything good for yall
            GLog.w( Messages.get(this, "no_magic") );
            return;
        }

        detach(curUser.belongings.backpack);
        Buff.affect( curUser, MagicInfuse.class, MagicInfuse.DURATION );
        new Flare( 5, 32 ).color( 0x00FF00, true ).show( curUser.sprite, 2f );

        identify();

        readAnimation();
    }
}
