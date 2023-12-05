package com.namekept.cosc341project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewPostFragment extends Fragment {

    private DatabaseReference root;
    private String postId;
    private View fragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_view_post, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postId = getArguments().getString("postId");
        root = FirebaseDatabase.getInstance().getReference().child(postId);
        TextView titleText = view.findViewById(R.id.screenTitle);
        TextView locText = view.findViewById(R.id.location);
        TextView typeText = view.findViewById(R.id.postType);
        TextView contentText = view.findViewById(R.id.content);

        Button done = view.findViewById(R.id.backButton1);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    titleText.setText(dataSnapshot.child("title").getValue(String.class));
                    locText.setText(dataSnapshot.child("location").getValue(String.class));
                    typeText.setText(dataSnapshot.child("type").getValue(String.class).toUpperCase());
                    contentText.setText(dataSnapshot.child("content").getValue(String.class));

                    Log.d("test",dataSnapshot.child("content").getValue(String.class));

                } else {
                    // Data doesn't exist at this specific location
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential error while reading data
            }
        });
    }
}