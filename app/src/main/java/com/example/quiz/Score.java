package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Score extends AppCompatActivity {

    ProgressBar pb;
    TextView tvscore, btnMenu;
    Button btnLogOut;
    Button btnTryAgain;
    int score;
    int total;

    // Drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        setContentView(R.layout.activity_score);

        sessionManager = new SessionManager(this);

        pb = findViewById(R.id.progressBar);
        tvscore = findViewById(R.id.tv_score);
        btnLogOut = findViewById(R.id.button2);
        btnTryAgain = findViewById(R.id.button);
        btnMenu = findViewById(R.id.btnMenu);

        // DRAWER
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Mettre à jour le header du Drawer
        updateNavigationHeader();

        // Ouvrir le drawer avec le bouton menu
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Gérer les clics du menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // 🔥 Retour à la page de sélection
                startActivity(new Intent(Score.this, SelectionActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(Score.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(Score.this, SettingsActivity.class));
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(Score.this, StatisticsActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        Intent i1 = getIntent();
        score = i1.getIntExtra("score", 0);
        total = i1.getIntExtra("total", 5);

        int percentage = (total > 0) ? (score * 100 / total) : 0;

        tvscore.setText(percentage + "%");
        pb.setProgress(percentage);

        // BOUTON RÉESSAYER
        btnTryAgain.setOnClickListener(view -> {
            Toast.makeText(Score.this,
                    "Choisissez un nouveau quiz ! 🎯",
                    Toast.LENGTH_SHORT).show();

            Intent i2 = new Intent(getApplicationContext(), SelectionActivity.class);
            startActivity(i2);
            finish();
        });

        // BOUTON DÉCONNEXION
        btnLogOut.setOnClickListener(view -> {
            sessionManager.logout();
            Toast.makeText(Score.this,
                    "Déconnexion réussie ! 👋",
                    Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
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
                Intent intent = new Intent(Score.this, MainActivity.class);
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
            // Rediriger vers SelectionActivity au lieu de fermer l'app
            Intent intent = new Intent(Score.this, SelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}