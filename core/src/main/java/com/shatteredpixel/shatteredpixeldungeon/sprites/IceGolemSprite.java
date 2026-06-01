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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class IceGolemSprite extends MobSprite {

	public IceGolemSprite() {
		super();
		
		texture( Assets.Sprites.ICEGOLEM );
		
		TextureFilm frames = new TextureFilm( texture, 17, 19 );
		
		idle = createAnimation("idle", 2, true);
        idle.frames( frames, 0, 0, 0, 0, 0, 1, 1, 1);
		
		run = createAnimation("run", 10, true);
		run.frames( frames, 2, 3, 4, 5 );
		
		attack = createAnimation("attack", 8, false);
		attack.frames( frames, 6, 7 );

		zap = createAnimation("zap", 8, false);
		zap.frames( frames, 6, 7 );

		die = createAnimation("die", 8, false);
		die.frames( frames, 9, 9, 10, 11, 12, 13 );
		
		play( idle );
	}

	@Override
	public int blood() {
		return 0xFFD8F4FF;
	}
}
