package com.namekept.cosc341project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class ReportFragment extends Fragment implements ReportAdapter.OnReportListener {

    private RecyclerView recyclerView;
    private View fragmentView;
    private ReportAdapter adapter;
    private DatabaseReference databaseReference;
    private List<Report> reportList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("reports");
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

        // Initialize your RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_reports);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize your adapter with the report list and set it to the RecyclerView
        adapter = new ReportAdapter(reportList, this);
        recyclerView.setAdapter(adapter);

        // Load reports from Firebase
        loadReportsFromFirebase();

        // Initialize the FAB for adding new reports and set its click listener
        FloatingActionButton fabAddReport = view.findViewById(R.id.fab_add_report);
        fabAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the fragment where you can add a new report
                // Make sure you have the correct action ID here to navigate to the AddReportFragment
                NavHostFragment.findNavController(getParentFragment())
                        .navigate(R.id.action_navigation_report_to_addPostFragment);
            }
        });
    }

    private void loadReportsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportList.clear(); // Clear the existing list before adding new items
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class);
                    if (report != null) {
                        reportList.add(report); // Add the fetched report to the list
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ReportFragment", "loadReportsFromFirebase:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public void onReportClicked(int position) {
        Report report = reportList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("coords", report.getCoordinates()); // Assuming Report has coordinates
        // Replace R.id.action_showMap with the correct action ID to navigate
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_rtd_to_navigation_maps, bundle);
    }


    @Override
    public void onMoreOptionsClicked(int position) {
        PopupMenu popup = new PopupMenu(getContext(), recyclerView.findViewHolderForAdapterPosition(position).itemView);
        popup.inflate(R.menu.report_options_menu); // Your menu resource
        popup.setOnMenuItemClickListener(item -> {
            Report selectedReport = reportList.get(position);
            switch (item.getItemId()) {
                case R.id.edit:
                    // Navigate to the edit fragment, passing the selected report's data
                    Bundle editBundle = new Bundle();
                    editBundle.putString("reportId", selectedReport.getId());
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_navigation_report_to_navigation_maps, editBundle);

                    return true;
                case R.id.share:
                    // Share report content via an intent
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, selectedReport.getDescription());
                    startActivity(Intent.createChooser(shareIntent, "Share Report Using"));
                    return true;
                case R.id.delete:
                    // Delete the report from Firebase
                    databaseReference.child(selectedReport.getId()).removeValue();
                    reportList.remove(position); // Remove from the local list
                    adapter.notifyItemRemoved(position); // Notify the adapter
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }


}