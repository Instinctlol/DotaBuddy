package jwwu.com.dotabuddy.dota_logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.activities.DotaSingletonInitializerActivity;
import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.util.Utils;

/**
 * Created by Instinctlol on 13.03.2016.
 */
public class DotaSingleton {

    private HashMap<String, Hero> heroHashMap;
    private ArrayList<Hero> heroArrayList;
    private ArrayList<String> heroNameArrayList;
    private boolean initializing;

    private static DotaSingleton ourInstance = null;
    private BoolHolder boolHolder = new BoolHolder();

    public static synchronized DotaSingleton getInstance() {
        if(ourInstance == null) {
            Log.d("SNGLTN","creating new singleton");
            ourInstance = new DotaSingleton();
        }


        return ourInstance;
    }

    private DotaSingleton() {
        heroHashMap = new HashMap<>();
        heroArrayList = new ArrayList<>();
        heroNameArrayList = new ArrayList<>();
    }

    public synchronized void setup(final Activity activity, TextView textView, ProgressBar progressBar) {
        if(!isInitialized() && !initializing) {
            synchronized (activity) {
                if(!initializing) {
                    Log.d("SNGTLN","Setting up Singleton");
                    new HeroListInitializerTask(activity, getHeroHashMap(), textView, progressBar, boolHolder, heroArrayList, heroNameArrayList).execute();
                }
                initializing = true;
            }
        }

    }

    public synchronized void init(final Activity activity) {
        synchronized (activity) {
            if(!isInitialized() && !initializing) {
                Log.d("SNGTLN","initializing Singleton");
                Intent initializeIntent = new Intent(activity,DotaSingletonInitializerActivity.class);
                activity.startActivity(initializeIntent);
            }
        }
    }

    public synchronized HashMap<String, Hero> getHeroHashMap() {
        Log.d("SNGLTN","HeroHashMap size: "+heroHashMap.size());
        return heroHashMap;
    }

    public ArrayList<Hero> getHeroes() {
        return heroArrayList;
    }

    public ArrayList<String> getHeroNames() {
        return heroNameArrayList;
    }

    public Hero getHero(String name) {
        return heroHashMap.get(name);
    }

    public boolean isInitialized() {
        return boolHolder.getBool();
    }



    private class BoolHolder {
        private boolean initialized = false;

        public synchronized void setTrue() {
            initialized=true;
        }
        public synchronized  void setFalse() {
            initialized=false;
        }

        public synchronized boolean getBool() {
            return initialized;
        }
    }

    private class HeroListInitializerTask extends AsyncTask<Void,String,Void> {

        HashMap<String, Hero> heroHashMap;
        ArrayList<String> heroNameArrayList;
        ArrayList<Hero> heroArrayList;
        Activity activity;
        TextView textView;
        ProgressBar progressBar;
        BoolHolder boolHolder;

        public HeroListInitializerTask(Activity activity, HashMap<String, Hero> heroHashMap,
                                       TextView textView, ProgressBar progressBar,
                                       BoolHolder boolHolder, ArrayList<Hero> heroArrayList,
                                       ArrayList<String> heroNameArrayList) {
            this.heroHashMap = heroHashMap;
            this.boolHolder = boolHolder;
            this.activity = activity;
            this.textView = textView;
            this.progressBar = progressBar;
            this.heroArrayList = heroArrayList;
            this.heroNameArrayList = heroNameArrayList;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setProgress(progressBar.getMax());
            textView.setText("Finished!");
            boolHolder.setTrue();
            activity.finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);


            progressBar.setMax(Integer.parseInt(values[1]));

            progressBar.incrementProgressBy(1);

