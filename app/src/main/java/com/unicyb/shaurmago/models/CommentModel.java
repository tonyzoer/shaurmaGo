package com.unicyb.shaurmago.models;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoer on 10.05.2017.
 */

public class CommentModel {
    private String id;
    private String user_id;
    private String pointInfoId;
    private String comment;
    private Double tasteRate;
    private Double textureRate;
    private Double pointRate;
    private Double rate;
    private ArrayList<String> imagesPath;
    @Exclude
    private ArrayList<Bitmap> images;
    @Exclude
    private ArrayList<Integer> downloaded;//0-No image, 1-started downloading, 2-downloaded
    public CommentModel(String id, String user_id, String pointInfoId, String comment, Double tasteRate, Double textureRate, Double pointRate, ArrayList<String> imagesPath) {
        this.id = id;
        this.user_id = user_id;
        this.pointInfoId = pointInfoId;
        this.comment = comment;
        this.tasteRate = tasteRate;
        this.textureRate = textureRate;
        this.pointRate = pointRate;
        this.imagesPath = imagesPath;
        images=new ArrayList<>();
        imagesPath=new ArrayList<>();
        downloaded=new ArrayList<>();
    }

    public CommentModel() {
        images=new ArrayList<>();
        imagesPath=new ArrayList<>();
        downloaded=new ArrayList<>();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPointInfoId() {
        return pointInfoId;
    }

    public void setPointInfoId(String pointInfoId) {
        this.pointInfoId = pointInfoId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getTasteRate() {
        return tasteRate;
    }

    public void setTasteRate(Double tasteRate) {
        this.tasteRate = tasteRate;
    }

    public Double getTextureRate() {
        return textureRate;
    }

    public void setTextureRate(Double textureRate) {
        this.textureRate = textureRate;
    }

    public Double getPointRate() {
        return pointRate;
    }

    public void setPointRate(Double pointRate) {
        this.pointRate = pointRate;
    }

    public Double getRate() {
        return (pointRate + tasteRate + textureRate) / 3;
    }

    public void setRate(Double rate){this.rate=rate;}


    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void setImages(ArrayList<Bitmap> images) {
        this.images = images;
    }
    public void addImage(Bitmap bmp,int i){
        images.add(bmp);
        setDownloaded(i);
    }

    public ArrayList<String> getImagesPath() {
        return imagesPath;
    }
    @Exclude
    public ArrayList<String> getNotDownloadedImagesPath() {
        ArrayList<String> notDownloaded=new ArrayList<>();
        for (int i = 0; i <imagesPath.size() ; i++) {
            if (downloaded.get(i)==0)notDownloaded.add(imagesPath.get(i));
        }
        return notDownloaded;
    }


    public void setImagesPath(ArrayList<String> imagesPath) {
        this.imagesPath = imagesPath;
        for (int i = 0; i <imagesPath.size() ; i++) {
            downloaded.add(0);
        }
    }

    public void setDownloaded(int i) {
        this.downloaded.set(i-1,2);
    }
    public void setStartedDownloading(int i){
        this.downloaded.set(i-1,1);
    }
    public ArrayList<Integer> getDownloaded(){return downloaded;}
}
