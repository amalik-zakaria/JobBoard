package com.example.jobboard.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.jobboard.R;

public class PreferencesFragment extends Fragment {

    private EditText searchKeywordEditText;
    private Button saveButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "JobBoardPreferences";
    private static final String SEARCH_KEYWORD_KEY = "search_keyword";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchKeywordEditText = view.findViewById(R.id.search_keyword_edit_text);
        saveButton = view.findViewById(R.id.save_button);

        // Initialiser SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Charger la valeur sauvegardée
        loadSavedKeyword();

        // Gérer le clic du bouton de sauvegarde
        saveButton.setOnClickListener(v -> saveSearchKeyword());
    }

    private void loadSavedKeyword() {
        String savedKeyword = sharedPreferences.getString(SEARCH_KEYWORD_KEY, "");
        searchKeywordEditText.setText(savedKeyword);
    }

    private void saveSearchKeyword() {
        String keyword = searchKeywordEditText.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez entrer un mot-clé", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEARCH_KEYWORD_KEY, keyword);
        editor.apply();

        Toast.makeText(requireContext(), "Mot-clé sauvegardé : " + keyword, Toast.LENGTH_SHORT).show();
    }
}

