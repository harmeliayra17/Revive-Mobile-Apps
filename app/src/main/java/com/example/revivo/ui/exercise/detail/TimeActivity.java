package com.example.revivo.ui.exercise.detail;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.revivo.R;
import com.example.revivo.data.local.database.ActivityLogHelper;
import com.example.revivo.data.local.database.ExerciseLogHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISE_NAME = "exercise_name";
    public static final String EXTRA_EXERCISE_ID = "exercise_id";

    private ImageView ivBack;
    private TextView tvExerciseName, tvTimer, tvDuration, tvMotivation;
    private ProgressBar progressBar;
    private TextView btnPlayPause, btnStop;

    // Duration Selection Buttons
    private TextView btn30Sec, btn1Min, btn2Min, btn3Min, btn4Min, btn5Min, btn10Min, btnCustom;
    private TextView selectedDurationButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis, totalTimeInMillis;
    private boolean isTimerRunning = false;
    private boolean isTimerSet = false;

    private String exerciseName, exerciseId;
    private float durationMinutes = 0;
    private long startTime;

    private ExerciseLogHelper exerciseLogHelper;
    private ActivityLogHelper activityLogHelper;

    private String[] motivationalQuotes = {
            "Push your limits, embrace the challenge!",
            "Every rep counts towards your goals!",
            "Stay focused, stay strong!",
            "You're stronger than you think!",
            "Consistency is the key to success!",
            "Make every second count!",
            "Your body can do it, it's your mind you need to convince!",
            "Progress, not perfection!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        exerciseLogHelper = new ExerciseLogHelper(this); exerciseLogHelper.open();
        activityLogHelper = new ActivityLogHelper(this); activityLogHelper.open();

        initViews();
        getIntentData();
        setupClickListeners();
        setupDurationButtons();
        setRandomMotivationalQuote();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        if (exerciseLogHelper != null) exerciseLogHelper.close();
        if (activityLogHelper != null) activityLogHelper.close();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvTimer = findViewById(R.id.tvTimer);
        tvDuration = findViewById(R.id.tvDuration);
        tvMotivation = findViewById(R.id.tvMotivation);
        progressBar = findViewById(R.id.progressBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);

        btn30Sec = findViewById(R.id.btn30Sec);
        btn1Min = findViewById(R.id.btn1Min);
        btn2Min = findViewById(R.id.btn2Min);
        btn3Min = findViewById(R.id.btn3Min);
        btn4Min = findViewById(R.id.btn4Min);
        btn5Min = findViewById(R.id.btn5Min);
        btn10Min = findViewById(R.id.btn10Min);
        btnCustom = findViewById(R.id.btnCustom);

        btnPlayPause.setEnabled(false);
        btnStop.setEnabled(false);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        exerciseName = intent.getStringExtra(EXTRA_EXERCISE_NAME);
        exerciseId = intent.getStringExtra(EXTRA_EXERCISE_ID);
        tvExerciseName.setText(exerciseName != null ? exerciseName : "Exercise");
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            if (isTimerRunning) showExitConfirmationDialog();
            else finish();
        });

        btnPlayPause.setOnClickListener(v -> {
            if (isTimerRunning) pauseTimer();
            else startTimer();
        });

        btnStop.setOnClickListener(v -> stopTimer());
    }

    private void setupDurationButtons() {
        btn30Sec.setOnClickListener(v -> selectDuration(btn30Sec, 0.5f));
        btn1Min.setOnClickListener(v -> selectDuration(btn1Min, 1));
        btn2Min.setOnClickListener(v -> selectDuration(btn2Min, 2));
        btn3Min.setOnClickListener(v -> selectDuration(btn3Min, 3));
        btn4Min.setOnClickListener(v -> selectDuration(btn4Min, 4));
        btn5Min.setOnClickListener(v -> selectDuration(btn5Min, 5));
        btn10Min.setOnClickListener(v -> selectDuration(btn10Min, 10));
        btnCustom.setOnClickListener(v -> showCustomDurationDialog());
    }

    private void selectDuration(TextView button, float minutes) {
        if (selectedDurationButton != null) selectedDurationButton.setSelected(false);
        selectedDurationButton = button; button.setSelected(true);

        durationMinutes = minutes;
        if (minutes < 1) tvDuration.setText("30 seconds");
        else tvDuration.setText((int) minutes + " minute" + (minutes > 1 ? "s" : ""));

        setupTimer();
        btnPlayPause.setEnabled(true);
        btnStop.setEnabled(true);
        isTimerSet = true;
    }

    private void showCustomDurationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom Duration");
        final EditText input = new EditText(this);
        input.setHint("Enter minutes (1-60)");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                int customMinutes = Integer.parseInt(input.getText().toString());
                if (customMinutes > 0 && customMinutes <= 60) {
                    selectDuration(btnCustom, customMinutes);
                    btnCustom.setText(customMinutes + "m");
                } else {
                    Toast.makeText(this, "Please enter a value between 1-60", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setRandomMotivationalQuote() {
        int randomIndex = (int) (Math.random() * motivationalQuotes.length);
        tvMotivation.setText(motivationalQuotes[randomIndex]);
    }

    private void setupTimer() {
        if (durationMinutes == 0.5f) totalTimeInMillis = 30 * 1000L;
        else totalTimeInMillis = (long) (durationMinutes * 60 * 1000L);
        timeLeftInMillis = totalTimeInMillis;
        progressBar.setMax(100);
        updateTimerText();
        updateProgressBar();
    }

    private void startTimer() {
        if (!isTimerSet) {
            Toast.makeText(this, "Please select a duration", Toast.LENGTH_SHORT).show();
            return;
        }
        startTime = System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                updateProgressBar();
            }
            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 0;
                updateTimerText();
                updateProgressBar();
                btnPlayPause.setText("Start");
                btnPlayPause.setEnabled(false);
                btnStop.setEnabled(false);
                Toast.makeText(TimeActivity.this, "Exercise completed! Well done!", Toast.LENGTH_LONG).show();
                setRandomMotivationalQuote();
                double actualMinutes = totalTimeInMillis / 60000.0;
                saveExerciseLog(actualMinutes);
                resetTimer();
            }
        }.start();
        isTimerRunning = true;
        btnPlayPause.setText("Pause");
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        btnPlayPause.setText("Resume");
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (isTimerRunning && startTime > 0) {
            long elapsed = System.currentTimeMillis() - startTime;
            double actualMinutes = elapsed / 60000.0;
            if (actualMinutes < 1 && elapsed > 10000) actualMinutes = 1;
            if (actualMinutes > 0) {
                saveExerciseLog(actualMinutes);
                Toast.makeText(this, "Exercise stopped. Logged " + (int) actualMinutes + " min", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exercise too short to log", Toast.LENGTH_SHORT).show();
            }
        }
        isTimerRunning = false;
        resetTimer();
        setRandomMotivationalQuote();
    }

    private void resetTimer() {
        timeLeftInMillis = totalTimeInMillis;
        updateTimerText();
        updateProgressBar();
        btnPlayPause.setText("Start");
        btnPlayPause.setEnabled(isTimerSet);
        btnStop.setEnabled(isTimerSet);
        startTime = 0;
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void updateProgressBar() {
        if (totalTimeInMillis > 0) {
            int progress = (int) ((timeLeftInMillis * 100) / totalTimeInMillis);
            progressBar.setProgress(progress);
        }
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Timer Running")
                .setMessage("Timer is currently running. Do you want to stop and save your progress?")
                .setPositiveButton("Stop & Save", (dialog, which) -> {
                    stopTimer();
                    finish();
                })
                .setNegativeButton("Continue", null)
                .show();
    }

    private void saveExerciseLog(double actualMinutes) {
        if (actualMinutes <= 0) return;
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            long userId = 1L; // TODO: Get actual userId from session

            // 1. Insert to Exercise_Log
            long insertResult = exerciseLogHelper.insert(userId, exerciseId, exerciseName, actualMinutes);

            if (insertResult > 0) {
                // 2. Totalkan hari ini
                int totalDuration = exerciseLogHelper.sumDurationByDate(userId, today);

                // 3. Upsert ke Activity_Log
                int updated = activityLogHelper.update(userId, today,
                        0, // steps
                        totalDuration, // exercise min
                        0, // water
                        0f // sleep
                );
                if (updated == 0) {
                    activityLogHelper.insert(userId, today, 0, totalDuration, 0, 0f);
                }
                Toast.makeText(this, "Logged " + (int) actualMinutes + " min. Total today: " + totalDuration + " min", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save exercise log", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving exercise log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isTimerRunning) showExitConfirmationDialog();
        else super.onBackPressed();
    }
}