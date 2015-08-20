package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Countries implements Parcelable {

    private int id_country;
    private String name;
    private Boolean active;
    private String latitude;
    private String longitude;
    private String code;

    public Countries() {
    }

    private Countries(Parcel in) {
        this.id_country = in.readInt();
        this.name = in.readString();
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.code = in.readString();
    }

    public static final Creator<Countries> CREATOR = new Creator<Countries>() {
        public Countries createFromParcel(Parcel source) {
            return new Countries(source);
        }

        public Countries[] newArray(int size) {
            return new Countries[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_country);
        dest.writeString(this.name);
        dest.writeValue(this.active);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.code);
    }

    public int getId_country() {
        return id_country;
    }

    public void setId_country(int id_country) {
        this.id_country = id_country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return getName();
    }
}
