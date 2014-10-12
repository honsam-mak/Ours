package com.maksapplab.ours.utilities;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.maksapplab.ours.adapters.items.PhotoItem;

import java.io.File;
import java.util.ArrayList;
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

    public static List<PhotoItem> getAlbumPhotos() {

        File[] files = new File(CAMERA_IMAGE_BUCKET_NAME).listFiles();
        List<PhotoItem> result = new ArrayList<PhotoItem>(files.length);

        for(File file : files) {
            if(file.isFile()) {
                String photoPath = file.getPath();
                String thumbnalPath = ThumbnailUtil.toThumbnailName(photoPath);

                File f = new File(thumbnalPath);
                if(f.exists()) {
                    PhotoItem newItem = new PhotoItem(Uri.parse(thumbnalPath), Uri.parse(photoPath));
                    result.add(newItem);
                } else {
                    Log.i("ImageProvider", "this image has no thumbnail : " + file.getName());
                }
            }
        }
        return result;
    }
}
