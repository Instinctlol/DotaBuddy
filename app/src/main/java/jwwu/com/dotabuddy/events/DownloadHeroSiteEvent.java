package jwwu.com.dotabuddy.events;

import jwwu.com.dotabuddy.jobs.holders.DownloadHeroSiteHolder;

/**
 * Created by Instinctlol on 16.03.2016.
 */
public class DownloadHeroSiteEvent {

    public DownloadHeroSiteHolder downloadHeroSiteHolder;
    public final boolean[] cmds;

    /**
     * Gets called as soons as all non Parsed Herosites were downloaded.
     * @param downloadHeroSiteHolder The Holder, containing all the non parsed Herosite information.
     */
    public DownloadHeroSiteEvent (DownloadHeroSiteHolder downloadHeroSiteHolder, boolean[] cmds) {
        this.downloadHeroSiteHolder = downloadHeroSiteHolder;
        this.cmds = cmds;
    }


}
