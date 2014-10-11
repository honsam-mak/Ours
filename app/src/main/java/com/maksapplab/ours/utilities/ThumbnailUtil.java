package com.maksapplab.ours.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.maksapplab.ours.constants.PhotoConstant.PREFIX_PHOTO;
import static com.maksapplab.ours.constants.PhotoConstant.PREFIX_THUMBNAIL;
import static com.maksapplab.ours.constants.PhotoConstant.THUMBNAIL_FOLDER;

/**
 * Created by honsam on 9/25/14.
 */
public class ThumbnailUtil {

    private static final int IMAGE_RESOLUTION = 15;

    public static Bitmap getBitmap(Uri uri) {
        try {
            //第一次 decode,只取得圖片長寬,還未載入記憶體
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(uri.getPath(), opts);

            //The new size to decode to
            final int NEW_SIZE=100;

            //Now we have image width and height. We should find the correct scale value. (power of 2)
            int width=opts.outWidth;
            int height=opts.outHeight;
            int scale=1;
            while(true){
                if(width/2<NEW_SIZE || height/2<NEW_SIZE)
                    break;

                width/=2;
                height/=2;
                scale++;
            }

            //Decode again with inSampleSize
            opts = new BitmapFactory.Options();
            opts.inSampleSize=scale;

            Bitmap bmp = BitmapFactory.decodeFile(uri.getPath(), opts);

            return bmp;
        } catch (Exception err) {
            return null;
        }
    }

    public static String toThumbnailName(String path) {
        return path.replace(PREFIX_PHOTO, THUMBNAIL_FOLDER+File.separator+PREFIX_THUMBNAIL);
    }

    public static void createThumbnails(Uri uri) {

        try {
            String filePath = toThumbnailName(uri.getPath());

            File file = new File(filePath);

            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();

            // 壓成 JPEG 存檔
            FileOutputStream fos = new FileOutputStream(filePath);
            Bitmap resizedBitmap = getBitmap(uri);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e("ThumbnailUtil", "Fail to createThumbnail");
        }
    }

    /**
     * Render a thumbnail photo and scale it down to a smaller size.
     * @param path
     * @return
     */
    private static Bitmap bitmapFromPath(String path){
        File imgFile = new File(path);
        Bitmap imageBitmap = null;

        if(imgFile.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = IMAGE_RESOLUTION;
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        }
        return imageBitmap;
    }

}
