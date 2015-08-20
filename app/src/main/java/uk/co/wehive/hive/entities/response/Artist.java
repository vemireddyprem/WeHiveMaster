package uk.co.wehive.hive.entities.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    private int id_user;
    private String username;
    private String first_name;
    private String last_name;
    private String real_photo;
    private boolean premium;
    private boolean is_blocked;
    private int follow;

    public Artist() {
    }

    private Artist(Parcel in) {
        this.id_user = in.readInt();
        this.username = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.real_photo = in.readString();
        this.premium = in.readByte() != 0;
        this.is_blocked = in.readByte() != 0;
        this.follow = in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_user);
        dest.writeString(this.username);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.real_photo);
        dest.writeByte(premium ? (byte) 1 : (byte) 0);
        dest.writeByte(is_blocked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.follow);
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getReal_photo() {
        return real_photo;
    }

    public void setReal_photo(String real_photo) {
        this.real_photo = real_photo;
    }

    public boolean getPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }
}
