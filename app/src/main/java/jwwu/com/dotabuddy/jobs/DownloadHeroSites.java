package jwwu.com.dotabuddy.jobs;

import android.util.Log;

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
import java.util.Collections;

import jwwu.com.dotabuddy.events.DownloadHeroSiteEvent;
import jwwu.com.dotabuddy.events.DownloadHeroSiteUIEvent;
import jwwu.com.dotabuddy.jobs.holders.DownloadHeroSiteHolder;
import jwwu.com.dotabuddy.util.RegExHelper;
import jwwu.com.dotabuddy.requests.RequestQueueSingleton;

/**
 * Created by Instinctlol on 16.03.2016.
 */
public class DownloadHeroSites extends Job {

    public static final int PRIORITY = 100;       //The Priority of this Job.
    public static final int MAXREQUESTS = 40;   //How many Heroes will be searched with one Request? Has to be below 50.

    private int heroesCount;    //How many Heroes are there?

    private String[] requestUrls;   //Every index has following format: 'hero_1|hero_2|...|hero_N'. Will get used by volley.
    private boolean[] cmds;

    ArrayList<String> sortedHeroNames;

    String heronames;


    private DownloadHeroSiteHolder downloadHeroSiteHolder;


    /**
     * Generates Requests, sends them via volley, notifies Subscribers with DownloadHeroSiteEvent.
     * @param heronamesResponse    Unparsed Heronames Site.
     */
    public DownloadHeroSites(String heronamesResponse, boolean[] cmds) {
        super(new Params(PRIORITY).requireNetwork().persist().groupBy("DownloadHerosites"));
        this.heronames = heronamesResponse;
        this.cmds = cmds;
    }

    @Override
    public void onAdded() {
        System.out.println("Added!");
    }

    @Override
    public void onRun() throws Throwable {



        ArrayList<String> findings = RegExHelper.searchForValuesBetweenPrefixAndSuffix("\\{\\{Hero ID\\|", "\\}", heronames);
        RegExHelper.formatArrayListWhitespace(findings);    //replace " " with "_", important for generating URLs


        this.sortedHeroNames = findings;
        Collections.sort(sortedHeroNames);

        heroesCount = sortedHeroNames.size();
        int totalRequests = heroesCount/MAXREQUESTS+1;   //should be 3 for a long time

        this.downloadHeroSiteHolder = new DownloadHeroSiteHolder(sortedHeroNames, totalRequests);

        requestUrls = new String[downloadHeroSiteHolder.totalRequests];

        StringBuilder heroesInUrl;

        int pos = 0;

        //Build request Strings
        for(int i=0; i<downloadHeroSiteHolder.totalRequests; i++) {
            heroesInUrl=new StringBuilder();

            for (int k = 0; k < MAXREQUESTS; k++) {
                if ((pos + 1) % MAXREQUESTS != 0 && pos != heroesCount-1) {  //not the last element of a subrequest and not the last element of the heroes array
                    heroesInUrl.append(sortedHeroNames.get(pos)).append("|");
                }
                else {
                    heroesInUrl.append(sortedHeroNames.get(pos));                    //last elements dont want a vertical bar
                    if(pos==heroesCount-1)                                   //stop building strings after reaching very last element
                        break;
                }

                if(pos<heroesCount-1) {
                    pos++;
                }

            }

            requestUrls[i]=URLEncoder.encode(heroesInUrl.toString(), "UTF-8");
            //System.out.println("requests url "+i+" :"+requestUrls[i]);
        }


        //after building the request strings, send the requests via volley
        //int subrequest marks the id of the subrequest, so we can later find the content of a specific subrequest in the holder of the volleycallback
        for(int subRequest=0; subRequest<requestUrls.length; subRequest++) {
            String url = "http://dota2.gamepedia.com/api.php?action=query&titles="+requestUrls[subRequest]+"&prop=revisions&rvprop=content&format=json";
            //System.out.println(url);

            EventBus.getDefault().post(new DownloadHeroSiteUIEvent(subRequest+1, requestUrls.length, "Sending Herosite Downloadrequest: "+ (subRequest+1) + "/" +requestUrls.length, false, false));

            //give subrequest id to MyListener via parameter, accessible via its subRequestId attribute
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new MyListener<JSONObject>(subRequest) {

                        @Override
                        public void onResponse(JSONObject response) {
                            downloadHeroSiteHolder.unparsedHerositeHolder.put(subRequestID,response.toString());
                            downloadHeroSiteHolder.incrementFinishedRequests();

                            if(downloadHeroSiteHolder.isFinished()) {
                                EventBus.getDefault().post(new DownloadHeroSiteEvent(downloadHeroSiteHolder,cmds));  //notify subscribers
                                EventBus.getDefault().post(new DownloadHeroSiteUIEvent(downloadHeroSiteHolder.getFinishedRequests(), downloadHeroSiteHolder.totalRequests,
                                        "Received all Herosites: done", false, true));
                            }
                            else {
                                EventBus.getDefault().post(new DownloadHeroSiteUIEvent(downloadHeroSiteHolder.getFinishedRequests(), downloadHeroSiteHolder.totalRequests,
                                        "Received all Herosites: "+downloadHeroSiteHolder.getFinishedRequests() + " / " + downloadHeroSiteHolder.totalRequests, false, false));
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO
                            int  statusCode = error.networkResponse.statusCode;
                            NetworkResponse response = error.networkResponse;

                            Log.d("VOLLEY",""+statusCode+" "+ Arrays.toString(response.data));
                            EventBus.getDefault().post(new DownloadHeroSiteUIEvent(downloadHeroSiteHolder.getFinishedRequests(),downloadHeroSiteHolder.totalRequests,
                                    "VOLLEY ERROR: "+Arrays.toString(response.data), true, false));

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
