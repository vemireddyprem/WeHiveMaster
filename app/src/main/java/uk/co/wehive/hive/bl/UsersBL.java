/**********************************************************************************************
 PROJECT:       Hive
 FILE:          UsersBL.java
 DESCRIPTION:
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        11/07/2014  Juan Pablo B.    1. Initial definition.
 1.0        14/07/2014  Marcela Güiza    2. Included Login listener and service implementation.
 **********************************************************************************************/
package uk.co.wehive.hive.bl;

import android.content.Context;
import android.util.Log;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.services.IUsersApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class UsersBL {

    private Context mContext;
    private IHiveResponse hiveListener;
    private IUsersApi mUsersService;
    private final String TAG = "UsersBL";

    public UsersBL() {
        mContext = ApplicationHive.getContext();
        this.mUsersService = ApplicationHive.getRestAdapter().create(IUsersApi.class);
    }

    public void setHiveListener(IHiveResponse hiveListener) {
        this.hiveListener = hiveListener;
    }

    public void login(String userName, String password, String token, String imei, String os) {
        mUsersService.login(userName, password, token, imei, os, new Callback<LoginResponse>() {

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                Log.i(TAG, loginResponse.toString());
                hiveListener.onResult(loginResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, "error login");
                hiveListener.onError(error);
            }
        });
    }

    public void loginFacebook(String id_facebook, String email, String device_token, String imei,
                              String os) {
        mUsersService.loginFacebook(id_facebook, email, device_token, imei, os, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        hiveListener.onResult(loginResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                }
        );
    }

    public void forgotPassword(String emailUsername) {
        mUsersService.forgotPassword(emailUsername, new Callback<HiveResponse>() {
                    @Override
                    public void success(HiveResponse hiveResponse, Response response) {
                        UsersBL.this.hiveListener.onResult(hiveResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                }
        );
    }

    public void changePassword(String token, String id_user, String password, String new_password) {
        mUsersService.changePassword(token, id_user, password, new_password, new Callback<HiveResponse>() {
                    @Override
                    public void success(HiveResponse hiveResponse, Response response) {
                        UsersBL.this.hiveListener.onResult(hiveResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                }
        );
    }

    public void profile(String token, String responseId, String userId) {
        final String fileName = "profile=" + responseId + "" + userId;
        mUsersService.profile(token, responseId, "", userId, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, loginResponse, fileName);
                hiveListener.onResult(loginResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                LoginResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, LoginResponse.class);
                if (response != null) {
                    hiveListener.onResult(response);
                } else {
                    hiveListener.onError(error);
                }
            }
        });
    }

    public void existUsername(String username) {
        mUsersService.existUsername(username, new Callback<ExistEmailUsernameResponse>() {
            @Override
            public void success(ExistEmailUsernameResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void existEmail(String email) {
        mUsersService.existEmail(email, new Callback<ExistEmailUsernameResponse>() {
            @Override
            public void success(ExistEmailUsernameResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void saveProfile(String token, String idUser, String firstName, String lastName,
                            String username, String code_city, String bio, String code_country,
                            File photo, String phone) {
        TypedFile photoData = null;
        if (photo != null) {
            photoData = new TypedFile("image/jpeg", photo);
        }

        mUsersService.saveProfile(token, idUser, firstName, lastName, username,
                photoData, bio, code_city, phone, new Callback<HiveResponse>() {
                    @Override
                    public void success(HiveResponse loginResponse, Response response) {
                        hiveListener.onResult(loginResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                });
    }

    public void singUp(String email, String password, String firstName, String lastName,
                       String username, String code_city, String phone,
                       String imei, File photo) {
        TypedFile photoData = null;
        if (photo != null) {
            photoData = new TypedFile("image/jpeg", photo);
        }

        mUsersService.singUpPhoto(email, password, firstName, lastName, username, code_city,
                phone, imei, photoData, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        hiveListener.onResult(loginResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                }
        );
    }

    public void getFollowers(String token, String id_user, String idResponse, String start, String amount) {
        final String fileName = "followers=" + id_user + "_" + idResponse;
        mUsersService.getFollowers(token, id_user, idResponse, start, amount, new Callback<UserNetworkResponse>() {
                    @Override
                    public void success(UserNetworkResponse hiveResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, hiveResponse, fileName);
                        UsersBL.this.hiveListener.onResult(hiveResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UserNetworkResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, UserNetworkResponse.class);
                        if (response != null) {
                            hiveListener.onResult(response);
                        } else {
                            hiveListener.onError(error);
                        }
                    }
                }
        );
    }

    public void getFollowing(String token, String id_user, String idResponse, String start, String amount) {
        final String fileName = "following=" + id_user + "_" + idResponse;
        mUsersService.getFollowing(token, id_user, idResponse, start, amount, new Callback<UserNetworkResponse>() {
                    @Override
                    public void success(UserNetworkResponse hiveResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, hiveResponse, fileName);
                        UsersBL.this.hiveListener.onResult(hiveResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UserNetworkResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, UserNetworkResponse.class);
                        if (response != null) {
                            hiveListener.onResult(response);
                        } else {
                            hiveListener.onError(error);
                        }
                    }
                }
        );
    }

    public void getGoodTimes(String token, String id_user, String start, String amount) {
        final String fileName = "goodtimes=" + id_user;
        mUsersService.getGoodTimes(token, id_user, start, amount, new Callback<GoodTimesResponse>() {
                    @Override
                    public void success(GoodTimesResponse goodTimesResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, goodTimesResponse, fileName);
                        UsersBL.this.hiveListener.onResult(goodTimesResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        GoodTimesResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, GoodTimesResponse.class);
                        if (response != null) {
                            hiveListener.onResult(response);
                        } else {
                            hiveListener.onError(error);
                        }
                    }
                }
        );
    }

    public void blockUser(String token, String id_user, String id_user_blocked) {
        mUsersService.blockUser(token, id_user, id_user_blocked, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void changeAutoCheckin(String token, String id_user, String active) {
        mUsersService.changeAutoCheckin(token, id_user, active, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void mergeFromFacebook(String token, String id_user, String id_facebook, String[] ids_facebook) {
        mUsersService.mergeFromFacebook(token, id_user, id_facebook, ids_facebook, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse userNetworkResponse, Response response) {
                UsersBL.this.hiveListener.onResult(userNetworkResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void mergeFromTwitter(String token, String id_user, String id_twitter, String[] ids_twitter) {
        mUsersService.mergeFromTwitter(token, id_user, id_twitter, ids_twitter, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void mergeFromPhones(String token, String id_user, String[] phones) {
        mUsersService.mergeFromPhones(token, id_user, phones, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void searchUsers(String token, String id_user, String start, String amount, String criteria) {
        mUsersService.searchUsers(token, id_user, start, amount, criteria, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void followUser(String token, String id_user, String id_user_follow, String active) {
        mUsersService.followUser(token, id_user, id_user_follow, active, new Callback<FollowUserResponse>() {
            @Override
            public void success(FollowUserResponse followUserResponse, Response response) {
                UsersBL.this.hiveListener.onResult(followUserResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void getGoodTimeDetail(String token, String id_goodtime, String guestId, String userId, String start, String amount) {
        mUsersService.getGoodTimeDetail(token, id_goodtime, guestId, userId, start, amount, new Callback<GoodTimesDetailResponse>() {
            @Override
            public void success(GoodTimesDetailResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void unblockUser(String token, String id_user, String id_user_blocked) {
        mUsersService.unblockUser(token, id_user, id_user_blocked, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                UsersBL.this.hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void signUpFacebook(String email, String id_facebook, String first_name,
                               String last_name, String username_facebook, String link_facebook,
                               String photo, String code_city, String username, String password,
                               String device_token, String imei, String phone) {
        mUsersService.signUpFacebook(email, id_facebook, first_name, last_name,
                username_facebook, link_facebook, photo, code_city, username, password,
                device_token, imei, phone, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        hiveListener.onResult(loginResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hiveListener.onError(error);
                    }
                }
        );
    }

    public void checkinbycoords(String token, String id_user, String latitude, String longitude) {
        mUsersService.checkinbycoords(token, id_user, latitude, longitude, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public void saveFacebook(String token, String id_user, String id_facebook, String username_facebook, String link_facebook) {
        mUsersService.saveFacebook(token, id_user, id_facebook, username_facebook, link_facebook, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void saveTwitter(String token, String id_user, String id_twitter, String username_twitter) {
        mUsersService.saveTwitter(token, id_user, id_twitter, username_twitter, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void searchArtists(String token, String idUser, String start, String amount, String criteria) {
        mUsersService.searchArtists(token, idUser, start, amount, criteria, new Callback<ArtistsResponse>() {
            @Override
            public void success(ArtistsResponse userNetworkResponse, Response response) {
                hiveListener.onResult(userNetworkResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void disconnectTwitter(String token, String id_user) {
        mUsersService.disconnectTwitter(token, id_user, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void reportUser(String token, String id_user, String id_user_reported, String reason) {
        mUsersService.reportUser(token, id_user, id_user_reported, reason, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }

    public void newsFeed(String token, String userId, String start, String amount) {
        final String fileName = "newsFeed=" + userId;
        mUsersService.newsFeed(token, userId, start, amount, new Callback<NewsFeedResponse>() {
            @Override
            public void success(NewsFeedResponse newsfeedResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, newsfeedResponse, fileName);
                hiveListener.onResult(newsfeedResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                NewsFeedResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, NewsFeedResponse.class);
                if (response != null) {
                    hiveListener.onResult(response);
                } else {
                    hiveListener.onError(error);
                }
            }
        });
    }

    public void logOut(String token, String id_user) {
        mUsersService.logOut(token, id_user, new Callback<HiveResponse>() {
            @Override
            public void success(HiveResponse hiveResponse, Response response) {
                hiveListener.onResult(hiveResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                hiveListener.onError(error);
            }
        });
    }
}
