package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ExerciseLogHelper {
    private static final String TABLE = DatabaseContract.ExerciseLog.TABLE_NAME;
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ExerciseLogHelper(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public long insert(long userId, String exerciseId, String exerciseName, double durationMin) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ExerciseLog.COLUMN_USER_ID, userId);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_EXERCISE_NAME, exerciseName);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_DURATION_MIN, durationMin);
        // completed_at otomatis
        return db.insert(TABLE, null, cv);
    }

    public Cursor queryByDate(String userId, String date) {
        return db.query(
                TABLE, null,
                DatabaseContract.ExerciseLog.COLUMN_USER_ID + "=? AND DATE(" +
                        DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT + ")=?",
                new String[]{ userId, date },
                null, null, DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT + " ASC"
        );
    }

    public int deleteById(long id) {
        return db.delete(TABLE, DatabaseContract.ExerciseLog._ID + "=?",
                new String[]{ String.valueOf(id) });
    }

    // Contoh penghitungan total durasi hari ini
    public int sumDurationByDate(long userId, String date) {
        Cursor c = db.rawQuery(
                "SELECT SUM(" + DatabaseContract.ExerciseLog.COLUMN_DURATION_MIN + ") " +
                        "FROM " + TABLE +
                        " WHERE " + DatabaseContract.ExerciseLog.COLUMN_USER_ID + "=? " +
                        "AND DATE(" + DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT + ")=?",
                new String[]{String.valueOf(userId), date }
        );
        int sum = 0;
        if (c.moveToFirst()) sum = c.getInt(0);
        c.close();
        return sum;
    }
}
