package com.example.revivo.data;

import android.util.Log;

import com.example.revivo.data.networking.network.APIService;
import com.example.revivo.data.networking.response.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseRepository {
    private static final String TAG = "ExerciseRepository";
    private final APIService api;

    public ExerciseRepository(APIService api) {
        this.api = api;
    }

    public void fetchExercises(final RepositoryCallback<List<Exercise>> callback) {
        Log.d(TAG, "Starting API call...");

        Call<List<Exercise>> call = api.getExercises(); // Tanpa offset & limit
        Log.d(TAG, "API Call URL: " + call.request().url());

        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                Log.d(TAG, "API Response received");
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response message: " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> exercises = response.body();
                    Log.d(TAG, "Success! Data size: " + exercises.size());

                    if (!exercises.isEmpty()) {
                        Exercise firstExercise = exercises.get(0);
                        Log.d(TAG, "First exercise: " + firstExercise.getName());
                        Log.d(TAG, "ID: " + firstExercise.getId());
                        // Jika kamu punya metode getBodyPart(), panggil di sini
                        // Log.d(TAG, "Body Part: " + firstExercise.getBodyPart());
                    }

                    callback.onSuccess(exercises);
                } else {
                    Log.e(TAG, "Response not successful or body is null");
                    callback.onError(new Exception("Invalid response"));
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(Throwable t);
    }
}
