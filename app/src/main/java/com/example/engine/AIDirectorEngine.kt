package com.example.engine

import android.graphics.Bitmap

data class LUT(
    val name: String,
    val colorMatrix: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
)

val KodakGold = LUT("Kodak Gold", floatArrayOf(
    1.2f, 0.05f, 0.0f, 0f, 10f,
    0.05f, 1.1f, 0.0f, 0f, 5f,
    0.0f, 0.05f, 0.9f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
))

val TealOrange = LUT("Teal & Orange", floatArrayOf(
    0.9f, 0.1f, 0.0f, 0f, 15f,
    0.0f, 1.0f, 0.2f, 0f, 5f,
    0.1f, 0.3f, 1.2f, 0f, 20f,
    0f, 0f, 0f, 1f, 0f
))

val FujiFilm = LUT("FujiFilm Cine", floatArrayOf(
    1.05f, 0.0f, 0.0f, 0f, 0f,
    0.0f, 1.15f, 0.05f, 0f, 5f,
    0.0f, 0.05f, 1.1f, 0f, 10f,
    0f, 0f, 0f, 1f, 0f
))

val CinematicBlue = LUT("Cinematic Blue", floatArrayOf(
    0.85f, 0.05f, 0.1f, 0f, 0f,
    0.05f, 0.95f, 0.15f, 0f, 5f,
    0.1f, 0.2f, 1.3f, 0f, 15f,
    0f, 0f, 0f, 1f, 0f
))

val ColdBlue = LUT("Cold Blue", floatArrayOf(
    0.75f, 0.1f, 0.15f, 0f, -10f,
    0.05f, 0.9f, 0.2f, 0f, 0f,
    0.1f, 0.3f, 1.4f, 0f, 25f,
    0f, 0f, 0f, 1f, 0f
))

enum class CaptionStyle {
    UrduPoetry,
    UrduSad,
    UrduNostalgic,
    BoldWhite,
    UrduShayari
}

data class Video(
    val title: String = "Untitled Video",
    val durationSec: Float = 15.0f,
    val activeLUT: LUT? = null,
    val speed: Float = 1.0f,
    val glowIntensity: Float = 0.0f,
    val filmGrain: Float = 0.0f,
    val sfxTrack: String? = null,
    val musicTrack: String? = null,
    val captionStyle: CaptionStyle? = null,
    val frameBitmap: Bitmap? = null,
    val audio: ByteArray? = null,
    val subtitles: String? = null
) {
    fun applyLUT(lut: LUT): Video = copy(activeLUT = lut)
    fun changeSpeed(newSpeed: Float): Video = copy(speed = newSpeed)
    fun addGlow(glow: Float): Video = copy(glowIntensity = glow)
    fun addFilmGrain(grain: Float): Video = copy(filmGrain = grain)
    fun addSFX(sfx: String): Video = copy(sfxTrack = sfx)
    fun addMusic(music: String): Video = copy(musicTrack = music)
    fun addAICaptions(style: CaptionStyle): Video = copy(captionStyle = style)
    fun addSubtitles(text: String): Video = copy(subtitles = text)
}

class AIDirectorEngine {

    private var lastFFmpegCommand: List<String> = emptyList()

    data class MoodPreset(
        val lut: LUT,
        val speed: Float,
        val glow: Float,
        val grain: Float,
        val sfx: String,
        val music: String,
        val captionStyle: CaptionStyle
    )

    private val moodMap = mapOf(
        "Romantic" to MoodPreset(KodakGold, 0.85f, 0.4f, 0.1f, "Rain", "Piano", CaptionStyle.UrduPoetry),
        "Sad" to MoodPreset(TealOrange, 0.5f, 0.0f, 0.3f, "Thunder", "Violin", CaptionStyle.UrduSad),
        "Nostalgic" to MoodPreset(FujiFilm, 0.75f, 0.2f, 0.5f, "Wind", "LoFi", CaptionStyle.UrduNostalgic),
        "Epic" to MoodPreset(CinematicBlue, 1.0f, 0.0f, 0.0f, "Drum", "Orchestra", CaptionStyle.BoldWhite),
        "Heartbreak" to MoodPreset(ColdBlue, 0.6f, 0.1f, 0.2f, "Rain", "Guitar", CaptionStyle.UrduShayari)
    )

    fun applyMood(video: Video, mood: String): Video {
        val cmd = when (mood) {
            "Romantic" -> applyPack(
                lut = "kodak_gold.cube",
                speed = 0.85f,
                music = "romantic_piano.mp3",
                sfx = "rain.wav",
                captionPrompt = "Urdu romantic shayari likho"
            )
            "Sad" -> applyPack(lut = "teal_orange.cube", speed = 0.5f, music = "sad_violin.mp3", sfx = "thunder.wav")
            "Epic" -> applyPack(lut = "cinematic_blue.cube", speed = 1.0f, music = "epic_drum.mp3")
            "Nostalgic" -> applyPack(lut = "fuji_film.cube", speed = 0.75f, music = "lofi.mp3", grain = 0.5f)
            else -> applyPack(lut = "cinematic_blue.cube", speed = 1.0f, music = "epic_drum.mp3")
        }
        lastFFmpegCommand = cmd

        val preset = moodMap[mood] ?: moodMap["Epic"]!!

        return video
            .applyLUT(preset.lut)
            .changeSpeed(preset.speed)
            .addGlow(preset.glow)
            .addFilmGrain(preset.grain)
            .addSFX(preset.sfx)
            .addMusic(preset.music)
            .addAICaptions(preset.captionStyle)
    }

    fun applyMood(videoPath: String, mood: String): String {
        applyMood(Video(title = videoPath), mood)
        return videoPath
    }

    fun getFFmpegCommand(): List<String> = lastFFmpegCommand

    private fun applyPack(
        lut: String,
        speed: Float,
        music: String,
        sfx: String? = null,
        grain: Float = 0f,
        captionPrompt: String? = null
    ): List<String> {
        val cmd = mutableListOf("-i", "input.mp4")

        // 1. Video Filters Chain: Speed + LUT + Grain + Glow
        var vf = "[0:v]setpts=${1 / speed}*PTS," // Speed
        vf += "lut3d=${lut}," // LUT
        vf += "noise=alls=${grain * 1000}:allf=t+u," // Grain
        vf += "glow=glow_strength=0.3" // Glow
        vf += "[v]"

        cmd.add("-filter_complex")
        cmd.add(vf)

        // 2. Audio: Music + SFX Mix
        cmd.add("-i")
        cmd.add("assets/$music")
        sfx?.let {
            cmd.add("-i")
            cmd.add("assets/$it")
            cmd.add("-filter_complex")
            cmd.add("[1:a][2:a]amix=inputs=2:duration=longest[a]")
            cmd.add("-map")
            cmd.add("[a]")
        }

        // 3. Output
        cmd.add("-map")
        cmd.add("[v]")
        cmd.add("-c:a")
        cmd.add("aac")
        cmd.add("output.mp4")

        return cmd
    }

    fun getAvailableMoods(): List<String> = moodMap.keys.toList()

    fun getMoodPreset(mood: String): MoodPreset = moodMap[mood] ?: moodMap["Epic"]!!
}
