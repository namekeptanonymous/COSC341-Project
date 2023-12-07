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

    private ListView listView;
    private View fragmentView;
    private ArrayAdapter<Report> adapter;
    private DatabaseReference root;
    private List<Report> reportList;
    private String postId;
    private Long timestamp;
    private String type;
    private String title;
    private String content;
    private String location;
    private int verifications;

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
        ArrayList<String> reqListTitle = new ArrayList<>();
        ArrayList<String> reqListContent = new ArrayList<>();
        ArrayList<String> reqIdList = new ArrayList<>();

        listView = view.findViewById(R.id.Reports);

        // Setup Firebase ValueEventListener
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
                        reqListTitle.add(title);
                        reqListContent.add(content);
                        reqIdList.add(postId);
                    }
                }

                // Setup ArrayAdapter with simple_list_item_2 layout
                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, reqListTitle) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(reqListTitle.get(position));
                        text2.setText(reqListContent.get(position));
                        return view;
                    }
                };
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });


//        ImageView imageViewShare = view.findViewById(R.id.share);
//        imageViewShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        FloatingActionButton addButton = view.findViewById(R.id.fab_add_report);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_maps_to_addPostFragment);
            }
        });

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("test",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class); // Assuming Report is a model class with appropriate fields
                    reportList.add(report);
                    postId = postSnapshot.getKey();
                    timestamp = postSnapshot.child("timestamp").getValue(Long.class);
                    type = postSnapshot.child("type").getValue(String.class);
                    title = postSnapshot.child("title").getValue(String.class);
                    content = postSnapshot.child("content").getValue(String.class);
                    location = postSnapshot.child("location").getValue(String.class);
                    verifications = postSnapshot.child("verifications").getValue(Integer.class);
                    if (type.equalsIgnoreCase("report")){
                        reqListTitle.add(title);
                        reqListContent.add(content);
                        reqIdList.add(postId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());

            }
        });
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, reqListTitle) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(reqListTitle.get(position));
                text2.setText(reqListContent.get(position));
                return view;
            }
        };
        listView.setAdapter(adapter);

        // handle click
        AdapterView.OnItemClickListener reqClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                String postId = reqIdList.get(position);
//                Bundle bundle = new Bundle();
//                bundle.putString("postId", postId);
//                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_navigation_community_to_viewPostFragment, bundle);
            }
        };
        listView.setOnItemClickListener(reqClickedHandler);
    }
    private void showPopupMenu(View view, Report report) {
//        PopupMenu popup = new PopupMenu(getContext(), view);
//        popup.setOnMenuItemClickListener(item -> {
//            Log.d("test","in da menu");
//            switch (item.getItemId()) {
//                case R.id.edit:
//                    // Navigate to the edit fragment, passing the selected report's data
//                    Bundle editBundle = new Bundle();
//                    editBundle.putString("reportId", report.getId());
//                    NavHostFragment.findNavController(this)
//                            .navigate(R.id.action_navigation_report_to_navigation_maps, editBundle);
//                    return true;
//                case R.id.share:
//                    // Share report content via an intent
//                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, report.getDescription());
//                    startActivity(Intent.createChooser(shareIntent, "Share Report Using"));
//                    return true;
//                case R.id.delete:
//                    // Delete the report from Firebase
//                    databaseReference.child(report.getId()).removeValue();
//                    reportList.remove(report);
//                    adapter.notifyDataSetChanged();
//                    return true;
//                default:
//                    return false;
//            }
//        });
//        popup.show();
    }
}