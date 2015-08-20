package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Location;

public class LocationResponse extends HiveResponse {

    private ArrayList<Location> data;

    public ArrayList<Location> getData() {
        return data;
    }

    public void setData(ArrayList<Location> data) {
        this.data = data;
    }
}
