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
import com.example.jobboard.models.Post;

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

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapter(jobOffers, jobOffer -> {
            // Gérer le clic sur une offre
            Toast.makeText(getContext(), "Offre: " + jobOffer.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(jobAdapter);

        // Charger les offres depuis l'API
        fetchJobOffers();
    }

    private void fetchJobOffers() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        JobApiService apiService = RetrofitClient.getApiService();
        Call<List<Post>> call = apiService.getJobOffers();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    jobOffers.clear();

                    // Convertir les Posts de l'API en JobOffer
                    List<Post> posts = response.body();
                    for (int i = 0; i < Math.min(10, posts.size()); i++) {
                        Post post = posts.get(i);

                        // Créer une JobOffer à partir du Post
                        JobOffer jobOffer = new JobOffer();
                        jobOffer.setId(post.getId());
                        jobOffer.setTitle(post.getTitle());
                        jobOffer.setCompany("Tech Company " + post.getUserId());
                        jobOffer.setLocation("Ville " + (post.getId() % 5 + 1));

                        jobOffers.add(jobOffer);
                    }

                    jobAdapter.setJobOffers(jobOffers);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    showError("Erreur: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Erreur réseau: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

