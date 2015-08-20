package uk.co.wehive.hive.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import uk.co.wehive.hive.R;

public class ManageGCM {

    private Activity activity;
    private GoogleCloudMessaging gcm;

    public ManageGCM(Activity activity) {
        this.activity = activity;
    }

    public void validateGCM() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            String regid = getRegistrationId(activity);
            if (regid.isEmpty() || ManagePreferences.isFirstTimeUser()) {
                try {
                    registerInBackground();
                } catch (Exception ignore) {
                }
            }
        }
    }

    //Step 1: validar si el dispositivo tiene instalado play services
    //Validate if the device has play services installed
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 1).show();
            } else {
                activity.finish();
            }
            return false;
        }
        return true;
    }

    //Step 2: validar que no se hay registrado a GCM
    //Validate that GCM hasn't been registered
    private String getRegistrationId(Context context) {
        //valida si ya el dispositivo esta registrado en el servidor de GCM
        String registrationId = ManagePreferences.getIdGCM();
        if (registrationId.isEmpty()) {
            return "";
        }
        // verifica la version porque puede pasar que no funcione por el cambio de version
        // verify the version because it could stop working with a version change
        int registeredVersion = Integer.parseInt(activity.getResources().getString(R.string.version_app));
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    //Step 3
    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());
                    }
                    String regid = gcm.register(AppConstants.SENDER_ID_GCM);
                    //msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    ManagePreferences.setIdGCM(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }
}
