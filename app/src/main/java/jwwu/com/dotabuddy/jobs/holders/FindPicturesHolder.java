package jwwu.com.dotabuddy.jobs.holders;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;

import jwwu.com.dotabuddy.events.FindPicturesEvent;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class FindPicturesHolder {

    public ArrayList<String> remainingAbilities;
    public TreeMap<String,JSONObject> unparsedAbilityPictures, unparsedHeroPictures;
    public TreeMap<String,String> abilityNameAndImagename;
    private int finishedAbilityRequests, finishedHeroRequests;
    public final int totalAbilityRequests, totalHeroRequests, totalRequests;
    public final boolean[] cmds;

    public FindPicturesHolder(ArrayList<String> remainingAbilities, int totalHeroRequests, int totalAbilityRequests, boolean[] cmds) {
        this.unparsedAbilityPictures = new TreeMap<>();
        this.unparsedHeroPictures = new TreeMap<>();
        this.remainingAbilities = remainingAbilities;
        this.totalAbilityRequests = totalAbilityRequests;
        this.totalHeroRequests = totalHeroRequests;
        this.totalRequests = totalAbilityRequests + totalHeroRequests;
        this.finishedHeroRequests = 0;
        this.finishedAbilityRequests = 0;
        this.abilityNameAndImagename = new TreeMap<>();
        this.cmds = cmds;
    }

    public void incrementAbilityRequests() {
        if(finishedAbilityRequests<totalAbilityRequests)
            finishedAbilityRequests++;
        if(isFinished()) {
            executeNextEvent();
        }
    }

    public void incrementHeroRequests() {
        if(finishedHeroRequests<totalHeroRequests)
            finishedHeroRequests++;
        if(isFinished()) {
            executeNextEvent();
        }
    }

    public boolean isFinished() {
        return (finishedHeroRequests==totalHeroRequests) && (finishedAbilityRequests==totalAbilityRequests);
    }

    private void executeNextEvent() {
        EventBus.getDefault().post(new FindPicturesEvent(this));
    }

    public int getFinishedRequests() {
        return finishedAbilityRequests+finishedHeroRequests;
    }
}
