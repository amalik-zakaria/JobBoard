package com.example.jobboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.DetailActivity;
import com.example.jobboard.R;
import com.example.jobboard.models.JobOffer;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<JobOffer> jobOffers;
    private OnJobClickListener onJobClickListener;

    public interface OnJobClickListener {
        void onJobClick(JobOffer jobOffer);
    }

    public JobAdapter(List<JobOffer> jobOffers, OnJobClickListener listener) {
        this.jobOffers = jobOffers;
        this.onJobClickListener = listener;
    }

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
        holder.bind(jobOffers.get(position), onJobClickListener);
    }

    @Override
    public int getItemCount() {
        return jobOffers != null ? jobOffers.size() : 0;
    }

    // ── ViewHolder ───────────────────────────────────────────────────────────
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView companyTextView;
        private final TextView locationTextView;
        private final TextView descriptionPreview;
        private final Button   detailsButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView      = itemView.findViewById(R.id.job_title);
            companyTextView    = itemView.findViewById(R.id.job_company);
            locationTextView   = itemView.findViewById(R.id.job_location);
            descriptionPreview = itemView.findViewById(R.id.job_description_preview);
            detailsButton      = itemView.findViewById(R.id.details_button);
        }

        public void bind(JobOffer jobOffer, OnJobClickListener listener) {
            titleTextView.setText(jobOffer.getTitle());
            companyTextView.setText("🏢 " + jobOffer.getCompany());
            locationTextView.setText("📍 " + jobOffer.getLocation());

            String desc = jobOffer.getDescription();
            if (desc != null && !desc.isEmpty()) {
                descriptionPreview.setText(desc);
                descriptionPreview.setVisibility(View.VISIBLE);
            } else {
                descriptionPreview.setVisibility(View.GONE);
            }

            detailsButton.setOnClickListener(v ->
                    openDetail(itemView.getContext(), jobOffer, listener));
            itemView.setOnClickListener(v ->
                    openDetail(itemView.getContext(), jobOffer, listener));
        }

        private void openDetail(Context context, JobOffer jobOffer, OnJobClickListener listener) {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("job_id",          jobOffer.getId());
            intent.putExtra("job_title",       jobOffer.getTitle());
            intent.putExtra("job_company",     jobOffer.getCompany());
            intent.putExtra("job_location",    jobOffer.getLocation());
            intent.putExtra("job_description", jobOffer.getDescription() != null
                                               ? jobOffer.getDescription() : "");
            context.startActivity(intent);
            if (listener != null) listener.onJobClick(jobOffer);
        }
    }
}
