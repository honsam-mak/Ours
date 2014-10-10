package com.maksapplab.ours.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.maksapplab.ours.R;
import com.maksapplab.ours.utilities.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.maksapplab.ours.constants.PhotoConstant.SUFFIX_DISPLAY_DATE;

/**
 * Created by honsam on 9/28/14.
 */
public class PropertyManager {

    public static String PREF_NAME = "OURS";
    public static String PREGNANT_DATE = "PREGNANT_DATE";
    public static String COVER_PATH = "COVER_FILE_PATH";

    private Context context;

    private static PropertyManager instance = null;

    protected PropertyManager() {}

    public static PropertyManager getInstance() {
        if(instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setProperty(String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);

        editor.commit();
    }

    public String getProperty(String key, String defaultValue) {
        //Restore preferences
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return settings.getString(key, defaultValue);
    }

    public void setPregnantDate(String dateFormat) {
        setProperty(PREGNANT_DATE, dateFormat);
    }

    public String getPregnantDate() {
        return getProperty(
                PREGNANT_DATE,
                new SimpleDateFormat(DateUtil.DATE_FORMAT).format(Calendar.getInstance().getTime()));
    }

    public void setCoverImage(String filepath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filepath);

        ImageView mImageView = (ImageView) ((Activity)context).findViewById(R.id.cover);
        mImageView.setImageBitmap(bitmap);

        setProperty(COVER_PATH, filepath);
    }

    public String getCoverImage() {
        return getProperty(COVER_PATH, null);
    }

    public void saveDisplayDateByCreatedTime(String imageName) {
        PropertyManager.getInstance().setProperty(
                imageName + SUFFIX_DISPLAY_DATE,
                new SimpleDateFormat(DateUtil.DATE_FORMAT).format(Calendar.getInstance().getTime()));
    }
}
