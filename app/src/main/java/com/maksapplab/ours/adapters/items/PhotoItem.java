package com.maksapplab.ours.adapters.items;

import android.net.Uri;

import com.maksapplab.ours.manager.PropertyManager;
import com.maksapplab.ours.utilities.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.maksapplab.ours.constants.PhotoConstant.SUFFIX_DISPLAY_DATE;

/**
 * Used to represent a photo item.
 */
public class PhotoItem {

    private Uri thumbnailUri;
    private Uri fullImageUri;

    public PhotoItem(Uri thumbnailUri,Uri fullImageUri) {
        this.thumbnailUri = thumbnailUri;
        this.fullImageUri = fullImageUri;
    }

    /**
     * Getters and setters
     */
    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public Uri getFullImageUri() {
        return fullImageUri;
    }

    public void setFullImageUri(Uri fullImageUri) {
        this.fullImageUri = fullImageUri;
    }

    public Date getDisplayDate() {

        String displayDate = PropertyManager.getInstance().getProperty(
                getFullImageUri().getLastPathSegment() + SUFFIX_DISPLAY_DATE,
                new SimpleDateFormat(DateUtil.DATE_FORMAT).format(Calendar.getInstance().getTime()));

        return DateUtil.parse(displayDate);
    }
}
