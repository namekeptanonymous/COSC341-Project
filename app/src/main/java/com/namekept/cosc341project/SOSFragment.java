package com.namekept.cosc341project;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SOSFragment extends Fragment {

    ConstraintLayout constraintLayout;
    Button button;
    TextView textView3;
    TextView textView5;
    int batteryLevel;
    View fragmentView;
    double latitude;
    double longitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_s_o_s, container, false);

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout = view.findViewById(R.id.layout);

        button = view.findViewById(R.id.button2);

        textView3 = view.findViewById(R.id.textView3);

        textView5 = view.findViewById(R.id.textView5);


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;
        textView3.setText((int) batteryPct + "%");
        textView3.setVisibility(View.VISIBLE);
        textView5.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a ValueAnimator to animate the background color change
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                        getResources().getColor(android.R.color.transparent),
                        getResources().getColor(android.R.color.holo_green_light)
                );
                colorAnimation.setDuration(1200);

                TextView textView = view.findViewById(R.id.textView);
                textView.setText("Data sent.");
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });

                // Start the color animation
                colorAnimation.start();
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
                                TextView latText = fragmentView.findViewById(R.id.lat);
                                TextView longText = fragmentView.findViewById(R.id.longitude);
                                latText.setText(latitude+"");
                                longText.setText(longitude+"");
                                getAddressFromLocation(getContext(), latitude, longitude);
                            } else {
                                textView5 = fragmentView.findViewById(R.id.textView5);
                                textView5.setText("Location is turned off.");
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

                // Use the fullAddress as needed
                textView5 = fragmentView.findViewById(R.id.textView5);
                textView5.setText(address.getAddressLine(0));
            } else {
                // Handle no address found
                Toast.makeText(context, "No address found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}