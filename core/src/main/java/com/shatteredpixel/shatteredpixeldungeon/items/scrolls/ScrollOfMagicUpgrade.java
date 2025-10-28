package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
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
    public void upgradeItem( Item item ){

        Degrade.detach( curUser, Degrade.class );

        if (item instanceof Armor) {
            Armor a = (Armor) item;
            boolean wasCursed = a.cursed;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();

            // Upgrade magic level
            a.magicLevel++;

            // Remove curse
            a.cursed = false;

            // Handle curse removal chances
            int uncurseChance = 4 + a.visiblyUpgraded();
            int weakenChance = 2 + a.visiblyUpgraded();

            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph() && Random.Int(uncurseChance) == 0){
                ScrollOfUpgrade.removeCurse( Dungeon.hero );
            } else if (a.cursedKnown && wasCursed && !a.cursed && Random.Int(weakenChance) == 0){
                ScrollOfUpgrade.weakenCurse( Dungeon.hero );
            }

            // Hardening loss chance
            if (a.glyphHardened && a.magicLevel >= 6 && Random.Float(10) < Math.pow(2, a.magicLevel-6)){
                a.glyphHardened = false;
                GLog.w( Messages.get(Armor.class, "hardening_gone") );
            } else if (hadGoodGlyph && !a.hasGoodGlyph()){
                GLog.w( Messages.get(Armor.class, "incompatible") );
            }

        } else if (item instanceof Wand) {
            boolean wasCursed = item.cursed;
            item.upgrade();

            if (item.cursedKnown && wasCursed && !item.cursed) {
                ScrollOfUpgrade.removeCurse(Dungeon.hero);
            }
        }

        Badges.validateItemLevelAquired( item );
        Statistics.upgradesUsed++;
        Badges.validateMageUnlock();
        Catalog.countUse(item.getClass());
    }

    public static void magicUpgrade( Hero hero ) {
        hero.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
    }

    @Override
    public int value() {
        return isKnown() ? 50 * quantity : super.value();
    }

    @Override
    public int energyVal() {
        return isKnown() ? 10 * quantity : super.energyVal();
    }
}