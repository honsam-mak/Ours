package com.maksapplab.ours.utilities;

import android.util.Log;

import com.maksapplab.ours.adapters.items.PhotoItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by honsam on 9/28/14.
 */
public class PhotoItemMap {

    private ArrayList<PhotoItem> photoItemList;
    private Date targetDate;
    private TreeMap<Long, ArrayList<PhotoItem>> photoItemMap  = new TreeMap<Long, ArrayList<PhotoItem>>();
    private int calUnit;

    public PhotoItemMap(ArrayList<PhotoItem> photoItemList, Date targetDate, int calUnit) {
        this.photoItemList = photoItemList;
        this.targetDate = targetDate;
        this.calUnit = calUnit;
        process();
    }

    public TreeMap<Long, ArrayList<PhotoItem>> getResultMap() {
        return photoItemMap;
    }

    private void process() {
        for(PhotoItem photoItem : photoItemList) {
            Long diff = DateUtil.getDateDiff(targetDate, photoItem.getDisplayDate(), calUnit);
            ArrayList<PhotoItem> photoItemList;
            if(photoItemMap.containsKey(diff)) {
                photoItemList = photoItemMap.get(diff);
                photoItemList.add(photoItem);
            } else {
                photoItemList = new ArrayList<PhotoItem>();
                photoItemList.add(photoItem);
            }
            photoItemMap.put(diff, photoItemList);
        }
        print();
    }

    private void print() {
        for(Long d : photoItemMap.keySet()) {
            Log.i("Map", "diff = " + d);
            ArrayList<PhotoItem> itemList = photoItemMap.get(d);
            for(PhotoItem pi : itemList) {
                Log.i("List", "item = " + pi.getFullImageUri());
            }
        }
    }
}
