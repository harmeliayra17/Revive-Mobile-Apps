package com.example.revivo.ui.add;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.revivo.R;
import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.data.local.model.ActivityLog;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivityLogFragment extends Fragment {

    private EditText etSteps, etWaterIntake, etSleepDuration;
    private Button btnSave;

    public AddActivityLogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_activity_log, container, false);

        // Inisialisasi input field
        etSteps = view.findViewById(R.id.et_steps);
        etWaterIntake = view.findViewById(R.id.et_water_intake);
        etSleepDuration = view.findViewById(R.id.et_sleep_duration);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> saveLog());

        return view;
    }

    private void saveLog() {
        String stepStr = etSteps.getText().toString();
        String waterStr = etWaterIntake.getText().toString();
        String sleepStr = etSleepDuration.getText().toString();

        if (TextUtils.isEmpty(stepStr) || TextUtils.isEmpty(waterStr) || TextUtils.isEmpty(sleepStr)) {
            Toast.makeText(getContext(), "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int steps = Integer.parseInt(stepStr);
            int water = Integer.parseInt(waterStr);
            float sleep = Float.parseFloat(sleepStr);

            long userId = requireActivity()
                    .getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    .getLong("user_id", -1);

            if (userId == -1) {
                Toast.makeText(getContext(), "User tidak dikenali", Toast.LENGTH_SHORT).show();
                return;
            }

            // Buat objek ActivityLog
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            log.setSteps(steps);
            log.setWaterMl(water);
            log.setSleepHours(sleep);
            log.setUpdatedAt(new Date().toString());

            // Simpan ke database
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.insertActivityLog(log);

            Toast.makeText(getContext(), "Log berhasil disimpan", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format input tidak valid", Toast.LENGTH_SHORT).show();
        }
    }
}
