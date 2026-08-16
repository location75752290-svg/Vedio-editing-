package com.example.moralstoryreel.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Story(
    val title: String,
    val script: String,
    val imagePrompts: List<String>
)

class StoryRepository(private val context: Context) {

    companion object {
        private const val TAG = "StoryRepository"
        private const val GEMINI_MODEL = "gemini-flash-latest"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun parseStoryResponse(jsonString: String): Story {
        val json = Json { ignoreUnknownKeys = true }
        val root = json.parseToJsonElement(jsonString).jsonObject
        val text = root["candidates"]?.jsonArray?.get(0)?.jsonObject
            ?.get("content")?.jsonObject
            ?.get("parts")?.jsonArray?.get(0)?.jsonObject
            ?.get("text")?.jsonPrimitive?.content ?: ""

        // Gemini sometimes returns JSON inside ```json ... ```
        val cleanJson = text.replace("```json", "").replace("```", "").trim()
        val storyJson = json.parseToJsonElement(cleanJson).jsonObject

        return Story(
            title = storyJson["title"]?.jsonPrimitive?.content ?: "Untitled Story",
            script = storyJson["script"]?.jsonPrimitive?.content ?: "",
            imagePrompts = storyJson["image_prompts"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
        )
    }

    suspend fun generateStory(topic: String): Story = withContext(Dispatchers.IO) {
        val apiKey = SecretManager.getGeminiApiKey(context)
        if (apiKey.isEmpty() || apiKey.startsWith("MY_GEMINI_API_KEY") || apiKey.startsWith("PASTE_YOUR_") || apiKey.startsWith("YOUR_")) {
            throw IllegalStateException("Valid Gemini API key (GEMINI_API_KEY) not found in Secrets.")
        }

        val prompt = """
            Write a 30 second Urdu moral story about $topic.
            Return ONLY a valid JSON object matching this exact structure:
            {
              "title": "Urdu story title here",
              "script": "Complete 30-second Urdu story narration here",
              "image_prompts": [
                "Detailed English image prompt for scene 1",
                "Detailed English image prompt for scene 2",
                "Detailed English image prompt for scene 3"
              ]
            }
            Do not include markdown code block syntax (like ```json or ```). Return pure JSON.
        """.trimIndent()

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

        val url = "$BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBodyString = response.body?.string()

        if (!response.isSuccessful || responseBodyString.isNullOrEmpty()) {
            throw RuntimeException("Gemini API call failed (HTTP ${response.code}): $responseBodyString")
        }

        parseStoryResponse(responseBodyString)
    }
}
