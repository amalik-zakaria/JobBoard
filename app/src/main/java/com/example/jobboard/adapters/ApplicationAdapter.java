package com.example.jobboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.models.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applications;

    public ApplicationAdapter(List<Application> applications) {
        this.applications = applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        holder.bind(applications.get(position));
    }

    @Override
    public int getItemCount() {
        return applications != null ? applications.size() : 0;
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView companyTextView;
        private final TextView locationTextView;
        private final TextView dateTextView;
        private final TextView statusTextView;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView    = itemView.findViewById(R.id.application_title);
            companyTextView  = itemView.findViewById(R.id.application_company);
            locationTextView = itemView.findViewById(R.id.application_location);
            dateTextView     = itemView.findViewById(R.id.application_date);
            statusTextView   = itemView.findViewById(R.id.application_status);
        }

        public void bind(Application app) {
            titleTextView.setText(app.getJobTitle());
            companyTextView.setText("🏢 " + app.getCompany());
            locationTextView.setText("📍 " + app.getLocation());
            dateTextView.setText("📅 Postulé le : " + app.getDateApplied());
            statusTextView.setText(app.getStatus());

            // Couleur dynamique selon le statut
            switch (app.getStatus()) {
                case "En attente":
                    statusTextView.setBackgroundResource(R.drawable.badge_waiting);
                    break;
                default:
                    statusTextView.setBackgroundResource(R.drawable.badge_waiting);
                    break;
            }
        }
    }
}

