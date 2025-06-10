package com.example.revivo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.revivo.data.local.database.UsersHelper;
import com.example.revivo.data.local.model.Users;

public class ActivityLevelActivity extends AppCompatActivity {

    private LinearLayout layoutSedentary, layoutLightlyActive, layoutModeratelyActive, layoutVeryActive;
    private TextView btnNext;
    private ImageView btnBack;
    private String selectedActivityLevel = "";
    private String fullName, email, password, gender, birthDate;
    private float height, weight;
    private UsersHelper usersHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        // Get data from previous activity
        fullName = getIntent().getStringExtra("full_name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        gender = getIntent().getStringExtra("gender");
        birthDate = getIntent().getStringExtra("birth_date");
        height = getIntent().getFloatExtra("height", 0);
        weight = getIntent().getFloatExtra("weight", 0);

        initViews();
        initDatabase();
        setListeners();
    }

    private void initViews() {
        layoutSedentary = findViewById(R.id.layout_sedentary);
        layoutLightlyActive = findViewById(R.id.layout_lightly_active);
        layoutModeratelyActive = findViewById(R.id.layout_moderately_active);
        layoutVeryActive = findViewById(R.id.layout_very_active);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initDatabase() {
        usersHelper = new UsersHelper(this);
        usersHelper.open();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        layoutSedentary.setOnClickListener(v -> {
            selectActivityLevel("Sedentary");
            updateSelection(layoutSedentary);
        });

        layoutLightlyActive.setOnClickListener(v -> {
            selectActivityLevel("Lightly Active");
            updateSelection(layoutLightlyActive);
        });

        layoutModeratelyActive.setOnClickListener(v -> {
            selectActivityLevel("Moderately Active");
            updateSelection(layoutModeratelyActive);
        });

        layoutVeryActive.setOnClickListener(v -> {
            selectActivityLevel("Very Active");
            updateSelection(layoutVeryActive);
        });

        btnNext.setOnClickListener(v -> saveUserAndFinish());
    }

    private void selectActivityLevel(String level) {
        selectedActivityLevel = level;
    }

    private void updateSelection(LinearLayout selectedLayout) {
        // Reset all backgrounds
        layoutSedentary.setBackgroundResource(R.drawable.activity_option_background);
        layoutLightlyActive.setBackgroundResource(R.drawable.activity_option_background);
        layoutModeratelyActive.setBackgroundResource(R.drawable.activity_option_background);
        layoutVeryActive.setBackgroundResource(R.drawable.activity_option_background);

        // Highlight selected option
        selectedLayout.setBackgroundResource(R.drawable.activity_option_selected_background);
    }

    private void saveUserAndFinish() {
        if (selectedActivityLevel.isEmpty()) {
            Toast.makeText(this, "Please select an activity level", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user object
        Users user = new Users();
        user.setName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setGender(gender);
        user.setBirthDate(birthDate);
        user.setHeightCm(height);
        user.setWeightKg(weight);
        user.setActivityLevel(selectedActivityLevel);
        user.setCreatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));

        // Save to database
        long result = usersHelper.insert(user);

        if (result > 0) {
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

            // Save user session
            saveUserSession(result);

            // Go to MainActivity
            Intent intent = new Intent(ActivityLevelActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserSession(long userId) {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .putLong("user_id", userId)
                .putBoolean("is_logged_in", true)
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usersHelper != null) {
            usersHelper.close();
        }
    }
}