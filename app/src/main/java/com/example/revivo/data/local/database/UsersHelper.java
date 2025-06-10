package com.example.revivo.data.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.revivo.data.local.model.Users;

public class UsersHelper {
    private static final String TABLE = DatabaseContract.Users.TABLE_NAME;
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UsersHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(Users user) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Users.COLUMN_NAME, user.getName());
        cv.put(DatabaseContract.Users.COLUMN_EMAIL, user.getEmail());
        cv.put(DatabaseContract.Users.COLUMN_PASSWORD, user.getPassword());
        cv.put(DatabaseContract.Users.COLUMN_GENDER, user.getGender());
        cv.put(DatabaseContract.Users.COLUMN_BIRTH_DATE, user.getBirthDate());
        cv.put(DatabaseContract.Users.COLUMN_WEIGHT_KG, user.getWeightKg());
        cv.put(DatabaseContract.Users.COLUMN_HEIGHT_CM, user.getHeightCm());
        cv.put(DatabaseContract.Users.COLUMN_ACTIVITY_LEVEL, user.getActivityLevel());
        return db.insert(TABLE, null, cv);
    }

    public int update(long id, Users user) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Users.COLUMN_NAME, user.getName());
        cv.put(DatabaseContract.Users.COLUMN_EMAIL, user.getEmail());
        cv.put(DatabaseContract.Users.COLUMN_PASSWORD, user.getPassword());
        cv.put(DatabaseContract.Users.COLUMN_GENDER, user.getGender());
        cv.put(DatabaseContract.Users.COLUMN_BIRTH_DATE, user.getBirthDate());
        cv.put(DatabaseContract.Users.COLUMN_WEIGHT_KG, user.getWeightKg());
        cv.put(DatabaseContract.Users.COLUMN_HEIGHT_CM, user.getHeightCm());
        cv.put(DatabaseContract.Users.COLUMN_ACTIVITY_LEVEL, user.getActivityLevel());
        return db.update(TABLE, cv, DatabaseContract.Users._ID + "=?", new String[]{ String.valueOf(id) });
    }

    public int delete(long id) {
        return db.delete(TABLE, DatabaseContract.Users._ID + "=?", new String[]{ String.valueOf(id) });
    }

    public Cursor queryById(long id) {
        return db.query(TABLE, null, DatabaseContract.Users._ID + "=?",
                new String[]{ String.valueOf(id) }, null, null, null);
    }

    public Cursor queryAll() {
        return db.query(TABLE, null, null, null, null, null,
                DatabaseContract.Users._ID + " ASC");
    }
}
