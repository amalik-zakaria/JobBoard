package com.example.jobboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.models.JobOffer;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<JobOffer> jobOffers;
    private OnJobClickListener onJobClickListener;

    public interface OnJobClickListener {
        void onJobClick(JobOffer jobOffer);
    }

    public JobAdapter(List<JobOffer> jobOffers, OnJobClickListener onJobClickListener) {
        this.jobOffers = jobOffers;
        this.onJobClickListener = onJobClickListener;
    }

    /**
     * Met à jour la liste des offres
     */
    public void setJobOffers(List<JobOffer> jobOffers) {
        this.jobOffers = jobOffers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_offer, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobOffer jobOffer = jobOffers.get(position);
        holder.bind(jobOffer, onJobClickListener);
    }

    @Override
    public int getItemCount() {
        return jobOffers != null ? jobOffers.size() : 0;
    }

    /**
     * ViewHolder pour une offre d'emploi
     */
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView companyTextView;
        private TextView locationTextView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.job_title);
            companyTextView = itemView.findViewById(R.id.job_company);
            locationTextView = itemView.findViewById(R.id.job_location);
        }

        public void bind(JobOffer jobOffer, OnJobClickListener listener) {
            titleTextView.setText(jobOffer.getTitle());
            companyTextView.setText("Entreprise: " + jobOffer.getCompany());
            locationTextView.setText("Localisation: " + jobOffer.getLocation());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(jobOffer);
                }
            });
        }
    }
}

