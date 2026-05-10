package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class MagicInfuse extends FlavourBuff {

    // the default duration here is actually hard to determine
    // go low, because the effect is actually pretty damn strong against specific cases
    // go high, because the effect isn't that busted in most cases, and it'd only be worth if it lasted long
    // i just eyeballed and went 15, which is how it will likely stay forever no matter what
    public static final float DURATION = 15f;

    {
        type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return BuffIndicator.MAGIC_INFUSE;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }


}
