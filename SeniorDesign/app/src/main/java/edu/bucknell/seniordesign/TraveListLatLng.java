package edu.bucknell.seniordesign;

/**
 * Created by nrs007 on 11/10/17.
 */

public class TraveListLatLng {

    private Double latitude;
    private Double longitude;

    public TraveListLatLng() {} //constructor with no arguments

    public TraveListLatLng(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
