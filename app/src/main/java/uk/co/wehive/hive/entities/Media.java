package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {

    private int id_post;
    private String age_post;
    private String absolute_file;
    private String thumbnail;
    private boolean liked;
    private boolean last_media;
    private String share_link;
    private String first_name;
    private String last_name;
    private String type;
    private String user_photo;

    public Media() {
    }

    private Media(Parcel in) {
        this.id_post = in.readInt();
        this.age_post = in.readString();
        this.absolute_file = in.readString();
        this.thumbnail = in.readString();
        this.liked = in.readByte() != 0;
        this.last_media = in.readByte() != 0;
        this.share_link = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.type = in.readString();
        this.user_photo = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_post);
        dest.writeString(this.age_post);
        dest.writeString(this.absolute_file);
        dest.writeString(this.thumbnail);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeByte(last_media ? (byte) 1 : (byte) 0);
        dest.writeString(this.share_link);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.type);
        dest.writeString(this.user_photo);
    }

    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }

    public String getAge_post() {
        return age_post;
    }

    public void setAge_post(String age_post) {
        this.age_post = age_post;
    }

    public String getAbsolute_file() {
        return absolute_file;
    }

    public void setAbsolute_file(String absolute_file) {
        this.absolute_file = absolute_file;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isLast_media() {
        return last_media;
    }

    public void setLast_media(boolean last_media) {
        this.last_media = last_media;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }
}
