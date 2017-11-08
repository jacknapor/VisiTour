package edu.bucknell.seniordesign;


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

public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, edu.bucknell.seniordesign.MapFragment.OnFragmentInteractionListener {

    private String TAG = "NAV_DRAWER";


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

    //Handles fragment changes when menu item is selected by user.
    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.create_list:
                fragmentClass = CreateNewListFragment.class;
                break;
            case R.id.default_lists:
                View v= findViewById(R.id.t);
                Intent i = new Intent(NavigationDrawerActivity.this,DefaultListLoader.class);
                startActivity(i);
                break;

            case R.id.nearby_sites:
                fragmentClass = edu.bucknell.seniordesign.MapFragment.class;
                break;
            case R.id.test_fragment:
                fragmentClass = TestFragment.class;
                break;
            case R.id.search_locations:
                fragmentClass = SearchLocationsFragment.class;
                break;
            default:
                fragmentClass = SearchLocationsFragment.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).commit();
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
