package com.unicyb.shaurmago.models;

/**
 * Created by mafio on 26.05.2017.
 */

public class PointInfoModel {
   private String id;
   private String photo;
   private String desc;

    public PointInfoModel(String id, String photo, String desc) {
        this.id = id;
        this.photo = photo;
        this.desc = desc;
    }

    public PointInfoModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
