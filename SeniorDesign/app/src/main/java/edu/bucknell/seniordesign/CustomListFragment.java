package edu.bucknell.seniordesign;

import android.app.Fragment;
import android.app.FragmentManager;
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


    private String TAG = "CustomListFragment";


    @Override
    public void onBackPressed(){
        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_choose_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        final ViewGroup v=container;


            addLocationButton = (FloatingActionButton) rootView.findViewById(R.id.add_location_button);
            addLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "in onClick");
                    android.support.v4.app.Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = SearchLocationsFragment.class;
                    try {
                        fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
                        Log.i(TAG, "in try");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                Bundle bundle = new Bundle();
                List newList = new List(listName, "");
                bundle.putSerializable("current_list", newList);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frag, fragment);
                fragmentTransaction.addToBackStack(null);


                    fragmentTransaction.replace(R.id.content_frag, fragment);
                    fragmentTransaction.commit();
                    Log.i(TAG, "fragment committed");
                }
            });


        if (isLists) {

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
                    fragmentManager.beginTransaction().replace(v.getId(), fragment).addToBackStack(null).commit();



                }});


        } else {
            ListAdapter adapter = new ListAdapter(getActivity(), R.layout.listlayout, this.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "HERE I AM");


                    Location loc = list.getLocation(position);
                    Log.i(TAG, "Location: " + loc.getLocationName());

                    MapFragment fragment = MapFragment.newInstance(loc);
                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(v.getId(), fragment).addToBackStack(null).commit();



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

