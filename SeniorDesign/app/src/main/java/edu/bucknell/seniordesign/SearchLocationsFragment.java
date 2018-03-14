package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * SearchLocationsFragment.java
 * TraveList - Senior Design
 *
 * Fragment for searching for alertDialog location.
 *
 */
public class SearchLocationsFragment extends android.support.v4.app.Fragment implements OnBackPressedListener{

    // Reference to FirebaseStorage
    private FirebaseStorage storage= FirebaseStorage.getInstance("gs://natparksdb.appspot.com");

    // StorageReference used to gt reference to Firebase Storage
    private StorageReference storageRef= storage.getReference();

    // AlertDialog builder
    private AlertDialog.Builder builder;

    // AlertDialog
    private AlertDialog alertDialog;

    // GeoDataClient
    private GeoDataClient mGeoDataClient;

    // OnFragmentInteractionListener
    private OnFragmentInteractionListener mListener;

    // Reference to database
    private DatabaseReference mDb;

    // User
    private FirebaseUser user;

    // User email
    private String userEmail;

    private Bitmap pic;

    // Static view
    private static View view;

    // View
    private View mView;


    // List to add to
    private List list = null;
    private SearchLocationsFragment k;

    // No arguments constructor
    public SearchLocationsFragment() {
    }

    // Create alertDialog new instance of SearchLocationsFragment given no parameters
    public static SearchLocationsFragment newInstance() {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
        return fragment;
    }

