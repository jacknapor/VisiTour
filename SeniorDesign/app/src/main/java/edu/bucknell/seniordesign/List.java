package edu.bucknell.seniordesign;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * List.java
 * TraveList - Senior Design
 *
 * A class to represent a TraveList list.
 *
 * Created by Caroline Whitman on 9/28/2017.
 */
public class List implements Serializable {

    // Name of List
    private String listName = "";

    // List description
    private String listDescription = "";

    // Array of Locations in the List
    private ArrayList<Location> locationArray = new ArrayList<Location>();

    // Number of Locations in List
    private int listSize;

    // No argument constructor. Necessary for datasnapshot.getvalue(List.class)
    public List (){}

    // List constructor given a list name and a list description
    public List(String listName, String listDescription) {
        this.listName = listName;
        this.listDescription = listDescription;
    }

    // Add a location to the list
    public void addLocation(Location location) {
        this.locationArray.add(location);
        this.listSize++;
    }

    public int getCompletionStatus(){
        double progress = 0;
        for (int i = 0; i < locationArray.size(); i++){
            if (locationArray.get(i).getVisited()){
                progress++;
            }

        }
        double doubleProgressPercent = (progress/ (double) locationArray.size())*100;
        int intProgressPercent = (int) doubleProgressPercent;
        Log.e("progress", Double.toString(doubleProgressPercent));
        return intProgressPercent;
    }

    public Location getLocation(int position) {
        return locationArray.get(position);
    }

    public String getListName() {
        return listName;
    }

    public String getListDescription() {
        return listDescription;
    }

    public ArrayList<Location> getLocationArray(){
        return locationArray;
    }

    public int getListSize() {
        return listSize;
    }

    public void setLocationArray(ArrayList<Location> locations){
        this.locationArray= locations;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setListDescription(String listDescription) {
        this.listDescription = listDescription;
    }

}
