package uk.co.wehive.hive.bl;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.CreateCommentResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.ICommentsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class CommentsBL {

    private ICommentsApi mCommentsService;
    private IHiveResponse mHiveResponseListener;

    public CommentsBL() {
        this.mCommentsService = ApplicationHive.getRestAdapter().create(ICommentsApi.class);
    }

    public void setListener(IHiveResponse mHiveResponseListener) {
        this.mHiveResponseListener = mHiveResponseListener;
    }

    public void createComment(String token, String userId, String origin, String comment, String originId, String mentions) {

        mCommentsService.create(token, userId, origin, comment, originId, mentions, new Callback<CreateCommentResponse>() {
            @Override
            public void success(CreateCommentResponse createCommentResponse, Response response) {
                mHiveResponseListener.onResult(createCommentResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void like(String token,String userId, String commentId, String active) {
        mCommentsService.like(token, userId, commentId, active, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                mHiveResponseListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void deleteComment(String token,String commentId) {
        mCommentsService.delete(token, commentId, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                mHiveResponseListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }
}
