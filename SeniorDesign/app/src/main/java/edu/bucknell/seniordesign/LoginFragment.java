package edu.bucknell.seniordesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.util.List;
import java.util.concurrent.Executor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import edu.bucknell.seniordesign.R;

/**
 * LoginFragment.java
 * TraveList - Senior Design
 *
 * Fragment to log in to Facebook
 *
 * Created by nrs007 on 11/1/17.
 */

public class LoginFragment extends android.support.v4.app.Fragment {

    private String fbID;
    android.support.v4.app.FragmentManager fm;
    private String url;
    private boolean t=false;
    ShowcaseView b;

    // Firebase authentication
    private FirebaseAuth mAuth;

    // User
    private FirebaseUser user;

    // User email
    private String userEmail;

    // Database reference
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();

    // Callback Manager
    private CallbackManager mCallbackManager;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

   LayoutInflater i;



    // No arguments constructor
    public LoginFragment() {}

    public static LoginFragment newInstance(){
        LoginFragment a =new LoginFragment();


        return a;
    }

    private FacebookCallback<LoginResult>  mCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
            n.isNetworkAvailable();
            updateUser();
            AccessToken accessToken;
            if (null == user) {
                accessToken = loginResult.getAccessToken();
                if(accessToken.getUserId()!=null) {
                    fbID=accessToken.getUserId();
                    builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Logging in..").setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.show();
                    handleToken(accessToken);
                }

                //resetUserDisplay();
            } else {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();



            }
        }

        @Override
        public void onCancel() {
            if(alertDialog!=null){alertDialog.dismiss();}
        }

        @Override
        public void onError(FacebookException error) {
            if(alertDialog!=null){alertDialog.dismiss();}
            NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
            n.isNetworkAvailable();
        }
    };

    private AccessTokenTracker accessTokenTracker ;

    // Resets profile display after a user logs out.
    public void resetUserDisplay() {
        TextView userName = (TextView) getActivity().findViewById(R.id.user_name);
        userName.setText("Not Logged In");
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userName
                .getLayoutParams();
        mlp.setMargins(10,90,0,0);
        userName.setLayoutParams(mlp);
        TextView userEmail = (TextView) getActivity().findViewById(R.id.user_email);
        userEmail.setText("");
        ProfilePictureView profpic= (ProfilePictureView) getActivity().findViewById(R.id.profile_pic);
        profpic.setProfileId(null);
    }

    // Updates user and user email
    private void updateUser() {
        user = mAuth.getCurrentUser();
        if (user != null) {
            if(user.getEmail()==null){
                userEmail=user.getUid();
            }else {
                userEmail = user.getEmail().replace(".", ",");
            }
        } else {
            userEmail = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Facebook Log In");
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        ((NavigationDrawerActivity) getActivity()).alertDialog.dismiss();
        fm=getActivity().getSupportFragmentManager();

    }

    private void handleToken(final AccessToken accessToken) {

        AuthCredential cred = FacebookAuthProvider.getCredential(accessToken.getToken());

        mAuth.signInWithCredential(cred).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    fbID=accessToken.getUserId();
                    updateUser();
                    updateUserDisplay();
                    alertDialog.dismiss();

                    builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Loading..").setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.show();
                    mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (user != null) {
                                if (!(dataSnapshot.child("Users").hasChild(userEmail))) {










                                    try {
                                        t=true;
                                        createNewUser(dataSnapshot);

                                    } catch (Exception E) {
                                    }
                                } else {




                                }
                                if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("F", true)){

                                    Toolbar toolbar1=(Toolbar)getActivity().findViewById(R.id.toolbar);
                                    ImageButton ib=new ImageButton(getActivity().getApplicationContext());
                                    for (int i = 0; i < toolbar1.getChildCount(); i++)
                                        if(toolbar1.getChildAt(i) instanceof ImageButton)
                                            ib= (ImageButton) toolbar1.getChildAt(i);
                                    b= new ShowcaseView.Builder(getActivity())
                                            .setTarget(new ViewTarget(ib))
                                            .setContentTitle("Awesome, you've successfully logged in!").blockAllTouches()
                                            .setContentText("Now, head back over to the menu and press the button labelled 'Your Lists' to see some examples of travel lists and how they can help you organize and track your travels!").setStyle(R.style.CustomShowcaseTheme3)
                                            .build();
                                    b.setButtonText("Next");
                                    b.setClickable(true);
                                   }
                            } alertDialog.dismiss();
                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction().add(R.id.content_frag, MapFragment.newInstance()).commit();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, LoginFragment.newInstance()).addToBackStack(null).commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            alertDialog.dismiss();
                            mAuth.signOut();
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                        }
                    });


                    accessTokenTracker.stopTracking();
                }
                else {
                    if(alertDialog!=null){alertDialog.dismiss();}
                }
            }
        });
    }

    // Creates a new user in Firebase and creates a copy of default lists for the user
    private void createNewUser(DataSnapshot ds) throws IllegalAccessException, java.lang.InstantiationException {
        mDb.child("Users").child(this.userEmail).child("displayName").setValue(this.user.getDisplayName());
        mDb.child("Users").child(this.userEmail).child("userID").setValue(this.user.getUid());
        mDb.child("Users").child(this.userEmail).child("lists").setValue(ds.child("DefaultLists").getValue());


    }

    // Updates the user display to display user name and email
    private void updateUserDisplay() {
        TextView userName = (TextView) getActivity().findViewById(R.id.user_name);
        userName.setText(user.getDisplayName());
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) userName
                .getLayoutParams();
        mlp.setMargins(10,0,0,0);
        userName.setLayoutParams(mlp);
        TextView userEmail = (TextView) getActivity().findViewById(R.id.user_email);
        if(user.getEmail()!=null){
        userEmail.setText(this.userEmail.replace(",", "."));}
        else{
            userEmail.setText("");
        }
        ProfilePictureView profpic= (ProfilePictureView) getActivity().findViewById(R.id.profile_pic);

       //download h=new download(getActivity(),fbID);
        ProfilePictureView p= (ProfilePictureView) getActivity().findViewById(R.id.fbProfilePicture);
        Log.i("Rasputin", AccessToken.getCurrentAccessToken().getUserId());
        fbID=AccessToken.getCurrentAccessToken().getUserId();
       // url="https://graph.facebook.com/"+ AccessToken.getCurrentAccessToken().getUserId()+ "/picture?type=large&access_token="+ AccessToken.getCurrentAccessToken().getToken();
       // Log.i("Rasputin", url);

        p.setPresetSize(ProfilePictureView.LARGE);
        p.setProfileId(fbID);


        profpic.setProfileId(fbID);





    }

    ViewGroup con;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
        n.isNetworkAvailable();
        i=inflater;
        con=container;
        View v= inflater.inflate(R.layout.fragment_login, container, false);
        LoginButton loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setReadPermissions( "email", "public_profile");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
        ProfilePictureView p= (ProfilePictureView) v.findViewById(R.id.fbProfilePicture);



        p.setPresetSize(ProfilePictureView.LARGE);


        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
