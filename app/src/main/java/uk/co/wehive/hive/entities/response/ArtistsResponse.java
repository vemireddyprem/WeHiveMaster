package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

public class ArtistsResponse extends HiveResponse {

    private ArrayList<Artist> data;

    public ArrayList<Artist> getData() {
        return data;
    }

    public void setData(ArrayList<Artist> data) {
        this.data = data;
    }
}