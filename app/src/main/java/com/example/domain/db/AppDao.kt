package com.example.domain.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModifiedMs DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isAutoSavedDraft = 1 LIMIT 1")
    suspend fun getAutoSavedDraft(): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface ExportHistoryDao {
    @Query("SELECT * FROM export_history ORDER BY timestampMs DESC")
    fun getExportHistory(): Flow<List<ExportHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportRecord(record: ExportHistoryEntity)

    @Query("DELETE FROM export_history WHERE id = :id")
    suspend fun deleteExportRecord(id: String)
}

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY addedAtMs DESC")
    fun getAllMediaItems(): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMediaItem(id: String)
}
