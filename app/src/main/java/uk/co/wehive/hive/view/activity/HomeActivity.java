/*******************************************************************************
 PROJECT:       Hive
 FILE:          HomeActivity.java
 DESCRIPTION:   Main view
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        14/07/2014  Marcela Güiza    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.view.activity;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import java.io.File;
import java.util.ArrayList;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.adapter.DrawerMenuAdapter;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.utils.OptionMenu;
import uk.co.wehive.hive.view.fragment.CommentGoodTimesDetailFragment;
import uk.co.wehive.hive.view.fragment.EventDetailFragment;
import uk.co.wehive.hive.view.fragment.EventsFragment;
import uk.co.wehive.hive.view.fragment.FollowPeopleFragment;
import uk.co.wehive.hive.view.fragment.GoodTimesDetailFragment;
import uk.co.wehive.hive.view.fragment.NewsFeedFragment;
import uk.co.wehive.hive.view.fragment.NotificationsFragment;
import uk.co.wehive.hive.view.fragment.SettingsFragment;
import uk.co.wehive.hive.view.fragment.UserProfileFragment;

public class HomeActivity extends ActionBarActivity implements ListView.OnItemClickListener,
        View.OnClickListener, IHiveResponse {

    private String[] mMenuTitles;
    private final String TAG = "UserProfileFragment";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragmentManager;
    private UserProfileFragment mUserProfileFragment;
    private EventsFragment mEventsFragment;
    private NotificationsFragment mNotificationsFragment;
    private NewsFeedFragment mNewsFeedFragment;
    private SettingsFragment mSettingsFragment;
    private FollowPeopleFragment mFollowPeopleFragment;
    private DrawerMenuAdapter mMenuAdapter;
    private User mUserPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);
        mFragmentManager = getSupportFragmentManager();
        mUserPrefs = ManagePreferences.getUserPreferences();
        Log.i(TAG, " token - home - onCreate " + mUserPrefs.getToken());
        UsersBL usersBL = new UsersBL();
        usersBL.setHiveListener(this);
        usersBL.profile(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()), String.valueOf(mUserPrefs.getId_user()));
        initViewComponents();

        deleteInternalStorage();
    }

    private void isNotification() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey(AppConstants.ORIGIN_GCM) && extras.containsKey(AppConstants.ID_ORIGIN_GCM)) {
                String origin = extras.getString(AppConstants.ORIGIN_GCM);
                String idOrigin = extras.getString(AppConstants.ID_ORIGIN_GCM);

                if (origin.equals(AppConstants.OPTIONS_NOTIFICATIONS.user.toString())) {
                    mUserProfileFragment = new UserProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.USER_ID, idOrigin);
                    mUserProfileFragment.setArguments(bundle);
                    Log.i(TAG, " token - home - isNotification " + mUserPrefs.getToken());
                    mFragmentManager.beginTransaction().replace(R.id.content_frame, mUserProfileFragment).commit();

                } else if (origin.equals(AppConstants.OPTIONS_NOTIFICATIONS.post.toString())) {
                    CommentGoodTimesDetailFragment fragment = new CommentGoodTimesDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.ID_POST, idOrigin);
                    fragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                } else if (origin.equals(AppConstants.OPTIONS_NOTIFICATIONS.goodtime.toString())) {
                    GoodTimesDetailFragment goodTimesDetailFragment = new GoodTimesDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppConstants.ID_EVENT, Integer.parseInt(idOrigin));
                    goodTimesDetailFragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().replace(R.id.content_frame, goodTimesDetailFragment).commit();

                } else if (origin.equals(AppConstants.OPTIONS_NOTIFICATIONS.event.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppConstants.ID_EVENT, Integer.parseInt(idOrigin));
                    EventDetailFragment eventDetailFragment = new EventDetailFragment();
                    eventDetailFragment.setArguments(bundle);
                    mFragmentManager.beginTransaction().replace(R.id.content_frame, eventDetailFragment).commit();
                }
            } else {
                String fragmentLoad = extras.getString("fragment");
                if (fragmentLoad != null && fragmentLoad.equals(AppConstants.FOLLOW_PEOPLE)) {
                    changeFragment(AppConstants.FOLLOW_PEOPLE);
                } else {
                    changeFragment(AppConstants.USER_PROFILE);
                }
            }
        }
    }

    //Inicializate view components
    private void initViewComponents() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.bt_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        initDrawerToggle();
        mDrawerList = (ListView) findViewById(R.id.ltv_left_drawer);

        //Menu options views
        mUserProfileFragment = new UserProfileFragment();
        mEventsFragment = new EventsFragment();
        mNotificationsFragment = new NotificationsFragment();
        mNewsFeedFragment = new NewsFeedFragment();
        mSettingsFragment = new SettingsFragment();
        mFollowPeopleFragment = new FollowPeopleFragment();
        isNotification();
    }

    private void initDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.bt_menu,
                R.string.we_hive,
                R.string.we_hive
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                if (mMenuAdapter != null) {
                    mMenuAdapter.notifyDataSetChanged();
                }
                invalidateOptionsMenu();
            }
        };

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));

        View customView = getSupportActionBar().getCustomView();
        TextView txtTitle = (TextView) customView.findViewById(R.id.txt_title_view);
        txtTitle.setText(mUserPrefs.getUsername());

        customView.findViewById(R.id.lly_action_bar_menu).setOnClickListener(this);
        customView.findViewById(R.id.img_action_bar_icon).setOnClickListener(this);
        customView.findViewById(R.id.img_wehive_actionbar).setOnClickListener(this);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView txtTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.txt_title_view);
        txtTitle.setText(title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setTitle(mMenuTitles[position]);
        changeFragment(mMenuTitles[position]);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void changeFragment(String optionMenu) {

        if (AppConstants.USER_PROFILE.equals(optionMenu) && !mUserProfileFragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mUserProfileFragment).addToBackStack("").commit();
        } else if (AppConstants.EVENTS.equals(optionMenu) && !mEventsFragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mEventsFragment).addToBackStack("").commit();
        } else if (AppConstants.NOTIFICATIONS.equals(optionMenu) && !mNotificationsFragment.isAdded()) {
            ManagePreferences.setNewFragment(true);
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mNotificationsFragment).addToBackStack("").commit();
        } else if (AppConstants.NEWSFEED.equals(optionMenu) && !mNewsFeedFragment.isAdded()) {
            ManagePreferences.setNewFragment(true);
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mNewsFeedFragment).addToBackStack("").commit();
        } else if (AppConstants.SETTINGS.equals(optionMenu) && !mSettingsFragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mSettingsFragment).addToBackStack("").commit();
        } else if (AppConstants.FOLLOW_PEOPLE.equals(optionMenu) && !mFollowPeopleFragment.isAdded()) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, mFollowPeopleFragment).addToBackStack("").commit();
        }

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    //Fill option menu
    private ArrayList<OptionMenu> getOptionsMenu() {
        ArrayList<OptionMenu> optionsMenu = new ArrayList<OptionMenu>();

        mMenuTitles = getResources().getStringArray(R.array.options_menu);
        TypedArray iconsArray = getResources().obtainTypedArray(R.array.icons_options_menu);

        for (int i = 0; i < mMenuTitles.length; i++) {
            OptionMenu option = new OptionMenu();

            option.setMenuTitle(mMenuTitles[i]);
            option.setMenuImage(iconsArray.getResourceId(i, -1));
            optionsMenu.add(option);
        }
        iconsArray.recycle();
        return optionsMenu;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lly_action_bar_menu:
            case R.id.img_action_bar_icon:
            case R.id.img_wehive_actionbar:
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                break;
        }
    }

    @Override
    public void onError(RetrofitError error) {
        if (error.isNetworkError()) {
            setOptionsMenu();
        }
    }

    @Override
    public void onResult(HiveResponse response) {
        if (response.getStatus()) {
            String token = mUserPrefs.getToken();
            try {
                mUserPrefs = ((LoginResponse) response).getData();
                mUserPrefs.setToken(token);
                ManagePreferences.setPreferencesUser(mUserPrefs);
            } catch (Exception e) {
                Log.d(TAG, "error " + e.getMessage());
            }
        }
        setOptionsMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int backStackCount = mFragmentManager.getBackStackEntryCount();
        if (backStackCount == 0) {
            this.finish();
        }
    }

    private void setOptionsMenu() {
        mMenuAdapter = new DrawerMenuAdapter(getApplicationContext(), getOptionsMenu());
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(this);
    }

    private void deleteInternalStorage() {
        File[] listFiles = getFilesDir().listFiles();
        if (listFiles.length > 30) {
            for (File file : listFiles) {
                boolean deletedFile = file.delete();
                if (deletedFile) {
                    Log.i("HomeActivity", "Cleaning internal storage...");
                }
            }
        }
    }
}
