package edu.bucknell.seniordesign;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//import com.google.android.gms.maps.model.TraveListLatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;


public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, edu.bucknell.seniordesign.MapFragment.OnFragmentInteractionListener {


    private String TAG = "NAV_DRAWER";

    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user;
    private String userEmail;
    //private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //private String userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead


    private android.support.v4.app.Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ReadData readData = new ReadData();
        try {
            readData.readXLSFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //addNationalParks();
        //addMuseums();
        //addRestaurants();

        if (savedInstanceState == null) {

            Fragment fragment = null;
            Class fragmentClass = null;
            //fragmentClass = SearchLocationsFragment.class;
            fragmentClass = edu.bucknell.seniordesign.MapFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
     

    private void addNationalParks() {
        List n= new List("National Parks", "List of several National Parks");
        Location yosemite= new Location("Yosemite National Park", "Yosemite National Park", new TraveListLatLng(37.8651, -119.5383));
        Location yellowstone= new Location("Yellowstone National Park", "Yellowstone National Park", new TraveListLatLng(44.4280, -110.5885));
        Location grandcanyon= new Location("Grand Canyon National Park", "Grand Canyon National Park", new TraveListLatLng(36.0544, -112.1401));
        Location acadia = new Location("Acadia National Park", "Acadia National Park", new TraveListLatLng(44.3386, -68.2733));
        ArrayList parks = new ArrayList();
        parks.add(yosemite);
        parks.add(yellowstone);
        parks.add(grandcanyon);
        parks.add(acadia);

        n.setLocationArray(parks);
        //Intent i = new Intent(DefaultListLoader.this,ListLoader.class); // intent to start new activity
        //i.putExtra("list",n); //pass the list instance we just made to the new activity
        //startActivity(i);
        mDb.child("DefaultLists").child(n.getListName()).setValue(n);
        //mDb.child("Users").child(userEmail).child("lists").child("National Parks").setValue(n);
    }

    private void addMuseums() {
        List n= new List("Lewisburg Museums", "List of Lewisburg Museums");
        Location silfer= new Location("Slifer House Museum", "Slifer House Museum", new TraveListLatLng(40.975443, -76.882733));
        Location children= new Location("Lewisburg Children's Museum", "Lewisburg Children's Museum", new TraveListLatLng(40.960241, -76.891185));
        Location packwood = new Location("Packwood House Museum", "Packwood House Museum", new TraveListLatLng(40.966640, -76.881917));

        ArrayList<Location> museums= new ArrayList();
        museums.add(silfer);
        museums.add(children);
        museums.add(packwood);
        n.setLocationArray(museums);

        mDb.child("DefaultLists").child(n.getListName()).setValue(n);
        //mDb.child("Users").child(userEmail).child("lists").child("Lewisburg Museums").setValue(n);
    }

    private void addRestaurants() {
        List n= new List("Lewisburg Restaurants", "List of Lewisburg Restaurants");
        Location siam= new Location("Siam Restaurant & Bar", "Siam Restaurant & Bar", new TraveListLatLng(40.962939,-76.88770));
        Location elizabeths= new Location("Elizabeth's", "Elizabeth's", new TraveListLatLng(40.963738, -76.886577));
        Location mercado = new Location("Mercado Burrito", "Mercado Burrito", new TraveListLatLng(40.962949, -76.887841));

        ArrayList<Location> restaurants = new ArrayList();
        restaurants.add(siam);
        restaurants.add(elizabeths);
        restaurants.add(mercado);
        n.setLocationArray(restaurants);

        mDb.child("DefaultLists").child(n.getListName()).setValue(n);
        //mDb.child("Users").child(userEmail).child("lists").child("Lewisburg Restaurants").setValue(n);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        // Handle navigation view item clicks here.

        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        this.fragment = null;
        Class fragmentClass = null;
       final ArrayList<List> dlist= new ArrayList<List>();

        DatabaseReference mDb= FirebaseDatabase.getInstance().getReference();

        boolean defaultList=false;

        switch(menuItem.getItemId()) {
            case R.id.create_list:
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (null == user) {
                    Toast.makeText(getApplicationContext(), "You must log in to use this feature", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                fragmentClass = CreateNewListFragment.class;
                break;
                }
            case R.id.nearby_sites:
                fragmentClass = edu.bucknell.seniordesign.MapFragment.class;
                break;
            case R.id.search_locations:
                fragmentClass = SearchLocationsFragment.class;
                break;
            case R.id.login_button:
                fragmentClass = LoginFragment.class;
                break;
            case R.id.default_lists:
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (null == user) {
                    Toast.makeText(getApplicationContext(), "You must log in to use this feature", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    fragmentClass= CustomListFragment.class;
                    userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead
                    Log.d(TAG, "lookforme USER IS " + userEmail);

                    mDb.child("Users").child(userEmail).child("lists").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot s : dataSnapshot.getChildren()) {

                                if (s.getKey().equals("National Parks")) {
                                    List n = s.getValue(List.class);
                                    dlist.add(n);

                                }
                                if (s.getKey().equals("Museums Near Lewisburg")) {
                                    List n = s.getValue(List.class);
                                    dlist.add(n);
                                }
                                if (s.getKey().equals("Lewisburg Museums")) {
                                    List n = s.getValue(List.class);
                                    dlist.add(n);
                                }
                                if (s.getKey().equals("All National Parks")) {
                                    List n = s.getValue(List.class);
                                    dlist.add(n);
                                }
                                if (s.getKey().equals("Lewisburg Restaurants")) {
                                    List n = s.getValue(List.class);
                                    dlist.add(n);
                                    fragment = CustomListFragment.newInstance(dlist, true);
                                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                defaultList=true;
                break;
            default:
                fragmentClass = TestFragment.class;
                break;
        }

        try {

            if(defaultList){
                fragment = CustomListFragment.newInstance(dlist, true);
            }else{
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();}

        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
