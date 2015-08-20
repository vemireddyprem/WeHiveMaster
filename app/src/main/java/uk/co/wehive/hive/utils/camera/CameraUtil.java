package uk.co.wehive.hive.utils.camera;

import android.content.Context;
import android.content.pm.PackageManager;

public class CameraUtil {

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }
}
