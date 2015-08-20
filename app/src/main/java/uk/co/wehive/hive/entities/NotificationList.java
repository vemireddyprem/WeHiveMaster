package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationList implements Parcelable {

    private String artists_post;
    private String comment_my_post;
    private String follow_me;
    private String gallery_aviable;
    private String like_my_post;
    private String mention_me;
    private String new_concert_announced;
    private String reminder_event;
    private String shared_my_post;

    public NotificationList() {
    }

    private NotificationList(Parcel in) {
        this.artists_post = in.readString();
        this.comment_my_post = in.readString();
        this.follow_me = in.readString();
        this.gallery_aviable = in.readString();
        this.like_my_post = in.readString();
        this.mention_me = in.readString();
        this.new_concert_announced = in.readString();
        this.reminder_event = in.readString();
        this.shared_my_post = in.readString();
    }

    public static final Creator<NotificationList> CREATOR = new Creator<NotificationList>() {
        public NotificationList createFromParcel(Parcel source) {
            return new NotificationList(source);
        }

        public NotificationList[] newArray(int size) {
            return new NotificationList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artists_post);
        dest.writeString(this.comment_my_post);
        dest.writeString(this.follow_me);
        dest.writeString(this.gallery_aviable);
        dest.writeString(this.like_my_post);
        dest.writeString(this.mention_me);
        dest.writeString(this.new_concert_announced);
        dest.writeString(this.reminder_event);
        dest.writeString(this.shared_my_post);
    }

    public String getArtists_post() {
        return artists_post;
    }

    public void setArtists_post(String artists_post) {
        this.artists_post = artists_post;
    }

    public String getComment_my_post() {
        return comment_my_post;
    }

    public void setComment_my_post(String comment_my_post) {
        this.comment_my_post = comment_my_post;
    }

    public String getFollow_me() {
        return follow_me;
    }

    public void setFollow_me(String follow_me) {
        this.follow_me = follow_me;
    }

    public String getGallery_aviable() {
        return gallery_aviable;
    }

    public void setGallery_aviable(String gallery_aviable) {
        this.gallery_aviable = gallery_aviable;
    }

    public String getLike_my_post() {
        return like_my_post;
    }

    public void setLike_my_post(String like_my_post) {
        this.like_my_post = like_my_post;
    }

    public String getMention_me() {
        return mention_me;
    }

    public void setMention_me(String mention_me) {
        this.mention_me = mention_me;
    }

    public String getNew_concert_announced() {
        return new_concert_announced;
    }

    public void setNew_concert_announced(String new_concert_announced) {
        this.new_concert_announced = new_concert_announced;
    }

    public String getReminder_event() {
        return reminder_event;
    }

    public void setReminder_event(String reminder_event) {
        this.reminder_event = reminder_event;
    }

    public String getShared_my_post() {
        return shared_my_post;
    }

    public void setShared_my_post(String shared_my_post) {
        this.shared_my_post = shared_my_post;
    }
}
