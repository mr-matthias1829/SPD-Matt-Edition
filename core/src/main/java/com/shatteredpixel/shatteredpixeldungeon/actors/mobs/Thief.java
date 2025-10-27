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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Thief extends Mob {
	
	public Item item;
	
	{
		spriteClass = ThiefSprite.class;
		
		HP = HT = 32; //25 //20
		defenseSkill = 12;
		
		EXP = 1; //5
		maxLvl = 11;

		loot = Random.oneOf(Generator.Category.RING, Generator.Category.ARTIFACT);
		lootChance = 0.03f; //initially, see lootChance()

		WANDERING = new Wandering();
		FLEEING = new Fleeing();
        setLevel(Dungeon.scalingDepth());

		properties.add(Property.UNDEAD);
	}

	private static final String ITEM = "item";
    private int stealAttempts = 0;
    private static final String STEALATTEMPTS = "stealattempts";
    public void setLevel( int depth ){
        int lvl = 0;
        if (depth < 5) {
            lvl = 0;
        } else {
            lvl = 2;
        }
        this.level = lvl;
        adjustStats(level);
    }
    public void adjustStats( int level ) {
        if (level >= 5){
            baseSpeed = 0.9f;
        }
        else{
            baseSpeed = 0.8f; //0.75
        }
    }

    private int level;
    private static final String LEVEL	= "level";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
        bundle.put( STEALATTEMPTS, stealAttempts );
        bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
        stealAttempts = bundle.getInt( STEALATTEMPTS );
        level = bundle.getInt( LEVEL );
        adjustStats(level);
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 12 );
	} //1,10

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.THEIF_MISC.count);
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
	public Item createLoot() {
		Dungeon.LimitedDrops.THEIF_MISC.count++;
		return super.createLoot();
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 3);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (alignment == Alignment.ENEMY && item == null
				&& enemy instanceof Hero && steal( (Hero)enemy )) {
			state = FLEEING;
		}

		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) {
			Dungeon.level.drop( new Gold(), pos ).sprite.drop();
		}

		return super.defenseProc(enemy, damage);
	}

    protected boolean steal( Hero hero ) {

        if (Random.Int(8) < stealAttempts+1) {
            stealAttempts++;
            return false;
        }

        stealAttempts = 0; // Succeeded, reset counter

        Item toSteal;
        if (Random.Int(10) > 6) { // 40%
            toSteal = hero.belongings.randomEquipped();
            if (toSteal == null || toSteal.unique || toSteal.visiblyUpgraded() > 4) {
                toSteal = hero.belongings.randomUnequipped();
            }
        } else
        {
            toSteal = hero.belongings.randomUnequipped();
        }

        if (toSteal != null && !toSteal.unique && toSteal.visiblyUpgraded() <= 4) {

            GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );

            // FIX: Check if the item is equipped and unequip it first
            if (hero.belongings.armor == toSteal) {
                hero.belongings.armor = null;
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
                item = toSteal.detach(hero.belongings.backpack);
            } else {
                // For non-stackable, take the whole item
                item = toSteal.detachAll(hero.belongings.backpack);
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

	@Override
	public String description() {
		String desc = super.description();

		if (item != null) {
			desc += Messages.get(this, "carries", item.name() );
		}

		return desc;
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
					&& Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

				int count = 32;
				int newPos;
				do {
					newPos = Dungeon.level.randomRespawnCell( Thief.this );
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

				if (item != null) GLog.n( Messages.get(Thief.class, "escapes", item.name()));
				item = null;
				state = WANDERING;
			} else {
				state = WANDERING;
			}
		}
	}
}
