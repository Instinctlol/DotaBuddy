package jwwu.com.dotabuddy.jobs;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import jwwu.com.dotabuddy.events.DownloadPicturesUIEvent;
import jwwu.com.dotabuddy.jobs.holders.DownloadPicturesHolder;
import jwwu.com.dotabuddy.requests.RequestQueueSingleton;

/**
 * Created by Instinctlol on 21.03.2016.
 */
public class DownloadPictures extends Job {

    public static final int PRIORITY = FindPictures.PRIORITY-1;

    TreeMap<String,String> heroPictureUris, abilityPictureUris;

    private final boolean[] cmds;

    public DownloadPictures(TreeMap<String,String> heroPictureUris, TreeMap<String,String> abilityPictureUris, boolean[] cmds) {
        super(new Params(PRIORITY).requireNetwork().persist().groupBy("DownloadHerosites"));
        this.heroPictureUris = heroPictureUris;
        this.abilityPictureUris = abilityPictureUris;
        this.cmds = cmds;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        final DownloadPicturesHolder downloadPicturesHolder = new DownloadPicturesHolder(heroPictureUris, abilityPictureUris, cmds);

        //download images for heroes
        for(Map.Entry<String,String> heroPictureUri : heroPictureUris.entrySet()) {
            //create Imagerequest for every hero / ability
            ImageRequest request = new ImageRequest(heroPictureUri.getValue(),
                    new MyHeroPictureResponseListener<Bitmap>(heroPictureUri) {
                        @Override
                        public void onResponse(Bitmap response) {
                            downloadPicturesHolder.incrementHeroPictureResponses();
                            EventBus.getDefault().post(new DownloadPicturesUIEvent(downloadPicturesHolder.getFinishedRequests(),
                                    downloadPicturesHolder.totalRequests,
                                    "Downloaded Image "+downloadPicturesHolder.getFinishedRequests()+"/"+downloadPicturesHolder.totalRequests,false));
                            downloadPicturesHolder.heroPictures.put(this.heroPictureUri.getKey(),response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            int  statusCode = error.networkResponse.statusCode;
                            NetworkResponse response = error.networkResponse;

                            Log.d("VOLLEY",""+statusCode+" "+ Arrays.toString(response.data));
                            EventBus.getDefault().post(new DownloadPicturesUIEvent(downloadPicturesHolder.getFinishedRequests(),
                                    downloadPicturesHolder.totalRequests, "VOLLEY ERROR: "+Arrays.toString(response.data),true));
                        }
                    });
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }

        //download images for abilities
        for(final Map.Entry<String,String> abilityPictureUri : abilityPictureUris.entrySet()) {
            //create Imagerequest for every hero / ability
            ImageRequest request = new ImageRequest(abilityPictureUri.getValue(),
                    new MyAbilityPictureResponseListener<Bitmap>(abilityPictureUri) {
                        @Override
                        public void onResponse(Bitmap response) {
                            downloadPicturesHolder.incrementAbilityPictureResponses();
                            EventBus.getDefault().post(new DownloadPicturesUIEvent(downloadPicturesHolder.getFinishedRequests(),
                                    downloadPicturesHolder.totalRequests,
                                    "Downloaded Image "+downloadPicturesHolder.getFinishedRequests()+"/"+downloadPicturesHolder.totalRequests,false));
                            downloadPicturesHolder.abilityPictures.put(this.abilityPictureUri.getKey(), response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            int  statusCode = error.networkResponse.statusCode;
                            NetworkResponse response = error.networkResponse;

                            Log.d("VOLLEY",""+statusCode+" "+ Arrays.toString(response.data));
                            EventBus.getDefault().post(new DownloadPicturesUIEvent(downloadPicturesHolder.getFinishedRequests(),
                                    downloadPicturesHolder.totalRequests, "VOLLEY ERROR: "+Arrays.toString(response.data),true));
                        }
                    });
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }

    private abstract class MyHeroPictureResponseListener<Bitmap> implements Response.Listener<Bitmap> {

        public Map.Entry<String,String> heroPictureUri;

        public MyHeroPictureResponseListener(Map.Entry<String,String> heroPictureUri) {
            this.heroPictureUri = heroPictureUri;
        }

    }

    private abstract class MyAbilityPictureResponseListener<Bitmap> implements Response.Listener<Bitmap> {

        public Map.Entry<String,String> abilityPictureUri;

        public MyAbilityPictureResponseListener(Map.Entry<String,String> abilityPictureUri) {
            this.abilityPictureUri = abilityPictureUri;
        }

    }
}
