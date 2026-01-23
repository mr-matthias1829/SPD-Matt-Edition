package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Bulky extends FlavourBuff {

    {
        type = buffType.NEUTRAL;
        announced = true;
    }

    public static final float DURATION	= 20f; // Default duration

    public static float speedBoost(Char owner) {
        if ((Dungeon.level.map[owner.pos] != Terrain.DOOR && Dungeon.level.map[owner.pos] != Terrain.OPEN_DOOR )) {
            return 1;
        } else {
            return 1/3f; // 33% speed when in doorways
        }
    }

    public static float evasionMultiplier() {
        return 0f; // multiplies evasion by 0 = cannot dodge
    }

    public static float drMultiplier() {
        return 1.3f; // 30% more damage reduction
    }

    @Override
    public int icon() {
        return BuffIndicator.BULKY;
    }
}