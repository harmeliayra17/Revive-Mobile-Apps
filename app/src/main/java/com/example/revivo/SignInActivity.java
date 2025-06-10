package com.example.revivo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.revivo.data.local.database.DatabaseContract;
import com.example.revivo.data.local.database.UsersHelper;

public class SignInActivity extends AppCompatActivity {

    private EditText etFullName, etPassword;
    private TextView btnSignIn;
    private TextView tvCreateAccount, tvForgotPassword;
    private ImageView btnBack, btnShowPassword;
    private UsersHelper usersHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
        initDatabase();
        setListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvCreateAccount = findViewById(R.id.tv_create_account);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        btnBack = findViewById(R.id.btn_back);
        btnShowPassword = findViewById(R.id.btn_show_password);
    }

    private void initDatabase() {
        usersHelper = new UsersHelper(this);
        usersHelper.open();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnShowPassword.setOnClickListener(v -> togglePasswordVisibility());

        btnSignIn.setOnClickListener(v -> signIn());

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            // Handle forgot password
            Toast.makeText(this, "Forgot password functionality", Toast.LENGTH_SHORT).show();
        });
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

    private void signIn() {
        String email = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etFullName.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        // Check user credentials
        Cursor cursor = usersHelper.queryAll();
        boolean isUserFound = false;

        if (cursor.moveToFirst()) {
            do {
                String dbEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_EMAIL));
                String dbPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.Users.COLUMN_PASSWORD));

                if (dbEmail.equals(email) && dbPassword.equals(password)) {
                    isUserFound = true;
                    // Save user session (you can use SharedPreferences)
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Users._ID));
                    saveUserSession(userId);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (isUserFound) {
            Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
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