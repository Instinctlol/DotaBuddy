package jwwu.com.dotabuddy.dota_logic;

import java.util.ArrayList;

/**
 * Created by Instinctlol on 08.12.2015.
 */
public class AbilityNote {
    protected String note;
    protected ArrayList<AbilityNote> subnotes;
    protected boolean hidden;
    protected int level;
    public static final String HIDDENMARKER = "#HIDDEN#";
    public static final String OPENPARMARKER = "__[__";
    public static final String CLOSEPARMARKER = "__]__";

    public AbilityNote(String note, int level) {


        //check whether this note starts with '*..* ' or '*..*: ', last one defines a hidden note (=doesn't have its own point, but is still a subnote)
        for(int i=0; i<note.length()-1; i++) {
            if(note.charAt(i)==' ') {
                this.note=note.substring(i+1);
                hidden=false;
                break;
            }
            else if(note.charAt(i) == ':') {
                this.note=note.substring(i+2);
                hidden=true;
                break;
            }
        }
        this.level=level;
    }

    public void addSubnote(String subnote) {
        if(subnotes==null)
            subnotes=new ArrayList<>();

        subnotes.add(new AbilityNote(subnote, level+1));
    }

    public ArrayList<AbilityNote> getSubnotes() {
        return subnotes;
    }

    public void setSubnotes(ArrayList<AbilityNote> subnotes) {
        this.subnotes = subnotes;
    }

    public AbilityNote getLastAbilityNote() {
        return subnotes.get(subnotes.size()-1);
    }

    public int getLevel() {
        return level;
    }

    public boolean hasSubnotes() {
        return !((subnotes==null)||subnotes.isEmpty());
    }

    public String getString() {
        String str=note;
        if(hasSubnotes()) {
            String subNotesString = "";
            for(int i = 0; i<subnotes.size(); i++) {
                if(subnotes.get(i).hidden) {
                    subNotesString+=HIDDENMARKER;
                }

                if(i<subnotes.size()-1)
                    subNotesString+=subnotes.get(i).getString()+"__"+(level+1)+"__";
                else
                    subNotesString+=subnotes.get(i).getString();
            }
            str+=OPENPARMARKER+subNotesString+CLOSEPARMARKER;
        }



        return str;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //e.g. Note1__[__Note11__2__Note12__2__Note13__[__Note131__3__#HIDDEN#Note132__]____]__
    public final static AbilityNote createFromFirstLevelString(String parStr) {
        AbilityNote an = new AbilityNote("",1);

        if(parStr.substring(0, HIDDENMARKER.length() - 1).equals(HIDDENMARKER)) {
            an.hidden=true;
            parStr=parStr.substring(HIDDENMARKER.length());
        }
        if(!parStr.contains(OPENPARMARKER)) {   //no subnotes
            an.setNote(parStr);
        }
        else {
            int indexOpenPar = parStr.indexOf(OPENPARMARKER);
            int indexClosePar = parStr.lastIndexOf(CLOSEPARMARKER);
            an.setNote(parStr.substring(0,indexOpenPar-1));
            String children = parStr.substring(indexOpenPar+OPENPARMARKER.length(),indexClosePar-1);
            an.createChildren(children);
        }

        return an;
    }


    private void createChildren(String parStr) {
        String[] children = parStr.split("__"+(level+1)+"__");
        subnotes = new ArrayList<>();

        for(String child : children) {
            AbilityNote an = new AbilityNote("",level+1);

            String note = child;

            if(note.substring(0, HIDDENMARKER.length() - 1).equals(HIDDENMARKER)) {
                an.hidden=true;
                note=note.substring(HIDDENMARKER.length());
            }
            if(!note.contains(OPENPARMARKER)) {
                an.setNote(note);
                subnotes.add(an);
            }
            else {
                int indexOpenPar = note.indexOf(OPENPARMARKER);
                int indexClosePar = note.lastIndexOf(CLOSEPARMARKER);

                String nextChildren = parStr.substring(indexOpenPar + OPENPARMARKER.length(), indexClosePar - 1);
                an.createChildren(nextChildren);

                an.setNote(note.substring(0,indexOpenPar-1));
                subnotes.add(an);
            }
        }
    }
}
