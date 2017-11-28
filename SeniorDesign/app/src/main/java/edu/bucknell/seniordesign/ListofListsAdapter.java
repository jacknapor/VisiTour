package edu.bucknell.seniordesign;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class ListofListsAdapter extends ArrayAdapter<List> {
    private ArrayList<List> listarray = new ArrayList<List>();
    public ListofListsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListofListsAdapter(Context context, int resource, ArrayList<List> dList) {
        super(context, resource, dList);

        this.listarray =dList;
    }



    @Override
    public int getCount(){

        return this.listarray.size();

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listlayout, null);
        }


        List p = this.listarray.get(position);


        if (p != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.imageView3);
            TextView name = (TextView) v.findViewById(R.id.textViewName);
            TextView description = (TextView) v.findViewById(R.id.textViewDescription);


            String img = p.getLocationArray().get(0).getImageUrl();

            if (img != null && icon != null) {
                Log.d("lookformeyay", "in the right if sick bro");
                Glide.with(getContext()).load(img).into(icon);
            } else if (icon != null) {
                icon.setImageResource(R.drawable.ic_menu_gallery);
            }

            if (name != null) {
                name.setText(p.getListName());
            }

            if (description != null) {
                description.setText(p.getListDescription());
            }
        }

        return v;
    }
}
