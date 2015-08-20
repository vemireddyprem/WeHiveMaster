package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.GoodTimesDetail;

public class GoodTimesDetailResponse extends HiveResponse {

    public GoodTimesDetail getData() {
        return data;
    }

    public void setData(GoodTimesDetail data) {
        this.data = data;
    }

    private GoodTimesDetail data;
}
