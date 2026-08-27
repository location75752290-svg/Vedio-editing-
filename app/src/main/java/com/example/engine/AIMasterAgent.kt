package com.example.engine

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID

data class ProjectStep(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val details: String = ""
)

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val path: String,
    val details: String = ""
)

data class VideoProject(
    val clips: MutableList<VideoClip> = mutableListOf(),
    var finalPath: String? = null
) {
    fun add(clip: VideoClip) { clips.add(clip) }
    val steps: List<ProjectStep> get() = clips.map { ProjectStep(name = it.name, details = it.details) }
    var isRendered: Boolean = true
    var outputUri: String
        get() = finalPath ?: ""
        set(value) { finalPath = value }

    suspend fun render(): VideoProject {
        return this
    }
}

// ========= GEMINI NANO AI ENGINE =========
class GeminiNano(private val integrationLayer: GeminiAiIntegrationLayer = GeminiAiIntegrationLayer()) {
    suspend fun generate(prompt: String): String {
        return try {
            val res = integrationLayer.generateTextToVideoPrompt(prompt)
            res.getOrElse { generateFallbackJson(prompt) }
        } catch (e: Exception) {
            generateFallbackJson(prompt)
        }
    }

    private fun generateFallbackJson(prompt: String): String {
        val lower = prompt.lowercase()
        return org.json.JSONObject().apply {
            put("prompt", prompt)
            put("isTextToVideo", listOf("banao", "generate", "video banao", "create").any { it in lower })
            put("needsAvatar", listOf("avatar", "presenter", "bolne wala", "anchor").any { it in lower })
            put("character", when {
                "emma" in lower -> "Emma"
                "david" in lower -> "David"
                "sarah" in lower -> "Sarah"
                "alex" in lower -> "Alex"
                else -> org.json.JSONObject.NULL
            })
            put("emotion", when {
                "sad" in lower -> "Sad"
                "energetic" in lower -> "Energetic"
                "friendly" in lower -> "Friendly"
                else -> "Professional"
            })
            put("background", when {
                "rain" in lower -> "rain"
                "matrix" in lower -> "matrix"
                "tokyo" in lower -> "tokyo"
                "cyberpunk" in lower -> "cyberpunk"
                else -> "none"
            })
            put("needsVoice", listOf("voice", "dub", "bol", "urdu", "hindi").any { it in lower })
            put("language", when {
                "urdu" in lower -> "Urdu"
                "hindi" in lower -> "Hindi"
                "spanish" in lower -> "Spanish"
                else -> "Urdu"
            })
            put("voiceModel", when {
                "male" in lower -> "Male Deep"
                "news" in lower -> "News Anchor"
                "urdu male" in lower -> "Urdu Male"
                else -> "Urdu Female"
            })
            put("needsAutoCut", listOf("reel", "shorts", "cut", "silence").any { it in lower })
            put("mood", when {
                "sad" in lower -> "Teal & Orange"
                "romantic" in lower -> "Kodak Gold 200"
                "epic" in lower -> "HDR Cinematic"
                "cyberpunk" in lower -> "Cyberpunk Neon"
                "nostalgic" in lower -> "Fuji Film"
                else -> "none"
            })
            put("needsCaptions", listOf("caption", "subtitle", "reel", "shorts").any { it in lower })
            put("captionStyle", if ("urdu" in lower) "Urdu Nastaliq" else "TikTok Animated")
            put("needsUpscale", listOf("4k", "8k", "hd", "upscale").any { it in lower })
            put("targetRes", if ("8k" in lower) "8K" else "4K")
            put("fps", if ("120" in lower) 120 else if ("60" in lower) 60 else 30)
            put("aspectRatio", when {
                "landscape" in lower || "16:9" in lower -> "16:9"
                "square" in lower || "1:1" in lower -> "1:1"
                else -> "9:16"
            })
        }.toString()
    }
}

class PromptParser(private val gemini: GeminiNano = GeminiNano()) {

