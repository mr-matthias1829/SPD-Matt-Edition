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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class DarkKingSprite extends MobSprite {

	public DarkKingSprite() {
		super();
		
		texture( Assets.Sprites.DARKKING );
		
		TextureFilm frames = new TextureFilm( texture, 16, 16 );
		
		idle = createAnimation("idle", 12, true, true, true);
		idle.frames( frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2 );
		
		run = createAnimation("run", 15, true, true, true);
		run.frames( frames, 3, 4, 5, 6, 7, 8 );
		
		attack = createAnimation("attack", 15, false, true, true);
		attack.frames( frames, 9, 10, 11 );
		
		die = createAnimation("die", 8, false, true, true);
		die.frames( frames, 12, 13, 14, 15 );
		
		play( idle );
	}
}
