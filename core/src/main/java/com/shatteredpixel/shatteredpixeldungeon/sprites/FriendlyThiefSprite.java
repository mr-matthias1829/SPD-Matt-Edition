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

public class FriendlyThiefSprite extends MobSprite {

	public FriendlyThiefSprite() {
		super();
		
		texture( Assets.Sprites.THIEF2 );
		TextureFilm film = new TextureFilm( texture, 12, 13 );

        idle = createAnimation("idle", 2, true);
		idle.frames( film, 0, 1);

        run = createAnimation("run", 15, true);
		run.frames( film, 0, 0, 2, 3, 3, 4 );

        die = createAnimation("die", 10, false);
		die.frames( film, 5, 6, 7, 8, 9 );

        attack = createAnimation("attack", 12, false);
		attack.frames( film, 10, 11, 12, 0 );
		
		idle();
	}
}
