package uk.co.wehive.hive.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.NetworkMemberAdapter;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Artist;
import uk.co.wehive.hive.entities.response.ArtistsResponse;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class SocialNetworkFragment extends GATrackerFragment implements IHiveResponse,
        AdapterView.OnItemClickListener {

    private ProgressHive mProgressHive;
    private ArrayList<Follower> mUserNetworkList;
    private ArrayList<Artist> mArtistList;
    private EditText mEdtSearch;
    private UsersBL mUserBL;
    private UsersBL mArtistBL;
    private UsersBL mUserNetwork;
    private NetworkMemberAdapter mListAdapter;
    private FragmentManager mFragmentManager;
    private ListView mLvtNetworkMembers;
    private String mCurrentUserId;
    private String mCurrentNetwork;
    private User mUserPrefs;
    private boolean isSearch = false;
    private boolean isArtist = false;
    private boolean mItemSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.social_network_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setInitialData();
        initViewComponents();
    }

    private void setInitialData() {
        mFragmentManager = getActivity().getSupportFragmentManager();

        mProgressHive = new ProgressHive();
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        mUserPrefs = ManagePreferences.getUserPreferences();

        String userId = getArguments().getString(AppConstants.USER_ID);
        if (userId != null && userId.length() > 0) {
            mCurrentUserId = userId;
        } else {
            mCurrentUserId = String.valueOf(mUserPrefs.getId_user());
        }

        if (mItemSelected) {
            if (getString(R.string.followers).equals(mCurrentNetwork)) {
                refreshFollowers();
            } else if (getString(R.string.following).equals(mCurrentNetwork)) {
                refreshFollowing();
            }
        } else if (getArguments().getString(AppConstants.USER_NETWORK).equals(getString(R.string.followers))) {
            CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.followers));
            mCurrentNetwork = getString(R.string.followers);
            mUserNetworkList = getArguments().getParcelableArrayList(AppConstants.FOLLOWERS_LIST);
        } else if (getArguments().getString(AppConstants.USER_NETWORK).equals(getString(R.string.following))) {
            CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.following));
            mCurrentNetwork = getString(R.string.following);
            mUserNetworkList = getArguments().getParcelableArrayList(AppConstants.FOLLOWED_LIST);
        } else if (getArguments().getString(AppConstants.USER_NETWORK).equals(getString(R.string.search_for_users_title))) {
            CustomViewHelper.setUpActionBar(getActivity(), getArguments().getString(AppConstants.USER_NETWORK));
            mUserNetworkList = new ArrayList<Follower>();
            isSearch = true;
        } else if (getArguments().getString(AppConstants.USER_NETWORK).equals(getString(R.string.track_artists_title))) {
            CustomViewHelper.setUpActionBar(getActivity(), getArguments().getString(AppConstants.USER_NETWORK));
            mUserNetworkList = new ArrayList<Follower>();
            isSearch = true;
            isArtist = true;
        } else {
            CustomViewHelper.setUpActionBar(getActivity(), getArguments().getString(AppConstants.USER_NETWORK));
            mUserNetworkList = getArguments().getParcelableArrayList(AppConstants.FOLLOW_LIST);
        }

        mLvtNetworkMembers = (ListView) getView().findViewById(R.id.lvt_social_network);
        mLvtNetworkMembers.setOnItemClickListener(this);

        if (mUserNetworkList == null) {
            mUserNetworkList = new ArrayList<Follower>();
        }

        mListAdapter = new NetworkMemberAdapter(getActivity(), mUserNetworkList);
        mLvtNetworkMembers.setAdapter(mListAdapter);

        mUserBL = new UsersBL();
        mUserBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
            }

            @Override
            public void onResult(HiveResponse response) {
                mUserNetworkList = ((UserNetworkResponse) response).getData();
                mListAdapter.updateData(mUserNetworkList);
                mProgressHive.dismiss();
            }
        });

        mArtistBL = new UsersBL();
        mArtistBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
            }

            @Override
            public void onResult(HiveResponse response) {
                mArtistList = ((ArtistsResponse) response).getData();
                artistToFollower();
                mListAdapter.updateData(mUserNetworkList);
                mProgressHive.dismiss();
            }
        });

        mProgressHive.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mItemSelected = true;
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.USER_ID, String.valueOf(mUserNetworkList.get(position).getId_user()));
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
    }

    private void initViewComponents() {
        mEdtSearch = (EditText) getView().findViewById(R.id.edt_search_user);
        if (isSearch) {
            mEdtSearch.setVisibility(View.VISIBLE);
        } else {
            mEdtSearch.setVisibility(View.GONE);
        }

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == KeyEvent.KEYCODE_ENTER) {
                    if (mEdtSearch.getText().length() > 0) {
                        performSearch(mEdtSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        if (isArtist) {
            mProgressHive.show(getActivity().getSupportFragmentManager(), "");
            mArtistBL.searchArtists(mUserPrefs.getToken(), mCurrentUserId, "0", "20", "");
        }
    }

    private void performSearch(String search) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
        mProgressHive.show(getActivity().getSupportFragmentManager(), "");
        if (isArtist) {
            mArtistBL.searchArtists(mUserPrefs.getToken(), mCurrentUserId, "0", "20", search);
        } else {
            mUserBL.searchUsers(mUserPrefs.getToken(), mCurrentUserId, "0", "10", search);
        }
    }

    private void artistToFollower() {
        mUserNetworkList = new ArrayList<Follower>();
        Follower follower;
        for (Artist artist : mArtistList) {
            follower = new Follower();
            follower.setId_user(artist.getId_user());
            follower.setFirst_name(artist.getFirst_name());
            follower.setLast_name(artist.getLast_name());
            follower.setUsername(artist.getUsername());
            follower.setReal_photo(artist.getReal_photo());
            follower.setPremium((artist.getPremium()) ? "1" : "0");
            follower.setFollow(artist.getFollow());
            follower.setIs_blocked(artist.isIs_blocked());
            mUserNetworkList.add(follower);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        mItemSelected = false;
        mUserNetworkList = ((UserNetworkResponse) response).getData();
        mListAdapter = new NetworkMemberAdapter(getActivity(), mUserNetworkList);
        mListAdapter.notifyDataSetChanged();
        mLvtNetworkMembers.setAdapter(mListAdapter);
    }

    private void refreshFollowing() {
        mUserNetwork = new UsersBL();
        mUserNetwork.setHiveListener(this);
        mUserNetwork.getFollowing(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), mCurrentUserId, "0", "50");
    }

    private void refreshFollowers() {
        mUserNetwork = new UsersBL();
        mUserNetwork.setHiveListener(this);
        mUserNetwork.getFollowers(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), mCurrentUserId, "0", "50");
    }
}
