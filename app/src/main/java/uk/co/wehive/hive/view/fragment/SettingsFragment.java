package uk.co.wehive.hive.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.io.File;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.activity.StartActivity;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class SettingsFragment extends GATrackerFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    private ProgressHive mProgressHive;
    private String mUserId;
    private boolean mIsAutoCheckIn = false;
    private User userPreference;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UiLifecycleHelper uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.menu_option_settings));

        mIsAutoCheckIn = ManagePreferences.isAutocheckin();
        userPreference = ManagePreferences.getUserPreferences();

        initViewComponents();
    }

    private void initViewComponents() {
        mProgressHive = new ProgressHive();
        getView().findViewById(R.id.btn_terms_and_conditions).setOnClickListener(this);
        getView().findViewById(R.id.btn_change_password).setOnClickListener(this);
        getView().findViewById(R.id.btn_logout).setOnClickListener(this);

        ToggleButton tgbCheckEvents = (ToggleButton) getView().findViewById(R.id.tgb_check_events);
        tgbCheckEvents.setChecked(mIsAutoCheckIn);

        mUserId = String.valueOf(ManagePreferences.getUserPreferences().getId_user());
        tgbCheckEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ManagePreferences.setAutocheckin(isChecked);
                UsersBL user = new UsersBL();
                user.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        mProgressHive.dismiss();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
                        mProgressHive.dismiss();
                    }
                });
                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mProgressHive.setCancelable(false);
                user.changeAutoCheckin(userPreference.getToken(),mUserId,
                        String.valueOf(ManagePreferences.isAutocheckin())
                );
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_change_password:
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("")
                        .replace(R.id.content_frame, new ChangePasswordFragment()).commit();
                break;

            case R.id.btn_logout:
                cleanStorageData();

                ManagePreferences.cleanUserPreferences();
                ManagePreferences.cleanAutoCheckinPreferences();
                UsersBL user = new UsersBL();
                user.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
                    }
                });

                user.logOut(userPreference.getToken(),mUserId);

                ManagePreferences.cleanUserPreferences();
                //Close FB Session
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();
                getActivity().finish();
                startActivity(new Intent(getActivity(), StartActivity.class));
                break;

            case R.id.btn_terms_and_conditions:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("Terms and conditions")
                        .replace(R.id.content_frame, new TermsAndConditionsFragment()).commit();
                break;
        }
    }

    private void cleanStorageData() {
        File[] listFiles = getActivity().getFilesDir().listFiles();
        for (File file : listFiles) {
            boolean deletedFile = file.delete();
            if (deletedFile) {
                Log.i(TAG, "Cleaning internal storage...");
            }
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }
}
