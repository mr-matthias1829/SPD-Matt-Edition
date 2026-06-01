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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class Fireball extends MovieClip {

    private static boolean second = false;

    // Rainbow control
    private boolean rainbow = true;
    private float hue = 0f;         // 0..1
    private float hueSpeed = 0.005f; // tune this to speed up/down

    // default: keeps old behavior but enables rainbow tint
    public Fireball() {
        this(second);
        second = !second;
    }

    // main ctor (keep behavior identical to original)
    public Fireball(boolean second) {

        if (PixelScene.landscape()){
            texture( "effects/fireball-tall.png" );
            TextureFilm frames = new TextureFilm( texture, 61, 61 );
            MovieClip.Animation anim = new MovieClip.Animation( 24, true );
            anim.frames( frames,
                    0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 );
            play( anim );
        } else {
            texture( "effects/fireball-short.png" );
            TextureFilm frames = new TextureFilm( texture, 47, 47 );
            MovieClip.Animation anim = new MovieClip.Animation( 24, true );
            anim.frames( frames,
                    0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 );
            play( anim );
        }

        // second fireball is flipped and has its animation offset
        if (second){
            flipHorizontal = true;
            curFrame = 12;
            frame( curAnim.frames[curFrame] );
            // optionally set opposite starting hue to give contrasting effect:
            hue = 0.5f; // offset hue by half a cycle
        }

        // start with a neutral tint (so old default look preserved until update runs)
        // setColor using normalized floats 0..1
        // Visual provides color(r,g,b) method — use that to tint.
        color(1f, 1f, 1f);
    }

    @Override
    public void update() {
        super.update();

        if (!rainbow) return;

        // advance hue
        hue += hueSpeed;
        if (hue > 1f) hue -= 1f;

        // convert HSB -> RGB (returns floats 0..1)
        float[] rgb = hsbToRgb(hue, 1f, 1f);

        // apply tint. Visual/ MovieClip should expose color(float r, float g, float b)
        // If your engine exposes 'tint' instead, replace with: tint(rgb[0], rgb[1], rgb[2]);
        color(rgb[0], rgb[1], rgb[2]);
    }

    // small HSB->RGB converter returning floats 0..1 (avoids needing java.awt.Color)
    private float[] hsbToRgb(float h, float s, float v) {
        if (s == 0f) {
            return new float[]{v, v, v};
        }
        h = (h % 1f) * 6f;               // sector 0..5
        int i = (int)Math.floor(h);
        float f = h - i;
        float p = v * (1f - s);
        float q = v * (1f - s * f);
        float t = v * (1f - s * (1f - f));
        switch(i) {
            case 0: return new float[]{v, t, p};
            case 1: return new float[]{q, v, p};
            case 2: return new float[]{p, v, t};
            case 3: return new float[]{p, q, v};
            case 4: return new float[]{t, p, v};
            default: return new float[]{v, p, q};
        }
    }

    // optional helpers you can call from elsewhere:
    public void setRainbow(boolean enabled){
        this.rainbow = enabled;
        if (!enabled) resetColor(); // reset tint to default
    }

    public void setHueSpeed(float speed){
        this.hueSpeed = speed;
    }
}
