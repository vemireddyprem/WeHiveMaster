/*******************************************************************************
 PROJECT:       Hive
 FILE:          NewsFeedFragment.java
 DESCRIPTION:   NewsFeed view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        16/07/2014  Marcela Güiza    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.NewsfeedAdapter;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.entities.NewsFeed;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.NewsFeedResponse;
import uk.co.wehive.hive.listeners.INewsFeedEventsListener;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.NewsfeedViewHelper;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class NewsFeedFragment extends GATrackerFragment implements IHiveResponse,
        AdapterView.OnItemClickListener, INewsFeedEventsListener,
        PullToRefreshListView.OnRefreshListener, AbsListView.OnScrollListener {

    @InjectView(R.id.plv_newsfeed) PullToRefreshListView mPlvNewsfeed;

    private final static int PAGE_SIZE = 10;

    private ProgressHive mProgressBar;
    private FragmentManager mFragmentManager;
    private List<NewsFeed> mNewsFeedList;
    private PostBL mPostBL;
    private UsersBL mUsersBL;
    private User mUser;
    private String mOptionService;
    private NewsfeedAdapter mNewsFeedAdapter;
    private int mStartingItem;
    private int mLastItemSelected;
    private boolean mIsLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newsfeed_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewComponents();
        NewsfeedViewHelper newsfeedViewHelper = new NewsfeedViewHelper(getActivity());
        newsfeedViewHelper.setListener(this);

        mFragmentManager = getActivity().getSupportFragmentManager();

        mUser = ManagePreferences.getUserPreferences();
        mUsersBL = new UsersBL();
        mUsersBL.setHiveListener(this);

        mPostBL = new PostBL();
        mPostBL.setListener(this);

        boolean isNewFragment = ManagePreferences.isANewFragment();

        if (mNewsFeedList != null && mNewsFeedList.size() > 0 && !isNewFragment) {
            mPlvNewsfeed.setAdapter(mNewsFeedAdapter);
            mPlvNewsfeed.setSelection(mLastItemSelected);
            mProgressBar.dismiss();
        } else {
            mStartingItem = 0;
            mNewsFeedList = new ArrayList<NewsFeed>();
            mNewsFeedAdapter = new NewsfeedAdapter(getActivity(), mNewsFeedList);

            mUsersBL.newsFeed(mUser.getToken(),String.valueOf(mUser.getId_user()), String.valueOf(mStartingItem), String.valueOf(PAGE_SIZE));
        }
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
        Toast.makeText(getActivity(), getString(R.string.toast_news_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
        mProgressBar.dismiss();
        if (!AppConstants.LIKE_POST_SERVICE.equals(mOptionService)) {
            ArrayList<NewsFeed> newsFeeds = ((NewsFeedResponse) response).getData();

            boolean alreadyIncluded = newsFeedAlreadyIncluded(mNewsFeedList, newsFeeds);

            if (mIsLoading && !alreadyIncluded) {
                mNewsFeedList.addAll(newsFeeds);
                mNewsFeedAdapter.updateNewsFeedList(mNewsFeedList);
                mPlvNewsfeed.setAdapter(mNewsFeedAdapter);
                mPlvNewsfeed.setSelection(mStartingItem);
                mIsLoading = false;
            } else {
                mNewsFeedList.clear();
                mNewsFeedList.addAll(newsFeeds);
                mNewsFeedAdapter.updateNewsFeedList(mNewsFeedList);
                mPlvNewsfeed.setAdapter(mNewsFeedAdapter);
                mPlvNewsfeed.onRefreshComplete();
            }

            mStartingItem = mStartingItem + newsFeeds.size();
        } else {
            mOptionService = "";
        }
    }

    @Override
    public void setAction(NewsFeed newsFeed, String option, String active) {
        mOptionService = option;
        mPostBL.addLike(mUser.getToken(),String.valueOf(mUser.getId_user()), String.valueOf(newsFeed.getDetail().getId_post()), active);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ManagePreferences.setNewFragment(false);
        mLastItemSelected = position;
        NewsFeed newsfeed = mNewsFeedList.get(position);
        if (AppConstants.CHECK_IN_NEWSFEED.equals(newsfeed.getType_newsfeed())) {
            goEventDetail(newsfeed);
        } else if (AppConstants.POST_CREATED_NEWSFEED.equals(newsfeed.getType_newsfeed())) {
            goPostDetail(newsfeed);
        } else {
            goGoodTimeDetail(newsfeed);
        }
    }

    @Override
    public void onRefresh() {
        mStartingItem = 0;
        mUsersBL.newsFeed(mUser.getToken(),String.valueOf(mUser.getId_user()), String.valueOf(mStartingItem), String.valueOf(PAGE_SIZE));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean lastItem = (firstVisibleItem + visibleItemCount == totalItemCount);
        if (lastItem && totalItemCount != 0 && !mIsLoading) {
            mIsLoading = true;
            mUsersBL.newsFeed(mUser.getToken(),String.valueOf(mUser.getId_user()), String.valueOf(mStartingItem), String.valueOf(PAGE_SIZE));
        }
    }

    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), AppConstants.NEWSFEED);

        mProgressBar = new ProgressHive();
        mProgressBar.show(getActivity().getSupportFragmentManager(), "");
        mProgressBar.setCancelable(false);
        cleanRefreshingLabels();

        mPlvNewsfeed.setOnItemClickListener(this);
        mPlvNewsfeed.setOnRefreshListener(this);
        mPlvNewsfeed.setOnScrollListener(this);
    }

    private void goPostDetail(NewsFeed newsfeed) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.ID_POST, String.valueOf(newsfeed.getDetail().getId_post()));
        CommentGoodTimesDetailFragment fragment = new CommentGoodTimesDetailFragment();
        fragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, fragment).commit();
    }

    private void goGoodTimeDetail(NewsFeed newsfeed) {
        GoodTimesDetailFragment goodTimesDetailFragment = new GoodTimesDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.ID_EVENT, newsfeed.getDetail().getId_event());
        bundle.putString(AppConstants.USER_ID, String.valueOf(newsfeed.getId_user()));
        bundle.putString(AppConstants.USERNAME, newsfeed.getUsername());
        goodTimesDetailFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, goodTimesDetailFragment).commit();
    }

    private void goEventDetail(NewsFeed newsfeed) {
        EventsUser eventDetail = new EventsUser();
        eventDetail.setName(newsfeed.getFullname());
        eventDetail.setId_event(newsfeed.getDetail().getId_event());
        eventDetail.setAbsolute_photo(newsfeed.getDetail().getEvents_photo());
        eventDetail.setDate_event(newsfeed.getDetail().getEvent_date());
        eventDetail.setVenue(newsfeed.getDetail().getEvent_venue());

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.EVENT_DATA, eventDetail);
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        eventDetailFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, eventDetailFragment).commit();
    }

    private void cleanRefreshingLabels() {
        mPlvNewsfeed.setTextPullToRefresh("");
        mPlvNewsfeed.setTextReleaseToRefresh("");
        mPlvNewsfeed.setTextRefreshing("");
    }

    private boolean newsFeedAlreadyIncluded(List<NewsFeed> previousNewsFeed, ArrayList<NewsFeed> newNewsFeed) {
        if (newNewsFeed != null && newNewsFeed.size() > 0) {
            NewsFeed recentNewsFeed = newNewsFeed.get(0);

            for (NewsFeed feed : previousNewsFeed) {
                if (feed.getId_newsfeed() == recentNewsFeed.getId_newsfeed()) {
                    return true;
                }
            }
        }
        return false;
    }
}