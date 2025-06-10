package com.example.revivo.ui.exercise;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.revivo.data.ExerciseRepository;

public class ExerciseViewModelFactory implements ViewModelProvider.Factory {
    private ExerciseRepository repository;

    public ExerciseViewModelFactory(ExerciseRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExerciseViewModel.class)) {
            return (T) new ExerciseViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}