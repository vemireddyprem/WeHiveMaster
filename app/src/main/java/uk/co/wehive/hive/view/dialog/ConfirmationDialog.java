/*******************************************************************************
 PROJECT:       Hive
 FILE:          ConfirmationDialog.java
 DESCRIPTION:   Show dialog of user profile settings
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        17/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.listeners.dialogs.IConfirmation;

public class ConfirmationDialog extends DialogFragment {

    private static IConfirmation mConfirmationListener;

    public static void showMessage(Context activity, String title, String message, IConfirmation iConfirmation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        mConfirmationListener = iConfirmation;
        if (message != null && message.length() > 0) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mConfirmationListener.positiveSelected();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mConfirmationListener.negativeSelected();
            }
        });

        builder.show();
    }
}
