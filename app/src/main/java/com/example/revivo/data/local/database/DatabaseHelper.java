package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.revivo.data.local.model.ActivityLog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.revivo.data.local.model.ActivityLog;
import com.example.revivo.data.local.model.DailyTarget;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "revivo.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users
        db.execSQL(
                "CREATE TABLE " + DatabaseContract.Users.TABLE_NAME + " (" +
                        DatabaseContract.Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.Users.COLUMN_NAME + " TEXT, " +
                        DatabaseContract.Users.COLUMN_EMAIL + " TEXT UNIQUE, " +
                        DatabaseContract.Users.COLUMN_PASSWORD + " TEXT, " +
                        DatabaseContract.Users.COLUMN_GENDER + " TEXT, " +
                        DatabaseContract.Users.COLUMN_BIRTH_DATE + " DATE, " +
                        DatabaseContract.Users.COLUMN_WEIGHT_KG + " REAL, " +
                        DatabaseContract.Users.COLUMN_HEIGHT_CM + " REAL, " +
                        DatabaseContract.Users.COLUMN_ACTIVITY_LEVEL + " TEXT, " +
                        DatabaseContract.Users.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");"
        );

        // Daily Targets
        db.execSQL(
                "CREATE TABLE " + DatabaseContract.DailyTargets.TABLE_NAME + " (" +
                        DatabaseContract.DailyTargets._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.DailyTargets.COLUMN_USER_ID + " INTEGER, " +
                        DatabaseContract.DailyTargets.COLUMN_DATE + " DATE, " +
                        "target_steps INTEGER, " +
                        "target_exercise INTEGER, " +
                        "target_water_ml INTEGER, " +
                        "target_sleep_hr REAL, " +
                        "FOREIGN KEY(" + DatabaseContract.DailyTargets.COLUMN_USER_ID + ") REFERENCES " +
                        DatabaseContract.Users.TABLE_NAME + "(" + DatabaseContract.Users._ID + ")" +
                        ");"
        );

        // Activity Log
        db.execSQL(
                "CREATE TABLE " + DatabaseContract.ActivityLog.TABLE_NAME + " (" +
                        DatabaseContract.ActivityLog._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.ActivityLog.COLUMN_USER_ID + " INTEGER, " +
                        DatabaseContract.ActivityLog.COLUMN_DATE + " DATE, " +
                        DatabaseContract.ActivityLog.COLUMN_STEPS + " INTEGER, " +
                        DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN + " INTEGER, " +
                        DatabaseContract.ActivityLog.COLUMN_WATER_ML + " INTEGER, " +
                        DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS + " REAL, " +
                        DatabaseContract.ActivityLog.COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(" + DatabaseContract.ActivityLog.COLUMN_USER_ID + ") REFERENCES " +
                        DatabaseContract.Users.TABLE_NAME + "(" + DatabaseContract.Users._ID + ")" +
                        ");"
        );

        // Exercise Log
        db.execSQL(
                "CREATE TABLE " + DatabaseContract.ExerciseLog.TABLE_NAME + " (" +
                        DatabaseContract.ExerciseLog._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabaseContract.ExerciseLog.COLUMN_USER_ID + " INTEGER, " +
                        DatabaseContract.ExerciseLog.COLUMN_EXERCISE_ID + " TEXT, " +
                        DatabaseContract.ExerciseLog.COLUMN_EXERCISE_NAME + " TEXT, " +
                        DatabaseContract.ExerciseLog.COLUMN_DURATION_MIN + " INTEGER, " +
                        DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(" + DatabaseContract.ExerciseLog.COLUMN_USER_ID + ") REFERENCES " +
                        DatabaseContract.Users.TABLE_NAME + "(" + DatabaseContract.Users._ID + ")" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ExerciseLog.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ActivityLog.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DailyTargets.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.Users.TABLE_NAME);
        onCreate(db);
    }

    public void insertActivityLog(ActivityLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", log.getUserId());
        values.put("date", log.getDate());
        values.put("steps", log.getSteps());
        values.put("exercise_minutes", log.getExerciseMinutes());
        values.put("water_ml", log.getWaterMl());
        values.put("sleep_hours", log.getSleepHours());
        values.put("updated_at", log.getUpdatedAt());

        db.insert("Activity_Log", null, values);
        db.close();
    }

    public ActivityLog getLatestActivityLogForUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ActivityLog log = null;

        Cursor cursor = db.rawQuery("SELECT * FROM Activity_Log WHERE user_id = ? ORDER BY date DESC LIMIT 1",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            log = new ActivityLog();
            log.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
            log.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            log.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            log.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow("steps")));
            log.setExerciseMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("exercise_minutes")));
            log.setWaterMl(cursor.getInt(cursor.getColumnIndexOrThrow("water_ml")));
            log.setSleepHours(cursor.getFloat(cursor.getColumnIndexOrThrow("sleep_hours")));
            log.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        }
        cursor.close();
        return log;
    }

    public DailyTarget getDailyTargetByDate(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Daily_Targets WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), date});

        DailyTarget target = null;

        if (cursor != null && cursor.moveToFirst()) {
            target = new DailyTarget();
            target.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
            target.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            target.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            target.setTargetSteps(cursor.getInt(cursor.getColumnIndexOrThrow("target_steps")));
            target.setTargetExercise(cursor.getInt(cursor.getColumnIndexOrThrow("target_exercise")));
            target.setTargetWaterMl(cursor.getInt(cursor.getColumnIndexOrThrow("target_water_ml")));
            target.setTargetSleepHr(cursor.getFloat(cursor.getColumnIndexOrThrow("target_sleep_hr")));
        }

        if (cursor != null) cursor.close();
        return target;
    }


    public ActivityLog getActivityLogByDate(long userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Activity_Log WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), date});

        ActivityLog log = null;

        if (cursor != null && cursor.moveToFirst()) {
            log = new ActivityLog();
            log.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
            log.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            log.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            log.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow("steps")));
            log.setExerciseMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("exercise_minutes")));
            log.setWaterMl(cursor.getInt(cursor.getColumnIndexOrThrow("water_ml")));
            log.setSleepHours(cursor.getFloat(cursor.getColumnIndexOrThrow("sleep_hours")));
            log.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        }

        if (cursor != null) cursor.close();
        return log;
    }



}
