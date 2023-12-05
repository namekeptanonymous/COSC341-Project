package com.namekept.cosc341project;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddPostFragment extends Fragment {

    private DatabaseReference root;
    private View fragmentView;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getContext(), "AIzaSyDUgyTf_nWM_3PkLaqbzya53FlN4UWBXAQ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_add_post, container, false);
        return fragmentView;
    }


    private double longitude; private double latitude;
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

        AutoCompleteTextView locationEditText = view.findViewById(R.id.location);
        locationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.clearFocus();
                    address();
                }

            }
        });

        Button submit = view.findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                RadioGroup type = view.findViewById(R.id.type);
                TextInputEditText contentField = view.findViewById(R.id.content);
                String content = contentField.getText().toString();
                AutoCompleteTextView titleField = view.findViewById(R.id.titleTextInputLayout);
                String title = titleField.getText().toString();

                if (type.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getContext(), "Please select an option.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (title.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a title.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (content.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (locationEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a location.", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selected = view.findViewById(type.getCheckedRadioButtonId());
                root.child(id+"").child("content").setValue(content);
                if (selected.getText().equals("Fire")) {
                    root.child(id+"").child("fire").setValue(true);
                }
                else {
                    root.child(id+"").child("type").setValue(false);
                }
                root.child(id+"").child("location").setValue(latitude + "," + longitude);
                root.child(id+"").child("timestamp").setValue(System.currentTimeMillis());
                root.child(id+"").child("title").setValue(title);
                root.child(id+"").child("verifications").setValue(0);

                Toast.makeText(getContext(), "The report was successfully added!", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    public void address() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("CA")
                .build(requireContext());
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                AutoCompleteTextView locationEditText = fragmentView.findViewById(R.id.location);
                locationEditText.setText(place.getAddress());
                latitude=place.getLatLng().latitude;
                longitude=place.getLatLng().longitude;

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}