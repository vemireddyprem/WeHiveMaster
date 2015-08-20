package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Post;

public class EventPostsResponse extends HiveResponse {

    private ArrayList<Post> data = new ArrayList<Post>();

    public ArrayList<Post> getData() {
        return data;
    }

    public void setData(ArrayList<Post> data) {
        this.data = data;
    }
}
