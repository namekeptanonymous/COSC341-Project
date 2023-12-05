package com.namekept.cosc341project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;


public class MapFragment extends Fragment {

    private DatabaseReference root;

    private GoogleMap map;
    private Marker selectedMarker;
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
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    selectedMarker = m;
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    FloatingActionButton edit = fragmentView.findViewById(R.id.editButton);
                    delete.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    selectedMarker = null;
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    FloatingActionButton edit = fragmentView.findViewById(R.id.editButton);
                    delete.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.INVISIBLE);
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

        FloatingActionButton add = view.findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 NavHostFragment.findNavController(mapFragment).navigate(R.id.action_navigation_maps_to_addPostFragment);
            }
        });

        FloatingActionButton edit = view.findViewById(R.id.editButton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postId = null;
                if (selectedMarker!=null) {
                    for (Map.Entry<String, Marker> entry : markerHashMap.entrySet()) {
                        if (entry.getValue().equals(selectedMarker)) {
                            postId = entry.getKey();
                            break;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("postId", postId);
                    NavHostFragment.findNavController(mapFragment).navigate(R.id.action_navigation_maps_to_viewPostFragment, bundle);
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String postId = dataSnapshot.getKey();
                String coordsString = dataSnapshot.child("location").getValue(String.class);
                DataSnapshot fireSnapshot = dataSnapshot.child("fire");
                Boolean fire;
                if (fireSnapshot==null)
                    fire = false;
                else
                    fire = fireSnapshot.getValue(Boolean.class);
                String title = dataSnapshot.child("title").getValue(String.class);
                if (coordsString==null) return;
                String[] coords = coordsString.split(",");
                LatLng postCoords = new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                Marker addedMarker;

                if (fire != null && fire) {
                    Bitmap highResBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
                    int targetWidth = 100;
                    int targetHeight = 100;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, targetWidth, targetHeight, false);
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title).icon(markerIcon));
                } else
                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title));
                markerHashMap.put(postId, addedMarker);
                Log.d("Firebase", "markerHashMap: " + markerHashMap.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", "am removinh!!!!!!!");
                String postId = snapshot.getKey();
                Marker removedMarker = markerHashMap.get(postId);
                if (removedMarker!=null) removedMarker.remove();
                markerHashMap.remove(postId);
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

        FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Deleting Pin and Post");
                builder.setMessage("Are you sure you want to delete this pin? Deleting this will also delete the post associated with it.");

                // Add buttons for positive (confirm) and negative (cancel) actions
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String postId = null;
                        if (selectedMarker!=null) {
                            for (Map.Entry<String, Marker> entry : markerHashMap.entrySet()) {
                                if (entry.getValue().equals(selectedMarker)) {
                                    postId = entry.getKey();
                                    break;
                                }
                            }

                            markerHashMap.remove(postId);
                            root.child(postId).removeValue();
                            selectedMarker.remove();
                            Toast.makeText(getContext(), "The selected marker has been removed.", Toast.LENGTH_SHORT).show();
                            FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                            FloatingActionButton edit = fragmentView.findViewById(R.id.editButton);
                            delete.setVisibility(View.INVISIBLE);
                            edit.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
