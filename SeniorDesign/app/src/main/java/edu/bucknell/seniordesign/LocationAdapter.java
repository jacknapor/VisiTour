package edu.bucknell.seniordesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * LocationAdapter.java
 * TraveList - Senior Design
 *
 * Location adapter
 * Created by Jack on 10/23/2017.
 */

public class LocationAdapter extends ArrayAdapter<Location> {

    // Arraylist of locations
    ArrayList<Location> locations;

    // Constructor given a Context and an int
    public LocationAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    // Constructor given a Context, int, and ArrayList of locations
    public LocationAdapter(Context context, int resource, ArrayList<Location> locations) {
        super(context, resource, locations);
        this.locations = locations;
    }

    @Override
    public int getCount(){
        return locations.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.listlayout, null);
        }

        Location location = locations.get(position);

        if (location != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.listItemImage);
            TextView name = (TextView) view.findViewById(R.id.textViewName);
            TextView description = (TextView) view.findViewById(R.id.textViewDescription);

            if (icon != null) {
                icon.setImageResource(R.drawable.ic_menu_gallery);
            }

            if (name != null) {
                name.setText(location.getLocationName());
            }

            if (description != null) {
                description.setText(location.getLocationDescription());
            }
        }
        return view;
    }
}
