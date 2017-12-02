package edu.bucknell.seniordesign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * MapFragment.java
 * TraveList - Senior Design
 *
 * Fragment for Google Maps
 *
 */
public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    // Location
    private Location location;

    // View
    private static View view;

    // MapView
    MapView mapView;

    // Google Map
    GoogleMap mMap;

    final Float ZOOM_LEVEL = 13f;

    // OnFragmentInteractionListener
    private OnFragmentInteractionListener mListener;

    // No arguments constructor
    public MapFragment() {
    }

    // Creates a new instance of the fragment with no parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    // Creates a new instance of the fragment with a Location parameter
    public static MapFragment newInstance(Location loc) {

        Bundle bundle = new Bundle();
        bundle.putString("locName", loc.getLocationName());
        bundle.putSerializable("loc", loc);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setLocation(Location a){
        this.location =a;
        double lat = this.location.getTraveListLatLng().getLatitude();
        double lng = this.location.getTraveListLatLng().getLongitude();
        LatLng googLatLng = new LatLng(lat,lng);
        addPoint(this.location.getLocationName(), googLatLng);
    }

    // Reads a bundle and retrieves arguments.
    public void readBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            String name = bundle.getString("locName");
            getActivity().setTitle(name);

            Location location = (Location) bundle.getSerializable("loc");

            double lat = location.getTraveListLatLng().getLatitude();
            double lng = location.getTraveListLatLng().getLongitude();
            LatLng googLatLng = new LatLng(lat,lng);
            addPoint(name, googLatLng);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);

        } catch (InflateException e) {
            e.printStackTrace();
        }
        getActivity().setTitle("Map");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapFragment.this);

        if(mapView!=null)
        {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
        return view;
    }

    // Removes map fragment
    private void killOldMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.map));

        if(mapFragment != null) {
            FragmentManager fM = getFragmentManager();
            fM.beginTransaction().remove(mapFragment).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        killOldMap();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        killOldMap();
        super.onDestroy();
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        killOldMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readBundle();
    }

    public LatLng addPoint(String name, LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL)));
        return latLng;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public GoogleMap getMap() {
        return mMap;
    }
}
