package edu.bucknell.seniordesign;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.*;

import static android.R.id.list;

/**
 * Created by Jack on 10/13/2017.
 */

public class DefaultListLoader extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list);





        String parkslist[]= {"Yosemite National Park", "Yellowstone National Park", "Grand Canyon National Park", "Glacier National Park", "Arches National Park" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),
                android.R.layout.simple_list_item_1, parkslist);
        getListView().setAdapter(adapter);


    }
}
