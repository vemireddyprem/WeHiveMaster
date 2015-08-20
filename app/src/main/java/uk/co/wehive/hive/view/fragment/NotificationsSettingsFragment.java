package uk.co.wehive.hive.view.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import retrofit.RetrofitError;
import uk.co.wehive.hive.R;
import uk.co.wehive.hive.bl.NotificationsBL;
import uk.co.wehive.hive.entities.NotificationList;
import uk.co.wehive.hive.entities.User;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.NotificationsListResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.utils.AppConstants;
import uk.co.wehive.hive.utils.CustomViewHelper;
import uk.co.wehive.hive.utils.ManagePreferences;
import uk.co.wehive.hive.view.dialog.ProgressHive;

public class NotificationsSettingsFragment extends GATrackerFragment implements IHiveResponse,
        View.OnClickListener {

    private ProgressHive mProgressHive;
    private CheckBox chkWhenFollowMe;
    private CheckBox chkWhenCommentMyPost;
    private CheckBox chkWhenMentionInPost;
    private CheckBox chkWhenNewConcert;
    private CheckBox chlWhenLikeInPost;
    private CheckBox chkWhenShareMyPost;
    private CheckBox chkWhenTrackedArtistPost;
    private CheckBox chkWhenGoodTimesGalleryReady;
    private CheckBox chkWhenEventAboutToStart;
    private NotificationsBL mNotificationsBL;
    private User mUserPrefs;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);
        initViewComponents();
        CustomViewHelper.setUpActionBar(getActivity(), getString(R.string.notificationsettings));

        mProgressHive = new ProgressHive();
        mProgressHive.show(fm, "");
        mProgressHive.setCancelable(false);

        loadConfiguration();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications_settings, container, false);
    }

    private void initViewComponents() {
        chkWhenFollowMe = (CheckBox) getView().findViewById(R.id.chkWhenFollowMe);
        chkWhenCommentMyPost = (CheckBox) getView().findViewById(R.id.chkWhenCommentMyPost);
        chkWhenMentionInPost = (CheckBox) getView().findViewById(R.id.chkWhenMentionInPost);
        chkWhenNewConcert = (CheckBox) getView().findViewById(R.id.chkWhenNewConcert);
        chlWhenLikeInPost = (CheckBox) getView().findViewById(R.id.chlWhenLikeInPost);
        chkWhenShareMyPost = (CheckBox) getView().findViewById(R.id.chkWhenShareMyPost);
        chkWhenTrackedArtistPost = (CheckBox) getView().findViewById(R.id.chkWhenTrackedArtistPost);
        chkWhenGoodTimesGalleryReady = (CheckBox) getView().findViewById(R.id.chkWhenGoodTimesGalleryReady);
        chkWhenEventAboutToStart = (CheckBox) getView().findViewById(R.id.chkWhenEventAboutToStart);

        chkWhenFollowMe.setOnClickListener(this);
        chkWhenCommentMyPost.setOnClickListener(this);
        chkWhenMentionInPost.setOnClickListener(this);
        chkWhenNewConcert.setOnClickListener(this);
        chlWhenLikeInPost.setOnClickListener(this);
        chkWhenShareMyPost.setOnClickListener(this);
        chkWhenTrackedArtistPost.setOnClickListener(this);
        chkWhenGoodTimesGalleryReady.setOnClickListener(this);
        chkWhenEventAboutToStart.setOnClickListener(this);

        mUserPrefs = ManagePreferences.getUserPreferences();
    }

    private void loadConfiguration() {
        mNotificationsBL = new NotificationsBL();
        mNotificationsBL.setHiveListener(this);
        mNotificationsBL.getConfigNotifications(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()));
    }

    @Override
    public void onError(RetrofitError error) {
        mProgressHive.dismiss();
        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(HiveResponse response) {
        NotificationList data = ((NotificationsListResponse) response).getData();
        chkWhenFollowMe.setChecked(data.getFollow_me().equals(AppConstants.TRUE));
        chkWhenCommentMyPost.setChecked(data.getComment_my_post().equals(AppConstants.TRUE));
        chkWhenMentionInPost.setChecked(data.getMention_me().equals(AppConstants.TRUE));
        chkWhenNewConcert.setChecked(data.getNew_concert_announced().equals(AppConstants.TRUE));
        chlWhenLikeInPost.setChecked(data.getLike_my_post().equals(AppConstants.TRUE));
        chkWhenShareMyPost.setChecked(data.getShared_my_post().equals(AppConstants.TRUE));
        chkWhenTrackedArtistPost.setChecked(data.getArtists_post().equals(AppConstants.TRUE));
        chkWhenGoodTimesGalleryReady.setChecked(data.getGallery_aviable().equals(AppConstants.TRUE));
        chkWhenEventAboutToStart.setChecked(data.getReminder_event().equals(AppConstants.TRUE));
        mProgressHive.dismiss();
    }

    @Override
    public void onClick(View view) {
        mNotificationsBL = new NotificationsBL();

        switch (view.getId()) {
            case R.id.chkWhenFollowMe:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenFollowMe.setChecked(!chkWhenFollowMe.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });
                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configFollowMe(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenFollowMe.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenCommentMyPost:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenCommentMyPost.setChecked(!chkWhenCommentMyPost.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configCommentMyPost(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenCommentMyPost.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenMentionInPost:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenMentionInPost.setChecked(!chkWhenMentionInPost.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configMentionMode(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenMentionInPost.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenNewConcert:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenNewConcert.setChecked(!chkWhenNewConcert.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configNewConcertAnnounced(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenNewConcert.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chlWhenLikeInPost:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chlWhenLikeInPost.setChecked(!chlWhenLikeInPost.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configLikeMyPost(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chlWhenLikeInPost.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenShareMyPost:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenShareMyPost.setChecked(!chkWhenShareMyPost.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configSharedMyPost(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenShareMyPost.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenTrackedArtistPost:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenTrackedArtistPost.setChecked(!chkWhenTrackedArtistPost.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configArtistPost(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenTrackedArtistPost.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenGoodTimesGalleryReady:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenGoodTimesGalleryReady.setChecked(!chkWhenGoodTimesGalleryReady.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.configGalleryAvailable(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenGoodTimesGalleryReady.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;

            case R.id.chkWhenEventAboutToStart:
                mNotificationsBL.setHiveListener(new IHiveResponse() {
                    @Override
                    public void onError(RetrofitError error) {
                        chkWhenEventAboutToStart.setChecked(!chkWhenEventAboutToStart.isChecked());
                        mProgressHive.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.toast_error_updating), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResult(HiveResponse response) {
                        mProgressHive.dismiss();
                    }
                });

                mProgressHive.show(getActivity().getSupportFragmentManager(), "");
                mNotificationsBL.reminderEvent(mUserPrefs.getToken(), String.valueOf(mUserPrefs.getId_user()),
                        chkWhenEventAboutToStart.isChecked() ? AppConstants.TRUE : AppConstants.FALSE);
                break;
        }
    }
}
