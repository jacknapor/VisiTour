package edu.bucknell.seniordesign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateNewListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateNewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewListFragment extends android.support.v4.app.Fragment {

    private String TAG = "CreateNewListFragment";

    private Button mNewListButton;

    public static DatabaseReference mDatabase;
    private FirebaseUser user;
    private String userEmail;

    private EditText mNameField;

    private OnFragmentInteractionListener mListener;

    public CreateNewListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateNewListFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment

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

                String name = mNameField.getText().toString().trim();
                //mDatabase.child("User Lists").child(name).setValue(name);
                hideKeyboard();

                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                Bundle bundle = new Bundle();
                List newList = new List(name, "");
                mDatabase.child("Users").child(userEmail).child("customLists").child(name).setValue(newList);
                bundle.putSerializable("new_list", newList);
                CustomListFragment fragment= CustomListFragment.newInstance(newList);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
            }
        });

        return view;

    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
