package edu.bucknell.seniordesign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
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

    private GoogleApiClient mGoogleApiClient;

    private OnFragmentInteractionListener mListener;

    private String TAG = "SearchLocationsFragment";

    private DatabaseReference mDb;

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

        View view = inflater.inflate(R.layout.place_autocomplete_fragment, container, false);

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

                Location newLocation = new Location(place.getName().toString(), "", place.getLatLng().latitude, place.getLatLng().longitude);
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

                mDb.child(list.getListName()).child(newLocation.getLocationName()).setValue(newLocation);

                displayList(list);

            }

            @Override
            public void onError(Status status) {

            }
        });

        Log.i(TAG, "list list list" + list.getListName());

        //Class fragmentClass = CustomListFragment.class;
        Fragment fragment = (Fragment) CustomListFragment.newInstance(list);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frag, autocompleteFragment);
        fragmentTransaction.commit();

        return view;
    }

    public void displayList(List list) {
        Class fragmentClass = CustomListFragment.class;
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
