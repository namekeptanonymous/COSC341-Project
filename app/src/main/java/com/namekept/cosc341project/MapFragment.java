package com.namekept.cosc341project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    private DatabaseReference root;
    private GoogleMap map;
    HashMap<String, Marker> markerHashMap = new HashMap<>();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
            LatLng ubco = new LatLng(49.9394, -119.3948);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubco));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }
    private String pinName; private String location;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        root = FirebaseDatabase.getInstance().getReference();
        FloatingActionButton button = view.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
//                builder.setTitle("Add a Pin/Report");
//
//                final EditText nameInput = new EditText(getContext());
//                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(nameInput);
//
//                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        pinName = nameInput.getText().toString();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//                root.child("56").child("Name").setValue(pinName);       // code to write to database
//                root.child("56").child("Location").setValue("49.9537,-119");

                 NavHostFragment.findNavController(mapFragment).navigate(R.id.action_navigation_maps_to_navigation_report);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String postName = dataSnapshot.child("Name").getValue(String.class);
                String coordsString = dataSnapshot.child("Location").getValue(String.class);
                if (coordsString==null) return;
                String[] coords = coordsString.split(",");
                LatLng postCoords = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                Marker addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(postName));
                markerHashMap.put(postName, addedMarker);
                Log.d("Firebase", "markerHashMap: " + markerHashMap.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", "am removinh!!!!!!!");
                String postName = snapshot.child("Name").getValue(String.class);
                Marker removedMarker = markerHashMap.get(postName);
                if (removedMarker!=null) removedMarker.remove();
                markerHashMap.remove(postName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
        });
    }
}