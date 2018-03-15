package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * CreateNewListFragment.java
 * TraveList - Senior Design
 *
 * Fragment for creating a new list
 *
 */
public class CreateNewListFragment extends android.support.v4.app.Fragment {

    // Create list button
    private Button mNewListButton;

    // Database reference
    public static DatabaseReference mDatabase;

    // User
    private FirebaseUser user;

    // User email
    private String userEmail;

    // Editable name of list to create
    private EditText mNameField;

    private EditText mDesc;
    private AlertDialog a;
    // OnFragmentInteractionListener
    private OnFragmentInteractionListener mListener;

    // No argument constructor
    public CreateNewListFragment() {
    }

    // Create new instance of fragment
    public static CreateNewListFragment newInstance() {
        CreateNewListFragment fragment = new CreateNewListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
        n.isNetworkAvailable();
        getActivity().setTitle("Create a New List");
        View view = inflater.inflate(R.layout.fragment_create_new_list, container, false);
        TextView title= (TextView) view.findViewById(R.id.lctitle);
        title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        mNewListButton = (Button) view.findViewById(R.id.newListButton);
        mNameField = (EditText) view.findViewById(R.id.nameField);
        mDesc= (EditText) view.findViewById(R.id.descriptionField);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            if(user.getEmail()!=null) {
                userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead
            }else{
                userEmail=user.getUid();
            }
        }

        mNewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
                n.isNetworkAvailable();
                String name = mNameField.getText().toString().trim();
                String desc = mDesc.getText().toString().trim();
                if (name.contains(".")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid List Name").setMessage("Please do not add periods to your list name." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else if (name.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid List Name").setMessage("Your list name must be at least 1 character long." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

                }else if (name.length()>26){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid List Name").setMessage("Your list name must be fewer than 26 characters." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else if(desc.contains(".")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid Description").setMessage("Please do not add periods to your description." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

                }else if (desc.length()>27){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid Description").setMessage("Your description must be fewer than 28 characters." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                else{
                hideKeyboard();

                final android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


                final List newList = new List(name, desc);
                final String p= name;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Creating '"+ p+"'...").setCancelable(false);
                    a = builder.create();
                    a.show();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        a.dismiss();
                        if(!(dataSnapshot.child("Users").child(userEmail).child("lists").child(p).exists())){
                            mDatabase.child("Users").child(userEmail).child("lists").child(p).setValue(newList);
                            Bundle bundle= new Bundle();
                            bundle.putSerializable("new_list", newList);

                            CustomListFragment fragment= CustomListFragment.newInstance(newList);
                            fragment.setArguments(bundle);
                            mNameField.setText("");
                            mDesc.setText("");
                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Duplicate List Name").setMessage("You already have a list named "+p+". Please use a different name." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                            fragmentManager.beginTransaction().replace(R.id.content_frag, new CreateNewListFragment()).commit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        a.dismiss();

                    }
                });

                }
            }});


        return view;
    }

    // Hide keyboard once user has created their list
    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
