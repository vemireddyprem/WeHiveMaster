package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.MoreEventDetail;

public class MoreEventDetailResponse extends HiveResponse{

    private MoreEventDetail data;

    public MoreEventDetail getData() {
        return data;
    }

    public void setData(MoreEventDetail data) {
        this.data = data;
    }
}