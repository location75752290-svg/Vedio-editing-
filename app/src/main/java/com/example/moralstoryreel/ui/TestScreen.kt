package com.example.moralstoryreel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moralstoryreel.data.MoralStory
import com.example.moralstoryreel.data.SecretManager
import com.example.moralstoryreel.data.StoryRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    repository: StoryRepository = remember { StoryRepository() },
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var topicInput by remember { mutableStateOf("An arrogant eagle learns humility from a tiny hummingbird") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var generatedStory by remember { mutableStateOf<MoralStory?>(null) }

    val hasKey = remember { SecretManager.hasValidApiKey() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Reel Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Moral Story Reel Generator",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!hasKey) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Gemini API key is not configured in AI Studio Secrets yet. Please add GEMINI_API_KEY in Secrets panel.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Story Reel Prompt",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall
                        )

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            label = { Text("Moral Topic or Premise") },
                            placeholder = { Text("e.g. A poor boy returns a lost diamond ring") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("topic_input_field"),
                            minLines = 2,
                            maxLines = 4
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("English", "Hindi", "Urdu", "Spanish").forEach { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(lang) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (topicInput.isBlank()) return@Button
                                isLoading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val result = repository.generateStory(
                                        topic = topicInput,
                                        language = selectedLanguage
                                    )
                                    isLoading = false
                                    result.onSuccess {
                                        generatedStory = it
                                        snackbarHostState.showSnackbar("Reel script generated successfully!")
                                    }.onFailure {
                                        errorMessage = it.localizedMessage ?: "Failed to generate story script."
                                    }
                                }
                            },
                            enabled = !isLoading && topicInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_story_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating Script...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Reel Script")
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            generatedStory?.let { story ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = story.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                IconButton(
                                    onClick = {
                                        val fullScript = buildString {
                                            appendLine("🎬 Title: ${story.title}")
                                            appendLine("💡 Theme: ${story.theme}")
                                            appendLine("✨ Moral: ${story.moralLesson}\n")
                                            story.scenes.forEach { scene ->
                                                appendLine("--- Scene ${scene.sceneNumber} ---")
                                                appendLine("🗣️ Voiceover: ${scene.narration}")
                                                appendLine("🎨 Visual: ${scene.visualPrompt}")
                                                if (!scene.dialogue.isNullOrBlank()) {
                                                    appendLine("💬 Dialogue: ${scene.dialogue}")
                                                }
                                                appendLine()
                                            }
                                            appendLine("🏷️ ${story.hashtags.joinToString(" ")}")
                                        }
                                        clipboardManager.setText(AnnotatedString(fullScript))
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Full reel script copied!")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Script",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Text(
                                text = "Theme: ${story.theme} | Target: ${story.targetAudience}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💡 Moral: \"${story.moralLesson}\"",
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Storyboard Scenes (${story.scenes.size} Scenes)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                items(story.scenes) { scene ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Scene ${scene.sceneNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "🗣️ Voiceover Narration:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = scene.narration,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (!scene.dialogue.isNullOrBlank()) {
                                Column {
                                    Text(
                                        text = "💬 Dialogue:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = scene.dialogue,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "🎨 Visual Generation Prompt:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = scene.visualPrompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Hashtags: ${story.hashtags.joinToString(" ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
