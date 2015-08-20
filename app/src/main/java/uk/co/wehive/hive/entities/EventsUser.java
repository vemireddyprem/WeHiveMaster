package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class EventsUser implements Parcelable {

    private String absolute_photo;
    private String event_date;
    private String date_event;
    private int followings;
    private int id_event;
    private String is_live;
    private String latitude;
    private String longitude;
    private String name;
    private String thumbnail_photo;
    private String venue;

    public EventsUser() {
    }

    public String getAbsolute_photo() {
        return absolute_photo;
    }

    public String setAbsolute_photo(String absolute_photo) {
        return this.absolute_photo = absolute_photo;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String setEvent_date(String event_date) {
        return this.event_date = event_date;
    }

    public String getDate_event() {
        return date_event;
    }

    public void setDate_event(String date_event) {
        this.date_event = date_event;
    }

    public int getFollowings() {
        return followings;
    }

    public int setFollowings(int followings) {
        return this.followings = followings;
    }

    public int getId_event() {
        return id_event;
    }

    public int setId_event(int id_event) {
        return this.id_event = id_event;
    }

    public String getIs_live() {
        return is_live;
    }

    public String setIs_live(String is_live) {
        return this.is_live = is_live;
    }

    public String getLatitude() {
        return latitude;
    }

    public String setLatitude(String latitude) {
        return this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String setLongitude(String longitude) {
        return this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }

    public String getThumbnail_photo() {
        return thumbnail_photo;
    }

    public String setThumbnail_photo(String thumbnail_photo) {
        return this.thumbnail_photo = thumbnail_photo;
    }

    public String getVenue() {
        return venue;
    }

    public String setVenue(String venue) {
        return this.venue = venue;
    }

    private EventsUser(Parcel obj) {
        absolute_photo = obj.readString();
        event_date = obj.readString();
        date_event = obj.readString();
        followings = obj.readInt();
        id_event = obj.readInt();
        is_live = obj.readString();
        latitude = obj.readString();
        longitude = obj.readString();
        name = obj.readString();
        thumbnail_photo = obj.readString();
        venue = obj.readString();
    }

    public static final Parcelable.Creator<EventsUser> CREATOR = new Parcelable.Creator<EventsUser>() {
        public EventsUser createFromParcel(Parcel in) {
            return new EventsUser(in);
        }

        public EventsUser[] newArray(int size) {
            return new EventsUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(absolute_photo);
        dest.writeString(event_date);
        dest.writeString(date_event);
        dest.writeInt(followings);
        dest.writeInt(id_event);
        dest.writeString(is_live);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(name);
        dest.writeString(thumbnail_photo);
        dest.writeString(venue);
    }
}
