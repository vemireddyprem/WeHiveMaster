package uk.co.wehive.hive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.entities.Connection;
import uk.co.wehive.hive.entities.User;

public class ManagePreferences {

    public static boolean isFirstTimeUser() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.prefereces_is_first_time_user), Context.MODE_PRIVATE);
        return preferences.getBoolean(resources.getString(R.string.prefereces_is_first_time_user), true);
    }

    public static void setIsFirstTimeUser(boolean isFirstTimeUser) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.prefereces_is_first_time_user), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(resources.getString(R.string.prefereces_is_first_time_user), isFirstTimeUser);
        prefEditor.commit();
    }

    public static void setPreferencesUser(User user) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putInt(resources.getString(R.string.preferences_user_id), user.getId_user());
        prefEditor.putString(resources.getString(R.string.preferences_username), user.getUsername());
        prefEditor.putString(resources.getString(R.string.preferences_first_name), user.getFirst_name());
        prefEditor.putString(resources.getString(R.string.preferences_last_name), user.getLast_name());
        prefEditor.putString(resources.getString(R.string.preferences_email), user.getEmail());
        prefEditor.putString(resources.getString(R.string.preferences_bio), user.getBio());
        prefEditor.putString(resources.getString(R.string.preferences_photo), user.getPhoto());
        prefEditor.putBoolean(resources.getString(R.string.preferences_follow), user.isFollow());
        prefEditor.putInt(resources.getString(R.string.preferences_following), user.getFollowing());
        prefEditor.putInt(resources.getString(R.string.preferences_followers), user.getFollowers());
        prefEditor.putInt(resources.getString(R.string.preferences_events), user.getEvents());
        prefEditor.putString(resources.getString(R.string.preferences_city), user.getCity());
        prefEditor.putString(resources.getString(R.string.preferences_city_code), user.getCity_code());
        prefEditor.putString(resources.getString(R.string.preferences_country), user.getCountry());
        prefEditor.putBoolean(resources.getString(R.string.preferences_blocked), user.isBlocked());
        prefEditor.putString(resources.getString(R.string.preferences_id_facebook), user.getId_facebook());
        prefEditor.putString(resources.getString(R.string.preferences_id_twitter), user.getId_twitter());
        prefEditor.putString(resources.getString(R.string.preferences_link), user.getLink());
        prefEditor.putString(resources.getString(R.string.preferences_fbUsername), user.getFbUsername());
        prefEditor.putInt(resources.getString(R.string.preferences_count_newsfeed), user.getCount_newsfeeds());
        prefEditor.putInt(resources.getString(R.string.preferences_count_connections), user.getConnections().size());
        prefEditor.putString(resources.getString(R.string.preferences_access_token), user.getToken());

        for (int i = 0; i < user.getConnections().size(); i++) {
            prefEditor.remove("ConnectionId_" + i);
            prefEditor.remove("ConnectionName_" + i);
            prefEditor.remove("ConnectionPhoto_" + i);

            prefEditor.putInt("ConnectionId_" + i, user.getConnections().get(i).getId_user());
            prefEditor.putString("ConnectionName_" + i, user.getConnections().get(i).getName());
            prefEditor.putString("ConnectionPhoto_" + i, user.getConnections().get(i).getPhoto());
        }

        prefEditor.commit();
        if (user.getToken() != null && !user.getToken().isEmpty()) {
            setIsFirstTimeUser(false);
        }
    }

//    public static void setAccessToken(String token) {
//        Resources resources = ApplicationHive.getContext().getResources();
//        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefEditor = preferences.edit();
//        prefEditor.putString(resources.getString(R.string.preferences_access_token), token);
//        prefEditor.commit();
//    }

