package com.namekept.cosc341project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.core.graphics.drawable.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class MapFragment extends Fragment {

    private DatabaseReference root;

    private GoogleMap map;
    private View fragmentView;
    HashMap<String, Marker> markerHashMap = new HashMap<>();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
            LatLng ubco = new LatLng(49.9394, -119.3948);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubco));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker m) {
                    Log.d("test", "clicked a merker!");
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    delete.setVisibility(View.VISIBLE);

                    // TODO: PROMPT USER TO DELETE, ARE YOU SURE? DELETE ONLY IF USER ID IS EQUAL TO CURRENT ID.

                    return false;
                }
            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    delete.setVisibility(View.INVISIBLE);
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        return fragmentView;
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

        FloatingActionButton button = view.findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 NavHostFragment.findNavController(mapFragment).navigate(R.id.action_navigation_maps_to_addPostFragment);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String postId = dataSnapshot.getKey();
                String coordsString = dataSnapshot.child("location").getValue(String.class);
                String type = dataSnapshot.child("type").getValue(String.class);
                DataSnapshot verifications = dataSnapshot.child("verifications");
                if (coordsString==null) return;
                String[] coords = coordsString.split(",");
                LatLng postCoords = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                Marker addedMarker;

                if (type != null && type.equals("fire")) {
                    Bitmap highResBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
                    /* Your high-resolution bitmap */;
                    int targetWidth = 100; // Define your target width in pixels
                    int targetHeight = 100; // Define your target height in pixels
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, targetWidth, targetHeight, false);
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title("Fire").icon(markerIcon));
                } else if (verifications.getValue() == null || verifications.getValue(Integer.class) < 0)
                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title("Request"));
                 else
                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title("Post"));
                markerHashMap.put(postId, addedMarker);
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