package uk.co.wehive.hive.entities;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.response.Follower;

public class MoreEventDetail {

    private int id_event;
    private int total_followers;
    private String date_event;
    private String date_end_event;
    private int media_count;
    private ArrayList<Follower> followers;
    private int total_followers_friends;
    private String lineup;
    private String status_event;

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public int getTotal_followers() {
        return total_followers;
    }

    public void setTotal_followers(int total_followers) {
        this.total_followers = total_followers;
    }

    public String getDate_event() {
        return date_event;
    }

    public void setDate_event(String date_event) {
        this.date_event = date_event;
    }

    public String getDate_end_event() {
        return date_end_event;
    }

    public void setDate_end_event(String date_end_event) {
        this.date_end_event = date_end_event;
    }

    public int getMedia_count() {
        return media_count;
    }

    public void setMedia_count(int media_count) {
        this.media_count = media_count;
    }

    public ArrayList<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<Follower> followers) {
        this.followers = followers;
    }

    public int getTotal_followers_friends() {
        return total_followers_friends;
    }

    public void setTotal_followers_friends(int total_followers_friends) {
        this.total_followers_friends = total_followers_friends;
    }

    public String getLineup() {
        return lineup;
    }

    public void setLineup(String lineup) {
        this.lineup = lineup;
    }

    public String getStatus_event() {
        return status_event;
    }

    public void setStatus_event(String status_event) {
        this.status_event = status_event;
    }
}