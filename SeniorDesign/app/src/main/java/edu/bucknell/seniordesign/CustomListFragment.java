package edu.bucknell.seniordesign;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.ListFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class CustomListFragment extends Fragment {
    DatabaseReference mDb;
    ListView listview;
    ArrayList<List> lists;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lists= new ArrayList<List>();
        mDb = FirebaseDatabase.getInstance().getReference();

        mDb.child("DefaultLists").child("DefaultList0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List n = dataSnapshot.getValue(List.class);
                lists.add(n);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDb.child("DefaultLists").child("DefaultList1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List n = dataSnapshot.getValue(List.class);
                lists.add(n);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDb.child("DefaultLists").child("DefaultList2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List n = dataSnapshot.getValue(List.class);
                lists.add(n);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
      ListAdapter adapter = new ListAdapter(getActivity(), R.layout.listlayout, lists);
        listview= (ListView) getView().findViewById(android.R.id.list);
        listview.setAdapter(adapter);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_choose_list, container, false);
        return view;
    }



}
