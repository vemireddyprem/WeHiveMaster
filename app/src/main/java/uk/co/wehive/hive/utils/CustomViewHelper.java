/**********************************************************************+++++++++++++++++*********
 PROJECT:       Hive
 FILE:          CustomViewHelper.java
 DESCRIPTION:   Helper to custom views
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        11/07/2014  Marcela Güiza    1. Initial definition, added setUpActionBar method.
 ***********************************************************+++++++++++++++++********************/
package uk.co.wehive.hive.utils;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import uk.co.wehive.hive.R;

public class CustomViewHelper {

    // Update or set ActionBar title
    public static void setUpActionBar(FragmentActivity activity, String title) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            TextView txtTitle = (TextView) actionBar.getCustomView().findViewById(R.id.txt_title_view);
            txtTitle.setText(title);
        }
    }
}