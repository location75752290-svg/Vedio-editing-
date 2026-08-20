package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.VisionCutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialVideoUri: Uri? = intent?.data ?: (if (intent?.action == Intent.ACTION_SEND) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
        } else null)

        setContent {
            VisionCutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBackground
                ) {
                    AppNavigation(initialVideoUri = initialVideoUri)
                }
            }
        }
    }
}
