package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.watabou.utils.Random;

public class ScrollOfMagicUpgrade extends InventoryScroll {

    {
        icon = ItemSpriteSheet.Icons.SCROLL_ENCHANT;
        preferredBag = Belongings.Backpack.class;
        unique = true;
        talentFactor = 2f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Armor || item instanceof Wand;
    }

    @Override
    protected void onItemSelected( Item item ) {
        GameScene.show(new WndUpgrade(this, item, identifiedByUse));
    }

    public void reShowSelector(boolean force){
        identifiedByUse = force;
        curItem = this;
        GameScene.selectItem(itemSelector);
    }

    // This method actually performs the magic upgrade
    public Item upgradeItem( Item item ){
       // magicUpgrade( curUser );

        Degrade.detach( curUser, Degrade.class );

        //logic for telling the user when item properties change from upgrades
        //...yes this is rather messy
        if (item instanceof Weapon){
            Weapon w = (Weapon) item;
            boolean wasCursed = w.cursed;
            boolean wasHardened = w.enchantHardened;
            boolean hadCursedEnchant = w.hasCurseEnchant();
            boolean hadGoodEnchant = w.hasGoodEnchant();

            item = w.upgrade();

            if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()){
                removeCurse( Dungeon.hero );
            } else if (w.cursedKnown && wasCursed && !w.cursed){
                weakenCurse( Dungeon.hero );
            }
            if (wasHardened && !w.enchantHardened){
                GLog.w( Messages.get(Weapon.class, "hardening_gone") );
            } else if (hadGoodEnchant && !w.hasGoodEnchant()){
                GLog.w( Messages.get(Weapon.class, "incompatible") );
            }

        } else if (item instanceof Armor){
            Armor a = (Armor) item;
            boolean wasCursed = a.cursed;
            boolean wasHardened = a.glyphHardened;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();

            item = a.upgrade();

            int uncurseChance = 4 + a.visiblyUpgraded();
            int weakenChance = 2 + a.visiblyUpgraded();

            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph() && Random.Int(uncurseChance) == 0){
                removeCurse( Dungeon.hero );
            } else if (a.cursedKnown && wasCursed && !a.cursed && Random.Int(weakenChance) == 0){
                weakenCurse( Dungeon.hero );
            }
            if (wasHardened && !a.glyphHardened){
                GLog.w( Messages.get(Armor.class, "hardening_gone") );
            } else if (hadGoodGlyph && !a.hasGoodGlyph()){
                GLog.w( Messages.get(Armor.class, "incompatible") );
            }

        } else if (item instanceof Wand || item instanceof Ring) {
            boolean wasCursed = item.cursed;

            item = item.upgrade();

            if (item.cursedKnown && wasCursed && !item.cursed){
                removeCurse( Dungeon.hero );
            }

        } else {
            item = item.upgrade();
        }

        Badges.validateItemLevelAquired( item );
        Statistics.upgradesUsed++;
        Badges.validateMageUnlock();

        Catalog.countUse(item.getClass());

        return item;
    }

    public static void magicUpgrade( Hero hero ) {
        hero.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
    }

    public static void weakenCurse( Hero hero ){
        GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
        hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );
    }

    public static void removeCurse( Hero hero ){
        GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
        hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
        Badges.validateClericUnlock();
    }

    @Override
    public int value() {
        return isKnown() ? 50 * quantity : super.value();
    }

    @Override
    public int energyVal() {
        return isKnown() ? 5 * quantity : super.energyVal();
    }
}