package com.example.quiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class QuizDynamicActivity extends AppCompatActivity {

    // UI Elements
    TextView tvQuestionNumber, tvQuestion, tvHint, tvExplanation, tvResultTitle, tvResultIcon;
    TextView tvTimer;
    ImageView imageView;
    ImageButton btnHint, btnMic;
    RadioButton rbA, rbB, rbC;
    RadioGroup radioGroup;
    Button btnNext;
    LinearLayout hintContainer, explanationContainer, actionButtonsContainer;
    ProgressBar progressBar; // 🔥 NOUVEAU

    // Questions
    List<Question> questionList = new ArrayList<>();
    int currentQuestionIndex = 0;
    int score = 0;
    boolean answered = false;

    // Theme et Difficulty
    String selectedTheme;
    String selectedDifficulty;

    // Timer
    boolean withTimer;
    CountDownTimer countDownTimer;
    long timeLeftInMillis = 30000;

    // Sons
    MediaPlayer soundCorrect;
    MediaPlayer soundWrong;

    // 🔥 Gemini Service
    GeminiService geminiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        preferencesManager.applyLanguage(preferencesManager.getLanguage());
        preferencesManager.applyTheme(preferencesManager.getTheme());

        setContentView(R.layout.activity_quiz1);

        // Récupérer theme, difficulty et timer depuis Intent
        selectedTheme = getIntent().getStringExtra("theme");
        selectedDifficulty = getIntent().getStringExtra("difficulty");
        withTimer = getIntent().getBooleanExtra("timerEnabled", false);

        // 🔥 Initialiser Gemini Service
        geminiService = new GeminiService();

        // Initialiser UI
        initializeViews();

        // Initialiser les sons
        soundCorrect = MediaPlayer.create(this, R.raw.sound_correct);
        soundWrong = MediaPlayer.create(this, R.raw.sound_wrong);

        // 🔥 Charger questions depuis Gemini AI
        loadQuestionsFromGemini();

        // Setup Listeners
        setupListeners();
    }

    private void initializeViews() {
        tvQuestionNumber = findViewById(R.id.textView2);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        imageView = findViewById(R.id.imageView);
        btnHint = findViewById(R.id.btnHint);
        btnMic = findViewById(R.id.btnMic);
        radioGroup = findViewById(R.id.radioGroup);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        btnNext = findViewById(R.id.buttonnext);
        progressBar = findViewById(R.id.progressBar); // 🔥 NOUVEAU

        // Conteneurs
        hintContainer = findViewById(R.id.hintContainer);
        explanationContainer = findViewById(R.id.explanationContainer);
        actionButtonsContainer = findViewById(R.id.actionButtonsContainer);

        // TextViews
        tvHint = findViewById(R.id.tvHint);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultIcon = findViewById(R.id.tvResultIcon);

        // Afficher/Cacher le timer selon l'option
        if (!withTimer) {
            tvTimer.setVisibility(View.GONE);
        }
    }

    // 🔥 CHARGER LES QUESTIONS DEPUIS GEMINI AI
    private void loadQuestionsFromGemini() {
        // Afficher le loading
        progressBar.setVisibility(View.VISIBLE);
        tvQuestion.setText("🤖 Gemini AI génère vos questions...\n\nThème : " + selectedTheme + "\nDifficulté : " + selectedDifficulty);
        btnNext.setEnabled(false);
        btnHint.setEnabled(false);
        btnMic.setEnabled(false);
        radioGroup.setVisibility(View.GONE);
        actionButtonsContainer.setVisibility(View.GONE);

        // Générer les questions avec Gemini
        geminiService.generateQuiz(selectedTheme, selectedDifficulty, 10, new GeminiService.QuizGenerationCallback() {
            @Override
            public void onSuccess(List<Question> generatedQuestions) {
                runOnUiThread(() -> {
                    questionList = generatedQuestions;
                    progressBar.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.VISIBLE);
                    actionButtonsContainer.setVisibility(View.VISIBLE);
                    btnNext.setEnabled(true);
                    btnHint.setEnabled(true);
                    btnMic.setEnabled(true);

                    // Limiter à 5 questions
                    if (questionList.size() > 5) {
                        questionList = questionList.subList(0, 5);
                    }

                    displayQuestion();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(QuizDynamicActivity.this,
                            "Erreur : " + error,
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    private void displayQuestion() {

        if (currentQuestionIndex >= questionList.size()) {
            // Quiz terminé
            Intent intent = new Intent(this, Score.class);
            intent.putExtra("score", score);
            intent.putExtra("total", questionList.size());
            startActivity(intent);
            finish();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);

        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + "/" + questionList.size());
        tvQuestion.setText(currentQuestion.getQuestion());

        rbA.setText(currentQuestion.getOption1());
        rbB.setText(currentQuestion.getOption2());
        rbC.setText(currentQuestion.getOption3());

        rbA.setBackgroundResource(R.drawable.radio_option_normal);
        rbB.setBackgroundResource(R.drawable.radio_option_normal);
        rbC.setBackgroundResource(R.drawable.radio_option_normal);

        // 🔥 Les images ne sont pas générées par Gemini, donc on cache l'image
        imageView.setVisibility(View.GONE);

        hintContainer.setVisibility(View.GONE);
        explanationContainer.setVisibility(View.GONE);
        actionButtonsContainer.setVisibility(View.VISIBLE);

        resetButton();

        radioGroup.clearCheck();
        answered = false;

        // Démarrer le timer si activé
        if (withTimer) {
            startTimer();
        }
    }

    private void startTimer() {
        timeLeftInMillis = 30000;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                if (!answered) {
                    tvTimer.setText("⏰ Temps écoulé !");
                    tvTimer.setTextColor(Color.RED);
                    Toast.makeText(QuizDynamicActivity.this,
                            "Temps écoulé ! ⏰",
                            Toast.LENGTH_SHORT).show();

                    answered = true;
                    actionButtonsContainer.setVisibility(View.GONE);
                    changeButtonToNext();

                    Question currentQuestion = questionList.get(currentQuestionIndex);
                    showExplanation(false,
                            "La bonne réponse est : " + currentQuestion.getCorrectAnswer() +
                                    "\n" + currentQuestion.getExplanation());

                    RadioButton correctRb = null;
                    if (rbA.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                        correctRb = rbA;
                    } else if (rbB.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                        correctRb = rbB;
                    } else if (rbC.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                        correctRb = rbC;
                    }
                    if (correctRb != null) {
                        correctRb.setBackgroundResource(R.drawable.radio_option_correct);
                    }
                }
            }
        }.start();
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText("⏱️ " + seconds + "s");

        if (seconds <= 10) {
            tvTimer.setTextColor(Color.RED);
        } else {
            tvTimer.setTextColor(Color.parseColor("#7C3AED"));
        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void setupListeners() {

        btnHint.setOnClickListener(v -> {
            if (questionList.isEmpty() || currentQuestionIndex >= questionList.size()) return;
            Question currentQuestion = questionList.get(currentQuestionIndex);
            tvHint.setText("💡 " + currentQuestion.getHint());
            hintContainer.setVisibility(View.VISIBLE);
        });

        btnMic.setOnClickListener(v -> {
            if (questionList.isEmpty() || answered) return;
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez maintenant...");
            startActivityForResult(intent, 1);
        });

        btnNext.setOnClickListener(v -> {

            if (radioGroup.getCheckedRadioButtonId() == -1 && !answered) {
                Toast.makeText(this,
                        "Merci de choisir une réponse SVP !",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!answered) {
                checkAnswer();
            } else {
                currentQuestionIndex++;
                displayQuestion();
            }
        });
    }

    private void checkAnswer() {

        stopTimer();

        Question currentQuestion = questionList.get(currentQuestionIndex);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) return;
        RadioButton selectedRb = findViewById(checkedId);

        RadioButton correctRb = null;
        if (rbA.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
            correctRb = rbA;
        } else if (rbB.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
            correctRb = rbB;
        } else if (rbC.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
            correctRb = rbC;
        }

        if (selectedRb.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
            score++;

            selectedRb.setBackgroundResource(R.drawable.radio_option_correct);
            playSound(soundCorrect);
            showExplanation(true, currentQuestion.getExplanation());

        } else {

            selectedRb.setBackgroundResource(R.drawable.radio_option_incorrect);

            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
            selectedRb.startAnimation(shake);

            playSound(soundWrong);

            if (correctRb != null) {
                correctRb.setBackgroundResource(R.drawable.radio_option_correct);
            }

            showExplanation(false,
                    "La bonne réponse est : " + currentQuestion.getCorrectAnswer() +
                            "\n" + currentQuestion.getExplanation());
        }

        actionButtonsContainer.setVisibility(View.GONE);
        changeButtonToNext();
        answered = true;
    }

    private void playSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        }
    }

    private void showExplanation(boolean isCorrect, String explanation) {

        explanationContainer.setVisibility(View.VISIBLE);
        tvExplanation.setText(explanation);

        if (isCorrect) {
            tvResultIcon.setText("✅");
            tvResultTitle.setText("Bonne réponse !");
            tvResultTitle.setTextColor(Color.parseColor("#2E7D32"));
            tvExplanation.setTextColor(Color.parseColor("#1B5E20"));
            explanationContainer.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            tvResultIcon.setText("❌");
            tvResultTitle.setText("Mauvaise réponse");
            tvResultTitle.setTextColor(Color.parseColor("#C62828"));
            tvExplanation.setTextColor(Color.parseColor("#B71C1C"));
            explanationContainer.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }
    }

    private void resetButton() {
        btnNext.setText("VALIDER");

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(25);
        drawable.setColor(Color.parseColor("#7C3AED"));
        btnNext.setBackground(drawable);
    }

    private void changeButtonToNext() {
        btnNext.setText("SUIVANT ➜");

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(25);
        drawable.setColor(Color.parseColor("#10B981"));
        btnNext.setBackground(drawable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (result != null && result.size() > 0) {
                String voiceText = result.get(0);
                Question currentQuestion = questionList.get(currentQuestionIndex);

                Toast.makeText(this,
                        "Vous avez dit : " + voiceText,
                        Toast.LENGTH_SHORT).show();

                RadioButton correctRb = null;
                RadioButton spokenRb = null;

                if (rbA.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                    correctRb = rbA;
                } else if (rbB.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                    correctRb = rbB;
                } else if (rbC.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                    correctRb = rbC;
                }

                if (rbA.getText().toString().equalsIgnoreCase(voiceText)) {
                    spokenRb = rbA;
                } else if (rbB.getText().toString().equalsIgnoreCase(voiceText)) {
                    spokenRb = rbB;
                } else if (rbC.getText().toString().equalsIgnoreCase(voiceText)) {
                    spokenRb = rbC;
                }

                if (voiceText.equalsIgnoreCase(currentQuestion.getCorrectAnswer())
                        && !answered) {
                    score++;
                    answered = true;

                    stopTimer();

                    if (correctRb != null) {
                        correctRb.setBackgroundResource(R.drawable.radio_option_correct);
                    }

                    playSound(soundCorrect);
                    showExplanation(true, currentQuestion.getExplanation());
                    actionButtonsContainer.setVisibility(View.GONE);
                    changeButtonToNext();

                } else if (!answered) {

                    stopTimer();

                    if (spokenRb != null) {
                        spokenRb.setBackgroundResource(R.drawable.radio_option_incorrect);
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
                        spokenRb.startAnimation(shake);
                    }

                    if (correctRb != null) {
                        correctRb.setBackgroundResource(R.drawable.radio_option_correct);
                    }

                    playSound(soundWrong);
                    showExplanation(false,
                            "La bonne réponse est : " + currentQuestion.getCorrectAnswer());
                    actionButtonsContainer.setVisibility(View.GONE);
                    changeButtonToNext();
                    answered = true;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopTimer();

        if (soundCorrect != null) {
            soundCorrect.release();
            soundCorrect = null;
        }

        if (soundWrong != null) {
            soundWrong.release();
            soundWrong = null;
        }
    }
}