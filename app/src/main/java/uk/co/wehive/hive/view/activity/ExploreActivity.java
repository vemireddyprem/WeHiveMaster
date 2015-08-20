/*******************************************************************************
 PROJECT:       Hive
 FILE:          ExploreActivity.java
 DESCRIPTION:   Main view for explore mode
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        06/10/2014  Marcela Güiza    1. Created explore mode.
 *******************************************************************************/
package uk.co.wehive.hive.view.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.view.fragment.EventsFragment;

public class ExploreActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explore_fregment);

        initViewComponents();

        EventsFragment eventsFragment = new EventsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.EXPLORE_MODE, true);
        eventsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_frame, eventsFragment).addToBackStack("").commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lly_action_bar_menu:
            case R.id.img_action_bar_icon:
            case R.id.img_wehive_actionbar:
                this.finish();
                break;
        }
    }

    private void initViewComponents() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));

        View customView = getSupportActionBar().getCustomView();

        customView.findViewById(R.id.lly_action_bar_menu).setOnClickListener(this);
        customView.findViewById(R.id.img_action_bar_icon).setOnClickListener(this);
        customView.findViewById(R.id.img_wehive_actionbar).setOnClickListener(this);
    }
}