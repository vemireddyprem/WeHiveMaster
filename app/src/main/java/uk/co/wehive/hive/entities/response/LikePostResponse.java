package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.LikePost;

public class LikePostResponse extends HiveResponse {

    private LikePost data;

    public LikePost getData() {
        return data;
    }

    public void setData(LikePost data) {
        this.data = data;
    }
}
