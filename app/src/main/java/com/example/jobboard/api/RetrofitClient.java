package com.example.jobboard.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ─────────────────────────────────────────────────────────────────────────
    // 🔧 CONFIGURATION DE L'URL
    //
    // Étapes pour héberger sur Mocky.io :
    //   1. Allez sur  https://designer.mocky.io/
    //   2. Collez le contenu de  app/src/main/assets/job_offers.json
    //   3. Choisissez Content-Type : application/json
    //   4. Cliquez "Generate my HTTP Response"
    //   5. Copiez l'URL obtenue, ex: https://run.mocky.io/v3/a1b2c3d4-...
    //   6. Mettez BASE_URL = "https://run.mocky.io/"
    //   7. Dans JobApiService, mettez @GET("v3/a1b2c3d4-...")
    //
    // Étapes pour héberger sur npoint.io :
    //   1. Allez sur  https://www.npoint.io/
    //   2. Cliquez "Create JSON Bin", collez le JSON
    //   3. Copiez l'ID, ex: abc123
    //   4. Mettez BASE_URL = "https://api.npoint.io/"
    //   5. Dans JobApiService, mettez @GET("abc123")
    // ─────────────────────────────────────────────────────────────────────────

    // ← Changer cette URL après hébergement sur Mocky.io
    private static final String BASE_URL = "https://run.mocky.io/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Réinitialise l'instance Retrofit.
     * Utile si BASE_URL change dynamiquement (ex: en test).
     */
    public static void reset() {
        retrofit = null;
    }

    public static JobApiService getApiService() {
        return getRetrofitInstance().create(JobApiService.class);
    }
}
