package edu.bucknell.seniordesign;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * NavigationDrawerActivity.java
 * TraveList - Senior Design
 *
 * This activity controls the navigation of TraveList.
 *
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener {

    // Reference to database
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();

    // User email
    private String userEmail;

    // User, initialized to current user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Fragment
    private android.support.v4.app.Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Uncomment the following line of code to push default lists to database using ReadData.java
        // callReadData();
        
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            convertUserEmail();
        }

        updateUser();

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = fragmentClass = MapFragment.class;
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

    // Convert user email. Firebase keys cannot contain '.' so emails have ',' instead
    private void convertUserEmail() {
        userEmail = user.getEmail().replace(".", ",");
    }

    // Pushes a default list (in form of Excel sheet) to Firebase. Uses ReadData.java class.
    private void callReadData() {
        ReadData readData = new ReadData();
        try {
            readData.readXLSFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updates user to the current user
    private void updateUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (null != user) {
            convertUserEmail();
        } else {
            userEmail = null;
        }
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
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        updateUser();
        setUserName();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        this.fragment = null;
        Class fragmentClass = null;
        final ArrayList<List> listOfLists = new ArrayList<List>();
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        updateUser();
        boolean defaultList = false;

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
                fragmentClass = LoginFragment.class;
                break;
            case R.id.your_lists:
                defaultList = true;
                if (null == user) {
                    forceLogin();
                    break;
                } else {
                    fragmentClass = CustomListFragment.class;
                    updateUser();
                    final DatabaseReference finalDatabaseReference = this.mDb;
                    mDb.child("Users").child(userEmail).child("lists").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listOfLists.clear();
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                List listToAdd = s.getValue(List.class);
                                listOfLists.add(listToAdd);
                            }
                            finalDatabaseReference.child("Users").child(userEmail).child("lists").removeEventListener(this);
                            fragment = CustomListFragment.newInstance(listOfLists, true);
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                defaultList = true;
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

    // Makes Toast text to inform user they must login to use a certain feature
    private void forceLogin() {
        Toast.makeText(getApplicationContext(), "You must log in to use this feature", Toast.LENGTH_SHORT).show();
    }

    // Sets user name and user email in Navigation Drawer profile section.
    private void setUserName() {
            TextView userTextView = (TextView) findViewById(R.id.user_name);
            TextView userEmailTextView = (TextView) findViewById(R.id.user_email);
        if (null != user) {
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
