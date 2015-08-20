package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.NetworkMemberAdapter;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.PostLikersResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.activity.CarouselMenuActivity;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class PostLikersFragment extends Fragment implements IHiveResponse {

    @InjectView(R.id.ltv_post_likers)
    PullToRefreshListView mLvLikers;
    @InjectView(R.id.txt_empty_list)
    CustomFontTextView mTxtEmptyList;

    private NetworkMemberAdapter mListAdapter;
    private ArrayList<Follower> mLikersList;
    private ProgressHive mProgressBar;
    private User mUserPrefs;
    private final String TAG = "PostLikersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_likers_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserPrefs = ManagePreferences.getUserPreferences();
        String postId = getArguments().getString(AppConstants.ID_POST);

        initViewComponents();

        PostBL postBL = new PostBL();
        postBL.setListener(this);
        postBL.postsLikers(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), postId, "0", "20");
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressBar.dismiss();
        try {
            ArrayList<Follower> data = ((PostLikersResponse) response).getData();
            mTxtEmptyList.setVisibility(data != null && data.size() > 0 ? View.GONE : View.VISIBLE);
            mLikersList.addAll(data);
            mListAdapter.notifyDataSetChanged();
            mLvLikers.setAdapter(mListAdapter);
        } catch (Exception e) {
            Log.d(TAG, "error " + e.getMessage());
        }
    }

    private void initViewComponents() {
        cleanRefreshingLabels();
        mLikersList = new ArrayList<Follower>();
        mListAdapter = new NetworkMemberAdapter(getActivity(), mLikersList);

        mProgressBar = new ProgressHive();
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");
    }

    private void cleanRefreshingLabels() {
        mLvLikers.setTextPullToRefresh("");
        mLvLikers.setTextReleaseToRefresh("");
        mLvLikers.setTextRefreshing("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            CarouselMenuActivity.mCarouselMenuActivity.finish();
        } catch (Exception ignored) {
        }
    }
}