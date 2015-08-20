/*******************************************************************************
 PROJECT:       Hive
 FILE:          GoodTimes.java
 DESCRIPTION:   Entity for parse result of GoodTimes (goodTimes.json)
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        18/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class GoodTimes implements Parcelable {

    private int id_event;
    private String name;
    private String absolute_photo;
    private int posts;
    private String city;
    private String venue;
    private String date_event;
    private String time_event;
    private boolean show_gallery;

    public GoodTimes() {
    }

    private GoodTimes(Parcel in) {
        this.id_event = in.readInt();
        this.name = in.readString();
        this.absolute_photo = in.readString();
        this.posts = in.readInt();
        this.city = in.readString();
        this.venue = in.readString();
        this.date_event = in.readString();
        this.time_event = in.readString();
        this.show_gallery = in.readByte() != 0;
    }

    public static final Creator<GoodTimes> CREATOR = new Creator<GoodTimes>() {
        public GoodTimes createFromParcel(Parcel source) {
            return new GoodTimes(source);
        }

        public GoodTimes[] newArray(int size) {
            return new GoodTimes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_event);
        dest.writeString(this.name);
        dest.writeString(this.absolute_photo);
        dest.writeInt(this.posts);
        dest.writeString(this.city);
        dest.writeString(this.venue);
        dest.writeString(this.date_event);
        dest.writeString(this.time_event);
        dest.writeByte(show_gallery ? (byte) 1 : (byte) 0);
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolute_photo() {
        return absolute_photo;
    }

    public void setAbsolute_photo(String absolute_photo) {
        this.absolute_photo = absolute_photo;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate_event() {
        return date_event;
    }

    public void setDate_event(String date_event) {
        this.date_event = date_event;
    }

    public String getTime_event() {
        return time_event;
    }

    public void setTime_event(String time_event) {
        this.time_event = time_event;
    }

    public boolean isShow_gallery() {
        return show_gallery;
    }

    public void setShow_gallery(boolean show_gallery) {
        this.show_gallery = show_gallery;
    }
}
