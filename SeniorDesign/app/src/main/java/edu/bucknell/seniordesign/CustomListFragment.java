package edu.bucknell.seniordesign;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;

import com.google.android.gms.maps.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class CustomListFragment extends Fragment implements View.OnClickListener, OnBackPressedListener {
    private boolean isDefault;
    private DatabaseReference mDb;
    private List list = null;
    private ArrayList<List> defaultList = null;


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

        if (isDefault) {
            DefaultListAdapter adapter = new DefaultListAdapter(getActivity(),
                    R.layout.listlayout, (ArrayList<List>) getArguments().getSerializable("list"));
            listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    List n= defaultList.get(position);
                    CustomListFragment fragment= CustomListFragment.newInstance(n);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(v.getId(), fragment).addToBackStack(null).commit();



                }});


        } else {
            ListAdapter adapter = new ListAdapter(getActivity(), R.layout.listlayout, this.list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
        return rootView;

    }



    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static CustomListFragment newInstance(List l) {
        Bundle args = new Bundle();
        args.putSerializable("list", l);
        CustomListFragment n = new CustomListFragment();
        n.setDefault(false);
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

        n.setDefault(d);
        n.setmDb();
        n.setArguments(args);
        n.setDefaultList(defaultList);
        return n;
    }

    public void setList(List l) {
        this.list = l;
    }

    public void setDefaultList(ArrayList<List> dlist) {
        this.defaultList = dlist;
    }

    public void setDefault(boolean d) {
        this.isDefault = d;
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

