package com.example.domain.model

import com.google.gson.annotations.SerializedName

data class ProjectCutItem(
    @SerializedName("id") val id: String,
    @SerializedName("startMs") val startMs: Long,
    @SerializedName("endMs") val endMs: Long,
    @SerializedName("label") val label: String = ""
)

data class ProjectCropConfig(
    @SerializedName("ratio") val ratio: String = "Original",
    @SerializedName("rectX") val rectX: Float = 0f,
    @SerializedName("rectY") val rectY: Float = 0f,
    @SerializedName("rectWidth") val rectWidth: Float = 1f,
    @SerializedName("rectHeight") val rectHeight: Float = 1f
)

data class ProjectFilterConfig(
    @SerializedName("filterName") val filterName: String = "None",
    @SerializedName("intensity") val intensity: Float = 1.0f,
    @SerializedName("brightness") val brightness: Float = 0f,
    @SerializedName("contrast") val contrast: Float = 1.0f,
    @SerializedName("saturation") val saturation: Float = 1.0f,
    @SerializedName("vignette") val vignette: Float = 0f
)

data class ProjectTextOverlay(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("startMs") val startMs: Long,
    @SerializedName("endMs") val endMs: Long,
    @SerializedName("positionX") val positionX: Float = 0.5f,
    @SerializedName("positionY") val positionY: Float = 0.8f,
    @SerializedName("colorHex") val colorHex: String = "#FFFFFF",
    @SerializedName("fontSizeSp") val fontSizeSp: Float = 24f,
    @SerializedName("fontName") val fontName: String = "Bold"
)

data class ProjectKeyframeItem(
    @SerializedName("timeMs") val timeMs: Long,
    @SerializedName("scale") val scale: Float = 1.0f,
    @SerializedName("rotation") val rotation: Float = 0f,
    @SerializedName("translateX") val translateX: Float = 0f,
    @SerializedName("translateY") val translateY: Float = 0f
)

data class ProjectCaptionWord(
    @SerializedName("word") val word: String,
    @SerializedName("startMs") val startMs: Long,
    @SerializedName("endMs") val endMs: Long
)

data class ProjectCaptionItem(
    @SerializedName("id") val id: String,
    @SerializedName("startMs") val startMs: Long,
    @SerializedName("endMs") val endMs: Long,
    @SerializedName("text") val text: String,
    @SerializedName("words") val words: List<ProjectCaptionWord> = emptyList()
)

data class ProjectMusicTrack(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("uri") val uri: String = "",
    @SerializedName("startMs") val startMs: Long = 0L,
    @SerializedName("durationMs") val durationMs: Long = 0L,
    @SerializedName("volume") val volume: Float = 0.8f
)

data class ProjectBgRemoverConfig(
    @SerializedName("enabled") val enabled: Boolean = false,
    @SerializedName("mode") val mode: String = "green_screen",
    @SerializedName("colorHex") val colorHex: String = "#00FF00",
    @SerializedName("featherAmount") val featherAmount: Int = 30,
    @SerializedName("blurAmount") val blurAmount: Int = 20,
    @SerializedName("isHighQuality") val isHighQuality: Boolean = false,
    @SerializedName("replaceBgUri") val replaceBgUri: String = ""
)

data class VisionCutProjectData(
    @SerializedName("version") val version: String = "1.8",
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("createdTimeMs") val createdTimeMs: Long,
    @SerializedName("lastModifiedMs") val lastModifiedMs: Long,
    @SerializedName("videoUri") val videoUri: String,
    @SerializedName("videoName") val videoName: String = "",
    @SerializedName("videoDurationMs") val videoDurationMs: Long,
    @SerializedName("projectDuration") val projectDuration: Float = 0f,
    @SerializedName("playheadPositionMs") val playheadPositionMs: Long = 0L,
    @SerializedName("musicUri") val musicUri: String = "",
    @SerializedName("cuts") val cuts: List<ProjectCutItem> = emptyList(),
    @SerializedName("crop") val crop: ProjectCropConfig = ProjectCropConfig(),
    @SerializedName("speed") val speed: Float = 1.0f,
    @SerializedName("filters") val filters: ProjectFilterConfig = ProjectFilterConfig(),
    @SerializedName("text") val text: List<ProjectTextOverlay> = emptyList(),
    @SerializedName("keyframes") val keyframes: List<ProjectKeyframeItem> = emptyList(),
    @SerializedName("captions") val captions: List<ProjectCaptionItem> = emptyList(),
    @SerializedName("music") val music: List<ProjectMusicTrack> = emptyList(),
    @SerializedName("bgRemover") val bgRemover: ProjectBgRemoverConfig = ProjectBgRemoverConfig(),
    @SerializedName("thumbnailPath") val thumbnailPath: String = "",
    @SerializedName("filePath") val filePath: String = ""
)
