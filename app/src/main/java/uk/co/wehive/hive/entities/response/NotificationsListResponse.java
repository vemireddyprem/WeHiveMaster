package uk.co.wehive.hive.entities.response;


import uk.co.wehive.hive.entities.NotificationList;

public class NotificationsListResponse extends HiveResponse {

    private NotificationList data;

    public NotificationList getData() {
        return data;
    }

    public void setData(NotificationList data) {
        this.data = data;
    }
}
