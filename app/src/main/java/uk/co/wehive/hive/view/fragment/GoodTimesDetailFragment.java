package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.GoodTimesDetailAdapter;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.GoodTimes;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.GoodTimesDetailResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IGoodTimesOptions;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.DateOperations;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.swipe.ListViewSwipeGesture;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class GoodTimesDetailFragment extends GATrackerFragment implements IHiveResponse,
        IGoodTimesOptions {

    @InjectView(R.id.lvPost) PullToRefreshListView mLvGoodTimes;
    @InjectView(R.id.lblEvent) TextView mLblEvent;
    @InjectView(R.id.lblPlace) TextView mLblPlace;
    @InjectView(R.id.lblDate) TextView mLblDate;

    private static final int PAGE_SIZE = 10;

    private static enum options_load_data {load, like, share, comments, add}

    private UsersBL mUsersBL;
    private GoodTimes mGoodTimes;
    private String mGoodTimesIdUser;
    private String mUserId;
    private String mOption;
    private String mUserName;
    private GoodTimesDetailAdapter mGoodTimesDetailAdapter;
    private PostBL mPostBL;
    private ProgressHive mProgressHive;
    private FragmentManager mFragmentManager;
    private User mUserPrefs;
    private ArrayList<Post> mPosts;
    private GoodTimesDetailResponse goodTimesDetailResponse;
    private int mPage;
    private int mIdEvent;
    private int mLastItemSelected;
    private boolean mLoading;
    private boolean mItemListSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.good_times_detail_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initControls();
        loadGoodTimesDetail();
    }

    private void initControls() {
        mFragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = getArguments();
        mUserPrefs = ManagePreferences.getUserPreferences();
        mUsersBL = new UsersBL();
        mPostBL = new PostBL();
        mPostBL.setListener(this);
        mUsersBL.setHiveListener(this);

        mGoodTimesIdUser = bundle.getString(AppConstants.USER_ID);
        if (mGoodTimesIdUser == null || mGoodTimesIdUser.length() < 1) {
            mGoodTimesIdUser = String.valueOf(mUserPrefs.getId_user());
        } else {
            mUserName = bundle.getString(AppConstants.USERNAME);
        }
        mUserId = String.valueOf(mUserPrefs.getId_user());

        addEvents();

        mOption = options_load_data.load.toString();
        mPage = 0;
        mLoading = false;
    }

    private void addEvents() {
        mProgressHive = new ProgressHive();
        mProgressHive.setCancelable(false);

        mLvGoodTimes.setTextPullToRefresh("");
        mLvGoodTimes.setTextReleaseToRefresh("");
        mLvGoodTimes.setTextRefreshing("");

        final ListViewSwipeGesture everythingTouchListener =
                new ListViewSwipeGesture(mLvGoodTimes, swipeListener, this.getActivity(),
                        R.id.list_display_view_container, R.drawable.ic_flag_white, false);

        mLvGoodTimes.setOnTouchListener(everythingTouchListener);
        mLvGoodTimes.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOption = options_load_data.load.toString();
                mPage = 0;
                mUsersBL.getGoodTimeDetail(mUserPrefs.getToken(),String.valueOf(mIdEvent), mGoodTimesIdUser, mUserId, String.valueOf(mPage), String.valueOf(PAGE_SIZE));
            }
        });

        mLvGoodTimes.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mLoading) {
                    mOption = options_load_data.add.toString();
                    mPage = mPage + PAGE_SIZE;
                    mUsersBL.getGoodTimeDetail(mUserPrefs.getToken(),String.valueOf(mIdEvent), mGoodTimesIdUser, mUserId, String.valueOf(mPage), String.valueOf(PAGE_SIZE));
                    mLoading = true;
                }
            }
        });
    }

    private void loadGoodTimesDetail() {

        mProgressHive.show(mFragmentManager, "");

        Bundle bundle = getArguments();
        if (bundle.containsKey(AppConstants.GOODTIMES)) {
            mGoodTimes = bundle.getParcelable(AppConstants.GOODTIMES);
            mIdEvent = mGoodTimes.getId_event();
        } else if (bundle.containsKey(AppConstants.ID_EVENT)) {
            mIdEvent = bundle.getInt(AppConstants.ID_EVENT);
        }

        if (goodTimesDetailResponse == null) {
            mPosts = new ArrayList<Post>();
            mUsersBL.getGoodTimeDetail(mUserPrefs.getToken(),String.valueOf(mIdEvent), mGoodTimesIdUser, mUserId, String.valueOf(mPage), String.valueOf(PAGE_SIZE));
        } else {
            // Check if a item was selected and recover previous state or load all event details.
            if (mItemListSelected) {
                mItemListSelected = false;
                mProgressHive.dismiss();
                setTopViewData();
                mLvGoodTimes.setAdapter(mGoodTimesDetailAdapter);
                mLvGoodTimes.getItemAtPosition(mLastItemSelected);
            } else {
                loadGoodTimes(goodTimesDetailResponse);
            }
        }

        String nameTopView = (mUserName != null) ? "@" + mUserName : "@" + mUserPrefs.getUsername();
        CustomViewHelper.setUpActionBar(getActivity(), nameTopView);
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        Toast.makeText(getActivity(), getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        if (!mOption.equals(options_load_data.like.toString())) {
            loadGoodTimes(goodTimesDetailResponse = (GoodTimesDetailResponse) response);
        }
    }

    private void loadGoodTimes(GoodTimesDetailResponse response) {
        if (mOption.equals(options_load_data.load.toString())) {

            goodTimesDetailResponse = response;
            mPosts = goodTimesDetailResponse.getData().getPosts();
            setTopViewData();

            mGoodTimesDetailAdapter = new GoodTimesDetailAdapter(this, mPosts);
            mGoodTimesDetailAdapter.setListener(this);
            mLvGoodTimes.setAdapter(mGoodTimesDetailAdapter);
            mProgressHive.dismiss();
            mLvGoodTimes.onRefreshComplete();

        } else if (mOption.equals(options_load_data.add.toString())) {

            goodTimesDetailResponse = response;
            mLoading = false;
            if (goodTimesDetailResponse.getData().getPosts() != null && goodTimesDetailResponse.getData().getPosts().size() > 0) {
                mPosts.addAll(goodTimesDetailResponse.getData().getPosts());
                mGoodTimesDetailAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setAction(Post post, AppConstants.OPTIONS_GOOD_TIMES option, String active) {
        this.mOption = option.toString();
        if (mOption.equals(options_load_data.like.toString())) {
            mPostBL.addLike(mUserPrefs.getToken(),mGoodTimesIdUser, post.getId_post(), active);
        } else if (mOption.equals(options_load_data.comments.toString())) {
            CommentGoodTimesDetailFragment commentFragment = new CommentGoodTimesDetailFragment();
            changeFragment(commentFragment, post);
        } else if (mOption.equals(options_load_data.share.toString())) {
            ShareGoodTimesDetailFragment shareFragment = new ShareGoodTimesDetailFragment();
            changeFragment(shareFragment, post);
        }
    }

    private void changeFragment(Fragment fragment, Post post) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.POST, post);
        bundle.putParcelable(AppConstants.GOODTIMES, mGoodTimes);
        fragment.setArguments(bundle);
        mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("").commit();
    }

    private void setTopViewData() {
        mLblEvent.setText(goodTimesDetailResponse.getData().getName());
        mLblPlace.setText(goodTimesDetailResponse.getData().getPlace());
        for (Post mPost : mPosts) {
            mPost.setSwipe(!String.valueOf(mUserPrefs.getId_user()).equals(mPost.getId_user()));
        }

        mLblDate.setText(DateOperations.getDate(DateOperations
                .getDate(Long.parseLong(goodTimesDetailResponse.getData().getDate_event()) * 1000, "dd MMMM h:m:a")));
    }

    ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

        @Override
        public void HalfSwipeListView(int position) {
            Bundle bundle;
            bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, mPosts.get(position - 1).getId_post());
            FlagPostFragment flagPostFragment = new FlagPostFragment();
            flagPostFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, flagPostFragment).commit();
        }

        @Override
        public void LoadDataForScroll(int count) {
        }

        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        }

        @Override
        public void OnClickListView(int position) {
            mItemListSelected = true;
            mLastItemSelected = position - 1;
            if (position > 0) {
                Post post = mPosts.get(position - 1);
                CommentGoodTimesDetailFragment commentGoodTimesDetailFragment = new CommentGoodTimesDetailFragment();
                changeFragment(commentGoodTimesDetailFragment, post);
            }
        }
    };
}
