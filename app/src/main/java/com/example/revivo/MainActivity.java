package com.example.revivo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.ui.add.AddActivityLogFragment;
import com.example.revivo.ui.exercise.ExerciseFragment;
import com.example.revivo.ui.home.HomeFragment;
import com.example.revivo.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek login
        if (!isUserLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Inisialisasi database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();

        // Inisialisasi komponen
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        // Default fragment saat pertama kali dibuka
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.nav_exercise) {
                    fragment = new ExerciseFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                } else if (itemId == R.id.nav_add ) {
                    fragment = new AddActivityLogFragment();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });


    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void logout() {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
