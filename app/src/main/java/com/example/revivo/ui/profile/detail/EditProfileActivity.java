package com.example.revivo.ui.profile.detail;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.database.DatabaseContract;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etName, etEmail, etWeight, etHeight, etBirthDate;
    private Spinner spGender;
    private TextView btnSave;
    private ImageView ivBack;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        initDatabase();
        setupSpinner();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etWeight = findViewById(R.id.et_weight);
        etHeight = findViewById(R.id.et_height);
        etBirthDate = findViewById(R.id.et_birth_date);
        spGender = findViewById(R.id.sp_gender);
        btnSave = findViewById(R.id.btn_save);
        ivBack = findViewById(R.id.iv_back);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        currentUserId = (int) sharedPreferences.getLong("user_id", -1L);
    }

    private void setupSpinner() {
        String[] genders = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);
    }

    private void loadUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Users.COLUMN_NAME,
                DatabaseContract.Users.COLUMN_EMAIL,
                DatabaseContract.Users.COLUMN_WEIGHT_KG,
                DatabaseContract.Users.COLUMN_HEIGHT_CM,
                DatabaseContract.Users.COLUMN_GENDER,
                DatabaseContract.Users.COLUMN_BIRTH_DATE
        };

        String selection = DatabaseContract.Users._ID + " = ?";
        String[] selectionArgs = {String.valueOf(currentUserId)};

        Cursor cursor = db.query(
                DatabaseContract.Users.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_NAME)));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_EMAIL)));
            etWeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_WEIGHT_KG))));
            etHeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_HEIGHT_CM))));
            etBirthDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_BIRTH_DATE)));

            String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_GENDER));
            if ("Female".equals(gender)) {
                spGender.setSelection(1);
            } else {
                spGender.setSelection(0);
            }

            cursor.close();
        }

        db.close();
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void saveUserData() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Users.COLUMN_NAME, name);
            values.put(DatabaseContract.Users.COLUMN_EMAIL, email);
            values.put(DatabaseContract.Users.COLUMN_WEIGHT_KG, weight);
            values.put(DatabaseContract.Users.COLUMN_HEIGHT_CM, height);
            values.put(DatabaseContract.Users.COLUMN_GENDER, gender);
            values.put(DatabaseContract.Users.COLUMN_BIRTH_DATE, birthDate);

            String selection = DatabaseContract.Users._ID + " = ?";
            String[] selectionArgs = {String.valueOf(currentUserId)};

            int rowsUpdated = db.update(
                    DatabaseContract.Users.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );

            db.close();

            if (rowsUpdated > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for weight and height", Toast.LENGTH_SHORT).show();
        }
    }
}