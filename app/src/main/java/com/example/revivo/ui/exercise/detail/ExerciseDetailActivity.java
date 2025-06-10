package com.example.revivo.ui.exercise.detail;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.revivo.R;
import com.example.revivo.data.ExerciseRepository;
import com.example.revivo.data.networking.network.APIService;
import com.example.revivo.data.networking.network.ApiConfig;
import com.example.revivo.data.networking.response.Exercise;
import com.example.revivo.ui.exercise.detail.TimeActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISE_ID = "exercise_id";
    public static final String EXTRA_EXERCISE_NAME = "exercise_name";
    // Base URL untuk gambar
    private final String baseUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";
    private ImageView ivBack;
    private ImageView ivExerciseImage;
    private TextView tvExerciseName;
    private TextView tvExerciseLevel;
    private TextView tvExerciseEquipment;
    private TextView tvExercisePrimaryMuscle;
    private TextView tvExerciseSecondaryMuscle;
    private TextView tvExerciseForce;
    private TextView tvExerciseMechanic;
    private TextView tvExerciseCategory;
    private TextView tvExerciseInstructions;
    private LinearLayout btnStartExercise;
    private ExerciseRepository exerciseRepository;
    private String exerciseId;
    private String exerciseName;
    private Exercise currentExercise;
    // Slideshow members
    private List<String> imageUrls;
    private Handler imageHandler;
    private Runnable imageRunnable;
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exercise);

        initViews();
        setupRepository();
        getIntentData();
        setupClickListeners();

        imageHandler = new Handler();
        loadExerciseData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageHandler != null && imageRunnable != null) {
            imageHandler.removeCallbacks(imageRunnable);
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvExerciseLevel = findViewById(R.id.tvExerciseLevel);
        tvExerciseEquipment = findViewById(R.id.tvExerciseEquipment);
        tvExercisePrimaryMuscle = findViewById(R.id.tvExercisePrimaryMuscle);
        tvExerciseSecondaryMuscle = findViewById(R.id.tvExerciseSecondaryMuscle);
        tvExerciseForce = findViewById(R.id.tvExerciseForce);
        tvExerciseMechanic = findViewById(R.id.tvExerciseMechanic);
        tvExerciseCategory = findViewById(R.id.tvExerciseCategory);
        tvExerciseInstructions = findViewById(R.id.tvExerciseInstructions);
        btnStartExercise = findViewById(R.id.btnStartExercise);
    }

    private void setupRepository() {
        APIService apiService = ApiConfig.getApiService();
        exerciseRepository = new ExerciseRepository(apiService);
    }

    private void getIntentData() {
        exerciseId = getIntent().getStringExtra(EXTRA_EXERCISE_ID);
        exerciseName = getIntent().getStringExtra(EXTRA_EXERCISE_NAME);

        if (exerciseName != null && !exerciseName.isEmpty()) {
            tvExerciseName.setText(exerciseName);
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke fragment/activity sebelumnya
                onBackPressed();
            }
        });

        btnStartExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Langsung arahkan ke TimeActivity tanpa dialog
                startExerciseTimer();
            }
        });
    }

    private void loadExerciseData() {
        if (exerciseId == null || exerciseId.isEmpty()) {
            Toast.makeText(this, "Exercise ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        exerciseRepository.fetchExercises(new ExerciseRepository.RepositoryCallback<List<Exercise>>() {
            @Override
            public void onSuccess(List<Exercise> exercises) {
                Exercise targetExercise = null;
                for (Exercise exercise : exercises) {
                    if (exercise.getId().equals(exerciseId)) {
                        targetExercise = exercise;
                        break;
                    }
                }

                if (targetExercise != null) {
                    currentExercise = targetExercise;
                    populateExerciseData(targetExercise);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ExerciseDetailActivity.this, "Exercise not found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }

            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(ExerciseDetailActivity.this, "Failed to load exercise data", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void populateExerciseData(final Exercise exercise) {
        runOnUiThread(() -> {
            tvExerciseName.setText(exercise.getName());
            tvExerciseLevel.setText(exercise.getLevel() != null ? exercise.getLevel() : "-");
            tvExerciseEquipment.setText(exercise.getEquipment() != null ? exercise.getEquipment() : "-");

            // Primary Muscles - gabungkan semua
            if (exercise.getPrimaryMuscles() != null && !exercise.getPrimaryMuscles().isEmpty()) {
                StringBuilder primaryMuscles = new StringBuilder();
                for (int i = 0; i < exercise.getPrimaryMuscles().size(); i++) {
                    primaryMuscles.append(exercise.getPrimaryMuscles().get(i));
                    if (i < exercise.getPrimaryMuscles().size() - 1) {
                        primaryMuscles.append(", ");
                    }
                }
                tvExercisePrimaryMuscle.setText(primaryMuscles.toString());
            } else {
                tvExercisePrimaryMuscle.setText("-");
            }

            // Secondary Muscles - gabungkan semua
            if (exercise.getSecondaryMuscles() != null && !exercise.getSecondaryMuscles().isEmpty()) {
                StringBuilder secondaryMuscles = new StringBuilder();
                for (int i = 0; i < exercise.getSecondaryMuscles().size(); i++) {
                    secondaryMuscles.append(exercise.getSecondaryMuscles().get(i));
                    if (i < exercise.getSecondaryMuscles().size() - 1) {
                        secondaryMuscles.append(", ");
                    }
                }
                tvExerciseSecondaryMuscle.setText(secondaryMuscles.toString());
            } else {
                tvExerciseSecondaryMuscle.setText("-");
            }

            tvExerciseForce.setText(exercise.getForce() != null ? exercise.getForce() : "-");
            tvExerciseMechanic.setText(exercise.getMechanic() != null ? exercise.getMechanic() : "-");
            tvExerciseCategory.setText(exercise.getCategory() != null ? exercise.getCategory() : "-");

            // Instructions - gabungkan semua
            if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
                StringBuilder instructions = new StringBuilder();
                for (int i = 0; i < exercise.getInstructions().size(); i++) {
                    instructions.append(i + 1).append(". ").append(exercise.getInstructions().get(i));
                    if (i < exercise.getInstructions().size() - 1) {
                        instructions.append("\n\n");
                    }
                }
                tvExerciseInstructions.setText(instructions.toString());
            } else {
                tvExerciseInstructions.setText("No instructions available");
            }

            // Siapkan slideshow gambar
            if (exercise.getImages() != null && !exercise.getImages().isEmpty()) {
                imageUrls = new ArrayList<>();
                for (String imageName : exercise.getImages()) {
                    imageUrls.add(baseUrl + imageName);
                }
            } else {
                imageUrls = Collections.emptyList();
            }

            // Mulai slideshow jika ada lebih dari 1 gambar
            if (imageUrls.size() > 1) {
                startImageSlideshow();
            } else if (imageUrls.size() == 1) {
                // Load gambar tunggal dengan rounded corners
                loadImageWithRoundedCorners(imageUrls.get(0));
            } else {
                ivExerciseImage.setImageResource(R.drawable.placeholder);
            }
        });
    }

    private void loadImageWithRoundedCorners(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(20))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(ExerciseDetailActivity.this)
                .load(imageUrl)
                .apply(requestOptions)
                .centerCrop()
                .into(ivExerciseImage);
    }

    private void startImageSlideshow() {
        currentImageIndex = 0;
        imageRunnable = new Runnable() {
            @Override
            public void run() {
                String url = imageUrls.get(currentImageIndex);

                // Load dengan smooth transition dan rounded corners
                RequestOptions requestOptions = new RequestOptions()
                        .transform(new RoundedCorners(20))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder);

                Glide.with(ExerciseDetailActivity.this)
                        .load(url)
                        .apply(requestOptions)
                        .centerCrop()
                        .into(ivExerciseImage);

                currentImageIndex = (currentImageIndex + 1) % imageUrls.size();
                imageHandler.postDelayed(this, 3000); // Ganti setiap 3 detik untuk lebih smooth
            }
        };
        imageHandler.post(imageRunnable);
    }

    private void startExerciseTimer() {
        Intent intent = new Intent(this, TimeActivity.class);
        intent.putExtra(TimeActivity.EXTRA_EXERCISE_NAME, currentExercise.getName());
        intent.putExtra(TimeActivity.EXTRA_EXERCISE_ID, exerciseId);
        startActivity(intent);
    }
}