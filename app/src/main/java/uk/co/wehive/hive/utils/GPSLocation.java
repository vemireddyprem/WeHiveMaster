/*******************************************************************************
 PROJECT:       Hive
 FILE:          GPSLocation.java
 DESCRIPTION:   Class to get GPS coordinates. Updates 60 sec
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        21/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;

public class GPSLocation implements LocationListener {

    private static final long mRefreshTime = 60000;

    private String mUserId;
    private UsersBL mUserBL;
    private LocationManager mLocationManager;
    private boolean mIsGpsEnabled = false;
    private boolean mNetworkEnabled = false;
    private User user;

    public GPSLocation(Context ctx) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        mUserBL = new UsersBL();
        user = ManagePreferences.getUserPreferences();
    }

    @Override
    public void onLocationChanged(Location location) {
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        String mStrProvider = location.getProvider();
        mUserBL.checkinbycoords(user.getToken(),mUserId, mLatitude + "", mLongitude + "");
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void startGPS() {
        if (mIsGpsEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mRefreshTime, 0, this);
        }

        if (mNetworkEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mRefreshTime, 0, this);
        }
    }

    public void removeGPS() {
        mLocationManager.removeUpdates(this);
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }
}
