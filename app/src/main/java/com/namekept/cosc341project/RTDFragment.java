package com.namekept.cosc341project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RTDFragment extends Fragment {

    ListView listView;
    ImageView imageView2;
    View fragmentView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private DatabaseReference root;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_r_t_d, container, false);

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

    private String content; private String title; private String postId;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.lists);
        imageView2 = view.findViewById(R.id.imageView2);
        TextView textView10 = view.findViewById(R.id.textView10);
        TextView textView12 = view.findViewById(R.id.textView12);

        root = FirebaseDatabase.getInstance().getReference();

        ArrayList<String>  titleList = new ArrayList<String>();
        ArrayList<String>  contentList = new ArrayList<String>();
        ArrayList<String>  idList = new ArrayList<String>();

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0; // Counter to limit to the first 5 posts
                ArrayList<String> combinedItems = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (count < 10) { //limit output
                        content = postSnapshot.child("content").getValue(String.class);
                        title = postSnapshot.child("title").getValue(String.class);
                        postId = postSnapshot.getKey();

                        titleList.add(title);
                        contentList.add(content);
                        idList.add(postId);

                        count++;
                    } else {
                        break;
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, titleList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(titleList.get(position));
                        text2.setText(contentList.get(position));
                        return view;
                    }
                };

                listView.setAdapter(adapter);

                AdapterView.OnItemClickListener clickedHandler = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        String postId = idList.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId", postId);
                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_rtd_to_viewPostFragment, bundle);
                    }
                };
                listView.setOnItemClickListener(clickedHandler);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the browser to the specified URL
                String url = "https://weather.gc.ca/city/pages/bc-48_metric_e.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        textView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the browser to the specified URL for air quality
                String url = "https://weather.gc.ca/airquality/pages/bcaq-008_e.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        textView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the browser to the specified URL for air quality
                String url = "https://cwfis.cfs.nrcan.gc.ca/report";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
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
                                getAddressFromLocation(getContext(), location.getLatitude(), location.getLongitude());
                            } else {
                                TextView locationField = fragmentView.findViewById(R.id.textView21);
                                TextView locationData1 = fragmentView.findViewById(R.id.textView22);
                                TextView locationData2 = fragmentView.findViewById(R.id.textView23);
                                TextView locationData3 = fragmentView.findViewById(R.id.textView20);
                                locationField.setText("Location is off.");
                                locationData1.setText("");
                                locationData2.setText("");
                                locationData3.setText("");

                            }
                        }
                    });
        }
    }

    private void getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Get address details
                String city = address.getLocality();
                String state = getStateCode(address.getAdminArea());
                // Construct the full address
                String location = city + ", " + state;

                TextView locationField = fragmentView.findViewById(R.id.textView21);
                locationField.setText(location);
            } else {
                Toast.makeText(context, "No address found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStateCode(String fullStateName) {
        // Method to convert full state name to state code (abbreviated form)
        switch (fullStateName) {
            // Canadian provinces
            case "Alberta":
                return "AB";
            case "British Columbia":
                return "BC";
            case "Manitoba":
                return "MB";
            case "New Brunswick":
                return "NB";
            case "Newfoundland and Labrador":
                return "NL";
            case "Nova Scotia":
                return "NS";
            case "Ontario":
                return "ON";
            case "Prince Edward Island":
                return "PE";
            case "Quebec":
                return "QC";
            case "Saskatchewan":
                return "SK";
            case "Northwest Territories":
                return "NT";
            case "Nunavut":
                return "NU";
            case "Yukon":
                return "YT";
            // United States
            case "Alabama":
                return "AL";
            case "Alaska":
                return "AK";
            case "Arizona":
                return "AZ";
            case "Arkansas":
                return "AR";
            case "California":
                return "CA";
            case "Colorado":
                return "CO";
            case "Connecticut":
                return "CT";
            case "Delaware":
                return "DE";
            case "Florida":
                return "FL";
            case "Georgia":
                return "GA";
            case "Hawaii":
                return "HI";
            case "Idaho":
                return "ID";
            case "Illinois":
                return "IL";
            case "Indiana":
                return "IN";
            case "Iowa":
                return "IA";
            case "Kansas":
                return "KS";
            case "Kentucky":
                return "KY";
            case "Louisiana":
                return "LA";
            case "Maine":
                return "ME";
            case "Maryland":
                return "MD";
            case "Massachusetts":
                return "MA";
            case "Michigan":
                return "MI";
            case "Minnesota":
                return "MN";
            case "Mississippi":
                return "MS";
            case "Missouri":
                return "MO";
            case "Montana":
                return "MT";
            case "Nebraska":
                return "NE";
            case "Nevada":
                return "NV";
            case "New Hampshire":
                return "NH";
            case "New Jersey":
                return "NJ";
            case "New Mexico":
                return "NM";
            case "New York":
                return "NY";
            case "North Carolina":
                return "NC";
            case "North Dakota":
                return "ND";
            case "Ohio":
                return "OH";
            case "Oklahoma":
                return "OK";
            case "Oregon":
                return "OR";
            case "Pennsylvania":
                return "PA";
            case "Rhode Island":
                return "RI";
            case "South Carolina":
                return "SC";
            case "South Dakota":
                return "SD";
            case "Tennessee":
                return "TN";
            case "Texas":
                return "TX";
            case "Utah":
                return "UT";
            case "Vermont":
                return "VT";
            case "Virginia":
                return "VA";
            case "Washington":
                return "WA";
            case "West Virginia":
                return "WV";
            case "Wisconsin":
                return "WI";
            case "Wyoming":
                return "WY";
            default:
                return ""; // Return empty string or handle unknown states as needed
        }

    }
}
