package com.namekept.cosc341project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapFragment extends Fragment {

    private DatabaseReference root;

    private GoogleMap map;
    private Marker selectedMarker;
    private View fragmentView;
    private String coordsString; private String type; private boolean fire; private String title;
    private String[] coord;
    private LatLng postCoords = new LatLng(0,0);
    private Marker addedMarker; private Marker removedMarker;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude; private double longitude;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            if (getArguments().get("coords") != null) {
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                String[] latlng = ((String) getArguments().get("coords")).split(",");
                LatLng pin = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pin));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(11));
                Log.d("test", latitude + "lat " + longitude + "long ");
                LatLng initialLocation = new LatLng(latitude,longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialLocation));
            }


            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker m) {
                    selectedMarker = m;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(m.getPosition()));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    FloatingActionButton viewPost = fragmentView.findViewById(R.id.viewButton);
                    delete.setVisibility(View.VISIBLE);
                    viewPost.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    selectedMarker = null;
                    FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                    FloatingActionButton viewPost = fragmentView.findViewById(R.id.viewButton);
                    delete.setVisibility(View.INVISIBLE);
                    viewPost.setVisibility(View.INVISIBLE);
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }

        return fragmentView;
    }

    private String postId;
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

        FloatingActionButton viewPost = view.findViewById(R.id.viewButton);
        viewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker != null) {

                    Iterator<Map.Entry<String, Marker>> iterator = markerHashMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Marker> entry = iterator.next();
                        String key = entry.getKey();
                        Marker value = entry.getValue();

                        if (value.getPosition().equals(selectedMarker.getPosition())) {
                            postId = key;
                            Bundle bundle = new Bundle();
                            bundle.putString("postId", postId);
                            NavHostFragment.findNavController(mapFragment).navigate(R.id.action_navigation_maps_to_viewPostFragment, bundle);
                            break;
                        }
                    }
                    }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                String addPostId = dataSnapshot.getKey();
                coordsString = dataSnapshot.child("location").getValue(String.class);
                DataSnapshot firesnapshot = dataSnapshot.child("fire");
                type = dataSnapshot.child("type").getValue(String.class);

                if (firesnapshot.getValue(Boolean.class) == null)
                    fire = false;
                else
                    fire = firesnapshot.getValue(Boolean.class);

                title = dataSnapshot.child("title").getValue(String.class);
                if (coordsString == null) return;
                coord = coordsString.split(",");
                postCoords = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));

                if (fire) {
                    Bitmap highResBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
                    int targetWidth = 90;
                    int targetHeight = 90;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, targetWidth, targetHeight, false);
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title).icon(markerIcon));
                } else if (type.equalsIgnoreCase("request")){
                    Bitmap highResBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.request);
                    int targetWidth = 90;
                    int targetHeight = 90;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, targetWidth, targetHeight, false);
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title).icon(markerIcon));

                } else if (type.equalsIgnoreCase("accommodation")){
                    Bitmap highResBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.accommodation);
                    int targetWidth = 90;
                    int targetHeight = 90;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, targetWidth, targetHeight, false);
                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title).icon(markerIcon));

                } else {
                    addedMarker = map.addMarker(new MarkerOptions().position(postCoords).title(title));
                }
                markerHashMap.put(addPostId, addedMarker);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                postId = snapshot.getKey();
                removedMarker = markerHashMap.get(postId);
                if (removedMarker != null) removedMarker.remove();
                markerHashMap.remove(postId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
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
                        if (selectedMarker != null) {
                            for (Map.Entry<String, Marker> entry : markerHashMap.entrySet()) {
                                if (entry.getValue().equals(selectedMarker)) {
                                    postId = entry.getKey();
                                    break;
                                }
                            }


                            if (postId != null) {
                                markerHashMap.remove(postId);
                                selectedMarker.remove();
                                root.child(postId).removeValue();
                            }
                            Toast.makeText(getContext(), "The selected marker has been removed.", Toast.LENGTH_SHORT).show();
                            FloatingActionButton delete = fragmentView.findViewById(R.id.deleteButton);
                            FloatingActionButton viewPost = fragmentView.findViewById(R.id.viewButton);
                            delete.setVisibility(View.INVISIBLE);
                            viewPost.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    });
        } else {
            latitude = 49.9394;
            longitude = -119.3948;
        }
    }
}
