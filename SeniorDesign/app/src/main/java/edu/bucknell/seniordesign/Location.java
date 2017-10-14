package edu.bucknell.seniordesign;

import java.io.Serializable;

/**
 * Created by Jack on 9/29/2017.
 */

public class Location implements Serializable{

    private String LocationName;
    private String LocationDescription;
    private int GPS;

    public Location(String Name, String Description){
        this.LocationName= Name;
        this.LocationDescription= Description;

    }

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
