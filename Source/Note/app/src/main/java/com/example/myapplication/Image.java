package com.example.myapplication;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Image {
    ImageView img;
    Bitmap bitmap;
    Integer idImage;
    Integer id;

    public Image(Bitmap bitmap, Integer idImage) {
        this.bitmap = bitmap;
        this.idImage=idImage;
    }
    public ImageView getImg() {
        return img;
    }

    public void setImg() {
        this.img.setImageBitmap(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Integer getIdImage() {
        return idImage;
    }

    public void setIdImage(Integer idImage) {
        this.idImage = idImage;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
