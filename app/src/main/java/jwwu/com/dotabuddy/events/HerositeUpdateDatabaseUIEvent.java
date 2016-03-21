package jwwu.com.dotabuddy.events;

/**
 * Created by Instinctlol on 17.03.2016.
 */
public class HerositeUpdateDatabaseUIEvent {
    public final String message;
    public final int progress, progressMax;
    public final boolean isFinished;
    public final boolean[] cmds;

    public HerositeUpdateDatabaseUIEvent(int progress, int progressMax, String message, boolean isFinished, boolean[] cmds) {
        this.progress = progress;
        this.progressMax = progressMax;
        this.message = message;
        this.isFinished = isFinished;
        this.cmds = cmds;
    }
}
