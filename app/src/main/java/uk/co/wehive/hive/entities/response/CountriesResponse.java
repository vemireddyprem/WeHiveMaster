package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.Countries;

public class CountriesResponse extends HiveResponse {

    private ArrayList<Countries> data = new ArrayList<Countries>();

    public ArrayList<Countries> getData() {
        return data;
    }

    public void setData(ArrayList<Countries> data) {
        this.data = data;
    }
}