package com.example.quiz;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Créer une session de connexion
    public void createLoginSession(String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    // Vérifier si l'utilisateur est connecté
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Obtenir l'email de l'utilisateur
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    // Obtenir le nom de l'utilisateur
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    // Déconnexion
    public void logout() {
        editor.clear();
        editor.apply();
    }
}