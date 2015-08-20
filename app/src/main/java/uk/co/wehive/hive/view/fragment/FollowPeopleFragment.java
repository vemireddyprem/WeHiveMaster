package uk.co.wehive.hive.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.UsersBL;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.Follower;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.ContactsUtils;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.activity.WebActivity;
import uk.co.wehive.hive.view.dialog.ErrorDialog;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class FollowPeopleFragment extends Fragment implements View.OnClickListener {

    private static final int TWITTER_REQUEST = 0;
    private static final String TAG = "FollowPeopleFragment";

    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    private FragmentManager mFragmentManager;
    private User mUserPrefs;
    private ProgressHive mProgressHive;
    private String mFacebookId;
    private boolean mIsLoading;

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.follow_people_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentManager = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);
        mUserPrefs = ManagePreferences.getUserPreferences();
        mFacebookId = mUserPrefs.getId_facebook();
        initViewComponents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.follow_people, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.USER_ID, String.valueOf(mUserPrefs.getId_user()));
                bundle.putSerializable(AppConstants.ACCESS_TOKEN, String.valueOf(mUserPrefs.getToken()));
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, userProfileFragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Initialize view components
    private void initViewComponents() {
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.follow_people_title));
        Button btnSearchUsers = (Button) getView().findViewById(R.id.btn_search_for_users);
        btnSearchUsers.setOnClickListener(this);
        Button btnTwitterFollowers = (Button) getView().findViewById(R.id.btn_twitter_followers);
        btnTwitterFollowers.setOnClickListener(this);
        Button btnPhoneContacts = (Button) getView().findViewById(R.id.btn_phone_contacts);
        btnPhoneContacts.setOnClickListener(this);
        mSharedPreferences = getActivity().getApplicationContext().getSharedPreferences("weHive", 0);
        mProgressHive = new ProgressHive();

        Button btnFacebookFriends = (Button) getView().findViewById(R.id.btn_facebook_friends);
        btnFacebookFriends.setOnClickListener(this);
        mIsLoading = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_search_for_users:
                SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
                Bundle bundle;
                bundle = new Bundle();
                bundle.putString(AppConstants.USER_NETWORK, getString(R.string.search_for_users_title));
                socialNetworkFragment.setArguments(bundle);
                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, socialNetworkFragment).commit();
                break;

            case R.id.btn_facebook_friends:
                getFriendsFacebook();
                break;

            case R.id.btn_twitter_followers:
                if (!mIsLoading) {
                    mIsLoading = true;
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.commit();

                    try {
                        if (Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }

                        ConfigurationBuilder builder = new ConfigurationBuilder();
                        builder.setOAuthConsumerKey(AppConstants.TWITTER_CONSUMER_KEY);
                        builder.setOAuthConsumerSecret(AppConstants.TWITTER_CONSUMER_SECRET);

                        Configuration configuration = builder.build();

                        TwitterFactory factory = new TwitterFactory(configuration);
                        twitter = factory.getInstance();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mIsLoading = false;
                                    requestToken = twitter.getOAuthRequestToken(AppConstants.TWITTER_CALLBACK_URL);
                                    Intent i = new Intent(getActivity().getApplicationContext(), WebActivity.class);
                                    i.putExtra("URL", requestToken.getAuthenticationURL().replace("http:", "https:"));
                                    startActivityForResult(i, TWITTER_REQUEST);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case R.id.btn_phone_contacts:
                if (!mIsLoading) {
                    mIsLoading = true;
                    getPhoneContacts();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    private void getPhoneContacts() {
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        String[] contactPhones = ContactsUtils.contactsNumbers(this.getActivity());

        if (contactPhones.length > 0) {
            UsersBL userBl = new UsersBL();
            userBl.setHiveListener(new IHiveResponse() {
                @Override
                public void onError(RetrofitError error) {
                    mProgressHive.dismiss();
                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong),
                            getString(R.string.invite_phone));
                }

                @Override
                public void onResult(HiveResponse response) {
                    try {
                        mProgressHive.dismiss();
                    } catch (Exception e) {
                        Log.d(TAG, "error " + e.getMessage());
                    }

                    if (response.getStatus()) {
                        ArrayList<Follower> phoneContactList;
                        phoneContactList = ((UserNetworkResponse) response).getData();

                        if (phoneContactList != null && phoneContactList.size() > 0) {
                            SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
                            Bundle bundle;
                            bundle = new Bundle();
                            bundle.putString(AppConstants.USER_NETWORK, getString(R.string.phone_contacts_tile));
                            bundle.putParcelableArrayList(AppConstants.FOLLOW_LIST, phoneContactList);
                            socialNetworkFragment.setArguments(bundle);
                            mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, socialNetworkFragment).commit();
                        } else {
                            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.invite_phone));
                        }
                    } else {
                        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), response.getMessageError());
                    }
                }
            });
            userBl.mergeFromPhones(mUserPrefs.getToken(), mUserPrefs.getId_user() + "", contactPhones);
        } else {
            mIsLoading = false;
            mProgressHive.dismiss();
            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.invite_phone));
        }

    }

    private void getFriendsFacebook() {
        mProgressHive.show(mFragmentManager, "");
        mProgressHive.setCancelable(false);

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            requestFacebookFriends(session);
        } else {
            startFacebookLogin();
        }
    }

    private void requestFacebookFriends(Session session) {
        Request request = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {

                String[] idsFacebook = new String[users.size()];

                if (users.size() > 0) {
                    for (int i = 0; i < users.size(); i++) {
                        idsFacebook[i] = users.get(i).getId();
                    }

                    UsersBL userBl = new UsersBL();
                    userBl.setHiveListener(new IHiveResponse() {
                        @Override
                        public void onError(RetrofitError error) {
                            if (mProgressHive.isVisible()) {
                                mProgressHive.dismiss();
                            }
                            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.problem_open_facebook));
                        }

                        @Override
                        public void onResult(HiveResponse response) {
                            //mProgressHive.dismiss();
                            if (response.getStatus()) {
                                ArrayList<Follower> facebookUserList;
                                facebookUserList = ((UserNetworkResponse) response).getData();

                                if (facebookUserList.size() > 0) {
                                    SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
                                    Bundle bundle;
                                    bundle = new Bundle();
                                    bundle.putString(AppConstants.USER_NETWORK, getString(R.string.facebook_friends_title));
                                    bundle.putParcelableArrayList(AppConstants.FOLLOW_LIST, facebookUserList);
                                    socialNetworkFragment.setArguments(bundle);

                                    if (mProgressHive.isVisible()) {
                                        mProgressHive.dismiss();
                                    }

                                    mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, socialNetworkFragment).commit();
                                } else {
                                    if (mProgressHive.isVisible()) {
                                        mProgressHive.dismiss();
                                    }
                                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong),
                                            getString(R.string.invite_facebook));
                                }
                            } else {
                                logoutFacebook();
                                if (mProgressHive.isVisible()) {
                                    mProgressHive.dismiss();
                                }
                                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), response.getMessageError());
                            }
                        }
                    });
                    userBl.mergeFromFacebook(mUserPrefs.getToken(), mUserPrefs.getId_user() + "", mFacebookId, idsFacebook);
                } else {
                    if (mProgressHive.isVisible()) {
                        mProgressHive.dismiss();
                    }
                    ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong),
                            getString(R.string.invite_facebook));
                }
            }
        });
        request.executeAsync();
    }


    private void startFacebookLogin() {
        Session.openActiveSession(getActivity(), this, true, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FollowPeopleFragment.TWITTER_REQUEST && resultCode == Activity.RESULT_OK) {
            handleTwitterResult(data);
        }
    }

    private boolean handleTwitterResult(Intent data) {
        String verifier = (String) data.getExtras().get(AppConstants.URL_TWITTER_OAUTH_VERIFIER);
        User userPreferences = ManagePreferences.getUserPreferences();

        try {
            mProgressHive.show(mFragmentManager, "");
            mProgressHive.setCancelable(false);
            // get access token
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            userPreferences.setToken_twitter(accessToken.getToken());
            userPreferences.setToken_twittersecret(accessToken.getToken());
            ManagePreferences.setPreferencesUser(userPreferences);

            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(AppConstants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(AppConstants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.commit();
            Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
            Log.e("Twitter Token Secret", "> " + accessToken.getTokenSecret());
            long userID = accessToken.getUserId();

            Log.e("user", String.valueOf(accessToken.getUserId()));

            long lCursor = -1;
            IDs friendsIDs = twitter.getFriendsIDs(userID, lCursor);

            List<String> idsTwitterList = new ArrayList<String>();
            do {
                for (long i : friendsIDs.getIDs()) {
                    idsTwitterList.add(i + "");
                }
            } while (friendsIDs.hasNext());

            if (idsTwitterList.size() > 0) {
                UsersBL userBl = new UsersBL();
                userBl.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        //mProgressHive.dismiss();
                        ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.problem_open_twitter));
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        if (response.getStatus()) {
                            ArrayList<Follower> twitterUserList;
                            twitterUserList = ((UserNetworkResponse) response).getData();

                            if (twitterUserList.size() > 0) {
                                SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
                                Bundle bundle;
                                bundle = new Bundle();
                                bundle.putString(AppConstants.USER_NETWORK, getString(R.string.twitter_friends_title));
                                bundle.putParcelableArrayList(AppConstants.FOLLOW_LIST, twitterUserList);
                                socialNetworkFragment.setArguments(bundle);

                                if (mProgressHive.isVisible()) {
                                    mProgressHive.dismiss();
                                }

                                mFragmentManager.beginTransaction().addToBackStack("").replace(R.id.content_frame, socialNetworkFragment).commit();
                            } else {
                                if (mProgressHive.isVisible()) {
                                    mProgressHive.dismiss();
                                }
                                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong),
                                        getString(R.string.invite_twitter));
                            }
                        } else {
                            if (mProgressHive.isVisible()) {
                                mProgressHive.dismiss();
                            }
                            ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), response.getMessageError());
                        }

                    }
                });
                userBl.mergeFromTwitter(userPreferences.getToken(), userPreferences.getId_user() + "", userID + "",
                        idsTwitterList.toArray(new String[idsTwitterList.size()]));
            } else {
                ErrorDialog.showErrorMessage(getActivity(), getString(R.string.something_wrong), getString(R.string.invite_twitter));
            }
            return true;

        } catch (Exception e) {
            Log.e("Twitter Login Error", ">" + e.getMessage());
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void logoutFacebook() {
        try {
            Session session = Session.getActiveSession();
            if (session != null && !session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            requestFacebookFriends(session);
        } else if (state.isClosed()) {
            mProgressHive.dismiss();
        }
    }
}
