package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElderGnollSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ElderGnoll extends Mob {

    {
        spriteClass = ElderGnollSprite.class;

        HP = HT = 16; //25
        defenseSkill = 8;

        EXP = 12;
        maxLvl = 16;

        // can't be pushed.
        // this is secretly super important. if this mob dies by falling into a pit,
        // it WILL drop the key, but it won't be retrievable by the hero, soft-locking the player from going any deeper.
        // there's certainly better ways to solve this, but like... no? SURELY you can live with this one random enemy NOT being pushable?
        properties.add(Property.IMMOVABLE);
    }

    private int pushCooldown = 0;
    private static final int PUSH_COOLDOWN_MAX = 6;
    private static final int PUSH_DISTANCE = 3;
    private static final int PREFERRED_DISTANCE = 5; // Try to stay this far from hero

    private int lastSeenHeroPos = -1;

    @Override
    protected boolean act() {
        if (pushCooldown > 0) {
            pushCooldown--;
        }

        // Update last known hero position if we can see them
        if (enemy != null && enemySeen) {
            lastSeenHeroPos = enemy.pos;
        }

        return super.act();
    }

    @Override
    protected boolean getCloser(int target) {
        // If we haven't seen the hero yet, wander normally
        if (lastSeenHeroPos == -1) {
            return super.getCloser(target);
        }

        int distance = Dungeon.level.distance(pos, lastSeenHeroPos);

        // If too close (< 5 tiles), flee
        if (distance < PREFERRED_DISTANCE) {
            return getFurther(lastSeenHeroPos);
        }
        // If too far (> 5 tiles), approach
        /*
        else if (distance > PREFERRED_DISTANCE) {
            return super.getCloser(lastSeenHeroPos);
        }
         */
        // If at ideal distance, just wander/stay put
        else {
            return true; // Stay in place
        }
    }

    @Override
    protected boolean canAttack(Char enemy) {
        // Only attack if adjacent AND we can't flee anywhere
        if (!Dungeon.level.adjacent(pos, enemy.pos)) {
            return false;
        }

        // Check if we have any valid escape tiles
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = pos + i;
            if (Dungeon.level.passable[cell]
                    && Actor.findChar(cell) == null
                    && Dungeon.level.distance(cell, enemy.pos) > Dungeon.level.distance(pos, enemy.pos)) {
                // Found a tile that gets us further away - don't attack, flee instead
                return false;
            }
        }

        // Cornered - no escape routes, so attack
        return true;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(4, 8);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    @Override
    public void die( Object cause ) {

        super.die( cause );

        // Drops the key that the hero needs to enter the ice caves
        Dungeon.level.drop( new WornKey( Dungeon.depth ), pos ).sprite.drop();
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);

        // Try to push if cooldown is ready and push is possible
        if (pushCooldown == 0 && canPush(enemy)) {
            push(enemy);
            pushCooldown = PUSH_COOLDOWN_MAX;
            return damage / 2; // reduced damage when pushing
        }

        // If can't push (cooldown or cornered), just deal normal damage
        return damage;
    }

    private boolean canPush(Char enemy) {
        if (!Dungeon.level.adjacent(pos, enemy.pos)) {
            return false;
        }

        int dir = enemy.pos - pos;
        int cur = enemy.pos;

        // Check if we can push at least 1 tile
        for (int i = 0; i < PUSH_DISTANCE; i++) {
            int next = cur + dir;
            if (!Dungeon.level.passable[next] || Actor.findChar(next) != null) {
                break;
            }
            cur = next;
        }

        return cur != enemy.pos; // Can push if final position is different
    }

    private void push(Char enemy) {
        int dir = enemy.pos - pos;
        int newPos = enemy.pos;

        // Find how far we can actually push
        for (int i = 0; i < PUSH_DISTANCE; i++) {
            int next = newPos + dir;
            if (!Dungeon.level.passable[next] || Actor.findChar(next) != null) {
                break;
            }
            newPos = next;
        }

        if (newPos != enemy.pos) {
            int oldPos = enemy.pos;

            // Actually move the character in game logic
            enemy.pos = newPos;
            Dungeon.level.occupyCell(enemy);

            // Visual effects
            Actor.addDelayed(new Pushing(enemy, oldPos, newPos), -1);
            enemy.sprite.move(oldPos, newPos);
            enemy.sprite.flash();
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("push_cooldown", pushCooldown);
        bundle.put("last_seen_pos", lastSeenHeroPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pushCooldown = bundle.getInt("push_cooldown");
        lastSeenHeroPos = bundle.getInt("last_seen_pos");
    }
}