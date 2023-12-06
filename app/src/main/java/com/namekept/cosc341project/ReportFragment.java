package com.namekept.cosc341project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

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

        listView = view.findViewById(R.id.list_view_reports);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, reportList);
        listView.setAdapter(adapter);

        ImageView imageViewShare = view.findViewById(R.id.share);
        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    Long timestamp = postSnapshot.child("timestamp").getValue(Long.class);
                    String type = postSnapshot.child("type").getValue(String.class);
                    String title = postSnapshot.child("title").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String location = postSnapshot.child("location").getValue(String.class);
                    int verifications = postSnapshot.child("verifications").getValue(Integer.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
