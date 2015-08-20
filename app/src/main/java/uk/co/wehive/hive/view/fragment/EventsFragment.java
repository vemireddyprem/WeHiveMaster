/*******************************************************************************
 PROJECT:       Hive
 FILE:          EventsFragment.java
 DESCRIPTION:   Events view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        16/07/2014  Marcela Güiza    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.EventsUserAdapter;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.EventsUserResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomFontTextView;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.LocationHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class EventsFragment extends GATrackerFragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, PullToRefreshListView.OnRefreshListener {

    @InjectView(R.id.plv_nearby_events)
    PullToRefreshListView mPtvNearbyEvents;
    @InjectView(R.id.plv_trending_events)
    PullToRefreshListView mPtvTrendingEvents;
    @InjectView(R.id.plv_recommended_events)
    PullToRefreshListView mPtvRecommendedEvents;
    @InjectView(R.id.txt_pending_posts)
    TextView mTxtPendingPosts;
    @InjectView(R.id.tbt_nearby)
    ToggleButton mTbtNearby;
    @InjectView(R.id.tbt_trending)
    ToggleButton mTbtTrending;
    @InjectView(R.id.tbt_recommended)
    ToggleButton mTbtRecommended;
    @InjectView(R.id.lbl_not_events)
    CustomFontTextView mTxtNotEvents;

    private static final String PAGE_SIZE = "10";
    private static final String NEARBY_TAB = "nearby";
    private static final String TRENDING_TAB = "trending";
    private static final String RECOMMENDED_TAB = "recommended";
    private final String TAG = "EventsFragment";

    private User mUser;
    private EventsUserAdapter mNearByAdapter;
    private EventsUserAdapter mTrendingAdapter;
    private EventsUserAdapter mRecommendedAdapter;
    private ProgressHive mProgresiveHive;
    private LocationHelper mLocationHelper;
    private FragmentManager mFragmentManager;
    private ArrayList<EventsUser> mListNearbyEvents;
    private ArrayList<EventsUser> mListTrendingEvents;
    private ArrayList<EventsUser> mListRecommendedEvents;
    private String mLastTabSelected;
    private int mLastItemSelected;
    private int mStartingNearbyItem;
    private int mStartingTrendingItem;
    private int mStartingRecommendedItem;
    private boolean mLoadingNearby;
    private boolean mLoadingTrending;
    private boolean mLoadingRecommended;
    private boolean mIsExploreMode;
    private boolean mItemListSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = ManagePreferences.getUserPreferences();
        mProgresiveHive = new ProgressHive();
        mProgresiveHive.setCancelable(false);
        mProgresiveHive.show(getFragmentManager(), "");
        mFragmentManager = getActivity().getSupportFragmentManager();

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(AppConstants.EXPLORE_MODE)) {
            mIsExploreMode = arguments.getBoolean(AppConstants.EXPLORE_MODE);
        }

        // Get initial location
        mLocationHelper = LocationHelper.getInstance(getActivity());
        mLocationHelper.requestLocation(new LocationHelper.LocationResult() {
            @Override
            public void gotLocation(Location location) {
            }
        }, 6000);

        if (mItemListSelected) {
            mProgresiveHive.dismiss();
            mItemListSelected = false;
            setPreviousState();
        } else {
            mListNearbyEvents = new ArrayList<EventsUser>();
            mListTrendingEvents = new ArrayList<EventsUser>();
            mListRecommendedEvents = new ArrayList<EventsUser>();

            initViewComponents();
            activeNearbyTab();

            getNearbyEvents();
            getTrendingEvents();
            getRecommendedEvents();
        }

        setHasOptionsMenu(true);
    }

    private void setPreviousState() {
        if (NEARBY_TAB.equals(mLastTabSelected)) {
            activeNearbyTab();
            mPtvNearbyEvents.setSelection(mLastItemSelected);
        } else if (TRENDING_TAB.equals(mLastTabSelected)) {
            activeTrendingTab();
            mPtvTrendingEvents.setSelection(mLastItemSelected);
        } else if (RECOMMENDED_TAB.equals(mLastTabSelected)) {
            activeRecommendedTab();
            mPtvRecommendedEvents.setSelection(mLastItemSelected);
        }
        mLastTabSelected = "";
        initViewComponents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.events_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearchView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tbt_nearby:
                activeNearbyTab();
                mStartingNearbyItem = 0;
                getNearbyEvents();
                break;

            case R.id.tbt_trending:
                activeTrendingTab();
                mTxtNotEvents.setVisibility((mTrendingAdapter.isEmpty()) ? View.VISIBLE : View.GONE);
                break;

            case R.id.tbt_recommended:
                activeRecommendedTab();
                mTxtNotEvents.setVisibility((mRecommendedAdapter.isEmpty()) ? View.VISIBLE : View.GONE);
                break;

            case R.id.txt_pending_posts:
                PendingPostsFragment pendingPostsFragment = new PendingPostsFragment();
                mFragmentManager.beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.content_frame, pendingPostsFragment)
                        .commit();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mItemListSelected = true;
        mLastItemSelected = position;

        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.EXPLORE_MODE, mIsExploreMode);
        if (mTbtNearby.isChecked()) {
            mLastTabSelected = NEARBY_TAB;
            bundle.putParcelable(AppConstants.EVENT_DATA, mListNearbyEvents.get(position));
        } else if (mTbtTrending.isChecked()) {
            mLastTabSelected = TRENDING_TAB;
            bundle.putParcelable(AppConstants.EVENT_DATA, mListTrendingEvents.get(position));
        } else if (mTbtRecommended.isChecked()) {
            mLastTabSelected = RECOMMENDED_TAB;
            bundle.putParcelable(AppConstants.EVENT_DATA, mListRecommendedEvents.get(position));
        }

        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        eventDetailFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, eventDetailFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.events));
    }

    public void onRefresh() {
        if (mTbtNearby.isChecked()) {
            mStartingNearbyItem = 0;
            getNearbyEvents();
        } else if (mTbtTrending.isChecked()) {
            mStartingTrendingItem = 0;
            getTrendingEvents();
        } else if (mTbtRecommended.isChecked()) {
            mStartingRecommendedItem = 0;
            getRecommendedEvents();
        }
    }

    // Go to search view
    private void openSearchView() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.EXPLORE_MODE, mIsExploreMode);
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, searchFragment).commit();
    }

    // Inicializate view components
    private void initViewComponents() {
        setPendingPostsNotification();

        mStartingNearbyItem = 0;
        mStartingTrendingItem = 0;
        mStartingRecommendedItem = 0;

        cleanRefreshingLabels();

        mTbtNearby.setOnClickListener(this);
        mTbtTrending.setOnClickListener(this);
        mTbtRecommended.setOnClickListener(this);

        mNearByAdapter = new EventsUserAdapter(getActivity(), mListNearbyEvents);
        mTrendingAdapter = new EventsUserAdapter(getActivity(), mListTrendingEvents);
        mRecommendedAdapter = new EventsUserAdapter(getActivity(), mListRecommendedEvents);

        setPullToRefreshListeners();
    }

    private void getNearbyEvents() {

        mLocationHelper = LocationHelper.getInstance(getActivity());
        Location location = mLocationHelper.getLastKnownLocation();

        EventsBL nearByEvents = new EventsBL();
        nearByEvents.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mLoadingNearby = false;
                mProgresiveHive.dismiss();
                mPtvNearbyEvents.onRefreshComplete();
                mPtvNearbyEvents.setEmptyView(mTxtNotEvents);
                Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(HiveResponse response) {
                ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
                setNearbyEventsData(((EventsUserResponse) response));
            }
        });
        if (location != null) {
            nearByEvents.searchNearYou(String.valueOf(mUser.getId_user()),
                    mUser.getCity_code(), String.valueOf(mStartingNearbyItem),
                    String.valueOf(PAGE_SIZE), String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()));
        } else {
            nearByEvents.searchNearYou(String.valueOf(mUser.getId_user()),
                    mUser.getCity_code(), String.valueOf(mStartingNearbyItem),
                    String.valueOf(PAGE_SIZE), "0", "0");
        }
    }

    private void setNearbyEventsData(EventsUserResponse response) {
        ArrayList<EventsUser> nearbyEvents = response.getData();
        boolean alreadyIncluded = eventAlreadyIncluded(mListNearbyEvents, nearbyEvents);
        try {

            if (mLoadingNearby && !alreadyIncluded) {
                mListNearbyEvents.addAll(nearbyEvents);
                mNearByAdapter.updateList(mListNearbyEvents);
                mLoadingNearby = false;
            } else {
                mListNearbyEvents.clear();
                mListNearbyEvents.addAll(nearbyEvents);
                mPtvNearbyEvents.onRefreshComplete();
            }

            mNearByAdapter.notifyDataSetChanged();
            mTxtNotEvents.setVisibility((mListNearbyEvents.isEmpty() || mListNearbyEvents.size() == 0) ? View.VISIBLE : View.GONE);
            mStartingNearbyItem = mStartingNearbyItem + nearbyEvents.size();
            mProgresiveHive.dismiss();
        } catch (Exception e) {
            Log.d(TAG, "setNearbyEventsData " + e.getMessage());
        }
    }

    private void getTrendingEvents() {

        EventsBL trendingEvents = new EventsBL();
        trendingEvents.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mLoadingTrending = false;
                mPtvTrendingEvents.onRefreshComplete();
                mPtvTrendingEvents.setEmptyView(mTxtNotEvents);
                if (mTbtRecommended.isChecked()) {
                    mProgresiveHive.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResult(HiveResponse response) {
                ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
                try {
                    setTrendingEventsData((EventsUserResponse) response);
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });
        trendingEvents.getTrending(mUser.getToken(), String.valueOf(mUser.getId_user()),
                String.valueOf(mStartingTrendingItem),
                String.valueOf(PAGE_SIZE));
    }

    private void setTrendingEventsData(EventsUserResponse response) {
        ArrayList<EventsUser> trendingEvents = response.getData();
        boolean alreadyIncluded = eventAlreadyIncluded(mListTrendingEvents, trendingEvents);

        if (mLoadingTrending && !alreadyIncluded) {
            mListTrendingEvents.addAll(trendingEvents);
            mTrendingAdapter.updateList(mListTrendingEvents);
            mLoadingTrending = false;
        } else {
            mListTrendingEvents.clear();
            mListTrendingEvents.addAll(trendingEvents);
            mTrendingAdapter.updateList(mListTrendingEvents);
            mPtvTrendingEvents.onRefreshComplete();
        }

        mTrendingAdapter.notifyDataSetChanged();
        mTxtNotEvents.setVisibility((mTrendingAdapter.isEmpty()) ? View.VISIBLE : View.GONE);
        mStartingTrendingItem = mStartingTrendingItem + trendingEvents.size();
        mProgresiveHive.dismiss();
    }

    private void getRecommendedEvents() {

        EventsBL recommendedEvents = new EventsBL();
        recommendedEvents.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mLoadingRecommended = false;
                mPtvRecommendedEvents.onRefreshComplete();
                mPtvRecommendedEvents.setEmptyView(mTxtNotEvents);
                if (mTbtRecommended.isChecked()) {
                    mProgresiveHive.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResult(HiveResponse response) {
                ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());
                try {
                    setRecommendedEventsData((EventsUserResponse) response);
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });
        recommendedEvents.searchRecommended(String.valueOf(mStartingRecommendedItem),
                String.valueOf(PAGE_SIZE), String.valueOf(mUser.getId_user()));
    }

    private void setRecommendedEventsData(EventsUserResponse response) {
        ArrayList<EventsUser> recommendedEvents = response.getData();
        boolean alreadyIncluded = eventAlreadyIncluded(mListRecommendedEvents, recommendedEvents);

        if (mLoadingRecommended && !alreadyIncluded) {
            mListRecommendedEvents.addAll(recommendedEvents);
            mRecommendedAdapter.updateList(mListRecommendedEvents);
            mLoadingRecommended = false;
        } else {
            mListRecommendedEvents.clear();
            mListRecommendedEvents.addAll(recommendedEvents);
            mRecommendedAdapter.updateList(mListRecommendedEvents);
            mPtvRecommendedEvents.onRefreshComplete();
        }

        mRecommendedAdapter.notifyDataSetChanged();
        mTxtNotEvents.setVisibility((mRecommendedAdapter.isEmpty()) ? View.VISIBLE : View.GONE);
        mStartingRecommendedItem = mStartingRecommendedItem + recommendedEvents.size();
        mProgresiveHive.dismiss();
    }

    private boolean eventAlreadyIncluded(ArrayList<EventsUser> events, ArrayList<EventsUser> newEvents) {
        if (newEvents != null && newEvents.size() > 0) {
            EventsUser eventsUser = newEvents.get(0);

            for (EventsUser listEvent : events) {
                if (listEvent.getId_event() == eventsUser.getId_event()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cleanRefreshingLabels() {

        mPtvNearbyEvents.setTextPullToRefresh("");
        mPtvNearbyEvents.setTextReleaseToRefresh("");
        mPtvNearbyEvents.setTextRefreshing("");

        mPtvTrendingEvents.setTextPullToRefresh("");
        mPtvTrendingEvents.setTextReleaseToRefresh("");
        mPtvTrendingEvents.setTextRefreshing("");

        mPtvRecommendedEvents.setTextPullToRefresh("");
        mPtvRecommendedEvents.setTextReleaseToRefresh("");
        mPtvRecommendedEvents.setTextRefreshing("");
    }

    private void setPullToRefreshListeners() {

        mPtvNearbyEvents.setAdapter(mNearByAdapter);
        mPtvNearbyEvents.setOnItemClickListener(this);
        mPtvNearbyEvents.setOnRefreshListener(this);
        mPtvNearbyEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean lastItem = (firstVisibleItem + visibleItemCount == totalItemCount);
                boolean nearbyCharged = mListNearbyEvents != null && mListNearbyEvents.size() > 0 && mListNearbyEvents.size() < 10;

                if (lastItem && totalItemCount != 0 && mTbtNearby.isChecked() && !mLoadingNearby && !nearbyCharged) {
                    mLoadingNearby = true;
                    getNearbyEvents();
                }
            }
        });

        mPtvTrendingEvents.setAdapter(mTrendingAdapter);
        mPtvTrendingEvents.setOnItemClickListener(this);
        mPtvTrendingEvents.setOnRefreshListener(this);
        mPtvTrendingEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean lastItem = (firstVisibleItem + visibleItemCount == totalItemCount);
                boolean trendingCharged = mListTrendingEvents != null && mListTrendingEvents.size() > 0 && mListTrendingEvents.size() < 10;

                if (lastItem && totalItemCount != 0 && mTbtTrending.isChecked() && !mLoadingTrending && !trendingCharged) {
                    mLoadingTrending = true;
                    getTrendingEvents();
                }
            }
        });

        mPtvRecommendedEvents.setAdapter(mRecommendedAdapter);
        mPtvRecommendedEvents.setOnItemClickListener(this);
        mPtvRecommendedEvents.setOnRefreshListener(this);
        mPtvRecommendedEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean lastItem = (firstVisibleItem + visibleItemCount == totalItemCount);
                boolean recommendedCharged = mListRecommendedEvents != null && mListRecommendedEvents.size() > 0 && mListRecommendedEvents.size() < 10;

                if (lastItem && totalItemCount != 0 && mTbtRecommended.isChecked() && !mLoadingRecommended && !recommendedCharged) {
                    mLoadingRecommended = true;
                    getRecommendedEvents();
                }
            }
        });
    }

    private void setPendingPostsNotification() {
        EventPostsResponse readResponse =
                PersistenceHelper.readResponse(getActivity(), AppConstants.PENDING_POSTS + "_" +
                        String.valueOf(mUser.getId_user()), EventPostsResponse.class);

        if (readResponse != null) {
            mTxtPendingPosts.setOnClickListener(this);
            mTxtPendingPosts.setVisibility(View.VISIBLE);

            int pendingPosts = readResponse.getData().size();
            String message = null;
            if (pendingPosts == 1) {
                message = pendingPosts + " " + getString(R.string.pending_post);
            } else if (pendingPosts > 1) {
                message = pendingPosts + " " + getString(R.string.pending_posts);
            } else {
                mTxtPendingPosts.setVisibility(View.GONE);
            }
            mTxtPendingPosts.setText(message);
        }
    }

    private void activeRecommendedTab() {
        mTbtNearby.setChecked(false);
        mTbtTrending.setChecked(false);
        mTbtRecommended.setChecked(true);

        mPtvNearbyEvents.setVisibility(View.GONE);
        mPtvTrendingEvents.setVisibility(View.GONE);
        mPtvRecommendedEvents.setVisibility(View.VISIBLE);
    }

    private void activeTrendingTab() {
        mTbtNearby.setChecked(false);
        mTbtTrending.setChecked(true);
        mTbtRecommended.setChecked(false);

        mPtvNearbyEvents.setVisibility(View.GONE);
        mPtvTrendingEvents.setVisibility(View.VISIBLE);
        mPtvRecommendedEvents.setVisibility(View.GONE);
    }

    private void activeNearbyTab() {
        mTbtNearby.setChecked(true);
        mTbtTrending.setChecked(false);
        mTbtRecommended.setChecked(false);

        mPtvNearbyEvents.setVisibility(View.VISIBLE);
        mPtvTrendingEvents.setVisibility(View.GONE);
        mPtvRecommendedEvents.setVisibility(View.GONE);
    }
}