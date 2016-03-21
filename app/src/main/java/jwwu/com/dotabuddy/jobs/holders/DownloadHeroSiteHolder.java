package jwwu.com.dotabuddy.jobs.holders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Instinctlol on 16.03.2016.
 */
public class DownloadHeroSiteHolder implements Serializable{
    public TreeMap<Integer,String> unparsedHerositeHolder;  //will contain downloaded unparsed Herosites.
    public ArrayList<String> heronames;
    public final int totalRequests;
    protected int finishedRequests;

    /**
     * Holds all unparsed Herosites. The HashMap unParsedHerositeHolder will hold the downloaded,
     * nonparsed Herosites, which will be sorted chronologically and alphabetically.
     * e.g. get(0) holds Sites of {Abaddon, ..., Invoker}
     *      get(1) holds Sites of {Io, ..., Shadow Fiend}
     *      get(2) holds Sites of {Shadow Shaman, ..., Zeus}
     */
    public DownloadHeroSiteHolder(ArrayList<String> heronames, int totalRequests){
        this.heronames = heronames;
        unparsedHerositeHolder = new TreeMap<>();
        this.totalRequests = totalRequests;
        this.finishedRequests = 0;
    }

    public int getFinishedRequests(){
        return finishedRequests;
    }

    public void incrementFinishedRequests() {
        if(finishedRequests<totalRequests)
            finishedRequests+=1;
    }

    public boolean isFinished() {
        return totalRequests == finishedRequests;
    }
}
