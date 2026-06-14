package com.example.jobboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private Button callButton;
    private Button smsButton;
    private Button backButton;

    private static final String PHONE_NUMBER = "0612345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = findViewById(R.id.detail_title);
        callButton = findViewById(R.id.call_button);
        smsButton = findViewById(R.id.sms_button);
        backButton = findViewById(R.id.back_button);

        // Récupérer l'Intent explicite
        Intent intent = getIntent();
        String jobTitle = intent.getStringExtra("job_title");

        // Afficher le titre
        if (jobTitle != null) {
            titleTextView.setText(jobTitle);
        } else {
            titleTextView.setText("Détails de l'offre");
        }

        // Bouton Appeler
        callButton.setOnClickListener(v -> makePhoneCall());

        // Bouton SMS
        smsButton.setOnClickListener(v -> sendSMS());

        // Bouton Retour
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Lance un Intent implicite pour faire un appel (ACTION_DIAL)
     */
    private void makePhoneCall() {
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'appel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lance un Intent implicite pour envoyer un SMS (ACTION_SENDTO)
     */
    private void sendSMS() {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + PHONE_NUMBER));
            smsIntent.putExtra("sms_body", "J'aimerais en savoir plus sur cette offre d'emploi: " + titleTextView.getText());
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'envoi du SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

