package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RenderQueue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.RadiantPink
import com.example.ui.theme.TextPrimary

@Composable
fun RenderQueueNotificationBar(modifier: Modifier = Modifier) {
    val renderStatus by RenderQueue.status.collectAsState()

    AnimatedVisibility(
        visible = renderStatus.notificationText.isNotBlank(),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        BoxWithConstraints(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianBackground.copy(alpha = 0.95f))
                    .padding(12.dp)
                    .testTag("render_queue_notification_bar"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (renderStatus.isRendering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = RadiantPink,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = renderStatus.notificationText,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (renderStatus.isRendering) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { renderStatus.progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = RadiantPink,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
