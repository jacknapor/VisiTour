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

import com.google.android.gms.maps.model.LatLng;

import static android.R.id.list;
import static android.R.layout.simple_list_item_1;

/**
 * Created by Jack on 10/13/2017.
 */

public class DefaultListLoader extends AppCompatActivity {
    String dlist[]= {"National Parks", "Lewisburg Museums", "Lewisburg Restaurants" };

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
                    Location yosemite= new Location("Yosemite National Park", "Yosemite National Park", new LatLng(37.8651, -119.5383));
                    Location yellowstone= new Location("Yellowstone National Park", "Yellowstone National Park", new LatLng(44.4280, -110.5885));
                    Location grandcanyon= new Location("Grand Canyon National Park", "Grand Canyon National Park", new LatLng(36.0544, -112.1401));
                    Location acadia = new Location("Acadia National Park", "Acadia National Park", new LatLng(44.3386, -68.2733));
                    Location parks[]={yosemite,yellowstone,grandcanyon,acadia};
                    n.setLocationArray(parks);
                    Intent i = new Intent(DefaultListLoader.this,ListLoader.class);
                    i.putExtra("list",n);
                    startActivity(i);

                }
                if (itemPosition==1){
                    List n= new List("Lewisburg Museums", "List of Lewisburg Museums");
                    Location silfer= new Location("Slifer House Museum", "Slifer House Museum", new LatLng(40.975443, -76.882733));
                    Location children= new Location("Lewisburg Children's Museum", "Lewisburg Children's Museum", new LatLng(40.960241, -76.891185));
                    Location packwood = new Location("Packwood House Museum", "Packwood House Museum", new LatLng(40.966640, -76.881917));

                    Location museums[]={silfer,children,packwood};
                    n.setLocationArray(museums);
                    Intent i = new Intent(DefaultListLoader.this,ListLoader.class);
                    i.putExtra("list",n);
                    startActivity(i);

                }
                if (itemPosition==2){
                    List n= new List("Lewisburg Restaurants", "List of Lewisburg Restaurants");
                    Location siam= new Location("Siam Restaurant & Bar", "Siam Restaurant & Bar", new LatLng(40.962939, -76.887704));
                    Location elizabeths= new Location("Elizabeth's", "Elizabeth's", new LatLng(40.963738, -76.886577));
                    Location mercado = new Location("Mercado Burrito", "Mercado Burrito", new LatLng(40.962949, -76.887841));

                    Location restaurants[]={siam,elizabeths,mercado};
                    n.setLocationArray(restaurants);
                    Intent i = new Intent(DefaultListLoader.this,ListLoader.class);
                    i.putExtra("list",n);
                    startActivity(i);

                }

            }
        });


    }


}
