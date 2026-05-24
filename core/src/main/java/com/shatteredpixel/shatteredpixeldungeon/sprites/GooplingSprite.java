package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;


public class GooplingSprite extends MobSprite {

    public GooplingSprite() {
        super();

        texture(Assets.Sprites.SLIME);

        TextureFilm frames = new TextureFilm(texture, 14, 12);

        int offset = 9;

        idle = createAnimation("idle", 3, true);
        idle.frames(frames, offset + 0, offset + 1, offset + 1, offset + 0);

        run = createAnimation("run", 10, true);
        run.frames(frames, offset + 0, offset + 2, offset + 3, offset + 3, offset + 2, offset + 0);

        attack = createAnimation("attack", 15, false);
        attack.frames(frames, offset + 2, offset + 3, offset + 4, offset + 6, offset + 5);

        die = createAnimation("die", 10, false);
        die.frames(frames, offset + 0, offset + 5, offset + 6, offset + 7);

        play(idle);
    }

    @Override
    public int blood() {
        return 0xFF000000; // maybe black or dark purple
    }
}
