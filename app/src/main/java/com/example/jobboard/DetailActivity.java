package com.example.jobboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobboard.database.DatabaseHelper;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private Button callButton;
    private Button smsButton;
    private Button backButton;
    private Button applyButton;

    private DatabaseHelper dbHelper;

    private int    jobId      = -1;
    private String jobTitle   = "";
    private String jobCompany = "";
    private String jobLocation = "";

    private static final String PHONE_NUMBER = "0612345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = findViewById(R.id.detail_title);
        callButton    = findViewById(R.id.call_button);
        smsButton     = findViewById(R.id.sms_button);
        backButton    = findViewById(R.id.back_button);
        applyButton   = findViewById(R.id.apply_button);

        dbHelper = DatabaseHelper.getInstance(this);

        // ── Récupérer les données via Intent explicite ──────────────────────
        Intent intent = getIntent();
        jobId       = intent.getIntExtra("job_id", -1);
        jobTitle    = intent.getStringExtra("job_title") != null
                        ? intent.getStringExtra("job_title") : "Détails de l'offre";
        jobCompany  = intent.getStringExtra("job_company") != null
                        ? intent.getStringExtra("job_company") : "N/A";
        jobLocation = intent.getStringExtra("job_location") != null
                        ? intent.getStringExtra("job_location") : "N/A";

        titleTextView.setText(jobTitle);

        // Mettre à jour l'état du bouton si déjà postulé
        updateApplyButton();

        // ── Listeners ────────────────────────────────────────────────────────
        callButton.setOnClickListener(v -> makePhoneCall());
        smsButton.setOnClickListener(v  -> sendSMS());
        backButton.setOnClickListener(v -> finish());
        applyButton.setOnClickListener(v -> applyToJob());
    }

    // ── Postuler ─────────────────────────────────────────────────────────────

    private void applyToJob() {
        if (jobId == -1) {
            Toast.makeText(this, "Impossible d'identifier l'offre.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.insertApplication(jobId, jobTitle, jobCompany, jobLocation);

        if (result == -1) {
            Toast.makeText(this, "⚠️ Vous avez déjà postulé à cette offre !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "🎉 Candidature envoyée pour : " + jobTitle, Toast.LENGTH_LONG).show();
            updateApplyButton();
        }
    }

    private void updateApplyButton() {
        if (jobId != -1 && dbHelper.isAlreadyApplied(jobId)) {
            applyButton.setText("✅ Déjà postulé");
            applyButton.setEnabled(false);
            applyButton.setAlpha(0.6f);
        } else {
            applyButton.setText("✅ Postuler à cette offre");
            applyButton.setEnabled(true);
            applyButton.setAlpha(1f);
        }
    }

    // ── Appel ────────────────────────────────────────────────────────────────

    private void makePhoneCall() {
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'appel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ── SMS ──────────────────────────────────────────────────────────────────

    private void sendSMS() {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + PHONE_NUMBER));
            smsIntent.putExtra("sms_body",
                    "Bonjour, je suis intéressé(e) par l'offre : " + jobTitle);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

