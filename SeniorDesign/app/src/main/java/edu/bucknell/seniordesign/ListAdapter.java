package edu.bucknell.seniordesign;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class ListAdapter extends ArrayAdapter<Location> {
    List list;
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List newList) {
        super(context, resource, newList.getLocationArray());

        list=newList;
    }



    @Override
    public int getCount(){
        return list.getLocationArray().size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;


        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listlayout, null);
        }


        Location p = list.getLocationArray().get(position);


        if (p != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.imageView3);
            TextView name = (TextView) v.findViewById(R.id.textViewName);
            TextView description = (TextView) v.findViewById(R.id.textViewDescription);


            if (icon != null) {
                icon.setImageResource(R.drawable.ic_image_black_24dp);
            }

            if (name != null) {
                name.setText(p.getLocationName());
            }

            if (description != null) {
                description.setText(p.getLocationDescription());
            }
        }

        return v;
    }
}
