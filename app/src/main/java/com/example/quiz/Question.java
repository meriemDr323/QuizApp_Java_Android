package com.example.quiz;

public class Question {

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String correctAnswer;
    private String hint;
    private String explanation;
    private String difficulty;
    private String theme;
    private String image;

    // Constructeur vide requis pour Firestore
    public Question() {
    }

    // Constructeur complet
    public Question(String question, String option1, String option2, String option3,
                    String correctAnswer, String hint, String explanation,
                    String difficulty, String theme, String image) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctAnswer = correctAnswer;
        this.hint = hint;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.theme = theme;
        this.image = image;
    }

    // 🔥 NOUVEAU : Constructeur simplifié pour Gemini AI (sans image, theme, difficulty)
    public Question(String question, String option1, String option2, String option3,
                    String correctAnswer, String hint, String explanation) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.correctAnswer = correctAnswer;
        this.hint = hint;
        this.explanation = explanation;
        this.difficulty = "";
        this.theme = "";
        this.image = "";
    }

    // Getters
    public String getQuestion() { return question; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getHint() { return hint; }
    public String getExplanation() { return explanation; }
    public String getDifficulty() { return difficulty; }
    public String getTheme() { return theme; }
    public String getImage() { return image; }

    // 🔥 NOUVEAU : Méthode pour obtenir toutes les options sous forme de tableau
    public String[] getOptions() {
        return new String[]{option1, option2, option3};
    }

    // 🔥 NOUVEAU : Méthode pour obtenir l'index de la bonne réponse
    public int getCorrectAnswerIndex() {
        if (correctAnswer.equals(option1)) return 0;
        if (correctAnswer.equals(option2)) return 1;
        if (correctAnswer.equals(option3)) return 2;
        return -1;
    }

    // Setters
    public void setQuestion(String question) { this.question = question; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
    public void setOption3(String option3) { this.option3 = option3; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setHint(String hint) { this.hint = hint; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setImage(String image) { this.image = image; }
}