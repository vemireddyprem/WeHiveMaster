/*******************************************************************************
 PROJECT:       Hive
 FILE:          Connection.java
 DESCRIPTION:   Entity for parse result of Connection (Login.json)
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        14/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Connection implements Parcelable {

    private int id_user;
    private String name;
    private String photo;

    public Connection() {
    }

    private Connection(Parcel in) {
        this.id_user = in.readInt();
        this.name = in.readString();
        this.photo = in.readString();
    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        public Connection createFromParcel(Parcel source) {
            return new Connection(source);
        }

        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id_user);
        dest.writeString(this.name);
        dest.writeString(this.photo);
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
