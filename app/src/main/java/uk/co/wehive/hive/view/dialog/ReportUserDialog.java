package uk.co.wehive.hive.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;

public class ReportUserDialog extends GATrackerDialogFragment {

    private IOptionSelected typeSelectedListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.report_user_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                typeSelectedListener.onTypeSelected(which);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // Create the AlertDialog object
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setTypeSelectedListener(IOptionSelected typeSelectedListener) {
        this.typeSelectedListener = typeSelectedListener;
    }
}
