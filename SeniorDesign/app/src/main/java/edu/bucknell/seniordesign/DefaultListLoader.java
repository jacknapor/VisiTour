package edu.bucknell.seniordesign;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.*;

import static android.R.id.list;
import static android.R.layout.simple_list_item_1;

/**
 * Created by Jack on 10/13/2017.
 */

public class DefaultListLoader extends AppCompatActivity {
    String dlist[]= {"National Parks", "State Capitals", "7 Wonders of the World", "Lewisburg Restaurants" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list);
        ListView v= (ListView) findViewById(R.id.list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                simple_list_item_1, dlist);
        v.setAdapter(adapter);
        v.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition     = position;

                if (itemPosition==0){
                    List n= new List("National Parks", "List of several National Parks");
                    Location yosemite= new Location("Yosemite National Park", "Yosemite National Park");
                    Location yellowstone= new Location("Yellowstone National Park", "Yellowstone National Park");
                    Location grandcanyon= new Location("Grand Canyon National Park", "Grand Canyon National Park");
                    Location acadia = new Location("Acadia National Park", "Acadia National Park");
                    Location parks[]={yosemite,yellowstone,grandcanyon,acadia};
                    n.setLocationArray(parks);
                    Intent i = new Intent(DefaultListLoader.this,ListLoader.class);
                    i.putExtra("list",n);
                    startActivity(i);

                }



            }
        });


    }


}
