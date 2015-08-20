package uk.co.wehive.hive.bl;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.wehive.hive.entities.response.EventDetailResponse;
import uk.co.wehive.hive.entities.response.EventPostsResponse;
import uk.co.wehive.hive.entities.response.EventsUserResponse;
import uk.co.wehive.hive.entities.response.MediaListResponse;
import uk.co.wehive.hive.entities.response.MoreEventDetailResponse;
import uk.co.wehive.hive.entities.response.UserNetworkResponse;
import uk.co.wehive.hive.listeners.users.IHiveResponse;
import uk.co.wehive.hive.persistence.PersistenceHelper;
import uk.co.wehive.hive.services.IEventsApi;
import uk.co.wehive.hive.utils.ApplicationHive;

public class EventsBL {

    private IEventsApi mEventsService;
    private IHiveResponse eventsUserListener;
    private Context mContext;

    public void setEventsUserListener(IHiveResponse eventsUserListener) {
        this.eventsUserListener = eventsUserListener;
    }

    public EventsBL() {
        this.mEventsService = ApplicationHive.getRestAdapter().create(IEventsApi.class);
        mContext = ApplicationHive.getContext();
    }

    public void getPastEvents(String token, String userId, String start, String amount) {
        final String fileName = "pastEvents=" + userId;
        mEventsService.getPastEvents(token, userId, start, amount, new Callback<EventsUserResponse>() {
            @Override
            public void success(EventsUserResponse eventResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, eventResponse, fileName);
                eventsUserListener.onResult(eventResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                EventsUserResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, EventsUserResponse.class);
                if (response != null) {
                    eventsUserListener.onResult(response);
                } else {
                    eventsUserListener.onError(error);
                }
            }
        });
    }

    public void getUpComingEvents(String token, String userId, String start, String amount) {
        final String fileName = "upComingEvents=" + userId;
        mEventsService.getUpComingEvents(token, userId, start, amount, new Callback<EventsUserResponse>() {
            @Override
            public void success(EventsUserResponse eventResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, eventResponse, fileName);
                eventsUserListener.onResult(eventResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                EventsUserResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, EventsUserResponse.class);
                if (response != null) {
                    eventsUserListener.onResult(response);
                } else {
                    eventsUserListener.onError(error);
                }
            }
        });
    }

    public void searchNearYou(String userId, String cityId, String start, String amount, String longitude, String latitude) {
        mEventsService.searchNearYou(userId, cityId, start, amount, longitude, latitude, new Callback<EventsUserResponse>() {
            @Override
            public void success(EventsUserResponse eventsUserResponse, Response response) {
                eventsUserListener.onResult(eventsUserResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void searchRecommended(String start, String amount, String userId) {
        final String fileName = "searchRecommended=" + userId;
        mEventsService.searchRecommended(start, amount, userId, new Callback<EventsUserResponse>() {
            @Override
            public void success(EventsUserResponse eventsUserResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, eventsUserResponse, fileName);
                eventsUserListener.onResult(eventsUserResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                EventsUserResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, EventsUserResponse.class);
                if (response != null) {
                    eventsUserListener.onResult(response);
                } else {
                    eventsUserListener.onError(error);
                }
            }
        });
    }

    public void getTrending(String token, String userId, String start, String amount) {
        final String fileName = "getTrending=" + userId;
        mEventsService.getTrending(token, userId, start, amount, new Callback<EventsUserResponse>() {
            @Override
            public void success(EventsUserResponse eventsUserResponse, Response response) {
                PersistenceHelper.saveResponse(mContext, eventsUserResponse, fileName);
                eventsUserListener.onResult(eventsUserResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                EventsUserResponse response =
                        PersistenceHelper.readResponse(mContext, fileName, EventsUserResponse.class);
                if (response != null) {
                    eventsUserListener.onResult(response);
                } else {
                    eventsUserListener.onError(error);
                }
            }
        });
    }

    public void getDetail(String idEvent, String userId, String latitude, String longitude) {
        final String fileName = "eventDetail=" + idEvent + "_" + userId;
        mEventsService.getDetail(idEvent, userId, latitude, longitude,
                new Callback<EventDetailResponse>() {
                    @Override
                    public void success(EventDetailResponse eventDetailResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, eventDetailResponse, fileName);
                        eventsUserListener.onResult(eventDetailResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        EventDetailResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, EventDetailResponse.class);
                        if (response != null) {
                            eventsUserListener.onResult(response);
                        } else {
                            eventsUserListener.onError(error);
                        }
                    }
                }
        );
    }

    public void getMoreDetail(String token, String idUser, String idEvent) {
        mEventsService.getMoreDetail(token, idUser, idEvent, new Callback<MoreEventDetailResponse>() {
            @Override
            public void success(MoreEventDetailResponse moreEventDetailResponse, Response response) {
                eventsUserListener.onResult(moreEventDetailResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void getEverything(String id_event, String id_user, int start, int amount) {
        final String fileName = "everythingEvents=" + id_event + "_" + id_user;
        mEventsService.getNewsfeed(id_event, id_user, String.valueOf(start),
                String.valueOf(amount), new Callback<EventPostsResponse>() {
                    @Override
                    public void success(EventPostsResponse eventPostsResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, eventPostsResponse, fileName);
                        eventsUserListener.onResult(eventPostsResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        EventPostsResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, EventPostsResponse.class);
                        if (response != null) {
                            eventsUserListener.onResult(response);
                        } else {
                            eventsUserListener.onError(error);
                        }
                    }
                }
        );
    }

    public void getLive(String id_event, String id_user, int start, int amount) {
        final String fileName = "liveEvents=" + id_event + "_" + id_user;
        mEventsService.getLive(id_event, id_user, String.valueOf(start),
                String.valueOf(amount), new Callback<EventPostsResponse>() {
                    @Override
                    public void success(EventPostsResponse eventPostsResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, eventPostsResponse, fileName);
                        eventsUserListener.onResult(eventPostsResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        EventPostsResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, EventPostsResponse.class);
                        if (response != null) {
                            eventsUserListener.onResult(response);
                        } else {
                            eventsUserListener.onError(error);
                        }
                    }
                }
        );
    }

    public void getBuzzing(String id_event, String id_user, int start, int amount) {
        final String fileName = "buzzingEvents=" + id_event + "_" + id_user;
        mEventsService.getBuzzing(id_event, id_user, String.valueOf(start),
                String.valueOf(amount), new Callback<EventPostsResponse>() {
                    @Override
                    public void success(EventPostsResponse eventPostsResponse, Response response) {
                        PersistenceHelper.saveResponse(mContext, eventPostsResponse, fileName);
                        eventsUserListener.onResult(eventPostsResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        EventPostsResponse response =
                                PersistenceHelper.readResponse(mContext, fileName, EventPostsResponse.class);
                        if (response != null) {
                            eventsUserListener.onResult(response);
                        } else {
                            eventsUserListener.onError(error);
                        }
                    }
                }
        );
    }

    public void searchEvents(String artistId, String location, String locationType,
                             String startDate, String endDate, String start, String amount) {
        mEventsService.searchEvents(artistId, location, locationType, startDate, endDate,
                start, amount, new Callback<EventsUserResponse>() {

                    @Override
                    public void success(EventsUserResponse eventsUserResponse, Response response) {
                        eventsUserListener.onResult(eventsUserResponse);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        eventsUserListener.onError(error);
                    }
                });
    }

    public void track(String token, String id_event_follow, String id_user, String active) {
        mEventsService.track(token, id_event_follow, id_user, active, new Callback<EventDetailResponse>() {
            @Override
            public void success(EventDetailResponse eventPostsResponse, Response response) {
                eventsUserListener.onResult(eventPostsResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void checkin(String token, String id_event, String id_user, String active) {
        mEventsService.checkin(token, id_event, id_user, active, new Callback<EventDetailResponse>() {
            @Override
            public void success(EventDetailResponse eventPostsResponse, Response response) {
                eventsUserListener.onResult(eventPostsResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void getMediaList(String userId, String token, String eventId, String start, String amount) {
        mEventsService.getMediaList(userId, token, eventId, start, amount, new Callback<MediaListResponse>() {
            @Override
            public void success(MediaListResponse mediaListResponse, Response response) {
                eventsUserListener.onResult(mediaListResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void getTrackedBy(String id_user, String token, String id_event, String start,
                             String amount) {
        mEventsService.getTrackedBy(id_user, token, id_event, start, amount, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse userNetworkResponse, Response response) {
                eventsUserListener.onResult(userNetworkResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }

    public void getFollowers(String token, String id_user, String id_event, String start, String amount) {
        mEventsService.getFollowers(token, id_user, id_event, start, amount, new Callback<UserNetworkResponse>() {
            @Override
            public void success(UserNetworkResponse userNetworkResponse, Response response) {
                eventsUserListener.onResult(userNetworkResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                eventsUserListener.onError(error);
            }
        });
    }
}
