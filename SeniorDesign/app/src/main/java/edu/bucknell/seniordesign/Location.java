package edu.bucknell.seniordesign;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Jack on 9/29/2017.
 */

public class Location implements Serializable{

    private String LocationName;
    private String LocationDescription;
    private int GPS;

    private double lat;
    private double lng;

    public Location(String Name, String Description, double lat, double lng){
        this.LocationName= Name;
        this.LocationDescription= Description;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {return lat; }
    public double getLng() {return lng; }

    public String getLocationName(){
        return LocationName;
    }

    public String getLocationDescription(){
        return LocationDescription;
    }

    public void setLocationName(String name){
        this.LocationName= name;
    }

    public void setLocationDescription(String description){
        this.LocationDescription= description;
    }

    public Location() {}
}
