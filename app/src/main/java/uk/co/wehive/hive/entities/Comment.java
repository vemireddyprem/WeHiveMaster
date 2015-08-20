package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

import uk.co.wehive.hive.utils.swipe.SwipeListViewItemRow;

public class Comment extends SwipeListViewItemRow implements Parcelable {

    private int id_comment;
    private String comment;
    private int age_comment;
    private int user_id_user;
    private String user_real_photo;
    private String user_real_username;
    private String mentions;
    private String date_comment;

    public Comment() {
    }

    private Comment(Parcel in) {
        this.id_comment = in.readInt();
        this.comment = in.readString();
        this.age_comment = in.readInt();
        this.user_id_user = in.readInt();
        this.user_real_photo = in.readString();
        this.user_real_username = in.readString();
        this.mentions = in.readString();
        this.date_comment = in.readString();
        this.flag_like = in.readString();
        this.isSwipe = in.readByte() != 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_comment);
        dest.writeString(this.comment);
        dest.writeInt(this.age_comment);
        dest.writeInt(this.user_id_user);
        dest.writeString(this.user_real_photo);
        dest.writeString(this.user_real_username);
        dest.writeString(this.mentions);
        dest.writeString(this.date_comment);
        dest.writeString(this.flag_like);
        dest.writeByte(isSwipe ? (byte) 1 : (byte) 0);
    }

    public String getFlag_like() {
        return flag_like;
    }

    public void setFlag_like(String flag_like) {
        this.flag_like = flag_like;
    }

    private String flag_like;

    public int getId_comment() {
        return id_comment;
    }

    public void setId_comment(int id_comment) {
        this.id_comment = id_comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getAge_comment() {
        return age_comment;
    }

    public void setAge_comment(int age_comment) {
        this.age_comment = age_comment;
    }

    public int getUser_id_user() {
        return user_id_user;
    }

    public void setUser_id_user(int user_id_user) {
        this.user_id_user = user_id_user;
    }

    public String getUser_real_photo() {
        return user_real_photo;
    }

    public void setUser_real_photo(String user_real_photo) {
        this.user_real_photo = user_real_photo;
    }

    public String getUser_real_username() {
        return user_real_username;
    }

    public void setUser_real_username(String user_real_username) {
        this.user_real_username = user_real_username;
    }

    public String getMentions() {
        return mentions;
    }

    public void setMentions(String mentions) {
        this.mentions = mentions;
    }

    public String getDate_comment() {
        return date_comment;
    }

    public void setDate_comment(String date_comment) {
        this.date_comment = date_comment;
    }
}