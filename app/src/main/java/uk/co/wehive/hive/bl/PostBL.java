package uk.co.wehive.hive.bl;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import uk.co.wehive.hive.entities.response.CommentsResponse;
import uk.co.wehive.hive.entities.response.CreatePostResponse;
import uk.co.wehive.hive.entities.response.HiveResponse;
import uk.co.wehive.hive.entities.response.LikePostResponse;
import uk.co.wehive.hive.entities.response.PostDetailsResponse;
import uk.co.wehive.hive.entities.response.PostLikersResponse;
import uk.co.wehive.hive.entities.response.PostResponse;
import uk.co.wehive.hive.entities.response.VideoUploadResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.services.IPostApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class PostBL {

    private IPostApi mPostService;
    private IHiveResponse mHiveResponseListener;

    public PostBL() {
        this.mPostService = ApplicationHive.getRestAdapter().create(IPostApi.class);
    }

    public void setListener(IHiveResponse listener) {
        this.mHiveResponseListener = listener;
    }

    public void addLike(String token, String id_user, String id_post, String active) {
        mPostService.addLike(token, id_user, id_post, active, new Callback<LikePostResponse>() {
            @Override
            public void success(LikePostResponse postResponse, Response response) {
                mHiveResponseListener.onResult(postResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void addShare(String token, String id_user, String id_post) {
        mPostService.addShare(token, id_user, id_post, new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                mHiveResponseListener.onResult(postResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void newsFeed(String token, String postId, String start, String amount, String userId) {
        mPostService.newsfeed(token, postId, start, amount, userId, new Callback<CommentsResponse>() {
            @Override
            public void success(CommentsResponse commentsResponse, Response response) {
                mHiveResponseListener.onResult(commentsResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void getDetails(String token, String id_user, String id_post) {
        mPostService.getDetails(token, id_user, id_post, new Callback<PostDetailsResponse>() {
            @Override
            public void success(PostDetailsResponse postResponse, Response response) {
                mHiveResponseListener.onResult(postResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void createPost(String token, String userId, String postDescription, String eventId, String media,
                           String mentions) {
        mPostService.create(token, userId, postDescription, eventId, media, mentions, new Callback<CreatePostResponse>() {
            @Override
            public void success(CreatePostResponse createPostResponse, Response response) {
                mHiveResponseListener.onResult(createPostResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void uploadPostMedia(String token, String userId, String postId, File photo, String thumbnail) {
        TypedFile photoData = null;
        if (photo != null) {
            photoData = new TypedFile("image/jpeg", photo);
        }
        mPostService.uploadPostMedia(token, userId, postId, photoData, null, new Callback<HiveResponse>() {
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

    public void uploadPostVideo(String token, String userId, String postId, File video, File thumbnail) {
        TypedFile videoData = null;
        if (video != null) {
            videoData = new TypedFile("video/mp4", video);
        }

        TypedFile photoData = null;
        if (thumbnail != null) {
            photoData = new TypedFile("image/jpeg", thumbnail);
        }

        mPostService.uploadPostVideo(token, userId, postId, videoData, photoData, new Callback<VideoUploadResponse>() {
            @Override
            public void success(VideoUploadResponse createPostResponse, Response response) {
                mHiveResponseListener.onResult(createPostResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }

    public void deletePost(String token, String userId, String postId) {
        mPostService.deletePost(token, userId, postId, new Callback<HiveResponse>() {
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

    public void reportPost(String token, String userId, String postId, String issues) {
        mPostService.reportPost(token, userId, postId, issues, new Callback<HiveResponse>() {
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

    public void report(String id_user, String token, String id_post, String issues) {
        mPostService.report(id_user, token, id_post, issues, new Callback<HiveResponse>() {
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

    public void postsLikers(String token, String id_user, String id_post, String start, String amount) {
        mPostService.postLikers(token, id_user, id_post, start, amount, new Callback<PostLikersResponse>() {
            @Override
            public void success(PostLikersResponse postLikersResponse, Response response) {
                mHiveResponseListener.onResult(postLikersResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                mHiveResponseListener.onError(error);
            }
        });
    }
}