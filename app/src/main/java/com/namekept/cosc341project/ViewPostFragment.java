package com.namekept.cosc341project;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class ViewPostFragment extends Fragment {

    private DatabaseReference root;
    private String postId;
    private String coords;
    private View fragmentView;
    private String[] coord;
    private Date date;
    private SimpleDateFormat sdf;
    private long unixTime;

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
        Log.d("postId",postId);
        TextView titleText = view.findViewById(R.id.screenTitle);
        TextView locText = view.findViewById(R.id.location);
        TextView typeText = view.findViewById(R.id.postType);
        TextView contentText = view.findViewById(R.id.content);
        TextView verificationText = view.findViewById(R.id.postVerifications);
        TextView timestampText = view.findViewById(R.id.postTimestamp);

        Button done = view.findViewById(R.id.backButton1);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                NavController navController = NavHostFragment.findNavController(getParentFragment());
                navController.popBackStack();
            }
        });
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    titleText.setText(dataSnapshot.child("title").getValue(String.class));
                    coords = dataSnapshot.child("location").getValue(String.class);
                    coord = coords.split(",");
                    locText.setText(getCompleteAddressString(Double.parseDouble(coord[0]), Double.parseDouble(coord[1])));
                    typeText.setText(dataSnapshot.child("type").getValue(String.class).toUpperCase());
                    contentText.setText(dataSnapshot.child("content").getValue(String.class));
                    verificationText.setText(dataSnapshot.child("verifications").getValue(Integer.class) + "");

                    unixTime = dataSnapshot.child("timestamp").getValue(Long.class);
                    date = new Date(unixTime);
                    sdf = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getDefault());
                    timestampText.setText(sdf.format(date));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential error while reading data
            }
        });

        locText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                bundle.putString("coords", coords);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_viewPostFragment_to_navigation_maps, bundle);
            }
        });
    }

    private String strAdd;
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;

    }
}