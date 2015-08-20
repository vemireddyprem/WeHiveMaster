/*******************************************************************************
 PROJECT:       Hive
 FILE:          User.java
 DESCRIPTION:   Entity for parse data of Users
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        14/07/2014  Juan Pablo B.    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Added parameters.
 *******************************************************************************/
package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private int id_user;
    private String username;
    private String fbUsername;
    private String first_name;
    private String last_name;
    private String bio;
    private String photo;
    private String email;
    private String phone;
    private boolean follow;
    private int following;
    private int follower;
    private int events;
    private List<Connection> connections = new ArrayList<Connection>();
    private String city;
    private String city_code;
    private String country;
    private boolean blocked;
    private String id_facebook;
    private String id_twitter;
    private String link;
    private String token;
    private String message;
    private int count_newsfeeds;
    private int count_notifications;
    private String token_twitter;
    private String token_twittersecret;
    private String token_facebook;


    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return follower;
    }

    public void setFollowers(int followers) {
        this.follower = followers;
    }

    public int getEvents() {
        return events;
    }

    public void setEvents(int events) {
        this.events = events;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getId_facebook() {
        return id_facebook;
    }

    public void setId_facebook(String id_facebook) {
        this.id_facebook = id_facebook;
    }

    public String getId_twitter() {
        return id_twitter;
    }

    public void setId_twitter(String id_twitter) {
        this.id_twitter = id_twitter;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFbUsername() {
        return fbUsername;
    }

    public void setFbUsername(String fbUsername) {
        this.fbUsername = fbUsername;
    }

    public String getToken_twitter() {
        return token_twitter;
    }

    public void setToken_twitter(String token_twitter) {
        this.token_twitter = token_twitter;
    }

    public String getToken_twittersecret() {
        return token_twittersecret;
    }

    public void setToken_twittersecret(String token_twittersecret) {
        this.token_twittersecret = token_twittersecret;
    }

    public int getCount_newsfeeds() {
        return count_newsfeeds;
    }

    public void setCount_newsfeeds(int count_newsfeeds) {
        this.count_newsfeeds = count_newsfeeds;
    }

    public int getCount_notifications() {
        return count_notifications;
    }

    public void setCount_notifications(int count_notifications) {
        this.count_notifications = count_notifications;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id_user);
        dest.writeString(username);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(bio);
        dest.writeString(photo);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeByte((byte) (follow ? 1 : 0));
        dest.writeInt(following);
        dest.writeInt(follower);
        dest.writeInt(events);
        //dest.writeTypedList(connections);
        dest.writeString(city);
        dest.writeString(city_code);
        dest.writeString(country);
        dest.writeByte((byte) (blocked ? 1 : 0));
        dest.writeString(id_facebook);
        dest.writeString(id_twitter);
        dest.writeString(link);
        dest.writeString(token);
        dest.writeString(message);
    }

    public User() {
    }

    private User(Parcel obj) {
        id_user = obj.readInt();
        username = obj.readString();
        first_name = obj.readString();
        last_name = obj.readString();
        bio = obj.readString();
        photo = obj.readString();
        email = obj.readString();
        phone = obj.readString();
        follow = obj.readByte() != 0;
        following = obj.readInt();
        follower = obj.readInt();
        events = obj.readInt();
        city = obj.readString();
        city_code = obj.readString();
        country = obj.readString();
        blocked = obj.readByte() != 0;
        id_facebook = obj.readString();
        id_twitter = obj.readString();
        link = obj.readString();
        token = obj.readString();
        message = obj.readString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", username='" + username + '\'' +
                ", fbUsername='" + fbUsername + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", bio='" + bio + '\'' +
                ", photo='" + photo + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", follow=" + follow +
                ", following=" + following +
                ", follower=" + follower +
                ", events=" + events +
                ", connections=" + connections +
                ", city='" + city + '\'' +
                ", city_code='" + city_code + '\'' +
                ", country='" + country + '\'' +
                ", blocked=" + blocked +
                ", id_facebook='" + id_facebook + '\'' +
                ", id_twitter='" + id_twitter + '\'' +
                ", link='" + link + '\'' +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                ", count_newsfeeds=" + count_newsfeeds +
                ", count_notifications=" + count_notifications +
                ", token_twitter='" + token_twitter + '\'' +
                ", token_twittersecret='" + token_twittersecret + '\'' +
                ", token_facebook='" + token_facebook + '\'' +
                '}';
    }
}
