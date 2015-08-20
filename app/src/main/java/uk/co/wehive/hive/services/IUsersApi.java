/*******************************************************************************
 PROJECT:       Hive
 FILE:          IUsersApi.java
 DESCRIPTION:   Services for users api.
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import uk.co.wehive.hive.entities.response.ArtistsResponse;
import uk.co.wehive.hive.entities.response.ExistEmailUsernameResponse;
import uk.co.wehive.hive.entities.response.FollowUserResponse;
import uk.co.wehive.hive.entities.response.GoodTimesDetailResponse;
import uk.co.wehive.hive.entities.response.GoodTimesResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LoginResponse;
import uk.co.wehive.hive.entities.response.NewsFeedResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;

public interface IUsersApi {

    final static String urlLogin = "/Users/login.json";
    final static String urlSingUpRegister = "/Users/singUpRegister.json";
    final static String urlForgotPassword = "/Users/forgotPassword.json";
    final static String urlExistUsername = "/Users/existUsername.json";
    final static String urlExistEmail = "/Users/existEmail.json";
    final static String urlSingUpFacebook = "/Users/singUpFacebook.json";
    final static String urlLogout = "/Users/logout.json";
    final static String urlLoginFacebook = "/Users/loginFacebook.json";
    final static String urlProfile = "/Users/profile.json";
    final static String urlGoodTimes = "/Users/goodTimes.json";
    final static String urlSaveProfile = "/Users/saveProfile.json";
    final static String urlChangePassword = "/Users/changePassword.json";
    final static String urlFollowers = "/Users/followers.json";
    final static String urlFollowing = "/Users/following.json";
    final static String urlBlock = "/Users/block.json";
    final static String urlChangeAutoCheckin = "/Users/changeAutoCheckin.json";
    final static String urlMergeFromFacebook = "/Users/mergeFromFacebook.json";
    final static String urlMergeFromTwitter = "/Users/mergeFromTwitter.json";
    final static String urlMergeFromPhones = "/Users/mergeFromPhones.json";
    final static String urlSearch = "/Users/search.json";
    final static String urlFollowUser = "/Users/followUser.json";
    final static String urlGoodtimedetail = "/Users/goodtimedetail.json";
    final static String urlUnblock = "/Users/unblock.json";
    final static String urlGetAutoCheckin = "/Users/getAutoCheckin.json";
    final static String urlSaveFacebook = "/Users/saveFacebook.json";
    final static String urlNewsfeed = "/Users/newsfeed.json";
    final static String urlReport = "/Users/report.json";
    final static String urlSaveTwitter = "/Users/saveTwitter.json";
    final static String urlSavePhone = "/Users/savePhone.json";
    final static String urlCheckinbycoords = "/Users/checkinbycoords.json";
    final static String urlSearchArtists = "/Users/searchArtists.json";
    final static String urlDisconnectTwitter = "/Users/disconnectTwitter.json";

    @FormUrlEncoded
    @POST(urlLogin)
    void login(@Field("email") String email, @Field("password") String password,
               @Field("device_token") String token, @Field("imei") String imei,
               @Field("os") String os, Callback<LoginResponse> response);

    @Multipart
    @POST(urlSingUpRegister)
    void singUpPhoto(@Part("email") String email, @Part("password") String password,
                     @Part("first_name") String firstName, @Part("last_name") String lastName,
                     @Part("username") String username, @Part("code_city") String code_city,
                     @Part("phone") String phone, @Part("imei") String imei,
                     @Part("photo") TypedFile photoData, Callback<LoginResponse> response);

    @GET(urlForgotPassword)
    void forgotPassword(@Query("email") String email, Callback<HiveResponse> response);

    @GET(urlExistUsername)
    void existUsername(@Query("username") String username, Callback<ExistEmailUsernameResponse> response);

    @GET(urlExistEmail)
    void existEmail(@Query("email") String email, Callback<ExistEmailUsernameResponse> response);

    @FormUrlEncoded
    @POST(urlSingUpFacebook)
    void signUpFacebook(@Field("email") String email, @Field("id_facebook") String id_facebook,
                        @Field("first_name") String first_name, @Field("last_name") String last_name,
                        @Field("username_facebook") String username_facebook,
                        @Field("link_facebook") String link_facebook, @Field("photo") String photo,
                        @Field("code_city") String code_city, @Field("username") String username,
                        @Field("password") String password, @Field("device_token") String device_token,
                        @Field("imei") String imei, @Field("phone") String phone,
                        Callback<LoginResponse> response);

    @GET(urlLogout)
    void logOut(@Header("Authorization") String authorization, @Query("id_user") String id_user, Callback<HiveResponse> response);

    @GET(urlLoginFacebook)
    void loginFacebook(@Query("id_facebook") String id_facebook, @Query("email") String email,
                       @Query("device_token") String device_token, @Query("imei") String imei,
                       @Query("os") String os, Callback<LoginResponse> response);

    @GET(urlProfile)
    void profile(@Header("Authorization") String authorization,
                 @Query("id_response") String responseId, @Query("username") String username,
                 @Query("id_user") String userId, Callback<LoginResponse> response);

    @GET(urlGoodTimes)
    void getGoodTimes(@Header("Authorization") String authorization,
                      @Query("id_user") String id_user, @Query("start") String start, @Query("amount") String amount, Callback<GoodTimesResponse> response);

    @Multipart
    @POST(urlSaveProfile)
    void saveProfile(@Header("Authorization") String authorization,
                     @Part("id_user") String id_user, @Part("first_name") String first_name, @Part("last_name") String last_name,
                     @Part("username") String username, @Part("photo") TypedFile photoData,
                     @Part("bio") String bio, @Part("code_city") String code_city,
                     @Part("phone") String phone, Callback<HiveResponse> response);

    @FormUrlEncoded
    @PUT(urlChangePassword)
    void changePassword(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                        @Field("password") String password,
                        @Field("new_password") String new_password, Callback<HiveResponse> response);

    @GET(urlFollowers)
    void getFollowers(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                      @Query("id_response") String id_response, @Query("start") String start,
                      @Query("amount") String amount, Callback<UserNetworkResponse> response);

    @GET(urlFollowing)
    void getFollowing(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("id_response") String id_response,
                      @Query("start") String start,
                      @Query("amount") String amount, Callback<UserNetworkResponse> response);

    @FormUrlEncoded
    @POST(urlBlock)
    void blockUser(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                   @Field("id_user_blocked") String id_user_blocked, Callback<HiveResponse> response);

    @FormUrlEncoded
    @PUT(urlChangeAutoCheckin)
    void changeAutoCheckin(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                           @Field("active") String active, Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlMergeFromFacebook)
    void mergeFromFacebook(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                           @Field("id_facebook") String id_facebook, @Field("ids_facebook[]") String[] ids_facebook,
                           Callback<UserNetworkResponse> response);

    @FormUrlEncoded
    @POST(urlMergeFromTwitter)
    void mergeFromTwitter(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_twitter") String id_twitter,
                          @Field("ids_twitter[]") String[] ids_twitter,
                          Callback<UserNetworkResponse> response);

    @FormUrlEncoded
    @POST(urlMergeFromPhones)
    void mergeFromPhones(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                         @Field("phones[]") String[] phones, Callback<UserNetworkResponse> response);

    @GET(urlSearch)
    void searchUsers(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                     @Query("start") String start, @Query("amount") String amount,
                     @Query("criteria") String criteria, Callback<UserNetworkResponse> response);

    @FormUrlEncoded
    @POST(urlFollowUser)
    void followUser(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                    @Field("id_user_follow") String id_user_follow,
                    @Field("active") String active, Callback<FollowUserResponse> response);

    @GET(urlGoodtimedetail)
    void getGoodTimeDetail(@Header("Authorization") String authorization, @Query("id_goodtime") String id_goodtime, @Query("id_response") String id_response,
                           @Query("id_user") String id_user, @Query("start") String start,
                           @Query("amount") String amount, Callback<GoodTimesDetailResponse> response);

    @FormUrlEncoded
    @POST(urlUnblock)
    void unblockUser(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                     @Field("id_user_blocked") String id_user_blocked, Callback<HiveResponse> response);


    @FormUrlEncoded
    @POST(urlCheckinbycoords)
    void checkinbycoords(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("latitude") String latitude,
                         @Field("longitude") String longitude, Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlSaveFacebook)
    void saveFacebook(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_facebook") String id_facebook,
                      @Field("username_facebook") String username_facebook,
                      @Field("link_facebook") String link_facebook, Callback<HiveResponse> response);

    @FormUrlEncoded
    @POST(urlSaveTwitter)
    void saveTwitter(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_twitter") String id_twitter,
                     @Field("username_twitter") String username_twitter, Callback<HiveResponse> response);

    @GET(urlSearchArtists)
    void searchArtists(@Header("Authorization") String authorization, @Query("id_user") String id_user,
                       @Query("start") String start, @Query("amount") String amount,
                       @Query("criteria") String criteria, Callback<ArtistsResponse> response);

    @FormUrlEncoded
    @POST(urlDisconnectTwitter)
    void disconnectTwitter(@Header("Authorization") String authorization, @Field("id_user") String id_user, Callback<HiveResponse> response);

    @FormUrlEncoded
    @PUT(urlReport)
    void reportUser(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                    @Field("id_user_reported") String id_user_reported,
                    @Field("reason") String reason, Callback<HiveResponse> response);

    @GET(urlNewsfeed)
    void newsFeed(@Header("Authorization") String authorization, @Query("id_user") String userId, @Query("start") String start,
                  @Query("amount") String amount, Callback<NewsFeedResponse> response);
}
