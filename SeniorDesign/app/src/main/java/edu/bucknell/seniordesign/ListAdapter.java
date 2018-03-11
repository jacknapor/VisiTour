package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;

/**
 * ListAdapter.java
 * TraveList - Senior Design
 *
 * Adapter to populate Lists.
 *
 * Created by Jack on 10/23/2017.
 */

public class ListAdapter extends ArrayAdapter<Location> {

    // List
    List list;

    // Whether or not a location has been visited.
    Boolean isVisited;

    // ???
    Boolean made = false;

    // Reference to database
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();

    // User
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // User email
    private String userEmail = user.getEmail().replace(".", ",");


    // Constructor given a Context and an int
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    // Constructor given a Context, int, and List
    public ListAdapter(Context context, int resource, List newList) {
        super(context, resource, newList.getLocationArray());
        this.list = newList;
    }

    @Override
    public int getCount(){
        return list.getLocationArray().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        View view = convertView;

       if (view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.listlayout, null);
        }


        Location location = list.getLocationArray().get(position);

        if (location != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.listItemImage);
            TextView name = (TextView) view.findViewById(R.id.textViewName);
            TextView description = (TextView) view.findViewById(R.id.textViewDescription);
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.visited);
            checkbox.setVisibility(View.VISIBLE);
            ImageView delete = (ImageView) view.findViewById(R.id.delete);
             TextView address= (TextView) view.findViewById(R.id.textViewAddress);

            TextView visitedText = (TextView) view.findViewById(R.id.visitedTextView);
            visitedText.setVisibility(View.VISIBLE);
            final CheckBox finalCheckBox = checkbox;
            final int finalPosition = position;

            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).child("locationArray").child(Integer.toString(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        if (s.getKey().equals("visited")) {
                            isVisited= s.getValue(Boolean.class);
                        }
                }
                mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).child("locationArray").child(Integer.toString(finalPosition)).removeEventListener(this);
                finalCheckBox.setChecked(isVisited);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            final Location finalLocation = location;
            final List finalList = list;

            finalCheckBox.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    NavigationDrawerActivity n= (NavigationDrawerActivity)getContext();
                    n.isNetworkAvailable();
                    if(finalCheckBox.isChecked()){
                        finalLocation.setVisited(true);
                        mDb.child("Users").child(userEmail).child("lists").child(finalList.getListName()).child("locationArray").child(Integer.toString(finalPosition)).child("visited").setValue(true);
                        made = true;
                    } else{
                        finalLocation.setVisited(false);
                        mDb.child("Users").child(userEmail).child("lists").child(finalList.getListName()).child("locationArray").child(Integer.toString(finalPosition)).child("visited").setValue(false);
                        made = true;
                    }
                }
            });
            final ListAdapter finalListAdapter = this;
            delete.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            // overlay is black with transparency of 0x77 (119)
                            view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            // clear the overlay
                            view.getDrawable().clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });
            delete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    NavigationDrawerActivity n= (NavigationDrawerActivity)getContext();
                    n.isNetworkAvailable();
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == Dialog.BUTTON_POSITIVE) {
                                list.getLocationArray().remove(finalPosition);
                                mDb.child("Users").child(userEmail).child("lists").child(finalList.getListName()).child("locationArray").setValue(list.getLocationArray());
                                finalListAdapter.notifyDataSetChanged();
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to delete this location? Once it has been deleted this action cannot be undone.").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                }
            });



            if (location.getImageUrl() != null && icon != null) {
                Glide.with(getContext()).load(location.getImageUrl()).into(icon);
            }
            else if (location.getPic() != null && icon != null) {
                icon.setImageBitmap(location.getPic());
            }

            else if (icon != null) {
                icon.setImageResource(R.drawable.ic_menu_gallery);
            }

            if (name != null) {
                name.setText(location.getLocationName());
            }
            if (description != null) {
                if(location.getAddress()!=null){

                    if( location.getLocationDescription().equals("")){
                        description.setText("");
                        address.setText(location.getAddress());
                    }else{
                        description.setTextSize(16);
                        description.setText("Category: "+ location.getLocationDescription());
                        address.setText(location.getAddress());
                    }



                }
            }


        }
        return view;
    }
}
