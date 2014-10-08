package com.maksapplab.ours.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.maksapplab.ours.constants.DatePickerConstant;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.maksapplab.ours.constants.DatePickerConstant.*;
import static com.maksapplab.ours.constants.PhotoConstant.*;
import static com.maksapplab.ours.utilities.DateUtil.DATE_FORMAT;

public class DatePickerFragment extends DialogFragment
                    implements DatePickerDialog.OnDateSetListener {

    boolean fired = false;
    int dateType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            dateType = getArguments().getInt(DatePickerConstant.TYPE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar newCalendar = Calendar.getInstance();

        switch(dateType) {
           case PREGNANT_DATE:
               String pregnantDate = PropertyManager.getInstance().getPregnantDate();
               newCalendar.setTime(DateUtil.parse(pregnantDate));
               break;
            case DISPLAY_DATE:
                String imageName = getArguments().getString(NAME);

                String displayDate = PropertyManager.getInstance().getProperty(
                        imageName + SURFIX_DISPLAY_DATE,
                        new SimpleDateFormat(DateUtil.DATE_FORMAT).format(Calendar.getInstance().getTime()));
                newCalendar.setTime(DateUtil.parse(displayDate));
                break;
        }

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
            switch(dateType) {
                case PREGNANT_DATE:
                    PropertyManager.getInstance().setPregnantDate(
                            new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime()));
                    break;
                case DISPLAY_DATE:
                    String imageName = getArguments().getString(NAME);
                    PropertyManager.getInstance().setProperty(
                            imageName + SURFIX_DISPLAY_DATE,
                            new SimpleDateFormat(DateUtil.DATE_FORMAT).format(calendar.getTime()));
                    break;
            }

            fired = true;
        }
    }
}
