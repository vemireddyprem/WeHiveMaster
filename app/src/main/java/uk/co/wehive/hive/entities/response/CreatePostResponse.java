package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.CreatePost;

public class CreatePostResponse extends HiveResponse {

    private CreatePost data;

    public CreatePost getData() {
        return data;
    }

    public void setData(CreatePost data) {
        this.data = data;
    }
}