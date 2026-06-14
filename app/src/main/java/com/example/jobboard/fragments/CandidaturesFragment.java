package com.example.jobboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.adapters.ApplicationAdapter;
import com.example.jobboard.database.DatabaseHelper;
import com.example.jobboard.models.Application;

import java.util.List;

public class CandidaturesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private TextView countTextView;
    private ApplicationAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_candidatures, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView    = view.findViewById(R.id.recycler_view_candidatures);
        emptyView       = view.findViewById(R.id.empty_view);
        countTextView   = view.findViewById(R.id.candidatures_count);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter(null);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Rechargé à chaque fois que l'onglet devient visible
        loadApplications();
    }

    private void loadApplications() {
        List<Application> applications = dbHelper.getAllApplications();
        adapter.setApplications(applications);

        int count = applications.size();
        countTextView.setText(count + " candidature" + (count > 1 ? "s" : ""));

        if (count == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}

