package com.namekept.cosc341project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.namekept.cosc341project.R;

import java.util.ArrayList;

public class RTDFragment extends Fragment {

    ListView listView;
    ImageView imageView2;

    private ArrayList<String> storages = new ArrayList<String>();
    private DatabaseReference root;

    public RTDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_r_t_d, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.lists);
        imageView2 = view.findViewById(R.id.imageView2);
        TextView textView10 = view.findViewById(R.id.textView10);
        TextView textView12 = view.findViewById(R.id.textView12);
        root = FirebaseDatabase.getInstance().getReference();

//_____________________________________________________________________________________________________________________________________________________________firebase


        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0; // Counter to limit to the first 5 posts
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (count < 5) {
                       //postId = postSnapshot.child("postid").getValue(Integer.class);
                       // long timestamp = postSnapshot.child("timestamp").getValue(long.class);
                        //String type = postSnapshot.child("type").getValue(String.class);
                        String content = postSnapshot.child("content").getValue(String.class);
                        //String location = postSnapshot.child("location").getValue(String.class);
                        //int verifications = postSnapshot.child("verifications").getValue(Integer.class);
                        String title = postSnapshot.child("title").getValue(String.class);
                        // Process the retrieved data as needed
                        // Create a Post object or perform any required operations with the extracted data


                        storages.add(title +"\n"+content);
                        count++; // Increment the counter
                    } else {
                        break; // Break the loop after 5 posts
                    }
                }
                // Do something with the first 5 posts' data
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, storages);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });


//__________________________________________________________________________________________________________________________________________________


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
//--------------------------------------------------------------------------------------------------

        AdapterView.OnItemClickListener clickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
            }
        };
        listView.setOnItemClickListener(clickedHandler);




    }
}
