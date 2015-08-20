package uk.co.wehive.hive.utils.camera;

import android.graphics.Bitmap;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import uk.co.wehive.hive.utils.AppConstants;

public class ScreenshotHelper {

    public static String takeScreenshot(RelativeLayout view) {

        view.setDrawingCacheEnabled(true);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String memePath = AppConstants.IMAGE_FROM_MEME_PATH + nextPhotoId() + "tmp_meme.jpg";
        saveBitmap(bitmap, memePath);
        return memePath;
    }

    public static void saveBitmap(Bitmap bmp, String path) {
        try {
            File f = new File(path);
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String nextPhotoId() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }
}