package com.example.moralstoryreel.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class StoryScene(
    val sceneNumber: Int,
    val narration: String,
    val visualPrompt: String,
    val dialogue: String? = null
)

data class MoralStory(
    val title: String,
    val theme: String,
    val targetAudience: String,
    val scenes: List<StoryScene>,
    val moralLesson: String,
    val hashtags: List<String>
)

class StoryRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateStory(
        topic: String,
        targetDurationSeconds: Int = 45,
        language: String = "English",
        style: String = "Inspiring 3D Animation Style"
    ): Result<MoralStory> = withContext(Dispatchers.IO) {
        try {
            val apiKey = SecretManager.getGeminiApiKey()
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API Key missing or invalid. Please configure it in Secrets.")
                )
            }

            val prompt = """
                You are a master moral story scriptwriter for viral social media vertical video reels (TikTok, Shorts, Reels).
                Create a captivating, high-impact moral story reel script based on the following input:
                - Topic/Prompt: $topic
                - Target Duration: ~$targetDurationSeconds seconds (around 4-6 concise scenes)
                - Language: $language
                - Visual Artistic Style: $style

                Respond ONLY with a valid JSON object matching this structure:
                {
                  "title": "Short Catchy Reel Title",
                  "theme": "Core Theme (e.g., Kindness, Honesty, Hard Work)",
                  "targetAudience": "All Ages / Youth / Kids",
                  "scenes": [
                    {
                      "sceneNumber": 1,
                      "narration": "Voiceover narration for this scene",
                      "visualPrompt": "Detailed AI image generation prompt for this scene's background/characters",
                      "dialogue": "Optional dialogue if any, or null"
                    }
                  ],
                  "moralLesson": "One punchy sentence highlighting the takeaway message.",
                  "hashtags": ["#MoralStory", "#Shorts", "#Inspiration", "#Wisdom"]
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                
                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                }
                put("generationConfig", genConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Gemini API Error ${response.code}: $responseString")
                )
            }

            val jsonResp = JSONObject(responseString)
            val candidates = jsonResp.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleanedJsonText = rawText
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val storyJson = JSONObject(cleanedJsonText)
            val scenesArray = storyJson.getJSONArray("scenes")
            val scenesList = mutableListOf<StoryScene>()

            for (i in 0 until scenesArray.length()) {
                val sceneObj = scenesArray.getJSONObject(i)
                scenesList.add(
                    StoryScene(
                        sceneNumber = sceneObj.optInt("sceneNumber", i + 1),
                        narration = sceneObj.optString("narration", ""),
                        visualPrompt = sceneObj.optString("visualPrompt", ""),
                        dialogue = if (sceneObj.has("dialogue") && !sceneObj.isNull("dialogue")) sceneObj.optString("dialogue") else null
                    )
                )
            }

            val hashtagsArray = storyJson.optJSONArray("hashtags")
            val hashtagsList = mutableListOf<String>()
            if (hashtagsArray != null) {
                for (i in 0 until hashtagsArray.length()) {
                    hashtagsList.add(hashtagsArray.getString(i))
                }
            }

            val story = MoralStory(
                title = storyJson.optString("title", "Moral Story Reel"),
                theme = storyJson.optString("theme", "Wisdom"),
                targetAudience = storyJson.optString("targetAudience", "General"),
                scenes = scenesList,
                moralLesson = storyJson.optString("moralLesson", ""),
                hashtags = hashtagsList
            )

            Result.success(story)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