//                resetUserDisplay();
                    Toast.makeText(getContext(), "You have been signed out.", Toast.LENGTH_SHORT).show();




                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().add(R.id.content_frag, MapFragment.newInstance()).commit();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, LoginFragment.newInstance()).addToBackStack(null).commit();
                    this.stopTracking();

                    //how can we update the view when you log out?
                } else {



                    handleToken(currentAccessToken);


                    //resetUserDisplay();
                }
            }
        };
        if(mAuth.getCurrentUser()!=null) {
            p.setPresetSize(ProfilePictureView.LARGE);
            p.setProfileId(AccessToken.getCurrentAccessToken().getUserId());
            TextView t= (TextView) v.findViewById(R.id.dname);
            t.setText("Welcome "+mAuth.getCurrentUser().getDisplayName()+"!");
            t.setTextSize(25);


        }else{
            TextView t= (TextView) v.findViewById(R.id.dname);
            t.setText("Sign in to VisiTour with Facebook");
            t.setTextSize(20);
        }


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions( "email", "public_profile");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void onPause() {
        accessTokenTracker.stopTracking();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onResume() {
        getActivity().setTitle("Facebook Log In");
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

class download extends AsyncTask{
    private NavigationDrawerActivity n;
    private String fbid;
    public download(Activity a, String id){
        n= (NavigationDrawerActivity)a;
        fbid=id;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ProfilePictureView p= (ProfilePictureView) n.findViewById(R.id.fbProfilePicture);
        p.setProfileId(fbid);
        return p;

    }
}