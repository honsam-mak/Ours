package com.maksapplab.ours.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.maksapplab.ours.constants.DatePickerConstant;
import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.maksapplab.ours.utilities.DateUtil.DATE_FORMAT;

public class DatePickerFragment extends DialogFragment
                    implements DatePickerDialog.OnDateSetListener {

    boolean fired = false;
    int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt(DatePickerConstant.TYPE);
            Log.i("DatePicker", "type = " + type);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//           type = savedInstanceState.getInt(DatePickerConstant.TYPE);
//            Log.i("DatePicker", "type = " + type);
//        }
        Calendar newCalendar = Calendar.getInstance();

        if(type == DatePickerConstant.PREGNANT_DATE) {
            String pregnantDate = PropertyManager.getInstance().getPregnantDate();
            newCalendar.setTime(DateUtil.parse(pregnantDate));
        }
        if(type == DatePickerConstant.DISPLAY_DATE) {
            String imageName = getArguments().getString("ImageName");

            String displayDate = PropertyManager.getInstance().getProperty(
                    imageName + "_displaydate",
                    new SimpleDateFormat(DateUtil.DATE_FORMAT).format(Calendar.getInstance().getTime()));
            newCalendar.setTime(DateUtil.parse(displayDate));
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
            if(type == DatePickerConstant.PREGNANT_DATE) {
                Log.i("DatePicker", "pregnant date is picked");
                PropertyManager.getInstance().setPregnantDate(
                        new SimpleDateFormat(DATE_FORMAT).format(calendar.getTime()));
            }
            if(type == DatePickerConstant.DISPLAY_DATE) {
                Log.i("DatePicker", "display date is picked");
                String imageName = getArguments().getString("ImageName");
                PropertyManager.getInstance().setProperty(
                        imageName + "_displaydate",
                        new SimpleDateFormat(DateUtil.DATE_FORMAT).format(calendar.getTime()));
            }

            fired = true;
        }
    }
}
