/*******************************************************************************
 PROJECT:       Hive
 FILE:          AppConstants.java
 DESCRIPTION:   Constants used in the application
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.utils;

import android.os.Environment;

public class AppConstants {
    //    public static final String WEHIVE_URL = "http://54.164.69.254/hive-dev-v2/api/";
    public static final String WEHIVE_URL_DEV = "http://dev.wehive.co.uk/hive-dev-v2/api/";
    public static final String WEHIVE_URL_PRODUCTION = "https://www.wehive.co.uk/v3/api/";
    public static final String GOOGLE_PLAY_WE_HIVE = "http://wehive.co.uk/";
    public static final String WEHIVE_URL = WEHIVE_URL_PRODUCTION;
    public static final String CODE_CITY = "LON";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String LIKED = "1";
    public static final String UNLIKED = "0";
    public static final String MEDIA = "media";

    //App keys for authentication with Twitter
    public static String TWITTER_CONSUMER_KEY = "pBalYBQfhdmZec5BaJeGFbMt1";
    public static String TWITTER_CONSUMER_SECRET = "modFmTfW3ypx3Ml1v2SSszcm6wh5fLDiQ6xwikBR1ayo7EmZP4";
    public static final String TWITTER_CALLBACK_URL = "authtw://hivetwitteroauth";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";

    //Options menu
    public static final String USER_PROFILE = "User";
    public static final String EVENTS = "Events";
    public static final String NOTIFICATIONS = "Notifications";
    public static final String NEWSFEED = "NewsFeed";
    public static final String SETTINGS = "Settings";

    public static final String FOLLOWERS_LIST = "FollowersList";
    public static final String FOLLOWED_LIST = "FollowedList";
    public static final String USER_NETWORK = "UserNetwork";
    public static final String FOLLOW_LIST = "FollowList";
    public static final String FOLLOW_PEOPLE = "FollowPeople";
    public static final String GOODTIMES = "goodtimes";

    public enum OPTIONS_GOOD_TIMES {like, share, comments, explore}

    public enum OPTIONS_NOTIFICATIONS {user, post, goodtime, event}

    public static final String POST = "post";
    public static final String USER_ID = "idUser";
    public static final String COMMENTS_LIST = "CommentsList";
    public static final String POST_ID = "PostId";
    //    public static final String SENDER_ID_GCM = "119083901753";
    public static final String SENDER_ID_GCM = "548162807238";
    public static final String ACCESS_TOKEN = "accessToken";

    public static final String EVENT_DATA = "EventData";
    public static final String OS = "android";
    public static final String ORIGIN_GCM = "origin";
    public static final String ID_ORIGIN_GCM = "id_origin";
    public static final String BADGE = "badge";
    public static final String MESSAGE_GCM = "message";
    public static final String ID_POST = "idPost";
    public static final String ID_EVENT = "idEvent";
    public static final String USERNAME = "username";

    public static final String SEARCH_BY = "search_by";
    public static final String ARTIST = "artist";
    public static final String LOCATION = "location";
    public static final String RESULTS = "results";

    public static final String PHOTO_PATH = "PhotoPath";
    public static final String PHOTO_URI = "PhotoUri";
    public static final String PHOTO_ORIGIN = "PhotoORIGIN";

    public static final String FILTER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "tmp_filter.jpg";
    public static final String THUMBNAIL_FROM_VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String IMAGE_FROM_MEME_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String FROM_MEME = "from_meme";
    public static final String FROM_VIDEO = "from_video";
    public static final String FROM_GALLERY_VIDEO = "from_gallery_video";
    public static final String VIDEO_PATH = "video_path";
    public static final String MEME_PHOTO_PATH = "meme_path";
    public static final String CAMERA_FILTERS_FRAGMENT = "CameraFilters";
    public static final String CUSTOM_CAMERA_FRAGMENT = "CustomCamera";
    public static final String MEME_CREATOR_FRAGMENT = "MemeCreatorFragment";

    //NewsFeed types
    public static final String CHECK_IN_NEWSFEED = "checkin_event";
    public static final String POST_CREATED_NEWSFEED = "post_created";

    //Services
    public static final String LIKE_POST_SERVICE = "like_post_service";

    public static final String PENDING_POSTS = "PendingPost";
    public static final String MEME_POST = "MEME_POST";
    public static final String VIDEO_POST = "VIDEO_POST";

    public static final String EXPLORE_MODE = "explore_mode";
}
