package uk.co.wehive.hive.entities;

import android.os.Parcelable;
import android.os.Parcel;

public class NotificationMessage implements Parcelable {

    private String id_user;
    private String message;
    private String id_notification;
    private int age_notification;
    private int id_origin;
    private String origin;
    private String username;
    private String photo;

    public NotificationMessage() {
    }

    private NotificationMessage(Parcel in) {
        this.id_user = in.readString();
        this.message = in.readString();
        this.id_notification = in.readString();
        this.age_notification = in.readInt();
        this.id_origin = in.readInt();
        this.origin = in.readString();
        this.username = in.readString();
        this.photo = in.readString();
    }

    public static final Creator<NotificationMessage> CREATOR = new Creator<NotificationMessage>() {
        public NotificationMessage createFromParcel(Parcel source) {
            return new NotificationMessage(source);
        }

        public NotificationMessage[] newArray(int size) {
            return new NotificationMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id_user);
        dest.writeString(this.message);
        dest.writeString(this.id_notification);
        dest.writeInt(this.age_notification);
        dest.writeInt(this.id_origin);
        dest.writeString(this.origin);
        dest.writeString(this.username);
        dest.writeString(this.photo);
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_notification() {
        return id_notification;
    }

    public void setId_notification(String id_notification) {
        this.id_notification = id_notification;
    }

    public int getAge_notification() {
        return age_notification;
    }

    public void setAge_notification(int age_notification) {
        this.age_notification = age_notification;
    }

    public int getId_origin() {
        return id_origin;
    }

    public void setId_origin(int id_origin) {
        this.id_origin = id_origin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
