package com.maksapplab.ours.adapters.items;

import android.net.Uri;

import java.util.Date;

/**
 * Used to represent a photo item.
 */
public class PhotoItem {

    private Uri thumbnailUri;
    private Uri fullImageUri;
    private Date dateTaken;

    public PhotoItem(Uri thumbnailUri,Uri fullImageUri, Date dateTaken) {
        this.thumbnailUri = thumbnailUri;
        this.fullImageUri = fullImageUri;
        this.dateTaken = dateTaken;
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

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }
}