    // Create alertDialog new instance of SearchLocationsFragment with alertDialog List parameter
    public static SearchLocationsFragment newInstance(List list) {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
        fragment.k= fragment;
        fragment.list = list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        getActivity().setTitle("Add Location");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
        n.isNetworkAvailable();

        mDb = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        convertUserEmail();

        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        try {
            mView = inflater.inflate(R.layout.fragment_search_locations, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

       // mView = view;
        builder = new AlertDialog.Builder(getContext());
        // Gets the List to add the locations to
        Bundle bundle = getArguments();
        if (bundle != null) {
            final List finalList = this.list;
            getActivity().setTitle("Add Location");

          final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    final Place finalPlace = place;
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                getChildFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


                                Bundle bundle = new Bundle();
                                bundle.putSerializable("current_list", list);
                                SearchLocationsFragment frag = SearchLocationsFragment.newInstance(list);
                                frag.setArguments(bundle);
                                fragmentManager.popBackStack();
                                fragmentManager.popBackStack();

                                CustomListFragment fragment= CustomListFragment.newInstance(list, false);
                                fragmentManager.beginTransaction().replace(R.id.content_frag,fragment).addToBackStack(null).commit();
                                fragmentManager.beginTransaction().replace(R.id.content_frag,frag).addToBackStack(null).commit();
                                //getActivity().getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                            }
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                getChildFragmentManager().beginTransaction().remove(autocompleteFragment).commit();

                               // getActivity().getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                                TraveListLatLng newTraveListLatLng = new TraveListLatLng();
                                newTraveListLatLng.setLatitude(finalPlace.getLatLng().latitude);
                                newTraveListLatLng.setLongitude(finalPlace.getLatLng().longitude);
                                final TraveListLatLng f= newTraveListLatLng;
                                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(finalPlace.getId());
                                photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                                        // Get the list of photos.
                                        PlacePhotoMetadataResponse photos = task.getResult();
                                        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                                        PlacePhotoMetadata photoMetadata;
                                        // Get the first photo in the list.
                                        if(photoMetadataBuffer.getCount()==0){
                                            photoMetadataBuffer.release();
                                            Location newLocation = new Location(finalPlace.getName().toString(), "", f);
                                            if(finalPlace.getPlaceTypes()!=null && finalPlace.getPlaceTypes().size()>1){
                                            String t= getPlaceType(finalPlace.getPlaceTypes().get(0));
                                            newLocation.setLocationDescription(t);
                                            }
                                            if(finalPlace.getAddress()!=null){
                                                newLocation.setAddress(finalPlace.getAddress().toString());

                                            }
                                            String snip="";
                                            if(!finalPlace.getAddress().equals(finalPlace.getName())){
                                                snip=snip+ finalPlace.getAddress()+ "\n";
                                            }
                                            snip=snip+"Category: "+ getPlaceType(finalPlace.getPlaceTypes().get(0)) +"\n";
                                            if(finalPlace.getRating()>0){
                                                snip=snip+"Rating: "+ finalPlace.getRating()+"/5"+"\n";
                                            }
                                            if(!finalPlace.getPhoneNumber().equals("")) {
                                                snip = snip + "Phone: " + finalPlace.getPhoneNumber()+"\n";

                                            }

                                            newLocation.setSnip(snip);



                                            list.addLocation(newLocation);

                                            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                            CustomListFragment fragment = CustomListFragment.newInstance(list);
                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            //fragmentManager.beginTransaction().remove(k).commit();
                                            fragmentManager.popBackStack();
                                            fragmentManager.beginTransaction().replace(R.id.content_frag,fragment).addToBackStack(null).commit();
                                            fragmentManager.popBackStack();
                                            return;
                                        }else{
                                            photoMetadata = photoMetadataBuffer.get(0);
                                            }

                                        // Get alertDialog full-size bitmap for the photo.
                                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                            @Override
                                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                                PlacePhotoResponse photo = task.getResult();
                                                Bitmap bitmap = photo.getBitmap();
                                                pic=bitmap;
                                                if( pic==null) {
                                                    Location newLocation = new Location(finalPlace.getName().toString(), "", f);
                                                    if(finalPlace.getPlaceTypes()!=null && finalPlace.getPlaceTypes().size()>1){
                                                        String t= getPlaceType(finalPlace.getPlaceTypes().get(0));
                                                        newLocation.setLocationDescription(t);
                                                    }
                                                    if(finalPlace.getAddress()!=null){
                                                        newLocation.setAddress(finalPlace.getAddress().toString());
                                                    }
                                                    String snip="";
                                                    if(!finalPlace.getAddress().equals(finalPlace.getName())){
                                                        snip=snip+ finalPlace.getAddress()+ "\n";
                                                    }
                                                    snip=snip+"Category: "+ getPlaceType(finalPlace.getPlaceTypes().get(0)) +"\n";
                                                    if(finalPlace.getRating()>0){
                                                        snip=snip+"Rating: "+ finalPlace.getRating()+"/5"+"\n";
                                                    }
                                                    if(!finalPlace.getPhoneNumber().equals("")) {
                                                        snip = snip + "Phone: " + finalPlace.getPhoneNumber()+"\n";

                                                    }

                                                    newLocation.setSnip(snip);


                                                    list.addLocation(newLocation);

                                                    mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                                    CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                    //fragmentManager.beginTransaction().remove(k).commit();

                                                    getActivity().getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                                                    fragmentManager.popBackStack();
                                                    fragmentManager.beginTransaction().replace(R.id.content_frag,fragment).addToBackStack(null).commit();
                                                    fragmentManager.popBackStack();
                                                }else{
                                                    builder.setMessage("Loading..").setCancelable(false);
                                                    alertDialog = builder.create();
                                                    alertDialog.show();

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    pic.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
                                                    byte[] data= byteArrayOutputStream.toByteArray();
                                                    StorageReference picRef = storageRef.child(finalPlace.getName().toString()+".jpg");

                                                    UploadTask uploadTask= picRef.putBytes(data);
                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Uri downloadUrl=taskSnapshot.getDownloadUrl();
                                                            Location newLocation = new Location(finalPlace.getName().toString(), "", f, downloadUrl.toString());
                                                            if(finalPlace.getPlaceTypes()!=null && finalPlace.getPlaceTypes().size()>1){
                                                                String t= getPlaceType(finalPlace.getPlaceTypes().get(0));
                                                                newLocation.setLocationDescription(t);
                                                            }
                                                            if(finalPlace.getAddress()!=null){
                                                                newLocation.setAddress(finalPlace.getAddress().toString());
                                                            }
                                                            String snip="";
                                                            if(!finalPlace.getAddress().equals(finalPlace.getName())){
                                                                snip=snip+ finalPlace.getAddress()+ "\n";
                                                            }
                                                            snip=snip+"Category: "+ getPlaceType(finalPlace.getPlaceTypes().get(0)) +"\n";
                                                            if(finalPlace.getRating()>0){
                                                                snip=snip+"Rating: "+ finalPlace.getRating()+"/5"+"\n";
                                                            }
                                                            if(!finalPlace.getPhoneNumber().equals("")) {
                                                                snip = snip + "Phone: " + finalPlace.getPhoneNumber()+"\n";

                                                            }

                                                            newLocation.setSnip(snip);

                                                            list.addLocation(newLocation);
                                                            alertDialog.dismiss();

                                                            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);
                                                            CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                          //  fragmentManager.beginTransaction().remove(k).commit();
                                                            fragmentManager.popBackStack();
                                                            fragmentManager.beginTransaction().replace(R.id.content_frag,fragment).addToBackStack(null).commit();
                                                            fragmentManager.popBackStack();

                                                        }
                                                    });
                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Location newLocation = new Location(finalPlace.getName().toString(), "", f);
                                                            if(finalPlace.getPlaceTypes()!=null && finalPlace.getPlaceTypes().size()>1){
                                                                String t= getPlaceType(finalPlace.getPlaceTypes().get(0));
                                                                newLocation.setLocationDescription(t);
                                                            }
                                                            if(finalPlace.getAddress()!=null){
                                                                newLocation.setAddress(finalPlace.getAddress().toString());
                                                            }
                                                            String snip="";
                                                            if(!finalPlace.getAddress().equals(finalPlace.getName())){
                                                                snip=snip+ finalPlace.getAddress()+ "\n";
                                                            }
                                                            snip=snip+"Category: "+ getPlaceType(finalPlace.getPlaceTypes().get(0)) +"\n";
                                                            if(finalPlace.getRating()>0){
                                                                snip=snip+"Rating: "+ finalPlace.getRating()+"/5"+"\n";
                                                            }
                                                            if(!finalPlace.getPhoneNumber().equals("")) {
                                                                snip = snip + "Phone: " + finalPlace.getPhoneNumber()+"\n";

                                                            }

                                                            newLocation.setSnip(snip);

                                                            alertDialog.dismiss();
                                                            list.addLocation(newLocation);

                                                            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                                            CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                           // fragmentManager.beginTransaction().remove(k).commit();
                                                            fragmentManager.popBackStack();
                                                            fragmentManager.beginTransaction().replace(R.id.content_frag,fragment).addToBackStack(null).commit();
                                                            fragmentManager.popBackStack();

                                                    }
                                                });

                                            }
                                        }
                                    });
                                        photoMetadataBuffer.release();}
                                });
                        }
                    }};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Add " + place.getName() + " to " + list.getListName() + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                }

                @Override
                public void onError(Status status) {

                }
            });

        } else {
            getActivity().setTitle("Location Search");

            final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    //getChildFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
                    TraveListLatLng newTraveListLatLng = new TraveListLatLng();
                    newTraveListLatLng.setLatitude(place.getLatLng().latitude);
                    newTraveListLatLng.setLongitude(place.getLatLng().longitude);
                    String snip="";
                    if(!place.getAddress().equals(place.getName())){
                        snip=snip+ place.getAddress()+ "\n";
                    }
                    snip=snip+"Category: "+ getPlaceType(place.getPlaceTypes().get(0)) +"\n";
                    if(place.getRating()>0){
                        snip=snip+"Rating: "+ place.getRating()+"/5"+"\n";
                    }
                    if(!place.getPhoneNumber().equals("")) {
                        snip = snip + "Phone: " + place.getPhoneNumber()+"\n";

                    }

                    Location newLocation = new Location(place.getName().toString(), "", newTraveListLatLng);
                    newLocation.setSnip(snip);




                    MapFragment m = MapFragment.newInstance(newLocation);
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commitAllowingStateLoss();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, m).addToBackStack(null).commit();



                }

                @Override
                public void onError(Status status) {

                }
            });
        }
        return mView;
    }

    // Convert user email. Firebase keys cannot contain '.' so emails must be converted to have ',' instead
    private void convertUserEmail() {
        userEmail = user.getEmail().replace(".", ",");
    }

    // Switches fragments to display the list of locations.
    public void displayList(List list) {
        CustomListFragment fragment = CustomListFragment.newInstance(list);
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){


        super.onSaveInstanceState(null);

    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onBackPressed() {

        getActivity().onBackPressed();
    }

    public String getPlaceType(int ptype){
        if(ptype==Place.TYPE_ACCOUNTING){
            return "Accounting";

        }
        else if( ptype== Place.TYPE_AIRPORT){
            return "Airport";

        }
        else if( ptype== Place.TYPE_AMUSEMENT_PARK){
            return "Amusement Park";

        }
        else if( ptype== Place.TYPE_AQUARIUM){
            return "Aquarium";

        }
        else if( ptype== Place.TYPE_ART_GALLERY){
            return "Art Gallery";

        }
        else if( ptype== Place.TYPE_ATM){
            return "ATM";

        }

        else if( ptype== Place.TYPE_BAKERY){
            return "Bakery";

        }

        else if( ptype== Place.TYPE_BANK){
            return "Bank";

        }

        else if( ptype== Place.TYPE_BAR){
            return "Bar";

        }

        else if( ptype== Place.TYPE_BEAUTY_SALON){
            return "Beauty Salon";

        }

        else if( ptype== Place.TYPE_BICYCLE_STORE){
            return "Bicycle Store";

        }

        else if( ptype== Place.TYPE_BOOK_STORE){
            return "Book Store";
        }

        else if( ptype== Place.TYPE_BOWLING_ALLEY){
            return "Bowling Alley";
        }

        else if( ptype== Place.TYPE_BUS_STATION){
            return "Bus Station";

        }

        else if( ptype== Place.TYPE_CAFE){
            return "Café";

        }

        else if( ptype== Place.TYPE_CAMPGROUND){
            return "Campground";

        }

        else if( ptype== Place.TYPE_CAR_DEALER){
            return "Car Dealer";

        }

        else if( ptype== Place.TYPE_CAR_RENTAL){
            return "Car Rental";

        }

        else if( ptype== Place.TYPE_CAR_REPAIR){
            return "Car Repair";

        }

        else if( ptype== Place.TYPE_CAR_WASH){
            return "Car Wash";

        }

        else if( ptype== Place.TYPE_CASINO){
            return "Casino";

        }

        else if( ptype== Place.TYPE_CEMETERY){
            return "Cemetery";

        }

        else if( ptype== Place.TYPE_CHURCH){
            return "Church";

        }

        else if( ptype== Place.TYPE_CITY_HALL){
            return "City Hall";

        }

        else if( ptype== Place.TYPE_CLOTHING_STORE){
            return "Clothing Store";

        }

        else if( ptype== Place.TYPE_COLLOQUIAL_AREA){
            return "Colloquial Area";

        }

        else if( ptype== Place.TYPE_CONVENIENCE_STORE){
            return "Convenience Store";

        }

        else if( ptype== Place.TYPE_COUNTRY){
            return "Country";

        }

        else if( ptype== Place.TYPE_COURTHOUSE){
            return "Courthouse";

        }

        else if( ptype== Place.TYPE_DENTIST){
            return "Dentist";

        }

        else if( ptype== Place.TYPE_DEPARTMENT_STORE){
            return "Department Store";

        }

        else if( ptype== Place.TYPE_DOCTOR){
            return "Doctor";

        }

        else if( ptype== Place.TYPE_ELECTRICIAN){
            return "Electrician";

        }

        else if( ptype== Place.TYPE_ELECTRONICS_STORE){
            return "Electronics Store";

        }

        else if( ptype== Place.TYPE_EMBASSY){
            return "Embassy";

        }

        else if( ptype== Place.TYPE_ESTABLISHMENT){
            return "Establishment";

        }

        else if( ptype== Place.TYPE_FINANCE){
            return "Finance";

        }

        else if( ptype== Place.TYPE_FIRE_STATION){
            return "Fire Station";

        }

        else if( ptype== Place.TYPE_FLOOR){
            return "Floor";

        }

        else if( ptype== Place.TYPE_FLORIST){
            return "Florist";

        }

        else if( ptype== Place.TYPE_FOOD){
            return "Food";

        }

        else if( ptype== Place.TYPE_FUNERAL_HOME){
            return "Funeral Home";
        }

        else if( ptype== Place.TYPE_FURNITURE_STORE){
            return "Furniture Store";

        }

        else if( ptype== Place.TYPE_GAS_STATION){
            return "Gas Station";

        }

        else if( ptype== Place.TYPE_GENERAL_CONTRACTOR){
            return "General Contractor";

        }

        else if( ptype== Place.TYPE_GEOCODE){
            return "Geocode";

        }

        else if( ptype== Place.TYPE_GROCERY_OR_SUPERMARKET){
            return "Grocery Store";

        }

        else if( ptype== Place.TYPE_GYM){
            return "Gym";

        }

        else if( ptype== Place.TYPE_HAIR_CARE){
            return "Hair Care";

        }

        else if( ptype== Place.TYPE_HARDWARE_STORE){
            return "Hardware Store";

        }

        else if( ptype== Place.TYPE_HEALTH){
            return "Health";

        }

        else if( ptype== Place.TYPE_HINDU_TEMPLE){
            return "Hindu Temple";

        }

        else if( ptype== Place.TYPE_HOME_GOODS_STORE){
            return "Home Goods Store";

        }

        else if( ptype== Place.TYPE_HOSPITAL){
            return "Hospital";
        }

        else if( ptype== Place.TYPE_INSURANCE_AGENCY){
            return "Insurance Agency";
        }

        else if( ptype== Place.TYPE_INTERSECTION){
            return "Intersection";

        }

        else if( ptype== Place.TYPE_JEWELRY_STORE){
            return "Jewelry Store";

        }

        else if( ptype== Place.TYPE_LAUNDRY){
            return "Laundry";

        }

        else if( ptype== Place.TYPE_LAWYER){
            return "Lawyer";

        }

        else if( ptype== Place.TYPE_LIBRARY){
            return "Library";

        }

        else if( ptype== Place.TYPE_LIQUOR_STORE){
            return "Liquor Store";

        }

        else if( ptype== Place.TYPE_LOCAL_GOVERNMENT_OFFICE){
            return "Government Office";

        }

        else if( ptype== Place.TYPE_LOCALITY){
            return "City/Town";

        }

        else if( ptype== Place.TYPE_LOCKSMITH){
            return "Locksmith";
        }

        else if( ptype== Place.TYPE_LODGING){
            return "Lodging";

        }

        else if( ptype== Place.TYPE_MEAL_DELIVERY){
            return "Food Delivery";

        }

        else if( ptype== Place.TYPE_MEAL_TAKEAWAY){
            return "Food Takeout";

        }

        else if( ptype== Place.TYPE_MOSQUE){
            return "Mosque";

        }

        else if( ptype== Place.TYPE_MOVIE_RENTAL){
            return "Movie Rental";

        }

        else if( ptype== Place.TYPE_MOVIE_THEATER){
            return "Movie Theater";

        }

        else if( ptype== Place.TYPE_MOVING_COMPANY){
            return "Moving Company";

        }

        else if( ptype== Place.TYPE_MUSEUM){
            return "Museum";

        }

        else if( ptype== Place.TYPE_NATURAL_FEATURE){
            return "Natural Feature";

        }

        else if( ptype== Place.TYPE_NEIGHBORHOOD){
            return "Neighborhood";

        }

        else if( ptype== Place.TYPE_NIGHT_CLUB){
            return "Night Club";

        }

        else if( ptype== Place.TYPE_OTHER){
            return "Other";

        }

        else if( ptype== Place.TYPE_PAINTER){
            return "Painter";

        }

        else if( ptype== Place.TYPE_PARK){
            return "Park";

        }

        else if( ptype== Place.TYPE_PARKING){
            return "Parking";

        }

        else if( ptype== Place.TYPE_PET_STORE){
            return "Pet Store";

        }

        else if( ptype== Place.TYPE_PHARMACY){
            return "Pharmacy";

        }

        else if( ptype== Place.TYPE_PHYSIOTHERAPIST){
            return "Physiotherapist";

        }

        else if( ptype== Place.TYPE_PLACE_OF_WORSHIP){
            return "Place of Worship";

        }

        else if( ptype== Place.TYPE_PLUMBER){
            return "Plumber";

        }

        else if( ptype== Place.TYPE_POINT_OF_INTEREST){
            return "Point of Interest";

        }

        else if( ptype== Place.TYPE_POLICE){
            return "Police Station";

        }

        else if( ptype== Place.TYPE_POLITICAL){
            return "Political";

        }

        else if( ptype== Place.TYPE_POST_BOX){
            return "Post Box";

        }

        else if( ptype== Place.TYPE_POST_OFFICE){
            return "Post Office";

        }

        else if( ptype== Place.TYPE_REAL_ESTATE_AGENCY){
            return "Real Estate Agency";

        }

        else if( ptype== Place.TYPE_RESTAURANT){
            return "Restaurant";

        }

        else if( ptype== Place.TYPE_ROOFING_CONTRACTOR){
            return "Roofing Contractor";

        }

        else if( ptype== Place.TYPE_ROOM){
            return "Room";

        }

        else if( ptype== Place.TYPE_ROUTE){
            return "Route";

        }

        else if( ptype== Place.TYPE_RV_PARK){
            return "RV Park";

        }

        else if( ptype== Place.TYPE_SCHOOL){
            return "School";

        }

        else if( ptype== Place.TYPE_SHOE_STORE){
            return "Shoe Store";

        }

        else if( ptype== Place.TYPE_SHOPPING_MALL){
            return "Shopping Mall";

        }

        else if( ptype== Place.TYPE_SPA){
            return "Spa";

        }

        else if( ptype== Place.TYPE_STADIUM){
            return "Stadium";

        }

        else if( ptype== Place.TYPE_STORAGE){
            return "Storage";

        }

        else if( ptype== Place.TYPE_STORE){
            return "Store";

        }

        else if( ptype== Place.TYPE_STREET_ADDRESS){
            return "Street Address";

        }

        else if( ptype== Place.TYPE_SUBWAY_STATION){
            return "Subway Station";

        }

        else if( ptype== Place.TYPE_SYNAGOGUE){
            return "Synagogue";

        }

        else if( ptype== Place.TYPE_TAXI_STAND){
            return "Taxi Stand";

        }

        else if( ptype== Place.TYPE_TRAIN_STATION){
            return "Train Station";

        }

        else if( ptype== Place.TYPE_TRANSIT_STATION){
            return "Transit Station";

        }

        else if( ptype== Place.TYPE_TRAVEL_AGENCY){
            return "Travel Agency";

        }

        else if( ptype== Place.TYPE_UNIVERSITY){
            return "University";

        }

        else if( ptype== Place.TYPE_VETERINARY_CARE){
            return "Veterinarian";

        }

        else if( ptype== Place.TYPE_ZOO){
            return "Zoo";
        }
        else{
            return "";
        }
    }
}
