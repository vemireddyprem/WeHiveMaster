package uk.co.wehive.hive.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Media;
import uk.co.wehive.hive.utils.PhotoCarouselLayout;
import uk.co.wehive.hive.view.fragment.ItemCarouselFragment;
import uk.co.wehive.hive.view.fragment.PhotoCarouselFragment;

public class PhotoCarouselAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private final static int LOOPS = 1000;
    private final static float BIG_SCALE = 1.0f;
    private final static float SMALL_SCALE = 0.7f;
    private final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private PhotoCarouselFragment mContext;
    private FragmentManager mFm;
    private ArrayList<Media> mMediaList;

    public PhotoCarouselAdapter(PhotoCarouselFragment fragment, ArrayList<Media> list) {
        super(fragment.getActivity().getSupportFragmentManager());
        mFm = fragment.getActivity().getSupportFragmentManager();
        mContext = fragment;
        mMediaList = list;
    }

    @Override
    public Fragment getItem(int position) {
        position = position % mMediaList.size();
        return ItemCarouselFragment.newInstance(mMediaList.get(position));
    }

    @Override
    public int getCount() {
        return mMediaList.size() * LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            PhotoCarouselLayout cur = getRootView(position);
            PhotoCarouselLayout next = getRootView(position + 1);

            cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
            next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private PhotoCarouselLayout getRootView(int position) {
        String fragmentTag = this.getFragmentTag(position);
        View viewById = mFm.findFragmentByTag(fragmentTag).getView().findViewById(R.id.root);
        return (PhotoCarouselLayout) viewById;
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + mContext.mViewPager.getId() + ":" + position;
    }
}