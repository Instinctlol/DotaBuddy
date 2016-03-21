package jwwu.com.dotabuddy.events;

import jwwu.com.dotabuddy.jobs.holders.FindPicturesHolder;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class FindPicturesEvent {
    public FindPicturesHolder findPicturesHolder;

    public FindPicturesEvent(FindPicturesHolder findPicturesHolder) {
        this.findPicturesHolder = findPicturesHolder;
    }
}
