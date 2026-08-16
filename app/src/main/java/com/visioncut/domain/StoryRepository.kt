package com.visioncut.domain

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Data model for a generated story.
 */
data class UrduStory(
    val title: String,
    val script: String,
    val imagePrompts: List<String>
)

/**
 * Repository to generate Urdu stories using Gemini REST API with key loaded from SecretManager.
 */
class StoryRepository(private val context: Context) {

    companion object {
        private const val TAG = "StoryRepository"
        // Using recommended Gemini model as per specifications
        private const val GEMINI_MODEL = "gemini-3.5-flash"
        private const val API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Generates a 30-second Urdu story about "courage" (حوصلہ / ہمت).
     * Uses Gemini API key from secrets_gemini.json via SecretManager.
     * Returns UrduStory containing title, script, and image_prompts.
     */
    suspend fun generateCourageStory(customTopic: String = "courage"): Result<UrduStory> = withContext(Dispatchers.IO) {
        val apiKey = SecretManager.getGeminiApiKey(context)
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API Key is missing or invalid in app/src/main/assets/secrets_gemini.json")
            )
        }

        val prompt = """
            Write an inspiring 30-second short story in Urdu language on the topic of '$customTopic' (حوصلہ اور ہمت).
            The output MUST be in valid JSON format matching this exact schema:
            {
              "title": "Story title in Urdu (e.g. ننھے پرندے کی ہمت)",
              "script": "Complete 30-second Urdu narration script (approx 60-80 words in Urdu script).",
              "image_prompts": [
                "Visual scene 1 English prompt for image generation",
                "Visual scene 2 English prompt for image generation",
                "Visual scene 3 English prompt for image generation"
              ]
            }
            Do not include Markdown formatting or backticks. Return only pure JSON.
        """.trimIndent()

        try {
            // Build Gemini REST payload
            val partsArray = JSONArray().put(JSONObject().put("text", prompt))
            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))

            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", generationConfig)
            }

            val requestBody = requestJson.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val url = "$API_BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBodyString = response.body?.string()

            if (!response.isSuccessful || responseBodyString.isNullOrEmpty()) {
                Log.e(TAG, "Gemini API error: HTTP ${response.code}, body: $responseBodyString")
                return@withContext Result.failure(
                    RuntimeException("Gemini API call failed with HTTP ${response.code}: $responseBodyString")
                )
            }

            val parsedResponse = JSONObject(responseBodyString)
            val candidates = parsedResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(RuntimeException("No candidate response from Gemini API"))
            }

            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            // Parse structured story JSON
            val cleanJson = rawText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val storyJson = JSONObject(cleanJson)

            val title = storyJson.optString("title", "حوصلے کی کہانی")
            val script = storyJson.optString("script", "")
            val imagePromptsList = mutableListOf<String>()

            val promptsArray = storyJson.optJSONArray("image_prompts")
            if (promptsArray != null) {
                for (i in 0 until promptsArray.length()) {
                    imagePromptsList.add(promptsArray.getString(i))
                }
            }

            Result.success(
                UrduStory(
                    title = title,
                    script = script,
                    imagePrompts = imagePromptsList
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error generating story: ${e.message}", e)
            Result.failure(e)
        }
    }
}
