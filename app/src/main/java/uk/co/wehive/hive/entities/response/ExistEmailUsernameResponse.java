/*******************************************************************************
 PROJECT:       Hive
 FILE:          HiveResponse.java
 DESCRIPTION:   Entity for parse result of Users/existUsername.json and Users/existEmail.json webservice
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        10/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.entities.response;

public class ExistEmailUsernameResponse extends HiveResponse {

    private Exist data;

    public Exist getData() {
        return data;
    }

    public void setData(Exist data) {
        this.data = data;
    }
}
