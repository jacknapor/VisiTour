package edu.bucknell.seniordesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Jack on 10/23/2017.
 */

public class ListofListsAdapter extends ArrayAdapter<List> {
    private ArrayList<List> listarray = new ArrayList<List>();
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userEmail = user.getEmail().replace(".", ",");
    public ListofListsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListofListsAdapter(Context context, int resource, ArrayList<List> dList) {
        super(context, resource, dList);

        this.listarray =dList;
    }

    @Override
    public int getCount(){

        return this.listarray.size();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        Log.e("listoflist","listoflist");
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listlayout, null);
        }


        List p = this.listarray.get(position);


        if (p != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.imageView3);
            TextView name = (TextView) v.findViewById(R.id.textViewName);
            TextView description = (TextView) v.findViewById(R.id.textViewDescription);
            ProgressBar progress= (ProgressBar) v.findViewById(R.id.progressBar1);
            TextView pp= (TextView) v.findViewById(R.id.textView2);
            TextView a= (TextView) v.findViewById(R.id.textView4);
            ImageView delete= (ImageView) v.findViewById(R.id.delete);
            progress.setVisibility(View.VISIBLE);
            pp.setVisibility(View.VISIBLE);
            a.setVisibility(View.VISIBLE);

            if (progress !=null){
                int prog= p.getCompletionStatus();
            progress.setProgress(prog);
            a.setText(" "+Integer.toString(p.getCompletionStatus())+ "%");
            if(p.getCompletionStatus()==100){
                int color = 0xFF00FF00;
                progress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                progress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

            }
                mDb.child("Users").child(userEmail).child("lists").child(listarray.get(position).getListName()).child("completionStatus").setValue(prog);
            }
            final ListofListsAdapter ll= this;
            final int pos= position;
            delete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                mDb.child("Users").child(userEmail).child("lists").child(listarray.get(pos).getListName()).removeValue();
                                listarray.remove(pos);
                                ll.notifyDataSetChanged();
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
            if (p != null && p.getLocationArray().size() > 0) {
                img = p.getLocationArray().get(0).getImageUrl();

            }

            if (img != null && icon != null) {
                Log.d("lookformeyay", "in the right if sick bro");
                Glide.with(getContext()).load(img).into(icon);
            } else if (icon != null) {
                icon.setImageResource(R.drawable.ic_menu_gallery);
            }

            if (name != null) {
                name.setText(p.getListName());
            }

            if (description != null) {
                description.setText(p.getListDescription());
            }
        }

        return v;
    }
}
