package com.example.revivo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.revivo.R;

public class SuccessActivity extends AppCompatActivity {

    private TextView btnSetProfile;
    private String fullName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Get data from previous activity
        fullName = getIntent().getStringExtra("full_name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        initViews();
        setListeners();
    }

    private void initViews() {
        btnSetProfile = findViewById(R.id.btn_set_profile);
    }

    private void setListeners() {
        btnSetProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, PersonalInfoActivity.class);
            intent.putExtra("full_name", fullName);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        });
    }
}