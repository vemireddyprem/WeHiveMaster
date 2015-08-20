/*******************************************************************************
 PROJECT:       Hive
 FILE:          ForgotPasswordFragment.java
 DESCRIPTION:   Login view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Marcela Güiza    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Change error messages implementation.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class ForgotPasswordFragment extends GATrackerFragment implements View.OnClickListener,
        IHiveResponse {

    private EditText mEdtUsername;
    private ProgressHive mProgressHive;
    private UsersBL mUserBL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forgot_password_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewComponents();

        mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.forgot_password));

        mEdtUsername = (EditText) getView().findViewById(R.id.edt_email_username);

        Button btnSend = (Button) getView().findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_email_address:
                break;
            case R.id.btn_send:
                if (!validateData()) {
                    sendDataToService();
                }
                break;
        }
    }

    public boolean validateData() {
        if (mEdtUsername.getText().toString().trim().length() == 0) {
            mEdtUsername.requestFocus();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.email_username_required));
            return true;
        }
        return false;
    }

    private void sendDataToService() {
        mProgressHive.show(getActivity().getSupportFragmentManager(), null);
        mUserBL.forgotPassword(mEdtUsername.getText().toString().toLowerCase());
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