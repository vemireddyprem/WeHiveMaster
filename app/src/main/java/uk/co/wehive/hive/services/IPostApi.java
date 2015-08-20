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
import uk.co.wehive.hive.entities.response.CommentsResponse;
import uk.co.wehive.hive.entities.response.CreatePostResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LikePostResponse;
import uk.co.wehive.hive.entities.response.PostDetailsResponse;
import uk.co.wehive.hive.entities.response.PostLikersResponse;
import uk.co.wehive.hive.entities.response.PostResponse;
import uk.co.wehive.hive.entities.response.VideoUploadResponse;

public interface IPostApi {

    static final String urlLike = "/Posts/like.json";
    static final String urlShare = "/Posts/share.json";
    static final String urlDetail = "/Posts/detail.json";
    static final String urlNewsFeed = "/Posts/newsfeed.json";
    static final String urlCreate = "/Posts/create.json";
    static final String urlUploadPostMedia = "/Posts/uploadpostmedia.json";
    static final String urlListLikes = "/Posts/listlikes.json";
    static final String urlDeletePost = "/Posts/deletePost.json";
    static final String urlReport = "/Posts/report.json";

    @FormUrlEncoded
    @POST(urlLike)
    void addLike(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_post") String id_post,
                 @Field("active") String active, Callback<LikePostResponse> response);

    @FormUrlEncoded
    @POST(urlShare)
    void addShare(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_post") String id_post,
                  Callback<PostResponse> response);

    @GET(urlDetail)
    void getDetails(@Header("Authorization") String authorization, @Query("id_user") String id_user, @Query("id_post") String id_post,
                    Callback<PostDetailsResponse> response);

    @GET(urlNewsFeed)
    void newsfeed(@Header("Authorization") String authorization, @Query("id_post") String postId, @Query("start") String start,
                  @Query("amount") String amount, @Query("id_user") String userId,
                  Callback<CommentsResponse> response);

    @FormUrlEncoded
    @POST(urlCreate)
    void create(@Header("Authorization") String authorization, @Field("id_user") String userId, @Field("text") String postDescription,
                @Field("id_event") String eventId, @Field("media") String media,
                @Field("mentions") String mentions, Callback<CreatePostResponse> response);

    @Multipart
    @POST(urlUploadPostMedia)
    void uploadPostMedia(@Header("Authorization") String authorization, @Part("id_user") String userId, @Part("id_post") String postId,
                         @Part("file") TypedFile file, @Part("thumbnail") String thumbnail,
                         Callback<HiveResponse> response);

    @Multipart
    @POST(urlUploadPostMedia)
    void uploadPostVideo(@Header("Authorization") String authorization, @Part("id_user") String userId, @Part("id_post") String postId,
                         @Part("file") TypedFile file, @Part("thumbnail") TypedFile thumbnail,
                         Callback<VideoUploadResponse> response);

    @FormUrlEncoded
    @POST(urlDeletePost)
    void deletePost(@Header("Authorization") String authorization, @Field("id_user") String id_user, @Field("id_post") String id_post,
                    Callback<HiveResponse> response);

    @FormUrlEncoded
    @PUT(urlReport)
    void report(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                @Field("id_post") String id_post, @Field("issues") String issues,
                Callback<HiveResponse> response);

    @FormUrlEncoded
    @PUT(urlReport)
    void reportPost(@Header("Authorization") String authorization, @Field("id_user") String id_user,
                    @Field("id_post") String id_post, @Field("issues") String issues,
                    Callback<HiveResponse> response);

    @GET(urlListLikes)
    void postLikers(@Header("Authorization") String authorization, @Query("id_user") String userId, @Query("id_post") String postId,
                    @Query("start") String start, @Query("amount") String amount,
                    Callback<PostLikersResponse> response);
}