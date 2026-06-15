package com.example.jobboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.adapters.JobAdapter;
import com.example.jobboard.api.JobApiService;
import com.example.jobboard.api.RetrofitClient;
import com.example.jobboard.models.JobOffer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OffresFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private JobAdapter jobAdapter;
    private List<JobOffer> jobOffers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offres, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_offers);
        progressBar = view.findViewById(R.id.progress_bar);
        errorTextView = view.findViewById(R.id.error_text_view);

        jobOffers = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapter(jobOffers, jobOffer ->
                Toast.makeText(getContext(), jobOffer.getTitle(), Toast.LENGTH_SHORT).show());
        recyclerView.setAdapter(jobAdapter);

        fetchJobOffers();
    }

    // ── Appel Retrofit ───────────────────────────────────────────────────────
    private void fetchJobOffers() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        JobApiService apiService = RetrofitClient.getApiService();
        Call<List<JobOffer>> call = apiService.getJobOffers();

        call.enqueue(new Callback<List<JobOffer>>() {
            @Override
            public void onResponse(Call<List<JobOffer>> call, Response<List<JobOffer>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    updateList(response.body());
                } else {
                    // Fallback sur le JSON local si l'API répond mais est vide/invalide
                    loadFromAssets();
                }
            }

            @Override
            public void onFailure(Call<List<JobOffer>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                // Fallback sur le JSON local en cas d'erreur réseau
                loadFromAssets();
            }
        });
    }

    // ── Fallback : lire job_offers.json depuis les assets ────────────────────
    private void loadFromAssets() {
        try {
            InputStream is = requireContext().getAssets().open("job_offers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Type listType = new TypeToken<List<JobOffer>>() {}.getType();
            List<JobOffer> localOffers = new Gson().fromJson(json, listType);
            updateList(localOffers);
        } catch (IOException e) {
            showError("Impossible de charger les offres.");
        }
    }

    // ── Mise à jour du RecyclerView ──────────────────────────────────────────
    private void updateList(List<JobOffer> offers) {
        jobOffers.clear();
        jobOffers.addAll(offers);
        jobAdapter.setJobOffers(jobOffers);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
