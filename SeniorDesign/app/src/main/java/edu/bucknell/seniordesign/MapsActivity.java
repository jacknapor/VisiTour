package edu.bucknell.seniordesign;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Tasks;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private LocationManager locationManager;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!checkPermissions()) {
            requestPermissons();
        }

        //initializeLocationManager();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng point = new LatLng(40.9547, -76.8836);

        MarkerOptions m = new MarkerOptions();
        m.position(point);
        m.title("Bucknell");
        m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(m);

        mMap.moveCamera((CameraUpdateFactory.newLatLng(point)));

        //LatLng point = addPoint("Bucknell", 40.9547, -76.8836);


        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 13));


    }

    public LatLng addPoint(String name, double lat, double lng) {
        LatLng point = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(point).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        return point;
    }

    private void initializeLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
            mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        /*try {
            if (checkPermissions()) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }*/
    }

    public boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissons() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "This app needs to access your location.", Toast.LENGTH_SHORT).show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
    }
}
