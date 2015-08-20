package uk.co.wehive.hive.view.dialog;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment implements OnDateSetListener {

    private DatePickerListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onResult(year, month, day);
    }

    public void setDateChangedListener(DatePickerListener listener) {
        mListener = listener;
    }

    public interface DatePickerListener {
        void onResult(int year, int month, int day);
    }
}