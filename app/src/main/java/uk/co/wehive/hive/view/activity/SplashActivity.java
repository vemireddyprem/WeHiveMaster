package uk.co.wehive.hive.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.utils.ManagePreferences;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ManagePreferences.setAutocheckin(false);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        };
        timer.schedule(timerTask, 3000);
    }
}
