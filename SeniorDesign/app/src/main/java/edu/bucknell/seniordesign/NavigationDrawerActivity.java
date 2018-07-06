package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.maps.model.TraveListLatLng;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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

import bolts.AppLinks;

/**
 * NavigationDrawerActivity.java
 * TraveList - Senior Design
 *
 * This activity controls the navigation of TraveList.
 *
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements CreateNewListFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ListFragment.OnFragmentInteractionListener, SearchLocationsFragment.OnFragmentInteractionListener, TestFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener, Startup.OnFragmentInteractionListener {
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
    private android.support.v4.app.FragmentManager fragmentManager;
    ShowcaseView b;
    MapFragment m= MapFragment.newInstance();

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
            Toast.makeText(getApplicationContext(), "No network available. Please reconnect and restart VisiTour.",
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
            fragmentManager=getSupportFragmentManager();


            fragmentManager.beginTransaction().add(R.id.content_frag,m).commit();

            SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstRun = wmbPreference.getBoolean("F", true);


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
        if(user!=null){

            navigationView.getMenu().findItem(R.id.login_button).setTitle("Log Out");}
        else{

            navigationView.getMenu().findItem(R.id.login_button).setTitle("Log In");
        }
            if (isFirstRun)
            {
                Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar);
                ImageButton ib=new ImageButton(getApplicationContext());
                for (int i = 0; i < toolbar1.getChildCount(); i++)
                    if(toolbar1.getChildAt(i) instanceof ImageButton)
                        ib= (ImageButton) toolbar1.getChildAt(i);

                b= new ShowcaseView.Builder(this)
                        .setTarget(new ViewTarget(ib))
                        .setContentTitle("Welcome to VisiTour!")
                        .setContentText("Thank you for downloading VisiTour! Press the menu icon indicated at the top left of your screen and log in to get started. You can skip or restart the tutorial at any time by pressing the 'Tutorial' button in the menu.").setStyle(R.style.CustomShowcaseTheme3).blockAllTouches()
                        .build();
                b.setButtonText("Next");
                b.setClickable(true);




            }else{


                Toast.makeText(getApplicationContext(), "Open the menu to sign in and access app features.", Toast.LENGTH_LONG).show();}
        }


    }

    // Convert user email. Firebase keys cannot contain '.' so emails have ',' instead
    private void convertUserEmail() {
        if(user.getEmail()==null){
            userEmail= user.getUid();
        }else{
        userEmail = user.getEmail().replace(".", ",");}
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
            NavigationView n= (NavigationView) findViewById(R.id.nav_view);
            n.getMenu().findItem(R.id.login_button).setTitle("Log In");


        } else {
            userEmail = null;
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0 && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("F",true)){

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("F",true)&&item.getItemId()!=R.id.login_button &&item.getItemId()!=R.id.tutorial&&item.getItemId()!=R.id.your_lists){
            Toast.makeText(getApplicationContext(), "Please complete the tutorial first.", Toast.LENGTH_LONG).show();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if(user==null&&item.getItemId()!=R.id.login_button &&item.getItemId()!=R.id.tutorial){
            forceLogin();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);

            builder = new AlertDialog.Builder(this);
            builder.setMessage("Loading..").setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();



        selectDrawerItem(item);


        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        this.fragment = null;
        Class fragmentClass = null;
        final ArrayList<List> listOfLists = new ArrayList<List>();
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        updateUser();
        DrawerLayout d= (DrawerLayout)findViewById(R.id.drawer_layout);
        d.setSelected(true);

        boolean defaultList = false;


        switch(menuItem.getItemId()) {
            case R.id.create_list:
                if (null == user) {


                    break;
                } else {
                    fragmentManager = getSupportFragmentManager();
                    final MapFragment mf= MapFragment.newInstance();
                    final CreateNewListFragment cf= CreateNewListFragment.newInstance();

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction().add(R.id.content_frag, mf).commit();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, cf).addToBackStack(null).commit();

                        }
                    }, 310 );

                break;
               }
            case R.id.search_locations:
                if (null == user) {


                    break;
                } else {

                    fragmentManager = getSupportFragmentManager();
                    final MapFragment mf= MapFragment.newInstance();
                   final SearchLocationsFragment sf= SearchLocationsFragment.newInstance();

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction().add(R.id.content_frag, mf).commit();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, sf).addToBackStack(null).commit();

                        }
                    }, 310 );
                    break;
                }
            case R.id.login_button:
                fragmentClass = LoginFragment.class;

                 fragmentManager = getSupportFragmentManager();
               final MapFragment mf= MapFragment.newInstance();
                final LoginFragment lf= LoginFragment.newInstance();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction().replace(R.id.content_frag, mf).commit();
                        fragmentManager.beginTransaction().replace(R.id.content_frag, lf).addToBackStack(null).commit();

                    }
                }, 300 );

                break;
            case R.id.your_lists:
                defaultList = true;
                if (null == user) {


                    break;
                } else {

                    fragmentClass = CustomListFragment.class;
                    updateUser();
                    final DatabaseReference finalDatabaseReference = this.mDb;
                    Handler h = new Handler(Looper.getMainLooper());

                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            isNetworkAvailable();
                        }
                    }, 25000 );
                    mDb.child("Users").child(userEmail).child("lists").addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
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
                            isNetworkAvailable();
                            Handler h2= new Handler(Looper.getMainLooper());
                            h2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frag, MapFragment.newInstance()).commit();
                                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("F", true)){
                                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frag, fragment).commit();
                                    }else{
                                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();}
                                    alertDialog.dismiss();
                                }
                            }, 300 );


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
            case R.id.tutorial:
                alertDialog.dismiss();
                Toolbar toolbar1=(Toolbar)findViewById(R.id.toolbar);
                ImageButton ib=new ImageButton(getApplicationContext());
                for (int i = 0; i < toolbar1.getChildCount(); i++)
                    if(toolbar1.getChildAt(i) instanceof ImageButton)
                        ib= (ImageButton) toolbar1.getChildAt(i);

                b= new ShowcaseView.Builder(this)
                        .setTarget(new ViewTarget(ib))
                        .setContentTitle("Welcome to VisiTour!")
                        .setContentText("Thank you for downloading VisiTour! Press the menu icon indicated at the top left of your screen and log in to get started. You can skip or restart the tutorial at any time by pressing the 'Tutorial' button in the menu.").setStyle(R.style.CustomShowcaseTheme3).blockAllTouches().build();
                b.hide();
                b.setButtonText("Next");
                b.setClickable(true);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == DialogInterface.BUTTON_POSITIVE) {

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putBoolean("F", true);
                            editor.commit();

                            Toast.makeText(getApplicationContext(), "Tutorial reset.",
                                    Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            updateUser();
                            Handler h = new Handler(Looper.getMainLooper());

                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    fragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    fragmentManager.beginTransaction().replace(R.id.content_frag, MapFragment.newInstance()).commit();
                                    b.show();
                                }
                            }, 300 );







                        }else if(which==DialogInterface.BUTTON_NEGATIVE){
                            if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("F",true)){
                                alertDialog.dismiss();
                            }else {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putBoolean("F", false);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "Tutorial skipped. Please log in again.",
                                        Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                updateUser();
                                Handler h = new Handler(Looper.getMainLooper());

                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.dismiss();
                                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        fragmentManager.beginTransaction().replace(R.id.content_frag, MapFragment.newInstance()).commit();
                                        b.hide();
                                    }
                                }, 300);

                            }

                        }
                    }
                };
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Would you like to replay or skip the tutorial?").setPositiveButton("Replay", dialogClickListener).setNegativeButton("Skip", dialogClickListener).show();

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
            NavigationView n= (NavigationView) findViewById(R.id.nav_view);
            n.getMenu().findItem(R.id.login_button).setTitle("Log Out");

            if(!userTextView.getText().equals(user.getDisplayName())){
            userTextView.setText(user.getDisplayName());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userTextView
                        .getLayoutParams();
                mlp.setMargins(10,0,0,0);
                userTextView.setLayoutParams(mlp);
                if(user.getEmail()!=null){
            userEmailTextView.setText(userEmail.replace(",", "."));}else{
                    userEmailTextView.setText("");
                }

            profpic.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
           }
        } else {
            NavigationView n= (NavigationView) findViewById(R.id.nav_view);
            n.getMenu().findItem(R.id.login_button).setTitle("Log In");
            if(userTextView!=null) {
                userTextView.setText("Not Logged In");
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userTextView
                        .getLayoutParams();
                mlp.setMargins(10, 90, 0, 0);
                userTextView.setLayoutParams(mlp);
                profpic.setProfileId(null);
                userEmailTextView.setText(null);
            }
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
                        "No network available. Please reconnect and restart VisiTour.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, NavigationDrawerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

            return false;
        } else
            return activeNetworkInfo.isConnectedOrConnecting();
    }
}
