package com.example.ui.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.RemoveBackgroundScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.TimelineEditorScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    WELCOME,
    MAIN,
    EDITOR,
    REMOVE_BG,
    VIDEO_EDITOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    initialVideoUri: Uri? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var currentNavTab by remember { mutableStateOf(NavTab.HOME) }
    var showPreviewMenu by remember { mutableStateOf(false) }

    var selectedVideoUri by remember { mutableStateOf<Uri?>(initialVideoUri) }
    var selectedVideoFileName by remember {
        mutableStateOf(
            initialVideoUri?.let { getFileNameFromUri(context, it) } ?: "Imported Video"
        )
    }
    var activeVcpProjectData by remember { mutableStateOf<com.example.domain.model.VisionCutProjectData?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val vcpFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val loadedProject = com.example.engine.ProjectFileManager.loadProjectFromUri(context, uri)
                if (loadedProject != null) {
                    activeVcpProjectData = loadedProject
                    selectedVideoUri = if (loadedProject.videoUri.isNotBlank()) Uri.parse(loadedProject.videoUri) else null
                    selectedVideoFileName = loadedProject.name
                    currentScreen = AppScreen.VIDEO_EDITOR
                    snackbarHostState.showSnackbar("Opened .vcp project: ${loadedProject.name}")
                } else {
                    snackbarHostState.showSnackbar("Could not parse .vcp project file")
                }
            }
        }
    }

    fun openVcpProjectPicker() {
        try {
            vcpFilePickerLauncher.launch(arrayOf("*/*"))
        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("Unable to launch file picker") }
        }
    }

    LaunchedEffect(initialVideoUri) {
        if (initialVideoUri != null) {
            selectedVideoUri = initialVideoUri
            selectedVideoFileName = getFileNameFromUri(context, initialVideoUri) ?: "Imported Video"
            currentScreen = AppScreen.VIDEO_EDITOR
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoFileName = getFileNameFromUri(context, uri) ?: "Selected_Video.mp4"
            currentScreen = AppScreen.VIDEO_EDITOR
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Please select a video")
            }
        }
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            videoPickerLauncher.launch("video/*")
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Storage access permission needed to pick videos")
            }
            // Fallback launch for standard system picker
            videoPickerLauncher.launch("video/*")
        }
    }

    fun openVideoPicker() {
        val hasPermission = ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED
        if (hasPermission || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            videoPickerLauncher.launch("video/*")
        } else {
            permissionLauncher.launch(permissionToRequest)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // Main Screen Content based on currentScreen
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onNavigateNext = {
                            currentScreen = AppScreen.MAIN
                            currentNavTab = NavTab.HOME
                        }
                    )
                }

                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        onSkip = { currentScreen = AppScreen.WELCOME },
                        onGetStarted = { currentScreen = AppScreen.WELCOME }
                    )
                }

                AppScreen.WELCOME -> {
                    WelcomeScreen(
                        onSignIn = {
                            currentScreen = AppScreen.MAIN
                            currentNavTab = NavTab.HOME
                            scope.launch {
                                snackbarHostState.showSnackbar("Signed in successfully to VisionCut AI")
                            }
                        },
                        onCreateAccount = {
                            currentScreen = AppScreen.MAIN
                            currentNavTab = NavTab.HOME
                            scope.launch {
                                snackbarHostState.showSnackbar("Account created! Welcome to VisionCut AI")
                            }
                        },
                        onContinueAsGuest = {
                            currentScreen = AppScreen.MAIN
                            currentNavTab = NavTab.HOME
                            scope.launch {
                                snackbarHostState.showSnackbar("Continuing as Guest Mode")
                            }
                        }
                    )
                }

                AppScreen.MAIN -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = ObsidianBackground,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            BottomNavBar(
                                currentTab = currentNavTab,
                                onTabSelected = { currentNavTab = it }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentNavTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition"
                            ) { tab ->
                                when (tab) {
                                    NavTab.HOME -> {
                                        HomeScreen(
                                            onNewProjectClick = {
                                                activeVcpProjectData = null
                                                currentScreen = AppScreen.EDITOR
                                            },
                                            onOpenProjectFile = { openVcpProjectPicker() },
                                            onLoadVcpProject = { proj ->
                                                activeVcpProjectData = proj
                                                selectedVideoUri = if (proj.videoUri.isNotBlank()) Uri.parse(proj.videoUri) else null
                                                selectedVideoFileName = proj.name
                                                currentScreen = AppScreen.VIDEO_EDITOR
                                            },
                                            onNavigateToTemplates = { currentNavTab = NavTab.TEMPLATES },
                                            onNavigateToAiTools = { currentNavTab = NavTab.AI_TOOLS },
                                            onNavigateToProjects = { currentNavTab = NavTab.PROJECTS },
                                            onNavigateToRemoveBg = { currentScreen = AppScreen.REMOVE_BG },
                                            onOpenVideoPicker = {
                                                activeVcpProjectData = null
                                                openVideoPicker()
                                            }
                                        )
                                    }

                                    NavTab.TEMPLATES -> {
                                        TemplatesScreen(
                                            onUseTemplate = { template ->
                                                currentScreen = AppScreen.EDITOR
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Loaded template into Timeline: ${template.title}")
                                                }
                                            }
                                        )
                                    }

                                    NavTab.AI_TOOLS -> {
                                        AiToolsScreen(
                                            onOpenRemoveBg = { currentScreen = AppScreen.REMOVE_BG }
                                        )
                                    }

                                    NavTab.PROJECTS -> {
                                        ProjectsScreen(
                                            onOpenProject = { project ->
                                                currentScreen = AppScreen.EDITOR
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Opened Timeline: ${project.title}")
                                                }
                                            },
                                            onLoadVcpProject = { proj ->
                                                activeVcpProjectData = proj
                                                selectedVideoUri = if (proj.videoUri.isNotBlank()) Uri.parse(proj.videoUri) else null
                                                selectedVideoFileName = proj.name
                                                currentScreen = AppScreen.VIDEO_EDITOR
                                            },
                                            onOpenProjectFile = { openVcpProjectPicker() }
                                        )
                                    }

                                    NavTab.PROFILE -> {
                                        ProfileScreen(
                                            onLogout = {
                                                currentScreen = AppScreen.WELCOME
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Signed out")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                AppScreen.EDITOR -> {
                    TimelineEditorScreen(
                        onNavigateBack = { currentScreen = AppScreen.MAIN }
                    )
                }

                AppScreen.REMOVE_BG -> {
                    RemoveBackgroundScreen(
                        onNavigateBack = { currentScreen = AppScreen.MAIN }
                    )
                }

                AppScreen.VIDEO_EDITOR -> {
                    VideoEditorScreen(
                        videoUri = selectedVideoUri,
                        fileName = selectedVideoFileName,
                        initialProjectData = activeVcpProjectData,
                        onNavigateBack = { currentScreen = AppScreen.MAIN },
                        onNextClick = {
                            currentScreen = AppScreen.EDITOR
                            scope.launch {
                                snackbarHostState.showSnackbar("Loaded $selectedVideoFileName into Timeline Editor")
                            }
                        }
                    )
                }
            }
        }

        // Top-Right Quick Screen Switcher Chip (Allows seamless testing of Splash, Onboarding, Welcome, or Home)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 8.dp, end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .testTag("preview_switcher_chip")
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurface.copy(alpha = 0.92f))
                    .clickable { showPreviewMenu = !showPreviewMenu }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Screen Switcher",
                        tint = RadiantPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " View: ${
                            when (currentScreen) {
                                AppScreen.SPLASH -> "1. Splash"
                                AppScreen.ONBOARDING -> "2. Onboarding"
                                AppScreen.WELCOME -> "3. Welcome"
                                AppScreen.MAIN -> "4. ${currentNavTab.label}"
                                AppScreen.EDITOR -> "9. Editor"
                                AppScreen.REMOVE_BG -> "10. Remove BG"
                                AppScreen.VIDEO_EDITOR -> "Video Editor"
                            }
                        }",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            DropdownMenu(
                expanded = showPreviewMenu,
                onDismissRequest = { showPreviewMenu = false },
                modifier = Modifier.background(CharcoalSurface)
            ) {
                DropdownMenuItem(
                    text = { Text("1. Splash Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.SPLASH
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("2. Onboarding (3 Pages)", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.ONBOARDING
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("3. Welcome Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.WELCOME
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("4. Home Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.MAIN
                        currentNavTab = NavTab.HOME
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("5. Templates Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.MAIN
                        currentNavTab = NavTab.TEMPLATES
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("6. AI Tools Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.MAIN
                        currentNavTab = NavTab.AI_TOOLS
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("7. Projects Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.MAIN
                        currentNavTab = NavTab.PROJECTS
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("8. Profile Screen", color = TextPrimary, fontSize = 13.sp) },
                    onClick = {
                        currentScreen = AppScreen.MAIN
                        currentNavTab = NavTab.PROFILE
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("🎬 9. Timeline Editor", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    onClick = {
                        currentScreen = AppScreen.EDITOR
                        showPreviewMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("✂️ 10. Remove BG Tool", color = RadiantPink, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    onClick = {
                        currentScreen = AppScreen.REMOVE_BG
                        showPreviewMenu = false
                    }
                )
            }
        }
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
    }
    if (name == null) {
        name = uri.path?.let { path ->
            val cut = path.lastIndexOf('/')
            if (cut != -1) path.substring(cut + 1) else path
        }
    }
    return name
}
