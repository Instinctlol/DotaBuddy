package jwwu.com.dotabuddy.dota_logic;

import java.util.Observable;

import jwwu.com.dotabuddy.R;

public class DotaTimer extends Observable {

    private String name;
    private int execTime, offset, startTime;
    private boolean repeat, exact;      //exact is true, when the timer should consider the seconds of the startTime of this timer.
    private boolean destroyMe;          //if true, this object will be destroyed the next time it is called
    private boolean started;            //true, if this timer has been started
    public enum Standards {
        RDNT_MedCamp_Mid,
        RDNT_LrgCamp_Mid,
        RDNT_SmlCamp_Bot,
        RDNT_LrgCamp_Bot,
        RDNT_MedCamp_Bot,
        RDNT_Ancients,
        DIRE_LrgCamp_Mid,
        DIRE_MedCamp_Mid,
        DIRE_MedCamp_Rne,
        DIRE_SmlCamp_Top,
        DIRE_LrgCamp_Top,
        DIRE_Ancients,
        Roshan_Start,
        Roshan_End,
        Aegis}
    private int iconThumbId;

    //unused? TODO: delete
    public DotaTimer(Standards dotaTimer){
        switch(dotaTimer) {
            case Roshan_Start:
                exact=true;
                this.repeat=false;
                this.offset=30;
                this.execTime =8*60;        //8Mins
                this.name=dotaTimer.toString();
                this.iconThumbId= R.drawable.roshan_portrait_start;
                break;
            case Roshan_End:
                exact=true;
                this.repeat=false;
                this.offset=30;
                this.execTime =11*60;       //11Mins
                this.name=dotaTimer.toString();
                this.iconThumbId=R.drawable.roshan_portrait_end;
                break;
            case Aegis:
                exact=true;
                this.repeat=false;
                this.offset=30;
                this.execTime =60*5;        //5mins
                this.name=dotaTimer.toString();
                this.iconThumbId=R.drawable.aegis_of_the_immortal_icon;
                break;
            case RDNT_Ancients:
                this.iconThumbId=R.drawable.radiant_ancients;
                break;
            case RDNT_MedCamp_Bot:
                this.iconThumbId=R.drawable.radiant_medium_camp_bot;
                break;
            case RDNT_SmlCamp_Bot:
                this.iconThumbId=R.drawable.radiant_small_camp_bot;
                break;
            case RDNT_LrgCamp_Bot:
                this.iconThumbId=R.drawable.radiant_large_camp_bot;
                break;
            case RDNT_LrgCamp_Mid:
                this.iconThumbId=R.drawable.radiant_large_camp_mid;
                break;
            case RDNT_MedCamp_Mid:
                this.iconThumbId=R.drawable.radiant_medium_camp_mid;
                break;
            case DIRE_Ancients:
                this.iconThumbId=R.drawable.dire_ancients;
                break;
            case DIRE_LrgCamp_Mid:
                this.iconThumbId=R.drawable.dire_large_camp_mid;
                break;
            case DIRE_LrgCamp_Top:
                this.iconThumbId=R.drawable.dire_large_camp_top;
                break;
            case DIRE_SmlCamp_Top:
                this.iconThumbId=R.drawable.dire_small_camp_top;
                break;
            case DIRE_MedCamp_Mid:
                this.iconThumbId=R.drawable.dire_medium_camp_mid;
                break;
            case DIRE_MedCamp_Rne:
                this.iconThumbId=R.drawable.dire_medium_camp_rune;
                break;
            default:
                defaultCaseActions(dotaTimer.toString());
                break;
        }
    }


    public DotaTimer(String name, int execTime, int offset, boolean repeat,
                     boolean exact, int iconThumbId){
        this.name=name;
        this.execTime=execTime;
        this.offset=offset;
        this.repeat=repeat;
        this.exact=exact;
        this.iconThumbId=iconThumbId;
    }

    /**
     * Check, whether this timer has reached its execution time or not.
     * @param currTime  the current DotaGameTime
     * @return true, if timer has reached execution time.
     */
    public boolean checkExecution(DotaGameTime currTime) {
        if(currTime.getTimeAsSecs()>=startTime+execTime-offset) {
            if(repeat)
            {
                if(!exact) {
                    //Pretend the timer started on the Minute and add the execTime
                    DotaGameTime newStartTime = new DotaGameTime(currTime.getMins(),
                            currTime.getSecs(),currTime.getMils());
                    newStartTime.setSecs(0);
                    newStartTime.addSecs(execTime);
                    startTime=newStartTime.getTimeAsSecs();
                }
                else {
                    //Take the current startTime and add the execTime to it,
                    // this is the new startTime
                    startTime+=execTime;
                }
            }
            else
                destroyMe=true;
            return true;
        }
        else
            return false;

    }

    public boolean isDestroyable() {
        return destroyMe;
    }

    public boolean hasStarted() {
        return started;
    }

    public int getIconThumbId() {
        return iconThumbId;
    }

    public void setIcon(int iconThumbId) {
        this.iconThumbId = iconThumbId;
    }

    public void start(DotaGameTime currTime) {
        if(!hasStarted()) {
            if(exact)
                this.startTime=currTime.getTimeAsSecs();
            else {
                DotaGameTime setStartTime = new DotaGameTime(currTime.getMins(),
                        currTime.getSecs(),currTime.getMils());
                setStartTime.setSecs(0);
                this.startTime=setStartTime.getTimeAsSecs();
            }
            started = true;
        }
    }

    public void setOffset(int newOffset) {
        this.offset=newOffset;
    }

    private void defaultCaseActions(String name) {
        exact = false;
        this.repeat=true;
        this.name=name;
        this.offset=9;
        this.execTime =60;
    }
}
