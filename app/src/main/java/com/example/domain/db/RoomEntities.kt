package com.example.domain.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationSeconds: Float,
    val lastModifiedMs: Long = System.currentTimeMillis(),
    val thumbnailResId: Int = 0,
    val thumbnailUri: String = "",
    val isAutoSavedDraft: Boolean = false,
    val clipsDataJson: String = ""
)

@Entity(tableName = "export_history")
data class ExportHistoryEntity(
    @PrimaryKey val id: String,
    val timestampMs: Long,
    val title: String,
    val platformId: String,
    val resolution: String,
    val framerate: String,
    val durationSeconds: Float,
    val fileSizeMb: Float,
    val fileUri: String
)

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val uri: String,
    val mediaType: String, // VIDEO, IMAGE, AUDIO
    val durationMs: Long,
    val sizeBytes: Long,
    val addedAtMs: Long = System.currentTimeMillis()
)
