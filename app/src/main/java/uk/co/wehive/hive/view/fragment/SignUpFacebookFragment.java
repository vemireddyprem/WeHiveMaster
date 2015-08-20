package uk.co.wehive.hive.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.ExistEmailUsernameResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;
import uk.co.wehive.hive.view.activity.HomeActivity;

public class SignUpFacebookFragment extends Fragment implements View.OnClickListener, IHiveResponse {

    private static final int SHOW_DIALOG_ERROR = 1;

    private FragmentTransaction mFt;
    private EditText mEdtPassword;
    private EditText mEdtPhoneNumber;
    private EditText mEdtUsername;
    private Button mBtnSignUpFB;
    private Bitmap mPhoto = null;
    private CustomFontTextView mTxtAcceptTermsAndConditions;
    private CheckBox mCheckTermsAndConditions;
    private static String mMessage;
    private static String mTitle;
    private ProgressHive mProgressHive;
    private boolean isShowing = false;
    private boolean isValidatingUsername = false;
    private boolean isUsernameValid = false;
    private User userPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_facebook_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFt = getFragmentManager().beginTransaction();
        isShowing = true;
        initViewComponents();
    }

    // Initialize view components
    private void initViewComponents() {

        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.sign_up_title));
        User localUser = ManagePreferences.getUserPreferences();

        ImageView mImgUserPhoto = (ImageView) getView().findViewById(R.id.img_user_photo);
        CustomFontTextView mTxtUsername = (CustomFontTextView) getView().findViewById(R.id.txt_username);
        CustomFontTextView mTxtUserCity = (CustomFontTextView) getView().findViewById(R.id.txt_user_city);

        mEdtUsername = (EditText) getView().findViewById(R.id.edt_username);
        mEdtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateUsername();
                }
            }
        });

        mEdtPassword = (EditText) getView().findViewById(R.id.edt_password);
        mEdtPhoneNumber = (EditText) getView().findViewById(R.id.edt_phone_number);
        mTxtAcceptTermsAndConditions = (CustomFontTextView) getView().findViewById(R.id.txt_text_and_conditions_intro);
        mCheckTermsAndConditions = (CheckBox) getView().findViewById(R.id.chk_terms_and_conditions);
        mBtnSignUpFB = (Button) getView().findViewById(R.id.btn_signup_facebook);
        mBtnSignUpFB.setOnClickListener(this);
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
        userPreference = ManagePreferences.getUserPreferences();
        String photoUrl = localUser.getPhoto();
        if (photoUrl.length() > 10) {
            Picasso.with(getActivity()).load(photoUrl).transform(new PictureTransformer()).error(R.drawable.ic_or_drawable).into(mImgUserPhoto);
        }
        mTxtUsername.setText(localUser.getFbUsername());
        mTxtUserCity.setText(localUser.getCity_code().toUpperCase());

        setCustomTextTermsAndConditions();
        addEvents();
    }

    private void validateUsername() {
        isValidatingUsername = true;
        if (mEdtUsername.getText().toString().length() > 0) {
            UsersBL mUserBL = new UsersBL();
            mUserBL.setHiveListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                    if (isShowing) {
                        mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
                        isValidatingUsername = false;
                        isUsernameValid = false;
                    }
                }

                @Override
                public void onResult(HiveResponse response) {
                    isValidatingUsername = false;
                    if (isShowing) {
                        if (response.getStatus()) {
                            if (!((ExistEmailUsernameResponse) response).getData().isExist()) {
                                mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        getResources().getDrawable(R.drawable.ic_check_edit_user), null);
                                isUsernameValid = true;
                            } else {
                                mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
                                isUsernameValid = false;
                            }
                        } else {
                            mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                    getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
                            isUsernameValid = false;
                        }
                    }
                }
            });
            mUserBL.existUsername(mEdtUsername.getText().toString());
        } else {
            isValidatingUsername = false;
            isUsernameValid = false;
            mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
        }
    }

    private boolean validateData() {

        while (isValidatingUsername) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isUsernameValid) {
            mMessage = getString(R.string.username_exist);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }

        if (!mCheckTermsAndConditions.isChecked()) {
            mMessage = getString(R.string.terms_and_conditions_not_accepted);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }

        return false;
    }

    private void sendData() {
        UsersBL mUserBL;
        mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
        User localUser = ManagePreferences.getUserPreferences();
        File photo;
        if (mPhoto != null) {
            try {
                photo = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                FileOutputStream fOut = new FileOutputStream(photo);

                mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext()
                .getSystemService(Context
                        .TELEPHONY_SERVICE);

        mUserBL.signUpFacebook(localUser.getEmail(), localUser.getId_facebook(),
                localUser.getFirst_name(), localUser.getLast_name(), localUser.getFbUsername(),
                localUser.getLink(), localUser.getPhoto(), localUser.getCity_code(),
                mEdtUsername.getText().toString(), mEdtPassword.getText().toString(),
                "", telephonyManager.getDeviceId(), mEdtPhoneNumber.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnSignUpFB)) {

            Thread threadValidate = new Thread() {
                public void run() {
                    if (mEdtUsername.isFocused()) {
                        validateUsername();
                    }
                    mProgressHive.show(getActivity().getSupportFragmentManager(), null);
                    if (!validateData()) {
                        sendData();
                    } else {
                        mProgressHive.dismiss();
                    }
                }
            };
            threadValidate.start();
        }
    }

    // Set custom text on Facebook and Twitter buttons
    private void setCustomTextTermsAndConditions() {

        // Terms & Conditions button
        SpannableString content = new SpannableString(getString(R.string.accept_terms_conditions));
        content.setSpan(new UnderlineSpan(), 30, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtAcceptTermsAndConditions.setText(content);
    }

    private Handler sHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_DIALOG_ERROR:
                    ErrorDialog.showErrorMessage(getActivity(), mTitle, mMessage);
                    break;

                default:
                    break;
            }
        }
    };

    // set listener for controls
    public void addEvents() {
        mTxtAcceptTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mFt.replace(R.id.container, new TermsAndConditionsFragment()).addToBackStack("").commit();
            }
        });
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
            User user = new User();
            user.setId_user(((LoginResponse) response).getData().getId_user());
            user.setToken(((LoginResponse) response).getData().getToken());
            ManagePreferences.setPreferencesUser(user);

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.putExtra("fragment", AppConstants.FOLLOW_PEOPLE);
            startActivity(intent);

            getActivity().finish();
            onDestroy();
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.please_try_again), response.getMessageError());
        }
    }
}
