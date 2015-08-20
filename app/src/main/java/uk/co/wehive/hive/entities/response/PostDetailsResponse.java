package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.Post;

/**
 * Camilo Cabrales on 30/07/2014.
 */
public class PostDetailsResponse extends HiveResponse{

    public Post getData() {
        return data;
    }

    public void setData(Post post) {
        this.data = data;
    }

    private Post data;
}
