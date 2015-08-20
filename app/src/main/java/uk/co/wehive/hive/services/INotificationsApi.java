package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.NotificationsListResponse;
import uk.co.wehive.hive.entities.response.NotificationsResponse;

public interface INotificationsApi {

    final static String urlConfigCommentMyPost = "/Notifications/configCommentMyPost.json";
    final static String urlConfigMentionMode = "/Notifications/configMentionMe.json";
    final static String urlConfigFollowMe = "/Notifications/configFollowMe.json";
    final static String urlConfigLikeMyPost = "/Notifications/configLikeMyPost.json";
    final static String urlConfigSharedMyPost = "/Notifications/configSharedMyPost.json";
    final static String urlConfigNewConcertAnnounced = "/Notifications/configNewConcertAnnounced.json";
    final static String urlConfigArtistPost = "/Notifications/configArtistsPost.json";
    final static String urlGetConfigNotifications = "/Notifications/getConfigNotifications.json";
    final static String urlList = "/Notifications/list.json";
    final static String urlReaded = "/Notifications/readed.json";
    final static String urlReminderEvent = "/Notifications/configReminderEvent.json";
    final static String urlConfigGalleryAvailable = "Notifications/configGalleryAviable.json";

    @FormUrlEncoded
    @POST(urlConfigCommentMyPost)
    void configCommentMyPost(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                             Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigMentionMode)
    void configMentionMode(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                           Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigFollowMe)
    void configFollowMe(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                        Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigLikeMyPost)
    void configLikeMyPost(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                          Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigSharedMyPost)
    void configSharedMyPost(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                            Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigNewConcertAnnounced)
    void configNewConcertAnnounced(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                                   @Field("value") String value, Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigArtistPost)
    void configArtistPost(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                          Callback<HiveResponse> response);

    @GET(urlGetConfigNotifications)
    void getConfigNotifications(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                                Callback<NotificationsListResponse> response);

    @GET(urlList)
    void list(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("start") String start,
              @Query("amount") String amount, Callback<NotificationsResponse> response);

    @FormUrlEncoded
    @POST(urlReaded)
    void readed(@Header("Authorization") String authorization, @Field("id_notification") String id_notification, Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlReminderEvent)
    void reminderEvent(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                       Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlConfigGalleryAvailable)
    void configGalleryAvailable(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("value") String value,
                                Callback<HiveResponse> response);

}
