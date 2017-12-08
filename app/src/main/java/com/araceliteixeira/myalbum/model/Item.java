package com.araceliteixeira.myalbum.model;

import android.graphics.Bitmap;

/**
 * Created by araceliteixeira on 08/12/17.
 */

public class Item {
    private long id;
    private Bitmap image;
    private String label;

    public Item() {
    }

    public Item(Bitmap image, String label) {
        this.image = image;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