//    public static String getAccessToken() {
//        Resources resources = ApplicationHive.getContext().getResources();
//        SharedPreferences preferences =
//                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.preferences_access_token), Context.MODE_PRIVATE);
//        return preferences.getString(resources.getString(R.string.preferences_access_token), "");
//    }

    public static void setNumberOfNotifications(int number) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt(resources.getString(R.string.preferences_count_notifications), number);
        prefEditor.commit();
    }

    public static int getNumberOfNotifications() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getInt(resources.getString(R.string.preferences_count_notifications), 0);
    }

    public static void setIdGCM(String idGCM) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences = ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(resources.getString(R.string.id_gcm), idGCM);
        prefEditor.commit();
    }

    public static String getIdGCM() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getString(resources.getString(R.string.id_gcm), "");
    }

    public static User getUserPreferences() {

        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);

        User user = new User();
        user.setId_user(preferences.getInt(resources.getString(R.string.preferences_user_id), 0));
        user.setUsername(preferences.getString(resources.getString(R.string.preferences_username), ""));
        user.setFirst_name(preferences.getString(resources.getString(R.string.preferences_first_name), ""));
        user.setLast_name(preferences.getString(resources.getString(R.string.preferences_last_name), ""));
        user.setEmail(preferences.getString(resources.getString(R.string.preferences_email), ""));
        user.setBio(preferences.getString(resources.getString(R.string.preferences_bio), ""));
        user.setPhoto(preferences.getString(resources.getString(R.string.preferences_photo), ""));
        user.setFollow(preferences.getBoolean(resources.getString(R.string.preferences_follow), false));
        user.setFollowers(preferences.getInt(resources.getString(R.string.preferences_followers), 0));
        user.setFollowing(preferences.getInt(resources.getString(R.string.preferences_following), 0));
        user.setEvents(preferences.getInt(resources.getString(R.string.preferences_events), 0));
        user.setCity(preferences.getString(resources.getString(R.string.preferences_city), ""));
        user.setCity_code(preferences.getString(resources.getString(R.string.preferences_city_code), ""));
        user.setCountry(preferences.getString(resources.getString(R.string.preferences_country), ""));
        user.setBlocked(preferences.getBoolean(resources.getString(R.string.preferences_blocked), false));
        user.setId_facebook(preferences.getString(resources.getString(R.string.preferences_id_facebook), ""));
        user.setId_twitter(preferences.getString(resources.getString(R.string.preferences_id_twitter), ""));
        user.setLink(preferences.getString(resources.getString(R.string.preferences_link), ""));
        user.setFbUsername(preferences.getString(resources.getString(R.string.preferences_fbUsername), ""));
        user.setCount_newsfeeds(preferences.getInt(resources.getString(R.string.preferences_count_newsfeed), 0));
        user.setCount_notifications(preferences.getInt(resources.getString(R.string.preferences_count_notifications), 0));
        user.setToken(preferences.getString(resources.getString(R.string.preferences_access_token), ""));

        int size = preferences.getInt(resources.getString(R.string.preferences_count_connections), 0);
        Connection connection;
        List<Connection> connections = new ArrayList<Connection>();
        for (int i = 0; i < size; i++) {
            connection = new Connection();
            connection.setId_user(preferences.getInt("ConnectionId_" + i, 0));
            connection.setName(preferences.getString("ConnectionName_" + i, ""));
            connection.setPhoto(preferences.getString("ConnectionPhoto_" + i, ""));
            connections.add(connection);
        }
        user.setConnections(connections);
        return user;
    }

    public static void setAutocheckin(boolean autocheckin) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(resources.getString(R.string.preferences_autocheckin), autocheckin);

        prefEditor.commit();
    }

    public static boolean isAutocheckin() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getBoolean(resources.getString(R.string.preferences_autocheckin), false);
    }

    public static void cleanUserPreferences() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences sharedPref =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void cleanAutoCheckinPreferences() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences sharedPref =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.preferences_autocheckin), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void setNewsFeedCounterMenu(int newsFeedCount) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt(resources.getString(R.string.preferences_newsfeed_counter_menu), newsFeedCount);

        prefEditor.commit();
    }

    public static int getNewsFeedCounterMenu() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getInt(resources.getString(R.string.preferences_newsfeed_counter_menu), 0);
    }

    public static void setNotificationsCounterMenu(int notificationsCounter) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt(resources.getString(R.string.preferences_notifications_counter_menu), notificationsCounter);

        prefEditor.commit();
    }

    public static int getNotificationsCounterMenu() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getInt(resources.getString(R.string.preferences_notifications_counter_menu), 0);
    }

    public static void setNewFragment(boolean autocheckin) {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(resources.getString(R.string.preferences_set_new_fragment), autocheckin);

        prefEditor.commit();
    }

    public static boolean isANewFragment() {
        Resources resources = ApplicationHive.getContext().getResources();
        SharedPreferences preferences =
                ApplicationHive.getContext().getSharedPreferences(resources.getString(R.string.user_preferences), Context.MODE_PRIVATE);
        return preferences.getBoolean(resources.getString(R.string.preferences_set_new_fragment), false);
    }
}
