/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfExplosion extends DamageWand {

    {
        image = ItemSpriteSheet.WAND_EXPLOSION;
        collisionProperties = Ballistica.PROJECTILE;
    }
    @Override
    public int min(int lvl) {
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 8 + 2 * lvl;
    }

    /**
     * Returns the Chebyshev blast radius for the given wand level.
     * radius 1 → 3×3, radius 2 → 5×5, radius 3 → 7×7, …
     */
    public int blastRadius(int lvl) {
        return 1 + lvl / 4;
    }

    /**
     * Collects every map cell within blastRadius(lvl) of {@code center},
     * skipping cells that fall outside the map bounds.
     */
    protected ArrayList<Integer> cellsInBlast(int center, int radius) {
        int w = Dungeon.level.width();
        int h = Dungeon.level.height();
        int cx = center % w;
        int cy = center / w;

        ArrayList<Integer> cells = new ArrayList<>();
        // LOS checks are only valid when casting from an open tile.
        // If the bolt hits something solid (e.g. a closed door) we skip LOS
        // filtering — the surrounding walls still block via the solid check below.
        boolean doLOS = !Dungeon.level.solid[center];

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = cx + dx;
                int ny = cy + dy;
                if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;

                int cell = ny * w + nx;

                // The blast center always counts, regardless of what terrain is there.
                if (cell == center) {
                    cells.add(cell);
                    continue;
                }

                // Never affect solid wall tiles.
                if (Dungeon.level.solid[cell]) continue;

                // When casting from an open tile, verify the blast can actually
                // reach this cell — a wall between center and cell blocks it.
                if (doLOS) {
                    Ballistica los = new Ballistica(center, cell, Ballistica.STOP_SOLID);
                    if (los.collisionPos != cell) continue;
                }

                cells.add(cell);
            }
        }
        return cells;
    }

    // =========================================================================
    // Zap — AoE damage, no push, no status effects
    // =========================================================================

    @Override
    public void onZap(Ballistica bolt) {
        int radius = blastRadius(buffedLvl());

        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        // Visual blast ring sized to match the actual affected area.
        BlastWave.blast(bolt.collisionPos, (radius * 2 + 1));

        for (int cell : cellsInBlast(bolt.collisionPos, radius)) {
            Char ch = Actor.findChar(cell);
            if (ch == null) continue;

            wandProc(ch, chargesPerCast());
            ch.damage(damageRoll(), this);

            if (ch == Dungeon.hero && ch.isAlive()) {
                Dungeon.observe();
                GameScene.updateFog();
            }
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        float procChance = (buffedLvl() + 1f) / (buffedLvl() + 6f) * procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {

            BlastWave.blast(defender.pos, 3); // always radius-1 visually (3×3 ring)
            Sample.INSTANCE.play(Assets.Sounds.BLAST);

            for (int cell : cellsInBlast(defender.pos, 1)) {
                if (cell == defender.pos) continue; // primary target is not hit twice
                if (cell == attacker.pos) continue; // don't hit the wielder

                Char splash = Actor.findChar(cell);
                if (splash == null || splash == attacker) continue;

                // Half a damage roll for each bystander hit by the shockwave.
                splash.damage(damageRoll() / 3, this);
            }

        }
    }

    // =========================================================================
    // Tooltip stats
    // =========================================================================

    @Override
    public String upgradeStat2(int level) {
        int r = blastRadius(level);
        int side = r * 2 + 1;
        return side + "×" + side;
    }

    // =========================================================================
    // Visuals
    // =========================================================================

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(
                curUser.sprite.parent,
                MagicMissile.FIRE,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x664422);
        particle.am = 0.6f;
        particle.setLifespan(3f);
        particle.speed.polar(Random.Float(PointF.PI2), 0.3f);
        particle.setSize(1f, 2f);
        particle.radiateXY(2.5f);
    }

    // =========================================================================
    // BlastWave visual effect
    //
    // Kept as a static inner class so other effects (traps, potions, etc.)
    // can reuse it via WandOfExplosion.BlastWave.blast(pos).
    // =========================================================================

    public static class BlastWave extends Image {

        private static final float TIME_TO_FADE = 0.2f;

        private float time;
        private float size;

        public BlastWave() {
            super(Effects.get(Effects.Type.RIPPLE));
            origin.set(width / 2, height / 2);
        }

        public void reset(int pos, float size) {
            revive();

            x = (pos % Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - width) / 2f;
            y = (pos / Dungeon.level.width()) * DungeonTilemap.SIZE + (DungeonTilemap.SIZE - height) / 2f;

            resetColor();
            scale.set(0);

            time = TIME_TO_FADE;
            this.size = size;
        }

        @Override
        public void update() {
            super.update();

            if ((time -= Game.elapsed) <= 0) {
                kill();
            } else {
                float p = time / TIME_TO_FADE;
                alpha(p);
                scale.y = scale.x = (1 - p) * size;
            }
        }

        /** Convenience overload — radius-1 (3×3) blast at {@code pos}. */
        public static void blast(int pos) {
            blast(pos, 3);
        }

        public static void blast(int pos, float visualSize) {
            blast(pos, visualSize, -1);
        }

        public static void blast(int pos, float visualSize, int hardLight) {
            Group parent = Dungeon.hero.sprite.parent;
            BlastWave b = (BlastWave) parent.recycle(BlastWave.class);
            parent.bringToFront(b);
            b.reset(pos, visualSize);
            if (hardLight != -1) {
                b.hardlight(hardLight);
            }
        }
    }
}