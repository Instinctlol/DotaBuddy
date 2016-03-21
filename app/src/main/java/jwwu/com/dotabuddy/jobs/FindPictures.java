package jwwu.com.dotabuddy.jobs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.events.FindPicturesUIEvent;
import jwwu.com.dotabuddy.jobs.holders.FindPicturesHolder;
import jwwu.com.dotabuddy.requests.RequestQueueSingleton;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class FindPictures extends Job {

    public static final int PRIORITY = UpdateHeroDatabase.PRIORITY-1;

    private FindPicturesHolder findPicturesHolder;
    private final boolean[] cmds;

    public FindPictures(boolean[] cmds) {
        super(new Params(PRIORITY).requireNetwork().persist().groupBy("DownloadHerosites"));
        this.cmds = cmds;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        // Create new helper
        DotaDBSQLiteHelper dbHelper = new DotaDBSQLiteHelper(getApplicationContext());
        // Get the database. If it does not exist, this is where it will also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        final ArrayList<Pair<Integer,String>> heroList = new ArrayList<>();
        final ArrayList<Pair<String,String>> abilityImagenameAndName = new ArrayList<>();
        Cursor cursor = db.rawQuery("select "+ DotaDBContract.DotaHeroesDatabase._ID + DotaDBContract.COMMA_SEP +
                DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME+" from "+DotaDBContract.DotaHeroesDatabase.TABLE_NAME,null);
        if(cursor.moveToFirst()) {
            do {
                heroList.add(new Pair<>(cursor.getInt(cursor.getColumnIndexOrThrow(DotaDBContract.DotaHeroesDatabase._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME))));
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        cursor = db.rawQuery("select "+ DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGENAME + DotaDBContract.COMMA_SEP +
                DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME + " from "+ DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME,null);
        if(cursor.moveToFirst()) {
            do {
                abilityImagenameAndName.add(new Pair<>(cursor.getString(cursor.getColumnIndexOrThrow(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGENAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME))));
            }
            while(cursor.moveToNext());
        }
        cursor.close();

        final ArrayList<String> remainingAbilities= new ArrayList<>();
        for(Pair<String,String> p : abilityImagenameAndName) {
            remainingAbilities.add(p.second);
        }


        int heroListSize = heroList.size();
        int abilityListSize = abilityImagenameAndName.size();
        findPicturesHolder = new FindPicturesHolder(remainingAbilities, heroListSize, abilityListSize, cmds);



        //request Hero image urls, on resonse call heroImageCallback
        for(Pair<Integer,String> p : heroList) {

            EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                    findPicturesHolder.totalRequests,
                    "Sending Searchrequest for Image "+findPicturesHolder.getFinishedRequests()+"/"+findPicturesHolder.totalRequests,false));

            String url = "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix="+p.second.replace(' ','_').replace(":","").replace("!","")+"&aiprop=size|url&aimime=image/png&format=json";
            URLEncoder.encode(url,"UTF-8");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new MyHeroListener<JSONObject>(p) {
                @Override
                public void onResponse(JSONObject response) {
                    findPicturesHolder.unparsedHeroPictures.put(this.hero.second, response);
                    findPicturesHolder.incrementHeroRequests();
                    EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                            findPicturesHolder.totalRequests,
                            "Received Searchrequest for Image "+findPicturesHolder.getFinishedRequests()+"/"+findPicturesHolder.totalRequests,false));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int  statusCode = error.networkResponse.statusCode;
                    NetworkResponse response = error.networkResponse;

                    Log.d("VOLLEY",""+statusCode+" "+ Arrays.toString(response.data));
                    EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                            findPicturesHolder.totalRequests, "VOLLEY ERROR: "+Arrays.toString(response.data),true));
                }
            });
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }

        //request Ability image urls, on response call abilityImageCallback
        for(Pair<String,String> p : abilityImagenameAndName) {
            //TODO this line is a hack, later rewrite ArrayList<Pair> abilityImagenameAndName as TreeMap<String,String> abilityNameAndImagename
            findPicturesHolder.abilityNameAndImagename.put(p.second,p.first);

            EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                    findPicturesHolder.totalRequests,
                    "Sending Searchrequest for Image "+findPicturesHolder.getFinishedRequests()+"/"+findPicturesHolder.totalRequests,false));
            final String url = "http://dota2.gamepedia.com/api.php?action=query&list=allimages&aiprefix=" + p.first.replace(' ','_').replace(":","").replace("!","") +"&aiprop=size|url&aimime=image/png&format=json";
            URLEncoder.encode(url, "UTF-8");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new MyAbilityListener<JSONObject>(p) {
                @Override
                public void onResponse(JSONObject response) {
                    ArrayList<String> consoleList = new ArrayList<>();
                    consoleList.addAll(Arrays.asList("".split("\\n")));
                    consoleList.remove("");
                    findPicturesHolder.unparsedAbilityPictures.put(this.ability.second,response);
                    findPicturesHolder.incrementAbilityRequests();
                    EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                            findPicturesHolder.totalRequests,
                            "Received Searchrequest for Image "+findPicturesHolder.getFinishedRequests()+"/"+findPicturesHolder.totalRequests, false));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int  statusCode = error.networkResponse.statusCode;
                    NetworkResponse response = error.networkResponse;

                    Log.d("VOLLEY",""+statusCode+" "+ Arrays.toString(response.data));
                    EventBus.getDefault().post(new FindPicturesUIEvent(findPicturesHolder.getFinishedRequests(),
                            findPicturesHolder.totalRequests, "VOLLEY ERROR: "+Arrays.toString(response.data),true));
                }
            });
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
        }
    }

    private abstract class MyHeroListener<JSONObject> implements Response.Listener<JSONObject> {
        Pair<Integer,String> hero;

        public MyHeroListener(Pair<Integer,String> hero) {
            this.hero = hero;
        }
    }

    private abstract class MyAbilityListener<JSONObject> implements Response.Listener<JSONObject> {
        Pair<String,String> ability;

        public MyAbilityListener(Pair<String,String> ability) {
            this.ability = ability;
        }
    }
}
