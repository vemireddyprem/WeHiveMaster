/*******************************************************************************
 PROJECT:       Hive
 FILE:          SignUpFragment.java
 DESCRIPTION:   Sign up fragment
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        11/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.ExistEmailUsernameResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CropOption;
import uk.co.wehive.hive.utils.CropOptionAdapter;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.Utils;
import uk.co.wehive.hive.utils.facebook.FacebookConstants;
import uk.co.wehive.hive.view.activity.HomeActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;
import uk.co.wehive.hive.view.dialog.SelectPictureDialog;

public class SignUpFragment extends GATrackerFragment implements View.OnClickListener,
        IOptionSelected, IHiveResponse {

    public static final int FACEBOOK_REQUEST = 64206;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int SHOW_DIALOG_ERROR = 1;
    private static final int SHOW_DIALOG_EMAIL_INVALID = 2;
    private static final int SHOW_DIALOG_EMAIL_VALID = 3;
    private static final int SHOW_DIALOG_USERNAME_INVALID = 4;
    private static final int SHOW_DIALOG_USERNAME_VALID = 5;
    private static final int SHOW_DIALOG_PASSWORD_VALID = 5;
    private static final int RESULT_CANCELED = 0;
    private static final String TAG = "SignUpFragment";

    private static String mMessage, mTitle;
    private FragmentTransaction mFt;
    private EditText mEdtEmail, mEdtPassword, mEdtRePassword, mEdtFirstName, mEdtLastName, mEdtPhoneNumber, mEdtUsername;
    private ImageView mImgPhoto;
    private TextView mTxvPhoto;
    private LinearLayout mLytPhoto;
    private CustomFontTextView mTxtAcceptTermsAndConditions;
    private CheckBox mCheckTermsAndConditions;
    private Button mBtnRegister;
    private Uri mImageCaptureUri;
    private Bitmap mPhoto = null;
    private ProgressHive mProgressHive;
    private boolean isEmailValid = false, isValidatingEmail = false, isUsernameValid = false, isValidatingUsername = false, isShowing = false;
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

        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.btn_signup_facebook);
        authButton.setBackgroundResource(R.drawable.bt_facebook_background);
        authButton.setText(R.string.use_facebook);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList(FacebookConstants.USER_LIKES,
                FacebookConstants.USER_STATUS, FacebookConstants.EMAIL, FacebookConstants.PUBLIC_PROFILE));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFt = getFragmentManager().beginTransaction();
        isShowing = true;
        setHasOptionsMenu(true);
        initViewComponents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.signup_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_login:
                mFt.replace(R.id.container, new LoginFragment())
                        .addToBackStack("LoginFragment added")
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Initialize view components
    private void initViewComponents() {

        getActivity().getActionBar().show();
        getActivity().getActionBar().setCustomView(R.layout.action_bar_login);
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.sign_up_title));

        mEdtEmail = (EditText) getView().findViewById(R.id.edt_email);
        mEdtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail();
                }
            }
        });

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
        mEdtRePassword = (EditText) getView().findViewById(R.id.edt_repeat_password);
        mEdtFirstName = (EditText) getView().findViewById(R.id.edt_first_name);
        mEdtLastName = (EditText) getView().findViewById(R.id.edt_last_name);
        mEdtPhoneNumber = (EditText) getView().findViewById(R.id.edt_phone_number);

        mEdtEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtUsername.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEdtPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtRePassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtFirstName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtLastName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtPhoneNumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mBtnRegister = (Button) getView().findViewById(R.id.btn_save);
        mBtnRegister.setOnClickListener(this);

        mLytPhoto = (LinearLayout) getView().findViewById(R.id.lyt_photo);
        mLytPhoto.setOnClickListener(this);

        mImgPhoto = (ImageView) getView().findViewById(R.id.img_user_profile);
        mTxvPhoto = (TextView) getView().findViewById(R.id.lbl_user_photo);

        mTxtAcceptTermsAndConditions = (CustomFontTextView) getView().findViewById(R.id.txt_terms_and_conditions);

        mCheckTermsAndConditions = (CheckBox) getView().findViewById(R.id.checkBox);

        setCustomTextTermsAndConditions();
        addEvents();

        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    private void validateEmail() {

        isValidatingEmail = true;
        if (Utils.isValidEmail(mEdtEmail.getText().toString())) {
            UsersBL mUserBL = new UsersBL();
            mUserBL.setHiveListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                    sHandler.sendEmptyMessage(SHOW_DIALOG_EMAIL_INVALID);
                    isValidatingEmail = false;
                    isEmailValid = false;
                }

                @Override
                public void onResult(HiveResponse response) {
                    isValidatingEmail = false;

                    if (response.getStatus()) {
                        if (!((ExistEmailUsernameResponse) response).getData().isExist()) {
                            sHandler.sendEmptyMessage(SHOW_DIALOG_EMAIL_VALID);
                            isEmailValid = true;
                        } else {
                            sHandler.sendEmptyMessage(SHOW_DIALOG_EMAIL_INVALID);
                            isEmailValid = false;
                        }
                    } else {
                        sHandler.sendEmptyMessage(SHOW_DIALOG_EMAIL_INVALID);
                        isEmailValid = false;
                    }
                }
            });
            mUserBL.existEmail(mEdtEmail.getText().toString());
        } else {
            isValidatingEmail = false;
            isEmailValid = false;
            sHandler.sendEmptyMessage(SHOW_DIALOG_EMAIL_INVALID);
        }
    }

    private void validateUsername() {
        isValidatingUsername = true;
        if (mEdtUsername.getText().toString().length() > 0) {
            UsersBL mUserBL = new UsersBL();
            mUserBL.setHiveListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                    if (isShowing) {
                        sHandler.sendEmptyMessage(SHOW_DIALOG_USERNAME_INVALID);
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
                                sHandler.sendEmptyMessage(SHOW_DIALOG_USERNAME_VALID);
                                isUsernameValid = true;
                            } else {
                                sHandler.sendEmptyMessage(SHOW_DIALOG_USERNAME_INVALID);
                                isUsernameValid = false;
                            }
                        } else {
                            sHandler.sendEmptyMessage(SHOW_DIALOG_USERNAME_INVALID);
                            isUsernameValid = false;
                        }
                    }
                }
            });
            mUserBL.existUsername(mEdtUsername.getText().toString());
        } else {
            isValidatingUsername = false;
            isUsernameValid = false;
            sHandler.sendEmptyMessage(SHOW_DIALOG_USERNAME_INVALID);
        }
    }

    private boolean validateData() {
        if (mEdtEmail.getText().toString().trim().length() == 0 || mEdtPassword.getText().toString().trim().length() == 0 || mEdtFirstName.getText().toString().trim().length() == 0
                || mEdtLastName.getText().toString().trim().length() == 0 || mEdtUsername.getText().toString().trim().length() == 0) {
            mMessage = getString(R.string.all_fields_are_required);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return false;
        }

        if (!mEdtPassword.getText().toString().equals(mEdtRePassword.getText().toString())) {
            mMessage = getString(R.string.password_must_be_same);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return false;
        }

        if (mEdtPassword.getText().toString().trim().length() < SHOW_DIALOG_PASSWORD_VALID) {
            mMessage = getString(R.string.password_must_be_over_five);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return false;
        }

        if (!isEmailValid) {
            mMessage = getString(R.string.email_exist);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return false;
        }

        if (!mCheckTermsAndConditions.isChecked()) {
            mMessage = getString(R.string.terms_and_conditions_not_accepted);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnRegister)) {
            if (validateData()) {
                sendData();
                mProgressHive = new ProgressHive();
                mProgressHive.show(getActivity().getSupportFragmentManager(), null);
            }

        } else if (v.equals(mLytPhoto)) {
            SelectPictureDialog mSelectPictureDialog;
            mSelectPictureDialog = new SelectPictureDialog();
            mSelectPictureDialog.setTypeSelectedListener(this);
            mSelectPictureDialog.show(getActivity().getSupportFragmentManager(), null);
        }
    }

    private void sendData() {
        UsersBL mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
        File photo = null;
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
                .getSystemService(Context.TELEPHONY_SERVICE);

        mUserBL.singUp(mEdtEmail.getText().toString(), mEdtPassword.getText().toString(), mEdtFirstName.getText().toString(),
                mEdtLastName.getText().toString(), mEdtUsername.getText().toString(),
                AppConstants.CODE_CITY, mEdtPhoneNumber.getText().toString(),
                telephonyManager.getDeviceId(), photo);
    }

    @Override
    public void onDestroy() {
        isShowing = false;
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onTypeSelected(int index) {
        // Camera
        if (index == 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

            try {
                intent.putExtra("return-data", true);

                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (index == 1) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_CANCELED == resultCode && FACEBOOK_REQUEST == requestCode) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {

            case PICK_FROM_CAMERA:
                doCrop();
                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();
                doCrop();
                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mPhoto = extras.getParcelable("data");
                    mImgPhoto.setVisibility(View.VISIBLE);
                    mTxvPhoto.setVisibility(View.GONE);
                    mImgPhoto.setImageBitmap(mPhoto);
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

                break;

            case FACEBOOK_REQUEST:
                Session session = Session.getActiveSession();
                if (session != null && (session.isOpened() || session.isClosed())) {
                    onSessionStateChange(session, session.getState(), null);
                }
                uiHelper.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getActivity().getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
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
            String messageError = response.getMessageError();
            if (messageError.length() > 1) {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.please_try_again), messageError);
            } else {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.username_already_exists_please_try_again), "");
            }
        }
    }

    private Handler sHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case SHOW_DIALOG_ERROR:
                    ErrorDialog.showErrorMessage(getActivity(), mTitle, mMessage);
                    break;

                case SHOW_DIALOG_EMAIL_INVALID:
                    if (isAdded()) {
                        mEdtEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
                    }
                    break;

                case SHOW_DIALOG_EMAIL_VALID:
                    if (isAdded()) {
                        mEdtEmail.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_check_edit_user), null);
                    }
                    break;

                case SHOW_DIALOG_USERNAME_INVALID:
                    if (isAdded()) {
                        mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_fail_edit_user), null);
                    }
                    break;

                case SHOW_DIALOG_USERNAME_VALID:
                    if (isAdded()) {
                        mEdtUsername.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_check_edit_user), null);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
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
                        ManagePreferences.setAutocheckin(false);
                        if (user.getProperty("email") != null) {
                            localUser.setEmail(user.getProperty("email").toString());
                        }
                        localUser.setLink(user.getLink());

                        localUser.setCity_code(AppConstants.CODE_CITY);
                        localUser.setPhoto("https://graph.facebook.com/" + user.getId() + "/picture?type=large");

                        ManagePreferences.setPreferencesUser(localUser);
                        mFt.replace(R.id.container, new SignUpFacebookFragment()).addToBackStack("StartActivity added").commit();
                        onDestroy();
                    }
                }
            }).executeAsync();

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    // Set custom text on Facebook and Twitter buttons
    private void setCustomTextTermsAndConditions() {

        // Terms & Conditions button
        SpannableString content = new SpannableString(getString(R.string.accept_terms_conditions));
        content.setSpan(new UnderlineSpan(), 30, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtAcceptTermsAndConditions.setText(content);
    }

    // set listener for controls
    public void addEvents() {
        mTxtAcceptTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mFt.replace(R.id.container, new TermsAndConditionsFragment()).addToBackStack("Terms and conditions").commit();
            }
        });
    }
}
