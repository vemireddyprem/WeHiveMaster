package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

import uk.co.wehive.hive.entities.response.HiveResponse;

public class EventDetail extends HiveResponse implements Parcelable {

    private String name;
    private int id_event;
    private String photo;
    private String date_event;
    private String time_event;
    private boolean show_gallery;
    private String place;
    private String status_event;
    private boolean allow_post;
    private boolean allow_checkin;
    private boolean allow_interact;
    private boolean is_tracking;
    private boolean is_checkin;

    public static final Parcelable.Creator<EventDetail> CREATOR = new Parcelable.Creator<EventDetail>() {
        public EventDetail createFromParcel(Parcel in) {
            return new EventDetail(in);
        }

        public EventDetail[] newArray(int size) {
            return new EventDetail[size];
        }
    };

    private EventDetail(Parcel obj) {
        id_event = obj.readInt();
        name = obj.readString();
        photo = obj.readString();
        date_event = obj.readString();
        time_event = obj.readString();
        photo = obj.readString();
        show_gallery = obj.readByte() != 0;
        place = obj.readString();
        status_event = obj.readString();
        allow_post = obj.readByte() != 0;
        allow_checkin = obj.readByte() != 0;
        allow_interact = obj.readByte() != 0;
        is_tracking = obj.readByte() != 0;
        is_checkin = obj.readByte() != 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStatus_event() {
        return status_event;
    }

    public void setStatus_event(String status_event) {
        this.status_event = status_event;
    }

    public boolean isAllow_post() {
        return allow_post;
    }

    public void setAllow_post(boolean allow_post) {
        this.allow_post = allow_post;
    }

    public boolean isAllow_checkin() {
        return allow_checkin;
    }

    public void setAllow_checkin(boolean allow_checkin) {
        this.allow_checkin = allow_checkin;
    }

    public boolean isAllow_interact() {
        return allow_interact;
    }

    public void setAllow_interact(boolean allow_interact) {
        this.allow_interact = allow_interact;
    }

    public boolean isIs_tracking() {
        return is_tracking;
    }

    public void setIs_tracking(boolean is_tracking) {
        this.is_tracking = is_tracking;
    }

    public boolean isIs_checkin() {
        return is_checkin;
    }

    public void setIs_checkin(boolean is_checkin) {
        this.is_checkin = is_checkin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id_event);
        dest.writeString(photo);
        dest.writeString(date_event);
        dest.writeString(time_event);
        dest.writeByte((byte) (show_gallery ? 1 : 0));
        dest.writeString(place);
        dest.writeString(status_event);
        dest.writeByte((byte) (allow_post ? 1 : 0));
        dest.writeByte((byte) (allow_checkin ? 1 : 0));
        dest.writeByte((byte) (allow_interact ? 1 : 0));
        dest.writeByte((byte) (is_tracking ? 1 : 0));
        dest.writeByte((byte) (is_checkin ? 1 : 0));
    }
}
