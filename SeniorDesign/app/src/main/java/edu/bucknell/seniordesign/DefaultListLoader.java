package edu.bucknell.seniordesign;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.*;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



import static android.R.id.list;
import static android.R.layout.simple_list_item_1;

/**
 * Created by Jack on 10/13/2017.
 */

public class DefaultListLoader extends AppCompatActivity { //extend appcompatactivity to have a title bar at the top
    String dlist[]= {"National Parks", "Lewisburg Museums", "Lewisburg Restaurants" };
    DatabaseReference mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list);
        ListView v= (ListView) findViewById(R.id.list);

        //create adapter that will feed array into the listview layout
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                simple_list_item_1, dlist);


        mDb = FirebaseDatabase.getInstance().getReference();
        v.setAdapter(adapter);

        //create listener on the list for each item
        v.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition     = position; //position of item in array


                if (itemPosition==0) {


                    mDb.child("DefaultLists").child("DefaultList0").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List n = dataSnapshot.getValue(List.class);
                            Intent i = new Intent(DefaultListLoader.this, ListLoader.class); // intent to start new activity
                            i.putExtra("list", n); //pass the list instance we just made to the new activity
                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                if (itemPosition==1){




                    mDb.child("DefaultLists").child("DefaultList1").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List n = dataSnapshot.getValue(List.class);
                            Intent i = new Intent(DefaultListLoader.this, ListLoader.class); // intent to start new activity
                            i.putExtra("list", n); //pass the list instance we just made to the new activity
                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if (itemPosition==2){




                    mDb.child("DefaultLists").child("DefaultList2").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List n = dataSnapshot.getValue(List.class);
                            Intent i = new Intent(DefaultListLoader.this, ListLoader.class); // intent to start new activity
                            i.putExtra("list", n); //pass the list instance we just made to the new activity
                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });


    }


}
