package edu.bucknell.seniordesign;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * CustomListFragment.java
 * TraveList - Senior Design
 *
 * Fragment for displaying lists. Used for lists of lists as well as lists of locations.
 *
 * Created by Jack on 10/23/2017.
 */

public class CustomListFragment extends android.support.v4.app.Fragment implements View.OnClickListener, OnBackPressedListener {

    // Whether or not the list is a list of lists
    private boolean isLists;

    // Reference to database
    private DatabaseReference mDb;

    // Name of list
    private String listName = null;

    // List progress percentage
    private Long percentage = 0l;

    // Google Map instance
    private GoogleMap map = MapFragment.newInstance().getMap();

    // List
    private List list = null;

    // A list of list's ArrayList of lists
    private ArrayList<List> listoflists = null;

    // Button to add new Location
    private FloatingActionButton addLocationButton;

    // Button to share a list to Facebook
    private FloatingActionButton fbShareButton;

    // ShareDialog for Facebook sharing
    private ShareDialog shareDialog; //del

    // image URL
    private String imageUrl = null;

    // User email
    private String userEmail;

    private boolean showalert=true;

    @Override
    public void onBackPressed(){
        Log.e("j","j");
        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
        n.isNetworkAvailable();
        final View rootView =inflater.inflate(R.layout.activity_choose_list, container, false);;

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        final ViewGroup viewGroup = container;

        final List finalList = this.list;
        addLocationButton = (FloatingActionButton) rootView.findViewById(R.id.add_location_button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
                n.isNetworkAvailable();
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_list", finalList);
                SearchLocationsFragment fragment = SearchLocationsFragment.newInstance(finalList);
                fragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
            }
        });
        shareDialog = new ShareDialog(this);

        fbShareButton = (FloatingActionButton) rootView.findViewById(R.id.fb_share_button);

        if (isLists) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
            getActivity().setTitle("Lists");
            addLocationButton.hide();



                ListofListsAdapter adapter = new ListofListsAdapter(getContext(),
                        R.layout.listlayout, listoflists);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    List n= listoflists.get(position);
                    CustomListFragment fragment= CustomListFragment.newInstance(n);
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                }});

        } else {

            fbShareButton.setVisibility(View.VISIBLE);
            listName = this.list.getListName();
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
            if (this.list.getLocationArray().size() > 0) {
                if(this.showalert){
                Toast.makeText(getContext(), "Tap on any location to view it on the map.", Toast.LENGTH_SHORT).show();}else{
                    this.showalert=true;
                }
                imageUrl = finalList.getLocation(0).getImageUrl();
            } else if(this.list.getLocationArray().size()==0){
                if(this.showalert){
                Toast.makeText(getContext(), "Tap the '+' button to begin adding locations to your list.", Toast.LENGTH_LONG).show();}else{
                    this.showalert=true;
                }
            }
            shareDialog = new ShareDialog(getActivity());
            fbShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationDrawerActivity n= (NavigationDrawerActivity)getActivity();
                    n.isNetworkAvailable();
                    mDb.child("Users").child(userEmail).child("lists").child(listName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                if (s.getKey().equals("completionStatus")) {
                                    percentage = Long.parseLong(s.getValue().toString());
                                }
                            }
                           // if (shareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle("TraveList")
                                        .setContentDescription("Track your travels.")
                                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=edu.bucknell.seniordesign"))
                                        .setImageUrl(Uri.parse("https://imgur.com/AYKeqRn"))
                                        .setQuote("I have completed " + Long.toString(percentage) + "% of the list: " + listName)
                                        .build();

                                shareDialog.show(linkContent);

                           // }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
            getActivity().setTitle(this.list.getListName());

            ListAdapter adapter = new ListAdapter(getContext(), R.layout.listlayout, this.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Location location = list.getLocation(position);
                    MapFragment fragment = MapFragment.newInstance(location);
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frag, fragment).addToBackStack(null).commit();
                }
            });
            listView.setAdapter(adapter);
        }
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(List list) {
        Bundle args = new Bundle();
        args.putSerializable("list", list);
        CustomListFragment customListFragment = new CustomListFragment();
        customListFragment.setIsLists(false);
        customListFragment.setmDb();
        customListFragment.setArguments(args);
        customListFragment.setList(list);
        return customListFragment;
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(List list, boolean alert) {
        Bundle args = new Bundle();
        args.putSerializable("list", list);
        CustomListFragment customListFragment = new CustomListFragment();
        customListFragment.setIsLists(false);
        customListFragment.setmDb();
        customListFragment.setArguments(args);
        customListFragment.setList(list);
        customListFragment.showalert=false;
        return customListFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(ArrayList<List> defaultList, boolean isLists) {
        CustomListFragment customListFragment = new CustomListFragment();
        Bundle args = new Bundle();
        args.putSerializable("list", defaultList);

        customListFragment.setIsLists(isLists);
        customListFragment.setmDb();
        customListFragment.setArguments(args);
        customListFragment.setListoflists(defaultList);
        return customListFragment;
    }

    public void setList(List l) {
        this.list = l;
    }

    public void setListoflists(ArrayList<List> dlist) {
        this.listoflists = dlist;
    }

    public void setIsLists(boolean d) {
        this.isLists = d;
    }

    public void setmDb() {
        this.mDb = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onResume() {

        ListView listView= (ListView) getView().findViewById(R.id.list);
        final ListView lv= listView;
        if(isLists && listView.getAdapter()!=null){

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        mDb.child("Users").child(userEmail).child("lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listoflists.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    List listToAdd = s.getValue(List.class);
                    listoflists.add(listToAdd);
                }

                ListofListsAdapter adapter= (ListofListsAdapter) lv.getAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        super.onResume();
    }

}

