package edu.bucknell.seniordesign;

/**
 * Created by Caroline Whitman on 9/28/2017.
 */

public class List {

    private String listName = "";
    private String listDescription = "";

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

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setListDescription(String listDescription) {
        this.listDescription = listDescription;
    }
}
