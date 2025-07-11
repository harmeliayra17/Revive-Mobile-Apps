package com.example.revivo.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.database.DatabaseContract;
import com.example.revivo.SignInActivity;
import com.example.revivo.ui.profile.detail.EditProfileActivity;
import com.example.revivo.ui.profile.detail.MyTargetsActivity;
import com.example.revivo.ui.profile.detail.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvUserName, tvWeight, tvHeight, tvAge;
    private TextView btnEdit;
    private LinearLayout cvMyTarget, cvSettings, cvLogout;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        initDatabase();
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvHeight = view.findViewById(R.id.tv_height);
        btnEdit = view.findViewById(R.id.btn_edit);
        cvMyTarget = view.findViewById(R.id.cv_my_target);
        cvSettings = view.findViewById(R.id.cv_settings);
        cvLogout = view.findViewById(R.id.cv_logout);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences("user_session", getContext().MODE_PRIVATE);
        currentUserId = (int) sharedPreferences.getLong("user_id", -1L);
    }

    private void loadUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.Users.COLUMN_NAME,
                DatabaseContract.Users.COLUMN_WEIGHT_KG,
                DatabaseContract.Users.COLUMN_HEIGHT_CM,
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
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_NAME));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_WEIGHT_KG));
            double height = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_HEIGHT_CM));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_BIRTH_DATE));

            tvUserName.setText(name != null ? name : "User");
            tvWeight.setText(String.format(Locale.getDefault(), "%.0f kg", weight));
            tvHeight.setText(String.format(Locale.getDefault(), "%.0f cm", height));

            cursor.close();
        }

        db.close();
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        cvMyTarget.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyTargetsActivity.class);
            startActivity(intent);
        });

        cvSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        cvLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        // Clear session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Refresh data when returning from edit
    }
}