package com.example.newsapp.https;

import com.example.newsapp.models.Head;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PostInterface {
    @GET("top-headlines")
    Call<Head> getAllShops(@Query("country") String country, @Query("apiKey") String apiKey);
}
