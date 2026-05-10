/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfElements extends DamageWand {
    // huuggeeee wand class
    // effectively just threw the code of the effects of the 3 wands in here
    // is it a mess? yes. does it work though? absolutely.
    // i can imagine this having a crash case though, so don't expect this code to be clean or "safe"
    {
        image = ItemSpriteSheet.WAND_OF_ELEMENTS;
    }

    @Override
    public int initialCharges() {
        return 4;
    }

    // Damage ranges will vary based on which effect activates
    @Override
    public int min(int lvl) {
        // Return average of all wands' minimum damage
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        // Return average of all wands' maximum damage
        return 12 + 3 * lvl;
    }




    // Tracks which effect was last used for fx
    private EffectType lastEffect = null;
    private Ballistica currentBolt = null;

    private enum EffectType {
        LIGHTNING, FROST, FIREBLAST
    }

    // ===================== LIGHTNING EFFECT =====================
    private ArrayList<Char> affected = new ArrayList<>();
    private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    // ===================== FIREBLAST EFFECT =====================
    private ConeAOE cone;

    @Override
    public void onZap(Ballistica bolt) {
        currentBolt = bolt;

        if (lastEffect == null) {
            // Shouldn't happen, but just in case
            lastEffect = Random.element(EffectType.values());
        }

        switch (lastEffect) {
            case LIGHTNING:
                lightningEffect(bolt);
                break;
            case FROST:
                frostEffect(bolt);
                break;
            case FIREBLAST:
                // The cone should already be created in fx() method
                if (cone != null) {
                    fireblastEffect(bolt);
                } else {
                    // Fallback to lightning if cone wasn't created properly
                    lightningEffect(bolt);
                }
                break;
        }

        // Reset for next use
        lastEffect = null;
        currentBolt = null;
        cone = null;
    }

    private void lightningEffect(Ballistica bolt) {
        affected.clear();
        arcs.clear();

        for (Char ch : affected.toArray(new Char[0])) {
            if (ch != curUser && ch.alignment == curUser.alignment && ch.pos != bolt.collisionPos) {
                affected.remove(ch);
            } else if (ch.buff(LightningCharge.class) != null) {
                affected.remove(ch);
            }
        }

        int cell = bolt.collisionPos;
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            if (ch instanceof DwarfKing) {
                Statistics.qualifiedForBossChallengeBadge = false;
            }
            affected.add(ch);
            arcs.add(new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
            arc(ch);
        } else {
            arcs.add(new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
            CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
        }

        // Lightning deals less damage per-target, the more targets that are hit.
        float multiplier = 0.4f + (0.6f / affected.size());
        // If the main target is in water, all affected take full damage
        if (Dungeon.level.water[bolt.collisionPos]) multiplier = 1f;

        for (Char target : affected) {
            wandProc(target, chargesPerCast());
            if (target == curUser && target.isAlive()) {
                target.damage(Math.round(damageRoll() * multiplier * 0.5f), this);
                if (!curUser.isAlive()) {
                    Badges.validateDeathFromFriendlyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "ondeath"));
                }
            } else {
                target.damage(Math.round(damageRoll() * multiplier), this);
            }

            if (target == Dungeon.hero) PixelScene.shake(2, 0.3f);
            target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
            target.sprite.flash();
        }
    }

    private void arc(Char ch) {
        int dist = Dungeon.level.water[ch.pos] ? 2 : 1;
        if (curUser.buff(LightningCharge.class) != null) {
            dist++;
        }

        ArrayList<Char> hitThisArc = new ArrayList<>();
        PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level.solid, null), dist);
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char n = Actor.findChar(i);
                if (n == Dungeon.hero && PathFinder.distance[i] > 1)
                    // The hero is only zapped if they are adjacent
                    continue;
                else if (n != null && !affected.contains(n)) {
                    hitThisArc.add(n);
                }
            }
        }

        affected.addAll(hitThisArc);
        for (Char hit : hitThisArc) {
            arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
            arc(hit);
        }
    }

    // ===================== FROST EFFECT =====================
    private void frostEffect(Ballistica bolt) {
        Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
        if (heap != null) {
            heap.freeze();
        }

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
        if (fire != null && fire.volume > 0) {
            fire.clear(bolt.collisionPos);
        }

        MagicalFireRoom.EternalFire eternalFire = (MagicalFireRoom.EternalFire) Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class);
        if (eternalFire != null && eternalFire.volume > 0) {
            eternalFire.clear(bolt.collisionPos);
            // Bolt ends 1 tile short of fire, so check next tile too
            if (bolt.path.size() > bolt.dist + 1) {
                eternalFire.clear(bolt.path.get(bolt.dist + 1));
            }
        }

        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            int damage = damageRoll();

            if (ch.buff(Frost.class) != null) {
                return; // Do nothing, can't affect a frozen target
            }
            if (ch.buff(Chill.class) != null) {
                // 6.67% less damage per turn of chill remaining, to a max of 10 turns (50% dmg)
                float chillturns = Math.min(10, ch.buff(Chill.class).cooldown());
                damage = (int) Math.round(damage * Math.pow(0.9333f, chillturns));
            } else {
                ch.sprite.burst(0xFF99CCFF, buffedLvl() / 2 + 2);
            }

            wandProc(ch, chargesPerCast());
            ch.damage(damage, this);
            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, 1.1f * Random.Float(0.87f, 1.15f));

            if (ch.isAlive()) {
                if (Dungeon.level.water[ch.pos])
                    Buff.affect(ch, Chill.class, 4 + buffedLvl());
                else
                    Buff.affect(ch, Chill.class, 2 + buffedLvl());
            }
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    // ===================== FIREBLAST EFFECT =====================
    private void fireblastEffect(Ballistica bolt) {
        if (cone == null) {
            // Create cone if it wasn't created in fx()
            int maxDist = 3 + 2 * chargesPerCast();
            cone = new ConeAOE(bolt,
                    maxDist,
                    30 + 20 * chargesPerCast(),
                    Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
        }

        ArrayList<Char> affectedChars = new ArrayList<>();
        ArrayList<Integer> adjacentCells = new ArrayList<>();

        for (int cell : cone.cells) {
            // Ignore caster cell
            if (cell == bolt.sourcePos) {
                continue;
            }

            // Knock doors open
            if (Dungeon.level.map[cell] == Terrain.DOOR) {
                Level.set(cell, Terrain.OPEN_DOOR);
                GameScene.updateMap(cell);
            }

            // Only ignite cells directly near caster if they are flammable or solid
            if (Dungeon.level.adjacent(bolt.sourcePos, cell)
                    && !(Dungeon.level.flamable[cell] || Dungeon.level.solid[cell])) {
                adjacentCells.add(cell);
                // Do burn any heaps located here though
                if (Dungeon.level.heaps.get(cell) != null) {
                    Dungeon.level.heaps.get(cell).burn();
                }
            } else {
                GameScene.add(Blob.seed(cell, 1 + chargesPerCast(), Fire.class));
            }

            Char ch = Actor.findChar(cell);
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        // If wand was shot right at a wall
        if (cone.cells.isEmpty()) {
            adjacentCells.add(bolt.sourcePos);
        }

        // Ignite cells that share a side with an adjacent cell, are flammable, and are closer to the collision pos
        // This prevents short-range casts not igniting barricades or bookshelves
        for (int cell : adjacentCells) {
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.trueDistance(cell + i, bolt.collisionPos) < Dungeon.level.trueDistance(cell, bolt.collisionPos)
                        && Dungeon.level.flamable[cell + i]
                        && Fire.volumeAt(cell + i, Fire.class) == 0) {
                    GameScene.add(Blob.seed(cell + i, 1 + chargesPerCast(), Fire.class));
                }
            }
        }

        for (Char ch : affectedChars) {
            wandProc(ch, chargesPerCast());
            ch.damage(damageRoll(), this);
            if (ch.isAlive()) {
                Buff.affect(ch, Burning.class).reignite(ch);
                switch (chargesPerCast()) {
                    case 1:
                        break; // No effects
                    case 2:
                        Buff.affect(ch, Cripple.class, 4f);
                        break;
                    case 3:
                        Buff.affect(ch, Paralysis.class, 4f);
                        break;
                }
            }
        }
    }

    // ===================== FX METHODS =====================
    @Override
    public void fx(final Ballistica bolt, final Callback callback) {
        // Randomly select which effect to use
        lastEffect = Random.element(EffectType.values());

        switch (lastEffect) {
            case LIGHTNING:
                // Need to set up affected and arcs for lightning
                affected.clear();
                arcs.clear();

                int cell = bolt.collisionPos;
                Char ch = Actor.findChar(cell);
                if (ch != null) {
                    affected.add(ch);
                    arcs.add(new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
                    arc(ch);
                } else {
                    arcs.add(new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
                }

                curUser.sprite.parent.addToFront(new Lightning(arcs, new Callback() {
                    @Override
                    public void call() {
                        callback.call();
                    }
                }));
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                // Don't call callback here, let lightning animation handle it
                break;

            case FROST:
                MagicMissile.boltFromChar(curUser.sprite.parent,
                        MagicMissile.FROST,
                        curUser.sprite,
                        bolt.collisionPos,
                        callback);
                Sample.INSTANCE.play(Assets.Sounds.ZAP);
                break;

            case FIREBLAST:
                // Need to create cone here for the visual effect
                int maxDist = 3 + 2 * chargesPerCast();
                cone = new ConeAOE(bolt,
                        maxDist,
                        30 + 20 * chargesPerCast(),
                        Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

                // Cast to cells at the tip, rather than all cells, better performance.
                Ballistica longestRay = null;
                for (Ballistica ray : cone.outerRays) {
                    if (longestRay == null || ray.dist > longestRay.dist) {
                        longestRay = ray;
                    }
                    ((MagicMissile) curUser.sprite.parent.recycle(MagicMissile.class)).reset(
                            MagicMissile.FIRE_CONE,
                            curUser.sprite,
                            ray.path.get(ray.dist),
                            null
                    );
                }

                if (longestRay == null) {
                    callback.call();
                    return;
                }

                // Final zap at half distance of the longest ray, for timing of the actual wand effect
                MagicMissile.boltFromChar(curUser.sprite.parent,
                        MagicMissile.FIRE_CONE,
                        curUser.sprite,
                        longestRay.path.get(longestRay.dist / 2),
                        callback);
                Sample.INSTANCE.play(Assets.Sounds.ZAP);
                Sample.INSTANCE.play(Assets.Sounds.BURNING);
                break;
        }
    }

    // ===================== STAFF HIT EFFECTS =====================
    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        // Randomly choose one of the three wand's on-hit effects
        int choice = Random.Int(3);

        switch (choice) {
            case 0: // Lightning
                // lvl 0 - 25%
                // lvl 1 - 40%
                // lvl 2 - 50%
                float procChance = (buffedLvl() + 1f) / (buffedLvl() + 4f) * procChanceMultiplier(attacker);
                if (Random.Float() < procChance) {
                    float powerMulti = Math.min(1f, procChance);
                    FlavourBuff.prolong(attacker, LightningCharge.class, powerMulti * LightningCharge.DURATION);
                    attacker.sprite.centerEmitter().burst(SparkParticle.FACTORY, 10);
                    attacker.sprite.flash();
                    Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                }
                break;

            case 1: // Frost
                Chill chill = defender.buff(Chill.class);
                if (chill != null) {
                    // 1/9 at 2 turns of chill, scaling to 9/9 at 10 turns
                    float procChanceFrost = ((int) Math.floor(chill.cooldown()) - 1) / 9f;
                    procChanceFrost *= procChanceMultiplier(attacker);

                    if (Random.Float() < procChanceFrost) {
                        float powerMulti = Math.max(1f, procChanceFrost);
                        // Need to delay this through an actor so that the freezing isn't broken by taking damage from the staff hit.
                        new FlavourBuff() {
                            {
                                actPriority = VFX_PRIO;
                            }

                            public boolean act() {
                                Buff.affect(target, Frost.class, Math.round(Frost.DURATION * powerMulti));
                                return super.act();
                            }
                        }.attachTo(defender);
                    }
                }
                break;

            case 2: // Fireblast
                // Proc chance is initially 0..
                float procChanceFire = 0;
                for (int i : PathFinder.NEIGHBOURS9) {
                    // +25% proc chance per burning char within 3x3 of target
                    // This includes the attacker and defender
                    if (Actor.findChar(defender.pos + i) != null
                            && Actor.findChar(defender.pos + i).buff(Burning.class) != null) {
                        procChanceFire += 0.25f;
                        // Otherwise +5% proc chance per burning tile within 3x3 of target
                    } else if (Fire.volumeAt(defender.pos + i, Fire.class) > 0) {
                        procChanceFire += 0.05f;
                    }
                }

                procChanceFire = Math.min(1f, procChanceFire);
                procChanceFire *= Wand.procChanceMultiplier(attacker);

                if (Random.Float() < procChanceFire) {
                    float powerMulti = Math.max(1f, procChanceFire);
                    Blob fire = Dungeon.level.blobs.get(Fire.class);

                    // Explode, dealing damage to enemies in 3x3, and clearing all fire
                    CellEmitter.center(defender.pos).burst(BlastParticle.FACTORY, 30);
                    if (fire != null) {
                        for (int i : PathFinder.NEIGHBOURS9) {
                            CellEmitter.get(defender.pos + i).burst(SmokeParticle.FACTORY, 4);
                            if (Fire.volumeAt(defender.pos + i, Fire.class) > 0) {
                                Dungeon.level.destroy(defender.pos + i);
                                GameScene.updateMap(defender.pos + i);
                                fire.clear(defender.pos + i);
                            }

                            Char ch = Actor.findChar(defender.pos + i);
                            if (ch != null) {
                                if (ch.buff(Burning.class) != null) {
                                    ch.buff(Burning.class).detach();
                                }
                                if (ch.alignment == Char.Alignment.ENEMY) {
                                    // Damage of a 2-charge zap
                                    ch.damage(Math.round(powerMulti * Random.NormalIntRange(2 + 2 * buffedLvl(), 8 + 4 * buffedLvl())), this);
                                }
                            }
                        }
                    }
                    Sample.INSTANCE.play(Assets.Sounds.BLAST);
                }
                break;
        }
    }

    // ===================== CHARGE CONSUMPTION =====================
    @Override
    protected int chargesPerCast() {
        // Average charge consumption across wands
        return 1;
    }

    // ===================== STAFF FX =====================
    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        // Random particle effect based on which wand effect we might use
        int choice = Random.Int(3);

        switch (choice) {
            case 0: // Lightning
                particle.color(0xFFFFFF);
                particle.am = 0.6f;
                particle.setLifespan(0.6f);
                particle.acc.set(0, +10);
                particle.speed.polar(-Random.Float(3.1415926f), 6f);
                particle.setSize(0f, 1.5f);
                particle.sizeJitter = 1f;
                particle.shuffleXY(1f);
                float dst = Random.Float(1f);
                particle.x -= dst;
                particle.y += dst;
                break;

            case 1: // Frost
                particle.color(0x88CCFF);
                particle.am = 0.6f;
                particle.setLifespan(2f);
                float angle = Random.Float(PointF.PI2);
                particle.speed.polar(angle, 2f);
                particle.acc.set(0f, 1f);
                particle.setSize(0f, 1.5f);
                particle.radiateXY(Random.Float(1f));
                break;

            case 2: // Fireblast
                particle.color(0xEE7722);
                particle.am = 0.5f;
                particle.setLifespan(0.6f);
                particle.acc.set(0, -40);
                particle.setSize(0f, 3f);
                particle.shuffleXY(1.5f);
                break;
        }
    }

    // ===================== LIGHTNING CHARGE BUFF =====================
    public static class LightningCharge extends FlavourBuff {
        {
            type = buffType.POSITIVE;
        }

        public static float DURATION = 10f;

        @Override
        public int icon() {
            return BuffIndicator.IMBUE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1, 1, 0);
        }
    }





    private static int AlchemyCost = 36;

    public static class RecipeFireFrost extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs = new Class[]{
                    WandOfFireblast.class,
                    WandOfFrost.class
            };
            inQuantity = new int[]{1, 1};

            cost = AlchemyCost;

            output = WandOfElements.class;
            outQuantity = 1;
        }
    }

    public static class RecipeFrostLightning extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs = new Class[]{
                    WandOfFrost.class,
                    WandOfLightning.class
            };
            inQuantity = new int[]{1, 1};

            cost = AlchemyCost;

            output = WandOfElements.class;
            outQuantity = 1;
        }
    }

    public static class RecipeFireLightning extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs = new Class[]{
                    WandOfFireblast.class,
                    WandOfLightning.class
            };
            inQuantity = new int[]{1, 1};

            cost = AlchemyCost;

            output = WandOfElements.class;
            outQuantity = 1;
        }
    }
}