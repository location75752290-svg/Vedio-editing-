package com.example.domain.repository

import android.content.Context
import com.example.domain.db.AppDatabase
import com.example.domain.db.ExportHistoryEntity
import com.example.domain.db.MediaItemEntity
import com.example.domain.db.ProjectEntity
import com.example.domain.model.ExportRecord
import com.example.domain.model.SharePlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class VisionCutRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val projectDao = db.projectDao()
    private val exportHistoryDao = db.exportHistoryDao()
    private val mediaDao = db.mediaDao()

    val projectsFlow: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val mediaItemsFlow: Flow<List<MediaItemEntity>> = mediaDao.getAllMediaItems()

    val exportHistoryFlow: Flow<List<ExportRecord>> = exportHistoryDao.getExportHistory().map { list ->
        list.map { entity ->
            val platform = SharePlatform.values().find { it.id == entity.platformId } ?: SharePlatform.GALLERY
            ExportRecord(
                id = entity.id,
                timestampMs = entity.timestampMs,
                title = entity.title,
                platform = platform,
                resolution = entity.resolution,
                framerate = entity.framerate,
                durationSeconds = entity.durationSeconds,
                fileSizeMb = entity.fileSizeMb,
                fileUri = entity.fileUri
            )
        }
    }

    suspend fun saveProjectAutoDraft(title: String, durationSec: Float, clipsJson: String) = withContext(Dispatchers.IO) {
        val draft = ProjectEntity(
            id = "draft_autosave",
            title = title,
            durationSeconds = durationSec,
            lastModifiedMs = System.currentTimeMillis(),
            isAutoSavedDraft = true,
            clipsDataJson = clipsJson
        )
        projectDao.insertProject(draft)
    }

    suspend fun getAutoSavedDraft(): ProjectEntity? = withContext(Dispatchers.IO) {
        projectDao.getAutoSavedDraft()
    }

    suspend fun saveUserProject(id: String, title: String, durationSec: Float, clipsJson: String) = withContext(Dispatchers.IO) {
        val project = ProjectEntity(
            id = id,
            title = title,
            durationSeconds = durationSec,
            lastModifiedMs = System.currentTimeMillis(),
            isAutoSavedDraft = false,
            clipsDataJson = clipsJson
        )
        projectDao.insertProject(project)
    }

    suspend fun recordExport(record: ExportRecord) = withContext(Dispatchers.IO) {
        val entity = ExportHistoryEntity(
            id = record.id,
            timestampMs = record.timestampMs,
            title = record.title,
            platformId = record.platform.id,
            resolution = record.resolution,
            framerate = record.framerate,
            durationSeconds = record.durationSeconds,
            fileSizeMb = record.fileSizeMb,
            fileUri = record.fileUri
        )
        exportHistoryDao.insertExportRecord(entity)
    }

    suspend fun importMediaItem(name: String, uri: String, type: String, durationMs: Long, sizeBytes: Long) = withContext(Dispatchers.IO) {
        val item = MediaItemEntity(
            id = "media_${System.currentTimeMillis()}",
            name = name,
            uri = uri,
            mediaType = type,
            durationMs = durationMs,
            sizeBytes = sizeBytes
        )
        mediaDao.insertMediaItem(item)
    }
}
