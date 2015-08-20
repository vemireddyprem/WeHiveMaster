package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.EventDetail;

public class EventDetailResponse extends HiveResponse {

    private EventDetail data;

    public EventDetail getData() {
        return data;
    }

    public void setData(EventDetail data) {
        this.data = data;
    }
}
