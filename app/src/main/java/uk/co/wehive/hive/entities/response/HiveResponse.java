/*******************************************************************************
 PROJECT:       Hive
 FILE:          HiveResponse.java
 DESCRIPTION:   Entity for parse result of webservice
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiveResponse {

    private Boolean status;
    private List<String> message = new ArrayList<String>();
    private Map<Object, Object> errors;
    private String accessToken;
    private int count_newsfeed;
    private Double executionTime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public Map<Object, Object> getErrors() {
        return errors;
    }

    public void setErrors(Map<Object, Object> errors) {
        this.errors = errors;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getCount_newsfeed() {
        return count_newsfeed;
    }

    public void setCount_newsfeed(int count_newsfeed) {
        this.count_newsfeed = count_newsfeed;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
        this.executionTime = executionTime;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getMessageError() {
        String error = "";
        if (message.size() > 0) {
            error = message.get(0);
        } else {
            for (Map.Entry<Object, Object> errorsInfo : errors.entrySet()) {
                try {
                    for (String value : (ArrayList<String>) errorsInfo.getValue()) {
                        error = error + " " + value;
                    }
                } catch (Exception e) {
                    error = error + " ";
                }
            }
        }
        return error.trim();
    }
}