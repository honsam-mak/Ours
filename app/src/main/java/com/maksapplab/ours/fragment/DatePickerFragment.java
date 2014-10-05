package com.maksapplab.ours.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.maksapplab.ours.utilities.DateUtil.DATE_FORMAT;

public class DatePickerFragment extends DialogFragment
                    implements DatePickerDialog.OnDateSetListener {

    boolean fired = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String pregnantDate = PropertyManager.getInstance().getPregnantDate();

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(DateUtil.parse(pregnantDate));

        int newYear = newCalendar.get(Calendar.YEAR);
        int newMonth = newCalendar.get(Calendar.MONTH);
        int newDay = newCalendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, newYear, newMonth, newDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        if(!fired) {
            Calendar calendar = new GregorianCalendar(year, month, day);
              PropertyManager.getInstance().setPregnantDate(
                    new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime()));

            fired = true;
        }
    }
}
