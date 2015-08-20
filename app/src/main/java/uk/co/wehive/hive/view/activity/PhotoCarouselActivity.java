package uk.co.wehive.hive.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.PhotoCarouselAdapterM;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.Media;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.MediaListResponse;
import uk.co.wehive.hive.listeners.ICarouselCurrentView;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class PhotoCarouselActivity extends ActionBarActivity implements IHiveResponse,
        FragmentManager.OnBackStackChangedListener, ICarouselCurrentView {

    private final static int LOOPS = 1000;

    public ViewPager mViewPager;
    private ProgressHive mProgressBar;
    private ArrayList<Media> mMediaList;
    private FragmentManager mFragmentManager;
    private boolean mAnyOptionSelected;
    private final String TAG = "PhotoCarouselActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_carousel_activity);

        initViewComponents();

        Bundle extras = getIntent().getExtras();
        String eventId = extras.getString(AppConstants.ID_EVENT);
        String userId = String.valueOf(ManagePreferences.getUserPreferences().getId_user());

        EventsBL eventsBL = new EventsBL();
        eventsBL.setEventsUserListener(this);
        eventsBL.getMediaList(ManagePreferences.getUserPreferences().getToken(), userId, eventId, "0", "50");
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
    }

    @Override
    public void onResult(HiveResponse response) {
        try {
            mMediaList.addAll(((MediaListResponse) response).getData());
        } catch (Exception e) {
            Log.d(TAG, "error " + e.getMessage());
        }

        if (mMediaList.size() == 0) {
            ErrorDialog.showErrorMessage(this, "", getString(R.string.no_image_available));
            mFragmentManager.popBackStack();
        } else {
            PhotoCarouselAdapterM adapter = new PhotoCarouselAdapterM(this, mMediaList);
            adapter.setListener(this);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            mViewPager.setCurrentItem(mMediaList.size() * LOOPS / 2);
            mViewPager.setOffscreenPageLimit(3);
        }
        mProgressBar.dismiss();
    }

    private void initViewComponents() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);
        mMediaList = new ArrayList<Media>();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setVisibility(View.VISIBLE);

        mProgressBar = new ProgressHive();
        mProgressBar.setCancelable(false);
        mProgressBar.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onBackStackChanged() {
        if (mAnyOptionSelected) {
            mViewPager.setVisibility(View.GONE);
            mAnyOptionSelected = false;
        } else {
            mViewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateItemPosition(int position) {
    }
}
