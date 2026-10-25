package com.example.quiz;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GeminiService {

    private static final String TAG = "GeminiService";

    private static final String API_KEY = "AIzaSyBPcYpOpR0berHcinI-s_m112V-gRzEJw4";

    // Interface callback pour renvoyer les questions
    public interface QuizGenerationCallback {
        void onSuccess(List<Question> questions);
        void onError(String error);
    }

    public void generateQuiz(String theme, String difficulty, int numberOfQuestions, QuizGenerationCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Generating quiz - Theme: " + theme + ", Difficulty: " + difficulty);

                String prompt = buildPrompt(theme, difficulty, numberOfQuestions);

                // 1) Construire le JSON de la requête
                JSONObject textPart = new JSONObject();
                textPart.put("text", prompt);

                JSONArray parts = new JSONArray();
                parts.put(textPart);

                JSONObject contentObj = new JSONObject();
                contentObj.put("parts", parts);

                JSONArray contents = new JSONArray();
                contents.put(contentObj);

                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contents);

                // 🔁 CHANGEMENT ICI : utiliser un modèle v1 valide (ex: gemini-2.0-flash)
                String urlString =
                        "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

                Log.d(TAG, "API URL: " + urlString);
                Log.d(TAG, "Request body: " + requestBody.toString());

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000); // 30 secondes
                conn.setReadTimeout(30000);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(requestBody.toString());
                wr.flush();
                wr.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode != 200) {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorReader.close();
                    Log.e(TAG, "Error response: " + errorResponse.toString());
                    throw new Exception("HTTP error " + responseCode + ": " + errorResponse.toString());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d(TAG, "Full response: " + response.toString());

                // 3) Récupérer le texte généré
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray partsArray = content.getJSONArray("parts");
                String quizJsonText = partsArray.getJSONObject(0).getString("text");

                Log.d(TAG, "Generated text: " + quizJsonText);

                // 4) Parser le JSON en liste de Questions
                List<Question> questions = parseQuestionsFromJson(quizJsonText);

                Log.d(TAG, "✅ Successfully parsed " + questions.size() + " questions");

                // Retourner sur le thread principal
                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(questions));

            } catch (Exception e) {
                Log.e(TAG, "❌ Error generating quiz", e);
                e.printStackTrace();

                // Retourner sur le thread principal
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Erreur: " + e.getMessage())
                );
            }
        }).start();
    }

    private String buildPrompt(String theme, String difficulty, int numberOfQuestions) {
        String difficultyText;
        switch (difficulty.toLowerCase()) {
            case "easy":
            case "facile":
                difficultyText = "facile (débutant)";
                break;
            case "medium":
            case "moyen":
                difficultyText = "moyen (intermédiaire)";
                break;
            case "hard":
            case "difficile":
                difficultyText = "difficile (expert)";
                break;
            default:
                difficultyText = "moyen";
        }

        return "Génère EXACTEMENT " + numberOfQuestions + " questions de quiz en français.\n" +
                "Thème: " + theme + "\n" +
                "Difficulté: " + difficultyText + "\n\n" +
                "Réponds UNIQUEMENT avec un tableau JSON VALIDE (sans ``` ni texte avant/après):\n" +
                "[\n" +
                "  {\n" +
                "    \"question\": \"Question ici?\",\n" +
                "    \"option1\": \"Option A\",\n" +
                "    \"option2\": \"Option B\",\n" +
                "    \"option3\": \"Option C\",\n" +
                "    \"correctAnswer\": \"Option A\",\n" +
                "    \"hint\": \"Indice pour aider\",\n" +
                "    \"explanation\": \"Explication détaillée de la réponse\"\n" +
                "  }\n" +
                "]\n\n" +
                "IMPORTANT:\n" +
                "- correctAnswer doit être EXACTEMENT le même texte qu'une des 3 options\n" +
                "- Chaque question doit avoir un hint et une explanation\n" +
                "- Les questions doivent être claires et éducatives\n" +
                "- Les options doivent être plausibles";
    }

    private List<Question> parseQuestionsFromJson(String jsonText) throws Exception {
        List<Question> list = new ArrayList<>();

        // Nettoyer le JSON si nécessaire
        String cleanJson = jsonText.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring(7);
        }
        if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substring(3);
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
        }
        cleanJson = cleanJson.trim();

        Log.d(TAG, "Clean JSON: " + cleanJson);

        JSONArray qArray = new JSONArray(cleanJson);

        for (int i = 0; i < qArray.length(); i++) {
            JSONObject qObj = qArray.getJSONObject(i);

            String questionText = qObj.getString("question");
            String option1 = qObj.getString("option1");
            String option2 = qObj.getString("option2");
            String option3 = qObj.getString("option3");
            String correctAnswer = qObj.getString("correctAnswer");
            String hint = qObj.optString("hint", "Pas d'indice");
            String explanation = qObj.optString("explanation", "Pas d'explication");

            Question q = new Question(
                    questionText,
                    option1,
                    option2,
                    option3,
                    correctAnswer,
                    hint,
                    explanation
            );

            list.add(q);
        }

        return list;
    }
}