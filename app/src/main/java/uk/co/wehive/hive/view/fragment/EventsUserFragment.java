package uk.co.wehive.hive.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class EventsUserFragment extends GATrackerFragment implements View.OnClickListener {

    @InjectView(R.id.lvEvents)
    PullToRefreshListView mLvEvents;
    @InjectView(R.id.btnPast)
    Button mBtnPast;
    @InjectView(R.id.btnUpComing)
    Button mBtnUpComing;
    @InjectView(R.id.lblEmptyEvents)
    CustomFontTextView mLblEmptyEvents;
    @InjectView(R.id.txt_pending_posts)
    TextView mTxtPendingPosts;

    private final int mPageSize = 10;

    private EventsBL mUpComingBL;
    private EventsBL mPastBL;
    private User mUserPrefs;
    private ProgressHive mProgressBar;
    private FragmentManager mFragmentManager;
    private Resources mResources;
    private ArrayList<EventsUser> mEventList;
    private ArrayList<EventsUser> mUpComingList;
    private ArrayList<EventsUser> mPastList;
    private EventsUserAdapter mAdapter;
    private String mLastSelectedTab;
    private boolean mFirstLoad = true;
    private boolean mIsLoadingUpComing = false;
    private boolean mIsLoadingPast = false;
    private boolean mIsPastSelected = false;
    private int mLastSelectedItem;
    private final String TAG = "EventsUserFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_user_frament, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.tracked));
        mFragmentManager = getActivity().getSupportFragmentManager();
        mProgressBar = new ProgressHive();
        mProgressBar.setCancelable(false);
        mProgressBar.show(mFragmentManager, "");

        if (mEventList != null && mEventList.size() > 0) {
            if (getString(R.string.past).equals(mLastSelectedTab)) {
                changeBackground(true);
            } else if (getString(R.string.past).equals(mLastSelectedTab)) {
                changeBackground(false);
            }
            addEvents();
            mLvEvents.setSelection(mLastSelectedItem);
            mLvEvents.setAdapter(mAdapter);
            mProgressBar.dismiss();
        } else {
            initControls();
            loadData(false, mUpComingList);
        }

        setPendingPostsNotification();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_pending_posts:
                PendingPostsFragment pendingPostsFragment = new PendingPostsFragment();
                mFragmentManager.beginTransaction()
                        .addToBackStack("")
                        .replace(R.id.content_frame, pendingPostsFragment)
                        .commit();
                break;
        }
    }

    private void setEmptyList(boolean isEmpty) {
        if (isEmpty) {
            mLblEmptyEvents.setVisibility(View.VISIBLE);
            mLvEvents.setVisibility(View.GONE);
        } else {
            mLvEvents.setVisibility(View.VISIBLE);
            mLblEmptyEvents.setVisibility(View.GONE);
        }
    }

    private void initControls() {
        mResources = getResources();
        mUserPrefs = ManagePreferences.getUserPreferences();
        mUpComingBL = new EventsBL();
        mPastBL = new EventsBL();

        mEventList = new ArrayList<EventsUser>();
        mUpComingList = new ArrayList<EventsUser>();
        mPastList = new ArrayList<EventsUser>();
        mAdapter = (new EventsUserAdapter(getActivity(), mEventList));
        mLvEvents.setAdapter(mAdapter);

        addEvents();
    }

    private void loadData(boolean past, ArrayList<EventsUser> list) {

        if (past) {
            if (mPastList.size() > 0) {
                if (isAdded()) mLastSelectedTab = getString(R.string.past);
                changeBackground(true);
                setEmptyList(false);

                if (!itemAlreadyIncluded(mEventList, list)) {
                    mEventList.addAll(list);
                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.dismiss();
            } else {
                getPast();
            }
        } else {
            if (mUpComingList.size() > 0) {
                if (isAdded()) mLastSelectedTab = getString(R.string.upcoming);
                changeBackground(false);
                setEmptyList(false);

                if (!itemAlreadyIncluded(mEventList, list)) {
                    mEventList.addAll(list);
                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.dismiss();
            } else {
                getUpComing();
            }
        }
    }

    private void getUpComing() {
        if (!mIsLoadingUpComing) {
            mIsLoadingUpComing = true;
            mUpComingBL.getUpComingEvents(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                    String.valueOf(mUpComingList.size()), String.valueOf(mPageSize));
        }
    }

    private void getPast() {
        if (!mIsLoadingPast) {
            mIsLoadingPast = true;
            mPastBL.getPastEvents(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                    String.valueOf(mPastList.size()), String.valueOf(mPageSize));
        }
    }

    private void changeBackground(boolean past) {
        if (past) {
            mBtnPast.setTextColor(mResources.getColor(R.color.text_button_event_user));
            mBtnPast.setBackground(mResources.getDrawable(R.drawable.round_corners_options_left_selected_menu_events_user));
            mBtnUpComing.setTextColor(mResources.getColor(R.color.white));
            mBtnUpComing.setBackground(mResources.getDrawable(R.drawable.round_corners_options_right_unselected_menu_events_user));

        } else {
            mBtnPast.setTextColor(mResources.getColor(R.color.white));
            mBtnUpComing.setTextColor(mResources.getColor(R.color.text_button_event_user));
            mBtnPast.setBackground(mResources.getDrawable(R.drawable.round_corners_options_left_unselected_menu_events_user));
            mBtnUpComing.setBackground(mResources.getDrawable(R.drawable.round_corners_options_right_selected_menu_events_user));
        }
    }

    private void addEvents() {
        mUpComingBL.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressBar.dismiss();
                Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(HiveResponse response) {
                EventsUserResponse eventsUserResponse = (EventsUserResponse) response;
                try {
                    mUpComingList.addAll(eventsUserResponse.getData());
                    mIsLoadingUpComing = false;
                    cleanRefreshingLabels();
                    if (mFirstLoad && mUpComingList.size() == 0) {
                        mFirstLoad = false;
                        mIsPastSelected = true;
                        loadData(true, mPastList);
                    } else if (mUpComingList.size() == 0) {
                        setEmptyList(true);
                    } else {
                        setEmptyList(false);
                        loadData(false, eventsUserResponse.getData());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });

        mPastBL.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressBar.dismiss();
                Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(HiveResponse response) {
                EventsUserResponse eventsUserResponse = (EventsUserResponse) response;

                try {
                    mPastList.addAll(eventsUserResponse.getData());
                    mIsLoadingPast = false;
                    cleanRefreshingLabels();
                    if (mPastList.size() == 0) {
                        setEmptyList(true);
                    } else {
                        setEmptyList(false);
                        loadData(true, eventsUserResponse.getData());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });

        mBtnUpComing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackground(false);
                mIsPastSelected = false;
                mEventList.clear();
                mAdapter.notifyDataSetChanged();
                loadData(false, mUpComingList);
            }
        });
        mBtnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBackground(true);
                mIsPastSelected = true;
                mEventList.clear();
                mAdapter.notifyDataSetChanged();
                loadData(true, mPastList);
            }
        });
        mLvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mLastSelectedItem = position;
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EVENT_DATA, mEventList.get(position));
                EventDetailFragment eventDetailFragment = new EventDetailFragment();
                eventDetailFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, eventDetailFragment).commit();
            }
        });

        mLvEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mIsPastSelected) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsLoadingPast) {
                        getPast();
                    }
                } else {
                    if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsLoadingUpComing) {
                        getUpComing();
                    }
                }
            }
        });

        mLvEvents.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEventList.clear();
                if (mIsPastSelected) {
                    mPastList.clear();
                    loadData(true, mPastList);
                } else {
                    mUpComingList.clear();
                    loadData(false, mUpComingList);
                }
            }
        });
    }

    private void cleanRefreshingLabels() {
        mLvEvents.onRefreshComplete();
        mLvEvents.setTextPullToRefresh("");
        mLvEvents.setTextReleaseToRefresh("");
        mLvEvents.setTextRefreshing("");
    }

    private boolean itemAlreadyIncluded(List<EventsUser> previousList, ArrayList<EventsUser> newList) {
        if (newList != null && newList.size() > 0) {
            EventsUser recentItem = newList.get(0);

            for (EventsUser eventsUser : previousList) {
                if (eventsUser.getId_event() == recentItem.getId_event()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setPendingPostsNotification() {
        EventPostsResponse readResponse =
                PersistenceHelper.readResponse(getActivity(), AppConstants.PENDING_POSTS + "_" +
                        String.valueOf(mUserPrefs.getId_user()), EventPostsResponse.class);

        if (readResponse != null) {
            mTxtPendingPosts.setOnClickListener(this);
            mTxtPendingPosts.setVisibility(View.VISIBLE);

            try {
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
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }
        }
    }
}
