package uk.co.wehive.hive.services;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;
import uk.co.wehive.hive.entities.response.CreateCommentResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;

public interface ICommentsApi {

    final static String urlCreate = "/Comments/create.json";
    final static String urlLike = "/Comments/like.json";
    final static String urlDelete = "/Comments/delete.json";

    @FormUrlEncoded
    @POST(urlCreate)
    void create(@Header("Authorization") String authorization, @Field("id_user") String userId, @Field("origin") String origin,
                @Field("comment") String comment, @Field("id_origin") String originId,
                @Field("mentions") String mentions, Callback<CreateCommentResponse> hiveResponse);

    @FormUrlEncoded
    @POST(urlLike)
    void like(@Header("Authorization") String authorization, @Field("id_user") String userId, @Field("id_comment") String commentId,
              @Field("active") String active, Callback<HiveResponse> hiveResponse);

    @FormUrlEncoded
    @POST(urlDelete)
    void delete(@Header("Authorization") String authorization, @Field("id_comment") String commentId,
                Callback<HiveResponse> hiveResponse);
}