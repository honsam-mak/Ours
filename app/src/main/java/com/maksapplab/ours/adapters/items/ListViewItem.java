package com.maksapplab.ours.adapters.items;

import java.util.ArrayList;

/**
 * Created by honsam on 9/30/14.
 */
public class ListViewItem {

    private String mTitle;
    private ArrayList<PhotoItem> mPhotoListItem;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public ArrayList<PhotoItem> getPhotoItems() {
        return mPhotoListItem;
    }

    public void setPhotoItems(ArrayList<PhotoItem> photoItems) {
        this.mPhotoListItem = photoItems;
    }
}
