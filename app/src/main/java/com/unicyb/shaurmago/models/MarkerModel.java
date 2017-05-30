package com.unicyb.shaurmago.models;

/**
 * Created by mafio on 26.05.2017.
 */

public class MarkerModel {
   private String id;
   private Double Lat;
   private Double Lng;
   private String name;

    public MarkerModel(String id, Double lat, Double lng, String name) {
        this.id = id;
        Lat = lat;
        Lng = lng;
        this.name = name;
    }

    public MarkerModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
