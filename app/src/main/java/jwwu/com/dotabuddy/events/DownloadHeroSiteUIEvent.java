package jwwu.com.dotabuddy.events;

/**
 * Created by Instinctlol on 18.03.2016.
 */
public class DownloadHeroSiteUIEvent {

    public final int progress;
    public final int progressMax;
    public final String message;
    public final boolean isError;
    public final boolean isFinished;

    public DownloadHeroSiteUIEvent(int progress, int progressMax, String message, boolean isError, boolean isFinished) {
        this.progress = progress;
        this.progressMax = progressMax;
        this.message = message;
        this.isError = isError;
        this.isFinished = isFinished;
    }


}
