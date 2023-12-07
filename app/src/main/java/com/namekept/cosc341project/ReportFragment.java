package com.namekept.cosc341project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportFragment extends Fragment {

    private View fragmentView;
    private DatabaseReference root;
    private List<Report> reportList;
    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        reportList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_report, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> contentList = new ArrayList<>();
        ArrayList<String> idList = new ArrayList<>();

        ListView listView = view.findViewById(R.id.Reports);

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Extract data from snapshot
                    type = postSnapshot.child("type").getValue(String.class);
                    String title = postSnapshot.child("title").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String postId = postSnapshot.getKey();

                    if (type.equals("report")) {
                        titleList.add(title);
                        contentList.add(content);
                        idList.add(postId);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, idList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(titleList.get(position));
                        text2.setText(contentList.get(position));
                        return view;
                    }
                };

                listView.setAdapter(adapter);
                AdapterView.OnItemClickListener reqClickedHandler = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        String postId = idList.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("postId", postId);
                        NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_report_to_viewPostFragment, bundle);
                    }
                };
                listView.setOnItemClickListener(reqClickedHandler);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }

        });

        FloatingActionButton addButton = view.findViewById(R.id.fab_add_report);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_report_to_addPostFragment);
            }
        });
    }
}