package com.example.quiz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class PreferencesManager {

    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_THEME = "theme";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public PreferencesManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // 🔥 SAUVEGARDER LA LANGUE
    public void saveLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    // 🔥 RÉCUPÉRER LA LANGUE
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "fr"); // Français par défaut
    }

    // 🔥 APPLIQUER LA LANGUE
    public void applyLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        context.createConfigurationContext(config);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    // 🔥 SAUVEGARDER LE THÈME
    public void saveTheme(String theme) {
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    // 🔥 RÉCUPÉRER LE THÈME
    public String getTheme() {
        return prefs.getString(KEY_THEME, "light"); // Clair par défaut
    }

    // 🔥 APPLIQUER LE THÈME
    public void applyTheme(String theme) {
        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}