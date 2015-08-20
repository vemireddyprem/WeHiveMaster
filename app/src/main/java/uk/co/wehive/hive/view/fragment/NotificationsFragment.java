/*******************************************************************************
 PROJECT:       Hive
 FILE:          NotificationsFragment.java
 DESCRIPTION:   Notifications view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        16/07/2014  Marcela Güiza    1. Initial definition.
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.erikw.PullToRefreshListView;
import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.NotificationAdapter;
import uk.co.wehive.hive.bl.NotificationsBL;
import uk.co.wehive.hive.entities.NotificationMessage;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.NotificationsResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class NotificationsFragment extends GATrackerFragment implements IHiveResponse,
        AdapterView.OnItemClickListener, PullToRefreshListView.OnRefreshListener,
        AbsListView.OnScrollListener {

    private static final String PAGE_SIZE = "10";
    private static final String EVENT = "event";
    private static final String POST = "post";
    private static final String USER = "user";
    private static final String GOODTIME = "goodtime";

    private PullToRefreshListView mLvNotifications;
    private FragmentManager mFragmentManager;
    private NotificationsBL mNotificationsBL;
    private NotificationAdapter mNotificationsAdapter;
    private ArrayList<NotificationMessage> mNotificationsList;
    private ProgressHive mProgressBar;
    private User mUser;
    private int mStartingItem;
    private int mLastItemSelected;
    private boolean mIsLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notifications_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ManagePreferences.setNotificationsCounterMenu(0);
        mProgressBar = new ProgressHive();
        mProgressBar.setCancelable(false);
        mProgressBar.show(getFragmentManager(), "");

        mFragmentManager = getActivity().getSupportFragmentManager();
        mStartingItem = 0;

        mNotificationsBL = new NotificationsBL();
        mNotificationsBL.setHiveListener(this);

        initViewComponents();

        boolean isNewFragment = ManagePreferences.isANewFragment();

        if (mNotificationsList != null && mNotificationsList.size() > 0 && !isNewFragment) {
            mNotificationsList.remove(mLastItemSelected);
            mLvNotifications.setSelection(mLastItemSelected - 1);
            mProgressBar.dismiss();
        } else {
            mStartingItem = 0;
            mLastItemSelected = 0;
            mNotificationsList = new ArrayList<NotificationMessage>();
            mNotificationsAdapter = new NotificationAdapter(getActivity(), mNotificationsList);

            getNotifications();
        }
        mLvNotifications.setAdapter(mNotificationsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.notifications_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                NotificationsSettingsFragment fragment = new NotificationsSettingsFragment();
                mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("").commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViewComponents() {
        mUser = ManagePreferences.getUserPreferences();

        mLvNotifications = (PullToRefreshListView) getView().findViewById(R.id.lvnotifications);
        mLvNotifications.setOnItemClickListener(this);
        mLvNotifications.setOnRefreshListener(this);
        mLvNotifications.setOnScrollListener(this);
        cleanRefreshingLabels();
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.menu_option_notifications));
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressBar.dismiss();
        mLvNotifications.onRefreshComplete();
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        ManagePreferences.setNewsFeedCounterMenu(response.getCount_newsfeed());

        ArrayList<NotificationMessage> notifications = ((NotificationsResponse) response).getData();

        if (mIsLoading && !itemAlreadyIncluded(mNotificationsList, notifications)) {
            mNotificationsList.addAll(notifications);
            mNotificationsAdapter.updateNotificationsList(mNotificationsList);
            mIsLoading = false;
        } else {
            mNotificationsList.clear();
            mNotificationsList.addAll(notifications);
            mNotificationsAdapter.updateNotificationsList(mNotificationsList);
            mLvNotifications.onRefreshComplete();
        }

        mNotificationsAdapter.notifyDataSetChanged();
        mStartingItem = mStartingItem + notifications.size();
        mProgressBar.dismiss();
    }

    @Override
    public void onRefresh() {
        mStartingItem = 0;
        getNotifications();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int itemSelected, long l) {
        ManagePreferences.setNewFragment(false);
        mLastItemSelected = itemSelected;

        NotificationMessage mNotification = mNotificationsList.get(itemSelected);
        NotificationsBL notifyReadedBL = new NotificationsBL();
        notifyReadedBL.setHiveListener(new IHiveResponse() {
            @Override
            public void onError(RetrofitError error) {
            }

            @Override
            public void onResult(HiveResponse response) {
            }
        });
        notifyReadedBL.readed(mUser.getToken(), mNotification.getId_notification());

        if (EVENT.equals(mNotification.getOrigin())) {
            //event detail
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.ID_EVENT, mNotification.getId_origin());
            EventDetailFragment eventDetailFragment = new EventDetailFragment();
            eventDetailFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, eventDetailFragment).commit();

        } else if (POST.equals(mNotification.getOrigin())) {
            //Post detail
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.ID_POST, String.valueOf(mNotification.getId_origin()));
            CommentGoodTimesDetailFragment postFragment = new CommentGoodTimesDetailFragment();
            postFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, postFragment).commit();
        } else if (USER.equals(mNotification.getOrigin())) {
            //User profile
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.USER_ID, String.valueOf(mNotification.getId_origin()));
            UserProfileFragment userProfileFragment = new UserProfileFragment();
            userProfileFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();

        } else if (GOODTIME.equals(mNotification.getOrigin())) {
            //Goodtime detail
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.ID_EVENT, mNotification.getId_origin());
            GoodTimesDetailFragment goodTimeDetailFragment = new GoodTimesDetailFragment();
            goodTimeDetailFragment.setArguments(bundle);
            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, goodTimeDetailFragment).commit();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean lastItem = (firstVisibleItem + visibleItemCount == totalItemCount);
        boolean needToBeReload = mNotificationsList != null && mNotificationsList.size() > 0 && mNotificationsList.size() < 10;
        if (lastItem && totalItemCount != 0 && !mIsLoading && !needToBeReload) {
            mIsLoading = true;
            getNotifications();
        }
    }

    private void getNotifications() {
        mNotificationsBL.getListNotifications(mUser.getToken(), String.valueOf(mUser.getId_user()),
                String.valueOf(mStartingItem), PAGE_SIZE);
    }

    private void cleanRefreshingLabels() {
        mLvNotifications.setTextPullToRefresh("");
        mLvNotifications.setTextReleaseToRefresh("");
        mLvNotifications.setTextRefreshing("");
    }

    private boolean itemAlreadyIncluded(List<NotificationMessage> previousList, ArrayList<NotificationMessage> newList) {
        if (newList != null && newList.size() > 0) {
            NotificationMessage recentItem = newList.get(0);

            for (NotificationMessage notification : previousList) {
                if (notification.getId_notification().equals(recentItem.getId_notification())) {
                    return true;
                }
            }
        }
        return false;
    }
}
