package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ActivityLogHelper {
    private static final String TABLE = DatabaseContract.ActivityLog.TABLE_NAME;
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public ActivityLogHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return db;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(long userId, String date, int steps, double exerciseMin, int waterMl, float sleepHr) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ActivityLog.COLUMN_USER_ID, userId);
        cv.put(DatabaseContract.ActivityLog.COLUMN_DATE, date);
        cv.put(DatabaseContract.ActivityLog.COLUMN_STEPS, steps);
        cv.put(DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN, exerciseMin);
        cv.put(DatabaseContract.ActivityLog.COLUMN_WATER_ML, waterMl);
        cv.put(DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS, sleepHr);
        return db.insert(TABLE, null, cv);
    }

    public int update(long userId, String date, int steps, double exerciseMin, int waterMl, float sleepHr) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ActivityLog.COLUMN_STEPS, steps);
        cv.put(DatabaseContract.ActivityLog.COLUMN_EXERCISE_MIN, exerciseMin);
        cv.put(DatabaseContract.ActivityLog.COLUMN_WATER_ML, waterMl);
        cv.put(DatabaseContract.ActivityLog.COLUMN_SLEEP_HOURS, sleepHr);
        return db.update(TABLE, cv,
                DatabaseContract.ActivityLog.COLUMN_USER_ID + "=? AND " + DatabaseContract.ActivityLog.COLUMN_DATE + "=?",
                new String[]{ String.valueOf(userId), date });
    }

    public int delete(long id) {
        return db.delete(TABLE, DatabaseContract.ActivityLog._ID + "=?",
                new String[]{ String.valueOf(id) });
    }

    public Cursor queryByUserAndDate(long userId, String date) {
        return db.query(TABLE, null,
                DatabaseContract.ActivityLog.COLUMN_USER_ID + "=? AND " + DatabaseContract.ActivityLog.COLUMN_DATE + "=?",
                new String[]{ String.valueOf(userId), date }, null, null, null);
    }

    public Cursor queryAll() {
        return db.query(TABLE, null, null, null, null, null,
                DatabaseContract.ActivityLog.COLUMN_DATE + " ASC");
    }

}
