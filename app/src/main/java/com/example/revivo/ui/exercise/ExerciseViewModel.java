package com.example.revivo.ui.exercise;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.revivo.data.ExerciseRepository;
import com.example.revivo.data.networking.response.Exercise;
import java.util.List;

public class ExerciseViewModel extends ViewModel {
    private static final String TAG = "ExerciseViewModel";

    private final ExerciseRepository repository;
    private final MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ExerciseViewModel(ExerciseRepository repo) {
        this.repository = repo;
        Log.d(TAG, "ViewModel created");
        loadExercises();
    }

    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadExercises() {
        Log.d(TAG, "loadExercises() called");
        isLoading.setValue(true);
        error.setValue(null); // Clear previous error

        repository.fetchExercises(new ExerciseRepository.RepositoryCallback<List<Exercise>>() {
            @Override
            public void onSuccess(List<Exercise> data) {
                Log.d(TAG, "Repository success callback - data size: " + (data != null ? data.size() : "null"));
                exercises.setValue(data);
                error.setValue(null);
                isLoading.setValue(false);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Repository error callback: " + t.getMessage(), t);
                error.setValue(t.getMessage());
                exercises.setValue(null);
                isLoading.setValue(false);
            }
        });
    }
}