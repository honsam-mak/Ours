package com.maksapplab.ours.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.maksapplab.ours.adapters.items.PhotoItem;
import com.maksapplab.ours.manager.PropertyManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.maksapplab.ours.constants.PhotoConstant.*;

/**
 * This is a helper utility which automatically fetches paths to full size and thumbnail sized gallery images.
 *
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
public class PhotoGalleryImageProvider {

    // Buckets where we are fetching images from.
    public static final String DESTINATION_FOLDER =
                                PARENT_FOLDER + File.separator + APP_PHOTO_FOLDER;

    public static final String CAMERA_IMAGE_BUCKET_NAME =
                                Environment.getExternalStorageDirectory().toString()
                                + File.separator + DESTINATION_FOLDER;

    public static final String CAMERA_IMAGE_BUCKET_ID =
                                getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static List<PhotoItem> getAlbumPhotos(Context context) {

        final String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN
            };
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID};

        Cursor fullSizePhotoCursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                selection,
                selectionArgs,
                null);

        int photoColumnIndex = fullSizePhotoCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int dateColumnIndex = fullSizePhotoCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
        ArrayList<PhotoItem> result = new ArrayList<PhotoItem>(fullSizePhotoCursor.getCount());

        if (fullSizePhotoCursor.moveToFirst()) {
            do {
                preparePhotoItemList(fullSizePhotoCursor, photoColumnIndex, dateColumnIndex, result);
            } while (fullSizePhotoCursor.moveToNext());
        }
        fullSizePhotoCursor.close();
        return result;
    }

    private static void preparePhotoItemList(
            Cursor cursor,
            int photoColumnIndex,
            int dateColumnIndex,
            ArrayList<PhotoItem> result) {

        int photoImageID = cursor.getInt(photoColumnIndex);
        String photoPath = cursor.getString(photoImageID);

        Long dateInMil = cursor.getLong(dateColumnIndex);

        if(photoPath.contains(DESTINATION_FOLDER)) {
            Uri photoUri = Uri.parse(photoPath);
            String thumbnailPath = ThumbnailUtil.toThumbnailName(photoPath);
            Uri thumbnailUri = Uri.parse(thumbnailPath);

//            Log.d("ImageProvider", "thumbnail uri = " + thumbnailUri);
//            Log.d("ImageProvider", "full size uri = " + photoUri);

//            printDate(new Date(dateInMil));

            File f = new File(thumbnailPath);
            if (f.exists()) {
                // Create the list item.
                PhotoItem newItem = new PhotoItem(thumbnailUri, photoUri, new Date(dateInMil));
                result.add(newItem);
            } else {
                Log.d("ImageProvider", " this image has no thumbnail :" + photoImageID);
            }
        } else {
            Log.d("ImageProvider", " this image has no thumbnail :" + photoImageID);
        }
    }

    private static void printDate(Date d) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.i("Gallery", "date taken = " + sdf.format(d));

        //Restore preferences
        String pregnantDate = PropertyManager.getInstance().getPregnantDate();
        Date pregDate = DateUtil.parse(pregnantDate);

        Log.i("Gallery", "difference in day = " + DateUtil.getDateDiff(d, pregDate, Calendar.DATE));
        Log.i("Gallery", "difference in week = " + DateUtil.getWeekDiff(d, pregDate));
    }
}
