package edu.bucknell.seniordesign;


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
import android.widget.TextView;
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

    private String userEmail;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private android.support.v4.app.Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        Uncomment the following block of code to push default lists to database.
         */
        /*ReadData readData = new ReadData();
        try {
            readData.readXLSFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead

            //addNationalParks();
            //addMuseums();
            //addRestaurants();

        }

        updateUser();

        if (savedInstanceState == null) {

            Fragment fragment = null;
            Class fragmentClass = fragmentClass = edu.bucknell.seniordesign.MapFragment.class;
            updateUser();
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUser();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateUser();
    }



    private void updateUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (null != user) {
            userEmail = user.getEmail().replace(".", ",");
        } else {
            userEmail = null;
        }
    }

    private void addNationalParks() {
        List n= new List("National Parks", "List of several National Parks");
        Location yosemite= new Location("Yosemite National Park", "Yosemite National Park", new TraveListLatLng(37.8651, -119.5383));
        Location yellowstone= new Location("Yellowstone National Park", "Yellowstone National Park", new TraveListLatLng(44.4280, -110.5885));
        Location grandcanyon= new Location("Grand Canyon National Park", "Grand Canyon National Park", new TraveListLatLng(36.0544, -112.1401));
        Location acadia = new Location("Acadia National Park", "Acadia National Park", new TraveListLatLng(44.3386, -68.2733));
        ArrayList parks = new ArrayList();
        yosemite.setVisited(false);
        yellowstone.setVisited(false);
        grandcanyon.setVisited(false);
        acadia.setVisited(false);
        parks.add(yosemite);
        parks.add(yellowstone);
        parks.add(grandcanyon);
        parks.add(acadia);

        n.setLocationArray(parks);

       // mDb.child("DefaultLists").child(n.getListName()).setValue(n);
        mDb.child("Users").child(userEmail).child("lists").child("National Parks").setValue(n);
    }

    private void addMuseums() {
        List n= new List("Lewisburg Museums", "List of Lewisburg Museums");
        Location silfer= new Location("Slifer House Museum", "Slifer House Museum", new TraveListLatLng(40.975443, -76.882733));
        Location children= new Location("Lewisburg Children's Museum", "Lewisburg Children's Museum", new TraveListLatLng(40.960241, -76.891185));
        Location packwood = new Location("Packwood House Museum", "Packwood House Museum", new TraveListLatLng(40.966640, -76.881917));
        silfer.setVisited(false);
        children.setVisited(false);
        packwood.setVisited(false);
        ArrayList<Location> museums= new ArrayList();
        museums.add(silfer);
        museums.add(children);
        museums.add(packwood);
        n.setLocationArray(museums);

        //mDb.child("DefaultLists").child(n.getListName()).setValue(n);
        mDb.child("Users").child(userEmail).child("lists").child("Lewisburg Museums").setValue(n);
    }

    private void addRestaurants() {
        List n= new List("Lewisburg Restaurants", "List of Lewisburg Restaurants");
        Location siam= new Location("Siam Restaurant & Bar", "Siam Restaurant & Bar", new TraveListLatLng(40.962939,-76.88770), "https://firebasestorage.googleapis.com/v0/b/natparksdb.appspot.com/o/Default%2FLewisburg%20Restaurants%2Fsiam-cafe.jpg?alt=media&token=1b113486-8614-4290-b666-ef68a4b69bed");
        Location elizabeths= new Location("Elizabeth's", "Elizabeth's", new TraveListLatLng(40.963738, -76.886577), "https://firebasestorage.googleapis.com/v0/b/natparksdb.appspot.com/o/Default%2FLewisburg%20Restaurants%2FElizabeths.jpg?alt=media&token=40be4e77-4645-4224-93ed-de58f50dfe95");
        Location mercado = new Location("Mercado Burrito", "Mercado Burrito", new TraveListLatLng(40.962949, -76.887841), "https://firebasestorage.googleapis.com/v0/b/natparksdb.appspot.com/o/Default%2FLewisburg%20Restaurants%2Fmercado.jpg?alt=media&token=c1a68734-7dc0-463c-96be-228a0933b880");
        siam.setVisited(false);
        elizabeths.setVisited(false);
        mercado.setVisited(false);

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
        } else if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        updateUser();
        setUserName();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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
        updateUser();
        Log.d(TAG, "hihi in selectdraweritem, user is " + user);
        boolean defaultList=false;

        switch(menuItem.getItemId()) {
            case R.id.create_list:
                if (null == user) {
                    forceLogin();
                    break;
                } else {
                fragmentClass = CreateNewListFragment.class;
                break;
               }
            case R.id.search_locations:
                if (null == user) {
                    forceLogin();
                    break;
                } else {
                    fragmentClass = SearchLocationsFragment.class;
                    break;
                }
            case R.id.login_button:
                Log.d(TAG, "hihi user when you clicked the login button was " + userEmail);
                fragmentClass = LoginFragment.class;
                break;
            case R.id.your_lists:
                defaultList=true;
                if (null == user) {
                    forceLogin();
                    break;
                } else {
                    fragmentClass= CustomListFragment.class;

                    boolean t=true;
                    boolean f=false;

                    updateUser();
                    Log.e(TAG, "USER IS " + userEmail);
                    final DatabaseReference m= this.mDb;
                    mDb.child("Users").child(userEmail).child("lists").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dlist.clear();

                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                List n = s.getValue(List.class);
                                dlist.add(n);
                            }
                            m.child("Users").child(userEmail).child("lists").removeEventListener(this);
                            fragment = CustomListFragment.newInstance(dlist, true);
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
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
            }else{
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();}

        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        updateUser();
    }

    private void forceLogin() {
        Toast.makeText(getApplicationContext(), "You must log in to use this feature", Toast.LENGTH_SHORT).show();
    }

    private void setUserName() {
            TextView userTextView = (TextView) findViewById(R.id.user_name);
            TextView userEmailTextView = (TextView) findViewById(R.id.user_email);
        if (null != user) {
            Log.i(TAG, "my user: " + user.getDisplayName());
            userTextView.setText(user.getDisplayName());
            userEmailTextView.setText(user.getEmail());
        } else {
            userTextView.setText("Not Logged In");
            userEmailTextView.setText(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUser();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
