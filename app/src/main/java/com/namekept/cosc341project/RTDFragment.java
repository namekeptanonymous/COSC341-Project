package com.namekept.cosc341project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RTDFragment extends Fragment {

    ListView listView;
    ImageView imageView2;



    private DatabaseReference root;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_r_t_d, container, false);
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
}
