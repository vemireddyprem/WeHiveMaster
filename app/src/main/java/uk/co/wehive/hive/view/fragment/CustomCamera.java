package uk.co.wehive.hive.view.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.EventDetail;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CropOption;
import uk.co.wehive.hive.utils.CropOptionAdapter;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.camera.CameraPreview;

public class CustomCamera extends Fragment implements View.OnClickListener, MediaRecorder.OnInfoListener {

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int MEDIA_TYPE_GALLERY = 3;
    private static final int CROP_FROM_CAMERA = 2;
    private static final String SV_MODEL = "SM-G900M";
    private static final String NEXUS_5_MODEL = "Nexus 5";
    private final String TAG = "CustomCamera";

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView mImgFlash;
    private ImageView mImgCameraMode;
    private ImageView mImgVideoMode;
    private FragmentManager mFragmentManager;
    private Uri mImageCaptureUri;
    private int mNumberOfCameras;
    private int mCurrentCamera;         // Camera ID currently chosen
    private int mCameraCurrentlyLocked; // Camera ID that's actually acquired
    private boolean mUseFlash = false;
    private int mModeSelected = 1;

    // Video variables
    private static String mVideoName;
    private MediaRecorder mMediaRecorder;
    private Menu menu;
    private EventDetail mEventDetails;
    private FrameLayout mPreviewLayout;
    private boolean mIsRecording = false;
    private int mMinute;
    private int mSecond;
    private long mStartTime;
    private boolean mContinue = true;
    private boolean mIsTaking = false;
    private boolean mIsGallery = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mEventDetails = getArguments().getParcelable(AppConstants.EVENT_DATA);
        mFragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentCamera = 0;
        mModeSelected = MEDIA_TYPE_IMAGE;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContinue = true;
        if (mPreviewLayout != null) {
            mPreviewLayout.removeAllViews();
        }
        initViewComponents();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
        mContinue = false;
        if (!mIsGallery) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.custom_camera, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_camera:
                mFragmentManager.beginTransaction().remove(this).commit();
                mFragmentManager.popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int currentCamera) {
        Camera c = null;
        try {
            c = Camera.open(currentCamera); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseMediaRecorder() {
        try {
            mMediaRecorder.stop();  // stop the recording
        } catch (RuntimeException stopException) {
            //handle cleanup here
        }

        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Initialize view components.
     */
    private void initViewComponents() {

        CustomViewHelper.setUpActionBar(getActivity(), "");

        mCamera = getCameraInstance(mCurrentCamera);
        mCameraCurrentlyLocked = mCurrentCamera;

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this.getActivity());

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the rear-facing ("default") camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCurrentCamera = i;
            }
        }

        mPreview.setCamera(mCamera);

        mPreviewLayout = (FrameLayout) getView().findViewById(R.id.camera_preview);
        mPreviewLayout.addView(mPreview);

        ImageView imgChangeCamera = (ImageView) getView().findViewById(R.id.img_change_camera);
        if (mNumberOfCameras > 1) {
            imgChangeCamera.setOnClickListener(this);
        }

        mImgFlash = (ImageView) getView().findViewById(R.id.img_flash);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            mImgFlash.setOnClickListener(this);
        }

        ImageView imgTakePicture = (ImageView) getView().findViewById(R.id.img_take_picture);
        imgTakePicture.setOnClickListener(this);

        mImgCameraMode = (ImageView) getView().findViewById(R.id.img_camera_mode);
        mImgCameraMode.setOnClickListener(this);

        mImgVideoMode = (ImageView) getView().findViewById(R.id.img_video_mode);
        mImgVideoMode.setOnClickListener(this);

        ImageView mImgGalleyMode = (ImageView) getView().findViewById(R.id.img_gallery_mode);
        mImgGalleyMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_change_camera:
                // Release this camera -> mCameraCurrentlyLocked
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mPreview.setCamera(null);
                    mCamera.release();
                    mCamera = null;
                }

                // Acquire the next camera and request Preview to reconfigure parameters.
                mCurrentCamera = (mCameraCurrentlyLocked + 1) % mNumberOfCameras;
                mCamera = Camera.open(mCurrentCamera);
                mCameraCurrentlyLocked = mCurrentCamera;
                mPreview.switchCamera(mCamera);

                // Start the preview
                mCamera.startPreview();
                break;

            case R.id.img_flash:
                mImgFlash.setImageResource(mUseFlash ? R.drawable.ic_flash_camera_inactive : R.drawable.ic_flash_camera_active);
                mUseFlash ^= true;

                if (mUseFlash) {
                    mPreview.setFlash(Camera.Parameters.FLASH_MODE_ON);
                } else {
                    mPreview.setFlash(Camera.Parameters.FLASH_MODE_OFF);
                }
                break;

            case R.id.img_take_picture:
                if (!mIsTaking) {
                    mIsTaking = true;
                    takeMedia();
                }
                break;

            case R.id.img_camera_mode:
                mIsGallery = false;
                mImgCameraMode.setImageResource(R.drawable.ic_mode_camera_active);
                mImgVideoMode.setImageResource(R.drawable.ic_video_camera_inactive);
                CustomViewHelper.setUpActionBar(getActivity(), "");
                mModeSelected = MEDIA_TYPE_IMAGE;
                break;

            case R.id.img_video_mode:
                mIsGallery = false;
                mImgCameraMode.setImageResource(R.drawable.ic_mode_camera_inactive);
                mImgVideoMode.setImageResource(R.drawable.ic_video_camera_active);
                CustomViewHelper.setUpActionBar(getActivity(), "00:00");
                mModeSelected = MEDIA_TYPE_VIDEO;
                break;

            case R.id.img_gallery_mode:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        mIsGallery = true;
        mPreview.getHolder().removeCallback(mPreview);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*, video/*");

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), MEDIA_TYPE_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            switch (requestCode) {

                case CROP_FROM_CAMERA:
                    Bundle extras = data.getExtras();

                    mIsGallery = false;
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        extras.putString(AppConstants.PHOTO_URI, getImageUri(photo).toString());
                        extras.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
                        CameraFilters cameraFilters = new CameraFilters();
                        cameraFilters.setArguments(extras);

                        mFragmentManager.beginTransaction()
                                .replace(R.id.content_frame, cameraFilters)
                                .addToBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT).commit();
                    }
                    break;

                case MEDIA_TYPE_GALLERY:
                    mImageCaptureUri = data.getData();

                    if (Build.VERSION.SDK_INT < 19) {
                        doCrop();
                    } else {
                        Bitmap photoSquare = getPhotoSquare(mImageCaptureUri);

                        Bundle bundle = new Bundle();
                        //bundle for image
                        if (mImageCaptureUri.toString().contains("images")) {
                            bundle.putString(AppConstants.PHOTO_URI, getImageUri(photoSquare).toString());
                            bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
                            CameraFilters cameraFilters = new CameraFilters();
                            cameraFilters.setArguments(bundle);
                            mFragmentManager.beginTransaction().replace(R.id.content_frame, cameraFilters)
                                    .addToBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT).commit();
                        } else if (mImageCaptureUri.toString().contains("video")) {
                            //bundle for video
                            startVideoFragment(true, mImageCaptureUri.toString());
                        }
                    }
                    break;
            }
        }
    }

    private void startVideoFragment(boolean isFromGalleryVideo, String url) {
        Log.i(TAG, "startVideoFragment CALLED");
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
        bundle.putBoolean(AppConstants.FROM_MEME, false);
        bundle.putBoolean(AppConstants.FROM_VIDEO, true);
        bundle.putBoolean(AppConstants.FROM_GALLERY_VIDEO, isFromGalleryVideo);
        if (isFromGalleryVideo) {
            bundle.putString(AppConstants.VIDEO_PATH, url);
        } else {
            bundle.putString(AppConstants.VIDEO_PATH, mVideoName);
        }
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.content_frame, fragment)
                .addToBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT).commit();
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

    private void takeMedia() {
        switch (mModeSelected) {
            case MEDIA_TYPE_IMAGE:
                mCamera.takePicture(null, null, mPicture);
                break;
            case MEDIA_TYPE_VIDEO:
                captureVideo();
                mIsTaking = false;
                break;
        }
    }

    private void captureVideo() {
        if (mIsRecording) {
            mContinue = false;
            // stop recording and release camera

            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            mIsRecording = false;
            startVideoFragment(false, null);
        } else {
            // initialize video camera
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_active));
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,  now you can start recording
                mMediaRecorder.start();
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mStartTime = System.currentTimeMillis();

                Thread mVideoTimeThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            long millis;
                            while (mContinue) {
                                Thread.sleep(1000);
                                millis = System.currentTimeMillis() - mStartTime;
                                mSecond = (int) (millis / 1000);
                                mMinute = mSecond / 60;
                                mSecond = mSecond % 60;
                                sHandler.sendEmptyMessage(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                mVideoTimeThread.start();
                mIsRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    private Handler sHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (getActivity() != null) {
                CustomViewHelper.setUpActionBar(getActivity(), String.format("%02d", mMinute) + ":" + String.format("%02d", mSecond));
            }
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            mIsTaking = false;
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d("PictureCallback", "Error creating media file, check storage permissions: ");
                return;
            }

            try {

                Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);

                Bitmap modified;
                Matrix matrix = new Matrix();
                if (mCameraCurrentlyLocked == 0) {
                    matrix.setRotate(90);
                } else if (mCameraCurrentlyLocked == 1) {
                    matrix.setRotate(90);
                    matrix.preScale(-1, 1);
                }

                String deviceName = android.os.Build.MODEL;

                modified = Bitmap.createBitmap(original, 0, 0, original.getWidth(),
                        original.getHeight(), matrix, false);

                if (mCameraCurrentlyLocked == 1 && !NEXUS_5_MODEL.equals(deviceName)) {
                    modified = Bitmap.createBitmap(modified, 0, actionBarHeight,
                            modified.getWidth(), modified.getHeight() - actionBarHeight);
                }
                if (modified.getWidth() >= modified.getHeight()) {
                    modified = Bitmap.createBitmap(
                            modified,
                            0,
                            0,
                            modified.getHeight(),
                            modified.getHeight());
                } else {
                    modified = Bitmap.createBitmap(
                            modified,
                            0,
                            0,
                            modified.getWidth(),
                            modified.getWidth());
                }

                if (!SV_MODEL.equals(deviceName) && !NEXUS_5_MODEL.equals(deviceName)
                        && !deviceName.contains("HTC")) {
                    int size = modified.getWidth();
                    int unit = size / 8;
                    //Bitmap source, int x, int y, int width, int height
                    modified = Bitmap.createBitmap(modified, unit, 0, size - (2 * unit), size - (2 * unit));
                }

                //Changed the compression format from JPEG to PNG for better quality.
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                modified.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                data = bytes.toByteArray();

                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.PHOTO_PATH, pictureFile.getAbsolutePath());
                bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
                bundle.putInt(AppConstants.PHOTO_ORIGIN, mCameraCurrentlyLocked);

                CameraFilters cameraFilters = new CameraFilters();
                cameraFilters.setArguments(bundle);
                mFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, cameraFilters)
                        .addToBackStack(AppConstants.CUSTOM_CAMERA_FRAGMENT).commit();

            } catch (FileNotFoundException e) {
                Log.d("PictureCallback", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "weHive");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("weHive", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeStamp = formatter.format(new Date());
        File mediaFile;

        String tmp = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {

            //TODO: Esta línea es para pruebas
            // mVideoName = tmp + File.separator + "VID_" + timeStamp + ".mp4";

            mVideoName = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
            mediaFile = new File(mVideoName);
        } else {
            return null;
        }
        return mediaFile;
    }

    private boolean prepareVideoRecorder() {

        //mCamera = getCameraInstance(mCurrentCamera);
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        /*mMediaRecorder.setAudioChannels(1);

        mMediaRecorder.setVideoFrameRate(8);
        mMediaRecorder.setVideoEncodingBitRate(1000000);*/

        mMediaRecorder.setOrientationHint(90);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setMaxDuration(30000);
        mMediaRecorder.setOnInfoListener(this);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("CustomCamera", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("CustomCamera", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        captureVideo();
        mIsTaking = false;
    }

    //Crop image after pick it up from gallery
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

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "temp", null);
        return Uri.parse(path);
    }
}
