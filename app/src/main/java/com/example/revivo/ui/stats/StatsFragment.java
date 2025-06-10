package com.example.revivo.ui.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseContract;
import com.example.revivo.data.local.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvAvgSteps, tvAvgExercise, tvAvgWater, tvAvgSleep;
    private LinearLayout layoutDayStats, layoutBarSteps;
    private DatabaseHelper dbHelper;
    private long currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // Inisialisasi view
        tvAvgSteps = view.findViewById(R.id.tvAvgSteps);
        tvAvgExercise = view.findViewById(R.id.tvAvgExercise);
        tvAvgWater = view.findViewById(R.id.tvAvgWater);
        tvAvgSleep = view.findViewById(R.id.tvAvgSleep);
        layoutDayStats = view.findViewById(R.id.layoutDayStats);
        layoutBarSteps = view.findViewById(R.id.layoutBarSteps);

        dbHelper = new DatabaseHelper(getContext());

        // Ambil user_id dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong("user_id", -1L);

        if (currentUserId != -1) {
            loadWeeklyStats();
        }

        return view;
    }

    private void loadWeeklyStats() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Ambil tanggal untuk 7 hari terakhir
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String[] dates = new String[7];
        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -i);
            dates[6 - i] = sdf.format(cal.getTime());
        }
        String dateFrom = dates[0];
        String dateTo = dates[6];

        // Query data aktivitas user selama 7 hari terakhir
        String sql = "SELECT " +
                DatabaseContract.ActivityLog.COLUMN_DATE + ", " +
                DatabaseContract.ActivityLog.COLUMN_STEPS + ", " +
                DatabaseContract.ActivityLog.COLUMN_WATER_ML + ", " +
                DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS + ", " +
                DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN +
                " FROM " + DatabaseContract.ActivityLog.TABLE_NAME +
                " WHERE " + DatabaseContract.ActivityLog.COLUMN_USER_ID + " = ?" +
                " AND " + DatabaseContract.ActivityLog.COLUMN_DATE + " BETWEEN ? AND ?" +
                " ORDER BY " + DatabaseContract.ActivityLog.COLUMN_DATE + " ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(currentUserId), dateFrom, dateTo
        });

        int totalSteps = 0, totalWater = 0, totalExercise = 0;
        float totalSleep = 0f;
        int[] dailySteps = new int[7];
        int[] dailyWater = new int[7];
        float[] dailySleep = new float[7];
        int[] dailyExercise = new int[7];
        String[] dailyDates = dates.clone();

        int count = 0;
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ActivityLog.COLUMN_DATE));
            // --- SAFE GETTER for all fields ---
            int idxSteps   = cursor.getColumnIndex(DatabaseContract.ActivityLog.COLUMN_STEPS);
            int idxWater   = cursor.getColumnIndex(DatabaseContract.ActivityLog.COLUMN_WATER_ML);
            int idxSleep   = cursor.getColumnIndex(DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS);
            int idxExercise= cursor.getColumnIndex(DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN);

            int steps = (idxSteps >= 0 && !cursor.isNull(idxSteps)) ? cursor.getInt(idxSteps) : 0;
            int water = (idxWater >= 0 && !cursor.isNull(idxWater)) ? cursor.getInt(idxWater) : 0;
            float sleep = (idxSleep >= 0 && !cursor.isNull(idxSleep)) ? cursor.getFloat(idxSleep) : 0f;
            int exercise = (idxExercise >= 0 && !cursor.isNull(idxExercise)) ? cursor.getInt(idxExercise) : 0;

            // Cari index sesuai tanggal
            int idx = -1;
            for (int i = 0; i < 7; i++) {
                if (dailyDates[i].equals(date)) {
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                dailySteps[idx] = steps;
                dailyWater[idx] = water;
                dailySleep[idx] = sleep;
                dailyExercise[idx] = exercise;
            }

            totalSteps += steps;
            totalWater += water;
            totalSleep += sleep;
            totalExercise += exercise;
            count++;
        }
        cursor.close();
        db.close();

        // Hitung rata-rata
        int avgSteps = count != 0 ? totalSteps / count : 0;
        int avgWater = count != 0 ? totalWater / count : 0;
        float avgSleep = count != 0 ? totalSleep / count : 0f;
        int avgExercise = count != 0 ? totalExercise / count : 0;

        tvAvgSteps.setText(String.valueOf(avgSteps));
        tvAvgExercise.setText(avgExercise + " min");
        tvAvgWater.setText(avgWater + " ml");
        tvAvgSleep.setText(String.format(Locale.getDefault(), "%.1f hr", avgSleep));

        displayDailyStepBars(dailyDates, dailySteps);
        displayDailyDetails(dailyDates, dailySteps, dailyWater, dailySleep, dailyExercise);
    }

    private void displayDailyStepBars(String[] dailyDates, int[] dailySteps) {
        layoutBarSteps.removeAllViews();

        int maxStep = 1;
        for (int value : dailySteps) {
            if (value > maxStep) maxStep = value;
        }

        for (int i = 0; i < 7; i++) {
            View bar = LayoutInflater.from(getContext()).inflate(R.layout.item_bar_day, layoutBarSteps, false);
            TextView tvDay = bar.findViewById(R.id.tvDay);
            ProgressBar progressBar = bar.findViewById(R.id.progressBar);
            TextView tvValue = bar.findViewById(R.id.tvValue);

            tvDay.setText(getShortDayName(dailyDates[i]));
            progressBar.setMax(maxStep);
            progressBar.setProgress(dailySteps[i]);
            tvValue.setText(dailySteps[i] + " steps");

            layoutBarSteps.addView(bar);
        }
    }

    private void displayDailyDetails(String[] dailyDates, int[] dailySteps, int[] dailyWater, float[] dailySleep, int[] dailyExercise) {
        layoutDayStats.removeAllViews();

        for (int i = 0; i < 7; i++) {
            TextView tv = new TextView(getContext());
            String hari = getDayName(dailyDates[i]);
            tv.setText(String.format(Locale.getDefault(), "%s (%s): %d steps, %d ml, %.1f hr, %d min exercise",
                    hari, dailyDates[i], dailySteps[i], dailyWater[i], dailySleep[i], dailyExercise[i]));
            tv.setTextSize(14f);
            tv.setTextColor(getResources().getColor(R.color.color_primary, null));
            tv.setPadding(0, 4, 0, 4);
            layoutDayStats.addView(tv);
        }
    }

    private String getDayName(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE", new Locale("id"));
            return sdf2.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String getShortDayName(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEE", new Locale("id"));
            return sdf2.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}