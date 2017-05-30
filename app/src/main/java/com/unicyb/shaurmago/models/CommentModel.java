package com.unicyb.shaurmago.models;

/**
 * Created by Zoer on 10.05.2017.
 */

public class CommentModel {
    private String id;
    private String user_id;
    private String pointInfoId;
    private String comment;
    private Double rate;

    public CommentModel() {
    }

    public CommentModel(String user_name, String comment, Double rate) {
        this.user_id = user_name;
        this.comment = comment;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPointInfoId() {
        return pointInfoId;
    }

    public void setPointInfoId(String pointInfoId) {
        this.pointInfoId = pointInfoId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
