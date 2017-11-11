package edu.bucknell.seniordesign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchLocationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchLocationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchLocationsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private OnFragmentInteractionListener mListener;

    private String TAG = "SearchLocationsFragment";

    private DatabaseReference mDb;
    private FirebaseUser user;
    private String userEmail;

    private List list = null;

    public SearchLocationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SearchLocationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchLocationsFragment newInstance() {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Add Location");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mDb = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail().replace(".", ","); //firebase keys can't contain "." so emails have "," instead

        View view = inflater.inflate(R.layout.place_autocomplete_fragment, container, false);

        // Gets the List to add the locations to
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.list = (List) bundle.getSerializable("current_list");
            Log.i(TAG, "List to add to: " + this.list.getListName());
        }


        SupportPlaceAutocompleteFragment autocompleteFragment = new SupportPlaceAutocompleteFragment();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName().toString());

                TraveListLatLng newTraveListLatLng = new TraveListLatLng();
                newTraveListLatLng.setLatitude(place.getLatLng().latitude);
                newTraveListLatLng.setLongitude(place.getLatLng().longitude);

                Location newLocation = new Location(place.getName().toString(), "", newTraveListLatLng);
                if (newLocation == null) {
                    Log.i(TAG, "LOCATION IS NULL");
                }
                if (list == null) {
                    Log.i(TAG, "LIST IS NULL");
                }
                list.addLocation(newLocation);
                if (list!=null) {
                    Log.i(TAG, "New size of list: " + list.getListSize());
                }

                mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).child(newLocation.getLocationName()).setValue(newLocation);

                displayList(list);

            }

            @Override
            public void onError(Status status) {

            }
        });

        // display Places autocompleteFragment widget
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frag, autocompleteFragment);
        fragmentTransaction.commit();

        return view;
    }

    /**
     * Switches fragments to display the list of locations.
     *
     * @param list
     */
    public void displayList(List list) {
        Fragment fragment = CustomListFragment.newInstance(list);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frag, fragment);
        fragmentTransaction.commit();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
