package jwwu.com.dotabuddy.abstracts;

import android.graphics.Bitmap;
import android.util.Pair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class VolleyCallback {
    public int successCount =0;
    public int successLimit =0;
    public int count=0;
    public int countLimit=0;
    public String[][] holder;
    public HashMap<Pair<Integer,String>,String> uriHolder,heroesFilepaths,abilitiesFilepaths,heroesUris,abilityUris;
    public void onSuccess(ArrayList<String> result){}
    public void onSuccess(){}
    public void onSuccess(Pair<Integer, String> p, JSONObject response){}
    public void onSuccess(Pair<Integer, String> p, Bitmap bitmap, Integer type, String uri){}
}
