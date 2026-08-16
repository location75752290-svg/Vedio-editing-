package com.example.moralstoryreel.data

import android.content.Context
import com.example.BuildConfig

object SecretManager {
    /**
     * Retrieves the Gemini API Key from BuildConfig (injected via Secrets Gradle plugin / AI Studio secrets).
     * Falls back to empty string if missing.
     */
    fun getGeminiApiKey(context: Context? = null): String {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return key.trim()
    }
}
