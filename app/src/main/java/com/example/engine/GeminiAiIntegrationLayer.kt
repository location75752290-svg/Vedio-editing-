package com.example.engine

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiIntegrationLayer {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"

    val isApiKeyConfigured: Boolean
        get() = try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Exception) {
            false
        }

    /**
     * AI Text to Video Script & Scene Prompt Generation
     */
    suspend fun generateTextToVideoPrompt(userPrompt: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.failure(IllegalStateException("Gemini API Key is not configured. Please add your key in AI Studio Secrets."))
        }
        val apiKey = BuildConfig.GEMINI_API_KEY

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Act as an expert video editor. Expand this text idea into a detailed cinematographic prompt for video generation: $userPrompt")
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("$baseUrl/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful && body.isNotBlank()) {
                val jsonRes = JSONObject(body)
                val candidates = jsonRes.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val resultText = parts?.optJSONObject(0)?.optString("text")

                if (!resultText.isNullOrBlank()) {
                    Result.success(resultText)
                } else {
                    Result.failure(Exception("Empty response from Gemini AI"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code} $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * AI Auto Captions Generator using Gemini 3.5 Flash
     */
    suspend fun generateAutoCaptions(videoTitle: String): Result<List<String>> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.success(
                listOf(
                    "Welcome to VisionCut AI Studio",
                    "Creating cinematic video content automatically",
                    "Powered by high performance rendering"
                )
            )
        }
        val apiKey = BuildConfig.GEMINI_API_KEY

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Generate 4 subtitle caption lines for a video titled '$videoTitle'. Return as line-separated text.")
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("$baseUrl/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful && body.isNotBlank()) {
                val jsonRes = JSONObject(body)
                val candidates = jsonRes.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")
                val lines = text?.lines()?.filter { it.isNotBlank() } ?: emptyList()
                Result.success(lines)
            } else {
                Result.failure(Exception("API Error: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * AI Voice Generator Script Synthesizer
     */
    suspend fun generateVoiceScript(topic: String, style: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured) {
            return@withContext Result.success("Here is your AI generated narration for $topic in a $style tone.")
        }
        val apiKey = BuildConfig.GEMINI_API_KEY

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Write a compelling voiceover script for a video about '$topic' in a $style style.")
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("$baseUrl/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful && body.isNotBlank()) {
                val jsonRes = JSONObject(body)
                val candidates = jsonRes.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val resultText = parts?.optJSONObject(0)?.optString("text") ?: ""
                Result.success(resultText)
            } else {
                Result.failure(Exception("API Error: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
