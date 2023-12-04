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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddPostFragment extends Fragment {

    private DatabaseReference root;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        Button cancel = view.findViewById(R.id.cancelButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });

        Query lastElementQuery = root.orderByKey().limitToLast(1);
        lastElementQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        id = Integer.parseInt(key) + 1;
                    }
                } else id = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });

        Button locationButton = view.findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                // TODO: Figure location button out.

            }
        });

        Button submit = view.findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                RadioGroup type = view.findViewById(R.id.type);
                TextInputEditText contentField = view.findViewById(R.id.content);
                String content = contentField.getText().toString();
                // TODO: add location here once figured out
                if (type.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (content.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                    return;
                } // TODO: another else if to check if location was given
                RadioButton selected = view.findViewById(type.getCheckedRadioButtonId());
                root.child(id+"").child("timestamp").setValue(1);           // TODO: Set to current time and date, preferably time-zone independent.
                if (selected.getText().equals("Fire")) {
                    root.child(id+"").child("type").setValue("fire");
                }
                else {
                    root.child(id+"").child("type").setValue("");
                }                                                                              // TODO: Make it so that requests have a verification value of -1. (not possible value)
                root.child(id+"").child("user").setValue(0);                // Placeholder since we don't have a user system ready.
                root.child(id+"").child("content").setValue(content);
                root.child(id+"").child("location").setValue("140,140");    // TODO: change when actual location is ready
                root.child(id+"").child("verifications").setValue(0);
                Toast.makeText(getContext(), "The report was successfully added! Please refresh the map to see it.", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });



    }
}