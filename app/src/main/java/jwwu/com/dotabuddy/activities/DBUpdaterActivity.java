package jwwu.com.dotabuddy.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import jwwu.com.dotabuddy.helpers.RegExHelper;
import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.dota_logic.AbilityNote;
import jwwu.com.dotabuddy.dota_logic.Balancechangelog;
import jwwu.com.dotabuddy.dota_logic.HeroAbility;
import jwwu.com.dotabuddy.dota_logic.HeroStats;
import jwwu.com.dotabuddy.abstracts.VolleyCallback;
import jwwu.com.dotabuddy.requests.RequestQueueSingleton;

public class DBUpdaterActivity extends AppCompatActivity {

    private enum DownloadHeroSiteOperation {
        UPDATESTATS, UPDATEABILITIES
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbupdater);

        final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
        prg.setProgress(0);
        prg.setMax(100);
        prg.setVisibility(View.INVISIBLE);

        final TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setVisibility(View.INVISIBLE);

        final Button btn = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void update() {
        final CheckBox cb1 = (CheckBox) findViewById(R.id.checkBox);        //Heroes&Stats
        final CheckBox cb2 = (CheckBox) findViewById(R.id.checkBox2);       //Abilities
        final CheckBox cb3 = (CheckBox) findViewById(R.id.checkBox3);       //Pictures
        final Button btn = (Button) findViewById(R.id.button2);

        cb1.setEnabled(false);
        cb2.setEnabled(false);
        cb3.setEnabled(false);

        //cmds contains the status of all checkboxes, which indicates an operation to be performed.
        //after a operation is finished, that operation has to change its
        // representative successCount to false
        final boolean[] cmds = {cb1.isChecked(), cb2.isChecked(), cb3.isChecked()};
        btn.setEnabled(false);

        new UpdateTaskHelper(cmds).execute();
        }

    //'Mother Task' of all the tasks
    private class UpdateTaskHelper extends AsyncTask<Void, Void, Integer> {

        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean[] cmds;

        public UpdateTaskHelper(boolean[] cmds) {
            this.cmds=cmds;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int execute=-1;

            //check which operation is next
            for(int i = 0; i<cmds.length; i++){
                if(cmds[i]) {
                    execute=i;
                    break;
                }
            }

            Cursor cursor;

            switch(execute) {
                case 0: //UpdateHeroes & Stats
                    //Get JSON via volley, on success start Task to update heroes & stats, which
                    //consequently will launch other tasks, depending on state of checkboxes
                    parseHeroNamesJSON(new VolleyCallback() {
                        @Override
                        public void onSuccess(ArrayList<String> result) {
                            new UpdateHeroNamesDBTask(cmds).execute(result);
                        }
                    });
                    break;
                case 1: //UpdateAbilities

                    //TODO make sure there are actually heroes inside the database
                    ArrayList<String> heroesList = new ArrayList<>();
                    cursor = db.rawQuery("select "+ DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME+" from "+DotaDBContract.DotaHeroesDatabase.TABLE_NAME,null);
                    if(cursor.moveToFirst()) {
                        do {
                            heroesList.add(cursor.getString(0));
                        }
                        while(cursor.moveToNext());
                    }
                    cursor.close();

                    final String[] heroes = heroesList.toArray(new String[heroesList.size()]);    //convert List to Array

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DownloadHeroSitesTask(heroes, cmds).execute(DownloadHeroSiteOperation.UPDATEABILITIES);
                        }
                    });

                    break;
                case 2: //UpdatePictures
                    //TODO: new UpdatePicturesTask

                    final ArrayList<Pair<Integer,String>> abilityIdsAndNames = new ArrayList<>();
                    final ArrayList<Pair<Integer,String>> heroList = new ArrayList<>();
                    cursor = db.rawQuery("select "+ DotaDBContract.DotaAbilitiesDatabase._ID + DotaDBContract.COMMA_SEP +
                            DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME + " from "+ DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME,null);
                    if(cursor.moveToFirst()) {
                        do {
                            abilityIdsAndNames.add(new Pair<>(cursor.getInt(0), cursor.getString(1)));
                        }
                        while(cursor.moveToNext());
                    }
                    cursor.close();

