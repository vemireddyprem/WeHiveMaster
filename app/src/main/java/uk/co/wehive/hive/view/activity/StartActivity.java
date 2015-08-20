/*******************************************************************************
 PROJECT:       Hive
 FILE:          LoginActivity.java
 DESCRIPTION:   Initial class with options of Login and sign up
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        08/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManageGCM;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.fragment.LoginFragment;
import uk.co.wehive.hive.view.fragment.SignUpFragment;

import static uk.co.wehive.hive.R.id.btn_sign_up;

public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        User userPrefs = ManagePreferences.getUserPreferences();

        if (userPrefs.getId_user() > 0 && !ManagePreferences.isFirstTimeUser()) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("fragment", AppConstants.USER_PROFILE);
            startActivity(intent);
            finish();
        }
        ManageGCM manageGCM = new ManageGCM(this);
        manageGCM.validateGCM();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private Button mImgLogIn;
        private Button mImgSignUp;
        private Button mImgExplore;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if (getActivity() != null) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().hide();
            }

            View fragment = inflater.inflate(R.layout.fragment_start, container, false);
            mImgLogIn = (Button) fragment.findViewById(R.id.btn_log_in);
            mImgLogIn.setOnClickListener(this);

            mImgSignUp = (Button) fragment.findViewById(btn_sign_up);
            mImgSignUp.setOnClickListener(this);

            mImgExplore = (Button) fragment.findViewById(R.id.btn_explore);
            mImgExplore.setOnClickListener(this);

            return fragment;
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
            if (v.equals(mImgLogIn)) {
                fm.replace(R.id.container, new LoginFragment()).addToBackStack("").commit();
            } else if (v.equals(mImgSignUp)) {
                fm.replace(R.id.container, new SignUpFragment()).addToBackStack("").commit();
            } else if (v.equals(mImgExplore)) {
                startActivity(new Intent(getActivity(), ExploreActivity.class));
            }
        }
    }
}
