package edu.bucknell.seniordesign;


import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import android.widget.ListView;


import com.google.android.gms.maps.MapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.Test;



public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, edu.bucknell.seniordesign.MapFragment.OnFragmentInteractionListener {

    private String TAG = "NAV_DRAWER";


    private android.app.Fragment fragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {

            Fragment fragment = null;
            Class fragmentClass = null;
            //fragmentClass = SearchLocationsFragment.class;
            fragmentClass = ListFragment.class;
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

        /**
         *
         * Commented out until fragment transactions are figured out for navigation

        int id = item.getItemId();

        if (id == R.id.all_lists) {
            // Handle the camera action
        } else if (id == R.id.lists_in_progress) {

        } else if (id == R.id.completed_lists) {

        } else if (id == R.id.create_list) {

            Intent intent = new Intent(this, NewListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nearby_sites) {

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.top_lists) {

        } else if (id == R.id.default_lists){

            View v= findViewById(R.id.t);
            Intent i = new Intent(NavigationDrawerActivity.this,DefaultListLoader.class);
            startActivity(i);
        }
        **/


        return true;
    }




    public void selectDrawerItem(MenuItem menuItem) {
        this.fragment = null;
        Class fragmentClass = MapFragment.class;
       final ArrayList<List> dlist= new ArrayList<List>();

        DatabaseReference mDb= FirebaseDatabase.getInstance().getReference();

        boolean defaultlist=false;

        switch(menuItem.getItemId()) {
            case R.id.create_list:
                fragmentClass = CreateNewListFragment.class;
                break;
            case R.id.nearby_sites:
                fragmentClass = edu.bucknell.seniordesign.MapFragment.class;
                break;
            case R.id.test_fragment:
                fragmentClass = TestFragment.class;
                break;

            case R.id.search_locations:
                fragmentClass = SearchLocationsFragment.class;

            case R.id.default_lists:

                fragmentClass= CustomListFragment.class;
               mDb.child("DefaultLists").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       for(DataSnapshot s:dataSnapshot.getChildren()){

                           if(s.getKey().equals("DefaultList0")) {
                               List n = s.getValue(List.class);
                               dlist.add(n);


                           }
                           if(s.getKey().equals("DefaultList1")) {
                               List n = s.getValue(List.class);
                               dlist.add(n);
                           }
                           if(s.getKey().equals("DefaultList2")) {
                               List n = s.getValue(List.class);
                               dlist.add(n);
                               fragment = CustomListFragment.newInstance(dlist, true);
                               FragmentManager fragmentManager = getFragmentManager();
                               fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                           }
                       }

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });

                defaultlist=true;

                break;
            default:
                fragmentClass = SearchLocationsFragment.class;
                break;
        }

        try {

            if(defaultlist){
                //fragment = CustomListFragment.newInstance(dlist, true);
            }else{
            fragment = (android.app.Fragment) fragmentClass.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).commit();}

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
