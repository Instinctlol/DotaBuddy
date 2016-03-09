package jwwu.com.dotabuddy.dota_logic;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Contains all the standard timers and custom timers created by a user.
 */
public class DotaTimerSet {
    private HashSet<DotaTimer> timers;
    public DotaTimerSet() {
        timers=new HashSet<>();
    }

    /**
     * Creates all the possible timers.
     */
    private void initialize() {
        for(DotaTimer.Standards standard : DotaTimer.Standards.values()) {
            addTimer(new DotaTimer(standard));
        }
    }

    public void addTimer(DotaTimer timer) {
        timers.add(timer);
    }

    public void startAll(DotaGameTime currTime) {
        for(DotaTimer timer : timers) {
            if(!timer.hasStarted())
                timer.start(currTime);
        }
    }


    /**
     * Removes a timer from the set
     * @param timer the timer to be removed
     */
    public void removeTimer(DotaTimer timer) {
        timers.remove(timer);
    }

    public LinkedList<DotaTimer> returnAllFinishedTimers(DotaGameTime currTime) {
        LinkedList<DotaTimer> finished = new LinkedList<>();
        if(timers!=null) {
            for(DotaTimer timer:timers) {
                if(timer.checkExecution(currTime)) {
                    finished.add(timer);
                    if(timer.isDestroyable())
                        removeTimer(timer);
                }
            }
        }
        return finished;
    }


}
