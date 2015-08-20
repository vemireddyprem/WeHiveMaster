package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.Video;

public class VideoUploadResponse extends HiveResponse {

    private Video data;

    public Video getData() {
        return data;
    }

    public void setData(Video data) {
        this.data = data;
    }
}
