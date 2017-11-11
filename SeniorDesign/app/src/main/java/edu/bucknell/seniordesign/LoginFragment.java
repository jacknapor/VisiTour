package edu.bucknell.seniordesign;

import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Arrays;

import java.util.List;
import java.util.concurrent.Executor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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


/**
 * Created by nrs007 on 11/1/17.
 */

public class LoginFragment extends android.support.v4.app.Fragment {

    private String TAG = "LoginFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userEmail;
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            handleToken(accessToken);
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    private void handleToken(AccessToken accessToken) {
        Log.d(TAG, "handleToken:" + accessToken);
        AuthCredential cred = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(cred).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    userEmail = user.getEmail().replace(".", ","); //replaces "." with "," because Firebase doesn't allow "." in key
                    mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (user != null) {
                                Log.d(TAG, "lookforme email is " + userEmail);
                                if (!(dataSnapshot.child("Users").hasChild(userEmail))) {
                                    try {
                                        createNewUser(dataSnapshot);
                                    } catch (Exception E) {
                                        Log.d(TAG, "Error: Adding user ID to database failed");
                                    }
                                } else {
                                    Log.d(TAG, "User ID is already in Database");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "lookforme OH NO ADDLISTENER THING CANCELLED");
                        }
                    });
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
            }
        });
    }

    private void createNewUser(DataSnapshot ds) {
        mDb.child("Users").child(this.userEmail).child("displayName").setValue(this.user.getDisplayName());
        mDb.child("Users").child(this.userEmail).child("userID").setValue(this.user.getUid());
        //mDb.child("Users").child(this.userEmail).child("lists").setValue(mDb.child("Default Lists"));
        //mDb.child("Users").child(this.userEmail).child("lists").setValue(ds.child("Default Lists"));
        mDb.child("Users").child(this.userEmail).child("lists").setValue(ds.child("Default Lists").getValue());
        /*mDb.child("Default Lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDb.child("Users");
                mDb.child(userEmail);
                mDb.child("lists");
                mDb.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference databaseReference) {
                        if (error != null) {
                            Log.d(TAG, "copy failed");
                        } else {
                            Log.d(TAG, "success");
                        }
                    }
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "copy cancelled");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
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
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //maybe somehow make the login fragment not appear first if user is signed in?
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
