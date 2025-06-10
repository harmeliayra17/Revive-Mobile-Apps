package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DailyTargetsHelper {
    private static final String TABLE = DatabaseContract.DailyTargets.TABLE_NAME;
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DailyTargetsHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(long userId, String date, int steps, int exerciseMin, int waterMl, float sleepHr) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DailyTargets.COLUMN_USER_ID, userId);
        cv.put(DatabaseContract.DailyTargets.COLUMN_DATE, date);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS, steps);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE, exerciseMin);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML, waterMl);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR, sleepHr);
        return db.insert(TABLE, null, cv);
    }

    public int update(long id, int steps, int exerciseMin, int waterMl, float sleepHr) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_STEPS, steps);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_EXERCISE, exerciseMin);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_WATER_ML, waterMl);
        cv.put(DatabaseContract.DailyTargets.COLUMN_TARGET_SLEEP_HR, sleepHr);
        return db.update(TABLE, cv, DatabaseContract.DailyTargets._ID + "=?", new String[]{ String.valueOf(id) });
    }

    public int delete(long id) {
        return db.delete(TABLE, DatabaseContract.DailyTargets._ID + "=?", new String[]{ String.valueOf(id) });
    }

    public Cursor queryByUserAndDate(long userId, String date) {
        return db.query(TABLE, null,
                DatabaseContract.DailyTargets.COLUMN_USER_ID + "=? AND " + DatabaseContract.DailyTargets.COLUMN_DATE + "=?",
                new String[]{ String.valueOf(userId), date }, null, null, null);
    }

    public Cursor queryAll() {
        return db.query(TABLE, null, null, null, null, null,
                DatabaseContract.DailyTargets.COLUMN_DATE + " ASC");
    }
}
