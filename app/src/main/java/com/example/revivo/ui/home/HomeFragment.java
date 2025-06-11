package com.example.revivo.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseContract;
import com.example.revivo.data.local.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvUserName;
    private TextView tvStepsLog, tvStepsTarget;
    private TextView tvExerciseLog, tvExerciseTarget;
    private TextView tvWaterLog, tvWaterTarget;
    private TextView tvSleepLog, tvSleepTarget;
    private TextView tvMotivation;
    private DatabaseHelper dbHelper;
    private long currentUserId;

    public HomeFragment() {}

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvStepsLog = view.findViewById(R.id.tv_steps_log);
        tvStepsTarget = view.findViewById(R.id.tv_steps_target);
        tvExerciseLog = view.findViewById(R.id.tv_exercise_log);
        tvExerciseTarget = view.findViewById(R.id.tv_exercise_target);
        tvWaterLog = view.findViewById(R.id.tv_water_log);
        tvWaterTarget = view.findViewById(R.id.tv_water_target);
        tvSleepLog = view.findViewById(R.id.tv_sleep_log);
        tvSleepTarget = view.findViewById(R.id.tv_sleep_target);
        tvMotivation = view.findViewById(R.id.tvMotivation);

        dbHelper = new DatabaseHelper(getContext());

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("user_id", -1L);

        if (currentUserId != -1) {
            setUserNameFromDatabase();
            loadTodayActivityLogAndTargets();
        } else {
            tvUserName.setText("User");
            Toast.makeText(getContext(), "User tidak dikenali", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void setUserNameFromDatabase() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String[] projection = { DatabaseContract.Users.COLUMN_NAME };
            String selection = DatabaseContract.Users._ID + " = ?";
            String[] selectionArgs = { String.valueOf(currentUserId) };

            cursor = db.query(
                    DatabaseContract.Users.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_NAME));
                tvUserName.setText(name);
            } else {
                tvUserName.setText("User");
            }
        } catch (Exception e) {
            tvUserName.setText("User");
            Log.e("HomeFragment", "Error reading username: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    /**
     * Ambil data log aktivitas hari ini dari baris terakhir tabel ActivityLog,
     * dan target hari ini dari tabel DailyTargets (jika ada, jika tidak pakai default).
     */
    private void loadTodayActivityLogAndTargets() {
        SQLiteDatabase db = null;
        Cursor cursorTarget = null;
        Cursor cursorLastLog = null;
        try {
            db = dbHelper.getReadableDatabase();
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Default target jika tidak ada data di tabel DailyTargets
            int targetSteps = 10000, targetExercise = 30, targetWater = 2000;
            double targetSleep = 8.0;

            // Ambil target harian dari tabel
            cursorTarget = db.rawQuery(
                    "SELECT * FROM " + DatabaseContract.DailyTargets.TABLE_NAME +
                            " WHERE " + DatabaseContract.DailyTargets.COLUMN_USER_ID + " = ? AND " +
                            DatabaseContract.DailyTargets.COLUMN_DATE + " = ?",
                    new String[]{String.valueOf(currentUserId), todayDate}
            );

            if (cursorTarget != null && cursorTarget.moveToFirst()) {
                targetSteps = cursorTarget.getInt(cursorTarget.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS));
                targetExercise = cursorTarget.getInt(cursorTarget.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE));
                targetWater = cursorTarget.getInt(cursorTarget.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML));
                targetSleep = cursorTarget.getDouble(cursorTarget.getColumnIndexOrThrow(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR));
            }
            if (cursorTarget != null) cursorTarget.close();

            // --- Perubahan utama di sini: ganti ORDER BY updated_at DESC jadi ORDER BY _ID DESC ---
            cursorLastLog = db.rawQuery(
                    "SELECT " +
                            DatabaseContract.ActivityLog.COLUMN_STEPS + ", " +
                            DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN + ", " +
                            DatabaseContract.ActivityLog.COLUMN_WATER_ML + ", " +
                            DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS +
                            " FROM " + DatabaseContract.ActivityLog.TABLE_NAME +
                            " WHERE " + DatabaseContract.ActivityLog.COLUMN_USER_ID + " = ? AND " +
                            DatabaseContract.ActivityLog.COLUMN_DATE + " = ? " +
                            "ORDER BY " + DatabaseContract.ActivityLog._ID + " DESC LIMIT 1", // <--- DIUBAH
                    new String[]{String.valueOf(currentUserId), todayDate}
            );
            // --- Akhir perubahan utama ---

            int stepsLog = 0, exerciseLog = 0, waterLog = 0;
            float sleepLog = 0f;

            if (cursorLastLog != null && cursorLastLog.moveToFirst()) {
                stepsLog = cursorLastLog.getInt(cursorLastLog.getColumnIndexOrThrow(DatabaseContract.ActivityLog.COLUMN_STEPS));
                exerciseLog = cursorLastLog.getInt(cursorLastLog.getColumnIndexOrThrow(DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN));
                waterLog = cursorLastLog.getInt(cursorLastLog.getColumnIndexOrThrow(DatabaseContract.ActivityLog.COLUMN_WATER_ML));
                sleepLog = cursorLastLog.getFloat(cursorLastLog.getColumnIndexOrThrow(DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS));
            }
            if (cursorLastLog != null) cursorLastLog.close();

            // Tampilkan data ke view
            tvStepsLog.setText(String.valueOf(stepsLog));
            tvStepsTarget.setText(String.valueOf(targetSteps));
            tvExerciseLog.setText(exerciseLog + " min");
            tvExerciseTarget.setText(targetExercise + " min");
            tvWaterLog.setText(waterLog + " ml");
            tvWaterTarget.setText(targetWater + " ml");
            tvSleepLog.setText(String.format(Locale.getDefault(), "%.1f hr", sleepLog));
            tvSleepTarget.setText(String.format(Locale.getDefault(), "%.1f hr", targetSleep));

            // Tampilkan motivasi
            updateMotivation(stepsLog, targetSteps, exerciseLog, targetExercise, waterLog, targetWater, sleepLog, targetSleep);

        } catch (Exception e) {
            Log.e("HomeFragment", "Error loading log/targets: " + e.getMessage());
            Toast.makeText(getContext(), "Gagal mengambil data aktivitas hari ini.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursorTarget != null) cursorTarget.close();
            if (cursorLastLog != null) cursorLastLog.close();
            if (db != null) db.close();
        }
    }

    private void updateMotivation(int steps, int stepsTarget, int exercise, int exerciseTarget, int water, int waterTarget, float sleep, double sleepTarget) {
        boolean stepsAchieved = steps >= stepsTarget;
        boolean exerciseAchieved = exercise >= exerciseTarget;
        boolean waterAchieved = water >= waterTarget;
        boolean sleepAchieved = sleep >= sleepTarget;

        int achieved = 0;
        if (stepsAchieved) achieved++;
        if (exerciseAchieved) achieved++;
        if (waterAchieved) achieved++;
        if (sleepAchieved) achieved++;

        String message;

        if (achieved == 4) {
            message = "Luar biasa! Semua target harianmu tercapai. Pertahankan kebiasaan sehat ini!";
        } else if (achieved == 3) {
            if (!stepsAchieved) message = "Hampir semua target tercapai! Tambahkan langkahmu agar makin sehat.";
            else if (!exerciseAchieved) message = "Keren! Yuk, sempatkan berolahraga agar targetmu lengkap.";
            else if (!waterAchieved) message = "Mantap! Yuk, cukupkan kebutuhan air minummu hari ini.";
            else message = "Hampir komplit! Jangan lupa tidur cukup malam ini ya.";
        } else if (achieved == 2) {
            message = "Sudah ada kemajuan! Yuk, capai target lainnya agar tubuhmu lebih sehat.";
        } else if (achieved == 1) {
            message = "Langkah awal yang bagus! Terus tingkatkan agar semua target harianmu tercapai.";
        } else {
            message = "Ayo mulai bergerak, minum air, dan jaga kesehatan harianmu!";
        }

        tvMotivation.setText(message);
    }
}