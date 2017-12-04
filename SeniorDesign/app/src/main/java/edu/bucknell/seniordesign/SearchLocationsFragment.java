package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


/**
 * SearchLocationsFragment.java
 * TraveList - Senior Design
 *
 * Fragment for searching for a location.
 *
 */
public class SearchLocationsFragment extends android.support.v4.app.Fragment implements OnBackPressedListener{
    private FirebaseStorage storage= FirebaseStorage.getInstance("gs://natparksdb.appspot.com");
    private StorageReference storageRef= storage.getReference();
    private String u;
    private AlertDialog.Builder b ;
    private AlertDialog a;

    private GeoDataClient mGeoDataClient;
    // OnFragmentInteractionListener
    private OnFragmentInteractionListener mListener;

    // Autocomplete fragment used to search for locations
    private SupportPlaceAutocompleteFragment autocompleteFragment;

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

    // No arguments constructor
    public SearchLocationsFragment() {
    }

    // Create a new instance of SearchLocationsFragment given no parameters
    public static SearchLocationsFragment newInstance() {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
        return fragment;
    }

    // Create a new instance of SearchLocationsFragment with a List parameter
    public static SearchLocationsFragment newInstance(List list) {
        SearchLocationsFragment fragment = new SearchLocationsFragment();
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
        mDb = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        convertUserEmail();

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

        mView = view;
        b = new AlertDialog.Builder(getContext());
        // Gets the List to add the locations to
        Bundle bundle = getArguments();
        if (bundle != null) {
            final List finalList = this.list;
            getActivity().setTitle("Add a Location");

            SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
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
                                final TraveListLatLng f= newTraveListLatLng;
                                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(finalPlace.getId());
                                photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                                        // Get the list of photos.
                                        PlacePhotoMetadataResponse photos = task.getResult();
                                        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                                        // Get the first photo in the list.
                                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                                        // Get the attribution text.
                                        CharSequence attribution = photoMetadata.getAttributions();

                                        // Get a full-size bitmap for the photo.
                                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                            @Override
                                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                                PlacePhotoResponse photo = task.getResult();
                                                Bitmap bitmap = photo.getBitmap();
                                                pic=bitmap;
                                                if( pic==null) {
                                                    Log.e("ASGA", "ASGA");
                                                    Location newLocation = new Location(finalPlace.getName().toString(), "", f);

                                                    list.addLocation(newLocation);

                                                    mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                                    CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                    fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                                                }else{
                                                    b.setMessage("Loading..").setCancelable(false);
                                                    a=b.create();
                                                    a.show();

                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    pic.compress(Bitmap.CompressFormat.JPEG,100, baos);
                                                    byte[] data= baos.toByteArray();
                                                    StorageReference picRef = storageRef.child(finalPlace.getName().toString()+".jpg");

                                                    UploadTask uploadTask= picRef.putBytes(data);
                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Uri downloadUrl=taskSnapshot.getDownloadUrl();
                                                            Location newLocation = new Location(finalPlace.getName().toString(), "", f, downloadUrl.toString());

                                                            list.addLocation(newLocation);
                                                            a.dismiss();

                                                            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);
                                                            CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                                                        }
                                                    });
                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Location newLocation = new Location(finalPlace.getName().toString(), "", f);

                                                            list.addLocation(newLocation);

                                                            mDb.child("Users").child(userEmail).child("lists").child(list.getListName()).setValue(list);

                                                            CustomListFragment fragment = CustomListFragment.newInstance(list);
                                                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                            fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();

                                                    }
                                                });

                                            }
                                        }
                                    });}
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
            SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

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
    public void onDestroyView() {
        super.onDestroyView();
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
        autocompleteFragment.getView().setVisibility(View.GONE);
        getActivity().onBackPressed();
    }
}
