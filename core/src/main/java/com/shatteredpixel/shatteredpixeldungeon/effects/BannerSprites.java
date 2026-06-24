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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Image;

public class BannerSprites {

	public enum  Type {
		TITLE_PORT,
		TITLE_GLOW_PORT,
		TITLE_LAND,
		TITLE_GLOW_LAND,
		BOSS_SLAIN,
		GAME_OVER,
	}

    public static Image get(Type type) {
        switch (type) {

            case TITLE_PORT:
                return new Image(Assets.Interfaces.TITLE_PORT);

            case TITLE_LAND:
                return new Image(Assets.Interfaces.TITLE_LAND);

            case BOSS_SLAIN:
                return new Image(Assets.Interfaces.BOSS_SLAIN);

            case GAME_OVER:
                return new Image(Assets.Interfaces.GAME_OVER);

                // legacy logic for glows
                // ... mostly because their positioning with cuts is... weird
            case TITLE_GLOW_PORT:
                Image GP = new Image(Assets.Interfaces.BANNERS);
                GP.frame( GP.texture.uvRect( 139, 0, 278, 100 ) );
                return GP;

            case TITLE_GLOW_LAND:
                Image GL = new Image(Assets.Interfaces.BANNERS);
                GL.frame( GL.texture.uvRect( 240, 100, 480, 157 ) );
                return GL;
        }

        return null;
    }
}
