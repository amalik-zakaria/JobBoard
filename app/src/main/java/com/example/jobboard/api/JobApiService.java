package com.example.jobboard.api;

import com.example.jobboard.models.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JobApiService {

    // API endpoint pour récupérer les posts depuis JSONPlaceholder
    // JSONPlaceholder retourne des posts avec userId, id, title, body
    // Nous les utiliserons comme offres d'emploi dans notre application
    @GET("posts")
    Call<List<Post>> getJobOffers();
}

