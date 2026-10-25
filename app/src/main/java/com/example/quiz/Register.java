package com.example.quiz;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    EditText etName, etMail, etPassword, etConfPassword;
    Button btnsignUp;
    TextView btnBack, btnLogin, tvLoginLink;
    FirebaseAuth myAuth;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        // Gradient rose → violet sur le titre
        TextView title = findViewById(R.id.registerTitle);
        title.post(() -> {
            float width = title.getPaint().measureText(title.getText().toString());
            LinearGradient gradient = new LinearGradient(
                    0, 0, width, 0,
                    new int[]{0xFFEC4899, 0xFF7C3AED},
                    null,
                    Shader.TileMode.CLAMP
            );
            title.getPaint().setShader(gradient);
            title.invalidate();
        });

        // Initialiser les vues
        etName = findViewById(R.id.name);
        etMail = findViewById(R.id.mail);
        etPassword = findViewById(R.id.pswd);
        etConfPassword = findViewById(R.id.confirmpswd);
        btnsignUp = findViewById(R.id.btnsignup);
        btnBack = findViewById(R.id.btnBack);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Bouton RETOUR (←)
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Bouton "Se connecter" dans la TopBar
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Lien "Déjà un compte ? Se connecter"
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Bouton INSCRIPTION
        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String mail = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confPass = etConfPassword.getText().toString().trim();

                // 🔥 VALIDATION AMÉLIORÉE
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(Register.this, "Veuillez entrer votre nom !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(Register.this, "Veuillez entrer votre email !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    Toast.makeText(Register.this, "Email invalide !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Veuillez entrer un mot de passe !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(Register.this, "Le mot de passe doit contenir au moins 6 caractères !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confPass)) {
                    Toast.makeText(Register.this, "Veuillez confirmer votre mot de passe !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confPass.equals(password)) {
                    Toast.makeText(Register.this, "Les mots de passe ne correspondent pas !", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔥 Créer la session
                sessionManager.createLoginSession(mail, name);

                Toast.makeText(Register.this, "Inscription réussie ! 🎉", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, SelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}