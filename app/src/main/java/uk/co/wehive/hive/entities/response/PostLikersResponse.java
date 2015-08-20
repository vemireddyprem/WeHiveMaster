package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

public class PostLikersResponse extends HiveResponse {

    private ArrayList<Follower> data;

    public ArrayList<Follower> getData() {
        return data;
    }

    public void setData(ArrayList<Follower> data) {
        this.data = data;
    }
}