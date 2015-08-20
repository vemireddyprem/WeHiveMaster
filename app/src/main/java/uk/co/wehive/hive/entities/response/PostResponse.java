package uk.co.wehive.hive.entities.response;

import java.util.Map;

public class PostResponse extends HiveResponse {

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    private Map<String, Object> data;
}
