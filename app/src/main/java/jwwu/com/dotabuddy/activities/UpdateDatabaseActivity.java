package jwwu.com.dotabuddy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.path.android.jobqueue.JobManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import jwwu.com.dotabuddy.R;
import jwwu.com.dotabuddy.events.DownloadHeroSiteEvent;
import jwwu.com.dotabuddy.events.DownloadHeroSiteUIEvent;
import jwwu.com.dotabuddy.events.DownloadPicturesUIEvent;
import jwwu.com.dotabuddy.events.FindPicturesEvent;
import jwwu.com.dotabuddy.events.FindPicturesUIEvent;
import jwwu.com.dotabuddy.events.HerositeUpdateDatabaseUIEvent;
import jwwu.com.dotabuddy.events.UpdatePictureDatabaseLaunchEvent;
import jwwu.com.dotabuddy.events.UpdatePictureDatabaseUIEvent;
import jwwu.com.dotabuddy.jobs.DownloadPictures;
import jwwu.com.dotabuddy.jobs.FindPictures;
import jwwu.com.dotabuddy.jobs.UpdatePictureDatabase;
import jwwu.com.dotabuddy.util.RegExHelper;
import jwwu.com.dotabuddy.jobs.DownloadHeroSites;
import jwwu.com.dotabuddy.jobs.JobManagerSingleton;
import jwwu.com.dotabuddy.jobs.UpdateHeroDatabase;
import jwwu.com.dotabuddy.requests.RequestQueueSingleton;
import jwwu.com.dotabuddy.util.Utils;

public class UpdateDatabaseActivity extends AppCompatActivity {

    private static final String HEROSITE_START = "\\{\\{Hero infobox\\\\n";
    private static final String HEROSITE_END = "\\\"\\}\\]\\}";

    ProgressBar progressBar;
    TextView progressBarStatus, logMessages;
    Button btn;
    CheckBox cb1, cb2;

