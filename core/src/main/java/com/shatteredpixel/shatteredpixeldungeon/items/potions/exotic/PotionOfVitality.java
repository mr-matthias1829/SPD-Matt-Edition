package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;


// replacement for pot of mastery
// grants no STR, but PERMANENTLY increases hp based on hero level
public class PotionOfVitality extends ExoticPotion{

    {
        icon = ItemSpriteSheet.Icons.POTION_MASTERY;

        unique = true;

        talentFactor = 2f;
    }
    @Override
    public void apply(Hero hero) {
        identify();

        // Scale HP gain with hero level
        int lvl = hero.lvl;
        if (lvl > 10){ // you've guessed it, we don't want you to gain too much max hp
            lvl = 10 + (hero.lvl/2);
        }
        int hpGain = 5 + lvl; // assuming max lvl is 30, you can get up to +25 max HP

        if (Dungeon.vitalityPotionsUsed > 0) { // to prevent abuse and getting a gigaton hp
                        // this is meant as a late game item when STR becomes less relevant
                        // though if you saved a bunch of POS (like 4), you'd quickly more than double your hp.
            hpGain *= 3;
            hpGain = hpGain/(3 +Dungeon.vitalityPotionsUsed); // we don't want this to scale too punishingly

            hpGain = Math.max(hpGain, 5); // min increase of 5, im so nice :)
        }

        Dungeon.vitalityPotionsUsed++; // this is in dungeon so it properly saves even after you exit a run
        hero.vitalityBonus += hpGain;
        hero.updateHT(false);

        // Also give the current HP, just like level ups do
        // if you have the challenge enabled though, healing is a big nope, so you gotta cope with forced damage
        if (Dungeon.isChallenged(Challenges.NO_HEALING)){
            PotionOfHealing.pharmacophobiaProc(hero);
        } else {
            hero.HP += hpGain;
        }

        // Visual feedback
        hero.sprite.showStatusWithIcon(
                CharSprite.POSITIVE,
                Integer.toString(hpGain),
                FloatingText.HEALING
        );
    }
}
