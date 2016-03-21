package jwwu.com.dotabuddy.jobs;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatException;

import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.dota_logic.AbilityNote;
import jwwu.com.dotabuddy.dota_logic.Balancechangelog;
import jwwu.com.dotabuddy.dota_logic.HeroAbility;
import jwwu.com.dotabuddy.dota_logic.HeroStats;
import jwwu.com.dotabuddy.dota_logic.Stat;
import jwwu.com.dotabuddy.events.HerositeUpdateDatabaseUIEvent;
import jwwu.com.dotabuddy.util.RegExHelper;
import jwwu.com.dotabuddy.util.Utils;

/**
 * Created by Instinctlol on 17.03.2016.
 */
public class UpdateHeroDatabase extends Job {

    public static final int PRIORITY = DownloadHeroSites.PRIORITY-1;
    private static final String heronames_filename = "heronames";
    private static final String heroSiteFindings_filename = "heroSiteFindings";
    private final boolean[] cmds;

    private File heronamesFile, heroSiteFindingsFile;

    public UpdateHeroDatabase(Context context, ArrayList<String> heronames, ArrayList<String> heroSiteFindings, boolean[] cmds) {
        super(new Params(PRIORITY).persist().groupBy("DownloadHerosites"));

        heronamesFile = Utils.writeObjectFileToCache(context,heronames_filename,heronames);
        heroSiteFindingsFile = Utils.writeObjectFileToCache(context, heroSiteFindings_filename, heroSiteFindings);
        this.cmds = cmds;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<String> heronames = (ArrayList<String>) Utils.readObjectFromFile(heronamesFile);
        ArrayList<String> heroSiteFindings = (ArrayList<String>) Utils.readObjectFromFile(heroSiteFindingsFile);

        ArrayList<Pair<String,String>> namesAndUnparsedHerosites = new ArrayList<>();

        int size = heronames.size();
        if(size == heroSiteFindings.size()) {
            for(int i = 0; i<size; i++ ) {
                namesAndUnparsedHerosites.add(new Pair<String,String>(heronames.get(i),heroSiteFindings.get(i)));
            }
        }
        else {
            throw new Exception("Could not pair heronames and herosites");
        }



        //Search Abilities & Stats --> put in Database
        int currentProgress = 0;
        int progressMax = namesAndUnparsedHerosites.size();
        for(Pair<String,String> p : namesAndUnparsedHerosites) {

            EventBus.getDefault().post(new HerositeUpdateDatabaseUIEvent(currentProgress++, progressMax, "Updating: "+p.first, false, cmds));

            //Finding Herostats
            HeroStats hs = new HeroStats();
            hs.setPrimaryAttribute((RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " primary attribute = (.*?)\\\\n", p.second)).get(0));
            hs.setStrength(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " strength = (.*?)\\\\n", p.second).get(0));
            hs.setStrengthGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " strength growth = (.*?)\\\\n", p.second).get(0));
            hs.setAgility(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " agility = (.*?)\\\\n", p.second).get(0));
            hs.setAgilityGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " agility growth = (.*?)\\\\n", p.second).get(0));
            hs.setIntelligence(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " intelligence = (.*?)\\\\n", p.second).get(0));
            hs.setIntelligenceGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " intelligence growth = (.*?)\\\\n", p.second).get(0));
            hs.setDamageMin(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " attack damage min = (.*?)\\\\n", p.second).get(0));
            hs.setDamageMax(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " attack damage max = (.*?)\\\\n", p.second).get(0));
            hs.setArmor(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " armor = (.*?)\\\\n", p.second).get(0));
            hs.setMoveSpeed(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " movement speed = (.*?)\\\\n", p.second).get(0));
            hs.setAttackRange(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " attack range = (.*?)\\\\n", p.second).get(0));
            hs.setAttackPoint(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " attack point = (.*?)\\\\n", p.second).get(0));
            hs.setAttackBackswing(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " attack backswing = (.*?)\\\\n", p.second).get(0));
            hs.setBat(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " base attack time = (.*?)\\\\n", p.second).get(0));
            hs.setMissileSpeed(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +        //some Strings can be empty, because e.g. not every hero is ranged ;) KEEP THIS IN MIND!!
                    " missile speed = (.*?)\\\\n", p.second).get(0));
            hs.setSightRangeDay(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " sight range day = (.*?)\\\\n", p.second).get(0));
            hs.setSightRangeNight(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " sight range night = (.*?)\\\\n", p.second).get(0));
            hs.setTurnRate(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " turn rate = (.*?)\\\\n", p.second).get(0));
            hs.setCollisionSize(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                    " collision size = (.*?)\\\\n", p.second).get(0));

            //Finding Changelog
            String nonParsedChangelog = RegExHelper.searchForValuesBetweenPrefixAndSuffix("== Balance changelog ==\\\\n\\{\\{Update History\\|\\\\n", "\\\\n\\}\\}\\\\n\\\\n==", p.second).get(0);
            String[] lines = nonParsedChangelog.split("\\\\n");

            Balancechangelog bc = new Balancechangelog();

            for (String line : lines) {
                bc.feedNonParsedLine(line);
            }

            String statsStringRepresentation = HeroStats.getStringRepresentation(hs);
            String balancelogStringRepresentation = bc.getStringRepresentation();


            Log.d("HEROES:","name = "+p.first);
            /*for(Stat s : hs.getStatList()) {
                Log.d("HEROES:",s.getName() + ": "+s.getValue());
            }
            Log.d("HEROES:","stats = "+statsStringRepresentation);
            Log.d("HEROES:","blog = "+balancelogStringRepresentation);*/

            //INSERT Heroes with Changelog, save row ID after inserting (used by abilities)
            ContentValues cv = new ContentValues();
            cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME, p.first);
            cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_STATS, statsStringRepresentation);
            cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_BALANCECHANGELOG, balancelogStringRepresentation);
            //TODO Insert OR (IF EXISTS) Update, check http://stackoverflow.com/a/20568176/5331119
            int updatedRows = db.update(DotaDBContract.DotaHeroesDatabase.TABLE_NAME,cv, DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME + " like '" + p.first.replace("'","''") + "'", null);
            if(updatedRows <= 0)
                db.insertWithOnConflict(DotaDBContract.DotaHeroesDatabase.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            
            

            //Finding Abilities, reuse row ID of hero
            String filteredAbilities = RegExHelper.searchForValuesBetweenPrefixAndSuffix("== Abilities ==","==", p.second).get(0);  //All the abilities for this hero
            ArrayList<String> splittedAbilities = new ArrayList<>();
            Collections.addAll(splittedAbilities, filteredAbilities.split("\\{\\{Ability\\\\n"));                                   //All the abilities for this hero split up
            splittedAbilities.remove(0);                                                                                            //first entry is no ability

            for(String ability : splittedAbilities) {


                HeroAbility hab = new HeroAbility();
                hab.setName(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| name = ", "\\\\n", ability).get(0));
                //hab.setImage(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| image = ","\\\\n\\|",ability).get(0));
                hab.setType(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| type = ", "\\\\n", ability).get(0));
                hab.setDescription(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| description = ", "\\\\n", ability).get(0));
                hab.setLore(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| lore = ", "\\\\n", ability).get(0));
                hab.setAbility(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| ability = ", "\\\\n", ability).get(0));
                hab.setAffects(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| affects = ", "\\\\n", ability).get(0));
                hab.setAffects2(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| affects2 = ", "\\\\n", ability).get(0));
                hab.setBkbblock(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| bkbblock = ", "\\\\n", ability).get(0));
                hab.setBkbtext(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| bkbtext = ", "\\\\n", ability).get(0));
                hab.setLinkenblock(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| linkenblock = ", "\\\\n", ability).get(0));
                hab.setLinkentext(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| linkentext = ", "\\\\n", ability).get(0));
                hab.setPurgeable(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| purgeable = ", "\\\\n", ability).get(0));
                hab.setPurgetext(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| purgetext = ", "\\\\n", ability).get(0));
                hab.setIllusionuse(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| illusionuse = ", "\\\\n", ability).get(0));
                hab.setIllusiontext(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| illusiontext = ", "\\\\n", ability).get(0));
                hab.setBreakable(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| breakable = ", "\\\\n", ability).get(0));
                hab.setBreaktext(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| breaktext = ", "\\\\n", ability).get(0));
                hab.setUam(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| uam = ", "\\\\n", ability).get(0));
                hab.setCastpoint(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| cast point = ", "\\\\n", ability).get(0));
                hab.setCastbackswing(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| cast backswing = ", "\\\\n", ability).get(0));
                hab.setDamagetype(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| damagetype = ", "\\\\n", ability).get(0));
                String imagename = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| image = ", "\\\\n", ability).get(0);
                //Traits and Values
                boolean maximumFound = false;
                int curr = 1;
                while(!maximumFound) {
                    String first = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| trait"+curr+" = ","\\\\n",ability).get(0);
                    String second = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| value"+curr+" = ","\\\\n",ability).get(0);
                    if(!(first.isEmpty() && second.isEmpty()))
                        hab.getTraitsAndValuesList().add(new android.util.Pair<String,String>(first, second));

                    if(ability.contains("trait"+(curr+1))) {
                        curr++;
                    }
                    else
                        maximumFound=true;
                }
                hab.setMana(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| mana = ", "\\\\n", ability).get(0));
                hab.setCooldown(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| cooldown = ", "\\\\n", ability).get(0));
                hab.setAghanimsupgrade(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| aghanimsupgrade = ", "\\\\n", ability).get(0));
                //Notes
                String notes = RegExHelper.greedySearchForValuesBetweenPrefixAndSuffix("\\| notes =","\\}\\}",ability).get(0);
                ArrayList<String> linesList = new ArrayList<>();
                Collections.addAll(linesList,notes.split("\\\\n"));

                //edit every line
                //for every line get the substring of the first '*' and last '.' (including), unmatched and old lines get deleted
                int newSize = 0;
                while(newSize != linesList.size()) {
                    String currLine = linesList.get(newSize);
                    if(currLine.contains("*")) {
                        int first = currLine.indexOf("*");
                        int last = currLine.length();   //for some reason length()-1 doesnt include "."
                        linesList.add(newSize, currLine.substring(first,last));
                        linesList.remove(newSize+1);
                        newSize++;
                    }
                    else {
                        linesList.remove(newSize);
                    }

                }



                ArrayList<AbilityNote> noteList = new ArrayList<>();
                for(String line : linesList) {
                    //System.out.println("working line: "+line);
                    int count=0;        //count appearances of '*'
                    for(int i=0; i<line.length()-1; i++) {
                        if(line.charAt(i)=='*') {
                            //System.out.println("found * at "+i);
                            count++;
                        }
                        else
                            break;
                    }
                    if(count==1) {      //top level note -> just add
                        noteList.add(new AbilityNote(line,1));
                    }
                    else {              //not top level -> look for further levels inside newest note
                        AbilityNote currAbility = noteList.get(noteList.size()-1);    //get last 'first level' note
                        boolean inserted = false;
                        while(!inserted) {
                            //if we have a 2nd level note, then we add this note to last 'first level' note
                            //if we have a 3rd level note, get last 'second level' note and add it there. and so on
                            //System.out.println("currAbilityLevel: "+currAbility.getLevel()+", count: "+count);
                            if(currAbility.getLevel() == count-1) {
                                currAbility.addSubnote(line);
                                inserted=true;
                            }
                            else {
                                currAbility = currAbility.getLastAbilityNote();
                            }
                        }
                    }
                }
                linesList.clear();
                hab.setNotesList(noteList);


                Log.d("ABILITIES","Name: "+hab.getName());
                /*Log.d("ABILITIES","Type: "+hab.getType());
                Log.d("ABILITIES","Description: "+hab.getDescription());
                Log.d("ABILITIES","Lore: "+hab.getLore());
                Log.d("ABILITIES","Ability: "+hab.getAbility());
                Log.d("ABILITIES","Affects: "+hab.getAffects());
                Log.d("ABILITIES","Bkbblock: "+hab.getBkbblock());
                Log.d("ABILITIES","Bkbtext: "+hab.getBkbtext());
                Log.d("ABILITIES","Linkenblock: "+hab.getLinkenblock());
                Log.d("ABILITIES","Linkentext: "+hab.getLinkentext());
                Log.d("ABILITIES","Purgeable: "+hab.getPurgeable());
                Log.d("ABILITIES","Purgetext: "+hab.getPurgetext());
                Log.d("ABILITIES","Illusionuse: "+hab.getIllusionuse());
                Log.d("ABILITIES","Illusiontext: "+hab.getIllusiontext());
                Log.d("ABILITIES","Breakable: "+hab.getBreakable());
                Log.d("ABILITIES","Breaktext: "+hab.getBreaktext());
                Log.d("ABILITIES","UAM: "+hab.getUam());
                Log.d("ABILITIES","Castpoint: "+hab.getCastpoint());
                Log.d("ABILITIES","Castbackswing: "+hab.getCastbackswing());
                Log.d("ABILITIES","Traits and Values: "+hab.getTraitsAndValuesStringRepresentation());
                Log.d("ABILITIES","Mana: "+hab.getMana());
                Log.d("ABILITIES","Cooldown: "+hab.getCooldown());
                Log.d("ABILITIES","Aghanimsupgrade: "+hab.getAghanimsupgrade());
                Log.d("ABILITIES","Notes: "+hab.getNotesStringRepresentation());*/



                cv = new ContentValues();
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_HERO_NAME, p.first);
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME, hab.getName());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGENAME, imagename);
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_TYPE, hab.getType());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_DESCRIPTION, hab.getDescription());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LORE, hab.getLore());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ABILITY, hab.getAbility());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AFFECTS, hab.getAffects());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BKBBLOCK, hab.getBkbblock());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BKBTEXT, hab.getBkbtext());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LINKENBLOCK, hab.getLinkenblock());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LINKENKTEXT, hab.getLinkentext());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_PURGEABLE, hab.getPurgeable());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_PURGETEXT, hab.getPurgetext());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ILLUSIONUSE, hab.getIllusionuse());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ILLUSIONTEXT, hab.getIllusiontext());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BREAKABLE, hab.getBreakable());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BREAKTEXT, hab.getBreaktext());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_UAM, hab.getUam());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_CASTPOINT, hab.getCastpoint());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_CASTBACKSWING, hab.getCastbackswing());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_TRAITSANDVALUESLIST, hab.getTraitsAndValuesStringRepresentation());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_MANA, hab.getMana());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_COOLDOWN, hab.getCooldown());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AGHANIMSUPGRADE, hab.getAghanimsupgrade());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NOTESLIST, hab.getNotesStringRepresentation());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_DAMAGETYPE, hab.getDamagetype());
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AFFECTS2, hab.getAffects2());
                //TODO Insert OR (IF EXISTS) Update, check http://stackoverflow.com/a/20568176/5331119
                updatedRows = db.update(DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME, cv, DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME + " like '" + hab.getName().replace("'","''") + "'", null);
                if(updatedRows <= 0)
                    db.insertWithOnConflict(DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME, DotaDBContract.DotaAbilitiesDatabase._ID, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        heronamesFile.delete();
        heroSiteFindingsFile.delete();
        db.close();
        EventBus.getDefault().post(new HerositeUpdateDatabaseUIEvent(progressMax, progressMax, "Updating done.", true, cmds));
    }
}

