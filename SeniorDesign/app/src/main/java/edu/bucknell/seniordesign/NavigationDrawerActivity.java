package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.maps.model.TraveListLatLng;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.formula.functions.Na;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NavigationDrawerActivity.java
 * TraveList - Senior Design
 *
 * This activity controls the navigation of TraveList.
 *
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener {
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    private GeoDataClient mGeoDataClient;
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
        Log.e("hello", "a");
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);

            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
           if(user!=null){ user=null;}
            updateUser();


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getApplicationContext(), "No network available. Please reconnect and restart TraveList.",
                    Toast.LENGTH_LONG).show();
            finish();

        }
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Uncomment the following line of code to push default lists to database using ReadData.java
        // callReadData();
        
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            convertUserEmail();


        }

        updateUser();

        if (savedInstanceState == null) {

            Class fragmentClass = fragmentClass = MapFragment.class;
            updateUser();
            try {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateUser();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.content_frag, fragment).commit();
            Toast.makeText(getApplicationContext(), "Open the menu to sign in and access app features.", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                updateUser();
                setUserName();

            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            super.onBackPressed();
        } else {
          // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        updateUser();
        setUserName();
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);

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
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().add(R.id.content_frag, MapFragment.newInstance()).commit();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, CreateNewListFragment.newInstance()).addToBackStack(null).commit();
                break;
               }
            case R.id.search_locations:
                if (null == user) {
                    forceLogin();
                    break;
                } else {

                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().add(R.id.content_frag, MapFragment.newInstance()).commit();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, SearchLocationsFragment.newInstance()).addToBackStack(null).commit();
                    break;
                }
            case R.id.login_button:
                fragmentClass = LoginFragment.class;

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frag, LoginFragment.newInstance()).addToBackStack(null).commit();

                break;
            case R.id.your_lists:
                defaultList = true;
                if (null == user) {
                    forceLogin();
                    break;
                } else {
                    builder = new AlertDialog.Builder(this);
                    builder.setMessage("Loading..").setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.show();
                    fragmentClass = CustomListFragment.class;
                    updateUser();
                    final DatabaseReference finalDatabaseReference = this.mDb;
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            isNetworkAvailable();
                        }
                    }, 25000 );
                    mDb.child("Users").child(userEmail).child("lists").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            listOfLists.clear();
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                Log.e("e", s.getKey());
                                List listToAdd = s.getValue(List.class);
                                listOfLists.add(listToAdd);
                            }
                            finalDatabaseReference.child("Users").child(userEmail).child("lists").removeEventListener(this);
                            fragment = CustomListFragment.newInstance(listOfLists, true);
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction().add(R.id.content_frag, MapFragment.newInstance()).commit();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                            alertDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            alertDialog.dismiss();
                            isNetworkAvailable();

                        }

                    });

                }
                defaultList = true;
                break;
            default:

                break;
        }

        try {
            if(defaultList){
            }else{
            }

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
            ProfilePictureView profpic= (ProfilePictureView) findViewById(R.id.profile_pic);
        if (null != user) {

            if(!userTextView.getText().equals(user.getDisplayName())){
            userTextView.setText(user.getDisplayName());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userTextView
                        .getLayoutParams();
                mlp.setMargins(10,0,0,0);
                userTextView.setLayoutParams(mlp);
            userEmailTextView.setText(user.getEmail());

            profpic.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
           }
        } else {
            userTextView.setText("Not Logged In");
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userTextView
                    .getLayoutParams();
            mlp.setMargins(10,90,0,0);
            userTextView.setLayoutParams(mlp);
            profpic.setProfileId(null);
            userEmailTextView.setText(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frag);
        fragment.onActivityResult(requestCode, resultCode, data);
        updateUser();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public boolean isNetworkAvailable() {
        return  isNetworkAvailable(false);
    }

    public boolean isNetworkAvailable(boolean withToast) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo == null) {

                Toast.makeText(getApplicationContext(),
                        "No network available. Please reconnect and restart TraveList.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, NavigationDrawerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

            return false;
        } else
            return activeNetworkInfo.isConnectedOrConnecting();
    }
}
