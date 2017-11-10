package edu.bucknell.seniordesign;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Jack on 9/29/2017.
 */

public class Location implements Serializable{

    private String locationName;
    private String locationDescription;
    private int GPS;

    private double lat;
    private double lng;

    private LatLng latLng;

    public Location (String name) {
        this.locationName = name;
    }

    public Location(String name, String description, LatLng latLng) {
        this.locationName = name;
        this.locationDescription = description;
        this.latLng = latLng;
    }

    public Location(String name, String description, double lat, double lng) {
        this.locationName = name;
        this.locationDescription = description;
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public String getLocationName(){
        return locationName;
    }

    public String getLocationDescription(){
        return locationDescription;
    }

    public void setLocationName(String name){
        this.locationName = name;
    }

    public void setLocationDescription(String description){
        this.locationDescription = description;
    }

    public Location() {}
}
