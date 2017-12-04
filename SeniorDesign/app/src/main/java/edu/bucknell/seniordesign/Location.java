package edu.bucknell.seniordesign;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Location.java
 * TraveList - Senior Design
 *
 * A class to represent a Location
 *
 * Created by Jack on 9/29/2017.
 */

public class Location implements Serializable{

    private Bitmap pic;
    // Location name
    private String locationName;

    // Location description
    private String locationDescription;

    // Whether or not the Location has been visited
    private boolean visited;

    // URL of the Location's image
    private String imageUrl;

    // Latitude/Longitude coordinates
    private TraveListLatLng traveListLatLng= new TraveListLatLng();

    // No arguments Location constructor
    public Location() {}

    // Location constructor given a name
    public Location (String name) {
        this.locationName = name;
    }

    // Location constructor given a name, description, coordinates, and image URL
    public Location(String name, String description, TraveListLatLng traveListLatLng, String imageUrl) {
        this.locationName = name;
        this.locationDescription = description;
        this.traveListLatLng = traveListLatLng;
        this.imageUrl = imageUrl;
    }

    // Location constructor given a name, description, and coordinates
    public Location(String name, String description, TraveListLatLng traveListLatLng) {
        this.locationName = name;
        this.locationDescription = description;
        this.traveListLatLng = traveListLatLng;
    }
    public Location(String name, String description, TraveListLatLng traveListLatLng, Bitmap b) {
        this.locationName = name;
        this.locationDescription = description;
        this.traveListLatLng = traveListLatLng;
        this.pic=b;
    }

    public boolean getVisited(){
        return this.visited;
    }

    public TraveListLatLng getTraveListLatLng() {
        return this.traveListLatLng;
    }

    public String getLocationName(){
        return locationName;
    }

    public String getLocationDescription(){
        return locationDescription;
    }

    public String getImageUrl() {return imageUrl; }

    public Bitmap getPic(){
        return pic;
    }

    public void setLocationName(String name){
        this.locationName = name;
    }

    public void setLocationDescription(String description){
        this.locationDescription = description;
    }

    public void setVisited(boolean v){
        this.visited=v;
    }

}
