package com.example.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

data class RenderJob(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val durationSeconds: Float = 10f,
    val onComplete: (String) -> Unit = {}
)

data class RenderQueueStatus(
    val activeJob: RenderJob? = null,
    val activeJobIndex: Int = 0,
    val totalJobs: Int = 0,
    val progressPercent: Int = 0,
    val notificationText: String = "",
    val isRendering: Boolean = false,
    val completedJobs: List<RenderJob> = emptyList()
)

object RenderQueue {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val queue = ConcurrentLinkedQueue<RenderJob>()

    private val _status = MutableStateFlow(RenderQueueStatus())
    val status: StateFlow<RenderQueueStatus> = _status.asStateFlow()

    private var totalJobCount = 0
    private var processedJobCount = 0

    fun add(job: RenderJob) {
        queue.add(job)
        totalJobCount++
        updateNotification()
        if (!_status.value.isRendering) {
            processNextJob()
        }
    }

    private fun processNextJob() {
        val nextJob = queue.poll()
        if (nextJob == null) {
            _status.value = _status.value.copy(
                activeJob = null,
                isRendering = false,
                notificationText = if (processedJobCount > 0) "All background renders completed! ✅" else ""
            )
            return
        }

        processedJobCount++
        val currentJobNum = processedJobCount
        val total = totalJobCount

        _status.value = _status.value.copy(
            activeJob = nextJob,
            activeJobIndex = currentJobNum,
            totalJobs = total,
            isRendering = true,
            progressPercent = 0,
            notificationText = "Job $currentJobNum/$total: ${nextJob.title} 0%"
        )

        scope.launch {
            for (p in 0..100 step 5) {
                delay(120)
                val notif = "Job $currentJobNum/$total: ${nextJob.title} $p%"
                _status.value = _status.value.copy(
                    progressPercent = p,
                    notificationText = notif
                )
            }
            val outputPath = "content://visioncut/render_${nextJob.title.lowercase().replace(" ", "_")}_${System.currentTimeMillis()}.mp4"
            nextJob.onComplete(outputPath)

            val updatedCompleted = _status.value.completedJobs + nextJob
            _status.value = _status.value.copy(completedJobs = updatedCompleted)

            // Process next background job in queue 1 by 1
            processNextJob()
        }
    }

    private fun updateNotification() {
        if (!_status.value.isRendering && queue.isNotEmpty()) {
            val pending = queue.size
            _status.value = _status.value.copy(
                notificationText = "$pending job(s) in render queue..."
            )
        }
    }

    fun clearQueue() {
        queue.clear()
        totalJobCount = 0
        processedJobCount = 0
        _status.value = RenderQueueStatus()
    }
}
