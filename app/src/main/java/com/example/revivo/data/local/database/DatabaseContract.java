package com.example.revivo.data.local.database;

import android.provider.BaseColumns;

public final class DatabaseContract {
    private DatabaseContract() {}

    public static final class Users implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password"; // Tidak di-hash
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_BIRTH_DATE = "birth_date";
        public static final String COLUMN_WEIGHT_KG = "weight_kg";
        public static final String COLUMN_HEIGHT_CM = "height_cm";
        public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    public static final class DailyTargets implements BaseColumns {
        public static final String TABLE_NAME = "Daily_Targets";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TARGET_STEPS = "target_steps";
        public static final String COLUMN_TARGET_EXERCISE = "target_exercise";
        public static final String COLUMN_TARGET_WATER_ML = "target_water_ml";
        public static final String COLUMN_TARGET_SLEEP_HR = "target_sleep_hr";
    }

    public static final class ActivityLog implements BaseColumns {
        public static final String TABLE_NAME = "Activity_Log";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_STEPS = "steps";
        public static final String COLUMN_EXERCISE_MIN = "exercise_minutes";
        public static final String COLUMN_WATER_ML = "water_ml";
        public static final String COLUMN_SLEEP_HOURS = "sleep_hours";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final class ExerciseLog implements BaseColumns {
        public static final String TABLE_NAME = "Exercise_Log";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";
        public static final String COLUMN_EXERCISE_NAME = "exercise_name";
        public static final String COLUMN_DURATION_MIN = "duration_min";
        public static final String COLUMN_COMPLETED_AT = "completed_at";
    }
}