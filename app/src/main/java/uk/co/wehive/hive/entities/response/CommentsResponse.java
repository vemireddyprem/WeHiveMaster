package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Comment;

public class CommentsResponse extends HiveResponse {

    private ArrayList<Comment> data;

    public ArrayList<Comment> getData() {
        return data;
    }

    public void setData(ArrayList<Comment> data) {
        this.data = data;
    }
}
