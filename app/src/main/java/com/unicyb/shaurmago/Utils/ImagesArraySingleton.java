package com.unicyb.shaurmago.Utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mafio on 10/29/2017.
 */

public class ImagesArraySingleton {
    private static ImagesArraySingleton instance = new ImagesArraySingleton();

    List<Bitmap> images = new ArrayList<>();

    public static ImagesArraySingleton getInstance() {
        return instance;
    }

    public List<Bitmap> getImages() {
        return images;
    }

    public void setImages(List<Bitmap> images) {
        this.images.addAll(images);
    }

    public void clear(){
        this.images.clear();
    }
    private ImagesArraySingleton() {

    }
}
