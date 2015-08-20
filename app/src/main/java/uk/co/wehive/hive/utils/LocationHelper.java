package uk.co.wehive.hive.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for getting the user's location using either the GPS satellites or the
 * WiFi. It will set a time out of 20 secs. After it, it will try to use the latest known location
 * (if no location has been fetched yet).
 */
public class LocationHelper {

    private static final String TAG = LocationHelper.class.getSimpleName();
    private static final int LOCATION_TIMEOUT = 20000;
    private static LocationHelper instance;

    private final LocationManager mLocationManager;
    private final List<LocationResult> mLocationListeners;
    private final SimpleLocationListener mGpsListener = new SimpleLocationListener(LocationManager.GPS_PROVIDER);
    private final SimpleLocationListener mNetworkListener = new SimpleLocationListener(LocationManager.NETWORK_PROVIDER);

    private boolean mGpsEnabled = false;
    private boolean mNetworkEnabled = false;

    private LocationHelper(Context context) {
        mLocationListeners = new ArrayList<LocationResult>();
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocationHelper(context);
        }
        return instance;
    }

    /**
     * Try to get current location.
     *
     * @param result  a callback listener
     * @param timeout a timeout in millisecs. If after this timeout, location has not been find, it will try to use the
     *                latest known location or null if there is no location at all.
     * @return true if either GPS or Network locators are enabled
     */
    public boolean requestLocation(LocationResult result, long timeout) {
        if (result != null && !mLocationListeners.contains(result)) {
            mLocationListeners.add(result);
        }

        udpateProvidersFlags();

        //don't start listeners if no provider is enabled
        if (!mGpsEnabled && !mNetworkEnabled) {
            return false;
        }

        if (mGpsEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsListener);
        }
        if (mNetworkEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkListener);
        }
        mTimeoutHandler.sendEmptyMessageDelayed(0, timeout);
        return true;
    }

    private void udpateProvidersFlags() {
        try {
            mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
    }

    /**
     * Try to get current location. Timeout by default is 20 secs.
     *
     * @param result a callback listener
     */
    public void requestLocation(LocationResult result) {
        requestLocation(result, LOCATION_TIMEOUT);
    }

    public Location getBestLocation() {
        udpateProvidersFlags();

        Location netLocation = null, gpsLoc = null;
        if (mGpsEnabled) {
            gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (mNetworkEnabled) {
            netLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        //if there are both values use the latest one
        if (gpsLoc != null && netLocation != null) {
            if (gpsLoc.getTime() > netLocation.getTime()) {
                return gpsLoc;
            } else {
                return netLocation;
            }
        }

        if (gpsLoc != null) {
            return gpsLoc;
        }
        return netLocation == null ? null : netLocation;
    }

    public Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            //Log.d("last known location, provider: %s, location: %s", provider, l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                // ALog.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    private class SimpleLocationListener implements LocationListener {
        private final String mProvider;

        public SimpleLocationListener(String provider) {
            mProvider = provider;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, String.format("Got location from %s provider", mProvider));
            mTimeoutHandler.removeMessages(0);
            callbackLocationAndStop(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    private void callbackLocationAndStop(Location location) {
        for (LocationResult listener : mLocationListeners) {
            listener.gotLocation(location);
        }
    }

    private final Handler mTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "::::::::::: Timeout... could not find the location");
            callbackLocationAndStop(getBestLocation());
        }
    };

    public interface LocationResult {
        void gotLocation(Location location);
    }
}
