package jwwu.com.dotabuddy.dota_logic;

/**
 * A class to describe a timer for usage in dota.
 */
public class DotaGameTime {
    private int mins,secs, mils;
    //TODO: device time? synchronisieren
    public DotaGameTime(int mins, int secs, int mils) {
        this.mins=mins;
        this.secs=secs;
        this.mils=mils;
    }

    void setMinsAndSecs(int mins, int secs) {
        setMins(mins);
        setSecs(secs);
    }

    void setMinsSecsAndMils(int mins, int secs, int mils) {
        setMins(mins);
        setSecs(secs);
        setMils(mils);
    }

    public int getTimeAsSecs() {
        return mins*60+secs;
    }

    public int getTimeAsMils() {
        return mils+secs*1000+mins*60*1000;
    }

    public int getMins() {
        return mins;
    }

    public void setMins(int mins) {
        if(mins>0)
            this.mins = mins;
    }

    public int getSecs() {
        return secs;
    }

    public void setSecs(int secs) {
        if(mins>0)
            this.secs = secs;
    }

    public int getMils() {
        return mils;
    }

    public void setMils(int mils) {
        if(mils>0)
            this.mils = mils;
    }

    public boolean addMils(int mils) {
        if(mils>0) {
            this.mils+=mils;
            convertOnAdd();
            return true;
        }
        else
            return false;
    }

    public boolean addSecs(int secs) {
        if(secs>0) {
            this.secs+=secs;
            convertOnAdd();
            return true;
        }
        else
            return false;

    }

    public boolean addMins(int mins) {
        if(mins>0) {
            this.mins+=mins;
            return true;
        }
        else
            return false;
    }

    private boolean convertOnAdd() {
            boolean converted=false;
            int remainder,quotient;
            if(mils>=1000) {
                remainder=mils%1000;
                quotient=mils/1000;
                mils=remainder;     //remainder stays in mils
                secs+=quotient;     //quotient -> converted to secs

                converted=true;
            }
            if(secs>=60) {
                remainder=secs%60;
                quotient=secs/60;
                secs=remainder;     //remainder stays in secs
                mins+=quotient;     //quotient -> converted to secs

                converted=true;
            }
            return converted;
    }
}
