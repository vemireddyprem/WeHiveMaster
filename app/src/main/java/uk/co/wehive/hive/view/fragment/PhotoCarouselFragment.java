package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.PhotoCarouselAdapter;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.Media;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.MediaListResponse;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.PhotoCarouselOptionsDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class PhotoCarouselFragment extends GATrackerFragment implements IHiveResponse, IOptionSelected {

    private final static int LOOPS = 1000;
    private final String TAG = "PhotoCarouselFragment";

    public ViewPager mViewPager;
    private ProgressHive mProgressBar;
    private ArrayList<Media> mMediaList;
    private FragmentManager mFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_carousel_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewComponents();

        String eventId = getArguments().getString(AppConstants.ID_EVENT);
        String userId = String.valueOf(ManagePreferences.getUserPreferences().getId_user());

        EventsBL eventsBL = new EventsBL();
        eventsBL.setEventsUserListener(this);
        eventsBL.getMediaList(ManagePreferences.getUserPreferences().getToken(), userId, eventId, "0", "50");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.user_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                PhotoCarouselOptionsDialog dialog = new PhotoCarouselOptionsDialog();
                dialog.setTypeSelectedListener(this);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mProgressBar.dismiss();

        if (mMediaList.size() == 0) {
            ErrorDialog.showErrorMessage(getActivity(), "", getString(R.string.no_image_available));
            mFragmentManager.popBackStack();
        } else {
            PhotoCarouselAdapter adapter = new PhotoCarouselAdapter(this, mMediaList);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            mViewPager.setCurrentItem(mMediaList.size() * LOOPS / 2);
            mViewPager.setOffscreenPageLimit(3);
        }
    }

    private void initViewComponents() {
        mFragmentManager = getActivity().getSupportFragmentManager();
        mMediaList = new ArrayList<Media>();
        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        setHasOptionsMenu(true);
        mProgressBar = new ProgressHive();
        mProgressBar.setCancelable(false);
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onTypeSelected(int index) {

        int actualPosition = mViewPager.getCurrentItem() % mMediaList.size();
        Post currentPost = createPost(mMediaList.get(actualPosition));

        if (index == 0) {
            ShareGoodTimesDetailFragment shareGoodTimesDetailFragment = new ShareGoodTimesDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.POST, currentPost);
            shareGoodTimesDetailFragment.setArguments(bundle);

            mFragmentManager.beginTransaction().addToBackStack("")
                    .replace(R.id.content_frame, shareGoodTimesDetailFragment).commit();
        } else if (index == 1) {
            Bundle bundle;
            bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, currentPost.getId_post());

            FlagPostFragment flagPostFragment = new FlagPostFragment();
            flagPostFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, flagPostFragment).commit();
        } else if (index == 2) {
            Bundle bundle;
            bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, currentPost.getId_post());

            PostLikersFragment fragment = new PostLikersFragment();
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreCarousel();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreCarousel();
    }

    private void restoreCarousel() {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (fragment != null && fragment.getTag() != null &&
                    fragment.getTag().startsWith("android:switcher:" + R.id.pager)) {
                mFragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    private Post createPost(Media mMedia) {
        Post post = new Post();
        post.setId_post(String.valueOf(mMedia.getId_post()));
        post.setAge_post(mMedia.getAge_post());
        post.setMedia_absolute_file(mMedia.getAbsolute_file());
        post.setMedia_absolute_thumbvideo_file(mMedia.getThumbnail());
        post.setLiked(String.valueOf(mMedia.isLiked()));
        post.setShare_link(mMedia.getShare_link());
        post.setFirst_name(mMedia.getFirst_name());
        post.setLast_name(mMedia.getLast_name());
        post.setUser_photo(mMedia.getUser_photo());
        post.setMedia_type(mMedia.getType());
        return post;
    }
}