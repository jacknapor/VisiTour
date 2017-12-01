package edu.bucknell.seniordesign.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.bucknell.seniordesign.R;
import edu.bucknell.seniordesign.data.List;
import edu.bucknell.seniordesign.data.Location;

import static android.R.layout.simple_list_item_1;

/**
 * Created by Jack on 10/14/2017.
 */

public class ListLoader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("LIST LOADER", "PLAIN");


        setContentView(R.layout.activity_choose_list);
        ListView v = (ListView) findViewById(R.id.list);
        List n = (List) this.getIntent().getSerializableExtra("list"); //retrieve the list object passed from the defaultlist activity
        ArrayList<Location> x = n.getLocationArray();
        String names[] = new String[x.size()]; //need list of strings for arrayadapter
        for (int i = 0; i <= x.size() - 1; i++) {
            names[i] = x.get(i).getLocationName();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                simple_list_item_1, names);
        v.setAdapter(adapter);

        //want to change title bar from "Default lists" to the name of this list, need to define this instance as an activity
        final Activity activity = this;

        activity.setTitle(n.getListName()); //set the title bar to the name of this list

        //create listener for the location items of this list
        v.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;


            }
        });


    }
}
