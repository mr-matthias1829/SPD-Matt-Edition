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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Glock extends Weapon {

    public static final String AC_SHOOT = "SHOOT";
    public static final String AC_RELOAD = "RELOAD";

    private static final int MAX_AMMO = 6;

    {
        image = ItemSpriteSheet.GLOCK;

        defaultAction = AC_SHOOT;
        usesTargeting = true;

        unique = true;
        bones = false;
    }

    public int ammo = MAX_AMMO;

    private static final String AMMO = "ammo";

    @Override
    public int value() {
        return 50 * quantity;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(AMMO, ammo);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ammo = bundle.getInt(AMMO);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        // Visual indicator when low on ammo
        if (ammo <= 2 && ammo > 0) {
            return new ItemSprite.Glowing(0xFF8800, 0.3f); // Orange glow
        } else if (ammo == 0) {
            return new ItemSprite.Glowing(0xFF0000, 0.5f); // Red glow
        }
        return null;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_EQUIP);
        actions.add(AC_SHOOT);
        actions.add(AC_RELOAD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {

            if (ammo <= 0) {
                GLog.w(Messages.get(this, "no_ammo"));
                return;
            }

            curUser = hero;
            curItem = this;
            GameScene.selectCell(shooter);

        } else if (action.equals(AC_RELOAD)) {

            if (ammo >= MAX_AMMO) {
                GLog.w(Messages.get(this, "reload_full"));
                return;
            }

            int missingAmmo = MAX_AMMO - ammo;
            int reloadTime = 2 + missingAmmo;

            hero.spend(reloadTime);
            hero.busy();
            hero.sprite.operate(hero.pos);

            ammo = MAX_AMMO;
            updateQuickslot();

            GLog.i(Messages.get(this, "reload"));

        }
    }

    @Override
    public String status() {
        return ammo + "/" + MAX_AMMO;
    }

    @Override
    public String info() {
        String info = desc();

        info += "\n\n" + Messages.get(Glock.class, "stats",
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
                STRReq());

        info += "\n\n" + Messages.get(this, "ammo_status", ammo, MAX_AMMO);

        if (STRReq() > Dungeon.hero.STR()) {
            info += " " + Messages.get(Weapon.class, "too_heavy");
        } else if (Dungeon.hero.STR() > STRReq()){
            info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
        }

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
            if (enchantHardened) info += " " + Messages.get(Weapon.class, "enchant_hardened");
            info += " " + enchantment.desc();
        } else if (enchantHardened){
            info += "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant");
        }

        if (cursed && isEquipped(Dungeon.hero)) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

        return info;
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(4, lvl+1, getClass()); // tier 1
    }

    @Override
    public int min(int lvl) {
        // Fixed damage, not scaling with hero level
        int dmg = (int)(6 + lvl/2)
                + (curseInfusionBonus ? 1 : 0);
        return Math.max(0, dmg);
    }

    @Override
    public int max(int lvl) {
        // Fixed damage, not scaling with hero level
        int dmg = 15 + lvl
                + (curseInfusionBonus ? 2 : 0);
        return Math.max(0, dmg);
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockArrow().targetingPos(user, dst);
    }

    @Override
    public int damageRoll(Char owner) {
        int damage = augment.damageFactor(super.damageRoll(owner));

        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
        }

        return damage;
    }

    @Override
    public int buffedLvl() {
        // Level isn't affected by buffs/debuffs
        return level();
    }

    @Override
    public boolean isUpgradable() {
       // return false;
        return true;
    }

    public GlockBullet knockArrow(){
        return new GlockBullet();
    }

    public class GlockBullet extends MissileWeapon {

        {
            image = ItemSpriteSheet.GLOCK_BULLET;

            hitSound = Assets.Sounds.HIT_ARROW;

            setID = 0;
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public int damageRoll(Char owner) {
            return Glock.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return Glock.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return Glock.this.proc(attacker, defender, damage);
        }

        @Override
        public float delayFactor(Char user) {
            return Glock.this.delayFactor(user);
        }

        @Override
        public int STRReq(int lvl) {
            return Glock.this.STRReq();
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar(cell);
            if (enemy == null || enemy == curUser) {
                parent = null;
                Splash.at(cell, 0xCC99FFFF, 1);
            } else {
                if (!curUser.shoot(enemy, this)) {
                    Splash.at(cell, 0xCC99FFFF, 1);
                }
            }

            // Consume ammo after shot
            ammo--;
            updateQuickslot();
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play(Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f));
        }

        @Override
        public void cast(final Hero user, final int dst) {
            final int cell = throwPos(user, dst);

            user.sprite.zap(cell);
            user.busy();

            throwSound();

            Char enemy = Actor.findChar(cell);

            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                    reset(user.sprite,
                            cell,
                            this,
                            new Callback() {
                                @Override
                                public void call() {
                                    curUser = user;
                                    onThrow(cell);
                                    user.spendAndNext(castDelay(user, cell));
                                }
                            }, false);
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                knockArrow().cast(curUser, target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(Glock.class, "prompt");
        }
    };
}