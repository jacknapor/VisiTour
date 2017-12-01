package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.SupportMapFragment;
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
public class SearchLocationsFragment extends android.support.v4.app.Fragment implements GoogleApiClient.OnConnectionFailedListener , OnBackPressedListener{

    private OnFragmentInteractionListener mListener;

    private String TAG = "SearchLocationsFragment";
    private DatabaseReference mDb;
    private FirebaseUser user;
    private String userEmail;

    private static View view;

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
    public static SearchLocationsFragment newInstance(List l) {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
        fragment.list=l;
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

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_search_locations, container, false);

        } catch (InflateException e) {
            e.printStackTrace();
        }


        // Gets the List to add the locations to
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i(TAG, "List to add to: " + this.list.getListName());
            final List finalList = this.list;
            getActivity().setTitle("Add a Location");
            
            SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.i(TAG, "Place: " + place.getName().toString());
                    final Place finalPlace = place;

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                            }
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                TraveListLatLng newTraveListLatLng = new TraveListLatLng();
                                newTraveListLatLng.setLatitude(finalPlace.getLatLng().latitude);
                                newTraveListLatLng.setLongitude(finalPlace.getLatLng().longitude);

                                Location newLocation = new Location(finalPlace.getName().toString(), "", newTraveListLatLng);
                                if (newLocation == null) {
                                    Log.i(TAG, "LOCATION IS NULL");
                                }
                                if (list == null) {
                                    Log.i(TAG, "LIST IS NULL");
                                    Log.e("t", list.getListName());
                                }
                                list.addLocation(newLocation);
                                if (list != null) {
                                    Log.i(TAG, list.getListName());
                                    Log.i(TAG, "New size of list: " + list.getListSize());
                                }

                                mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                //  mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).child(newLocation.getLocationName()).setValue(newLocation);

                                CustomListFragment fragment = CustomListFragment.newInstance(list);
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Add " + place.getName() + " to " + list.getListName() + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();



                }
                // display Places autocompleteFragment widget


                @Override
                public void onError(Status status) {

                }
            });
           

        } else {
            getActivity().setTitle("Location Search");
            SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.i(TAG, "Place: " + place.getName().toString());

                    TraveListLatLng newTraveListLatLng = new TraveListLatLng();
                    newTraveListLatLng.setLatitude(place.getLatLng().latitude);
                    newTraveListLatLng.setLongitude(place.getLatLng().longitude);

                    Location newLocation = new Location(place.getName().toString(), "", newTraveListLatLng);
                    MapFragment m = MapFragment.newInstance(newLocation);
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, m).addToBackStack(null).commit();
                }


                @Override
                public void onError(Status status) {

                }
            });

        }

        return view;
    }

    /**
     * Switches fragments to display the list of locations.
     *
     * @param list
     */
    public void displayList(List list) {
        CustomListFragment fragment = CustomListFragment.newInstance(list);
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
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
        killAutocompleteFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        killAutocompleteFragment();
    }

    @Override
    public void onDestroy()
    {
        killAutocompleteFragment();
        super.onDestroy();
    }

    private void killAutocompleteFragment() {
        SupportPlaceAutocompleteFragment autocompleteFragment = ((SupportPlaceAutocompleteFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment));

        if(autocompleteFragment != null) {
            FragmentManager fM = getFragmentManager();
            fM.beginTransaction().remove(autocompleteFragment).commit();
        }

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
    @Override
    public void onBackPressed(){
        if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
            getActivity().getSupportFragmentManager().popBackStack();
    }
}
