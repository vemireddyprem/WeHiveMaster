/*******************************************************************************
 PROJECT:       Hive
 FILE:          GoodTimesResponse.java
 DESCRIPTION:   Entity for parse result of Users/login.json webservice
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        18/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.entities.response;

import java.util.ArrayList;
import java.util.List;

import uk.co.wehive.hive.entities.GoodTimes;

public class GoodTimesResponse extends HiveResponse {

    private List<GoodTimes> data = new ArrayList<GoodTimes>();

    public List<GoodTimes> getData() {
        return data;
    }

    public void setData(List<GoodTimes> data) {
        this.data = data;
    }
}