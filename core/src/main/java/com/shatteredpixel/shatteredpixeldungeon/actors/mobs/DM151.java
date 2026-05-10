package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM151Sprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class DM151 extends DM100 implements Callback {

    {
        spriteClass = DM151Sprite.class;
        HP = HT = 24; //15
        baseSpeed = 1f;
        lootChance = 0.15f; //0.25f

        WANDERING = new DM151.Wandering();
        state = WANDERING;
    }

    // custom zap delay (instance-based, not static)
    protected float TIME_TO_ZAP_151 = 1f / 2.25f; //3f

    @Override
    protected boolean doAttack(Char enemy) {
        // If adjacent, try to move away
        if (Dungeon.level.adjacent(pos, enemy.pos)) {
            if (getFurther(enemy.pos)) {
                spend(1 / speed());
                return true;
            } else {
                spend(TICK);
                return true;
            }
        }

        // Otherwise, zap
        spend(TIME_TO_ZAP_151);
        Invisibility.dispel(this);

        if (hit(this, enemy, true)) {
            int dmg = damageRoll();
            dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
            enemy.damage(dmg, new LightningBolt());

            if (enemy.sprite.visible) {
                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                enemy.sprite.flash();
            }

            if (enemy == Dungeon.hero) {
                PixelScene.shake(2, 0.3f);
                if (!enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "zap_kill"));
                }
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }

        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap(enemy.pos);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Badges.validateModProgression("dm151");
        if (Dungeon.depth < 6) Badges.validateModProgression("dm151_sewer");
    }


    public class Wandering extends Mob.Wandering {
        /* just moves to a random direction
        @Override
        protected int randomDestination() {
                return super.randomDestination();
        }
        */

        @Override
        protected int randomDestination() {
            //of two potential wander positions, picks the one closest to the hero
            // this "actively" seeks out the hero in a way
            int pos1 = super.randomDestination();
            int pos2 = super.randomDestination();
            PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
            if (PathFinder.distance[pos2] < PathFinder.distance[pos1]){
                return pos2;
            } else {
                return pos1;
            }
        }
    }
}
