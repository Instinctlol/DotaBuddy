package jwwu.com.dotabuddy.dota_logic;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Instinctlol on 13.03.2016.
 */
public class Hero {
    public Bitmap mPortrait;
    public String mName;
    public HeroStats mHeroStats;
    public ArrayList<HeroAbility> mHeroAbilities;

    public Hero(String name, Bitmap portrait, HeroStats heroStats, ArrayList<HeroAbility> heroAbilities) {
        mHeroStats = heroStats;
        mHeroAbilities = heroAbilities;
        mPortrait = portrait;
        mName = name;
    }
}