    @kotlinx.serialization.Serializable
    data class PromptPlan(
        val prompt: String = "",
        val isTextToVideo: Boolean = false,
        val needsAvatar: Boolean = false,
        val character: String? = null,
        val script: String? = null,
        val emotion: String = "Professional",
        val background: String = "none",
        val needsVoice: Boolean = false,
        val language: String = "Urdu",
        val voiceModel: String = "Urdu Female",
        val pitch: Float = 1.0f,
        val speed: Float = 1.0f,
        val needsAutoCut: Boolean = false,
        val silenceThreshold: Int = -30,
        val mood: String = "none",
        val intensity: Int = 70,
        val needsCaptions: Boolean = false,
        val captionStyle: String = "TikTok Animated",
        val needsUpscale: Boolean = false,
        val targetRes: String = "4K",
        val fps: Int = 60,
        val aspectRatio: String = "9:16",
        val needsEyeContact: Boolean = false
    )

    suspend fun parse(prompt: String): PromptPlan {
        val schema = """
            Return ONLY valid JSON with keys:
            isTextToVideo, needsAvatar, character, emotion, background,
            needsVoice, language, mood, needsCaptions, needsUpscale
        """.trimIndent()

        val aiResult = gemini.generate("$schema \n\nPrompt: $prompt")

        return try {
            parseJson(aiResult, fallbackPrompt = prompt)
        } catch (e: Exception) {
            parseFallback(prompt) // Agar JSON toota to keyword wala
        }
    }

    private fun parseJson(jsonStr: String, fallbackPrompt: String): PromptPlan {
        val cleanJsonStr = jsonStr.substringAfter("{").substringBeforeLast("}")
        val fullJsonStr = "{$cleanJsonStr}"
        val parsed = try {
            kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<PromptPlan>(fullJsonStr)
        } catch (e: Exception) {
            val json = org.json.JSONObject(fullJsonStr)
            PromptPlan(
                prompt = json.optString("prompt", fallbackPrompt),
                isTextToVideo = json.optBoolean("isTextToVideo", false),
                needsAvatar = json.optBoolean("needsAvatar", false),
                character = if (json.isNull("character")) null else json.optString("character", null),
                script = if (json.isNull("script")) null else json.optString("script", null),
                emotion = json.optString("emotion", "Professional"),
                background = json.optString("background", "none"),
                needsVoice = json.optBoolean("needsVoice", false),
                language = json.optString("language", "Urdu"),
                voiceModel = json.optString("voiceModel", "Urdu Female"),
                pitch = json.optDouble("pitch", 1.0).toFloat(),
                speed = json.optDouble("speed", 1.0).toFloat(),
                needsAutoCut = json.optBoolean("needsAutoCut", false),
                silenceThreshold = json.optInt("silenceThreshold", -30),
                mood = json.optString("mood", "none"),
                intensity = json.optInt("intensity", 70),
                needsCaptions = json.optBoolean("needsCaptions", false),
                captionStyle = json.optString("captionStyle", "TikTok Animated"),
                needsUpscale = json.optBoolean("needsUpscale", false),
                targetRes = json.optString("targetRes", "4K"),
                fps = json.optInt("fps", 60),
                aspectRatio = json.optString("aspectRatio", "9:16")
            )
        }
        return if (parsed.prompt.isBlank()) parsed.copy(prompt = fallbackPrompt) else parsed
    }

    private fun parseFallback(prompt: String): PromptPlan {
        val lower = prompt.lowercase()
        return PromptPlan(
            prompt = prompt,
            isTextToVideo = listOf("banao", "generate", "video banao", "create").any { it in lower },
            needsAvatar = listOf("avatar", "presenter", "bolne wala", "anchor").any { it in lower },
            character = when {
                "emma" in lower -> "Emma"
                "david" in lower -> "David"
                "sarah" in lower -> "Sarah"
                "alex" in lower -> "Alex"
                else -> null
            },
            emotion = when {
                "sad" in lower -> "Sad"
                "energetic" in lower -> "Energetic"
                "friendly" in lower -> "Friendly"
                else -> "Professional"
            },
            background = when {
                "rain" in lower -> "rain"
                "matrix" in lower -> "matrix"
                "tokyo" in lower -> "tokyo"
                "cyberpunk" in lower -> "cyberpunk"
                else -> "none"
            },
            needsVoice = listOf("voice", "dub", "bol", "urdu", "hindi").any { it in lower },
            language = when {
                "urdu" in lower -> "Urdu"
                "hindi" in lower -> "Hindi"
                "spanish" in lower -> "Spanish"
                else -> "Urdu"
            },
            voiceModel = when {
                "male" in lower -> "Male Deep"
                "news" in lower -> "News Anchor"
                "urdu male" in lower -> "Urdu Male"
                else -> "Urdu Female"
            },
            needsAutoCut = listOf("reel", "shorts", "cut", "silence").any { it in lower },
            mood = when {
                "sad" in lower -> "Teal & Orange"
                "romantic" in lower -> "Kodak Gold 200"
                "epic" in lower -> "HDR Cinematic"
                "cyberpunk" in lower -> "Cyberpunk Neon"
                "nostalgic" in lower -> "Fuji Film"
                else -> "none"
            },
            needsCaptions = listOf("caption", "subtitle", "reel", "shorts").any { it in lower },
            captionStyle = if ("urdu" in lower) "Urdu Nastaliq" else "TikTok Animated",
            needsUpscale = listOf("4k", "8k", "hd", "upscale").any { it in lower },
            targetRes = if ("8k" in lower) "8K" else "4K",
            fps = if ("120" in lower) 120 else if ("60" in lower) 60 else 30,
            aspectRatio = when {
                "landscape" in lower || "16:9" in lower -> "16:9"
                "square" in lower || "1:1" in lower -> "1:1"
                else -> "9:16"
            }
        )
    }
}

