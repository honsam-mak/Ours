package com.maksapplab.ours.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.maksapplab.ours.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.maksapplab.ours.constants.PhotoConstant.PREFIX_PHOTO;

/**
 * Created by honsam on 10/9/14.
 */
public class PhotoUtil {

    /*
     * Copy the photo into our folder
     * Return the target path
     */
    public static Uri copyPhoto(Intent data, Context context) {

        String sourcePath = getImagePathFromCameraData(data, context);
        if(sourcePath != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = PREFIX_PHOTO + timeStamp + ".jpg";
            String storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)
                    + File.separator
                    + context.getResources().getString(R.string.app_name);;
            String targetPath = storageDir + File.separator + imageFileName;
            File source = new File(sourcePath);
            File dest = new File(targetPath);
            try {
                FileUtils.copyFile(source, dest);

                /*
                 * It's required to send broadcast after copying photo
                 */
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(dest);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                return Uri.parse(targetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     *  This way has some issue. If choosing google alubm, it will return 'null'
     */
    public static String getImagePathFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
}
