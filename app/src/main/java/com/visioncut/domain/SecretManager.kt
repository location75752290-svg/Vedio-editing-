package com.visioncut.domain

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * SecretManager reads API keys and configuration safely from assets JSON files for VisionCut AI.
 */
object SecretManager {
    private const val TAG = "SecretManager"

    private const val FILE_GEMINI = "secrets_gemini.json"
    private const val FILE_PIXABAY = "secrets_pixabay.json"
    private const val FILE_PEXELS = "secrets_pexels.json"
    private const val FILE_FIREBASE = "secrets_firebase.json"

    private val cachedSecrets = mutableMapOf<String, String>()

    /**
     * Reads a key from an asset JSON file.
     * Returns null if file is missing, empty, or contains default placeholder.
     */
    fun getSecret(context: Context, fileName: String, jsonKey: String): String? {
        val cacheKey = "$fileName:$jsonKey"
        if (cachedSecrets.containsKey(cacheKey)) {
            return cachedSecrets[cacheKey]
        }

        return try {
            val jsonString = context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.has(jsonKey)) {
                val value = jsonObject.getString(jsonKey).trim()
                if (value.isNotEmpty() && !value.startsWith("PASTE_YOUR_") && !value.startsWith("YOUR_")) {
                    cachedSecrets[cacheKey] = value
                    value
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not load secret from assets/$fileName: ${e.message}")
            null
        }
    }

    /**
     * Returns the Gemini API Key from secrets_gemini.json
     */
    fun getGeminiApiKey(context: Context): String? {
        return getSecret(context, FILE_GEMINI, "gemini_api_key")
    }

    /**
     * Returns the Pixabay API Key from secrets_pixabay.json
     */
    fun getPixabayApiKey(context: Context): String? {
        return getSecret(context, FILE_PIXABAY, "pixabay_api_key")
    }

    /**
     * Returns the Pexels API Key from secrets_pexels.json
     */
    fun getPexelsApiKey(context: Context): String? {
        return getSecret(context, FILE_PEXELS, "pexels_api_key")
    }

    /**
     * Returns the Firebase Config from secrets_firebase.json
     */
    fun getFirebaseConfig(context: Context): String? {
        return getSecret(context, FILE_FIREBASE, "firebase_config")
    }
}
