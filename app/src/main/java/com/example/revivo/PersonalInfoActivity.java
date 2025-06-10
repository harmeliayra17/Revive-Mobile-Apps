package com.example.revivo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PersonalInfoActivity extends AppCompatActivity {

    private final Calendar dateCalendar = Calendar.getInstance();
    private Spinner spinnerGender;
    private EditText etBirthDate, etHeight, etWeight;
    private TextView btnNext;
    private ImageView btnBack;
    private String fullName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Ambil data dari activity sebelumnya
        fullName = getIntent().getStringExtra("full_name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        initViews();
        setupSpinner();
        setListeners();
    }

    private void initViews() {
        spinnerGender = findViewById(R.id.spinner_gender);
        etBirthDate = findViewById(R.id.et_birth_date);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupSpinner() {
        String[] genderOptions = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genderOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setPrompt("Select Gender");
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());

        etBirthDate.setOnClickListener(v -> showDatePicker());

        btnNext.setOnClickListener(v -> validateAndContinue());
    }

    private void showDatePicker() {
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) -> {
                    dateCalendar.set(Calendar.YEAR, y);
                    dateCalendar.set(Calendar.MONTH, m);
                    dateCalendar.set(Calendar.DAY_OF_MONTH, d);
                    updateBirthDateField();
                }, year, month, day
        );
        picker.show();
    }

    private void updateBirthDateField() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        etBirthDate.setText(sdf.format(dateCalendar.getTime()));
    }

    private void validateAndContinue() {
        String gender = spinnerGender.getSelectedItem().toString();
        String birthDate = etBirthDate.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (birthDate.isEmpty()) {
            etBirthDate.setError("Birth date is required");
            return;
        }

        if (height.isEmpty()) {
            etHeight.setError("Height is required");
            return;
        }

        if (weight.isEmpty()) {
            etWeight.setError("Weight is required");
            return;
        }

        Intent intent = new Intent(PersonalInfoActivity.this, ActivityLevelActivity.class);
        intent.putExtra("full_name", fullName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("gender", gender);
        intent.putExtra("birth_date", birthDate);
        intent.putExtra("height", Float.parseFloat(height));
        intent.putExtra("weight", Float.parseFloat(weight));
        startActivity(intent);
    }
}
