package com.example.jobboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobboard.R;
import com.example.jobboard.models.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applications;

    // ── Interface de suppression ─────────────────────────────────────────────
    public interface OnDeleteClickListener {
        void onDeleteClick(Application application, int position);
    }

    private OnDeleteClickListener deleteListener;

    public ApplicationAdapter(List<Application> applications, OnDeleteClickListener deleteListener) {
        this.applications   = applications;
        this.deleteListener = deleteListener;
    }

    /** Rétrocompatibilité sans listener */
    public ApplicationAdapter(List<Application> applications) {
        this(applications, null);
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }

    /** Supprime un item à la position donnée et notifie le RecyclerView. */
    public void removeItem(int position) {
        if (applications != null && position >= 0 && position < applications.size()) {
            applications.remove(position);
            notifyItemRemoved(position);
            // Met à jour les badges de position des éléments suivants
            notifyItemRangeChanged(position, applications.size());
        }
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
        holder.bind(applications.get(position), position, deleteListener);
    }

    @Override
    public int getItemCount() {
        return applications != null ? applications.size() : 0;
    }

    // ── ViewHolder ───────────────────────────────────────────────────────────
    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private final TextView     titleTextView;
        private final TextView     companyTextView;
        private final TextView     locationTextView;
        private final TextView     dateTextView;
        private final TextView     statusTextView;
        private final ImageButton  deleteButton;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView    = itemView.findViewById(R.id.application_title);
            companyTextView  = itemView.findViewById(R.id.application_company);
            locationTextView = itemView.findViewById(R.id.application_location);
            dateTextView     = itemView.findViewById(R.id.application_date);
            statusTextView   = itemView.findViewById(R.id.application_status);
            deleteButton     = itemView.findViewById(R.id.delete_button);
        }

        public void bind(Application app, int position, OnDeleteClickListener listener) {
            // ── Données réelles issues de la BDD ────────────────────────────
            titleTextView.setText(app.getJobTitle());
            companyTextView.setText("🏢 " + (app.getCompany()  != null ? app.getCompany()  : "N/A"));
            locationTextView.setText("📍 " + (app.getLocation() != null ? app.getLocation() : "N/A"));
            dateTextView.setText("📅 Postulé le : " + app.getDateApplied());
            statusTextView.setText(app.getStatus());

            // Style du badge statut
            statusTextView.setBackgroundResource(R.drawable.badge_waiting);

            // ── Listener corbeille ───────────────────────────────────────────
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(app, getAdapterPosition());
                }
            });
        }
    }
}
