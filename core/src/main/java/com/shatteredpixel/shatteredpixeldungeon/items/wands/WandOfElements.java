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
import com.watabou.utils.Bundle;
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
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 12 + 3 * lvl;
    }


    // ===================== ELEMENT SEQUENCE =====================
    // A fixed, randomly-ordered cycle of the three effects, created when
    // the wand is first used and saved/loaded with the item from that point on.

    private enum EffectType {
        LIGHTNING, FROST, FIREBLAST
    }

    private static final String SEQUENCE       = "sequence";
    private static final String SEQUENCE_INDEX = "sequence_index";

    private EffectType[] sequence      = null;   // null until first use
    private int          sequenceIndex = 0;

    /** Returns the wand's unique fixed order (e.g. FROST → FIREBLAST → LIGHTNING). */
    private void initSequence() {
        // Fisher-Yates shuffle over the three values
        EffectType[] values = EffectType.values();
        for (int i = values.length - 1; i > 0; i--) {
            int j = Random.Int(i + 1);
            EffectType tmp = values[i];
            values[i] = values[j];
            values[j] = tmp;
        }
        sequence      = values;
        sequenceIndex = 0;
    }

    /** The element that will fire on the next cast. */
    private EffectType currentEffect() {
        if (sequence == null) initSequence();
        return sequence[sequenceIndex % sequence.length];
    }

    /** Step to the next element in the cycle. */
    private void advanceSequence() {
        if (sequence == null) initSequence();
        sequenceIndex = (sequenceIndex + 1) % sequence.length;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (sequence != null) {
            // Store as comma-separated names so it survives enum order changes
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sequence.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(sequence[i].name());
            }
            bundle.put(SEQUENCE, sb.toString());
        }
        bundle.put(SEQUENCE_INDEX, sequenceIndex);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(SEQUENCE)) {
            String raw = bundle.getString(SEQUENCE);
            String[] parts = raw.split(",");
            sequence = new EffectType[parts.length];
            for (int i = 0; i < parts.length; i++) {
                sequence[i] = EffectType.valueOf(parts[i].trim());
            }
        }
        sequenceIndex = bundle.getInt(SEQUENCE_INDEX);
    }


    // Tracks which effect was last used (shared between fx() and onZap())
    private EffectType lastEffect  = null;
    private Ballistica currentBolt = null;

    // ===================== LIGHTNING EFFECT =====================
    private ArrayList<Char>          affected = new ArrayList<>();
    private ArrayList<Lightning.Arc> arcs     = new ArrayList<>();

    // ===================== FIREBLAST EFFECT =====================
    private ConeAOE cone;

    @Override
    public void onZap(Ballistica bolt) {
        currentBolt = bolt;

        if (lastEffect == null) {
            lastEffect = currentEffect();
        }

        switch (lastEffect) {
            case LIGHTNING:
                lightningEffect(bolt);
                break;
            case FROST:
                frostEffect(bolt);
                break;
            case FIREBLAST:
                if (cone != null) {
                    fireblastEffect(bolt);
                } else {
                    lightningEffect(bolt);
                }
                break;
        }

        // Advance the cycle
        advanceSequence();

        // Reset per-cast state
        lastEffect  = null;
        currentBolt = null;
        cone        = null;
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

        float multiplier = 0.4f + (0.6f / affected.size());
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
            if (bolt.path.size() > bolt.dist + 1) {
                eternalFire.clear(bolt.path.get(bolt.dist + 1));
            }
        }

        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            int damage = damageRoll();

            if (ch.buff(Frost.class) != null) {
                return;
            }
            if (ch.buff(Chill.class) != null) {
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
            int maxDist = 3 + 2 * chargesPerCast();
            cone = new ConeAOE(bolt,
                    maxDist,
                    30 + 20 * chargesPerCast(),
                    Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
        }

        ArrayList<Char>    affectedChars  = new ArrayList<>();
        ArrayList<Integer> adjacentCells  = new ArrayList<>();

        for (int cell : cone.cells) {
            if (cell == bolt.sourcePos) continue;

            if (Dungeon.level.map[cell] == Terrain.DOOR) {
                Level.set(cell, Terrain.OPEN_DOOR);
                GameScene.updateMap(cell);
            }

            if (Dungeon.level.adjacent(bolt.sourcePos, cell)
                    && !(Dungeon.level.flamable[cell] || Dungeon.level.solid[cell])) {
                adjacentCells.add(cell);
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

        if (cone.cells.isEmpty()) {
            adjacentCells.add(bolt.sourcePos);
        }

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
                    case 1: break;
                    case 2: Buff.affect(ch, Cripple.class, 4f);    break;
                    case 3: Buff.affect(ch, Paralysis.class, 4f);  break;
                }
            }
        }
    }

    // ===================== FX METHODS =====================
    @Override
    public void fx(final Ballistica bolt, final Callback callback) {
        // Use the sequence, not a random pick
        lastEffect = currentEffect();

        switch (lastEffect) {
            case LIGHTNING:
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
                }, false));
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
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
                int maxDist = 3 + 2 * chargesPerCast();
                cone = new ConeAOE(bolt,
                        maxDist,
                        30 + 20 * chargesPerCast(),
                        Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

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

    // ===================== BATTLEMAGE EFFECT =====================
    // Peeks at the next element in the sequence (does NOT advance it).
    // All three cases share the same proc chance formula as WandOfLightning:
    //   lvl 0 → ~25%, lvl 1 → ~40%, lvl 2 → ~50%
    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        EffectType next = currentEffect(); // peek only — sequence does not advance on hit

        float procChance = (buffedLvl() + 1f) / (buffedLvl() + 4f) * procChanceMultiplier(attacker);
        if (Random.Float() >= procChance) return;

        switch (next) {

            case FIREBLAST:
                // Ignite the enemy for a short 1–2 turns.
                Buff.affect(defender, Burning.class).reignite(defender, 1f + Random.Int(2));
                break;

            case FROST:
                if (defender.buff(Chill.class) != null) {
                    // Already chilled: consume the chill and freeze for 2 turns.
                    // Delayed through a FlavourBuff so the freeze isn't immediately
                    // broken by the staff hit damage (same pattern as WandOfFrost.onHit).
                    defender.buff(Chill.class).detach();
                    new FlavourBuff() {
                        {
                            actPriority = VFX_PRIO;
                        }
                        public boolean act() {
                            Buff.affect(target, Frost.class, 2f);
                            return super.act();
                        }
                    }.attachTo(defender);
                } else {
                    // Not yet chilled: apply chill for 3 turns.
                    Buff.affect(defender, Chill.class, 3f);
                    defender.sprite.burst(0xFF99CCFF, 3);
                }
                break;

            case LIGHTNING:
                // Same as WandOfLightning.onHit but half the charge duration.
                float power = Math.min(1f, procChance);
                FlavourBuff.prolong(attacker, LightningCharge.class, power * LightningCharge.DURATION * 0.5f);
                attacker.sprite.centerEmitter().burst(SparkParticle.FACTORY, 10);
                attacker.sprite.flash();
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                break;
        }
    }

    // ===================== CHARGE CONSUMPTION =====================
    @Override
    protected int chargesPerCast() {
        return 1;
    }

    @Override
    public String desc() {
        String base = super.desc();
        if (sequence != null) {
            // Only show next-cast info once the sequence has been initialised
            // (i.e. after the first use), so unidentified wands stay clean.
            switch (currentEffect()) {
                case FIREBLAST:
                    base += "\n\n" + Messages.get(this, "desc_next_fire");
                    break;
                case FROST:
                    base += "\n\n" + Messages.get(this, "desc_next_frost");
                    break;
                case LIGHTNING:
                    base += "\n\n" + Messages.get(this, "desc_next_lightning");
                    break;
            }
        }
        return base;
    }

    // ===================== STAFF FX =====================
    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        // Colour the particle based on whichever element comes next,
        // giving a subtle visual hint about the upcoming cast.
        EffectType next = currentEffect();

        switch (next) {
            case LIGHTNING:
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

            case FROST:
                particle.color(0x88CCFF);
                particle.am = 0.6f;
                particle.setLifespan(2f);
                float angle = Random.Float(PointF.PI2);
                particle.speed.polar(angle, 2f);
                particle.acc.set(0f, 1f);
                particle.setSize(0f, 1.5f);
                particle.radiateXY(Random.Float(1f));
                break;

            case FIREBLAST:
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


    // ===================== ALCHEMY RECIPES =====================
    private static int AlchemyCost = 30;

    public static class RecipeFireFrost extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs    = new Class[]{ WandOfFireblast.class, WandOfFrost.class };
            inQuantity = new int[]{ 1, 1 };
            cost      = AlchemyCost;
            output    = WandOfElements.class;
            outQuantity = 1;
        }
    }

    public static class RecipeFrostLightning extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs    = new Class[]{ WandOfFrost.class, WandOfLightning.class };
            inQuantity = new int[]{ 1, 1 };
            cost      = AlchemyCost;
            output    = WandOfElements.class;
            outQuantity = 1;
        }
    }

    public static class RecipeFireLightning extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
        {
            inputs    = new Class[]{ WandOfFireblast.class, WandOfLightning.class };
            inQuantity = new int[]{ 1, 1 };
            cost      = AlchemyCost;
            output    = WandOfElements.class;
            outQuantity = 1;
        }
    }
}