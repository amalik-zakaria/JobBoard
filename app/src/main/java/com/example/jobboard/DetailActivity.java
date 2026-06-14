package com.example.jobboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.jobboard.database.DatabaseHelper;
import com.example.jobboard.utils.NotificationHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    // ── Views ────────────────────────────────────────────────────────────────
    private TextView  titleTextView;
    private Button    applyButton, photoCvButton, callButton, smsButton, backButton;
    private ImageView cvImageView;

    // ── Data ─────────────────────────────────────────────────────────────────
    private int    jobId       = -1;
    private String jobTitle    = "";
    private String jobCompany  = "";
    private String jobLocation = "";

    // ── Helpers ──────────────────────────────────────────────────────────────
    private DatabaseHelper dbHelper;

    // ── Camera ───────────────────────────────────────────────────────────────
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private Uri    photoUri;
    private ActivityResultLauncher<Intent> cameraLauncher;

    // ── Notification ─────────────────────────────────────────────────────────
    private static final int REQUEST_NOTIF_PERMISSION = 102;

    private static final String PHONE_NUMBER = "0612345678";

    // ── Coordonnées de démonstration par jobId (lat/lng) ─────────────────────
    private static final double[][] DEMO_COORDS = {
        { 48.8566,  2.3522 },  // Paris
        { 45.7640,  4.8357 },  // Lyon
        { 43.2965,  5.3698 },  // Marseille
        { 43.6047,  1.4442 },  // Toulouse
        { 47.2184, -1.5536 }   // Nantes
    };

    // ────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Vues
        titleTextView = findViewById(R.id.detail_title);
        applyButton   = findViewById(R.id.apply_button);
        photoCvButton = findViewById(R.id.photo_cv_button);
        callButton    = findViewById(R.id.call_button);
        smsButton     = findViewById(R.id.sms_button);
        backButton    = findViewById(R.id.back_button);
        cvImageView   = findViewById(R.id.cv_image_view);

        dbHelper = DatabaseHelper.getInstance(this);

        // ── Intent ──────────────────────────────────────────────────────────
        Intent intent = getIntent();
        jobId       = intent.getIntExtra("job_id", -1);
        jobTitle    = intent.getStringExtra("job_title")    != null ? intent.getStringExtra("job_title")    : "Offre";
        jobCompany  = intent.getStringExtra("job_company")  != null ? intent.getStringExtra("job_company")  : "N/A";
        jobLocation = intent.getStringExtra("job_location") != null ? intent.getStringExtra("job_location") : "N/A";

        titleTextView.setText(jobTitle);
        updateApplyButton();

        // ── Carte ──────────────────────────────────────────────────────────
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // ── Launcher appareil photo ─────────────────────────────────────────
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && photoUri != null) {
                        cvImageView.setImageURI(photoUri);
                        cvImageView.setVisibility(android.view.View.VISIBLE);
                        Toast.makeText(this, "📷 Photo du CV enregistrée !", Toast.LENGTH_SHORT).show();
                    }
                });

        // ── Listeners ──────────────────────────────────────────────────────
        applyButton.setOnClickListener(v   -> applyToJob());
        photoCvButton.setOnClickListener(v -> checkCameraPermissionAndLaunch());
        callButton.setOnClickListener(v    -> makePhoneCall());
        smsButton.setOnClickListener(v     -> sendSMS());
        backButton.setOnClickListener(v    -> finish());
    }

    // ── OnMapReadyCallback ──────────────────────────────────────────────────
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Sélectionner les coordonnées selon l'ID
        int idx = Math.abs(jobId) % DEMO_COORDS.length;
        double lat = DEMO_COORDS[idx][0];
        double lng = DEMO_COORDS[idx][1];

        LatLng position = new LatLng(lat, lng);

        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(jobCompany)
                .snippet(jobLocation));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    // ── Postuler ─────────────────────────────────────────────────────────────
    private void applyToJob() {
        if (jobId == -1) {
            Toast.makeText(this, "Impossible d'identifier l'offre.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.insertApplication(jobId, jobTitle, jobCompany, jobLocation);

        if (result == -1) {
            Toast.makeText(this, "⚠️ Déjà postulé à cette offre !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "🎉 Candidature envoyée : " + jobTitle, Toast.LENGTH_LONG).show();
            updateApplyButton();
            sendCandidatureNotification();
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

    // ── Notification ─────────────────────────────────────────────────────────
    private void sendCandidatureNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIF_PERMISSION);
                return;
            }
        }
        NotificationHelper.sendApplicationNotification(this, jobTitle);
    }

    // ── Appareil Photo ───────────────────────────────────────────────────────
    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "Aucune application caméra disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer un fichier temporaire pour la photo
        File photoFile = createImageFile();
        if (photoFile == null) {
            Toast.makeText(this, "Impossible de créer le fichier photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        photoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider",
                photoFile);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(cameraIntent);
    }

    private File createImageFile() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String fileName = "CV_" + timestamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            return null;
        }
    }

    // ── Permissions callback ─────────────────────────────────────────────────
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Permission caméra refusée.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_NOTIF_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.sendApplicationNotification(this, jobTitle);
            } else {
                Toast.makeText(this, "Permission notification refusée.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── Appel ─────────────────────────────────────────────────────────────────
    private void makePhoneCall() {
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur appel : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ── SMS ──────────────────────────────────────────────────────────────────
    private void sendSMS() {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + PHONE_NUMBER));
            smsIntent.putExtra("sms_body", "Bonjour, je suis intéressé(e) par l'offre : " + jobTitle);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur SMS : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
