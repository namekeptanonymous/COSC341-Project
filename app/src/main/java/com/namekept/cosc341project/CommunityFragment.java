package com.namekept.cosc341project;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.namekept.cosc341project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommunityFragment extends Fragment {
    private DatabaseReference root;
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_community, container, false);
        return fragmentView;
    }
    private long timestamp; private String location; private String postId;
    private String type; private String content; private int verifications;
    private String title;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> accListTitle = new ArrayList<String>();
        ArrayList<String> accListContent = new ArrayList<String>();
        ArrayList<String> reqListTitle = new ArrayList<String>();
        ArrayList<String> reqListContent = new ArrayList<String>();
        root = FirebaseDatabase.getInstance().getReference();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0; // Counter to limit to the first 5 posts
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postId = postSnapshot.getKey();
                        timestamp = postSnapshot.child("timestamp").getValue(Long.class);
                        type = postSnapshot.child("type").getValue(String.class);
                        title = postSnapshot.child("title").getValue(String.class);
                        content = postSnapshot.child("content").getValue(String.class);
                        location = postSnapshot.child("location").getValue(String.class);
                        verifications = postSnapshot.child("verifications").getValue(Integer.class);

                        if(type.equalsIgnoreCase("accommodation")){
                            accListTitle.add(title);
                            accListContent.add(content);
                        }
                        else if (type.equalsIgnoreCase("request")){
                            reqListTitle.add(title);
                            reqListContent.add(content);
                        }
                }

                ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, accListTitle) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(accListTitle.get(position));
                        text2.setText(accListContent.get(position));
                        return view;
                    }
                };
                ListView accList = view.findViewById(R.id.accomodations);
                accList.setAdapter(adapter);

                ArrayAdapter adapter1 = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, reqListTitle) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(reqListTitle.get(position));
                        text2.setText(reqListContent.get(position));
                        return view;
                    }
                };
                ListView reqList = view.findViewById(R.id.requests);
                reqList.setAdapter(adapter1);

                // Create a message handling object as an anonymous class.
                AdapterView.OnItemClickListener clickedHandler = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                        String selected = parent.getItemAtPosition(position).toString();

                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setMessage(selected);
                        builder.setTitle(selected);
                        builder.show();

                    }
                };
                accList.setOnItemClickListener(clickedHandler);
                reqList.setOnItemClickListener(clickedHandler);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        FloatingActionButton button = view.findViewById(R.id.addPostButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_community_to_addPostFragment);
            }
        });

    }
}