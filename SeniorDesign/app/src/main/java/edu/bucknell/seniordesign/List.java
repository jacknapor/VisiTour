package edu.bucknell.seniordesign;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Caroline Whitman on 9/28/2017.
 */

public class List implements Serializable {

    private String listName = "";
    private String listDescription = "";
    private ArrayList<Location> locationArray;

    public List(String listName, String listDescription) {
        this.listName = listName;
        this.listDescription = listDescription;
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
    public void setLocationArray(ArrayList<Location> locations){
        this.locationArray= locations;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setListDescription(String listDescription) {
        this.listDescription = listDescription;
    }

    public List (){} //no argument constructor, necessary for datasnapshot.getvalue(list.class)

}
