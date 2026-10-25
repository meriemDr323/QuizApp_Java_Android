package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class StatisticsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView btnMenu;
    TextView tvTotalGames, tvBestScore, tvAvgScore;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        setContentView(R.layout.activity_statistics);

        sessionManager = new SessionManager(this);

        // Initialiser les vues
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btnMenu);
        tvTotalGames = findViewById(R.id.tvTotalGames);
        tvBestScore = findViewById(R.id.tvBestScore);
        tvAvgScore = findViewById(R.id.tvAvgScore);

        // Mettre à jour le header du Drawer
        updateNavigationHeader();

        // Ouvrir le drawer avec le bouton menu
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Gérer les clics du menu drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // 🔥 Retour à la page de sélection
                startActivity(new Intent(StatisticsActivity.this, SelectionActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(StatisticsActivity.this, ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(StatisticsActivity.this, SettingsActivity.class));
                finish();
            } else if (id == R.id.nav_stats) {
                Toast.makeText(this, "Vous êtes déjà sur Statistiques", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // TODO: Récupérer les stats depuis Firebase
        tvTotalGames.setText("15 parties");
        tvBestScore.setText("100%");
        tvAvgScore.setText("78%");
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
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
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