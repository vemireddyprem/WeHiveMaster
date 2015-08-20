/*******************************************************************************
 PROJECT:       Hive
 FILE:          ApplicationHive.java
 DESCRIPTION:   Entity for parse result of webservice
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.utils;

import android.app.Application;
import android.content.Context;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ApplicationHive extends Application{

    private static Context mContext;
    private static RestAdapter mRestAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = super.getApplicationContext();

        OkHttpClient http_client = new OkHttpClient();
        http_client.setReadTimeout(5, TimeUnit.MINUTES);
        http_client.setConnectTimeout(5, TimeUnit.MINUTES);
        mRestAdapter = new RestAdapter.Builder().setClient(
                new OkClient(http_client)).setEndpoint(AppConstants.WEHIVE_URL)
        /*.setLog(new RestAdapter.Log() {
            @Override
            public void log(String log) {
                Log.i("Tag retrofit", log);
            }
        }).setLogLevel(RestAdapter.LogLevel.FULL)*/
                .build();

        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
    }

    public static Context getContext() {
        return mContext;
    }

    public static RestAdapter getRestAdapter() {
        return mRestAdapter;
    }
}
