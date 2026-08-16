package com.example.moralstoryreel.data

import com.example.BuildConfig

object SecretManager {
    fun getGeminiApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    fun hasValidApiKey(): Boolean {
        val key = getGeminiApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }
}
