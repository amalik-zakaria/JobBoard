package com.example.jobboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.adapters.ApplicationAdapter;
import com.example.jobboard.database.DatabaseHelper;
import com.example.jobboard.models.Application;

import java.util.List;

public class CandidaturesFragment extends Fragment {

    private RecyclerView         recyclerView;
    private LinearLayout         emptyView;
    private TextView             countTextView;
    private ApplicationAdapter   adapter;
    private DatabaseHelper       dbHelper;
    private List<Application>    applicationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_candidatures, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView  = view.findViewById(R.id.recycler_view_candidatures);
        emptyView     = view.findViewById(R.id.empty_view);
        countTextView = view.findViewById(R.id.candidatures_count);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialiser l'adapter avec le listener de suppression
        adapter = new ApplicationAdapter(null, (application, position) ->
                deleteApplication(application, position));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadApplications();
    }

    // ── Charger les candidatures depuis SQLite ────────────────────────────────
    private void loadApplications() {
        applicationList = dbHelper.getAllApplications();
        adapter.setApplications(applicationList);
        refreshUI();
    }

    // ── Supprimer une candidature ─────────────────────────────────────────────
    private void deleteApplication(Application application, int position) {
        int deleted = dbHelper.deleteApplication(application.getId());

        if (deleted > 0) {
            // Retirer de la liste et animer la suppression dans le RecyclerView
            adapter.removeItem(position);
            refreshUI();
            Toast.makeText(requireContext(),
                    "\"" + application.getJobTitle() + "\" supprimée.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    "Erreur lors de la suppression.", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Mettre à jour compteur + vue vide ─────────────────────────────────────
    private void refreshUI() {
        int count = adapter.getItemCount();
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
