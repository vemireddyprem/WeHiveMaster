/*******************************************************************************
 PROJECT:       Hive
 FILE:          ChangePasswordFragment.java
 DESCRIPTION:   Login view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Marcela Güiza    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Change error messages implementation.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.Utils;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class ChangePasswordFragment extends GATrackerFragment implements View.OnClickListener,
        IHiveResponse {

    private EditText mEdtCurrentPassword;
    private EditText mEdtNewPassword;
    private EditText mEdtRepeatPassword;
    private ProgressHive mProgressHive;
    private UsersBL mUserBL;
    private User userPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_password_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
        userPreference = ManagePreferences.getUserPreferences();
        initViewComponents();
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.change_password));

        mEdtCurrentPassword = (EditText) getView().findViewById(R.id.edt_password);
        mEdtNewPassword = (EditText) getView().findViewById(R.id.edt_new_password);
        mEdtRepeatPassword = (EditText) getView().findViewById(R.id.edt_repeat_new_password);

        Button btnSave = (Button) getView().findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_email_address:
                break;
            case R.id.btn_save:
                if (!validateData()) {
                    sendDataToService();
                }
                break;
        }
    }

    public boolean validateData() {
        //No current password validation
        if (mEdtCurrentPassword.getText().toString().trim().length() == 0) {
            mEdtCurrentPassword.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_username_required));
            return true;
        }
        //No new password validation
        if (mEdtNewPassword.getText().toString().trim().length() == 0) {
            mEdtNewPassword.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_username_required));
            return true;
        }
        //No repeat new password validation
        if (mEdtRepeatPassword.getText().toString().trim().length() == 0) {
            mEdtRepeatPassword.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_username_required));
            return true;
        }
        //New password and repeat new password equals validation
        if (!mEdtNewPassword.getText().toString().trim().equals(mEdtRepeatPassword.getText().toString().trim())) {
            mEdtRepeatPassword.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_username_required));
            return true;
        }
        return false;
    }

    private void sendDataToService() {
        mProgressHive.show(getActivity().getSupportFragmentManager(), null);
        User localUser = ManagePreferences.getUserPreferences();

        String password = "", newPassword = "";
        try {
            password = Utils.getSHAHash(mEdtCurrentPassword.getText().toString());
            newPassword = Utils.getSHAHash(mEdtNewPassword.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("SHA-KEY-PASS", e.getMessage() + "");
        } catch (UnsupportedEncodingException e) {
            Log.d("SHA-KEY-PASS", e.getMessage() + "");
        }
        mUserBL.changePassword(userPreference.getToken(), String.valueOf(localUser.getId_user()), password, newPassword);
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
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.success), response.getMessageError());
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), response.getMessageError());
        }
    }
}