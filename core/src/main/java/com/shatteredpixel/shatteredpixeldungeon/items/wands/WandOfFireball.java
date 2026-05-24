package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfFireball extends WandOfExplosion {
    // what would happen if you combine fire with explosions?
    // yeah you get fireballs
    // TL:DR: its wand of explosion but the explosion also casts fire on the tiles
    // would reduce damage, because utility + damage is busted, but nahhhh
    {
        image = ItemSpriteSheet.WAND_OF_FIREBALL; // different sprite
    }

    @Override
    public void onZap(Ballistica bolt) {
        super.onZap(bolt); // handles all the AoE damage, radius scaling, hero check

        // Then layer fire on top
        int radius = blastRadius(buffedLvl());
        for (int cell : cellsInBlast(bolt.collisionPos, radius)) {
            GameScene.add(Blob.seed(cell, 4, Fire.class));
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.FIRE,     // already fire, but now the trail fits too
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xFF4400);
        particle.am = 0.6f;
        particle.setLifespan(2f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.5f);
        particle.setSize(1f, 3f);
        particle.radiateXY(2f);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        float procChance = (buffedLvl() + 1f) / (buffedLvl() + 7f) * procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {

            BlastWave.blast(defender.pos, 3);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);

            for (int cell : cellsInBlast(defender.pos, 1)) {
                if (cell == defender.pos) continue;
                if (cell == attacker.pos) continue;

                Char splash = Actor.findChar(cell);
                if (splash == null || splash == attacker) continue;

                splash.damage(damageRoll() / 3, this);

                // Seed fire only on cells where an enemy was actually hit,
                // and never under the attacker.
               // GameScene.add(Blob.seed(cell, 4, Fire.class));
               // Buff.affect(splash, Burning.class).reignite(splash);
            }

        }
    }



    public static class RecipeWand extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs    = new Class[]{ WandOfFireblast.class, WandOfExplosion.class, ArcaneResin.class };
            inQuantity = new int[]{ 1, 1, 1 };
            cost      = 20;
            output    = WandOfFireball.class;
            outQuantity = 1;
        }
    }
}