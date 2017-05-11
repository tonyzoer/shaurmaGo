package com.unicyb.shaurmago.models;

/**
 * Created by Zoer on 10.05.2017.
 */

public class CommentModel {
    String user_name;
    String comment;
    Double rate;

    public CommentModel(String user_name, String comment, Double rate) {
        this.user_name = user_name;
        this.comment = comment;
        this.rate = rate;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
