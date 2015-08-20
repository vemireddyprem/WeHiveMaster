package uk.co.wehive.hive.bl;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.NotificationsListResponse;
import uk.co.wehive.hive.entities.response.NotificationsResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.services.INotificationsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class NotificationsBL {

    private IHiveResponse mNotificationsListener;
    private INotificationsApi mNotificationServices;
    private Context mContext;

    public NotificationsBL() {
        this.mNotificationServices = ApplicationHive.getRestAdapter().create(INotificationsApi.class);
        mContext = ApplicationHive.getContext();
    }

    public void setHiveListener(IHiveResponse hiveListener) {
        this.mNotificationsListener = hiveListener;
    }

    public void getListNotifications(String token, String userId, String start, String amount) {
        final String fileName = "notifications=" + userId;
        mNotificationServices.list(token, userId, start, amount, new Callback<NotificationsResponse>() {
            @Override
            public void success(NotificationsResponse notificationsResponse, retrofit.client.Response response) {
                PersistenceHelper.saveResponse(mContext, notificationsResponse, fileName);
                mNotificationsListener.onResult(notificationsResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                NotificationsResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, NotificationsResponse.class);
                if (response != null) {
                    mNotificationsListener.onResult(response);
                } else {
                    mNotificationsListener.onError(error);
                }
            }
        });
    }

    public void configCommentMyPost(String token, String id_user, String value) {
        mNotificationServices.configCommentMyPost(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configMentionMode(String token, String id_user, String value) {
        mNotificationServices.configMentionMode(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configFollowMe(String token, String id_user, String value) {
        mNotificationServices.configFollowMe(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configLikeMyPost(String token, String id_user, String value) {
        mNotificationServices.configLikeMyPost(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configSharedMyPost(String token, String id_user, String value) {
        mNotificationServices.configSharedMyPost(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configNewConcertAnnounced(String token, String id_user, String value) {
        mNotificationServices.configNewConcertAnnounced(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configArtistPost(String token, String id_user, String value) {
        mNotificationServices.configArtistPost(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void reminderEvent(String token, String id_user, String value) {
        mNotificationServices.reminderEvent(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void configGalleryAvailable(String token, String id_user, String value) {
        mNotificationServices.configGalleryAvailable(token, id_user, value, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void readed(String token, String id_notification) {
        mNotificationServices.readed(token, id_notification, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }

    public void getConfigNotifications(String token, String id_user) {
        mNotificationServices.getConfigNotifications(token, id_user, new Callback<NotificationsListResponse>() {
            @Override
            public void success(NotificationsListResponse notificationsListResponse, retrofit.client.Response response) {
                mNotificationsListener.onResult(notificationsListResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mNotificationsListener.onError(error);
            }
        });
    }
}
