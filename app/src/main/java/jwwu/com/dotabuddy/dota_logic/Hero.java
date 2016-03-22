package jwwu.com.dotabuddy.dota_logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Instinctlol on 13.03.2016.
 */
public class Hero {
    public Bitmap mPortrait;
    public String mName;
    public HeroStats mHeroStats;
    public ArrayList<HeroAbility> mHeroAbilities;
    private String portraitPath;

    public Hero(String name, Bitmap portrait, String portraitPath, HeroStats heroStats, ArrayList<HeroAbility> heroAbilities) {
        mHeroStats = heroStats;
        mHeroAbilities = heroAbilities;
        mPortrait = portrait;
        mName = name;
        this.portraitPath = portraitPath;
    }

    public Bitmap getFullPortrait() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(portraitPath, options);
    }
}
