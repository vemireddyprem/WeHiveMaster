package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Cities implements Parcelable {

    private int id_city;
    private String name;
    private String code;

    public Cities() {
    }

    private Cities(Parcel in) {
        this.id_city = in.readInt();
        this.name = in.readString();
        this.code = in.readString();
    }

    public static final Creator<Cities> CREATOR = new Creator<Cities>() {
        public Cities createFromParcel(Parcel source) {
            return new Cities(source);
        }

        public Cities[] newArray(int size) {
            return new Cities[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_city);
        dest.writeString(this.name);
        dest.writeString(this.code);
    }

    public int getId_city() {
        return id_city;
    }

    public void setId_city(int id_city) {
        this.id_city = id_city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
