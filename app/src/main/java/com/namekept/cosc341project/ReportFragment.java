package com.namekept.cosc341project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends Fragment {

    private DatabaseReference root;
    public ReportFragment() {
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
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference();
        root.get().addOnCompleteListener(onValuesFetched);
//        root.child(stdNo).child("lastName").setValue(lastName);       // code to write to database
//        root.child(stdNo).child("firstName").setValue(firstName);
//        root.child(stdNo).child("gender").setValue(gender);
//        root.child(stdNo).child("division").setValue(division);
    }

    private OnCompleteListener<DataSnapshot> onValuesFetched = new
            OnCompleteListener<DataSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    if (!task.isSuccessful())
                    {
                        Log.e("firebase", "Error getting data.", task.getException());
                    }
                    else
                    {
                        HashMap<String, HashMap<String, String>> databaseEntries = (HashMap) task.getResult().getValue();
                        if (databaseEntries==null)
                            return;
                        List<Map.Entry<String, HashMap<String, String>>> indexableEntries = new ArrayList<>(databaseEntries.entrySet());
                        Log.d("test", indexableEntries.toString());
                    }
                }
            };
}