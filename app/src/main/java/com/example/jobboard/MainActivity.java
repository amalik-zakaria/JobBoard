package com.example.jobboard;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.jobboard.fragments.OffresFragment;
import com.example.jobboard.fragments.CandidaturesFragment;
import com.example.jobboard.fragments.PreferencesFragment;
import com.example.jobboard.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ── Edge-to-Edge : le header descend sous la status bar ──────────────
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app_header), (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(
                    v.getPaddingLeft(),
                    topInset,            // padding top = hauteur de la status bar
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Créer le canal de notifications (Android 8+)
        NotificationHelper.createNotificationChannel(this);

        // Afficher le fragment Offres par défaut
        if (savedInstanceState == null) {
            loadFragment(new OffresFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_offres);
        }

        // Gestion de la navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_offres) {
                fragment = new OffresFragment();
            } else if (itemId == R.id.nav_candidatures) {
                fragment = new CandidaturesFragment();
            } else if (itemId == R.id.nav_preferences) {
                fragment = new PreferencesFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}