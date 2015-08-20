package uk.co.wehive.hive.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Arrays;

import uk.co.wehive.hive.adapter.FontSizeAdapter;

public class TextSizeListDialog extends DialogFragment {

    private static String[] FONT_SIZES = {"8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23", "24", "25"};

    private IListItemClick mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(new FontSizeAdapter(getActivity(), Arrays.asList(FONT_SIZES)),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        mListener.onListItemSelected(Integer.parseInt(FONT_SIZES[item]));
                        dialog.dismiss();
                    }
                }
        );
        Dialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(400, 600);
        return alertDialog;
    }

    public void setItemSelectedListener(IListItemClick mListener) {
        this.mListener = mListener;
    }

    public interface IListItemClick {
        public void onListItemSelected(int selectedItem);
    }
}