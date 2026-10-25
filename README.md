# QuizApp Java Android

Une application Android de quiz interactive développée en Java, avec Firebase comme backend.

## Fonctionnalités

- Authentification utilisateur (inscription / connexion) avec session persistante
- Sélection du thème : Géographie, Science, Histoire, Sports, Technologie
- Sélection de la difficulté : Facile, Moyen, Difficile
- Mode chronomètre optionnel (30 secondes par question)
- Questions chargées dynamiquement depuis Firebase Firestore
- Support des images par question via Glide
- Réponse vocale via reconnaissance vocale Android
- Indices et explications après chaque réponse
- Effets sonores (bonne / mauvaise réponse)
- Écran de score avec pourcentage et barre de progression
- Navigation latérale (Drawer) avec accès au profil, paramètres et statistiques
- Paramètres de langue (Français, Anglais, Arabe) et de thème (Clair / Sombre)

## Stack technique

- **Langage** : Java
- **SDK** : Android API 26+ (min) / 34 (target)
- **Base de données** : Firebase Firestore
- **Authentification** : Firebase Auth + SessionManager (SharedPreferences)
- **Chargement d'images** : Glide 4.16
- **UI** : Material Design, DrawerLayout, NavigationView

## Structure du projet

```
app/src/main/java/com/example/quiz/
├── MainActivity.java          # Écran de connexion
├── Register.java              # Écran d'inscription
├── SelectionActivity.java     # Sélection thème / difficulté / chrono
├── QuizDynamicActivity.java   # Déroulement du quiz
├── Score.java                 # Écran de résultat
├── ProfileActivity.java       # Profil utilisateur
├── SettingsActivity.java      # Paramètres langue et thème
├── StatisticsActivity.java    # Statistiques de jeu
├── SessionManager.java        # Gestion de session
├── PreferencesManager.java    # Gestion langue et thème
└── Question.java              # Modèle de données Firestore
```

## Configuration Firebase

1. Créer un projet sur [Firebase Console](https://console.firebase.google.com/)
2. Ajouter une application Android avec le package `com.example.quiz`
3. Télécharger `google-services.json` et le placer dans `app/`
4. Activer Firestore et créer une collection `questions` avec les champs :
   - `question`, `option1`, `option2`, `option3`, `correctAnswer`
   - `hint`, `explanation`, `theme`, `difficulty`, `image` (URL optionnelle)

## Lancer le projet

1. Cloner le repo
2. Ouvrir dans Android Studio
3. Placer votre `google-services.json` dans `app/`
4. Lancer sur un appareil ou émulateur Android 8.0+
