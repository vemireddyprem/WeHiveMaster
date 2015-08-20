/*******************************************************************************
 PROJECT:       Hive
 FILE:          SelectPictureDialog.java
 DESCRIPTION:   Show dialog for selected type of picture
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        11/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/

package uk.co.wehive.hive.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import uk.co.wehive.hive.R;
import uk.co.wehive.hive.listeners.dialogs.IOptionSelected;

public class SelectPictureDialog extends DialogFragment {

    private IOptionSelected typeSelectedListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_image));
        builder.setItems(R.array.image_type, new DialogInterface.OnClickListener() {
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
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setTypeSelectedListener(IOptionSelected typeSelectedListener) {
        this.typeSelectedListener = typeSelectedListener;
    }
}
