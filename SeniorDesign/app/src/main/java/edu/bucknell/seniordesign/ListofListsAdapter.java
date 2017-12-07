package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * ListofListsAdapter.java
 * TraveList - Senior Design
 *
 * Adapter to populate the view for a List of Lists
 *
 * Created by Jack on 10/23/2017.
 */

public class ListofListsAdapter extends ArrayAdapter<List> {

    // Array of lists
    private ArrayList<List> listArray = new ArrayList<List>();

    // Reference to database
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();

    // User
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // User email
    private String userEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");

    // Constructor given a Context and an int
    public ListofListsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    // Constructor gien a Context, int, and ArrayList
    public ListofListsAdapter(Context context, int resource, ArrayList<List> lists) {
        super(context, resource, lists);
        this.listArray = lists;
    }

    @Override
    public int getCount(){
        return this.listArray.size();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();


        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.listlayout, null);
        }

        List list = this.listArray.get(position);


        if (list != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.listItemImage);
            TextView name = (TextView) view.findViewById(R.id.textViewName);
            TextView description = (TextView) view.findViewById(R.id.textViewDescription);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            TextView progressText = (TextView) view.findViewById(R.id.progressTextView);
            TextView textView = (TextView) view.findViewById(R.id.textView4);
            ImageView delete = (ImageView) view.findViewById(R.id.delete);
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            if (progressBar !=null){
                int intProgress = list.getCompletionStatus();
                progressBar.setProgress(intProgress);
                textView.setText(" " + Integer.toString(list.getCompletionStatus())+ "%");
                if(list.getCompletionStatus()==100){
                    int color = 0xFF00FF00;
                    progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
                mDb.child("Users").child(userEmail).child("lists").child(listArray.get(position).getListName()).child("completionStatus").setValue(intProgress);
            }
            final ListofListsAdapter finalListofListAdapter = this;
            final int finalPosition = position;
            delete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                mDb.child("Users").child(userEmail).child("lists").child(listArray.get(finalPosition).getListName()).removeValue();
                                listArray.remove(finalPosition);
                                finalListofListAdapter.notifyDataSetChanged();
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure you want to delete this list? Once it has been deleted this action cannot be undone.").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                }
            });
            delete.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            view.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            view.getDrawable().clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });

            String img = null;
            if (list != null && list.getLocationArray().size() > 0) {
                img = list.getLocationArray().get(0).getImageUrl();
            }

            if (img != null && icon != null) {
                Glide.with(getContext()).load(img).into(icon);
            } else if (icon != null) {
                icon.setImageResource(R.drawable.ic_menu_gallery);
            }

            if (name != null) {
                name.setText(list.getListName());
            }

            if (description != null) {
                description.setText(list.getListDescription());
            }
        }
        return view;
    }
}
