package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView btnMenu;
    Spinner spinnerLanguage, spinnerTheme;
    Button btnSaveSettings;
    PreferencesManager preferencesManager;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btnMenu);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerTheme = findViewById(R.id.spinnerTheme);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Mettre à jour le header du Drawer
        updateNavigationHeader();

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Configuration des spinners
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);

        // Charger les préférences actuelles
        String currentLanguage = preferencesManager.getLanguage();
        String currentTheme = preferencesManager.getTheme();

        if (currentLanguage.equals("fr")) {
            spinnerLanguage.setSelection(0);
        } else if (currentLanguage.equals("en")) {
            spinnerLanguage.setSelection(1);
        } else if (currentLanguage.equals("ar")) {
            spinnerLanguage.setSelection(2);
        }

        if (currentTheme.equals("light")) {
            spinnerTheme.setSelection(0);
        } else {
            spinnerTheme.setSelection(1);
        }

        // Sauvegarder les paramètres
        btnSaveSettings.setOnClickListener(v -> {
            String selectedLanguage = spinnerLanguage.getSelectedItem().toString();
            String selectedTheme = spinnerTheme.getSelectedItem().toString();

            String langCode = "fr";
            if (selectedLanguage.equals("English")) langCode = "en";
            else if (selectedLanguage.equals("العربية")) langCode = "ar";

            String themeCode = selectedTheme.equals("Light") ? "light" : "dark";

            preferencesManager.saveLanguage(langCode);
            preferencesManager.saveTheme(themeCode);

            Toast.makeText(this, "Paramètres sauvegardés !", Toast.LENGTH_SHORT).show();
            recreate();
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // 🔥 Retour à la page de sélection
                startActivity(new Intent(SettingsActivity.this, SelectionActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Vous êtes déjà sur Paramètres", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Mettre à jour le header du Drawer
    private void updateNavigationHeader() {
        // HEADER (en haut)
        View headerView = navigationView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        TextView tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);

        tvHeaderName.setText(sessionManager.getUserName());
        tvHeaderEmail.setText(sessionManager.getUserEmail());

        // FOOTER (bouton déconnexion en bas)
        Button btnLogout = navigationView.findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.logout();
                Toast.makeText(this, "Déconnexion réussie !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}