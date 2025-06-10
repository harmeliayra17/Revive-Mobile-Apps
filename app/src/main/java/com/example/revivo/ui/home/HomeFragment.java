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
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.model.ActivityLog;
import com.example.revivo.data.local.model.DailyTarget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.model.ActivityLog;
import com.example.revivo.data.local.model.DailyTarget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvStepsCount, tvExerciseTime, tvWaterIntake, tvSleepDuration;
    private ProgressBar progressSteps, progressExercise, progressWater, progressSleep;

    private DatabaseHelper dbHelper;
    private long currentUserId = 1; // ganti sesuai user login

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi DB
        dbHelper = new DatabaseHelper(requireContext());

        // Inisialisasi view dari XML
        tvStepsCount = view.findViewById(R.id.tvStepsCount);
        tvExerciseTime = view.findViewById(R.id.tvExerciseTime);
        tvWaterIntake = view.findViewById(R.id.tvWaterIntake);
        tvSleepDuration = view.findViewById(R.id.tvSleepDuration);

        // Load data
        loadTodayData();

        return view;
    }

    private void loadTodayData() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ActivityLog log = dbHelper.getActivityLogByDate(currentUserId, todayDate);
        DailyTarget target = dbHelper.getDailyTargetByDate(currentUserId, todayDate);

        // Default nilai
        int steps = 0, targetSteps = 8000;
        int exercise = 0, targetExercise = 45;
        int water = 0, targetWater = 2500; // ml
        float sleep = 0; // jam
        float targetSleep = 8;

        // Ambil data dari DB jika tersedia
        if (log != null) {
            steps = log.getSteps();
            exercise = log.getExerciseMinutes();
            water = log.getWaterMl();
            sleep = log.getSleepHours();
        }

        if (target != null) {
            targetSteps = target.getTargetSteps();
            targetExercise = target.getTargetExercise();
            targetWater = target.getTargetWaterMl();
            targetSleep = target.getTargetSleepHr();
        }

        // Update teks
        tvStepsCount.setText(steps + " / " + targetSteps);
        tvExerciseTime.setText(exercise + " min / " + targetExercise + " min");
        tvWaterIntake.setText((water / 1000.0) + "L / " + (targetWater / 1000.0) + "L");
        tvSleepDuration.setText(sleep + "h / " + targetSleep + "h");

        // Update progres bar
        progressSteps.setProgress(Math.min((steps * 100) / targetSteps, 100));
        progressExercise.setProgress(Math.min((exercise * 100) / targetExercise, 100));
        progressWater.setProgress(Math.min((water * 100) / targetWater, 100));
        progressSleep.setProgress((int) Math.min((sleep * 100) / targetSleep, 100));
    }
}
