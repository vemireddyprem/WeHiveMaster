package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Cities;

public class CitiesResponse extends HiveResponse {

    private ArrayList<Cities> data = new ArrayList<Cities>();

    public ArrayList<Cities> getData() {
        return data;
    }

    public void setData(ArrayList<Cities> data) {
        this.data = data;
    }
}
