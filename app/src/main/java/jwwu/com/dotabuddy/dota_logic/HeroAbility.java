package jwwu.com.dotabuddy.dota_logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Instinctlol on 04.12.2015.
 */
public class HeroAbility {
    protected String name, type, description, lore, ability, affects, affects2, damagetype,
    bkbblock, bkbtext, linkenblock, linkentext, purgeable, purgetext, illusionuse, illusiontext, breakable, breaktext, uam,
    castpoint, castbackswing;
    protected ArrayList<Pair<String,String>> traitsAndValuesList;
    private static String traitsAndValuesSeperator = "####";
    protected String mana, cooldown, aghanimsupgrade;
    protected ArrayList<AbilityNote> notesList;
    private static String strSeparator = "__#,#__";
    //TODO I dont think allocating abilityimages in Singleton is needed
    //private Bitmap image;
    private String imagepath;



    public HeroAbility() {

    }

    public Bitmap getFullImage() {
        if(imagepath.isEmpty())
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(imagepath, options);
    }

    //i.e. "Dodge Chance####10%/15%/20%/25%__,__Critical Chance####10%/15%/20%/25%
    public String getTraitsAndValuesStringRepresentation() {
        String str="";
        if(traitsAndValuesList!=null) {
            Iterator<Pair<String,String>> it = traitsAndValuesList.iterator();

            while(it.hasNext()) {
                Pair<String,String> p = it.next();

                if(!(p.first.isEmpty() && p.second.isEmpty()))
                    str = str + p.first + traitsAndValuesSeperator + p.second;

                // Do not append comma at the end of last element
                if (it.hasNext()) {
                    str = str + strSeparator;
                }
            }
        }
        return str;
    }

    public void putFromTraitsAndValuesStringRepresentation(String str) {
        if(!str.isEmpty()) {
            ArrayList<Pair<String,String>> newList = new ArrayList<>();
            String[] arr = str.split(strSeparator); //arr contains multiple pairs

            for(String pair : arr) {    //split pairs into its elements and add them to Pair in ArrayList
                String[] pairElements = pair.split(traitsAndValuesSeperator);

                if(pairElements.length>1)
                    newList.add(new Pair<>(pairElements[0], pairElements[1]));
            }
            if(traitsAndValuesList!=null) {
                traitsAndValuesList.clear();
            }
            traitsAndValuesList=newList;
        }
    }

    public String getNotesStringRepresentation() {
        String str="";

        if(notesList!=null) {
            Iterator<AbilityNote> it = notesList.iterator();

            while (it.hasNext()) {
                str += it.next().getString();
                // Do not append comma at the end of last element
                if (it.hasNext()) {
                    str += strSeparator;
                }
            }
        }
        return str;
    }

    public void putFromNotesStringRepresentation(String str) {
        if(!str.isEmpty()) {
            String[] arr = str.split(strSeparator);
            ArrayList<String> newList = new ArrayList<>();

            Collections.addAll(newList, arr);
            if(notesList!=null)
                notesList.clear();
            else {
                notesList = new ArrayList<>();
            }

            for(String firstLevel : newList) {
                notesList.add(AbilityNote.createFromFirstLevelString(firstLevel));
            }
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //TODO I dont think allocating abilityimages in Singleton is needed
    /*public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }*/

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getAffects() {
        return affects;
    }

    public void setAffects(String affects) {
        this.affects = affects;
    }

    public String getBkbblock() {
        return bkbblock;
    }

    public void setBkbblock(String bkbblock) {
        this.bkbblock = bkbblock;
    }

    public String getBkbtext() {
        return bkbtext;
    }

    public void setBkbtext(String bkbtext) {
        this.bkbtext = bkbtext;
    }

    public String getLinkenblock() {
        return linkenblock;
    }

    public void setLinkenblock(String linkenblock) {
        this.linkenblock = linkenblock;
    }

    public String getLinkentext() {
        return linkentext;
    }

    public void setLinkentext(String linkentext) {
        this.linkentext = linkentext;
    }

    public String getPurgeable() {
        return purgeable;
    }

    public void setPurgeable(String purgeable) {
        this.purgeable = purgeable;
    }

    public String getPurgetext() {
        return purgetext;
    }

    public void setPurgetext(String purgetext) {
        this.purgetext = purgetext;
    }

    public String getIllusionuse() {
        return illusionuse;
    }

    public void setIllusionuse(String illusionuse) {
        this.illusionuse = illusionuse;
    }

    public String getIllusiontext() {
        return illusiontext;
    }

    public void setIllusiontext(String illusiontext) {
        this.illusiontext = illusiontext;
    }

    public String getBreakable() {
        return breakable;
    }

    public void setBreakable(String breakable) {
        this.breakable = breakable;
    }

    public String getBreaktext() {
        return breaktext;
    }

    public void setBreaktext(String breaktext) {
        this.breaktext = breaktext;
    }

    public String getUam() {
        return uam;
    }

    public void setUam(String uam) {
        this.uam = uam;
    }

    public String getCastpoint() {
        return castpoint;
    }

    public void setCastpoint(String castpoint) {
        this.castpoint = castpoint;
    }

    public String getCastbackswing() {
        return castbackswing;
    }

    public void setCastbackswing(String castbackswing) {
        this.castbackswing = castbackswing;
    }

    public ArrayList<Pair<String, String>> getTraitsAndValuesList() {
        if(traitsAndValuesList==null)
            traitsAndValuesList=new ArrayList<>();

        return traitsAndValuesList;
    }

    public void setTraitsAndValuesList(ArrayList<Pair<String, String>> traitsAndValuesList) {
        this.traitsAndValuesList = traitsAndValuesList;
    }

    public String getMana() {
        return mana;
    }

    public void setMana(String mana) {
        this.mana = mana;
    }

    public String getCooldown() {
        return cooldown;
    }

    public void setCooldown(String cooldown) {
        this.cooldown = cooldown;
    }

    public String getAghanimsupgrade() {
        return aghanimsupgrade;
    }

    public void setAghanimsupgrade(String aghanimsupgrade) {
        this.aghanimsupgrade = aghanimsupgrade;
    }

    public ArrayList<AbilityNote> getNotesList() {
        return notesList;
    }

    public void setNotesList(ArrayList<AbilityNote> notesList) {
        this.notesList = notesList;
    }

    public void setAffects2(String affects2) {
        this.affects2 = affects2;
    }

    public String getAffects2() {
        return affects2;
    }

    public void setDamagetype(String damagetype) {
        this.damagetype = damagetype;
    }

    public String getDamagetype() {
        return damagetype;
    }
}
