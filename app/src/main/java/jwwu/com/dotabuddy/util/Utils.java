package jwwu.com.dotabuddy.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Instinctlol on 20.03.2016.
 */
public class Utils {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET
    };

    public static void verifyExternalStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.INTERNET)) {

            }
            else {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    public static File writeObjectFileToCache(Context context, String filename, Object object) {
        File f = new File("");
        try {
            f = File.createTempFile(filename, null, context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static Object readObjectFromFile(File f) {
        Object object = new Object();
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            object = ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static File writeBitmapToExternalStorage(String directory, String filename, String format, Bitmap bitmap) {
        File myDir = new File(directory);
        myDir.mkdirs();

        File file = new File(directory, filename);

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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
