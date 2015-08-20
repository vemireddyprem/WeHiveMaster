package uk.co.wehive.hive.entities.response;

public class CreateCommentResponse extends HiveResponse {

    private CommentPost data;

    public CommentPost getData() {
        return data;
    }

    public void setData(CommentPost data) {
        this.data = data;
    }
}
