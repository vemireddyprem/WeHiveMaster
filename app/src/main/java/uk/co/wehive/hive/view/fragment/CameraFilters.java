package uk.co.wehive.hive.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.PreviewAdapter;
import uk.co.wehive.hive.entities.EventDetail;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.camera.ImageFilters;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class CameraFilters extends GATrackerFragment implements IOptionSelected {

    private String mPhotoPath;
    private Uri mPhotoUri;
    private ImageView mImgPhoto;
    private Bitmap mBitmapPhoto;
    private Bitmap mBitmapFilter;
    private ProgressHive mProgressHive;
    private EventDetail mEventDetails;
    private int mFilterSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_filters, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(AppConstants.PHOTO_PATH)) {
            mPhotoPath = bundle.getString(AppConstants.PHOTO_PATH);
        }

        if (bundle != null && bundle.containsKey(AppConstants.PHOTO_URI)) {
            mPhotoUri = Uri.parse(bundle.getString(AppConstants.PHOTO_URI));
        }

        mEventDetails = getArguments().getParcelable(AppConstants.EVENT_DATA);
        mFilterSelected = 0;
        initViewComponents();
    }

    private void initViewComponents() {
        mImgPhoto = (ImageView) getView().findViewById(R.id.img_photo);
        PreviewAdapter previewAdapter = new PreviewAdapter(getActivity());
        previewAdapter.setFilterSelected(this);

        if (mPhotoPath != null && mPhotoPath.length() > 0) {
            mBitmapPhoto = BitmapFactory.decodeFile(mPhotoPath);
        } else if (mPhotoUri != null) {
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(mPhotoUri);
                mBitmapPhoto = BitmapFactory.decodeStream(stream);
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        saveBitmap(mBitmapPhoto);

        if (mBitmapPhoto != null) {
            mImgPhoto.setImageBitmap(mBitmapPhoto);
            previewAdapter.setImgInitial(Bitmap.createScaledBitmap(mBitmapPhoto, 120, 120, false));
        }

        ViewPager mViewPagerFilters = (ViewPager) getView().findViewById(R.id.pager_gallery);
        mViewPagerFilters.setAdapter(previewAdapter);
        mViewPagerFilters.setOffscreenPageLimit(5);
        mViewPagerFilters.setClipChildren(false);
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.next_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                MemeCreatorFragment memeCreator = new MemeCreatorFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetails);
                memeCreator.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(AppConstants.CAMERA_FILTERS_FRAGMENT)
                        .replace(R.id.content_frame, memeCreator).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTypeSelected(final int index) {

        if (mFilterSelected != index) {
            mFilterSelected = index;
            mProgressHive.show(getActivity().getSupportFragmentManager(), null);
            Thread applyFilter = new Thread(new Runnable() {
                @Override
                public void run() {
                    mBitmapFilter = ImageFilters.setImage(index, mBitmapPhoto);
                    saveBitmap(mBitmapFilter);
                    sHandler.sendEmptyMessage(0);
                }
            });
            applyFilter.start();
        }
    }

    private Handler sHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            mImgPhoto.setImageBitmap(mBitmapFilter);
            mProgressHive.dismiss();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBitmapFilter != null)
            mBitmapFilter.recycle();
        if (mBitmapPhoto != null)
            mBitmapPhoto.recycle();
    }

    private void saveBitmap(Bitmap bmp) {
        try {
            File f = new File(AppConstants.FILTER_PATH);
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
