package com.maksapplab.ours.utilities;

import android.util.Log;

import com.maksapplab.ours.manager.PropertyManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by honsam on 9/27/14.
 */
public class DateUtil {

    public static String DATE_FORMAT = "yyyy/MM/dd";

    public static Date parse(String date) {
        try {
            if (date != null)
                return new SimpleDateFormat(DATE_FORMAT).parse(date);
            else {
                return Calendar.getInstance().getTime();
            }
        } catch (ParseException e) {
            Log.e("DateUtil", e.getMessage());
        }
        return null;
    }

    public static long getDateDiff( Date d1, Date d2, int calUnit ) {
        if( d1.after(d2) ) {    // make sure d1 < d2, else swap them
            Date temp = d1;
            d1 = d2;
            d2 = temp;
        }
        GregorianCalendar c1 = new GregorianCalendar();
        c1.setTime(d1);
        GregorianCalendar c2 = new GregorianCalendar();
        c2.setTime(d2);
        for( long i=1; ; i++ ) {
            c1.add( calUnit, 1 );   // add one day, week, year, etc.
            if( c1.after(c2) )
                return i;
        }
    }

    public static long getWeekDiff(Date d1, Date d2) {
        if( d1.after(d2) ) {    // make sure d1 < d2, else swap them
            Date temp = d1;
            d1 = d2;
            d2 = temp;
        }
        GregorianCalendar c1 = new GregorianCalendar();
        c1.setTime(d1);
        GregorianCalendar c2 = new GregorianCalendar();
        c2.setTime(d2);
        for( long i=1; ; i++ ) {
            c1.add( Calendar.WEEK_OF_YEAR, 1 );   // add one week
            if( c1.after(c2) )
                return i;
        }
    }

    public static long getPregnantWeek(Date d) {
        String pregnantDate = PropertyManager.getInstance().getPregnantDate();
        Date pregDate = DateUtil.parse(pregnantDate);

        return getWeekDiff(d, pregDate);
    }
}
