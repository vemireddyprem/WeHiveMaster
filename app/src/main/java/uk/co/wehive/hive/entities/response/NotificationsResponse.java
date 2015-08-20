package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;

import uk.co.wehive.hive.entities.NotificationMessage;

public class NotificationsResponse extends HiveResponse {

    private ArrayList<NotificationMessage> data;
    private int count_notifications;

    public ArrayList<NotificationMessage> getData() {
        return data;
    }

    public void setData(ArrayList<NotificationMessage> data) {
        this.data = data;
    }

    public int getCount_notifications() {
        return count_notifications;
    }

    public void setCount_notifications(int count_notifications) {
        this.count_notifications = count_notifications;
    }
}
