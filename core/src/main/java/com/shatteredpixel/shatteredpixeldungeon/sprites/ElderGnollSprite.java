package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class ElderGnollSprite extends MobSprite {

        private Animation cast;

        public ElderGnollSprite() {
            super();

            texture( Assets.Sprites.GNOLL2 );

            TextureFilm frames = new TextureFilm( texture, 12, 15 );

            int c = 42;

            idle = createAnimation("idle", 2, true);
            idle.frames( frames, 0+c, 0+c, 0+c, 1+c, 0+c, 0+c, 1+c, 1+c );

            run = createAnimation("run", 12, true);
            run.frames( frames, 4+c, 5+c, 6+c, 7+c );

            attack = createAnimation("attack", 12, false);
            attack.frames( frames, 2+c, 3+c, 0+c );

            cast = createAnimation("cast", 12, false);
            cast.frames( frames, 2+c, 3+c, 0+c );

            die = createAnimation("die", 12, false);
            die.frames( frames, 8+c, 9+c, 10+c );

            play( idle );
        }

        @Override
        public void attack( int cell ) {
            if (!Dungeon.level.adjacent(cell, ch.pos)) {

                ((MissileSprite) parent.recycle(MissileSprite.class)).
                        reset(this, cell, new ParalyticDart(), new Callback() {
                            @Override
                            public void call() {
                                ch.onAttackComplete();
                            }
                        }, true);

                play(cast);
                turnTo(ch.pos, cell);

            } else {

                super.attack(cell);

            }
        }

}
