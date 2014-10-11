package com.maksapplab.ours.utilities;

import android.util.Log;

import com.maksapplab.ours.adapters.items.PhotoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by honsam on 10/10/14.
 */
public class LogUtil {

    public static void printPhotoMap(Map<Long, ArrayList<PhotoItem>> photoItemMap) {
        for(Long d : photoItemMap.keySet()) {
            Log.i("LogUtil", "week = " + d);
            ArrayList<PhotoItem> itemList = photoItemMap.get(d);
            for(PhotoItem pi : itemList) {
                Log.i("LogUtil", "map item = " + pi.getFullImageUri());
            }
        }
    }

    public static void printPhotoList(List<PhotoItem> photoItemList) {
        Log.i("LogUtil", "total list has " + photoItemList.size());
//        for(PhotoItem item : photoItemList) {
//            Log.i("LogUtil", "list item = " + item.getFullImageUri());
//        }
    }
}
