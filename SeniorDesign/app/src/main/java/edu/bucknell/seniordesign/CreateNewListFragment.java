package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
        n.isNetworkAvailable();
        getActivity().setTitle("Create a New List");
        View view = inflater.inflate(R.layout.fragment_create_new_list, container, false);

        mNewListButton = (Button) view.findViewById(R.id.newListButton);
        mNameField = (EditText) view.findViewById(R.id.nameField);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead

        mNewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
                n.isNetworkAvailable();
                String name = mNameField.getText().toString().trim();
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

                }else if (name.length()>15){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid List Name").setMessage("Your list name must be fewer than 15 characters." ).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else{
                hideKeyboard();

                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                Bundle bundle = new Bundle();
                List newList = new List(name, "");
                mDatabase.child("Users").child(userEmail).child("lists").child(name).setValue(newList);
                bundle.putSerializable("new_list", newList);

                CustomListFragment fragment= CustomListFragment.newInstance(newList);
                fragment.setArguments(bundle);
                mNameField.setText("");
                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();}
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
