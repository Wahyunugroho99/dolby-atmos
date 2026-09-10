package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AudioDemoType
import com.example.engine.DemoPlayerState
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyElectricBlue
import com.example.ui.theme.DolbyGlowCyan
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun SpatialDemoPlayerCard(
    playerState: DemoPlayerState,
    onTrackSelected: (AudioDemoType) -> Unit,
    onLocalFileSelected: (Uri, String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStopPlayback: () -> Unit,
    isDolbyEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "Custom Track"
            onLocalFileSelected(uri, fileName)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DolbyDarkSurfaceVariant)
            .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        // Card Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = DolbyCyanPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.spatial_demo_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyTextPrimary
                        )
                    )
                    Text(
                        text = "Real-time DSP Acoustic Spatializer Test",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DolbyTextSecondary
                        )
                    )
                }
            }

            // Real-time Session ID badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DolbyDarkSurface)
                    .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Session: ${playerState.audioSessionId}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DolbyCyanPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Track Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (track in AudioDemoType.values()) {
                val isSelected = track == playerState.currentTrack
                val borderCol = if (isSelected) DolbyCyanPrimary else DolbyDarkCardBorder
                val bgCol = if (isSelected) DolbyCyanPrimary.copy(alpha = 0.2f) else DolbyDarkSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                        .clickable {
                            if (track == AudioDemoType.LOCAL_FILE) {
                                filePickerLauncher.launch("audio/*")
                            } else {
                                onTrackSelected(track)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("demo_track_${track.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (track == AudioDemoType.LOCAL_FILE) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = if (isSelected) DolbyCyanPrimary else DolbyTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = stringResource(track.titleRes),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DolbyCyanPrimary else DolbyTextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Real-time 16-Band Animated Frequency Visualizer Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DolbyDarkSurface)
                .border(1.dp, DolbyDarkCardBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                val bands = playerState.spectrumLevels
                val count = bands.size
                val barSpacing = 4.dp.toPx()
                val totalSpacing = barSpacing * (count - 1)
                val barWidth = (width - totalSpacing) / count

                for (i in 0 until count) {
                    val rawLevel = if (playerState.isPlaying) bands[i] else 0.05f
                    val barHeight = (rawLevel * height).coerceIn(4.dp.toPx(), height)
                    val x = i * (barWidth + barSpacing)
                    val y = height - barHeight

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                if (isDolbyEnabled) DolbyCyanPrimary else DolbyTextSecondary,
                                if (isDolbyEnabled) DolbyElectricBlue else DolbyDarkCardBorder,
                                if (isDolbyEnabled) DolbyAmberAccent else DolbyDarkCardBorder
                            ),
                            startY = y,
                            endY = height
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Track Progress & Duration
        Column {
            val progress = if (playerState.durationSec > 0) {
                (playerState.currentPositionSec.toFloat() / playerState.durationSec).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = DolbyCyanPrimary,
                trackColor = DolbyDarkSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playerState.currentPositionSec),
                    style = MaterialTheme.typography.labelSmall.copy(color = DolbyTextSecondary)
                )
                Text(
                    text = formatTime(playerState.durationSec),
                    style = MaterialTheme.typography.labelSmall.copy(color = DolbyTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Play / Pause / Stop Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop button
            IconButton(
                onClick = onStopPlayback,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DolbyDarkSurface)
                    .border(1.dp, DolbyDarkCardBorder, CircleShape)
                    .testTag("demo_stop_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = stringResource(R.string.stop),
                    tint = DolbyAmberAccent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Play / Pause button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = if (playerState.isPlaying) 12.dp else 4.dp,
                        shape = CircleShape,
                        ambientColor = DolbyGlowCyan,
                        spotColor = DolbyCyanPrimary
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(DolbyCyanPrimary, DolbyElectricBlue)
                        )
                    )
                    .clickable(onClick = onTogglePlayPause)
                    .testTag("demo_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playerState.isPlaying) stringResource(R.string.paused) else stringResource(R.string.playing),
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