    private JobManager jobManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbupdater);

        jobManager = JobManagerSingleton.getInstance().getJobManager(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);

        cb1 = (CheckBox) findViewById(R.id.checkBox);        //Heroes&Stats
        cb2 = (CheckBox) findViewById(R.id.checkBox3);       //Picture

        progressBarStatus = (TextView) findViewById(R.id.progressBarStatus);
        progressBarStatus.setVisibility(View.INVISIBLE);

        logMessages = (TextView) findViewById(R.id.logMessages);
        logMessages.setText("");

        btn = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Utils.verifyExternalStoragePermissions(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void update() {
        cb1.setEnabled(false);
        cb2.setEnabled(false);

        logMessages.setText("");

        //cmds contains the status of all checkboxes, which indicates an operation to be performed.
        //after a operation is finished, that operation has to change its
        // representative successCount to false
        boolean[] cmds = new boolean[2];
        cmds[0] = cb1.isChecked();
        cmds[1] = cb2.isChecked();

        btn.setEnabled(false);

        executeNextCommand(cmds);
    }

    private void executeNextCommand(boolean[] cmds) {
        int execute=-1;     //which cmd to run

        //check which operation is next
        for(int i = 0; i<cmds.length; i++){
            if(cmds[i]) {
                execute=i;
                break;
            }
        }


        switch(execute) {
            case 0: //DownloadHeroSites & Stats
                //Get JSON via volley, on success start Task to update heroes & stats, which
                //consequently will launch other tasks, depending on state of checkboxes
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);
                progressBarStatus.setText("Downloading Heronames");
                progressBarStatus.setVisibility(View.VISIBLE);
                downloadHeroNamesAndPassToDownloadHeroSites(cmds);
                break;
            case 1: //UpdatePictures
                progressBar.setIndeterminate(false);
                progressBar.setMax(100);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                progressBarStatus.setText("Finding Images from Web-API");
                progressBarStatus.setVisibility(View.VISIBLE);
                JobManager jobManager = JobManagerSingleton.getInstance().getJobManager(this);
                jobManager.addJobInBackground(new FindPictures(cmds));
                break;
            default:
                cb1.setEnabled(true);
                cb2.setEnabled(true);
                btn.setEnabled(true);
                progressBarStatus.setText("Finished.");
                progressBar.setIndeterminate(false);
                progressBar.setProgress(progressBar.getMax());
                progressBar.setVisibility(View.VISIBLE);
                progressBarStatus.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void downloadHeroNamesAndPassToDownloadHeroSites(final boolean[] cmds) {
        String url = "http://dota2.gamepedia.com/api.php?action=query&titles=Heroes_by_release&prop=revisions&rvprop=content&format=json";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBarStatus.setText("Downloading Herosites");
                        jobManager.addJobInBackground(new DownloadHeroSites(response.toString(),cmds));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDownloadHeroSiteEvent(DownloadHeroSiteEvent event) {
        TreeMap<Integer,String> unparsedHerositeHolder = event.downloadHeroSiteHolder.unparsedHerositeHolder;

        ArrayList<String> heroSiteFindings = new ArrayList<>();
        for(String value : unparsedHerositeHolder.values()) {
            heroSiteFindings.addAll(RegExHelper.searchForValuesBetweenPrefixAndSuffix(HEROSITE_START,HEROSITE_END, value));
        }
        unparsedHerositeHolder.clear();

        jobManager = JobManagerSingleton.getInstance().getJobManager(getApplicationContext());
        jobManager.addJob(new UpdateHeroDatabase(this, event.downloadHeroSiteHolder.heronames, heroSiteFindings, event.cmds));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadHeroSiteUIEvent(DownloadHeroSiteUIEvent event) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(event.progressMax);
        progressBar.setProgress(event.progress);
        if(!event.isError)
            progressBarStatus.setText(event.message);
        else
            logMessages.append(event.message);
        if(event.isFinished) {
            logMessages.append("Downloading Herosites: done\n");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHeroSiteUpdateDatabaseUIEvent(HerositeUpdateDatabaseUIEvent event) {
        progressBarStatus.setText(event.message);
        progressBar.setMax(event.progressMax);
        progressBar.setProgress(event.progress);
        if(event.isFinished) {
            logMessages.append("Updating Herodatabase: done\n");
            progressBar.setProgress(progressBar.getMax());
            event.cmds[0] = false;
            executeNextCommand(event.cmds);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFindPicturesEvent(FindPicturesEvent event) {
        TreeMap<String,String> heroPictureUris = new TreeMap<>();
        TreeMap<String,String> abilityPictureUris = new TreeMap<>();

        //Find correct Hero Picture to download
        for(Map.Entry<String,JSONObject> heroEntry : event.findPicturesHolder.unparsedHeroPictures.entrySet()) {
            JSONArray array = heroEntry.getValue().optJSONObject("query").optJSONArray("allimages");

            //get image url and put it inside uriHolder with Pair as key (ID,Name)
            for(int i = 0; i<array.length(); i++) {
                JSONObject jo = array.optJSONObject(i);

                if(jo.optInt("width")==256 && jo.optInt("height")==144) {
                    heroPictureUris.put(heroEntry.getKey(),jo.optString("url"));
                    break;
                }
            }
        }
        event.findPicturesHolder.unparsedHeroPictures.clear();

        for(Map.Entry<String,JSONObject> abilityEntry : event.findPicturesHolder.unparsedAbilityPictures.entrySet()) {
            JSONArray array = abilityEntry.getValue().optJSONObject("query").optJSONArray("allimages");

            String abilityName = abilityEntry.getKey();
            String imageName = event.findPicturesHolder.abilityNameAndImagename.get(abilityName);
            String first = imageName.substring(0,1);                            //First char has to be Uppercase
            first = first.toUpperCase();
            imageName = first + imageName.substring(1,imageName.length());


            Log.d("PICS","Ability: "+abilityName+", Imagename: "+imageName);

            //get image url and put it inside uriHolder with Pair as key (ID,Name)
            for(int i = 0; i<array.length(); i++) {
                JSONObject jo = array.optJSONObject(i);

                String jsonNameField = jo.optString("name");
                int dot = jsonNameField.lastIndexOf(".");
                jsonNameField = jsonNameField.substring(0,dot);    // new String without ".png" or whatever fileending
                if(jsonNameField.contains("_icon"))
                    jsonNameField = jsonNameField.substring(0,jsonNameField.length()-5); //new String without "_icon"
                jsonNameField = jsonNameField.replace("_"," ");
                Log.d("PICS","jsonNameField: "+jsonNameField);

                if(jo.optInt("width")==128 && jo.optInt("height")==128 && jsonNameField.equals(imageName)) {
                    abilityPictureUris.put(abilityName, jo.optString("url"));
                    event.findPicturesHolder.remainingAbilities.remove(abilityName);
                    break;
                }
            }
        }

        String rest="";
        for(String s : event.findPicturesHolder.remainingAbilities) {
            rest+=s+",";
        }
        Log.d("PICS","Could not find images to remaining abilities: " + rest + ".. I will ignore this.");

        if(!rest.isEmpty())
            EventBus.getDefault().post(new DownloadPicturesUIEvent(0,100,"Could not find image for: "+rest+"\n",true));

        jobManager = JobManagerSingleton.getInstance().getJobManager(this);
        jobManager.addJob(new DownloadPictures(heroPictureUris,abilityPictureUris,event.findPicturesHolder.cmds));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindPicturesUIEvent(FindPicturesUIEvent event) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(event.total);
        progressBar.setProgress(event.progress);
        if(!event.isError)
            progressBarStatus.setText(event.message);
        else
            logMessages.append(event.message);
        if(event.isFinished)
            logMessages.append("Finding Images from Web-API: done\n");
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUpdatePictureDatabaseLaunchEvent(UpdatePictureDatabaseLaunchEvent event) {
        JobManager jobManager = JobManagerSingleton.getInstance().getJobManager(this);
        jobManager.addJob(new UpdatePictureDatabase(event.heroPictures,event.abilityPictures,event.heroPictureUris,event.abilityPictureUris,event.cmds));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePictureDatabaseUIEvent(UpdatePictureDatabaseUIEvent event) {
        progressBar.setMax(event.total);
        progressBar.setProgress(event.progress);
        progressBarStatus.setText(event.message);
        if(event.isFinished) {
            logMessages.append("Updating Picturedatabase: done\n");
            event.cmds[1] = false;
            System.out.println("abc contents cmds: "+event.cmds[0]+","+event.cmds[1]);
            executeNextCommand(event.cmds);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadPicturesUIEvent(DownloadPicturesUIEvent event) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(event.progress);
        progressBar.setMax(event.total);
        if(!event.isError)
            progressBarStatus.setText(event.message);
        else
            logMessages.append(event.message);
        if(event.isFinished)
            logMessages.append("Downloading Images: done\n");
    }
}
