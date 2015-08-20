package uk.co.wehive.hive.entities.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Follower implements Parcelable {

    private int id_user;
    private String username;
    private String first_name;
    private String last_name;
    private String real_photo;
    private String premium;
    private int follow;
    private String id_facebook;
    private boolean is_blocked;

    public Follower() {
    }

    private Follower(Parcel in) {
        this.id_user = in.readInt();
        this.username = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.real_photo = in.readString();
        this.premium = in.readString();
        this.follow = in.readInt();
        this.id_facebook = in.readString();
        this.is_blocked = in.readByte() != 0;
    }

    public static final Creator<Follower> CREATOR = new Creator<Follower>() {
        public Follower createFromParcel(Parcel source) {
            return new Follower(source);
        }

        public Follower[] newArray(int size) {
            return new Follower[size];
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
        dest.writeString(this.premium);
        dest.writeInt(this.follow);
        dest.writeString(this.id_facebook);
        dest.writeByte(is_blocked ? (byte) 1 : (byte) 0);
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

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public String getId_facebook() {
        return id_facebook;
    }

    public void setId_facebook(String id_facebook) {
        this.id_facebook = id_facebook;
    }

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }
}
