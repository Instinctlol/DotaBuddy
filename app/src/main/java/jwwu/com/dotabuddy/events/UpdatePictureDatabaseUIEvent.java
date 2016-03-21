package jwwu.com.dotabuddy.events;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class UpdatePictureDatabaseUIEvent {
    public final int progress,total;
    public final boolean isFinished;
    public final String message;
    public final boolean[] cmds;

    public UpdatePictureDatabaseUIEvent(int progress, int total, String message, boolean[] cmds) {
        this.progress = progress;
        this.total = total;
        this.isFinished = progress == total;
        this.message = message;
        this.cmds = cmds;
    }
}
