package com.example.jobboard.api;

import com.example.jobboard.models.JobOffer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JobApiService {

    /**
     * Récupère la liste des offres d'emploi depuis le serveur.
     *
     * ── Mocky.io ──────────────────────────────────────────────────────────────
     *  BASE_URL  : "https://run.mocky.io/"
     *  Endpoint  : "v3/VOTRE_MOCK_ID"   ← remplacer par l'ID obtenu sur mocky.io
     *
     * ── npoint.io ─────────────────────────────────────────────────────────────
     *  BASE_URL  : "https://api.npoint.io/"
     *  Endpoint  : "VOTRE_ENDPOINT_ID"
     * ─────────────────────────────────────────────────────────────────────────
     */
    @GET("v3/VOTRE_MOCK_ID")
    Call<List<JobOffer>> getJobOffers();
}
