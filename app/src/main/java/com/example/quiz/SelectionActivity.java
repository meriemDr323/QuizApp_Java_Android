package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SelectionActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView btnMenu;
    RadioGroup rgTheme, rgDifficulty, rgTimer;
    Button btnStartQuiz;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        setContentView(R.layout.activity_selection);

        sessionManager = new SessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btnMenu);
        rgTheme = findViewById(R.id.rgTheme);
        rgDifficulty = findViewById(R.id.rgDifficulty);
        rgTimer = findViewById(R.id.rgTimer);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        // 🔥 Mettre à jour le header du Drawer
        updateNavigationHeader();

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // 🔥 Navigation menu (sans déconnexion)
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // 🔥 Déjà sur la page de sélection
                Toast.makeText(this, "Vous êtes déjà sur la page de sélection", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SelectionActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(SelectionActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(SelectionActivity.this, StatisticsActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        btnStartQuiz.setOnClickListener(v -> {
            int selectedThemeId = rgTheme.getCheckedRadioButtonId();
            if (selectedThemeId == -1) {
                Toast.makeText(this, "Veuillez choisir un thème !", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedTheme = findViewById(selectedThemeId);
            String theme = selectedTheme.getTag().toString();

            int selectedDifficultyId = rgDifficulty.getCheckedRadioButtonId();
            if (selectedDifficultyId == -1) {
                Toast.makeText(this, "Veuillez choisir une difficulté !", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedDifficulty = findViewById(selectedDifficultyId);
            String difficulty = selectedDifficulty.getTag().toString();

            int selectedTimerId = rgTimer.getCheckedRadioButtonId();
            boolean timerEnabled = (selectedTimerId == R.id.rbWithTimer);

            Intent intent = new Intent(SelectionActivity.this, QuizDynamicActivity.class);
            intent.putExtra("theme", theme);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("timerEnabled", timerEnabled);
            startActivity(intent);
        });
    }

    // 🔥 Mettre à jour le header du Drawer
    private void updateNavigationHeader() {
        // 🔥 HEADER (en haut)
        View headerView = navigationView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        TextView tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);

        tvHeaderName.setText(sessionManager.getUserName());
        tvHeaderEmail.setText(sessionManager.getUserEmail());

        // 🔥 FOOTER (bouton déconnexion en bas)
        View footerView = navigationView.findViewById(R.id.btnLogout);
        if (footerView != null) {
            footerView.setOnClickListener(v -> {
                sessionManager.logout();
                Toast.makeText(this, "Déconnexion réussie !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }
}