package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * Insert exercise log, always set completed_at (yyyy-MM-dd HH:mm:ss)
     */
    public long insert(long userId, String exerciseId, String exerciseName, double durationMin) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ExerciseLog.COLUMN_USER_ID, userId);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_EXERCISE_ID, exerciseId);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_EXERCISE_NAME, exerciseName);
        cv.put(DatabaseContract.ExerciseLog.COLUMN_DURATION_MIN, durationMin);

        // Set completed_at sebagai string tanggal sekarang
        String completedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        cv.put(DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT, completedAt);

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

    /**
     * Sum all duration for user and date (date: yyyy-MM-dd)
     */
    public int sumDurationByDate(long userId, String date) {
        int sum = 0;
        Cursor c = db.rawQuery(
                "SELECT SUM(" + DatabaseContract.ExerciseLog.COLUMN_DURATION_MIN + ") " +
                        "FROM " + TABLE +
                        " WHERE " + DatabaseContract.ExerciseLog.COLUMN_USER_ID + "=? " +
                        "AND DATE(" + DatabaseContract.ExerciseLog.COLUMN_COMPLETED_AT + ")=?",
                new String[]{String.valueOf(userId), date }
        );
        if (c != null) {
            if (c.moveToFirst() && !c.isNull(0)) sum = c.getInt(0);
            c.close();
        }
        return sum;
    }
}