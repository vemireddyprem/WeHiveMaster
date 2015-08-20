package uk.co.wehive.hive.view.fragment;

import android.location.Location;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.GoodTimesDetailAdapter;
import uk.co.wehive.hive.bl.EventsBL;
import uk.co.wehive.hive.bl.PostBL;
import uk.co.wehive.hive.entities.EventDetail;
import uk.co.wehive.hive.entities.EventsUser;
import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.EventDetailResponse;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.dialogs.IGoodTimesOptions;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.LocationHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.camera.CameraUtil;
import uk.co.wehive.hive.utils.swipe.ListViewSwipeGesture;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class EventDetailFragment extends GATrackerFragment implements IHiveResponse,
        View.OnClickListener, IGoodTimesOptions, AdapterView.OnItemClickListener {

    @InjectView(R.id.txt_pending_posts)
    TextView mTxtPendingPosts;
    @InjectView(R.id.lbl_location)
    TextView mLblLocation;
    @InjectView(R.id.img_event)
    ImageView mImgEvent;
    @InjectView(R.id.img_carousel)
    ImageView mImgCarousel;
    @InjectView(R.id.lbl_artist)
    TextView mLblArtist;
    @InjectView(R.id.btn_track)
    Button mBtnTrack;
    @InjectView(R.id.btn_check_in)
    Button mBtnCheckIn;
    @InjectView(R.id.tbt_everything)
    ToggleButton mTbtEverything;
    @InjectView(R.id.tbt_live)
    ToggleButton mTbtLive;
    @InjectView(R.id.tbt_buzzing)
    ToggleButton mTbtBuzzing;
    @InjectView(R.id.list_everything)
    PullToRefreshListView mLvEverything;
    @InjectView(R.id.list_live)
    PullToRefreshListView mLvLive;
    @InjectView(R.id.list_buzzing)
    PullToRefreshListView mLvBuzzing;

    private static final int PAGE_SIZE = 10;
    private final String TAG = "EventDetailFragment";
    private static final String EVERYTHING_TAB = "everything";
    private static final String LIVE_TAB = "live";
    private static final String BUZZING_TAB = "buzzing";

    private ProgressHive mProgressHive;
    private EventsUser mEventsUser;
    private FragmentManager mFragmentManager;
    private User mUserProfile;
    private GoodTimesDetailAdapter mEverythingAdapter;
    private GoodTimesDetailAdapter mLiveAdapter;
    private GoodTimesDetailAdapter mBuzzingAdapter;
    private ArrayList<Post> mEverythingList;
    private ArrayList<Post> mLiveList;
    private ArrayList<Post> mBuzzingList;
    private EventsBL mEverythingBL;
    private EventsBL mLiveBL;
    private EventsBL mBuzzingBL;
    private PostBL mPostBL;
    private EventDetail mEventDetail;
    private String mLastTabSelected;
    private int mLastItemSelected;
    private int mTotalEverything = 0;
    private int mTotalLive = 0;
    private int mTotalBuzzing = 0;
    private boolean mIsLoadingEverything = false;
    private boolean mIsLoadingLive = false;
    private boolean mIsLoadingBuzzing = false;
    private boolean mIsTracking;
    private boolean mIsCheckedIn;
    private boolean mIsAllowCheckIn;
    private boolean mComesFromNotification;
    private boolean mIsExploreMode;
    private boolean mItemListSelected;
    private boolean mPostDone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.event_details_title));

        LocationHelper locationHelper = LocationHelper.getInstance(this.getActivity());
        Location lastKnownLocation = LocationHelper.getInstance(this.getActivity()).getLastKnownLocation();

        // Get initial location
        locationHelper.requestLocation(new LocationHelper.LocationResult() {
            @Override
            public void gotLocation(Location location) {
            }
        }, 4000);

        mUserProfile = ManagePreferences.getUserPreferences();

        Bundle bundle = getArguments();
        mFragmentManager = getActivity().getSupportFragmentManager();
        if (bundle != null && bundle.containsKey(AppConstants.EVENT_DATA)) {
            mEventsUser = bundle.getParcelable(AppConstants.EVENT_DATA);
        }

        if (getArguments().containsKey(AppConstants.EXPLORE_MODE)) {
            mIsExploreMode = getArguments().getBoolean(AppConstants.EXPLORE_MODE);
        }
        setHasOptionsMenu(true);

        if (bundle != null && bundle.containsKey(AppConstants.ID_EVENT)) {
            mComesFromNotification = true;
            mEventsUser = new EventsUser();
            mEventsUser.setId_event(bundle.getInt(AppConstants.ID_EVENT));

            EventsBL mEventsBLNotfif = new EventsBL();
            mEventsBLNotfif.setEventsUserListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                }

                @Override
                public void onResult(HiveResponse response) {
                    if (response.getStatus()) {
                        try {
                            mEventDetail = ((EventDetailResponse) response).getData();
                            mEventsUser.setName(mEventDetail.getName());
                            mEventsUser.setAbsolute_photo(mEventDetail.getPhoto());
                            mEventsUser.setVenue(mEventDetail.getPlace());
                            completeMissingData();
                        } catch (Exception e) {
                            Log.d(TAG, "error " + e.getMessage());
                        }
                    }
                }
            });

            mEventsBLNotfif.getDetail(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()), "", "");
        }

        mProgressHive = new ProgressHive();
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        EventsBL mEventsBL = new EventsBL();
        mEventsBL.setEventsUserListener(this);

        if (lastKnownLocation != null) {
            mEventsBL.getDetail(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()),
                    String.valueOf(lastKnownLocation.getLatitude() == 0 ? "" : lastKnownLocation.getLatitude()),
                    String.valueOf(lastKnownLocation.getLongitude() == 0 ? "" : lastKnownLocation.getLongitude()));
        } else {
            mEventsBL.getDetail(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()), "", "");
        }

        // Check if a item was selected and recover previous state or load all event details.
        if (mItemListSelected) {
            mItemListSelected = false;
            setPreviousState();
        } else if (mPostDone) {
            mPostDone = false;
            reloadPreviousTab();
        } else {
            mEverythingList = new ArrayList<Post>();
            mLiveList = new ArrayList<Post>();
            mBuzzingList = new ArrayList<Post>();

            initViewComponents();

            getEverything();
            getLive();
            getBuzzing();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mItemListSelected = true;
        mLastItemSelected = position;

        Post post = new Post();
        if (mTbtEverything.isChecked()) {
            post = mEverythingList.get(position);
            Log.i(TAG, " everything CLICKED");
            mLastTabSelected = EVERYTHING_TAB;
        } else if (mTbtLive.isChecked()) {
            post = mLiveList.get(position);
            Log.i(TAG, " live CLICKED");
            mLastTabSelected = LIVE_TAB;
        } else if (mTbtBuzzing.isChecked()) {
            post = mBuzzingList.get(position);
            Log.i(TAG, " buzzing CLICKED");
            mLastTabSelected = BUZZING_TAB;
        }
        setAction(post, AppConstants.OPTIONS_GOOD_TIMES.comments, "");
    }

    private void getBuzzing() {
        if (!mIsLoadingBuzzing) {
            mIsLoadingBuzzing = true;
            mBuzzingBL.getBuzzing(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()), mTotalBuzzing, PAGE_SIZE);
        }
    }

    private void getLive() {
        if (!mIsLoadingLive) {
            mIsLoadingLive = true;
            mLiveBL.getLive(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()), mTotalLive, PAGE_SIZE);
        }
    }

    private void getEverything() {
        if (!mIsLoadingEverything) {
            mIsLoadingEverything = true;
            mEverythingBL.getEverything(String.valueOf(mEventsUser.getId_event()),
                    String.valueOf(mUserProfile.getId_user()), mTotalEverything, PAGE_SIZE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.event_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mIsExploreMode) {
            menu.getItem(0).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                goMoreEventDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setEverythingAdapter() {
        mEverythingAdapter = new GoodTimesDetailAdapter(this, mEverythingList, mIsExploreMode);
        mEverythingAdapter.setListener(this);
        mLvEverything.setAdapter(mEverythingAdapter);
    }

    private void setLiveAdapter() {
        mLiveAdapter = new GoodTimesDetailAdapter(this, mLiveList, mIsExploreMode);
        mLiveAdapter.setListener(this);
        mLvLive.setAdapter(mLiveAdapter);
    }

    private void setBuzzingAdapter() {
        mBuzzingAdapter = new GoodTimesDetailAdapter(this, mBuzzingList, mIsExploreMode);
        mBuzzingAdapter.setListener(this);
        mLvBuzzing.setAdapter(mBuzzingAdapter);
    }

    private void setListener() {
        mEverythingBL = new EventsBL();
        mEverythingBL.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mIsLoadingEverything = false;
            }

            @Override
            public void onResult(HiveResponse response) {
                try {
                    ArrayList<Post> everythingList = ((EventPostsResponse) response).getData();
                    mTotalEverything = mTotalEverything + everythingList.size();

                    for (Post anEverythingList : everythingList) {
                        anEverythingList.setSwipe(!String.valueOf(mUserProfile.getId_user()).equals(anEverythingList.getId_user()));
                    }

                    if (itemAlreadyIncluded(mEverythingList, everythingList)) {
                        mEverythingList.clear();
                    }
                    mEverythingList.addAll(everythingList);
                    mIsLoadingEverything = false;
                    if (mTbtEverything.isChecked()) {
                        mEverythingAdapter.notifyDataSetChanged();
                        mLvEverything.onRefreshComplete();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });

        mLiveBL = new EventsBL();
        mLiveBL.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mIsLoadingLive = false;
            }

            @Override
            public void onResult(HiveResponse response) {
                mIsLoadingLive = false;
                if (response.getStatus()) {
                    try {
                        ArrayList<Post> liveList = ((EventPostsResponse) response).getData();
                        mTotalLive = mTotalLive + liveList.size();

                        for (Post aLiveList : liveList) {
                            aLiveList.setSwipe(!String.valueOf(mUserProfile.getId_user()).equals(aLiveList.getId_user()));
                        }

                        if (itemAlreadyIncluded(mLiveList, liveList)) {
                            mLiveList.clear();
                        }
                        mLiveList.addAll(liveList);

                        if (mTbtLive.isChecked()) {
                            mLiveAdapter.notifyDataSetChanged();
                            mLvLive.onRefreshComplete();
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "error " + e.getMessage());
                    }
                }
            }
        });

        mBuzzingBL = new EventsBL();
        mBuzzingBL.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mIsLoadingBuzzing = false;
            }

            @Override
            public void onResult(HiveResponse response) {
                try {
                    ArrayList<Post> buzzingList = ((EventPostsResponse) response).getData();
                    mTotalBuzzing = mTotalBuzzing + buzzingList.size();

                    for (Post aBuzzingList : buzzingList) {
                        aBuzzingList.setSwipe(!String.valueOf(mUserProfile.getId_user()).equals(aBuzzingList.getId_user()));
                    }

                    if (itemAlreadyIncluded(mBuzzingList, buzzingList)) {
                        mBuzzingList.clear();
                    }
                    mBuzzingList.addAll(buzzingList);
                    mIsLoadingBuzzing = false;
                    if (mTbtBuzzing.isChecked()) {
                        mBuzzingAdapter.notifyDataSetChanged();
                        mLvBuzzing.onRefreshComplete();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "error " + e.getMessage());
                }
            }
        });
    }

    // Initialize view components.
    private void initViewComponents() {

        setPendingPostsNotification();

        mTotalEverything = 0;
        mTotalLive = 0;
        mTotalBuzzing = 0;
        mImgEvent.setOnClickListener(this);
        ImageButton btnWritePost = (ImageButton) getView().findViewById(R.id.btn_write);
        btnWritePost.setOnClickListener(this);

        if (!mComesFromNotification) {
            mLblArtist.setText(mEventsUser.getName().toUpperCase());
            Picasso.with(getActivity())
                    .load(mEventsUser.getAbsolute_photo())
                    .into(mImgEvent);
        }

        mBtnTrack.setOnClickListener(this);
        mBtnCheckIn.setOnClickListener(this);
        mTbtEverything.setOnClickListener(this);
        mTbtLive.setOnClickListener(this);
        mTbtBuzzing.setOnClickListener(this);

        mLvEverything.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    setVisibilityTopViews(View.GONE);
                }
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsLoadingEverything) {
                    getEverything();
                }
            }
        });

        mLvEverything.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setVisibilityTopViews(View.VISIBLE);
                mTotalEverything = 0;
                mEverythingList.clear();
                getEverything();
            }
        });

        cleanRefreshingLabels(mLvLive);
        mLvLive.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    setVisibilityTopViews(View.GONE);
                }
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsLoadingLive) {
                    getLive();
                }
            }
        });

        mLvLive.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setVisibilityTopViews(View.VISIBLE);
                mTotalLive = 0;
                mLiveList.clear();
                getLive();
            }
        });

        cleanRefreshingLabels(mLvBuzzing);
        mLvBuzzing.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    setVisibilityTopViews(View.GONE);
                }
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsLoadingBuzzing) {
                    getBuzzing();
                }
            }
        });

        mLvBuzzing.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setVisibilityTopViews(View.VISIBLE);
                mTotalBuzzing = 0;
                mBuzzingList.clear();
                getBuzzing();
            }
        });

        mPostBL = new PostBL();
        mPostBL.setListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
            }

            @Override
            public void onResult(HiveResponse response) {
            }
        });

        ImageButton btnCamera = (ImageButton) getView().findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(this);

        setListener();

        setEverythingAdapter();
        setLiveAdapter();
        setBuzzingAdapter();

        if (!mIsExploreMode) {

            final ListViewSwipeGesture everythingTouchListener =
                    new ListViewSwipeGesture(mLvEverything, swipeListener, this.getActivity(),
                            R.id.list_display_view_container, R.drawable.ic_flag_white, false);
            mLvEverything.setOnTouchListener(everythingTouchListener);

            final ListViewSwipeGesture liveTouchListener =
                    new ListViewSwipeGesture(mLvLive, swipeListener, this.getActivity(),
                            R.id.list_display_view_container, R.drawable.ic_flag_white, false);
            mLvLive.setOnTouchListener(liveTouchListener);

            final ListViewSwipeGesture buzzingTouchListener =
                    new ListViewSwipeGesture(mLvBuzzing, swipeListener, this.getActivity(),
                            R.id.list_display_view_container, R.drawable.ic_flag_white, false);
            mLvBuzzing.setOnTouchListener(buzzingTouchListener);
        } else {

            mLvEverything.setOnItemClickListener(this);
            mLvLive.setOnItemClickListener(this);
            mLvBuzzing.setOnItemClickListener(this);
        }

        cleanRefreshingLabels(mLvEverything);
    }

    private void setPendingPostsNotification() {
        EventPostsResponse readResponse =
                PersistenceHelper.readResponse(getActivity(), AppConstants.PENDING_POSTS + "_" +
                        String.valueOf(mUserProfile.getId_user()), EventPostsResponse.class);

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

    private void completeMissingData() {
        mImgEvent.setOnClickListener(this);

        mLblArtist.setText(mEventsUser.getName().toUpperCase());
        Picasso.with(getActivity())
                .load(mEventsUser.getAbsolute_photo())
                .into(mImgEvent);
    }

    private void cleanRefreshingLabels(PullToRefreshListView listView) {
        listView.setTextPullToRefresh("");
        listView.setTextReleaseToRefresh("");
        listView.setTextRefreshing("");
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        mProgressHive.dismiss();
        if (response.getStatus()) {
            try {
                mEventDetail = ((EventDetailResponse) response).getData();
                mLblLocation.setText(mEventDetail.getPlace());
                if (mEventDetail.isIs_tracking()) {
                    mBtnTrack.setText(getString(R.string.tracking));
                    mBtnTrack.setBackgroundResource(R.drawable.bt_event_detail_green);
                    mIsTracking = mEventDetail.isIs_tracking();
                } else {
                    mBtnTrack.setText(getString(R.string.track));
                    mBtnTrack.setBackgroundResource(R.drawable.bt_event_detail);
                }

                if (mEventDetail.getStatus_event().equalsIgnoreCase("progress")) {
                    mBtnCheckIn.setVisibility(View.VISIBLE);
                    mTbtLive.setVisibility(View.VISIBLE);
                    if (mEventDetail.isIs_checkin()) {
                        mBtnCheckIn.setBackgroundResource(R.drawable.bt_event_detail_green);
                        mBtnCheckIn.setActivated(false);
                    } else {
                        mBtnCheckIn.setBackgroundResource(R.drawable.bt_event_detail);
                    }
                } else {
                    mBtnCheckIn.setVisibility(View.GONE);
                    mTbtLive.setVisibility(View.GONE);
                }
                mIsCheckedIn = mEventDetail.isIs_checkin();
                mIsAllowCheckIn = mEventDetail.isAllow_checkin();
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }

        } else {
            mProgressHive.dismiss();
            Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tbt_everything:
                activeEverythingTab();
                mEverythingAdapter.notifyDataSetChanged();
                mLvEverything.onRefreshComplete();
                break;

            case R.id.tbt_live:
                activeLiveTab();
                mLvLive.onRefreshComplete();
                mLiveAdapter.notifyDataSetChanged();
                break;

            case R.id.tbt_buzzing:
                activeBuzzingTab();
                mLvBuzzing.onRefreshComplete();
                mBuzzingAdapter.notifyDataSetChanged();
                break;

            case R.id.btn_track:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    trackEvent();
                }
                break;

            case R.id.btn_check_in:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    checkInEvent();
                }
                break;

            case R.id.btn_write:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    if (mIsTracking) {
                        createPost();
                    } else {
                        ErrorDialog.showErrorMessage(getActivity(),
                                getString(R.string.you_need_to_track_this_event_to_leave_a_post), "");
                    }
                }
                break;

            case R.id.btn_camera:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    if (mIsTracking) {
                        createPostFromCamera();
                    } else {
                        ErrorDialog.showErrorMessage(getActivity(),
                                getString(R.string.you_need_to_track_this_event_to_leave_a_post), "");
                    }
                }
                break;

            case R.id.img_event:
                if (mIsExploreMode) {
                    returnToLoginView();
                } else {
                    /*
                    Intent intent = new Intent(getActivity(), PhotoCarouselActivity.class);
                    intent.putExtra(AppConstants.ID_EVENT, String.valueOf(mEventsUser.getId_event()));
                    startActivity(intent);
                    */

                    PhotoCarouselFragment fragment = new PhotoCarouselFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.ID_EVENT, String.valueOf(mEventsUser.getId_event()));
                    fragment.setArguments(bundle);
                    mFragmentManager.beginTransaction()
                            .addToBackStack("")
                            .replace(R.id.content_frame, fragment)
                            .commit();
                }
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
    public void setAction(Post post, AppConstants.OPTIONS_GOOD_TIMES option, String active) {
        String selection = option.toString();

        if (selection.equals(AppConstants.OPTIONS_GOOD_TIMES.like.toString())) {
            mPostBL.addLike(mUserProfile.getToken(), String.valueOf(mUserProfile.getId_user()), post.getId_post(), active);
        } else if (selection.equals(AppConstants.OPTIONS_GOOD_TIMES.comments.toString())) {
            CommentGoodTimesDetailFragment fragment = new CommentGoodTimesDetailFragment();
            changeFragment(fragment, post);
        } else if (selection.equals(AppConstants.OPTIONS_GOOD_TIMES.share.toString())) {
            ShareGoodTimesDetailFragment fragment = new ShareGoodTimesDetailFragment();
            changeFragment(fragment, post);
        } else if (AppConstants.OPTIONS_GOOD_TIMES.explore.toString().equals(selection)) {
            returnToLoginView();
        }
    }

    private void changeFragment(Fragment fragment, Post post) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.POST, post);
        bundle.putBoolean(AppConstants.EXPLORE_MODE, mIsExploreMode);
        fragment.setArguments(bundle);
        mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("").commit();
    }

    private void createPost() {
        // Save current state view and use it when post is published
        mPostDone = true;
        setSelectedTab();

        if (mEventDetail.isAllow_post()) {
            CreatePostFragment fragment = new CreatePostFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetail);
            bundle.putBoolean(AppConstants.FROM_MEME, false);
            bundle.putBoolean(AppConstants.FROM_VIDEO, false);
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("").commit();
        } else {
            if (getDaysToEvent() < 0) {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_can_no_longer_post_to_this_event), "");
            } else {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_cannot_post_to_this_event_yet), "");
            }
        }
    }

    private void createPostFromCamera() {
        // Save current state view and use it when post is published
        mPostDone = true;
        setSelectedTab();

        if (mEventDetail.isAllow_post()) {
            if (CameraUtil.checkCameraHardware(getActivity())) {
                CustomCamera customCamera = new CustomCamera();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EVENT_DATA, mEventDetail);
                customCamera.setArguments(bundle);
                mFragmentManager.beginTransaction().replace(R.id.content_frame, customCamera).addToBackStack("").commit();
            } else {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.need_camera));
            }
        } else {
            if (getDaysToEvent() < 0) {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_can_no_longer_post_to_this_event), "");
            } else {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.you_cannot_post_to_this_event_yet), "");
            }
        }
    }

    private void goMoreEventDetails() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.ID_EVENT, String.valueOf(mEventsUser.getId_event()));
        MoreEventFragment moreEventFragment = new MoreEventFragment();
        moreEventFragment.setArguments(bundle);
        mFragmentManager.beginTransaction().replace(R.id.content_frame, moreEventFragment).addToBackStack("").commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.event_details_title));
    }

    ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

        @Override
        public void HalfSwipeListView(int position) {
            Bundle bundle;
            bundle = new Bundle();
            if (mTbtEverything.isChecked()) {
                bundle.putString(AppConstants.ID_POST, mEverythingList.get(position - 1).getId_post());
            } else if (mTbtLive.isChecked()) {
                bundle.putString(AppConstants.ID_POST, mLiveList.get(position - 1).getId_post());
            } else if (mTbtBuzzing.isChecked()) {
                bundle.putString(AppConstants.ID_POST, mBuzzingList.get(position - 1).getId_post());
            }
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
            if (position > 0) {
                mItemListSelected = true;
                mLastItemSelected = position - 1;

                Post post = new Post();
                if (mTbtEverything.isChecked()) {
                    post = mEverythingList.get(position - 1);
                    mLastTabSelected = EVERYTHING_TAB;
                } else if (mTbtLive.isChecked()) {
                    post = mLiveList.get(position - 1);
                    mLastTabSelected = LIVE_TAB;
                } else if (mTbtBuzzing.isChecked()) {
                    post = mBuzzingList.get(position - 1);
                    mLastTabSelected = BUZZING_TAB;
                }
                setAction(post, AppConstants.OPTIONS_GOOD_TIMES.comments, "");
            }
        }
    };

    private boolean itemAlreadyIncluded(List<Post> previousList, ArrayList<Post> newList) {
        if (newList != null && newList.size() > 0) {
            Post recentItem = newList.get(0);

            for (Post post : previousList) {
                if (post.getId_post().equals(recentItem.getId_post())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setVisibilityTopViews(int visibility) {
        mImgEvent.setVisibility(visibility);
        mImgCarousel.setVisibility(visibility);
        mLblArtist.setVisibility(visibility);
        mLblLocation.setVisibility(visibility);
    }

    private void returnToLoginView() {
        if (mIsExploreMode) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_in_left, 0);
        }
    }

    private void trackEvent() {
        EventsBL event = new EventsBL();
        mProgressHive.show(getFragmentManager(), "");
        event.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
            }

            @Override
            public void onResult(HiveResponse response) {
                if (response.getStatus()) {
                    mIsTracking = !mIsTracking;
                    if (mIsTracking) {
                        mBtnTrack.setText(getString(R.string.tracking));
                        mBtnTrack.setBackgroundResource(R.drawable.bt_event_detail_green);
                    } else {
                        mBtnTrack.setText(getString(R.string.track));
                        mBtnTrack.setBackgroundResource(R.drawable.bt_event_detail);
                    }
                    mProgressHive.dismiss();
                } else {
                    mProgressHive.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
                }
            }
        });

        event.track(mUserProfile.getToken(), String.valueOf(mEventsUser.getId_event()),
                String.valueOf(mUserProfile.getId_user()), mIsTracking ? "0" : "1");
    }

    private void checkInEvent() {
        EventsBL event = new EventsBL();
        mProgressHive.show(getFragmentManager(), "");
        event.setEventsUserListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
                mProgressHive.dismiss();
            }

            @Override
            public void onResult(HiveResponse response) {
                if (response.getStatus()) {
                    mIsCheckedIn = !mIsCheckedIn;
                    if (mIsCheckedIn) {
                        mBtnCheckIn.setBackgroundResource(R.drawable.bt_event_detail_green);
                        mBtnCheckIn.setActivated(false);
                    } else {
                        mBtnCheckIn.setBackgroundResource(R.drawable.bt_event_detail);
                    }
                    mProgressHive.dismiss();
                } else {
                    mProgressHive.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.toast_event_coming_soon), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //I can only check in to an event once, and I can't do checkout
        if (!mIsCheckedIn) {
            if (mIsAllowCheckIn) {
                event.checkin(mUserProfile.getToken(), String.valueOf(mEventsUser.getId_event()),
                        String.valueOf(mUserProfile.getId_user()), mIsCheckedIn ? "0" : "1");
            } else {
                mProgressHive.dismiss();
                ErrorDialog.showErrorMessage(getActivity(), "",
                        getString(R.string.far_away_checkin));
            }
        } else {
            mProgressHive.dismiss();
        }
    }

    private void setPreviousState() {
        if (EVERYTHING_TAB.equals(mLastTabSelected)) {
            activeEverythingTab();
            mLvEverything.setSelection(mLastItemSelected);
        } else if (LIVE_TAB.equals(mLastTabSelected)) {
            activeLiveTab();
            mLvLive.setSelection(mLastItemSelected);
        } else if (BUZZING_TAB.equals(mLastTabSelected)) {
            activeBuzzingTab();
            mLvEverything.setSelection(mLastItemSelected);
        }
        mLastTabSelected = "";
        initViewComponents();
    }

    private void activeBuzzingTab() {
        mTbtEverything.setChecked(false);
        mTbtLive.setChecked(false);
        mTbtBuzzing.setChecked(true);
        mLvEverything.setVisibility(View.GONE);
        mLvLive.setVisibility(View.GONE);
        mLvBuzzing.setVisibility(View.VISIBLE);
    }

    private void activeLiveTab() {
        mTbtEverything.setChecked(false);
        mTbtLive.setChecked(true);
        mTbtBuzzing.setChecked(false);
        mLvEverything.setVisibility(View.GONE);
        mLvLive.setVisibility(View.VISIBLE);
        mLvBuzzing.setVisibility(View.GONE);
    }

    private void activeEverythingTab() {
        mTbtEverything.setChecked(true);
        mTbtLive.setChecked(false);
        mTbtBuzzing.setChecked(false);
        mLvEverything.setVisibility(View.VISIBLE);
        mLvLive.setVisibility(View.GONE);
        mLvBuzzing.setVisibility(View.GONE);
    }

    private void setSelectedTab() {
        if (mTbtEverything.isChecked()) {
            mLastTabSelected = EVERYTHING_TAB;
        } else if (mTbtLive.isChecked()) {
            mLastTabSelected = LIVE_TAB;
        } else if (mTbtBuzzing.isChecked()) {
            mLastTabSelected = BUZZING_TAB;
        }
    }

    private void reloadPreviousTab() {
        if (EVERYTHING_TAB.equals(mLastTabSelected)) {
            mEverythingList.clear();
            mTotalEverything = 0;
            getEverything();
            activeEverythingTab();
        } else if (LIVE_TAB.equals(mLastTabSelected)) {
            mLiveList.clear();
            mTotalLive = 0;
            getLive();
            activeLiveTab();
        } else if (BUZZING_TAB.equals(mLastTabSelected)) {
            mBuzzingList.clear();
            mTotalBuzzing = 0;
            getBuzzing();
            activeBuzzingTab();
        }
        mLastTabSelected = "";
        initViewComponents();
    }

    private int getDaysToEvent() {
        SimpleDateFormat eventFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        newFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date convertedDate = new Date();
        try {
            String reformattedStr = newFormat.format(eventFormat.parse(mEventDetail.getDate_event()));
            convertedDate = dateFormat.parse(reformattedStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date d = new Date(System.currentTimeMillis());
        return (int) ((convertedDate.getTime() - d.getTime()) / (1000 * 60 * 60 * 24));
    }
}