package edu.bucknell.seniordesign;

import java.io.Serializable;

/**
 * TraveListLatLng.java
 * TraveList - Senior Design
 *
 * Created by nrs007 on 11/10/17.
 */

// A class to represent a TraveList LatLng object
public class TraveListLatLng implements Serializable{

    // Latitude
    private Double latitude;

    // Longitude
    private Double longitude;

    // No argument constructor
    public TraveListLatLng() {}

    // Constructor given a latitude and longitude
    public TraveListLatLng(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
