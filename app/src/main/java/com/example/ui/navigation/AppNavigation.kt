package com.example.ui.navigation

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavTab
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.TimelineEditorScreen
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
    EDITOR
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN) }
    var currentNavTab by remember { mutableStateOf(NavTab.HOME) }
    var showPreviewMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                        onNavigateNext = { currentScreen = AppScreen.ONBOARDING }
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
                                            onNewProjectClick = { currentScreen = AppScreen.EDITOR },
                                            onNavigateToTemplates = { currentNavTab = NavTab.TEMPLATES },
                                            onNavigateToAiTools = { currentNavTab = NavTab.AI_TOOLS },
                                            onNavigateToProjects = { currentNavTab = NavTab.PROJECTS }
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
                                        AiToolsScreen()
                                    }

                                    NavTab.PROJECTS -> {
                                        ProjectsScreen(
                                            onOpenProject = { project ->
                                                currentScreen = AppScreen.EDITOR
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Opened Timeline: ${project.title}")
                                                }
                                            }
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
            }
        }
    }
}
