package jwwu.com.dotabuddy.events;

import android.graphics.Bitmap;

import java.util.TreeMap;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class UpdatePictureDatabaseLaunchEvent {

    public final TreeMap<String, Bitmap> heroPictures, abilityPictures;
    public final TreeMap<String, String> heroPictureUris, abilityPictureUris;
    public final boolean[] cmds;

    public UpdatePictureDatabaseLaunchEvent(TreeMap<String, Bitmap> heroPictures,
                                            TreeMap<String, Bitmap> abilityPictures,
                                            TreeMap<String, String> heroPictureUris,
                                            TreeMap<String, String> abilityPictureUris, boolean[] cmds) {
        this.heroPictures = heroPictures;
        this.abilityPictures = abilityPictures;
        this.heroPictureUris = heroPictureUris;
        this.abilityPictureUris = abilityPictureUris;
        this.cmds = cmds;
    }
}
