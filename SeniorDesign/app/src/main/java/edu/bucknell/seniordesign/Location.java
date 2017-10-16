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

    private LatLng latLong;

    public Location(String Name, String Description, LatLng latLong){
        this.LocationName= Name;
        this.LocationDescription= Description;
        this.latLong = latLong;
    }

    public LatLng getLatLong() {return latLong; }

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
}
