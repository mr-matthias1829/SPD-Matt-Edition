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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class GnollExileSprite extends MobSprite {

	public GnollExileSprite() {
		super();

		texture( Assets.Sprites.GNOLL );

		TextureFilm frames = new TextureFilm( texture, 12, 15 );

		int c = 21;

        idle = createAnimation("idle", 2, true);
		idle.frames( frames, 0+c, 0+c, 0+c, 1+c, 0+c, 0+c, 1+c, 1+c );

        run = createAnimation("run", 12, true);
		run.frames( frames, 4+c, 5+c, 6+c, 7+c );

        attack = createAnimation("attack", 12, false);
		attack.frames( frames, 2+c, 3+c, 0+c );

        die = createAnimation("die", 12, false);
        die.frames( frames, 8+c, 9+c, 10+c );

		play( idle );
	}

}
