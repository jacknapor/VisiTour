package edu.bucknell.seniordesign;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jack on 10/23/2017.
 */

public class CustomListFragment extends android.support.v4.app.Fragment implements View.OnClickListener, OnBackPressedListener {
    private boolean isLists;

    private DatabaseReference mDb;

    private String listName = null;


    private GoogleMap map = MapFragment.newInstance().getMap();

    private List list = null;
    private ArrayList<List> listoflists = null;

    private FloatingActionButton addLocationButton;
    private FloatingActionButton fbShareButton;
    private ShareDialog shareDialog; //del

    private String TAG = "CustomListFragment";
    private String imageUrl = null;



    @Override
    public void onBackPressed(){
        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_choose_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        final ViewGroup vg=container;
        final List l= this.list;



            addLocationButton = (FloatingActionButton) rootView.findViewById(R.id.add_location_button);
            addLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();

                   // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                Bundle bundle = new Bundle();

                bundle.putSerializable("current_list", l);
                SearchLocationsFragment fragment= SearchLocationsFragment.newInstance(l);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(vg.getId(), fragment).addToBackStack(null).commit();



                }
            });

        shareDialog = new ShareDialog(this);
        fbShareButton = (FloatingActionButton) rootView.findViewById(R.id.fb_share_button);


        if (isLists) {
            getActivity().setTitle("Lists");
            addLocationButton.hide();

            ListofListsAdapter adapter = new ListofListsAdapter(getActivity(),
                    R.layout.listlayout, (ArrayList<List>) getArguments().getSerializable("list"));
            listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {


                    List n= listoflists.get(position);
                    CustomListFragment fragment= CustomListFragment.newInstance(n);
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(vg.getId(), fragment).addToBackStack(null).commit();

                }});


        } else {
            fbShareButton.setVisibility(View.VISIBLE);
            listName = this.list.getListName();
            imageUrl = this.list.getLocation(0).getImageUrl();
            fbShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (shareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(imageUrl))
                                .setQuote("I have completed some of " + listName)
                                .build();
                        shareDialog.show(linkContent);
                    }
                }
            });
            getActivity().setTitle(this.list.getListName());
            ListAdapter adapter = new ListAdapter(getActivity(), R.layout.listlayout, this.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "HERE I AM");


                    Location loc = list.getLocation(position);
                    Log.e(TAG, "Location: " + loc.getTraveListLatLng().getLatitude());

                    MapFragment fragment = MapFragment.newInstance(loc);
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(vg.getId(), fragment).addToBackStack(null).commit();



                }
            });

        }
        return rootView;

    }



    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(List l) {
        Bundle args = new Bundle();
        args.putSerializable("list", l);
        CustomListFragment n = new CustomListFragment();
        n.setIsLists(false);
        n.setmDb();
        n.setArguments(args);
        n.setList(l);
        return n;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(ArrayList<List> defaultList, boolean d) {
        CustomListFragment n = new CustomListFragment();
        Bundle args = new Bundle();
        args.putSerializable("list", defaultList);

        n.setIsLists(d);
        n.setmDb();
        n.setArguments(args);
        n.setListoflists(defaultList);
        return n;
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
}