class TextToVideoTool {
    fun generate(prompt: String, aspectRatio: String): VideoClip {
        val path = "content://visioncut/ttv_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "AI Text-to-Video ($aspectRatio)", durationSeconds = 6f))
        return VideoClip(name = "AI Text-to-Video", path = path, details = "Prompt: $prompt ($aspectRatio)")
    }
}

class AvatarTool {
    fun create(character: String, script: String, emotion: String, needsEyeContact: Boolean = false): VideoClip {
        val path = "content://visioncut/avatar_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "AI Avatar Presenter ($character)", durationSeconds = 5f))
        return VideoClip(name = "AI Avatar Presenter", path = path, details = "Character: $character, Emotion: $emotion")
    }
}

class BgRemoverTool {
    fun remove(inputPath: String): VideoClip {
        val path = "content://visioncut/bg_cut_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "BG Removal Engine", durationSeconds = 4f))
        return VideoClip(name = "BG Removal", path = path, details = "Matting source: $inputPath")
    }

    fun replace(cutClip: VideoClip, background: String): VideoClip {
        val path = "content://visioncut/bg_replaced_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "BG Replace -> $background", durationSeconds = 4f))
        return VideoClip(name = "BG Replacement", path = path, details = "Target BG: $background")
    }
}

class VoiceTool {
    fun generate(text: String, language: String, voiceModel: String, pitch: Float = 1.0f, speed: Float = 1.0f): VideoClip {
        val path = "content://visioncut/voice_${System.currentTimeMillis()}.mp3"
        RenderQueue.add(RenderJob(title = "AI Voice Synthesis ($language)", durationSeconds = 3f))
        return VideoClip(name = "AI Voice Synthesis", path = path, details = "Language: $language, Model: $voiceModel")
    }
}

class LipSyncTool {
    fun sync(videoPath: String, audioPath: String): VideoClip {
        val path = "content://visioncut/lipsync_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "AI Neural Lip-Sync", durationSeconds = 5f))
        return VideoClip(name = "Neural Lip-Sync", path = path, details = "Synced Audio to $videoPath")
    }
}

class AutoCutTool {
    fun removeSilence(inputPath: String, threshold: Int = -35): VideoClip {
        val path = "content://visioncut/autocut_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "Auto Silence Removal ($threshold dB)", durationSeconds = 3f))
        return VideoClip(name = "Auto Silence Removal", path = path, details = "Cut below threshold: $threshold dB")
    }
}

class ColorGradeTool {
    fun applyLUT(inputPath: String, mood: String, intensity: Float = 0.8f): VideoClip {
        val path = "content://visioncut/color_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "Cinematic LUT: $mood", durationSeconds = 3f))
        return VideoClip(name = "Cinematic Color Grade", path = path, details = "LUT: $mood (Intensity: ${(intensity * 100).toInt()}%)")
    }

    fun applyLUT(inputPath: String, mood: String, intensity: Int): VideoClip {
        return applyLUT(inputPath, mood, intensity / 100f)
    }
}

class CaptionsTool {
    fun generate(inputPath: String, style: String, language: String): VideoClip {
        val path = "content://visioncut/captions_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "Auto Subtitles ($style)", durationSeconds = 4f))
        return VideoClip(name = "Auto Captions & Subtitles", path = path, details = "Style: $style, Lang: $language")
    }

