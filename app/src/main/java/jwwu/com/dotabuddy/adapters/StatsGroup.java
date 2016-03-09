package jwwu.com.dotabuddy.adapters;

import java.util.ArrayList;
import java.util.List;

import jwwu.com.dotabuddy.dota_logic.Stat;

/**
 * Created by Instinctlol on 28.02.2016.
 */
public class StatsGroup {

    public String nameOfGroup;                                //group name, e.g. Stats, Abilities, Changelog
    public final List<Stat> children = new ArrayList<Stat>();   //contains the stats of a hero, TODO: change this for ability and changelog support

    public StatsGroup(String name) {
        this.nameOfGroup = name;
    }

    public List<Stat> getChildren() {
        return children;
    }

}
