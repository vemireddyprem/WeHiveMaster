package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import uk.co.wehive.hive.entities.response.EventDetailResponse;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.EventsUserResponse;
import uk.co.wehive.hive.entities.response.MediaListResponse;
import uk.co.wehive.hive.entities.response.MoreEventDetailResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;

public interface IEventsApi {

    final static String urlPastEvents = "/Events/pastByUser.json";
    final static String urlUpComingEvents = "/Events/upcomingByUser.json";
    final static String urlSearch = "/Events/search.json";
    final static String urlSearchNearYou = "/Events/searchNearYou.json";
    final static String urlSearchRecommended = "/Events/searchRecommended.json";
    final static String urlDetail = "/Events/detail.json";
    final static String urlDetailMore = "/Events/detailmore.json";
    final static String urlNewsfeed = "/Events/newsfeed.json";
    final static String urlTrack = "/Events/track.json";
    final static String urlCheckin = "/Events/checkin.json";
    final static String urlBuzzing = "/Events/buzzing.json";
    final static String urlTrackedBy = "/Events/trackedBy.json";
    final static String urlMedialist = "/Events/medialist.json";
    final static String urlLive = "/Events/live.json";
    final static String urlTrending = "/Events/trending.json";
    final static String urlFollowers = "/Events/followers.json";

    @GET(urlPastEvents)
    void getPastEvents(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                       @Query("start") String start, @Query("amount") String amount,
                       Callback<EventsUserResponse> response);

    @GET(urlUpComingEvents)
    void getUpComingEvents(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                           @Query("start") String start, @Query("amount") String amount,
                           Callback<EventsUserResponse> response);

    @GET(urlSearchNearYou)
    void searchNearYou(@Query("id_user") String userId, @Query("code_city") String cityId,
                       @Query("start") String start, @Query("amount") String amount,
                       @Query("longitude") String longitude, @Query("latitude") String latitude,
                       Callback<EventsUserResponse> response);

    @GET(urlSearchRecommended)
    void searchRecommended(@Query("start") String start, @Query("amount") String amount,
                           @Query("id_user") String userId, Callback<EventsUserResponse> response);

    @GET(urlTrending)
    void getTrending(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("start") String start,
                     @Query("amount") String amount, Callback<EventsUserResponse> response);

    @GET(urlDetail)
    void getDetail(@Query("id_event") String id_event, @Query("id_user") String id_user,
                   @Query("latitude") String latitude,
                   @Query("longitude") String longitude,
                   Callback<EventDetailResponse> response);

    @GET(urlDetailMore)
    void getMoreDetail(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("id_event") String id_event,
                       Callback<MoreEventDetailResponse> response);

    @GET(urlNewsfeed)
    void getNewsfeed(@Query("id_event") String id_event, @Query("id_user") String id_user,
                     @Query("start") String start,
                     @Query("amount") String amount, Callback<EventPostsResponse> response);

    @GET(urlLive)
    void getLive(@Query("id_event") String id_event, @Query("id_user") String id_user,
                 @Query("start") String start,
                 @Query("amount") String amount, Callback<EventPostsResponse> response);

    @GET(urlSearch)
    void searchEvents(@Query("id_artist") String artistId, @Query("location") String location,
                      @Query("location_type") String locationType, @Query("start_date") String startDate,
                      @Query("end_date") String endDate, @Query("start") String start,
                      @Query("amount") String amount, Callback<EventsUserResponse> response);

    @GET(urlBuzzing)
    void getBuzzing(@Query("id_event") String id_event, @Query("id_user") String id_user,
                    @Query("start") String start,
                    @Query("amount") String amount, Callback<EventPostsResponse> response);

    @FormUrlEncoded
    @POST(urlTrack)
    void track(@Header("Authorization") String authorization, @Field("id_event_follow") String id_event_follow, @Field("id_user") String id_user,
               @Field("active") String active, Callback<EventDetailResponse> response);

    @FormUrlEncoded
    @POST(urlCheckin)
    void checkin(@Header("Authorization") String authorization, @Field("id_event") String id_event, @Field("id_user") String id_user,
                 @Field("active") String active, Callback<EventDetailResponse> response);

    @GET(urlMedialist)
    void getMediaList(@Header("Authorization") String authorization, @Query("id_user") String userId,
                      @Query("id_event") String eventId, @Query("start") String start,
                      @Query("amount") String amount, Callback<MediaListResponse> response);

    @GET(urlTrackedBy)
    void getTrackedBy(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                      @Query("id_event") String id_event, @Query("start") String start,
                      @Query("amount") String amount, Callback<UserNetworkResponse> response);

    @GET(urlFollowers)
    void getFollowers(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("id_event") String id_event,
                      @Query("start") String start, @Query("amount") String amount,
                      Callback<UserNetworkResponse> response);
}
