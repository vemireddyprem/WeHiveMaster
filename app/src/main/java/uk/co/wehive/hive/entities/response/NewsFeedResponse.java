package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.NewsFeed;

public class NewsFeedResponse extends HiveResponse {

    private ArrayList<NewsFeed> data;

    public ArrayList<NewsFeed> getData() {
        return data;
    }

    public void setData(ArrayList<NewsFeed> data) {
        this.data = data;
    }
}
