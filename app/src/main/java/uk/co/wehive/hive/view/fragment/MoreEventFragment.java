/*******************************************************************************
 PROJECT:       Hive
 FILE:          MoreEventFragment.java
 DESCRIPTION:   Login view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Marcela Güiza    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Change error messages implementation.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.MoreEventDetail;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.MoreEventDetailResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class MoreEventFragment extends GATrackerFragment implements IHiveResponse, View.OnClickListener {

    @InjectView(R.id.txt_gallery) CustomFontTextView mTxtGallery;
    @InjectView(R.id.txt_followers) CustomFontTextView mTxtFollowers;
    @InjectView(R.id.txt_time) CustomFontTextView mTxtTime;
    @InjectView(R.id.txt_number_tracking_friends) CustomFontTextView mTxtTrackingFriends;
    @InjectView(R.id.img_first_friend) ImageView mImgFirstFriend;
    @InjectView(R.id.img_second_friend) ImageView mImgSecondFriend;
    @InjectView(R.id.img_third_friend) ImageView mImgThirdFriend;
    @InjectView(R.id.txt_line_up) CustomFontTextView mTxtLineUp;
    @InjectView(R.id.lyt_tracking) LinearLayout mLytTracking;

    private ProgressHive mProgressBar;
    private FragmentManager mFragmentManager;
    private String mEventId;
    private User userPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_event_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewComponents();

        String userId = String.valueOf(ManagePreferences.getUserPreferences().getId_user());
        mEventId = getArguments().getString(AppConstants.ID_EVENT);
        userPreference = ManagePreferences.getUserPreferences();
        EventsBL eventsBL = new EventsBL();
        eventsBL.setEventsUserListener(this);
        eventsBL.getMoreDetail(userPreference.getToken(),userId, mEventId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.more_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_less:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.connection_lost));
    }

    @Override
    public void onResult(HiveResponse response) {
        MoreEventDetail eventDetail = ((MoreEventDetailResponse) response).getData();
        setViewInfo(eventDetail);
        mProgressBar.dismiss();
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.event_details_title));
        mProgressBar = new ProgressHive();
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");
        mFragmentManager = getActivity().getSupportFragmentManager();

        mLytTracking.setOnClickListener(this);
        mTxtGallery.setOnClickListener(this);
    }

    private void setViewInfo(MoreEventDetail eventDetail) {
        mTxtGallery.setText(String.valueOf(eventDetail.getMedia_count()));
        mTxtFollowers.setText(String.valueOf(eventDetail.getTotal_followers()));

        String eventDate = eventDetail.getDate_event() != null ? eventDetail.getDate_event() : eventDetail.getDate_event();
        if (eventDate != null) {
            String hour = DateOperations.getDate(Long.parseLong(eventDate) * 1000, "H:mm");
            mTxtTime.setText(hour);
        }

        ArrayList<Follower> followers = eventDetail.getFollowers();
        if (followers.size() >= 1) {
            setImage(followers.get(0).getReal_photo(), mImgFirstFriend);
        }
        if (followers.size() >= 2) {
            setImage(followers.get(1).getReal_photo(), mImgSecondFriend);
        }
        if (followers.size() >= 3) {
            setImage(followers.get(2).getReal_photo(), mImgThirdFriend);
        }

        if (followers.size() == 0) {
            mTxtTrackingFriends.setText(getResources().getString(R.string.no_friends_are_tracking));
        } else if (followers.size() == 1) {
            mTxtTrackingFriends.setText(String.valueOf(eventDetail.getTotal_followers_friends())
                    + " " + getResources().getString(R.string.friend_is_tracking));
        } else {
            mTxtTrackingFriends.setText(String.valueOf(eventDetail.getTotal_followers_friends())
                    + " " + getResources().getString(R.string.friends_are_tracking));
        }

        mTxtLineUp.setText(mTxtLineUp.getText() + " " + eventDetail.getLineup());
    }

    private void setImage(String photo, ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);

        if (photo != null && photo.length() > 6) {
            Picasso.with(getActivity()).load(photo)
                    .transform(new PictureTransformer())
                    .error(R.drawable.ic_userphoto_menu)
                    .into(imageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.event_details_title));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lyt_tracking:
                Bundle followers = new Bundle();
                followers.putString(AppConstants.ID_EVENT, mEventId);
                EventFollowersFragment followersFragment = new EventFollowersFragment();
                followersFragment.setArguments(followers);
                mFragmentManager.beginTransaction().replace(R.id.content_frame, followersFragment).addToBackStack("").commit();
                break;

            case R.id.txt_gallery:
                PhotoCarouselFragment fragment = new PhotoCarouselFragment();
                Bundle gallery = new Bundle();
                gallery.putString(AppConstants.ID_EVENT, mEventId);
                fragment.setArguments(gallery);
                mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("").commit();
                break;
        }
    }
}