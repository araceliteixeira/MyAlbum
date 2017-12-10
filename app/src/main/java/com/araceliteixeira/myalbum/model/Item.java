package com.araceliteixeira.myalbum.model;

import android.graphics.Bitmap;

/**
 * Created by araceliteixeira on 08/12/17.
 */

public class Item {
    private long id;
    private Bitmap image;
    private String label;
    private String photographer;
    private boolean isSelected = false;

    public Item() {
    }

    public Item(Bitmap image, String label, String photographer) {
        this.image = image;
        this.label = label;
        this.photographer = photographer;
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

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void toggleSelected(){
        isSelected = !isSelected;
    }
}
