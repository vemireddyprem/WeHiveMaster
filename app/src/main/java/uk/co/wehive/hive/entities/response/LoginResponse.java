/*******************************************************************************
 PROJECT:       Hive
 FILE:          HiveResponse.java
 DESCRIPTION:   Entity for parse result of Users/login.json webservice
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        15/07/2014  Juan Pablo B.    1. Initial definition.
 1.0        15/07/2014  Marcela Güiza    2. Added count_newsfeed parameter.
 *******************************************************************************/

package uk.co.wehive.hive.entities.response;

import uk.co.wehive.hive.entities.User;

public class LoginResponse extends HiveResponse {

    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}