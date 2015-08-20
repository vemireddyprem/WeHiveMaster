package uk.co.wehive.hive.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

import uk.co.wehive.hive.R;

public class ErrorDialog extends DialogFragment {

    public static void showErrorMessage(Activity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        if (message != null && message.length() > 0) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
