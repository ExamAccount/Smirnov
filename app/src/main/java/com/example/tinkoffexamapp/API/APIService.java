package com.example.tinkoffexamapp.API;

import com.example.tinkoffexamapp.Models.Gif;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {

    @GET("random?json=true")
    Call<Gif> getRandomGif();
}