                    cursor = db.rawQuery("select "+ DotaDBContract.DotaHeroesDatabase._ID + DotaDBContract.COMMA_SEP +
                            DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME+" from "+DotaDBContract.DotaHeroesDatabase.TABLE_NAME,null);
                    if(cursor.moveToFirst()) {
                        do {
                            heroList.add(new Pair<>(cursor.getInt(0), cursor.getString(1)));
                        }
                        while(cursor.moveToNext());
                    }
                    cursor.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new DownloadPicturesTask(heroList,abilityIdsAndNames, cmds).execute();
                        }
                    });



                    break;
                default: //Do nothing
                    //TODO: delete/uncomment database debug operations
                    /* OUTPUT WHOLE DATABASE */
                    Cursor curs = db.rawQuery("select * from "+ DotaDBContract.DotaHeroesDatabase.TABLE_NAME,null);
                    if(curs.moveToFirst()) {
                        do {
                            int id = -1;
                            if(!curs.isNull(0)) {
                                Log.d("herostatstest", "id: " + curs.getString(0));
                                id = curs.getInt(0);
                            }
                            if(!curs.isNull(1))
                                Log.d("herostatstest","name: " + curs.getString(1));
                            if(!curs.isNull(2)) {
                                Log.d("herostatstest","stats: " + curs.getString(2));
                            }
                            if(!curs.isNull(3))
                                Log.d("herostatstest","balancechangelog: " + curs.getString(3));
                            if(!curs.isNull(4))
                                Log.d("herostatstest", "picture: " + curs.getString(4));
                            if(id>=0) {
                                Cursor cursAbility = db.rawQuery("select * from "+ DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME + " where "+ DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_HERO_ID+" = "+id,null);
                                if(cursAbility.moveToFirst()) {
                                    do {
                                        for(int i=0; i<cursAbility.getColumnCount(); i++) {
                                            if(!cursAbility.isNull(i)) {
                                                Log.d("abilitytest", cursAbility.getColumnName(i) + " " + cursAbility.getString(i));
                                            }
                                        }
                                    }
                                    while(cursAbility.moveToNext());
                                }
                            }
                        }
                        while(curs.moveToNext());
                    }
                    curs.close();
                    break;
            }
            db.close();
            return execute;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            //No operations left
            if(integer==-1) {
                TextView tv = (TextView) findViewById(R.id.textView2);
                tv.setText("done.");

                Button btn = (Button) findViewById(R.id.button2);
                btn.setEnabled(true);

                CheckBox cb1 = (CheckBox) findViewById(R.id.checkBox);
                CheckBox cb2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox cb3 = (CheckBox) findViewById(R.id.checkBox3);

                cb1.setEnabled(true);
                cb2.setEnabled(true);
                cb3.setEnabled(true);

            }
        }

        private void parseHeroNamesJSON(final VolleyCallback callback) {
            String url = "http://dota2.gamepedia.com/api.php?action=query&titles=Heroes_by_release&prop=revisions&rvprop=content&format=json";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<String> findings = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\{\\{Hero ID\\|", "\\}", response.toString());
                            RegExHelper.formatArrayListWhitespace(findings);    //replace " " with "_", important for generating URLs
                            //Log.d("jsontest", findings.toString());
                            callback.onSuccess(findings);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
    }

    //puts hero names in db, launch DownloadHerositesDBTask
    private class UpdateHeroNamesDBTask extends AsyncTask<ArrayList<String>, String, String[]>{
        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean[] cmds;

        public UpdateHeroNamesDBTask(boolean[] cmds) {
            this.cmds=cmds;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TextView tv = (TextView) findViewById(R.id.textView2);
            ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);

            //Update UI, so user sees that we are updating Heroes
            tv.setText("..updating Heroes");
            tv.setVisibility(View.VISIBLE);
            prg.setProgress(0);
            prg.setIndeterminate(true);
            prg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(ArrayList<String>... heroes) {

            //sort heroes alphabetically
            String[] allHeroesSorted = heroes[0].toArray(new String[heroes[0].size()]);
            Arrays.sort(allHeroesSorted);

            for(String hero : allHeroesSorted){
                ContentValues cv = new ContentValues();
                cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME, hero);
                db.insertWithOnConflict(DotaDBContract.DotaHeroesDatabase.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.close();
            return allHeroesSorted;
        }

        @Override
        protected void onPostExecute(final String[] heroes) {
            super.onPostExecute(heroes);

            //getting all Herosites
            //TODO please work with Pair<Integer,String> from here on. Containing: (hero_id,hero_name), not just the names!!!
            new DownloadHeroSitesTask(heroes,cmds).execute(DownloadHeroSiteOperation.UPDATESTATS);
        }
    }

    //This task will generate multiple requests to get all the herosites via web api over volley.
    //After calling execute(), this task creates a volleycallback, which will be called after every request.
    //If the volleycallback recognizes all requests as finished, it will then concatenate all the responses and pass it to UpdateStatsForHeroesDBTask
    private class DownloadHeroSitesTask extends AsyncTask<DownloadHeroSiteOperation,Void,Void> {

        private String[] heroes;
        private boolean[] cmds;


        public DownloadHeroSitesTask(String[] parHeroes, boolean[] parCmds) {
            heroes=parHeroes;
            cmds=parCmds;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            int length = heroes.length;
            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Downloading Herosites");

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setProgress(0);
            prg.setMax(length);
            prg.setIndeterminate(true);
        }

        @Override
        protected Void doInBackground(final DownloadHeroSiteOperation... op) {

            //create volleycallback, which will check if volley finished downloading all herosites
            VolleyCallback volleyCallback = new VolleyCallback() {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    this.successCount++;

                    //System.out.println("VolleyCallBack Success! successCount: "+this.successCount);

                    if(successCount == successLimit) {          //all herosites downloaded
                        int length=0;
                        for(String[] arr : this.holder)     //holder is a 2-dimensional String array, every array contains the requested downloaded herosites. for every request 1 array.
                            length += arr.length;

                        String[] allNonParsedHeroSites = new String[length];    //concatenate the arrays to 1 array
                        int pos=0;
                        for(String[] arr : this.holder) {
                            for(String str : arr) {
                                if(pos<length) {
                                    allNonParsedHeroSites[pos]=str;
                                    pos++;
                                }
                            }
                        }
                        switch(op[0]) {
                            case UPDATESTATS:
                                //must execute a String array containing each heroes' herosite, !!sorted alphabetically!!
                                new UpdateStatsForAllHeroesDBTask(heroes,cmds).execute(allNonParsedHeroSites);
                                break;
                            case UPDATEABILITIES:
                                new UpdateAbilitiesForAllHeroesDBTask(heroes,cmds).execute(allNonParsedHeroSites);
                                break;
                        }
                    }
                }
            };


            getAllHeroSites(heroes, volleyCallback);


            return null;
        }

        //TODO looks like performance is taking a large hit here, put asynctask in onResponse/callback
        //gets all Herosites from the webapi, requests have to be split, the volleycallback has to
        //listen to the responses and start the next task after recognizing all requests as finished
        private void getAllHeroSites(final String[] heroes, final VolleyCallback volleyCallback) {
            StringBuilder heroesInUrl;

            int length = heroes.length;
            int maxRequests = 40;
            int totalRequests = length/maxRequests+1;   //should be 3 for a long time

            int pos = 0;

            //every index will hold: a String array, each index containing a heroes' unparsed site
            volleyCallback.holder=new String[totalRequests][];
            volleyCallback.successLimit =totalRequests;

            //on each index you will find following format: 'hero_1|hero_2|...|hero_N'
            //which is the requestvariable for the webapi
            String[] requestUrls = new String[totalRequests];

            //generate subrequests so we dont overload the server
            //build the string for all heroes in this format: 'hero_1|hero_2|...|hero_50'
            for(int i=0; i<totalRequests; i++) {
                heroesInUrl=new StringBuilder();

                for (int k = 0; k < maxRequests; k++) {
                    if ((pos + 1) % maxRequests != 0 && pos != length-1) {  //not the last element of a subrequest and not the last element of the heroes array
                        heroesInUrl.append(heroes[pos]).append("|");
                    }
                    else {
                        heroesInUrl.append(heroes[pos]);                    //last elements dont want a vertical bar
                        if(pos==length-1)                                   //stop building strings after reaching very last element
                            break;
                    }

                    if(pos<length-1) {
                        pos++;
                    }

                }

                requestUrls[i]=heroesInUrl.toString();
                //System.out.println("requests url "+i+" :"+requestUrls[i]);
            }

            //after building the request strings, send the requests via volley
            //int subrequest marks the id of the subrequest, so we can later find the content of a specific subrequest in the holder of the volleycallback
            for(int subRequest=0; subRequest<requestUrls.length; subRequest++) {
                String url = "http://dota2.gamepedia.com/api.php?action=query&titles="+requestUrls[subRequest]+"&prop=revisions&rvprop=content&format=json";


                //give subrequest id to MyListener via parameter, accessible via its subRequestId attribute
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new MyListener<JSONObject>(subRequest) {

                            @Override
                            public void onResponse(JSONObject response) {

                                //finding RegEx's is hard...
                                //ArrayList<String> findings = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\{\\{Hero infobox\\n","\\n\\{\\{HeroNav\\}\\}",response.toString());
                                //ArrayList<String> findings = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\{\\{Hero infobox\\\\n","\\\\n\\{\\{HeroNav\\}\\}",response.toString());
                                //HeroNav not a good ending for a hero site, because unreleased heroes instead end with \n{{Unreleased hero navbox}}
                                ArrayList<String> findings = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\{\\{Hero infobox\\\\n","\\}\\}\\\"\\}\\]\\}",response.toString());

                                int length = findings.size();
                                String[] subRequestParsedHeroSites = findings.toArray(new String[length]);      //convert arraylist to array
                                /*System.out.println("overall "+length+"sites in this response \\n{{HeroNav}}");
                                for(String a : subRequestParsedHeroSites) {
                                    System.out.println("NonparsedHeroSite: "+a);
                                }*/


                                volleyCallback.holder[this.subRequestID]=subRequestParsedHeroSites;
                                volleyCallback.onSuccess();



                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });
                RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
            }
        }

        private abstract class MyListener<JSONObject> implements Response.Listener<JSONObject> {

            int subRequestID;

            public MyListener(int subRequestID) {
                this.subRequestID=subRequestID;
            }
        }
    }

    //This task parses the sites of each hero into HeroStats and updates each Hero's stats in the database
    private class UpdateStatsForAllHeroesDBTask extends AsyncTask<String[],Integer,Void> {
        boolean[] cmds;
        String[] allHeroes;
        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /**
         *
         * @param allHeroes contains all the found heroes from UpdateHeroNamesDBTask
         * @param cmds contains the chosen operations by the user
         */
        public UpdateStatsForAllHeroesDBTask(String[] allHeroes, boolean[] cmds) {
            this.cmds=cmds;
            this.allHeroes=allHeroes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setIndeterminate(false);
            prg.setProgress(0);
            prg.setMax(allHeroes.length);

            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Parsing stats for every hero");
        }

        //params[0] = array containing nonParsedHeroSites, !!alphabetically sorted!!
        @Override
        protected Void doInBackground(String[]... params) {
            final String[] allHeroSites = params[0];

            int length = allHeroSites.length;
            //Log.d("IndexOutOfBounds","Length of allHeroSites: "+allHeroSites.length);     //110
            //Log.d("IndexOutOfBounds","Length of allHeroes: "+allHeroes.length);           //112

            for(int i=0; i<length; i++) {
                //parse all Herosites into HeroStats Objects, then update database entry
                HeroStats hs = new HeroStats();

                publishProgress(i);

                hs.setPrimaryAttribute((RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " primary attribute = (.*?)\\\\n \\|", allHeroSites[i])).get(0));
                hs.setStrength(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " strength = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setStrengthGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " strength growth = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setAgility(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " agility = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setAgilityGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " agility growth = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setIntelligence(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " intelligence = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setIntelligenceGrow(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " intelligence growth = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setDamageMin(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " damage min = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setDamageMax(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " damage max = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setArmor(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " armor = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setMoveSpeed(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " movement speed = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setAttackRange(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " attack range = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setAttackPoint(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " attack point = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setAttackBackswing(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " attack backswing = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setBat(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " bat = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setMissileSpeed(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +        //some Strings can be empty, because e.g. not every hero is ranged ;) KEEP THIS IN MIND!!
                        " missile speed = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setSightRangeDay(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " sight range day = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setSightRangeNight(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " sight range night = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setTurnRate(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " turn rate = (.*?)\\\\n \\|", allHeroSites[i]).get(0));
                hs.setCollisionSize(RegExHelper.searchPatternInSourceRemoveWhitespace("\\|" +
                        " collision size = (.*?)\\\\n", allHeroSites[i]).get(0));




                String nonParsedChangelog = RegExHelper.searchForValuesBetweenPrefixAndSuffix("== Balance changelog ==\\\\n\\{\\{Update History\\|\\\\n", "\\\\n\\}\\}\\\\n\\\\n==", allHeroSites[i]).get(0);
                String[] lines = nonParsedChangelog.split("\\\\n");

                Balancechangelog bc = new Balancechangelog();

                for(String line : lines) {
                    bc.feedNonParsedLine(line);
                }

                ContentValues cv = new ContentValues();
                cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_STATS, HeroStats.getStringRepresentation(hs));
                cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_BALANCECHANGELOG, bc.getStringRepresentation());
                //update stats entry, where heroname = allHeroes[i]
                db.update(DotaDBContract.DotaHeroesDatabase.TABLE_NAME, cv,
                        DotaDBContract.DotaHeroesDatabase._ID + " = " + (i+1), null);
            }
            db.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setProgress(values[0]);

            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("parsing and updating stats for " + allHeroes[values[0]] + " in database");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setProgress(prg.getMax());

            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("done updating database for every hero");

            //first operation is over
            cmds[0]=false;
            //start next operation
            new UpdateTaskHelper(cmds).execute();
        }
    }

    private class UpdateAbilitiesForAllHeroesDBTask extends AsyncTask<String[],String,Void> {
        boolean[] cmds;
        String[] allHeroes;
        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public UpdateAbilitiesForAllHeroesDBTask(String[] parHeroes, boolean[] parCmds) {
            this.cmds=parCmds;
            this.allHeroes=parHeroes;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            int length = allHeroes.length;
            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Updating Heroabilities");

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setProgress(0);
            prg.setMax(length);
            prg.setIndeterminate(false);
        }



        @Override
        protected Void doInBackground(String[]... params) {
            String[] nonParsedHeroSites = params[0];
            String[] filteredAbilities = new String[nonParsedHeroSites.length];

            for (int i=0; i<nonParsedHeroSites.length; i++) {
                filteredAbilities[i] = RegExHelper.searchForValuesBetweenPrefixAndSuffix("== Abilities ==","==", nonParsedHeroSites[i]).get(0);
            }

            for(int h=0; h<filteredAbilities.length-1; h++) {
                String abilityFilter = filteredAbilities[h];
                String currHero = allHeroes[h];
                int currId = -1;

                publishProgress(currHero);

                //get ID from currHero
                if(currHero.contains("'"))
                    currHero=currHero.replace("'","''");

                Cursor cursor = db.rawQuery("select "+ DotaDBContract.DotaHeroesDatabase._ID+
                        " from "+DotaDBContract.DotaHeroesDatabase.TABLE_NAME+
                        " where "+DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME+
                        " = '"+currHero+"'",null);
                if(cursor.moveToFirst()) {
                    do {
                        currId=cursor.getInt(0);
                    }
                    while(cursor.moveToNext());
                }
                cursor.close();


                ArrayList<String> splittedAbilities = new ArrayList<>();
                Collections.addAll(splittedAbilities, abilityFilter.split("\\{\\{Ability\\\\n"));
                splittedAbilities.remove(0);                                                        //first entry is no ability



                for(String ability : splittedAbilities) {


                    HeroAbility hab = new HeroAbility();
                    hab.setName(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| name = ", "\\\\n", ability).get(0));
                    //hab.setImage(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| image = ","\\\\n\\|",ability).get(0));
                    hab.setType(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| type = ", "\\\\n", ability).get(0));
                    hab.setDescription(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| description = ", "\\\\n", ability).get(0));
                    hab.setLore(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| lore = ", "\\\\n", ability).get(0));
                    hab.setAbility(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| ability = ", "\\\\n", ability).get(0));
                    hab.setAffects(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| affects = ", "\\\\n", ability).get(0));
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
                    hab.setCastpoint(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| castpoint = ", "\\\\n", ability).get(0));
                    hab.setCastbackswing(RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| castbackswing = ", "\\\\n", ability).get(0));
                    //Traits and Values
                    boolean maximumFound = false;
                    int curr = 1;
                    while(!maximumFound) {
                        String first = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| trait"+curr+" = ","\\\\n",ability).get(0);
                        String second = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\| value"+curr+" = ","\\\\n",ability).get(0);
                        hab.getTraitsAndValuesList().add(new Pair<>(first, second));

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
                    ArrayList<String> lines = new ArrayList<>();
                    Collections.addAll(lines,notes.split("\\\\n"));

                    //edit every line
                    //for every line get the substring of the first '*' and last '.' (including), unmatched and old lines get deleted
                    int newSize = 0;
                    while(newSize != lines.size()) {
                        String currLine = lines.get(newSize);
                        if(currLine.contains("*")) {
                            int first = currLine.indexOf("*");
                            int last = currLine.length();   //for some reason length()-1 doesnt include "."
                            lines.add(newSize, currLine.substring(first,last));
                            lines.remove(newSize+1);
                            newSize++;
                        }
                        else {
                            lines.remove(newSize);
                        }

                    }



                    ArrayList<AbilityNote> noteList = new ArrayList<>();
                    for(String line : lines) {
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
                    lines.clear();
                    hab.setNotesList(noteList);



                    /*System.out.println("Name: "+hab.getName());
                    System.out.println("Type: "+hab.getType());
                    System.out.println("Description: "+hab.getDescription());
                    System.out.println("Lore: "+hab.getLore());
                    System.out.println("Ability: "+hab.getAbility());
                    System.out.println("Affects: "+hab.getAffects());
                    System.out.println("Bkbblock: "+hab.getBkbblock());
                    System.out.println("Bkbtext: "+hab.getBkbtext());
                    System.out.println("Linkenblock: "+hab.getLinkenblock());
                    System.out.println("Linkentext: "+hab.getLinkentext());
                    System.out.println("Purgeable: "+hab.getPurgeable());
                    System.out.println("Purgetext: "+hab.getPurgetext());
                    System.out.println("Illusionuse: "+hab.getIllusionuse());
                    System.out.println("Illusiontext: "+hab.getIllusiontext());
                    System.out.println("Breakable: "+hab.getBreakable());
                    System.out.println("Breaktext: "+hab.getBreaktext());
                    System.out.println("UAM: "+hab.getUam());
                    System.out.println("Castpoint: "+hab.getCastpoint());
                    System.out.println("Castbackswing: "+hab.getCastbackswing());
                    System.out.println("Traits and Values: "+hab.getTraitsAndValuesStringRepresentation());
                    System.out.println("Mana: "+hab.getMana());
                    System.out.println("Cooldown: "+hab.getCooldown());
                    System.out.println("Aghanimsupgrade: "+hab.getAghanimsupgrade());
                    System.out.println("Notes: "+hab.getNotesStringRepresentation());*/



                    ContentValues cv = new ContentValues();
                    cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_HERO_ID, currId);
                    cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME, hab.getName());
                    //cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGE, hab.getImage());
                    cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME, hab.getName());
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
                    db.insertWithOnConflict(DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME, DotaDBContract.DotaAbilitiesDatabase._ID, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }

            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //second operation is over
            cmds[1]=false;
            //start next operation
            new UpdateTaskHelper(cmds).execute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.incrementProgressBy(1);

            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Parsing & Updating abilities for " + values[0]);
        }

    }

    private class DownloadPicturesTask extends AsyncTask<Void,String,Void> {

        ArrayList<Pair<Integer, String>> heroList, abilityIdsAndNames;
        boolean[] cmds;

        public DownloadPicturesTask(ArrayList<Pair<Integer, String>> heroList, ArrayList<Pair<Integer, String>> abilityIdsAndNames, boolean[] cmds) {
            this.heroList=heroList;
            this.abilityIdsAndNames=abilityIdsAndNames;
            this.cmds=cmds;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Downloading Images");
            tv.setVisibility(View.VISIBLE);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.setProgress(0);
            prg.setMax(heroList.size()+abilityIdsAndNames.size());
            prg.setIndeterminate(true);
            prg.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Downloaded Image url for " +values[0]);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.incrementProgressBy(1);
        }

        @Override
        protected Void doInBackground(Void... params) {

            final ArrayList<String> remainingAbilities= new ArrayList<>();
            for(Pair<Integer,String> p : abilityIdsAndNames) {
                remainingAbilities.add(p.second);
            }


            final VolleyCallback createImageCallback = new VolleyCallback() {
                @Override
                public void onSuccess(final Pair<Integer, String> p, final Bitmap bitmap, final Integer type, final String uri) {
                    super.onSuccess(p, bitmap, type, uri);

                    successCount++;

                    //Heavy work --> do work in own Asynctask
                    new SaveImagesTask(p, bitmap, type, uri, successCount, successLimit, heroesFilepaths, abilitiesFilepaths, cmds).execute();


                }
            };

            //This callback gets called after we have found all the urls for every image to download,
            //it will then create requests for all the urls and call createImageCallback on response
            final VolleyCallback downloadImageCallback = new VolleyCallback() {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    successCount++;

                    if(successCount == successLimit) {
                        if(remainingAbilities.size()<20) {
                            String rest = "";

                            for(String s : remainingAbilities) {
                                rest+=s+",";
                            }

                            System.out.println("Could not find images to remaining abilities: " + rest + ".. I will ignore this.");
                        }



                        //maximum calls for next callback: amount of found urls for every hero/ability
                        createImageCallback.successCount=0;
                        createImageCallback.successLimit=heroesUris.size()+abilityUris.size();
                        createImageCallback.heroesFilepaths=new HashMap<>();
                        createImageCallback.abilitiesFilepaths=new HashMap<>();

                        final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
                        prg.setProgress(0);
                        prg.setMax(createImageCallback.successLimit);
                        prg.setIndeterminate(false);
                        prg.setVisibility(View.VISIBLE);


                        //download images for heroes
                        for(final Pair<Integer,String> p : heroesUris.keySet()) {
                            //create Imagerequest for every hero / ability
                            ImageRequest request = new ImageRequest(heroesUris.get(p),
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            //write bitmap to external storage
                                            String uri = heroesUris.get(p);
                                            createImageCallback.onSuccess(p,bitmap,1,uri);
                                        }
                                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                        }

                        //download images for abilities
                        for(final Pair<Integer,String> p : abilityUris.keySet()) {
                            //create Imagerequest for every hero / ability
                            ImageRequest request = new ImageRequest(abilityUris.get(p),
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            //write bitmap to external storage
                                            String uri = abilityUris.get(p);
                                            createImageCallback.onSuccess(p,bitmap,2,uri);
                                        }
                                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                        }

                    }
                }
            };

            downloadImageCallback.successCount =0;
            downloadImageCallback.successLimit =2;
            downloadImageCallback.count=0;
            downloadImageCallback.abilitiesFilepaths=new HashMap<>();
            downloadImageCallback.heroesFilepaths=new HashMap<>();


            //this callback gets called later after finding an image url for a specific hero
            //after finding all images for every hero, it will then call downloadImageCallback
            final VolleyCallback heroImageCallback = new VolleyCallback() {
                @Override
                public void onSuccess(Pair<Integer,String> p, JSONObject response) {
                    super.onSuccess(p, response);

                    successCount++;

                    JSONArray array = response.optJSONObject("query").optJSONArray("allimages");

                    //get image url and put it inside uriHolder with Pair as key (ID,Name)
                    for(int i = 0; i<array.length(); i++) {
                        JSONObject jo = array.optJSONObject(i);

                        if(jo.optInt("width")==256 && jo.optInt("height")==144) {
                            this.uriHolder.put(p, jo.optString("url"));
                            publishProgress(p.second);
                            break;
                        }
                    }


                    if(successCount == successLimit) {
                        downloadImageCallback.heroesUris = uriHolder;
                        downloadImageCallback.onSuccess();
                    }

                }
            };

            heroImageCallback.successCount=0;
            heroImageCallback.successLimit =heroList.size();
            heroImageCallback.uriHolder=new HashMap<>();

            //request Hero image urls, on resonse call heroImageCallback
            for(final Pair<Integer,String> p : heroList) {

                final String url = "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix="+p.second.replace(' ','_').replace(":","").replace("!","")+"&aiprop=size|url&aimime=image/png&format=json";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        heroImageCallback.onSuccess(p, response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Could not download from " + url);
                    }
                });
                RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
            }

            //this callback gets called later after finding an image url for a specific ability
            //after finding all images for every ability, it will then call downloadImageCallback
            final VolleyCallback abilityImageCallback = new VolleyCallback() {
                @Override
                public void onSuccess(Pair<Integer, String> p, JSONObject response) {
                    super.onSuccess(p, response);

                    successCount++;

                    JSONArray array = response.optJSONObject("query").optJSONArray("allimages");

                    //get image url and put it inside uriHolder with Pair as key (ID,Name)
                    for(int i = 0; i<array.length(); i++) {
                        JSONObject jo = array.optJSONObject(i);

                        if(jo.optInt("width")==128 && jo.optInt("height")==128) {
                            this.uriHolder.put(p, jo.optString("url"));
                            publishProgress(p.second);
                            remainingAbilities.remove(p.second);
                            break;
                        }
                    }

                    if(successCount== successLimit) {
                        downloadImageCallback.abilityUris = uriHolder;
                        downloadImageCallback.onSuccess();
                    }
                }
            };


            abilityImageCallback.successCount=0;
            abilityImageCallback.successLimit =abilityIdsAndNames.size();
            abilityImageCallback.uriHolder=new HashMap<>();

            //request Ability image urls, on response call abilityImageCallback
            for(final Pair<Integer,String> p : abilityIdsAndNames) {

                final String url;
                switch(p.second.toLowerCase()) {    //some abilities use an image different from their name -> hardcoding :(
                    case "chakram":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Chakram_icon&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "chakram (aghanim's scepter)":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Chakram_2_icon&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "return chakram (aghanim's scepter)":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Return_Chakram_2_icon&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "return chakram":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Return_Chakram_icon&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "eyes in the forest":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Eyes_in_the_Forest&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "intelligence steal":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Glaives_of_Wisdom&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    case "walrus punch!":
                        url= "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=Walrus_Punch&aiprop=size|url&aimime=image/png&format=json";
                        break;
                    default:
                        url = "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=" + p.second.replace(' ','_').replace(":","").replace("!","")+"&aiprop=size|url&aimime=image/png&format=json";
                        break;
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        abilityImageCallback.onSuccess(p, response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Could not download from " + url);
                    }
                });
                RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
            }


            return null;
        }
    }

    private class SaveImagesTask extends AsyncTask<Void,String,Void> {


        private final Pair<Integer, String> p;
        private final Bitmap bitmap;
        private final Integer type;
        private final String uri;
        private final int successCount;
        private final int successLimit;
        private final HashMap<Pair<Integer, String>, String> heroesFilepaths;
        private final HashMap<Pair<Integer, String>, String> abilitiesFilepaths;
        private boolean[] cmds;

        public SaveImagesTask(Pair<Integer, String> p, Bitmap bitmap, Integer type, String uri,
                              int successCount, int successLimit,
                              HashMap<Pair<Integer, String>, String> heroesFilepaths,
                              HashMap<Pair<Integer, String>, String> abilitiesFilepaths,
                              boolean[] cmds) {

            this.p = p;
            this.bitmap = bitmap;
            this.type = type;
            this.uri = uri;
            this.successCount = successCount;
            this.successLimit = successLimit;
            this.heroesFilepaths = heroesFilepaths;
            this.abilitiesFilepaths = abilitiesFilepaths;
            this.cmds = cmds;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Downloaded and saved image for " + values[0]);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //all images downloaded --> save filepaths in database
            if(successLimit==successCount) {
                new UpdateImagesDBTask(heroesFilepaths,abilitiesFilepaths,cmds).execute();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            int dot = uri.lastIndexOf(".");
            String format = uri.substring(dot + 1);

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/dotabuddy");
            myDir.mkdirs();


            String fname;       //name + .png/.jpg
            int slash = uri.lastIndexOf("/");
            fname = uri.substring(slash + 1, dot + 1)+format;
            try {
                fname = URLDecoder.decode(fname, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



            File file = new File(myDir, fname);

            if (file.exists())
                file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                switch (format.toLowerCase()) {
                    case "png":
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        break;
                    case "jpg":
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        break;
                    case "jpeg":
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        break;
                    default:
                        System.out.println("invalid format");
                        break;
                }

                out.flush();
                out.close();

                //save filepath
                if(type==1)
                    heroesFilepaths.put(p,file.getAbsolutePath());
                if(type==2)
                    abilitiesFilepaths.put(p,file.getAbsolutePath());


                publishProgress(p.second);




            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class UpdateImagesDBTask extends AsyncTask<Void,String,Void>{

        HashMap<Pair<Integer,String>,String> heroes,abilities;
        boolean[] cmds;

        public UpdateImagesDBTask(HashMap<Pair<Integer,String>,String> heroFilepaths, HashMap<Pair<Integer,String>,String> abilityFilepaths, boolean[] cmds) {
            this.heroes=heroFilepaths;
            this.abilities=abilityFilepaths;
            this.cmds=cmds;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Updating Imagepaths in Database");

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            int size=heroes.size()+abilities.size();
            prg.setProgress(0);
            prg.setMax(size);
            prg.setIndeterminate(false);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            final ProgressBar prg = (ProgressBar) findViewById(R.id.progressBar);
            prg.incrementProgressBy(1);

            final TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText("Updated image for " + values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //third operation is over
            cmds[2]=false;
            new UpdateTaskHelper(cmds).execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create new helper
            DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
            // Get the database. If it does not exist, this is where it will also be created.
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for(Pair<Integer,String> p : heroes.keySet()) {
                publishProgress(p.second);

                ContentValues cv = new ContentValues();

                //Image-Key from Hero or Ability + Filepath of image
                cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_PICTURE,heroes.get(p));
                db.update(DotaDBContract.DotaHeroesDatabase.TABLE_NAME, cv, DotaDBContract.DotaHeroesDatabase._ID + "=" + p.first, null);
            }

            for(Pair<Integer,String> p : abilities.keySet()) {
                publishProgress(p.second);

                ContentValues cv = new ContentValues();

                //Image-Key from Hero or Ability + Filepath of image
                cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGE,abilities.get(p));
                db.update(DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME, cv, DotaDBContract.DotaAbilitiesDatabase._ID + "=" + p.first, null);
            }
            db.close();
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dbupdater, menu);
        return true;
    }



}
