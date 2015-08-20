/*******************************************************************************
 PROJECT:       Hive
 FILE:          LoginFragment.java
 DESCRIPTION:   Login view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Marcela Güiza    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Change error messages implementation.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.Utils;
import uk.co.wehive.hive.utils.facebook.FacebookConstants;
import uk.co.wehive.hive.view.activity.HomeActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class LoginFragment extends GATrackerFragment implements View.OnClickListener, IHiveResponse {

    private static final String TAG = "LoginFragment";

    private FragmentTransaction mFt;
    private EditText mEdtEmailAddress;
    private EditText mEdtPassword;
    private UsersBL mUserBL;
    private ProgressHive mProgressHive;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.login_fragment, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.btn_login_facebook);
        authButton.setBackgroundResource(R.drawable.bt_facebook_background);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf");
        authButton.setTypeface(font, Typeface.NORMAL);
        authButton.setText(R.string.use_facebook);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList(FacebookConstants.USER_LIKES,
                FacebookConstants.USER_STATUS, FacebookConstants.EMAIL,
                FacebookConstants.PUBLIC_PROFILE));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFt = getFragmentManager().beginTransaction();

        initViewComponents();

        mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
    }

    //Inicializate view components
    private void initViewComponents() {

        getActivity().getActionBar().show();
        getActivity().getActionBar().setCustomView(R.layout.action_bar_login);
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.login));

        mEdtEmailAddress = (EditText) getView().findViewById(R.id.edt_email_address);
        mEdtPassword = (EditText) getView().findViewById(R.id.edt_password);

        mEdtEmailAddress.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
        getView().findViewById(R.id.btn_login_facebook).setOnClickListener(this);
        getView().findViewById(R.id.edt_email_address).setOnClickListener(this);
        getView().findViewById(R.id.edt_password).setOnClickListener(this);
        getView().findViewById(R.id.txt_forgot_password).setOnClickListener(this);
        getView().findViewById(R.id.btn_login).setOnClickListener(this);

        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    public boolean validateCredentials() {
        if (mEdtEmailAddress.getText().toString().trim().length() == 0) {
            mEdtEmailAddress.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_required));
            return true;
        }
        if (mEdtPassword.getText().toString().trim().length() == 0) {
            mEdtPassword.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.password_required));
            return true;
        }
        return false;
    }

    private void sendDataToService() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String password = "";
        try {
            password = Utils.getSHAHash(mEdtPassword.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("SHA-KEY-PASS", e.getMessage() + "");
        } catch (UnsupportedEncodingException e) {
            Log.d("SHA-KEY-PASS", e.getMessage() + "");
        }
        mProgressHive.show(getActivity().getSupportFragmentManager(), null);
        mUserBL.login(mEdtEmailAddress.getText().toString(), password,
                ManagePreferences.getIdGCM(), telephonyManager.getDeviceId(), AppConstants.OS);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_login_facebook:
                break;

            case R.id.txt_forgot_password:
                mFt.replace(R.id.container, new ForgotPasswordFragment())
                        .addToBackStack(TAG).commit();
                break;

            case R.id.btn_login:
                if (!validateCredentials()) {
                    sendDataToService();
                }
                break;
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        if (error == null) {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.internet_connection));
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.connection_lost));
        }
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        if (response.getStatus()) {
            User user = ((LoginResponse) response).getData();
            user.setToken(((LoginResponse) response).getData().getToken());
            ManagePreferences.setPreferencesUser(user);
            ManagePreferences.setAutocheckin(false);
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.putExtra("fragment", AppConstants.USER_PROFILE);
            startActivity(intent);

            getActivity().finish();
            onDestroy();
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.please_try_again), response.getMessageError());
            Session session = Session.getActiveSession();
            if (session != null && (session.isOpened() || session.isClosed())) {
                session.closeAndClearTokenInformation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        User localUser = ManagePreferences.getUserPreferences();
                        localUser.setId_facebook(user.getId());
                        localUser.setFirst_name(user.getFirstName());
                        localUser.setLast_name(user.getLastName());
                        if (user.getUsername() != null && user.getUsername().length() > 0) {
                            localUser.setFbUsername(user.getUsername());
                        } else {
                            localUser.setFbUsername(user.getFirstName() + user.getLastName());
                        }
                        if (user.getProperty("email") != null) {
                            localUser.setEmail(user.getProperty("email").toString());
                        }
                        localUser.setLink(user.getLink());

                        localUser.setCity_code(AppConstants.CODE_CITY);
                        localUser.setPhoto("https://graph.facebook.com/" + user.getId() + "/picture?type=large");

                        ManagePreferences.setPreferencesUser(localUser);

                        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext()
                                .getSystemService(Context.TELEPHONY_SERVICE);

                        mProgressHive.show(getActivity().getSupportFragmentManager(), null);
                        mUserBL.loginFacebook(localUser.getId_facebook(), localUser.getEmail(),
                                ManagePreferences.getIdGCM(), telephonyManager.getDeviceId(),
                                AppConstants.OS);
                    }
                }
            }).executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }
}
