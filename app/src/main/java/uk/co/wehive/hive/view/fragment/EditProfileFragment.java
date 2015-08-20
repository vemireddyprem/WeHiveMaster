/*******************************************************************************
 PROJECT:       Hive
 FILE:          EdirProfileFragment.java
 DESCRIPTION:   User profile fragment
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        18/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import retrofit.RetrofitError;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.CitiesBL;
import uk.co.wehive.hive.bl.CountriesBL;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.Cities;
import uk.co.wehive.hive.entities.Countries;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.CitiesResponse;
import uk.co.wehive.hive.entities.response.CountriesResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CropOption;
import uk.co.wehive.hive.utils.CropOptionAdapter;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.facebook.FacebookConstants;
import uk.co.wehive.hive.view.activity.WebActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;
import uk.co.wehive.hive.view.dialog.SelectPictureDialog;

public class EditProfileFragment extends GATrackerFragment implements View.OnClickListener,
        IOptionSelected, IHiveResponse {

    public static final int TWITTER_REQUEST = 0, PICK_FROM_CAMERA = 1, CROP_FROM_CAMERA = 2, PICK_FROM_FILE = 3, SHOW_DIALOG_ERROR = 1;
    private static final String TAG = "LoginFragment";

    private User mUserProfile;
    private EditText mEdtEmail, mEdtUsername, mEdtFirstName;
    private ImageView mImgPhoto;
    private TextView mTxvPhoto;
    private LinearLayout mLytPhoto;
    private EditText edtLastName, edtPhoneNumber, edtBio;
    private Spinner spinCity, spinCountry;
    private Button mBtnSave, mBtnTwitter, mBtnConnectedWithFacebook;
    private LoginButton mAuthButton;
    private ProgressHive mProgressHive;
    private Uri mImageCaptureUri;
    private Bitmap mPhoto = null;
    private static String mMessage, mTitle;
    private UsersBL mUserBL;
    private List<Cities> list;
    private ArrayAdapter<Cities> dataAdapter;
    private List<Countries> listCountries;
    private ArrayAdapter<Countries> countriesDataAdapter;
    private CitiesBL mCitiesBL;
    private CountriesBL mCountriesBL;
    private UsersBL mUserBLDT;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
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

        mSharedPreferences = getActivity().getSharedPreferences("weHive", 0);

        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        mAuthButton = (LoginButton) view.findViewById(R.id.btn_connect_facebook);
        mAuthButton.setBackgroundResource(R.drawable.bt_facebook_background);
        mAuthButton.setText(R.string.connect_with_facebook);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Light.ttf");
        mAuthButton.setTypeface(font, Typeface.NORMAL);
        mAuthButton.setFragment(this);
        mAuthButton.setReadPermissions(Arrays.asList(FacebookConstants.USER_LIKES,
                FacebookConstants.USER_STATUS, FacebookConstants.EMAIL, FacebookConstants.PUBLIC_PROFILE));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        initViewComponents();

        mCountriesBL.getCountries();

        if (bundle.containsKey(AppConstants.USER_PROFILE)) {
            mUserProfile = bundle.getParcelable(AppConstants.USER_PROFILE);
            mEdtEmail.setText(mUserProfile.getEmail());
            mEdtUsername.setText(mUserProfile.getUsername());
            mEdtFirstName.setText(mUserProfile.getFirst_name());
            edtLastName.setText(mUserProfile.getLast_name());
            edtBio.setText(mUserProfile.getBio());
            edtPhoneNumber.setText(mUserProfile.getPhone());
            if (mUserProfile.getPhoto().length() > 0) {
                mImgPhoto.setVisibility(View.VISIBLE);
                mTxvPhoto.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(mUserProfile.getPhoto()).into(mImgPhoto);
            }

            validateTwitter();
            validateFacebook();
        }
    }

    private void validateFacebook() {
        if (mUserProfile.getId_facebook().length() > 0) {
            mAuthButton.setText(getString(R.string.use_facebook));
            mAuthButton.setVisibility(View.GONE);
            mBtnConnectedWithFacebook.setVisibility(View.VISIBLE);
        } else {
            mAuthButton.setText(getString(R.string.connect_with_facebook));
        }
    }

    private void validateTwitter() {
        if (mUserProfile.getId_twitter().length() > 0) {
            mBtnTwitter.setText(getString(R.string.connected_to_twitter));
            mBtnTwitter.setBackgroundResource(R.drawable.btn_twitter_bird_left);
        } else {
            mBtnTwitter.setText("");
            mBtnTwitter.setBackgroundResource(R.drawable.btn_twitter_bird_center);
        }
    }

    // Initialize view components
    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.edit_profile_title));
        mEdtEmail = (EditText) getView().findViewById(R.id.edt_email);

        mEdtUsername = (EditText) getView().findViewById(R.id.edt_username);
        mEdtFirstName = (EditText) getView().findViewById(R.id.edt_first_name);
        mImgPhoto = (ImageView) getView().findViewById(R.id.img_user_profile);
        mTxvPhoto = (TextView) getView().findViewById(R.id.lbl_user_photo);
        mLytPhoto = (LinearLayout) getView().findViewById(R.id.lyt_photo);
        mLytPhoto.setOnClickListener(this);

        edtLastName = (EditText) getView().findViewById(R.id.edt_last_name);
        edtPhoneNumber = (EditText) getView().findViewById(R.id.edt_phone_number);
        edtBio = (EditText) getView().findViewById(R.id.edt_bio);

        mEdtEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtUsername.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mEdtFirstName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtLastName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtPhoneNumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtBio.setImeOptions(EditorInfo.IME_ACTION_DONE);

        list = new ArrayList<Cities>();
        listCountries = new ArrayList<Countries>();

        // BEGIN - Spinners
        spinCountry = (Spinner) getView().findViewById(R.id.edt_country);

        mCountriesBL = new CountriesBL();
        mCountriesBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(HiveResponse response) {
                try {
                    listCountries.addAll(((CountriesResponse) response).getData());
                    countriesDataAdapter = new ArrayAdapter<Countries>(getActivity(), R.layout.spinner_item_city, listCountries);
                    spinCountry.setAdapter(countriesDataAdapter);
                    countriesDataAdapter.notifyDataSetChanged();
                    spinCity.post(new Runnable() {
                        @Override
                        public void run() {
                            Iterator<Countries> it = listCountries.iterator();
                            int counter = 0;
                            while (it.hasNext()) {
                                Countries country = it.next();
                                if (country.getCode().toLowerCase().equals(mUserProfile.getCountry().toLowerCase())) {
                                    spinCountry.setSelection(counter);
                                    break;
                                }
                                counter++;
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });

        spinCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Iterator<Countries> itc = listCountries.iterator();
                String countryCode = "";
                while (itc.hasNext()) {
                    Countries country = itc.next();
                    if (country.getName().toLowerCase().equals(spinCountry.getSelectedItem().toString().toLowerCase())) {
                        countryCode = country.getCode();
                        break;
                    }
                }
                mCitiesBL.getCities(countryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinCity = (Spinner) getView().findViewById(R.id.edt_city);

        mCitiesBL = new CitiesBL();
        mCitiesBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(HiveResponse response) {
                try {
                    list.addAll(((CitiesResponse) response).getData());
                    dataAdapter = new ArrayAdapter<Cities>(getActivity(), R.layout.spinner_item_city, list);
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item_city);
                    spinCity.setAdapter(dataAdapter);
                    dataAdapter.notifyDataSetChanged();
                    spinCity.post(new Runnable() {
                        @Override
                        public void run() {
                            Iterator<Cities> it = list.iterator();
                            int counter = 0;
                            while (it.hasNext()) {
                                Cities city = it.next();
                                if (city.getName().toLowerCase().equals(mUserProfile.getCity().toLowerCase())) {
                                    spinCity.setSelection(counter);
                                    break;
                                }
                                counter++;
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });
        // END - Spinners

        mBtnTwitter = (Button) getView().findViewById(R.id.btn_connect_twitter);
        mBtnTwitter.setOnClickListener(this);
        mBtnConnectedWithFacebook = (Button) getView().findViewById(R.id.btn_connected_facebook);
        mBtnSave = (Button) getView().findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    private boolean validateData() {
        if (mEdtEmail.getText().toString().trim().length() == 0) {
            mMessage = getString(R.string.password_required_edit);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }

        if (mEdtFirstName.getText().toString().trim().length() == 0) {
            mMessage = getString(R.string.firstname_required_edit);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }

        if (edtLastName.getText().toString().trim().length() == 0) {
            mMessage = getString(R.string.lastname_required_edit);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }

        if (mEdtUsername.getText().toString().trim().length() == 0) {
            mMessage = getString(R.string.all_fields_are_required);
            mTitle = getString(R.string.something_wrong);
            sHandler.sendEmptyMessage(SHOW_DIALOG_ERROR);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnSave)) {

            Thread threadValidate = new Thread() {
                public void run() {

                    mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                    if (!validateData()) {
                        sendData();
                    } else {
                        mProgressHive.dismiss();
                    }
                }
            };
            threadValidate.start();
        } else if (v.equals(mLytPhoto)) {
            SelectPictureDialog mSelectPictureDialog;
            mSelectPictureDialog = new SelectPictureDialog();
            mSelectPictureDialog.setTypeSelectedListener(this);
            mSelectPictureDialog.show(getActivity().getSupportFragmentManager(), null);
        } else if (v.equals(mBtnTwitter)) {
            if (mUserProfile.getId_twitter().length() == 0) {
                loginToTwitter();
            } else {
                disconnectTwitter();
            }
        }
    }

    private void loginToTwitter() {
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.commit();

        try {
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);

            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        requestToken = twitter.getOAuthRequestToken(AppConstants.TWITTER_CALLBACK_URL);
                        Intent i = new Intent(getActivity().getApplicationContext(), WebActivity.class);
                        i.putExtra("URL", requestToken.getAuthenticationURL().replace("http:", "https:"));
                        startActivityForResult(i, TWITTER_REQUEST);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendData() {
        UsersBL mUserBL;
        mUserBL = new UsersBL();
        mUserBL.setHiveListener(this);
        File photo = null;
        if (mPhoto != null) {
            try {
                photo = new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" +
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                FileOutputStream fOut = new FileOutputStream(photo);

                mPhoto.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Iterator<Cities> it = list.iterator();
        String cityCode = "";
        while (it.hasNext()) {
            Cities city = it.next();
            if (city.getName().toLowerCase().equals(spinCity.getSelectedItem().toString().toLowerCase())) {

                cityCode = city.getCode();
                break;
            }
        }

        Iterator<Countries> itc = listCountries.iterator();
        String countryCode = "";
        while (itc.hasNext()) {
            Countries country = itc.next();
            if (country.getCode().toLowerCase().equals(spinCountry.getSelectedItem().toString().toLowerCase())) {
                countryCode = country.getCode();
                break;
            }
        }
        mUserBL.saveProfile(mUserProfile.getToken(), mUserProfile.getId_user() + "",
                mEdtFirstName.getText().toString(), edtLastName.getText().toString(), mEdtUsername.getText().toString(),
                cityCode, edtBio.getText().toString(), countryCode, photo, edtPhoneNumber.getText().toString());
    }

    @Override
    public void onDestroy() {
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

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {

            case PICK_FROM_CAMERA:
                doCrop();
                break;

            case PICK_FROM_FILE:
                try {
                    mImageCaptureUri = data.getData();

                    if (Build.VERSION.SDK_INT < 19) {
                        doCrop();
                    } else {
                        mPhoto = getPhotoSquare(mImageCaptureUri);
                        mImgPhoto.setVisibility(View.VISIBLE);
                        mTxvPhoto.setVisibility(View.GONE);
                        mImgPhoto.setImageBitmap(mPhoto);

                        File f = new File(mImageCaptureUri.getPath());
                        if (f.exists()) f.delete();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
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
            case TWITTER_REQUEST:
                handleTwitterResult(data);
                break;
        }
    }

    private boolean handleTwitterResult(Intent data) {
        String verifier = (String) data.getExtras().get(AppConstants.URL_TWITTER_OAUTH_VERIFIER);
        User userPreferences = ManagePreferences.getUserPreferences();

        try {
            mProgressHive.show(getActivity().getSupportFragmentManager(), "");
            mProgressHive.setCancelable(false);
            // get access token
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            userPreferences.setToken_twitter(accessToken.getToken());
            userPreferences.setToken_twittersecret(accessToken.getToken());

            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(AppConstants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(AppConstants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.commit();
            long userID = accessToken.getUserId();
            userPreferences.setId_twitter(String.valueOf(userID));
            mUserProfile.setId_twitter(String.valueOf(userID));
            ManagePreferences.setPreferencesUser(userPreferences);


            UsersBL userBl = new UsersBL();
            userBl.setHiveListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                    mProgressHive.dismiss();
                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.problem_open_twitter));
                }

                @Override
                public void onResult(HiveResponse response) {
                    mProgressHive.dismiss();
                    if (response.getStatus()) {
                        mBtnTwitter.setText(getString(R.string.connected_to_twitter));
                        mBtnTwitter.setBackgroundResource(R.drawable.btn_twitter_bird_left);
                    } else {
                        logoutTwitter();
                        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.please_try_again), getString(R.string.error_twitter_login));
                    }
                }
            });
            userBl.saveTwitter(mUserProfile.getToken(), String.valueOf(userPreferences.getId_user()), String.valueOf(userID), accessToken.getScreenName());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();

        //If login failed with facebook, disconnect from fb
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        if (response.getStatus()) {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.changes_have_been_saved_successfully), null);
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
            //If login failed with facebook, disconnect from fb
            Session session = Session.getActiveSession();
            if (session != null && (session.isOpened() || session.isClosed())) {
                session.closeAndClearTokenInformation();
            }
        }
    }

    private Handler sHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_DIALOG_ERROR:
                    ErrorDialog.showErrorMessage(getActivity(), mTitle, mMessage);
                    //If login failed with facebook, disconnect from fb
                    Session session = Session.getActiveSession();
                    if (session != null && (session.isOpened() || session.isClosed())) {
                        session.closeAndClearTokenInformation();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            // Request user data and show the results
            if (mUserBL == null) {
                mUserBL = new UsersBL();
                mUserBL.setHiveListener(this);
            }
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
                        mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                        mUserBL.saveFacebook(mUserProfile.getToken(), String.valueOf(localUser.getId_user()),
                                localUser.getId_facebook(), localUser.getFbUsername(), localUser.getLink());
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

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

    private void logoutTwitter() {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this.getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        mUserProfile.setId_twitter("");
    }

    private void disconnectTwitter() {

        mUserBLDT = new UsersBL();
        mUserBLDT.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.problem_open_twitter));
            }

            @Override
            public void onResult(HiveResponse response) {
                mProgressHive.dismiss();
                if (response.getStatus()) {
                    mBtnTwitter.setText("");
                    mBtnTwitter.setBackgroundResource(R.drawable.btn_twitter_bird_center);
                    mUserProfile.setId_twitter("");
                    twitter = null;
                    logoutTwitter();
                } else {
                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.please_try_again), getString(R.string.error_disconnect_twitter));
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle("Confirm");
        builder.setMessage(getString(R.string.sure_disconnect_twitter));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mUserBLDT.disconnectTwitter(mUserProfile.getToken(), String.valueOf(ManagePreferences.getUserPreferences().getId_user()));
                logoutTwitter();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private Bitmap getPhotoSquare(Uri selectedImage) {

        InputStream imageStream = null;
        try {
            imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap srcBmp = BitmapFactory.decodeStream(imageStream);
        Bitmap dstBmp = null;

        if (srcBmp != null) {
            if (srcBmp.getWidth() >= srcBmp.getHeight()) {
                dstBmp = Bitmap.createBitmap(
                        srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                        0, srcBmp.getHeight(), srcBmp.getHeight()
                );
            } else {
                dstBmp = Bitmap.createBitmap(
                        srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                        srcBmp.getWidth(), srcBmp.getWidth()
                );
            }
        }
        return dstBmp;
    }
}