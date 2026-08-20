package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import com.example.domain.model.VisionCutProjectData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ProjectFileManager {

    private const val PROJECTS_DIR_NAME = "Documents/VisionCutAI/Projects"
    private const val THUMBNAILS_DIR_NAME = "Documents/VisionCutAI/Thumbnails"
    private const val VCP_EXTENSION = ".vcp"

    private val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }

    /**
     * Gets the target directory for saving .vcp project files.
     */
    fun getProjectsDir(context: Context): File {
        val publicDocDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "VisionCutAI/Projects"
        )
        if (!publicDocDir.exists()) {
            publicDocDir.mkdirs()
        }

        if (publicDocDir.exists() && publicDocDir.canWrite()) {
            return publicDocDir
        }

        // Fallback to app external storage if public directory is unavailable
        val appSpecificDir = File(context.getExternalFilesDir(null), PROJECTS_DIR_NAME)
        if (!appSpecificDir.exists()) {
            appSpecificDir.mkdirs()
        }
        return appSpecificDir
    }

    /**
     * Gets directory for storing project thumbnails.
     */
    fun getThumbnailsDir(context: Context): File {
        val thumbDir = File(context.getExternalFilesDir(null), THUMBNAILS_DIR_NAME)
        if (!thumbDir.exists()) {
            thumbDir.mkdirs()
        }
        return thumbDir
    }

    /**
     * Saves a VisionCutProjectData object as a JSON file (.vcp) in Documents/VisionCutAI/Projects/
     */
    suspend fun saveProject(
        context: Context,
        project: VisionCutProjectData
    ): File = withContext(Dispatchers.IO) {
        val projectsDir = getProjectsDir(context)
        val sanitizedName = project.name.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
        val timestamp = System.currentTimeMillis()
        val fileName = "${sanitizedName}_${timestamp}$VCP_EXTENSION"
        val projectFile = File(projectsDir, fileName)

        // Try to capture a thumbnail image if videoUri is valid and no thumbnail exists
        val thumbnailPath = if (project.thumbnailPath.isBlank() && project.videoUri.isNotBlank()) {
            generateAndSaveThumbnail(context, "${sanitizedName}_${timestamp}", project.videoUri) ?: ""
        } else {
            project.thumbnailPath
        }

        val updatedProject = project.copy(
            version = "1.8",
            lastModifiedMs = timestamp,
            filePath = projectFile.absolutePath,
            thumbnailPath = thumbnailPath
        )

        val jsonString = gson.toJson(updatedProject)

        OutputStreamWriter(FileOutputStream(projectFile), Charsets.UTF_8).use { writer ->
            writer.write(jsonString)
        }

        projectFile
    }

    /**
     * Shares / Exports a .vcp project file with friends using Intent chooser.
     */
    fun shareProjectFile(context: Context, project: VisionCutProjectData) {
        val filePathToShare = project.filePath.ifBlank {
            // Find existing file if saved
            val file = File(getProjectsDir(context), "${project.name.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")}_${project.id.take(8)}$VCP_EXTENSION")
            if (file.exists()) file.absolutePath else ""
        }

        if (filePathToShare.isBlank()) return
        val file = File(filePathToShare)
        if (!file.exists()) return

        try {
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(android.content.Intent.EXTRA_STREAM, contentUri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share .vcp Project"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Loads a project from a .vcp file path.
     */
    suspend fun loadProjectFromFile(file: File): VisionCutProjectData? = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) return@withContext null
            file.reader(Charsets.UTF_8).use { reader ->
                gson.fromJson(reader, VisionCutProjectData::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads a project from a Uri (e.g. from file picker intent).
     */
    suspend fun loadProjectFromUri(context: Context, uri: Uri): VisionCutProjectData? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    gson.fromJson(reader, VisionCutProjectData::class.java)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Scans Documents/VisionCutAI/Projects/ and lists all saved .vcp projects.
     */
    suspend fun listSavedProjects(context: Context): List<VisionCutProjectData> = withContext(Dispatchers.IO) {
        val projectsList = mutableListOf<VisionCutProjectData>()
        val directories = listOf(
            getProjectsDir(context),
            File(context.getExternalFilesDir(null), PROJECTS_DIR_NAME)
        ).distinct()

        for (dir in directories) {
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles { _, name -> name.endsWith(VCP_EXTENSION, ignoreCase = true) }
                    ?.forEach { file ->
                        val project = loadProjectFromFile(file)
                        if (project != null) {
                            projectsList.add(project.copy(filePath = file.absolutePath))
                        }
                    }
            }
        }

        // Return distinct by id or filePath, sorted by last modified timestamp descending
        projectsList
            .distinctBy { it.filePath.ifBlank { it.id } }
            .sortedByDescending { it.lastModifiedMs }
    }

    /**
     * Deletes a .vcp project file and associated thumbnail.
     */
    suspend fun deleteProject(context: Context, project: VisionCutProjectData): Boolean = withContext(Dispatchers.IO) {
        var deleted = false
        if (project.filePath.isNotBlank()) {
            val file = File(project.filePath)
            if (file.exists()) {
                deleted = file.delete()
            }
        }
        if (project.thumbnailPath.isNotBlank()) {
            val thumbFile = File(project.thumbnailPath)
            if (thumbFile.exists()) {
                thumbFile.delete()
            }
        }
        deleted
    }

    /**
     * Generates a frame thumbnail bitmap from video URI and saves it as JPEG.
     */
    private fun generateAndSaveThumbnail(context: Context, projectId: String, videoUriStr: String): String? {
        val retriever = MediaMetadataRetriever()
        return try {
            val uri = Uri.parse(videoUriStr)
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime

            if (bitmap != null) {
                val thumbFile = File(getThumbnailsDir(context), "thumb_${projectId}.jpg")
                FileOutputStream(thumbFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                bitmap.recycle()
                thumbFile.absolutePath
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }
    }

    /**
     * Helper to format timestamps for recent project cards.
     */
    fun formatLastEditedTime(timestampMs: Long): String {
        if (timestampMs <= 0) return "Just now"
        val diffMs = System.currentTimeMillis() - timestampMs
        val minutes = diffMs / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 2 -> "Just now"
            minutes < 60 -> "$minutes mins ago"
            hours < 24 -> "$hours hours ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestampMs))
        }
    }

    /**
     * Helper to create sample initial projects if none exist.
     */
    suspend fun createDemoProjectsIfEmpty(context: Context): List<VisionCutProjectData> = withContext(Dispatchers.IO) {
        val existing = listSavedProjects(context)
        if (existing.isNotEmpty()) return@withContext existing

        val now = System.currentTimeMillis()
        val demo1 = VisionCutProjectData(
            id = "demo_cyberpunk",
            name = "Cyberpunk Neo City Vlog",
            createdTimeMs = now - 7200000L,
            lastModifiedMs = now - 7200000L,
            videoUri = "android.resource://${context.packageName}/raw/demo_video",
            videoDurationMs = 165000L,
            playheadPositionMs = 12500L,
            speed = 1.0f,
            filePath = ""
        )
        val demo2 = VisionCutProjectData(
            id = "demo_sunset",
            name = "Golden Hour Cinematic Reel",
            createdTimeMs = now - 86400000L,
            lastModifiedMs = now - 86400000L,
            videoUri = "android.resource://${context.packageName}/raw/demo_video",
            videoDurationMs = 58000L,
            playheadPositionMs = 5000L,
            speed = 1.25f,
            filePath = ""
        )

        saveProject(context, demo1)
        saveProject(context, demo2)

        listSavedProjects(context)
    }
}
