package com.example.revivo.data.networking.network;

import com.example.revivo.data.networking.response.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface APIService {
    @GET("exercises.json")
    Call<List<Exercise>> getExercises();

    @POST("exercises.json")
    Call<Exercise> createExercise(@Body Exercise exercise);
}
