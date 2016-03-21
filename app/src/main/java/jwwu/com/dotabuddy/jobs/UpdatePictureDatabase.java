package jwwu.com.dotabuddy.jobs;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

import jwwu.com.dotabuddy.database.DotaDBContract;
import jwwu.com.dotabuddy.database.DotaDBSQLiteHelper;
import jwwu.com.dotabuddy.events.UpdatePictureDatabaseUIEvent;
import jwwu.com.dotabuddy.util.Utils;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class UpdatePictureDatabase extends Job {

    public static final int PRIORITY = FindPictures.PRIORITY-1;

    private TreeMap<String, Bitmap> heroPictures, abilityPictures;
    private TreeMap<String, String> heroPictureUris, abilityPictureUris;
    private final boolean[] cmds;

    public UpdatePictureDatabase(TreeMap<String, Bitmap> heroPictures,
                                 TreeMap<String, Bitmap> abilityPictures,
                                 TreeMap<String, String> heroPictureUris,
                                 TreeMap<String, String> abilityPictureUris, boolean[] cmds) {
        super(new Params(PRIORITY).groupBy("DownloadHerosites"));
        this.heroPictures = heroPictures;
        this.abilityPictures = abilityPictures;
        this.heroPictureUris = heroPictureUris;
        this.abilityPictureUris = abilityPictureUris;
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
        ContentValues cv;

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = root+"/dotabuddy";
        Log.d("PICS",Environment.getExternalStorageState());

        String uri;

        final int total = heroPictures.size() + abilityPictures.size();
        int progress = 0;

        for(Map.Entry<String,Bitmap> heroBitmapEntry : heroPictures.entrySet()) {
            EventBus.getDefault().post(new UpdatePictureDatabaseUIEvent(progress++,total,"Saving image for: "+heroBitmapEntry.getKey(),cmds));
            uri = heroPictureUris.get(heroBitmapEntry.getKey());
            File f = generateBitmapFile(dir,uri,heroBitmapEntry.getValue());
            Log.d("PICS","Saved hero-pic ("+progress+"/"+total+") for "+heroBitmapEntry.getKey() + ", uri: "+uri+" , path:"+f.getAbsolutePath());
            cv = new ContentValues();
            cv.put(DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_PICTURE,f.getAbsolutePath());
            db.update(DotaDBContract.DotaHeroesDatabase.TABLE_NAME, cv, DotaDBContract.DotaHeroesDatabase.COLUMN_NAME_NAME + " like '" + heroBitmapEntry.getKey().replace("'","''") +"'", null);
        }
        heroPictures.clear();
        heroPictureUris.clear();

        for(Map.Entry<String,Bitmap> abilityBitmapEntry : abilityPictures.entrySet()) {
            EventBus.getDefault().post(new UpdatePictureDatabaseUIEvent(progress++,total,"Saving image for: "+abilityBitmapEntry.getKey(),cmds));

            uri = abilityPictureUris.get(abilityBitmapEntry.getKey());
            File f = generateBitmapFile(dir,uri,abilityBitmapEntry.getValue());
            Log.d("PICS","Saving ability-pic ("+progress+"/"+total+") for "+abilityBitmapEntry.getKey() + ", uri: "+uri+" , path:"+f.getAbsolutePath());
            cv = new ContentValues();
            cv.put(DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_IMAGEPATH, f.getAbsolutePath());
            db.update(DotaDBContract.DotaAbilitiesDatabase.TABLE_NAME, cv, DotaDBContract.DotaAbilitiesDatabase.COLUMN_NAME_NAME + " like '" + abilityBitmapEntry.getKey().replace("'","''") +"'", null);
        }
        abilityPictures.clear();
        abilityPictureUris.clear();

        EventBus.getDefault().post(new UpdatePictureDatabaseUIEvent(total,total,"Saving image: done",cmds));
    }

    private File generateBitmapFile(String dir, String uri, Bitmap bitmap) {
        int dot = uri.lastIndexOf(".");
        String format = uri.substring(dot + 1);

        int slash = uri.lastIndexOf("/");
        String fname = uri.substring(slash + 1, dot + 1)+format;
        try {
            fname = URLDecoder.decode(fname, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Utils.writeBitmapToExternalStorage(dir,fname,format,bitmap);
    }
}
