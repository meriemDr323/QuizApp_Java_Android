package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText mail, pswd;
    Button btnsignin;
    TextView textView, tvRegisterLink;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        // 🔥 Vérifier si l'utilisateur est déjà connecté
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            Intent i = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(i);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialiser les vues
        mail = findViewById(R.id.mail);
        pswd = findViewById(R.id.pswd);
        btnsignin = findViewById(R.id.btnsignin);
        textView = findViewById(R.id.textView);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Bouton "S'inscrire" dans la TopBar
        textView.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Register.class);
            startActivity(i);
            finish();
        });

        // Lien "Pas encore inscrit ? Rejoins-nous !"
        tvRegisterLink.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Register.class);
            startActivity(i);
            finish();
        });

        // Bouton Se connecter
        btnsignin.setOnClickListener(v -> {
            String userMail = mail.getText().toString().trim();
            String userPswd = pswd.getText().toString().trim();

            // 🔥 VALIDATION AMÉLIORÉE
            if (userMail.isEmpty()) {
                Toast.makeText(MainActivity.this, "Veuillez entrer votre email !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userMail).matches()) {
                Toast.makeText(MainActivity.this, "Email invalide !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPswd.isEmpty()) {
                Toast.makeText(MainActivity.this, "Veuillez entrer votre mot de passe !", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPswd.length() < 6) {
                Toast.makeText(MainActivity.this, "Le mot de passe doit contenir au moins 6 caractères !", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 Créer la session
            sessionManager.createLoginSession(userMail, "Utilisateur");

            Toast.makeText(MainActivity.this, "Connexion réussie ! 🎉", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(MainActivity.this, SelectionActivity.class);
            startActivity(i);
            finish();
        });
    }
}