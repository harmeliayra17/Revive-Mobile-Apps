package com.example.revivo.data.local.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                        DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS + " INTEGER, " +
                        DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE + " INTEGER, " +
                        DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML + " INTEGER, " +
                        DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR + " REAL, " +
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
}
