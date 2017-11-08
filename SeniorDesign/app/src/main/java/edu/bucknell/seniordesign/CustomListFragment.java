package edu.bucknell.seniordesign;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class CustomListFragment extends android.support.v4.app.Fragment implements View.OnClickListener, OnBackPressedListener {
    private boolean isLists;
    private DatabaseReference mDb;
    private List list = null;
    private ArrayList<List> listoflists = null;


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

        if (isLists) {
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

