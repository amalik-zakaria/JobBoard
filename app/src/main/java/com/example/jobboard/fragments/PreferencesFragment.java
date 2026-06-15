package com.example.jobboard.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import com.example.jobboard.R;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class PreferencesFragment extends Fragment {

    // ── Views ────────────────────────────────────────────────────────────────
    private TextInputEditText keywordEditText;
    private Button            addButton;
    private Button            clearAllButton;
    private ChipGroup         chipGroup;
    private TextView          chipsLabel;
    private TextView          countText;

    // ── SharedPreferences ────────────────────────────────────────────────────
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME        = "JobBoardPreferences";
    private static final String KEYWORDS_SET_KEY  = "search_keywords_set";

    // ── État local ───────────────────────────────────────────────────────────
    private Set<String> keywordsSet = new TreeSet<>();  // TreeSet = trié alphabétiquement

    // ────────────────────────────────────────────────────────────────────────
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Liaisons vues
        keywordEditText = view.findViewById(R.id.search_keyword_edit_text);
        addButton       = view.findViewById(R.id.add_keyword_button);
        clearAllButton  = view.findViewById(R.id.clear_all_button);
        chipGroup       = view.findViewById(R.id.chip_group_keywords);
        chipsLabel      = view.findViewById(R.id.chips_label);
        countText       = view.findViewById(R.id.keywords_count_text);

        sharedPreferences = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Charger les mots-clés sauvegardés
        loadKeywords();

        // Clic "Ajouter"
        addButton.setOnClickListener(v -> addKeyword());

        // Touche "Done" / "Entrée" sur le clavier → même effet que Ajouter
        keywordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                addKeyword();
                return true;
            }
            return false;
        });

        // Clic "Tout effacer"
        clearAllButton.setOnClickListener(v -> clearAllKeywords());
    }

    // ── Ajouter un mot-clé ───────────────────────────────────────────────────
    private void addKeyword() {
        String keyword = keywordEditText.getText() != null
                ? keywordEditText.getText().toString().trim()
                : "";

        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "Saisissez un mot-clé.", Toast.LENGTH_SHORT).show();
            return;
        }

        String keywordLower = keyword.toLowerCase();

        if (keywordsSet.contains(keywordLower)) {
            Toast.makeText(requireContext(),
                    "« " + keyword + " » est déjà dans la liste.", Toast.LENGTH_SHORT).show();
            keywordEditText.setText("");
            return;
        }

        // Ajouter au Set et sauvegarder
        keywordsSet.add(keywordLower);
        saveKeywords();

        // Créer le Chip dynamiquement
        addChip(keyword);

        // Réinitialiser le champ
        keywordEditText.setText("");
        hideSoftKeyboard();

        updateUI();
        Toast.makeText(requireContext(), "✅ « " + keyword + " » ajouté !", Toast.LENGTH_SHORT).show();
    }

    // ── Créer et insérer un Chip ─────────────────────────────────────────────
    private void addChip(String keyword) {
        Chip chip = new Chip(requireContext());
        chip.setText(keyword);
        chip.setCloseIconVisible(true);          // ✕ pour supprimer
        chip.setCheckable(false);
        chip.setChipBackgroundColorResource(com.google.android.material.R.color.m3_chip_background_color);

        // Clic sur ✕ → supprimer ce mot-clé
        chip.setOnCloseIconClickListener(v -> {
            keywordsSet.remove(keyword.toLowerCase());
            saveKeywords();
            chipGroup.removeView(chip);
            updateUI();
            Toast.makeText(requireContext(), "« " + keyword + " » supprimé.", Toast.LENGTH_SHORT).show();
        });

        chipGroup.addView(chip);
    }

    // ── Charger depuis SharedPreferences ─────────────────────────────────────
    private void loadKeywords() {
        Set<String> saved = sharedPreferences.getStringSet(KEYWORDS_SET_KEY, new HashSet<>());
        keywordsSet = new TreeSet<>(saved);   // copie modifiable + triée

        chipGroup.removeAllViews();
        for (String kw : keywordsSet) {
            addChip(kw);
        }
        updateUI();
    }

    // ── Sauvegarder dans SharedPreferences ───────────────────────────────────
    private void saveKeywords() {
        sharedPreferences.edit()
                .putStringSet(KEYWORDS_SET_KEY, new HashSet<>(keywordsSet))
                .apply();
    }

    // ── Tout effacer ─────────────────────────────────────────────────────────
    private void clearAllKeywords() {
        keywordsSet.clear();
        saveKeywords();
        chipGroup.removeAllViews();
        updateUI();
        Toast.makeText(requireContext(), "Tous les mots-clés ont été supprimés.", Toast.LENGTH_SHORT).show();
    }

    // ── Mise à jour visibilité / compteur ────────────────────────────────────
    private void updateUI() {
        int count = keywordsSet.size();
        boolean hasChips = count > 0;

        chipsLabel.setVisibility(hasChips ? View.VISIBLE : View.GONE);
        clearAllButton.setVisibility(hasChips ? View.VISIBLE : View.GONE);
        countText.setVisibility(hasChips ? View.VISIBLE : View.GONE);

        if (hasChips) {
            countText.setText(count + " mot" + (count > 1 ? "s-clés" : "-clé") + " enregistré" + (count > 1 ? "s" : ""));
        }
    }

    // ── Cacher le clavier ────────────────────────────────────────────────────
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && keywordEditText != null) {
            imm.hideSoftInputFromWindow(keywordEditText.getWindowToken(), 0);
        }
    }
}
