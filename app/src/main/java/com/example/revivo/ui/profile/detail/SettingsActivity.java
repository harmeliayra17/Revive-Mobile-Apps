package com.example.revivo.ui.profile.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.revivo.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchTheme;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Terapkan theme sesuai preferensi sebelum setContentView
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchTheme = findViewById(R.id.switch_theme);
        ivBack = findViewById(R.id.iv_back);

        // Set Switch sesuai preferensi
        switchTheme.setChecked(isDark);

        // Listener untuk Switch
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Simpan preferensi
            prefs.edit().putBoolean("dark_mode", isChecked).apply();

            // Opsional: restart activity biar efek langsung
            recreate();
        });

        // Tombol kembali
        ivBack.setOnClickListener(v -> finish());
    }
}