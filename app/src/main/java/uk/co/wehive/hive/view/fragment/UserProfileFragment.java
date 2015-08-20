/*******************************************************************************
 PROJECT:       Hive
 FILE:          UserProfileFragment.java
 DESCRIPTION:   User profile fragment
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        14/07/2014  Marcela Güiza    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Added service call.
 1.0        18/07/2014  Juan Pablo B.    3. Load good times info.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.GoodTimeAdapter;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.FollowUser;
import uk.co.wehive.hive.entities.GoodTimes;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.FollowUserResponse;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.GoodTimesResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontButton;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.GPSLocation;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.PictureTransformer;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;
import uk.co.wehive.hive.view.dialog.ReportUserDialog;
import uk.co.wehive.hive.view.dialog.ShowPictureDialog;
import uk.co.wehive.hive.view.dialog.UserProfileSettingsDialog;

public class UserProfileFragment extends GATrackerFragment implements View.OnClickListener,
        IOptionSelected, IHiveResponse {

    @InjectView(R.id.img_user_photo)
    ImageView mImgUserPhoto;
    @InjectView(R.id.txt_username)
    CustomFontTextView mTxtUsername;
    @InjectView(R.id.txt_user_city)
    CustomFontTextView mTxtUserCity;
    @InjectView(R.id.img_twitter_connect)
    ImageView mImgTwitter;
    @InjectView(R.id.img_facebook_connect)
    ImageView mImgFacebook;
    @InjectView(R.id.btn_user_followers)
    CustomFontButton mBtnFollowers;
    @InjectView(R.id.btn_user_following)
    CustomFontButton mBtnFollowing;
    @InjectView(R.id.btn_user_events)
    CustomFontButton mBtnEvents;
    @InjectView(R.id.txt_empty_goodtimes)
    TextView mTxtEmptyGoodTimes;
    @InjectView(R.id.lyt_social)
    LinearLayout mLytSocial;
    @InjectView(R.id.grid_goodtimes)
    GridView mGridGoodTimes;
    @InjectView(R.id.btn_follow)
    Button mBtnFollow;
    @InjectView(R.id.txt_bio)
    TextView mTxtBio;

    private static Fragment mContext;
    private ProgressHive mProgressHive;
    private SocialNetworkFragment mSocialNetworkFragment;
    private FragmentManager mFragmentManager;
    private User mUserPrefs;
    private User mUserProfile;
    private ArrayList<Follower> mUserFollowersList;
    private ArrayList<Follower> mUserFollowingList;
    private GPSLocation gpsLocation;
    private String mUserId;
    private UsersBL mUserBL;
    private final String TAG = "UserProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        mFragmentManager = getActivity().getSupportFragmentManager();

        mProgressHive = new ProgressHive();
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        mUserPrefs = ManagePreferences.getUserPreferences();

        if (bundle != null && bundle.containsKey(AppConstants.USER_ID)) {
            mUserId = bundle.getString(AppConstants.USER_ID);
        } else {
            mUserId = String.valueOf(mUserPrefs.getId_user());
        }
        setHasOptionsMenu(true);

        UsersBL usersBL = new UsersBL();
        usersBL.setHiveListener(this);
        Log.i(TAG, " token before -onViewCreated " + mUserPrefs.getToken());
        usersBL.profile(mUserPrefs.getToken(), mUserId, String.valueOf(mUserPrefs.getId_user()));
        Log.i(TAG, " token after -onViewCreated " + mUserPrefs.getToken());
        mContext = this;
        // Call services for getting user followers and following lists
        setUserNetwork();
        initViewComponents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (String.valueOf(mUserPrefs.getId_user()).equals(mUserId) || mUserId == null) {
            getActivity().getMenuInflater().inflate(R.menu.user_profile, menu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.report_user, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                UserProfileSettingsDialog userProfileSettingsDialog = new UserProfileSettingsDialog();
                userProfileSettingsDialog.setTypeSelectedListener(this);
                userProfileSettingsDialog.show(getActivity().getSupportFragmentManager(), null);
                return true;
            case R.id.report_user:
                ReportUserDialog reportUserDialog = new ReportUserDialog();
                reportUserDialog.setTypeSelectedListener(new ReportUserListener());
                reportUserDialog.show(getActivity().getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Initialize view components
    private void initViewComponents() {

        mImgUserPhoto.setOnClickListener(this);
        mBtnFollowers.setText("0\n" + getString(R.string.followers));
        mBtnFollowers.setOnClickListener(this);
        mBtnFollowing.setText("0\n" + getString(R.string.following));
        mBtnFollowing.setOnClickListener(this);
        mBtnEvents.setText("0\n" + getString(R.string.events));
        mBtnEvents.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mSocialNetworkFragment = new SocialNetworkFragment();
        addEvents();

        if (mUserPrefs.getId_twitter().length() > 0) {
            mImgTwitter.setBackgroundResource(R.drawable.ic_share_twitter_white);
        }
        if (mUserPrefs.getId_facebook().length() > 0) {
            mImgFacebook.setBackgroundResource(R.drawable.ic_share_facebook_white);
        }

        gpsLocation = new GPSLocation(this.getActivity());
    }

    private void addEvents() {
        mGridGoodTimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                GoodTimes goodTimes = (GoodTimes) adapter.getItemAtPosition(position);
                changeToGoodTimesDetailFragment(goodTimes);
            }
        });
    }

    private void changeToGoodTimesDetailFragment(GoodTimes goodTimes) {
        if (goodTimes.isShow_gallery()) {
            GoodTimesDetailFragment goodTimesDetailFragment = new GoodTimesDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.GOODTIMES, goodTimes);
            bundle.putString(AppConstants.USER_ID, mUserId);
            bundle.putString(AppConstants.USERNAME, mUserProfile.getUsername());
            goodTimesDetailFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, goodTimesDetailFragment).commit();
        } else {
            ErrorDialog.showErrorMessage(getActivity(), "", getString(R.string.you_can_access_your_good_time));
        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle;

        switch (view.getId()) {

            case R.id.btn_user_followers:
                bundle = new Bundle();
                bundle.putString(AppConstants.USER_NETWORK, getString(R.string.followers));
                bundle.putString(AppConstants.USER_ID, String.valueOf(mUserId));
                bundle.putParcelableArrayList(AppConstants.FOLLOWERS_LIST, mUserFollowersList);
                mSocialNetworkFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, mSocialNetworkFragment).commit();
                break;

            case R.id.btn_user_following:
                bundle = new Bundle();
                bundle.putString(AppConstants.USER_NETWORK, getString(R.string.following));
                bundle.putString(AppConstants.USER_ID, String.valueOf(mUserId));
                bundle.putParcelableArrayList(AppConstants.FOLLOWED_LIST, mUserFollowingList);
                mSocialNetworkFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, mSocialNetworkFragment).commit();
                break;

            case R.id.btn_user_events:
                EventsUserFragment eventsUserFrament = new EventsUserFragment();
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, eventsUserFrament).commit();
                break;

            case R.id.img_user_photo:
                if (mUserProfile != null) {
                    ShowPictureDialog pictureDialog = new ShowPictureDialog();
                    pictureDialog.setUrlPhoto(mUserProfile.getPhoto());
                    pictureDialog.show(getActivity().getSupportFragmentManager(), null);
                }
                break;

            case R.id.btn_follow:
                if (mUserProfile.isBlocked()) {
                    unblockUser();
                } else {
                    followUser();
                }
                break;
        }
    }

    private void unblockUser() {
        mUserProfile.setFollow(false);
        mUserProfile.setBlocked(false);
        mBtnFollow.setBackgroundResource(R.drawable.bt_followed_people_background);
        mBtnFollow.setText(getText(R.string.follow));

        mProgressHive.show(getActivity().getSupportFragmentManager(), "");
        if (mUserBL == null) {
            mUserBL = new UsersBL();
        }
        mUserBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
            }

            @Override
            public void onResult(HiveResponse response) {
                mProgressHive.dismiss();
            }
        });

        mUserBL.unblockUser(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), String.valueOf(mUserProfile.getId_user()));
    }

    private void followUser() {
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);
        UsersBL followUserBL = new UsersBL();
        followUserBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                if (mProgressHive.isVisible()) {
                    mProgressHive.dismiss();
                }
            }

            @Override
            public void onResult(HiveResponse response) {
                if (mProgressHive.isVisible()) {
                    mProgressHive.dismiss();
                }
                if (response.getStatus()) {
                    FollowUser followUser = ((FollowUserResponse) response).getData();
                    mUserPrefs.setConnections(followUser.getConnections());
                    ManagePreferences.setPreferencesUser(mUserPrefs);

                    mUserProfile.setFollow(!mUserProfile.isFollow());

                    mBtnFollow.setBackgroundResource(mUserProfile.isFollow() ?
                            R.drawable.bt_following_people_background :
                            R.drawable.bt_followed_people_background);

                    mBtnFollow.setText(mUserProfile.isFollow() ? getText(R.string.following) : getText(R.string.follow));
                }
            }
        });
        followUserBL.followUser(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                String.valueOf(mUserProfile.getId_user()), mUserProfile.isFollow() ? "0" : "1");
    }

    private void setUserNetwork() {
        UsersBL followersNetwork = new UsersBL();
        mUserPrefs = ManagePreferences.getUserPreferences();
        String userId = String.valueOf(mUserPrefs.getId_user());
        followersNetwork.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
            }

            @Override
            public void onResult(HiveResponse response) {
                mUserFollowersList = ((UserNetworkResponse) response).getData();
            }
        });
        followersNetwork.getFollowers(mUserPrefs.getToken(), userId, mUserId, "0", "50");

        UsersBL followingNetwork = new UsersBL();
        followingNetwork.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
            }

            @Override
            public void onResult(HiveResponse response) {
                mUserFollowingList = ((UserNetworkResponse) response).getData();
            }
        });
        followingNetwork.getFollowing(mUserPrefs.getToken(), userId, mUserId, "0", "50");

        UsersBL goodTimes = new UsersBL();
        goodTimes.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
            }

            @Override
            public void onResult(HiveResponse response) {
                setUserGoodTimesData(((GoodTimesResponse) response));
            }
        });
        goodTimes.getGoodTimes(mUserPrefs.getToken(), mUserId, "0", "10");
    }

    private void setUserGoodTimesData(GoodTimesResponse response) {
        List<GoodTimes> goodTimesList = new ArrayList<GoodTimes>();
        goodTimesList.addAll(response.getData());

        GoodTimeAdapter goodTimesAdapter = new GoodTimeAdapter(mContext, goodTimesList, 200);
        goodTimesAdapter.notifyDataSetChanged();
        mGridGoodTimes.setAdapter(goodTimesAdapter);

        if (goodTimesList.size() <= 0) {
            mTxtEmptyGoodTimes.setVisibility(View.VISIBLE);
            mGridGoodTimes.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTypeSelected(int index) {
        Bundle bundle = new Bundle();
        switch (index) {

            case 0: // Edit profile
                bundle.putParcelable(AppConstants.USER_PROFILE, mUserProfile);
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, editProfileFragment).commit();
                break;

            case 1: // Follow People
                FollowPeopleFragment followPeopleFragment = new FollowPeopleFragment();
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, followPeopleFragment).commit();
                break;

            case 2: // Follow artist
                SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
                bundle.putString(AppConstants.USER_NETWORK, getString(R.string.track_artists_title));
                socialNetworkFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, socialNetworkFragment).commit();
                break;
        }
    }

    public void onResume() {
        super.onResume();

        if (mUserProfile != null && mUserProfile.getUsername().length() > 0) {
            CustomViewHelper.setUpActionBar(getActivity(), "@" + mUserProfile.getUsername());
        } else {
            CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.menu_option_user));
        }
    }

    @Override
    public void onError(RetrofitError error) {
        try {
            mProgressHive.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        if (response.getStatus()) {
            mUserProfile = ((LoginResponse) response).getData();
            if (String.valueOf(mUserPrefs.getId_user()).equals(mUserId)) {
                mUserProfile.setToken(mUserPrefs.getToken());
                ManagePreferences.setPreferencesUser(mUserProfile);
                ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
            }
            mGridGoodTimes.setVisibility(View.VISIBLE);
            setViewData();
        } else {
            removeEvents();
            Resources resources = getResources();
            mGridGoodTimes.setVisibility(View.GONE);
            mGridGoodTimes.deferNotifyDataSetChanged();
            mProgressHive.dismiss();
            ErrorDialog.showErrorMessage(getActivity(), resources.getString(R.string.message_error), resources.getString(R.string.message_user_not_found));
        }
    }

    private void removeEvents() {
        setHasOptionsMenu(false);
        mImgUserPhoto.setOnClickListener(null);
        mBtnFollowers.setOnClickListener(null);
        mBtnFollowing.setOnClickListener(null);
        mBtnEvents.setOnClickListener(null);
        mBtnFollow.setOnClickListener(null);
    }

    private void setViewData() {
        String photoUrl = mUserProfile.getPhoto();
        if (photoUrl.length() > 10) {
            Picasso.with(getActivity())
                    .load(photoUrl).transform(new PictureTransformer())
                    .error(R.drawable.ic_userphoto_menu)
                    .into(mImgUserPhoto);
        }

        if (String.valueOf(mUserPrefs.getId_user()).equals(mUserId)) {
            mLytSocial.setVisibility(View.VISIBLE);
            mBtnFollow.setVisibility(View.GONE);
            if (mUserProfile.getId_twitter().length() > 0) {
                mImgTwitter.setBackgroundResource(R.drawable.ic_share_twitter_white);
            }
            if (mUserProfile.getId_facebook().length() > 0) {
                mImgFacebook.setBackgroundResource(R.drawable.ic_share_facebook_white);
            }
        } else {
            mLytSocial.setVisibility(View.GONE);
            mBtnFollow.setVisibility(View.VISIBLE);

            if (mUserProfile.isBlocked()) {
                mBtnFollow.setBackgroundResource(R.drawable.btn_unbloked);
                mBtnFollow.setText(R.string.unblock);

            } else {
                mBtnFollow.setBackgroundResource(mUserProfile.isFollow() ?
                        R.drawable.bt_following_people_background :
                        R.drawable.bt_followed_people_background);

                mBtnFollow.setText(mUserProfile.isFollow() ? getText(R.string.following) : getText(R.string.follow));
            }
        }

        mTxtUsername.setText(mUserProfile.getFirst_name() + " " + mUserProfile.getLast_name());
        mTxtUserCity.setText(mUserProfile.getCity().toUpperCase() + ", " + mUserProfile.getCountry());
        mImgTwitter.setEnabled(!mUserProfile.getId_twitter().isEmpty());
        mImgFacebook.setEnabled(!mUserProfile.getId_facebook().isEmpty());
        mBtnFollowers.setText(String.valueOf(mUserProfile.getFollowers()) + "\n" + getString(R.string.followers));
        mBtnFollowing.setText(String.valueOf(mUserProfile.getFollowing()) + "\n" + getString(R.string.following));
        mBtnEvents.setText(String.valueOf(mUserProfile.getEvents()) + "\n" + getString(R.string.events));
        mTxtBio.setText(mUserProfile.getBio());

        CustomViewHelper.setUpActionBar(getActivity(), "@" + mUserProfile.getUsername());

        if (ManagePreferences.isAutocheckin()) {
            gpsLocation.startGPS();
        } else {
            gpsLocation.removeGPS();
        }

        mProgressHive.dismiss();
    }

    private class ReportUserListener implements IOptionSelected {

        private String flagMessage;

        @Override
        public void onTypeSelected(int index) {
            switch (index) {
                case 0:
                    // Report User
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage(R.string.why_report_user);

                    // Set an EditText view to get user input
                    final EditText input = new EditText(getActivity());
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            flagMessage = input.getText().toString();
                            if (mUserBL == null) {
                                mUserBL = new UsersBL();
                                mUserBL.setHiveListener(new IHiveResponse() {
                                    @Override
                                    public void onError(RetrofitError error) {
                                        mProgressHive.dismiss();
                                    }

                                    @Override
                                    public void onResult(HiveResponse response) {
                                        mProgressHive.dismiss();
                                    }
                                });
                            }
                            mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                            mUserBL.reportUser(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), String.valueOf(mUserProfile.getId_user()), flagMessage);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                    break;

                case 1:
                    // Block And Unfollow
                    AlertDialog.Builder alertBlock = new AlertDialog.Builder(getActivity());
                    alertBlock.setMessage(R.string.block_user);

                    alertBlock.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            UsersBL usersBL = new UsersBL();
                            usersBL.setHiveListener(new IHiveResponse() {
                                @Override
                                public void onError(RetrofitError error) {
                                    mProgressHive.dismiss();
                                }

                                @Override
                                public void onResult(HiveResponse response) {
                                    mProgressHive.dismiss();
                                }
                            });
                            mBtnFollow.setBackgroundResource(R.drawable.btn_unbloked);
                            mBtnFollow.setText(R.string.unblock);
                            mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                            usersBL.blockUser(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), String.valueOf(mUserProfile.getId_user()));
                            usersBL.followUser(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), String.valueOf(mUserProfile.getId_user()), "0");

                            mUserProfile.setFollow(false);
                            mUserProfile.setBlocked(true);
                        }
                    });

                    alertBlock.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

                    alertBlock.show();
                    break;
            }
        }
    }
}
