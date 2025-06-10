package com.example.revivo.ui.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.revivo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    // UI Components
    private CircleImageView profileImage;
    private TextView tvUserName;
    private TextView tvExerciseTime;
    private TextView tvStepsCount;
    private TextView tvWaterIntake;
    private TextView tvSleepDuration;

    // Progress Bars
    private ProgressBar progressExercise;
    private ProgressBar progressSteps;
    private ProgressBar progressWater;
    private ProgressBar progressSleep;

    // Data variables
    private String userName = "Harmeliayra";
    private int targetExerciseTime = 45; // minutes    private int currentExerciseTime = getCurrentExerciseTime(); // minutes
    private int targetSteps = 8000;
    private double targetWaterIntake = 2.5; // liters    private int currentSteps = getCurrentSteps();
    private int targetSleepHours = 8; // hours

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        loadData();

        return view;
    }
    private void initViews(View view) {
        // Initialize UI components
        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvExerciseTime = view.findViewById(R.id.tvExerciseTime);
        tvStepsCount = view.findViewById(R.id.tvStepsCount);
        tvWaterIntake = view.findViewById(R.id.tvWaterIntake);
        tvSleepDuration = view.findViewById(R.id.tvSleepDuration);

        // Initialize progress bars
        progressExercise = view.findViewById(R.id.progressExercise);
        progressSteps = view.findViewById(R.id.progressSteps);
        progressWater = view.findViewById(R.id.progressWater);
        progressSleep = view.findViewById(R.id.progressSleep);
    }private double currentWaterIntake = getCurrentWaterIntake(); // liters

private void loadData() {
        // Load user data (you can replace this with actual data from database/API)
        loadUserData();
        loadHealthData();
        updateProgressBars();
    }

        private void loadUserData() {
        // Set user name
        tvUserName.setText(userName);

        // You can set profile image here if needed
        // profileImage.setImageResource(R.drawable.profilepic);
    }
    private void loadHealthData() {
        // Update exercise time
        tvExerciseTime.setText(currentExerciseTime + " min / " + targetExerciseTime + " min");

        // Update steps count
        tvStepsCount.setText(String.format("%,d / %,d", currentSteps, targetSteps));

        // Update water intake
        tvWaterIntake.setText(currentWaterIntake + "L / " + targetWaterIntake + "L");

        // Update sleep duration
        tvSleepDuration.setText(currentSleepHours + "h / " + targetSleepHours + "h");
    }private int currentSleepHours = getCurrentSleepHours(); // hours

    private void updateProgressBars() {
        // Calculate and set progress for exercise
        int exerciseProgress = (int) ((double) currentExerciseTime / targetExerciseTime * 100);
        progressExercise.setProgress(exerciseProgress);

        // Calculate and set progress for steps
        int stepsProgress = (int) ((double) currentSteps / targetSteps * 100);
        progressSteps.setProgress(stepsProgress);

        // Calculate and set progress for water intake
        int waterProgress = (int) (currentWaterIntake / targetWaterIntake * 100);
        progressWater.setProgress(waterProgress);

        // Calculate and set progress for sleep
        int sleepProgress = (int) ((double) currentSleepHours / targetSleepHours * 100);
        progressSleep.setProgress(sleepProgress);
    }

    // Method to update exercise data (can be called from other parts of the app)
    public void updateExerciseData(int currentMinutes, int targetMinutes) {
        this.currentExerciseTime = currentMinutes;
        this.targetExerciseTime = targetMinutes;

        if (tvExerciseTime != null) {
            tvExerciseTime.setText(currentMinutes + " min / " + targetMinutes + " min");
            int progress = (int) ((double) currentMinutes / targetMinutes * 100);
            progressExercise.setProgress(progress);
        }
    }

    // Method to update steps data
    public void updateStepsData(int currentSteps, int targetSteps) {
        this.currentSteps = currentSteps;
        this.targetSteps = targetSteps;

        if (tvStepsCount != null) {
            tvStepsCount.setText(String.format("%,d / %,d", currentSteps, targetSteps));
            int progress = (int) ((double) currentSteps / targetSteps * 100);
            progressSteps.setProgress(progress);
        }
    }

    // Method to update water intake data
    public void updateWaterData(double currentLiters, double targetLiters) {
        this.currentWaterIntake = currentLiters;
        this.targetWaterIntake = targetLiters;

        if (tvWaterIntake != null) {
            tvWaterIntake.setText(currentLiters + "L / " + targetLiters + "L");
            int progress = (int) (currentLiters / targetLiters * 100);
            progressWater.setProgress(progress);
        }
    }

    // Method to update sleep data
    public void updateSleepData(int currentHours, int targetHours) {
        this.currentSleepHours = currentHours;
        this.targetSleepHours = targetHours;

        if (tvSleepDuration != null) {
            tvSleepDuration.setText(currentHours + "h / " + targetHours + "h");
            int progress = (int) ((double) currentHours / targetHours * 100);
            progressSleep.setProgress(progress);
        }
    }

    // Method to refresh all data (useful for when returning to fragment)
    public void refreshData() {
        if (getView() != null) {
            loadData();
        }
    }

    // Getter methods for accessing current data
    public int getCurrentExerciseTime() { return currentExerciseTime; }

    public int getCurrentSteps() { return currentSteps; }

    public double getCurrentWaterIntake() { return currentWaterIntake; }

    public int getCurrentSleepHours() { return currentSleepHours; }









}