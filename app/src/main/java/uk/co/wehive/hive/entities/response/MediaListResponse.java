package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Media;

public class MediaListResponse extends HiveResponse {

    private ArrayList<Media> data;

    public ArrayList<Media> getData() {
        return data;
    }

    public void setData(ArrayList<Media> data) {
        this.data = data;
    }
}