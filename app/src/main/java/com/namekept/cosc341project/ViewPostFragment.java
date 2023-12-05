package com.namekept.cosc341project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewPostFragment extends Fragment {

    private String postId;
    private View fragmentView;
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
                        HashMap<String, HashMap<String, ?>> databaseEntries = (HashMap) task.getResult().getValue();
                        if (databaseEntries==null)
                            return;
                        List<Map.Entry<String, HashMap<String, ?>>> indexableEntries = new ArrayList<>(databaseEntries.entrySet());

                    }
                }
            };
}