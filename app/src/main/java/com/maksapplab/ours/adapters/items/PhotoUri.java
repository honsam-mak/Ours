package com.maksapplab.ours.adapters.items;

import java.io.Serializable;

/**
 * Created by honsam on 10/10/14.
 */
public class PhotoUri implements Serializable{
    private String imagePath;

    public PhotoUri(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
