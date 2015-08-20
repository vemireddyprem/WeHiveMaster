package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.facebook.FacebookConstants;
import uk.co.wehive.hive.utils.facebook.FacebookUtils;
import uk.co.wehive.hive.utils.twitter.TwitterHelper;
import uk.co.wehive.hive.view.activity.CarouselMenuActivity;
import uk.co.wehive.hive.view.activity.WebActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class ShareGoodTimesDetailFragment extends PostGoodTimesDetail implements IHiveResponse {

    @InjectView(R.id.btnTwitter)
    ToggleButton mBtnTwitter;
    @InjectView(R.id.btnFacebook)
    ToggleButton mBtnFacebook;
    @InjectView(R.id.txtComment)
    EditText mTxtComment;

    private static final List<String> PERMISSIONS = Arrays.asList(FacebookConstants.PUBLISH_ACTIONS);
    public static final int TWITTER_REQUEST = 0;

    private ProgressHive mProgressHive;
    private FragmentManager mFragmentManager;
    private UiLifecycleHelper mUiHelper;
    private PostBL mPostBL;
    private TwitterHelper mTwitterHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUiHelper = new UiLifecycleHelper(getActivity(), null);
        mUiHelper.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.share_good_times_detail_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragmentManager = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);

        CustomViewHelper.setUpActionBar(getActivity(), "Share post");
        initControls();

        mTwitterHelper = new TwitterHelper();
        mTwitterHelper.setAccessToken(mUserPrefs.getToken_twitter());
        mTwitterHelper.setAccessTokenSecret(mUserPrefs.getToken_twittersecret());

        mPostBL = new PostBL();
        mPostBL.setListener(this);
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.share_good_times, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPost:

                createPost();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPostWithFacebook() {
        Session session = Session.getActiveSession();

        if (session == null) {
            return;
        }

        List<String> permissions = session.getPermissions();
        if (!FacebookUtils.isSubsetOf(PERMISSIONS, permissions)) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
            return;
        }

        Bundle postParams = new Bundle();

        mPost.setText((mPost.getText() == null) ? "" : mPost.getPost_text());
        postParams.putString(FacebookConstants.MESSAGE, " " + mTxtComment.getText().toString().trim() + "-" + mPost.getPost_text());
        if (AppConstants.IMAGE.equals(mPost.getMedia_type())) {

            mImgPost.setDrawingCacheEnabled(true);
            mImgPost.buildDrawingCache();
            Bitmap bm = mImgPost.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            postParams.putByteArray(FacebookConstants.PICTURE, byteArray);

        } else if (AppConstants.VIDEO.equals(mPost.getMedia_type())) {

            postParams.putString(FacebookConstants.DESCRIPTION, mPost.getPost_text());
            postParams.putString(FacebookConstants.NAME, mPost.getPost_text());
            postParams.putString(FacebookConstants.LINK, mPost.getShare_link());
            postParams.putString(FacebookConstants.PICTURE, mPost.getMedia_absolute_thumbvideo_file());
        }

        Request.Callback callback = new Request.Callback() {
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    mProgressHive.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    mPostBL.addShare(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), mPost.getId_post());
                }
            }
        };

        Request request;
        if (mPost.getMedia_type() != null && mPost.getMedia_type().equals(AppConstants.IMAGE)) {
            request = new Request(session, FacebookConstants.ME_PHOTOS, postParams, HttpMethod.POST, callback);
        } else {
            request = new Request(session, FacebookConstants.ME_FEED, postParams, HttpMethod.POST, callback);
        }

        RequestAsyncTask task = new RequestAsyncTask(request);
        task.execute();
    }

    private void createPost() {
        if (mBtnFacebook.isChecked() || mBtnTwitter.isChecked()) {
            mProgressHive.show(mFragmentManager, "");
            if (mBtnFacebook.isChecked()) {
                Session session = Session.getActiveSession();
                if (session != null && session.isOpened()) {
                    createPostWithFacebook();
                } else {
                    Session.openActiveSession(getActivity(), this, true, callback);
                }
            }

            if (mBtnTwitter.isChecked()) {
                if (mTwitterHelper.verifyLoginData()) {
                    postTwitter();
                } else {
                    loginTwitter();
                }
            }
        } else {
            ErrorDialog.showErrorMessage(getActivity(), "", getString(R.string.select_social_network));
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(state);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            }
        });

        if (requestCode == TWITTER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                final String oauthVerifier = (String) data.getExtras().get(AppConstants.URL_TWITTER_OAUTH_VERIFIER);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mTwitterHelper.storeOAuthVerifier(oauthVerifier);
                        postTwitter();
                    }
                });
                thread.start();
            } else {
                if (mProgressHive.isVisible()) {
                    mProgressHive.dismiss();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        mFragmentManager.popBackStackImmediate();
    }

    private void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            try {
                createPostWithFacebook();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mProgressHive.dismiss();
        }
    }

    private void postTwitter() {
        try {
            String postText = mTxtComment.getText().toString();
            boolean sendStatus;

            if (AppConstants.IMAGE.equals(mPost.getMedia_type())) {
                mImgPost.setDrawingCacheEnabled(true);
                mImgPost.buildDrawingCache();
                Bitmap bm = mImgPost.getDrawingCache();
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "weHive");

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("weHive", "failed to create directory");
                        return;
                    }
                }

                // Create a media file name
                File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "TEMP.jpg");

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(mediaFile);
                    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

                sendStatus = mTwitterHelper.sendTweet(postText, mediaFile);
            } else if (AppConstants.VIDEO.equals(mPost.getMedia_type())) {
                sendStatus = mTwitterHelper.sendTweet(postText + " " + mPost.getMedia_absolute_file(), null);
            } else {
                sendStatus = mTwitterHelper.sendTweet(postText, null);
            }

            if (!sendStatus) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            } else {
                mPostBL.addShare(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), mPost.getId_post());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginTwitter() {
        Intent i = new Intent(getActivity().getApplicationContext(), WebActivity.class);
        i.putExtra("URL", mTwitterHelper.getAuthenticationURL().replace("http:", "https:"));
        startActivityForResult(i, TWITTER_REQUEST);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            CarouselMenuActivity.mCarouselMenuActivity.finish();
        } catch (Exception ignored) {
        }
    }
}
