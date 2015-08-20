package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GoodTimesDetail implements Parcelable {

    private ArrayList<Post> posts;
    private String date_event;
    private int id_event;
    private String name;
    private String place;

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public String getDate_event() {
        return date_event;
    }

    public void setDate_event(String date_event) {
        this.date_event = date_event;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public GoodTimesDetail() {
    }

    private GoodTimesDetail(Parcel obj) {
        date_event = obj.readString();
        id_event = obj.readInt();
        name = obj.readString();
        place = obj.readString();
    }

    public static final Parcelable.Creator<GoodTimesDetail> CREATOR = new Parcelable.Creator<GoodTimesDetail>() {
        public GoodTimesDetail createFromParcel(Parcel in) {
            return new GoodTimesDetail(in);
        }

        public GoodTimesDetail[] newArray(int size) {
            return new GoodTimesDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(date_event);
        dest.writeInt(id_event);
        dest.writeString(name);
        dest.writeString(place);
    }
}