    fun autoGenerate(style: String): VideoClip = generate("input.mp4", style, "Auto")
}

class UpscaleTool {
    fun upscale(inputPath: String, targetRes: String = "4K", fps: Int = 60): VideoClip {
        val path = "content://visioncut/upscale_${System.currentTimeMillis()}.mp4"
        RenderQueue.add(RenderJob(title = "Super-Res $targetRes ($fps FPS)", durationSeconds = 6f))
        return VideoClip(name = "Super Resolution Upscale", path = path, details = "Target: $targetRes @ $fps FPS")
    }

    fun to4K(): VideoClip = upscale("input.mp4", "4K", 60)
}

class AIToolsStudio {
    val textToVideo = TextToVideoTool()
    val avatar = AvatarTool()
    val bgRemover = BgRemoverTool()
    val bg_remover = bgRemover
    val voice = VoiceTool()
    val lipSync = LipSyncTool()
    val autoCut = AutoCutTool()
    val auto_cut = autoCut
    val colorGrade = ColorGradeTool()
    val color = colorGrade
    val captions = CaptionsTool()
    val upscale = UpscaleTool()
}

class AIMasterAgent(
    private val tools: AIToolsStudio = AIToolsStudio(),
    private val promptParser: PromptParser = PromptParser()
) {
    suspend fun buildFromPrompt(
        userPrompt: String, 
        inputPath: String = "input.mp4",
        onProgress: (step: String, progress: Int) -> Unit = { _, _ -> }
    ): VideoProject = coroutineScope {
        
        onProgress("🧠 Parsing Prompt with Gemini", 0)
        val plan = promptParser.parse(userPrompt)
        
        val project = VideoProject()
        var currentPath = inputPath
        
        // 1. BUILD DYNAMIC QUEUE
        val queue = buildRenderQueue(plan, currentPath)
        val totalSteps = queue.size
        if (totalSteps == 0) return@coroutineScope project
        
        // 2. EXECUTE WITH PROPORTIONAL %
        queue.forEachIndexed { index, (stepName, job) ->
            try {
                val progress = ((index + 1) * 100) / totalSteps
                onProgress(stepName, progress)
                
                currentPath = job(currentPath)
                project.add(VideoClip(path = currentPath, name = stepName))
                
            } catch (e: Exception) {
                onProgress("❌ Error in: $stepName", ((index + 1) * 100) / totalSteps)
                throw e
            }
        }

        onProgress("✅ Finalizing & Merging", 100)
        project.finalPath = currentPath
        return@coroutineScope project.render()
    }
    
    private fun buildRenderQueue(plan: PromptParser.PromptPlan, startPath: String): List<Pair<String, suspend (String) -> String>> {
        val queue = mutableListOf<Pair<String, suspend (String) -> String>>()
        
        if (plan.isTextToVideo) queue.add("✨ Generating Video from Text" to { _ -> tools.textToVideo.generate(plan.prompt, plan.aspectRatio).path })
        if (plan.background != "none") queue.add("🪄 Removing & Replacing Background" to { p -> tools.bgRemover.replace(tools.bgRemover.remove(p), plan.background).path })
        if (plan.needsAvatar) queue.add("🎬 Creating Presenter Avatar" to { _ -> tools.avatar.create(plan.character ?: "Emma", plan.script ?: plan.prompt, plan.emotion, plan.needsEyeContact).path })
        if (plan.needsVoice) queue.add("🎙️ Generating Voice + LipSync" to { p -> 
            val voice = tools.voice.generate(plan.script ?: plan.prompt, plan.language, plan.voiceModel, plan.pitch, plan.speed)
            tools.lipSync.sync(p, voice.path).path 
        })
        if (plan.needsAutoCut) queue.add("✂️ Removing Silence" to { p -> tools.autoCut.removeSilence(p, plan.silenceThreshold).path })
        if (plan.mood != "none") queue.add("🎨 Applying ${plan.mood} LUT" to { p -> tools.colorGrade.applyLUT(p, plan.mood, plan.intensity).path })
        if (plan.needsCaptions) queue.add("📝 Generating ${plan.captionStyle} Captions" to { p -> tools.captions.generate(p, plan.captionStyle, plan.language).path })
        if (plan.needsUpscale) queue.add("🚀 Upscaling to ${plan.targetRes} ${plan.fps}fps" to { p -> tools.upscale.upscale(p, plan.targetRes, plan.fps).path })
        
        return queue
    }
}
