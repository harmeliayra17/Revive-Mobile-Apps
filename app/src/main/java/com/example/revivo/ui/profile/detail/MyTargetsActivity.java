package com.example.revivo.ui.profile.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.revivo.R;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.database.DatabaseContract;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyTargetsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvSteps, tvExercise, tvWater, tvSleep;
    private LinearLayout cvEdit;
    private ImageView ivBack;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    private int targetSteps = 10000;
    private int targetExercise = 30;
    private int targetWater = 2000;
    private double targetSleep = 8.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_targets);

        initViews();
        initDatabase();
        loadTargets();
        setupClickListeners();
    }

    private void initViews() {
        tvSteps = findViewById(R.id.tv_steps);
        tvExercise = findViewById(R.id.tv_exercise);
        tvWater = findViewById(R.id.tv_water);
        tvSleep = findViewById(R.id.tv_sleep);
        cvEdit = findViewById(R.id.cv_edit);
        ivBack = findViewById(R.id.iv_back);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = (int) sharedPreferences.getLong("user_id", -1L);
    }

    private void loadTargets() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String[] projection = {
                DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS,
                DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE,
                DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML,
                DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR
        };

        String selection = DatabaseContract.DailyTargets.COLUMN_USER_ID + " = ? AND " +
                DatabaseContract.DailyTargets.COLUMN_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(currentUserId), today};

        Cursor cursor = db.query(
                DatabaseContract.DailyTargets.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            targetSteps = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS));
            targetExercise = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE));
            targetWater = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML));
            targetSleep = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR));
            cursor.close();
        } else {
            // Create default targets for today if not exists
            createDefaultTargets(today);
        }

        db.close();
        updateUI();
    }

    private void createDefaultTargets(String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyTargets.COLUMN_USER_ID, currentUserId);
        values.put(DatabaseContract.DailyTargets.COLUMN_DATE, date);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS, targetSteps);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE, targetExercise);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML, targetWater);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR, targetSleep);

        db.insert(DatabaseContract.DailyTargets.TABLE_NAME, null, values);
        db.close();
    }

    private void updateUI() {
        tvSteps.setText(String.valueOf(targetSteps));
        tvExercise.setText(targetExercise + " min");
        tvWater.setText(targetWater + " ml");
        tvSleep.setText(String.format("%.1f hr", targetSleep));
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        cvEdit.setOnClickListener(v -> showEditDialog());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom layout for dialog
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_targets, null);

        EditText etSteps = dialogView.findViewById(R.id.et_steps);
        EditText etExercise = dialogView.findViewById(R.id.et_exercise);
        EditText etWater = dialogView.findViewById(R.id.et_water);
        EditText etSleep = dialogView.findViewById(R.id.et_sleep);

        // Set current values
        etSteps.setText(String.valueOf(targetSteps));
        etExercise.setText(String.valueOf(targetExercise));
        etWater.setText(String.valueOf(targetWater));
        etSleep.setText(String.valueOf(targetSleep));

        builder.setView(dialogView)
                .setTitle("Edit Daily Targets")
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        int newSteps = Integer.parseInt(etSteps.getText().toString());
                        int newExercise = Integer.parseInt(etExercise.getText().toString());
                        int newWater = Integer.parseInt(etWater.getText().toString());
                        double newSleep = Double.parseDouble(etSleep.getText().toString());

                        updateTargets(newSteps, newExercise, newWater, newSleep);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTargets(int steps, int exercise, int water, double sleep) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS, steps);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE, exercise);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML, water);
        values.put(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR, sleep);

        String selection = DatabaseContract.DailyTargets.COLUMN_USER_ID + " = ? AND " +
                DatabaseContract.DailyTargets.COLUMN_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(currentUserId), today};

        int rowsUpdated = db.update(
                DatabaseContract.DailyTargets.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if (rowsUpdated == 0) {
            // Insert new record if update failed
            values.put(DatabaseContract.DailyTargets.COLUMN_USER_ID, currentUserId);
            values.put(DatabaseContract.DailyTargets.COLUMN_DATE, today);
            db.insert(DatabaseContract.DailyTargets.TABLE_NAME, null, values);
        }

        db.close();

        // Update local variables and UI
        targetSteps = steps;
        targetExercise = exercise;
        targetWater = water;
        targetSleep = sleep;

        updateUI();
        Toast.makeText(this, "Targets updated successfully", Toast.LENGTH_SHORT).show();
    }
}