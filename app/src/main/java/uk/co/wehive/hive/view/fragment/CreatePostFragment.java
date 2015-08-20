package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.ffmpeg.android.Clip;
import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils;
import org.ffmpeg.android.filters.CropVideoFilter;
import org.ffmpeg.android.filters.TransposeVideoFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.CreatePost;
import uk.co.wehive.hive.entities.EventDetail;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.Video;
import uk.co.wehive.hive.entities.response.CreatePostResponse;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.VideoUploadResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontEditText;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.Utils;
import uk.co.wehive.hive.utils.camera.ScreenshotHelper;
import uk.co.wehive.hive.utils.facebook.FacebookConstants;
import uk.co.wehive.hive.utils.facebook.FacebookUtils;
import uk.co.wehive.hive.utils.twitter.TwitterHelper;
import uk.co.wehive.hive.view.activity.WebActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class CreatePostFragment extends Fragment implements IHiveResponse {

    @InjectView(R.id.txt_post_text)
    CustomFontEditText mEdtPost;
    @InjectView(R.id.img_post_photo)
    ImageView mImgPost;
    @InjectView(R.id.txt_event_name)
    CustomFontTextView mTxtEventName;
    @InjectView(R.id.txt_event_place)
    CustomFontTextView mTxtEventPlace;
    private final String TAG = "CreatePostFragment";

    private static final String POST_WITHOUT_VIDEO = "0";
    private static final int TWITTER_REQUEST = 0;
    private static final List<String> PERMISSIONS = Arrays.asList(
            FacebookConstants.PUBLISH_ACTIONS, FacebookConstants.PUBLIC_PROFILE, FacebookConstants.USER_VIDEOS);

    private EventDetail mUserEvent;
    private PostBL mPostBL;
    private ProgressHive mProgressBar;
    private User mUserPrefs;
    private ToggleButton mBtnTwitter, mBtnFacebook;
    private UiLifecycleHelper mUiHelper;
    private TwitterHelper mTwitterHelper;
    private Video mUploadedVideo;
    private FragmentManager mFragmentManager;
    private File mPhotoFile;
    private String mVideoPath, mMemePath, mThumbnailPath, mGalleryVideoPath;
    private byte[] mByteArray;
    private boolean mIsMemePost, mIsVideoPost, mIsEndVideo, mIsFromGalleryVideo, mIsSimplePost, mSendImageTwitter, mSendVideoTwitter, mIsCropingVideo = false;
    private static CreatePostFragment instance;

    private static final String FRAME_DUMP_FOLDER_PATH = Environment.getExternalStorageDirectory() + "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUiHelper = new UiLifecycleHelper(getActivity(), null);
        mUiHelper.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.create_post_fragment, container, false);
        ButterKnife.inject(this, view);
        instance = this;
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mPostBL = new PostBL();
        mPostBL.setListener(this);

        mProgressBar = new ProgressHive();
        mProgressBar.setCancelable(false);

        mUserEvent = getArguments().getParcelable(AppConstants.EVENT_DATA);
        mIsMemePost = getArguments().getBoolean(AppConstants.FROM_MEME);
        mIsVideoPost = getArguments().getBoolean(AppConstants.FROM_VIDEO);
        mIsFromGalleryVideo = getArguments().getBoolean(AppConstants.FROM_GALLERY_VIDEO);

        if (mIsMemePost) {
            mMemePath = getArguments().getString(AppConstants.MEME_PHOTO_PATH);
        }

        if (mIsVideoPost) {
            mVideoPath = getArguments().getString(AppConstants.VIDEO_PATH);
            if (!mIsCropingVideo) {
                mIsCropingVideo = true;
                if (mIsFromGalleryVideo) {
                    mGalleryVideoPath = getRealPathFromURI(getActivity(), Uri.parse(mVideoPath));
                    cropVideo(mGalleryVideoPath);
                } else {
                    cropVideo(mVideoPath);
                }
            }
        }

        if (!mIsMemePost && !mIsVideoPost) {
            mIsSimplePost = true;
        }

        mSendImageTwitter = mIsMemePost;
        mIsEndVideo = false;
        mUserPrefs = ManagePreferences.getUserPreferences();

        setInitialComponentsView();
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
                if (mIsFromGalleryVideo) {
                    if (new Utils().isVideoTooLong(mGalleryVideoPath)) {
                        Toast.makeText(getActivity(), getString(R.string.toast_video_too_long), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                if (validateTextPost()) {
                    createPost();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSessionStateChange(SessionState state) {
        if (state.isOpened()) {
            publishStory();
        } else {
            mProgressBar.dismiss();
        }
    }

    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null) {

            List<String> permissions = session.getPermissions();
            if (!FacebookUtils.isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString(FacebookConstants.MESSAGE, mEdtPost.getText().toString());

            if (mIsMemePost) {
                postParams.putString(FacebookConstants.FILE_NAME, "");
                postParams.putByteArray(FacebookConstants.IMAGE, mByteArray);
                postParams.putString(FacebookConstants.CAPTION, "");
            } else if (mIsEndVideo) {
                postParams.putString(FacebookConstants.NAME, mEdtPost.getText().toString());
                postParams.putString(FacebookConstants.DESCRIPTION, AppConstants.VIDEO);
                postParams.putString(FacebookConstants.LINK, mUploadedVideo.getFile());
                postParams.putString(FacebookConstants.PICTURE, mUploadedVideo.getThumbnail());
            } else if (mIsSimplePost) {
                postParams.putString(FacebookConstants.LINK, AppConstants.GOOGLE_PLAY_WE_HIVE);
            }

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        mFragmentManager.popBackStack(AppConstants.CAMERA_FILTERS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mFragmentManager.popBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mFragmentManager.popBackStack(AppConstants.MEME_CREATOR_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mProgressBar.dismiss();
                        mFragmentManager.popBackStackImmediate();
                    }
                }
            };

            Request request = null;
            if (mIsMemePost) {
                request = new Request(session, FacebookConstants.ME_PHOTOS, postParams, HttpMethod.POST, callback);
            } else if (mIsEndVideo || mIsSimplePost) {
                request = new Request(session, FacebookConstants.ME_FEED, postParams, HttpMethod.POST, callback);
            }

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
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
                if (mProgressBar.isVisible()) {
                    mProgressBar.dismiss();
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

        Log.i(TAG, " " + error.getLocalizedMessage() + " " + error.getMessage() + " " + error.toString());
        Post pendingPost = new Post();
        pendingPost.setId_user(String.valueOf(mUserPrefs.getId_user()));
        pendingPost.setPost_text(mEdtPost.getText().toString());
        pendingPost.setPostEventId(String.valueOf(mUserEvent.getId_event()));
        if (mIsMemePost) {
            pendingPost.setPostPhotoFile(mPhotoFile);
            pendingPost.setPostType(AppConstants.MEME_POST);
        } else if (mIsVideoPost) {
            pendingPost.setMedia_video_thumbnail(mThumbnailPath);
            pendingPost.setPostVideoPath(mVideoPath);
            pendingPost.setPostType(AppConstants.VIDEO_POST);
        }

        EventPostsResponse readResponse =
                PersistenceHelper.readResponse(getActivity(), AppConstants.PENDING_POSTS + "_" +
                        String.valueOf(mUserPrefs.getId_user()), EventPostsResponse.class);
        if (readResponse != null) {
            try {
                ArrayList<Post> data = readResponse.getData();
                data.add(pendingPost);
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }
        } else {
            readResponse = new EventPostsResponse();
            ArrayList<Post> posts = new ArrayList<Post>();
            posts.add(pendingPost);
            readResponse.setData(posts);
        }
        PersistenceHelper.saveResponse(getActivity(), readResponse,
                AppConstants.PENDING_POSTS + "_" + String.valueOf(mUserPrefs.getId_user()));

        Toast.makeText(getActivity(), getString(R.string.couldnt_create_post_post_saved), Toast.LENGTH_LONG).show();

        mFragmentManager.popBackStack(AppConstants.CAMERA_FILTERS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mFragmentManager.popBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mFragmentManager.popBackStack(AppConstants.MEME_CREATOR_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mProgressBar.dismiss();
        mFragmentManager.popBackStackImmediate();
    }

    @Override
    public void onResult(HiveResponse response) {
        if (mIsMemePost) {
            try {
                CreatePost postResponse = ((CreatePostResponse) response).getData();
                mPostBL.uploadPostMedia(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        String.valueOf(postResponse.getId()), mPhotoFile, "");
                sendShares();
                mIsMemePost = false;
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }

        } else if (mIsVideoPost) {
            try {
                Log.i(TAG, "mIsVideoPost " + mIsVideoPost);
                CreatePost postResponse = ((CreatePostResponse) response).getData();
                sendVideo(postResponse);
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }
        } else if (mIsEndVideo) {
            try {
                Log.i(TAG, "mIsEndVideo " + mIsEndVideo);
                mUploadedVideo = ((VideoUploadResponse) response).getData();
                mSendVideoTwitter = true;
                sendShares();
                mIsEndVideo = false;
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }
        } else if (mIsSimplePost) {
            sendShares();
        }
    }

    private void sendVideo(final CreatePost postResponse) {
        Log.i(TAG, "sendVideo called");
        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsCropingVideo) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "uploadPostVideo called");
                mPostBL.uploadPostVideo(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        String.valueOf(postResponse.getId()), new File(FRAME_DUMP_FOLDER_PATH + File.separator + "3.mp4"), new File(mThumbnailPath));
                mIsMemePost = false;
                mIsVideoPost = false;
                mIsEndVideo = true;
            }
        });
        send.start();
    }

    private void setInitialComponentsView() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.write_a_post));
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mEdtPost.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEdtPost.requestFocus();
                imm.showSoftInput(mEdtPost, 0);
            }
        }, 100);
        mBtnTwitter = (ToggleButton) getView().findViewById(R.id.btnTwitter);
        mBtnFacebook = (ToggleButton) getView().findViewById(R.id.btnFacebook);
        mTwitterHelper = new TwitterHelper();
        mTwitterHelper.setAccessToken(mUserPrefs.getToken_twitter());
        mTwitterHelper.setAccessTokenSecret(mUserPrefs.getToken_twittersecret());

        if (mIsMemePost) {
            mPhotoFile = new File(mMemePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
            mImgPost.setImageBitmap(myBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            mByteArray = stream.toByteArray();
        } else if (mIsVideoPost) {
            Bitmap thumb = null;
            if (mIsFromGalleryVideo) {
                thumb = ThumbnailUtils.createVideoThumbnail(mGalleryVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            } else {
                thumb = ThumbnailUtils.createVideoThumbnail(mVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            }
            mImgPost.setImageBitmap(thumb);
            mThumbnailPath = saveBitmap(thumb);
        } else {
            Picasso.with(getActivity()).load(mUserEvent.getPhoto()).into(mImgPost);
        }

        mTxtEventName.setVisibility(View.VISIBLE);
        mTxtEventPlace.setVisibility(View.VISIBLE);
        try {
            mTxtEventName.setText(mUserEvent.getName().toUpperCase());
            mTxtEventPlace.setText(mUserEvent.getPlace());
        } catch (Exception e) {
            Log.d(TAG, "setInitialComponentsView " + e.getMessage());
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void createPost() {
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");
        mSendVideoTwitter = false;
        mPostBL.createPost(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), mEdtPost.getText().toString(),
                String.valueOf(mUserEvent.getId_event()), POST_WITHOUT_VIDEO, "");
    }

    private boolean validateTextPost() {
        String textPost = mEdtPost.getText().toString();
        if (textPost.length() > 0) {
            return true;
        } else {
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.must_write_some_to_post), null);
            return false;
        }
    }

    private String saveBitmap(Bitmap bmp) {
        String thumbnailPath = null;
        try {
            thumbnailPath = AppConstants.THUMBNAIL_FROM_VIDEO_PATH +
                    ScreenshotHelper.nextPhotoId() + "tmp_thumbnail.jpg";

            File f = new File(thumbnailPath);
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return thumbnailPath;
    }

    private void sendShares() {
        if (mBtnFacebook.isChecked() || mBtnTwitter.isChecked()) {
            if (mBtnFacebook.isChecked()) {
                try {
                    Session session = Session.getActiveSession();
                    if (session != null && session.isOpened()) {
                        publishStory();
                    } else {
                        Session.openActiveSession(getActivity(), this, true, callback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            mFragmentManager.popBackStack(AppConstants.CAMERA_FILTERS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mFragmentManager.popBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mFragmentManager.popBackStack(AppConstants.MEME_CREATOR_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mProgressBar.dismiss();
            mFragmentManager.popBackStackImmediate();
        }
    }

    private void postTwitter() {
        try {
            String postText = mEdtPost.getText().toString();
            boolean sendStatus;
            if (mSendVideoTwitter) {
                sendStatus = mTwitterHelper.sendTweet(postText + " " + mUploadedVideo.getFile(), null);
            } else if (mSendImageTwitter) {
                sendStatus = mTwitterHelper.sendTweet(postText, mPhotoFile);
            } else {
                sendStatus = mTwitterHelper.sendTweet(postText, null);
            }

            if (!sendStatus) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        instance.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFragmentManager.popBackStack(AppConstants.CAMERA_FILTERS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                mFragmentManager.popBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                mFragmentManager.popBackStack(AppConstants.MEME_CREATOR_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                mProgressBar.dismiss();
                                mFragmentManager.popBackStackImmediate();
                            }
                        });
                    }
                }).start();
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

    private void cropVideo(final String path) {
        Thread crop = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "cropVideo called " + path);
                CropVideoFilter vfCrop;
                File fileAppRoot = new File(getActivity().getApplicationInfo().dataDir);
                try {
                    FfmpegController ffmpegController = new FfmpegController(getActivity(), fileAppRoot);
                    Clip clipIn = new Clip();
                    clipIn.path = path;
                    ffmpegController.getInfo(clipIn);
                    int dimension = Math.min(clipIn.width, clipIn.height);
                    if (dimension < 1) {
                        dimension = 100;
                    }

                    vfCrop = new CropVideoFilter(String.valueOf(dimension), String.valueOf(dimension), "0", "0");

                    if (clipIn.videoCodec != null && clipIn.videoCodec.contains(" ")) {
                        clipIn.videoCodec = clipIn.videoCodec.trim();
                        clipIn.videoCodec = clipIn.videoCodec.substring(0, clipIn.videoCodec.indexOf(" "));
                    }

                    if (clipIn.audioCodec != null && clipIn.audioCodec.contains(" ")) {
                        clipIn.audioCodec = clipIn.audioCodec.trim();
                        clipIn.audioCodec = clipIn.audioCodec.substring(0, clipIn.audioCodec.indexOf(" "));
                    }

                    final Clip clipOut = new Clip();
                    clipOut.path = new File(FRAME_DUMP_FOLDER_PATH + File.separator + "2.mp4").getCanonicalPath();
                    clipOut.videoFilter = vfCrop.getFilterString();
                    ffmpegController.processVideo(clipIn, clipOut, true, new ShellUtils.ShellCallback() {

                        @Override
                        public void shellOut(String shellLine) {
                        }

                        @Override
                        public void processComplete(int exitValue) {
                            transposeVideo();
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "FileNotFoundException " + e.getMessage());
                } catch (IOException e) {
                    Log.i(TAG, "IOException " + e.getMessage());
                } catch (InterruptedException e1) {
                    Log.i(TAG, "InterruptedException " + e1.getMessage());
                } catch (Exception e) {
                    Log.i(TAG, "Exception " + e.getStackTrace());
                    e.printStackTrace();
                }
            }
        });
        crop.start();
    }

    private void transposeVideo() {
        Thread crop = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "transposeVideo called ");
                TransposeVideoFilter vfTranspose;
                File fileAppRoot = new File(getActivity().getApplicationInfo().dataDir);

                try {
                    FfmpegController ffmpegController = new FfmpegController(getActivity(), fileAppRoot);
                    Clip clipIn = new Clip();
                    File fileIn = new File(FRAME_DUMP_FOLDER_PATH + File.separator + "2.mp4");
                    if (!fileIn.exists()) {
                        fileIn.createNewFile();
                    }
                    clipIn.path = fileIn.getCanonicalPath();
                    ffmpegController.getInfo(clipIn);
                    int dimension = Math.min(clipIn.width, clipIn.height);
                    if (dimension < 1) {
                        dimension = 100;
                    }

                    vfTranspose = new TransposeVideoFilter(TransposeVideoFilter.NINETY_CLOCKWISE);

                    if (clipIn.videoCodec != null && clipIn.videoCodec.contains(" ")) {
                        clipIn.videoCodec = clipIn.videoCodec.trim();
                        clipIn.videoCodec = clipIn.videoCodec.substring(0, clipIn.videoCodec.indexOf(" "));
                    }

                    if (clipIn.audioCodec != null && clipIn.audioCodec.contains(" ")) {
                        clipIn.audioCodec = clipIn.audioCodec.trim();
                        clipIn.audioCodec = clipIn.audioCodec.substring(0, clipIn.audioCodec.indexOf(" "));
                    }

                    final Clip clipOut = new Clip();
                    File fileOut = new File(FRAME_DUMP_FOLDER_PATH + File.separator + "3.mp4");
                    if (!fileOut.exists()) {
                        fileOut.createNewFile();
                    }
                    clipOut.path = fileOut.getCanonicalPath();
                    clipOut.videoFilter = vfTranspose.getFilterString();
                    ffmpegController.processVideo(clipIn, clipOut, true, new ShellUtils.ShellCallback() {

                        @Override
                        public void shellOut(String shellLine) {
                        }

                        @Override
                        public void processComplete(int exitValue) {
                            mIsCropingVideo = false;
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        crop.start();
    }
}