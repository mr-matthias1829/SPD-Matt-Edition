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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BurglarSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Burglar extends Thief {
	
	public Item item;
	
	{
		spriteClass = BurglarSprite.class;

        WANDERING = new Burglar.Wandering();
        FLEEING = new Burglar.Fleeing();
		
		HP = HT = 40;
		defenseSkill = 10;
        baseSpeed = 1f;

		maxLvl = 14; // up from 11 since this is technically post-tengu

		properties.add(Property.UNDEAD);
	}

	private static final String ITEM = "item";
    private int stealAttempts = 0;
    private static final String STEALATTEMPTS = "stealattempts";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
        bundle.put( STEALATTEMPTS, stealAttempts );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
        stealAttempts = bundle.getInt( STEALATTEMPTS );
	}

    @Override
    public float attackDelay() {
        return super.attackDelay()*1f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 6, 14 );
    } // same as bandit

    @Override
    public String description() {
        String desc = super.description();

        if (item != null) {
            if (item.stackable && item.quantity() > 1){
                desc += Messages.get(this, "carries_several", item.quantity(), item.name());
            } else {
                desc += Messages.get(this, "carries", item.name());
            }
        }

        return desc;
    }

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public void rollToDropLoot() {
		if (item != null) {
			Dungeon.level.drop( item, pos ).sprite.drop();
			//updates position
			if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).dropPot( this, pos );
			item = null;
		}
		super.rollToDropLoot();
	}

	@Override
	public int attackSkill( Char target ) {
		return 15; // up from 12
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
        // doesnt drop gold when hit unlike a lowly thief
		return super.defenseProc(enemy, damage);
	}

    @Override
    protected boolean steal( Hero hero ) {

        if (Random.Int(10) < stealAttempts+1) { // start at 10%, increase by 10%, and 100% by 9 prior attempts
            stealAttempts++;
            return false;
        }

        stealAttempts = 0; // Succeeded, reset counter

        Item toSteal;
        // has two chances to go for equipment instead of one... oh snap
        boolean StealEq = (Random.Int(100) > 70);
        if (!StealEq) StealEq = (Random.Int(100) > 70);

        if (StealEq) {
            toSteal = hero.belongings.randomEquipped();
            if (toSteal == null || toSteal.unique || toSteal.cursed) {
                toSteal = hero.belongings.randomUnequipped();
            }
        } else
        {
            toSteal = hero.belongings.randomUnequipped();
        }

        if (toSteal != null && !toSteal.unique && toSteal.visiblyUpgraded() <= 4) {

            // Check if the item is equipped and unequip it first
            if (hero.belongings.armor == toSteal) {
                hero.belongings.armor = null;
                ((HeroSprite)hero.sprite).updateArmor();
            } else if (hero.belongings.weapon == toSteal) {
                hero.belongings.weapon = null;
            } else if (hero.belongings.artifact == toSteal) {
                hero.belongings.artifact = null;
            } else if (hero.belongings.misc == toSteal) {
                hero.belongings.misc = null;
            } else if (hero.belongings.ring == toSteal) {
                hero.belongings.ring = null;
            }

            // Now detach from wherever it actually is
            if (toSteal.stackable) {
                // For stackable items, just take one
                int TQ = toSteal.quantity();

                // determine first how many to take
                int Q = (TQ > 1) ? Random.Int(1, TQ) : 1;
                // halve it if TQ > 5 AND we have over total half to keep it "fair"
                if (TQ > 5 && Q > TQ/3) Q = Q/3;
                // clamp safety because im paranoid of another crash
                Q = Math.max(1, Math.min(Q, TQ));
                //steal.
                Item stolen = toSteal.detach(hero.belongings.backpack);

                if (stolen != null) {
                    stolen.quantity(Q);

                    // reduce original stack
                    toSteal.quantity(TQ - Q);

                    if (toSteal.quantity() <= 0) {
                        toSteal.detach(hero.belongings.backpack);
                    }
                }

                item = stolen;
                if (toSteal.quantity() == 1 || toSteal.quantity() == 0){
                    GLog.w( Messages.get(Burglar.class, "stole", toSteal.name()) );
                } else {
                    GLog.w(Messages.get(Burglar.class, "stole_several", stolen.quantity(), toSteal.name()));
                }

            } else {
                // For non-stackable, take the whole item
                item = toSteal.detachAll(hero.belongings.backpack);
                GLog.w( Messages.get(Burglar.class, "stole", toSteal.name()) );
            }

            if (!toSteal.stackable) {
                Dungeon.quickslot.convertToPlaceholder(toSteal);
            }
            Item.updateQuickslot();

            if (item instanceof Honeypot){
                item = ((Honeypot)item).shatter(this, this.pos);
            } else if (item instanceof Honeypot.ShatteredPot) {
                ((Honeypot.ShatteredPot)item).pickupPot(this);
            }

            return true;
        } else {
            return false;
        }
    }


    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            super.act(enemyInFOV, justAlerted);

            //if an enemy is just noticed and the thief posses an item, run, don't fight.
            if (state == HUNTING && item != null){
                state = FLEEING;
            }

            return true;
        }
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void escaped() {
            if (item != null
                    && !Dungeon.level.heroFOV[pos]
                    && Dungeon.level.distance(Dungeon.hero.pos, pos) >= 9) { // up from 6

                int count = 32;
                int newPos;
                do {
                    newPos = Dungeon.level.randomRespawnCell( Burglar.this );
                    if (count-- <= 0) {
                        break;
                    }
                } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

                if (newPos != -1) {

                    pos = newPos;
                    sprite.place( pos );
                    sprite.visible = Dungeon.level.heroFOV[pos];
                    if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                }

                if (item != null){
                    if (item.stackable && item.quantity() > 1){
                        GLog.n( Messages.get(Burglar.class, "escapes_several", item.quantity(), item.name()));
                    } else {
                        GLog.n( Messages.get(Burglar.class, "escapes", item.name()));
                    }
                }


                item = null;
                state = WANDERING;
            } else {
                state = WANDERING;
            }
        }
    }
}
