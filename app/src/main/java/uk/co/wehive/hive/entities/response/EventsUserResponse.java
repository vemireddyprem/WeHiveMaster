package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.EventsUser;

public class EventsUserResponse extends HiveResponse {

    private ArrayList<EventsUser> data;

    public ArrayList<EventsUser> getData() {
        return data;
    }

    public void setData(ArrayList<EventsUser> data) {
        this.data = data;
    }
}