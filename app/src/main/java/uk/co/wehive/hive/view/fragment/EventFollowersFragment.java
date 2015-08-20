package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.NetworkMemberAdapter;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class EventFollowersFragment extends GATrackerFragment implements IHiveResponse,
        AdapterView.OnItemClickListener {

    private final String TAG = "EventFollowersFragment";
    private ProgressHive mProgressHive;
    private ArrayList<Follower> mUserNetworkList;
    private NetworkMemberAdapter mListAdapter;
    private ListView lvtNetworkMembers;
    private FragmentManager mFragmentManager;
    private String mEventId;
    private User userPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.social_network_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentManager = getActivity().getSupportFragmentManager();

        mProgressHive = new ProgressHive();
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        userPreference = ManagePreferences.getUserPreferences();

        if (getArguments().getString(AppConstants.ID_EVENT) != null && getArguments().getString
                (AppConstants.ID_EVENT).length() > 0) {
            mEventId = getArguments().getString(AppConstants.ID_EVENT);
        }

        EventsBL eventFollowersBL = new EventsBL();
        eventFollowersBL.setEventsUserListener(this);
        eventFollowersBL.getFollowers(userPreference.getToken(), String.valueOf(userPreference.getId_user()), mEventId, "0", "20");

        initViewComponents();
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.follow_tracking));

        lvtNetworkMembers = (ListView) getView().findViewById(R.id.lvt_social_network);
        lvtNetworkMembers.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.USER_ID, String.valueOf(mUserNetworkList.get(position).getId_user()));
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        try {
            mUserNetworkList = ((UserNetworkResponse) response).getData();
            mListAdapter = new NetworkMemberAdapter(getActivity(), mUserNetworkList);
            lvtNetworkMembers.setAdapter(mListAdapter);
        } catch (Exception e) {
            Log.d(TAG, "error " + e.getMessage());
        }
    }
}
