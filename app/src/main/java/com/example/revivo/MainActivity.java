package com.example.revivo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.revivo.data.local.database.DatabaseHelper;
import com.example.revivo.ui.add.AddActivityLogFragment;
import com.example.revivo.ui.exercise.ExerciseFragment;
import com.example.revivo.ui.home.HomeFragment;
import com.example.revivo.ui.profile.ProfileFragment;
import com.example.revivo.ui.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserLoggedIn()) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);

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
                } else if (itemId == R.id.nav_Stats) {
                    fragment = new StatsFragment();
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                }

                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddActivityLogFragment());
            }
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
