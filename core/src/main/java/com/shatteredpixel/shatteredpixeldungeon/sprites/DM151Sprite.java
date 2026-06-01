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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;

public class DM151Sprite extends MobSprite {

	public DM151Sprite() {
		super();
		
		texture( Assets.Sprites.DM151 );
		
		TextureFilm frames = new TextureFilm( texture, 16, 14 );
		
		idle = createAnimation("idle", 1, true);
		idle.frames( frames, 0, 1 );

		run = createAnimation("run", 12, true);
		run.frames( frames, 6, 7, 8, 9 );
		
		attack = createAnimation("attack", 12, false);
		attack.frames( frames, 2, 3, 4, 0 );

		zap = createAnimation("zap", 8, false);
		zap.frames( frames, 5, 5, 1 );

		die = createAnimation("die", 12, false);
		die.frames( frames, 10, 11, 12, 13, 14, 15 );
		
		play( idle );
	}
	
	public void zap( int pos ) {

		Char enemy = Actor.findChar(pos);

		//shoot lightning from eye, not sprite center.
		PointF origin = center();
		if (flipHorizontal){
			origin.y -= 6*scale.y;
			origin.x -= 1*scale.x;
		} else {
			origin.y -= 8*scale.y;
			origin.x += 1*scale.x;
		}
		if (enemy != null) {
			parent.add(new Lightning(origin, enemy.sprite.destinationCenter(), (DM100) ch, true));
		} else {
			parent.add(new Lightning(origin, pos, (DM100) ch, true));
		}
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		
		super.zap( ch.pos );
		flash();
	}

	@Override
	public void die() {
		emitter().burst( Speck.factory( Speck.WOOL ), 5 );
		super.die();
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public int blood() {
		return 0x500000;
	}
}
