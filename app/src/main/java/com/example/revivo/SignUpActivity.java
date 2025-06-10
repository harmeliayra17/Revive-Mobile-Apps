package com.example.revivo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.revivo.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmailAddress, etPassword;
    private TextView btnContinue;
    private TextView tvSignIn;
    private ImageView btnBack, btnShowPassword;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        setListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etEmailAddress = findViewById(R.id.et_email_address);
        etPassword = findViewById(R.id.et_password);
        btnContinue = findViewById(R.id.btn_continue);
        tvSignIn = findViewById(R.id.tv_sign_in);
        btnBack = findViewById(R.id.btn_back);
        btnShowPassword = findViewById(R.id.btn_show_password);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> validateAndContinue());
        btnShowPassword.setOnClickListener(v -> togglePasswordVisibility());

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateAndContinue() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmailAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            return;
        }

        if (email.isEmpty()) {
            etEmailAddress.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailAddress.setError("Please enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Pass data to success activity
        Intent intent = new Intent(SignUpActivity.this, SuccessActivity.class);
        intent.putExtra("full_name", fullName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnShowPassword.setImageResource(R.drawable.ic_eye_off);
        } else {
            etPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnShowPassword.setImageResource(R.drawable.ic_eye);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }
}