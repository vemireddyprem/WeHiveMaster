package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.FollowUser;

public class FollowUserResponse extends HiveResponse {

    private FollowUser data;

    public FollowUser getData() {
        return data;
    }

    public void setData(FollowUser data) {
        this.data = data;
    }
}
