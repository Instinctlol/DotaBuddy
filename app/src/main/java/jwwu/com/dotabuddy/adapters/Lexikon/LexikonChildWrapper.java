package jwwu.com.dotabuddy.adapters.Lexikon;

import jwwu.com.dotabuddy.dota_logic.HeroAbility;
import jwwu.com.dotabuddy.dota_logic.Stat;

/**
 * Created by Instinctlol on 11.03.2016.
 */
public class LexikonChildWrapper {

    public enum Type {STAT, ABILITY, BALANCECHANGELOG}

    public Type type;

    public Stat stat;
    public HeroAbility heroAbility;


    public LexikonChildWrapper(Object obj) {
        if(obj instanceof Stat) {
            type = Type.STAT;
            stat = (Stat) obj;
        } else if (obj instanceof HeroAbility) {
            type = Type.ABILITY;
            heroAbility = (HeroAbility) obj;
        }
    }


}