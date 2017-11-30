package edu.bucknell.seniordesign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class ListAdapter extends ArrayAdapter<Location> {
    List list;
    Boolean isVisited;
    Boolean made=false;
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userEmail = user.getEmail().replace(".", ",");
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
            CheckBox checkbox= (CheckBox) v.findViewById(R.id.visited);
            checkbox.setVisibility(View.VISIBLE);

            TextView a= (TextView) v.findViewById(R.id.textView3);
            a.setVisibility(View.VISIBLE);
            final CheckBox c= checkbox;
            Log.e("a", Integer.toString(position));
            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).child("locationArray").child(Integer.toString(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {

                        if (s.getKey().equals("visited")) {
                            isVisited= s.getValue(Boolean.class);

                        }

                }c.setChecked(isVisited);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            final Location pp=p;
            final List l=list;
            final int pos= position;
            c.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    if(c.isChecked()){
                        pp.setVisited(true);

                         mDb.child("Users").child(userEmail).child("lists").child(l.getListName()).child("locationArray").child(Integer.toString(pos)).child("visited").setValue(true);
                        //mDb.child("Users").child("jacknapor@yahoo,com").child("lists").child(l.getListName()).child("locationArray").child(Integer.toString(pos)).child("visited").setValue(true);
                        made=true;

                    }else{
                        pp.setVisited(false);

                        mDb.child("Users").child(userEmail).child("lists").child(l.getListName()).child("locationArray").child(Integer.toString(pos)).child("visited").setValue(false);
                        made=true;

                        //mDb.child("Users").child("jacknapor@yahoo,com").child("lists").child(l.getListName()).child("locationArray").child(Integer.toString(pos)).child("visited").setValue(false);
                    }


                }
            });
            //<<<<<<<HEAD
            /*Log.e("wtf", "wtf");

            if (icon != null) {
                icon.setImageResource(R.drawable.ic_image_black_24dp);
=======*/
            Log.d("lookformeyayyyy", "imageUrl is: " + p.getImageUrl());

            if (p.getImageUrl() != null && icon != null) {
                Log.d("lookformeyay", "in the right if sick bro");
                Glide.with(getContext()).load(p.getImageUrl()).into(icon);
            }


            else if (icon != null) {
                //Log.d("lookformeyay", "location name is " + p.getLocationName());
                icon.setImageResource(R.drawable.ic_menu_gallery);

//>>>>>>> images
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
