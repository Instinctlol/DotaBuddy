package jwwu.com.dotabuddy.jobs.holders;

import android.graphics.Bitmap;

import org.greenrobot.eventbus.EventBus;

import java.util.TreeMap;

import jwwu.com.dotabuddy.events.UpdatePictureDatabaseLaunchEvent;
import jwwu.com.dotabuddy.events.UpdatePictureDatabaseUIEvent;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class DownloadPicturesHolder {
    public TreeMap<String,Bitmap> heroPictures, abilityPictures;
    public TreeMap<String,String> heroPictureUris, abilityPictureUris;
    public final int totalHeroPictureRequests, totalAbilityPictureRequests, totalRequests;
    private int finishedHeroPictureRequests, finishedAbilityPictureRequests;
    public final boolean[] cmds;

    public DownloadPicturesHolder(TreeMap<String,String> heroPictureUris, TreeMap<String,String> abilityPictureUris, boolean[] cmds) {
        heroPictures = new TreeMap<>();
        abilityPictures = new TreeMap<>();
        this.totalHeroPictureRequests = heroPictureUris.size();
        this.totalAbilityPictureRequests = abilityPictureUris.size();
        this.totalRequests = totalAbilityPictureRequests + totalHeroPictureRequests;
        this.heroPictureUris = heroPictureUris;
        this.abilityPictureUris = abilityPictureUris;
        this.finishedAbilityPictureRequests = 0;
        this.finishedHeroPictureRequests = 0;
        this.cmds = cmds;
    }


    public boolean isFinished() {
        return (finishedAbilityPictureRequests+finishedHeroPictureRequests == totalRequests);
    }

    public void incrementHeroPictureResponses() {
        if(finishedHeroPictureRequests<totalHeroPictureRequests)
            finishedHeroPictureRequests++;
        if(isFinished())
            executeNextCommand();
    }

    public void incrementAbilityPictureResponses() {
        if(finishedAbilityPictureRequests<totalAbilityPictureRequests)
            finishedAbilityPictureRequests++;
        if(isFinished())
            executeNextCommand();
    }

    private void executeNextCommand() {
        EventBus.getDefault().post(new UpdatePictureDatabaseLaunchEvent(heroPictures,abilityPictures,heroPictureUris,abilityPictureUris,cmds));
    }

    public int getFinishedRequests() {
        return finishedHeroPictureRequests+finishedAbilityPictureRequests;
    }
}
