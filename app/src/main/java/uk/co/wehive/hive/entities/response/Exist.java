package uk.co.wehive.hive.entities.response;

import java.util.HashMap;
import java.util.Map;

public class Exist {

    private boolean exist;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}