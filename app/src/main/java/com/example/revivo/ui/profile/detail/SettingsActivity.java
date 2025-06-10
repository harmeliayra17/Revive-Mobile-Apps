package com.example.revivo.ui.profile.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.revivo.R;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgTheme;
    private RadioButton rbLight, rbDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved theme before setting content view
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rgTheme = findViewById(R.id.rg_theme);
        rbLight = findViewById(R.id.rb_light);
        rbDark = findViewById(R.id.rb_dark);

        // Set selected state based on saved preference
        boolean isDark = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("dark_mode", false);

        if (isDark) rbDark.setChecked(true);
        else rbLight.setChecked(true);

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            boolean selectedDark = checkedId == R.id.rb_dark;
            AppCompatDelegate.setDefaultNightMode(
                    selectedDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Save preference
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", selectedDark)
                    .apply();

            recreate(); // Restart to apply theme
        });
    }
}
