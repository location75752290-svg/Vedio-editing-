package com.example.engine

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FFmpegRunner(private val context: Context) {
    suspend fun run(command: List<String>): Boolean {
        return true
    }
}

class AIViralPromptEngine(
    private val context: Context,
    private val bgEngine: AIBGRemoveEngine,
    private val moodEngine: AIDirectorEngine,
    private val lipSyncEngine: AILipSyncEngine,
    private val ffmpegRunner: FFmpegRunner // Tumhara FFmpeg wrapper
) {

    data class PromptPlan(
        val background: String, // "rain", "matrix", "tokyo", "none"
        val mood: String,       // "Romantic", "Sad", "Epic", "Nostalgic"
        val dub: Boolean,
        val voice: String       // "Female_Soft", "Male_Deep"
    )

    private var currentPlan: PromptPlan = PromptPlan("none", "Epic", false, "Female_Soft")

    suspend fun parseAndStart(prompt: String, inputVideoPath: String = "input.mp4"): String {
        currentPlan = parsePrompt(prompt)
        return inputVideoPath
    }

    suspend fun applyBG(videoPath: String): String {
        return if (currentPlan.background != "none") {
            val newBG = bgEngine.getAnimatedBG(currentPlan.background)
            bgEngine.replaceBackground(videoPath, newBG)
        } else videoPath
    }

    suspend fun applyMood(videoPath: String): String {
        val moodVideo = moodEngine.applyMood(videoPath, currentPlan.mood)
        ffmpegRunner.run(moodEngine.getFFmpegCommand())
        return videoPath
    }

    suspend fun applyDub(videoPath: String): String {
        return if (currentPlan.dub) {
            lipSyncEngine.dubToUrdu(videoPath, currentPlan.voice)
        } else videoPath
    }

    suspend fun executePrompt(inputVideoPath: String, prompt: String): String = coroutineScope {
        // Step 1: AI se prompt samjho
        val plan = parsePrompt(prompt)
        
        var currentVideoPath = inputVideoPath

        // Step 2: 3 Engine Parallel/Sequence me chalao
        // 2a. Background + 2b. Mood - Inko parallel kar sakte hain
        val bgJob = async {
            if (plan.background != "none") {
                val newBG = bgEngine.getAnimatedBG(plan.background)
                bgEngine.replaceBackground(currentVideoPath, newBG)
            } else currentVideoPath
        }

        val moodJob = async {
            val moodVideo = moodEngine.applyMood(currentVideoPath, plan.mood)
            // moodEngine.applyMood ke andar applyPack() call hoga aur FFmpeg chalega
            ffmpegRunner.run(moodEngine.getFFmpegCommand())
            moodVideo
        }

        // Pehle BG aur Mood ka result lo
        currentVideoPath = bgJob.await()
        val moodProcessedPath = moodJob.await()
        currentVideoPath = mergeVideos(currentVideoPath, moodProcessedPath)

        // 2c. LipSync - ye last me chalega kyunki isko final video chahiye
        if (plan.dub) {
            currentVideoPath = lipSyncEngine.dubToUrdu(currentVideoPath, plan.voice)
        }

        return@coroutineScope currentVideoPath // "output_final.mp4"
    }

    private fun parsePrompt(prompt: String): PromptPlan {
        val lower = prompt.lowercase()
        return PromptPlan(
            background = when {
                "rain" in lower -> "rain"
                "matrix" in lower -> "matrix"
                "tokyo" in lower -> "tokyo"
                else -> "none"
            },
            mood = when {
                "romantic" in lower -> "Romantic"
                "sad" in lower -> "Sad"
                "epic" in lower -> "Epic"
                "nostalgic" in lower -> "Nostalgic"
                else -> "Epic"
            },
            dub = "dub" in lower || "urdu" in lower || "voice" in lower,
            voice = if ("male" in lower) "Male_Deep" else "Female_Soft"
        )
    }

    private fun mergeVideos(path1: String, path2: String): String {
        // Agar BG aur Mood alag render hue to unko merge karo
        return path2 // Simple case: Mood wala hi final maan lo
    }
}