            textView.setText("Loading " + values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create new helper
            DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(activity.getApplicationContext());
            // Get the database. If it does not exist, this is where it will also be created.
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor heroCurs = db.rawQuery("select *"+
                    " from "+ DotaDBContract.DotaHeroesDatabase.TABLE_NAME,null);

            String max = ""+heroCurs.getCount();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;


            float density = activity.getResources().getDisplayMetrics().density;
            //TODO I dont think allocating abilityimages in Singleton is needed
            /*int reqWidthDpAbility = (int) (activity.getResources().getDimension(R.dimen.smallabilitywidth) / density);
            int reqHeightDpAbility = (int) (activity.getResources().getDimension(R.dimen.smallabilityheight) / density);*/
            int reqWidthDpHero = (int) (activity.getResources().getDimension(R.dimen.smallherowidth) / density);
            int reqHeightDpHero = (int) (activity.getResources().getDimension(R.dimen.smallheroheight) / density);



            if(heroCurs.moveToFirst()) {
                do {
                    String name = heroCurs.getString(heroCurs.getColumnIndexOrThrow(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME)).replace("_"," ");

                    publishProgress(name,max);

                    String portraitPath = heroCurs.getString(heroCurs.getColumnIndexOrThrow(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_PICTURE));
                    //Bitmap portrait = Utils.loadScaledDownBitmapFromFile(portraitPath,reqWidthDpHero,reqHeightDpHero,activity);
                    Bitmap portrait = BitmapFactory.decodeFile(portraitPath, options);
                    HeroStats stats = new HeroStats(heroCurs.getString(heroCurs.getColumnIndexOrThrow(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_STATS)));

                    ArrayList<HeroAbility> abilities = new ArrayList<>();
                    Cursor abilityCurs = db.rawQuery("select * from "+ DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME + " where "+ DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_HERO_NAME+" like '"+name.replace("'","''").replace(" ","_")+"'",null);
                    if(abilityCurs.moveToFirst()) {
                        do {
                            HeroAbility hab = new HeroAbility();

                            HashMap<String, Integer> indexes = new HashMap<>();
                            indexes.put("name",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME));
                            indexes.put("ability",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ABILITY));
                            indexes.put("affects",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AFFECTS));
                            indexes.put("aghanimsupgrade",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AGHANIMSUPGRADE));
                            indexes.put("bkbblock",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BKBBLOCK));
                            indexes.put("bkbtext",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BKBTEXT));
                            indexes.put("breakable",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BREAKABLE));
                            indexes.put("breaktext",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_BREAKTEXT));
                            indexes.put("castbackswing",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_CASTBACKSWING));
                            indexes.put("castpoint",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_CASTPOINT));
                            indexes.put("cooldown",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_COOLDOWN));
                            indexes.put("description",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_DESCRIPTION));
                            indexes.put("illusiontext",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ILLUSIONTEXT));
                            indexes.put("imagepath",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGEPATH));
                            indexes.put("illusionuse",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_ILLUSIONUSE));
                            indexes.put("linkenblock",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LINKENBLOCK));
                            indexes.put("linkentext",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LINKENKTEXT));
                            indexes.put("lore",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_LORE));
                            indexes.put("mana",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_MANA));
                            indexes.put("noteslist",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NOTESLIST));
                            indexes.put("purgeable",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_PURGEABLE));
                            indexes.put("purgetext",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_PURGETEXT));
                            indexes.put("traitsandvalues",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_TRAITSANDVALUESLIST));
                            indexes.put("type",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_TYPE));
                            indexes.put("uam",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_UAM));
                            indexes.put("damagetype",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_DAMAGETYPE));
                            indexes.put("affects2",abilityCurs.getColumnIndex(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_AFFECTS2));

                            for(Map.Entry<String, Integer> e : indexes.entrySet()) {
                                if(e.getValue()>=0) {
                                    String s = abilityCurs.getString(e.getValue());

                                    switch(e.getKey()) {
                                        case "name":
                                            hab.setName(s.replace("_"," "));
                                            //System.out.println(s);
                                            break;
                                        case "ability":
                                            hab.setAbility(s);
                                            break;
                                        case "affects":
                                            hab.setAffects(s);
                                            break;
                                        case "affects2":
                                            hab.setAffects2(s);
                                            break;
                                        case "aghanimsupgrade":
                                            hab.setAghanimsupgrade(s);
                                            break;
                                        case "bkbblock":
                                            hab.setBkbblock(s);
                                            break;
                                        case "bkbtext":
                                            hab.setBkbtext(s);
                                            break;
                                        case "breakable":
                                            hab.setBreakable(s);
                                            break;
                                        case "breaktext":
                                            hab.setBreaktext(s);
                                            break;
                                        case "castbackswing":
                                            hab.setCastbackswing(s);
                                            break;
                                        case "castpoint":
                                            hab.setCastpoint(s);
                                            break;
                                        case "cooldown":
                                            hab.setCooldown(s);
                                            break;
                                        case "description":
                                            hab.setDescription(s);
                                            break;
                                        case "illusiontext":
                                            hab.setIllusiontext(s);
                                            break;
                                        case "imagepath":
                                            //System.out.println(s);
                                            hab.setImagepath(s);
                                            //TODO I dont think allocating abilityimages in Singleton is needed
                                            /*if(s!= null && !s.isEmpty())
                                                hab.setImage(Utils.loadScaledDownBitmapFromFile(s,reqWidthDpAbility,reqHeightDpAbility,activity));
                                            else
                                                hab.setImage(BitmapFactory.decodeResource(activity.getResources(),R.drawable.unknown_icon));*/
                                            break;
                                        case "illusionuse":
                                            hab.setIllusiontext(s);
                                            break;
                                        case "linkenblock":
                                            hab.setLinkenblock(s);
                                            break;
                                        case "linkentext":
                                            hab.setLinkentext(s);
                                            break;
                                        case "lore":
                                            hab.setLore(s);
                                            break;
                                        case "mana":
                                            hab.setMana(s);
                                            break;
                                        case "noteslist":
                                            //System.out.println("Noteslist: "+s);
                                            hab.putFromNotesStringRepresentation(s);
                                            break;
                                        case "purgeable":
                                            hab.setPurgeable(s);
                                            break;
                                        case "purgetext":
                                            hab.setPurgetext(s);
                                            break;
                                        case "traitsandvalues":
                                            //System.out.println("TraitsAndValues: "+s);
                                            hab.putFromTraitsAndValuesStringRepresentation(s);
                                            break;
                                        case "type":
                                            hab.setType(s);
                                            break;
                                        case "uam":
                                            hab.setUam(s);
                                            break;
                                        case "damagetype":
                                            hab.setDamagetype(s);
                                            break;
                                        default:
                                            System.out.println("Did not match: "+e.getKey());
                                            break;
                                    }
                                }
                                else {
                                    System.out.println("Column not found: "+e.getKey());
                                }
                            }
                            abilities.add(hab);
                        }
                        while(abilityCurs.moveToNext());
                    }
                    abilityCurs.close();

                    heroHashMap.put(name,new Hero(name,portrait,portraitPath,stats,abilities));

                }
                while(heroCurs.moveToNext());
            }
            heroCurs.close();

            heroArrayList.addAll(heroHashMap.values());
            Collections.sort(heroArrayList, new Comparator<Hero>() {
                @Override
                public int compare(Hero lhs, Hero rhs) {
                    return lhs.mName.compareTo(rhs.mName);
                }
            });

            heroNameArrayList.addAll(heroHashMap.keySet());
            Collections.sort(heroNameArrayList);

            return null;
        }
    }
}
