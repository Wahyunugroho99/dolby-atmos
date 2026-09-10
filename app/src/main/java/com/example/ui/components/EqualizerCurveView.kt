package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DolbyProfileConfigDefaults
import com.example.data.model.EqPreset
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
import com.example.ui.theme.DolbyTextTertiary

@Composable
fun EqualizerCurveView(
    eqGains: List<Float>,
    currentPreset: EqPreset,
    onGainChanged: (Int, Float) -> Unit,
    onPresetSelected: (EqPreset) -> Unit,
    onResetEq: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eqWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DolbyDarkSurfaceVariant)
            .border(1.dp, DolbyDarkCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        // Equalizer Header & Reset
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
                        text = stringResource(R.string.graphic_equalizer),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyTextPrimary
                        )
                    )
                    Text(
                        text = "10-Band Precision Acoustic Tuning",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DolbyTextSecondary
                        )
                    )
                }
            }

            OutlinedButton(
                onClick = onResetEq,
                enabled = isEnabled,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DolbyCyanPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DolbyDarkCardBorder),
                modifier = Modifier.testTag("reset_eq_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.reset_eq),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (preset in EqPreset.values()) {
                val isSelected = preset == currentPreset
                val borderCol = if (isSelected && isEnabled) DolbyCyanPrimary else DolbyDarkCardBorder
                val bgCol = if (isSelected && isEnabled) DolbyCyanPrimary.copy(alpha = 0.2f) else DolbyDarkSurface

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                        .clickable(enabled = isEnabled) { onPresetSelected(preset) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("preset_chip_${preset.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(preset.labelRes),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected && isEnabled) DolbyCyanPrimary else DolbyTextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Curve Canvas with 0dB, +12dB, -12dB reference lines and smooth Bezier curve
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DolbyDarkSurface)
                .border(1.dp, DolbyDarkCardBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                val midY = height / 2f

                // Reference Grid Lines
                val linePaint = Color(0xFF1E293B)
                val textGridColor = Color(0xFF475569)

                // +12dB (top), +6dB, 0dB (center), -6dB, -12dB (bottom)
                drawLine(
                    color = linePaint,
                    start = Offset(0f, height * 0.15f),
                    end = Offset(width, height * 0.15f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = linePaint.copy(alpha = 0.6f),
                    start = Offset(0f, height * 0.325f),
                    end = Offset(width, height * 0.325f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(0f, midY),
                    end = Offset(width, midY),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = linePaint.copy(alpha = 0.6f),
                    start = Offset(0f, height * 0.675f),
                    end = Offset(width, height * 0.675f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = linePaint,
                    start = Offset(0f, height * 0.85f),
                    end = Offset(width, height * 0.85f),
                    strokeWidth = 1f
                )

                val gains = if (eqGains.size == 10) eqGains else List(10) { 0f }
                val numPoints = gains.size
                val stepX = width / (numPoints - 1).coerceAtLeast(1)

                val points = mutableListOf<Offset>()
                for (i in 0 until numPoints) {
                    val gain = if (isEnabled) gains[i] else 0f
                    // Map -12dB .. +12dB to height .. 0 (with 15% margin)
                    val normalized = (gain.coerceIn(-12f, 12f) + 12f) / 24f // 0..1
                    val y = height * 0.85f - normalized * (height * 0.70f)
                    val x = i * stepX
                    points.add(Offset(x, y))
                }

                if (points.isNotEmpty()) {
                    // Build smooth cubic bezier path
                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlX = (p0.x + p1.x) / 2f
                            cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                    }

                    // Fill under the curve
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                (if (isEnabled) DolbyCyanPrimary else DolbyDarkCardBorder).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw the curve stroke
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                if (isEnabled) DolbyCyanPrimary else DolbyTextTertiary,
                                if (isEnabled) DolbyElectricBlue else DolbyTextTertiary,
                                if (isEnabled) DolbyAmberAccent else DolbyTextTertiary
                            )
                        ),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw frequency control node points
                    for (p in points) {
                        drawCircle(
                            color = if (isEnabled) DolbyDarkSurface else DolbyDarkSurfaceVariant,
                            radius = 4.5.dp.toPx(),
                            center = p
                        )
                        drawCircle(
                            color = if (isEnabled) DolbyCyanPrimary else DolbyTextSecondary,
                            radius = 3.dp.toPx(),
                            center = p
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 10 Interactive Frequency Band Sliders (Scrollable row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val labels = DolbyProfileConfigDefaults.FREQUENCY_LABELS
            val gains = if (eqGains.size == 10) eqGains else List(10) { 0f }

            for (index in 0 until 10) {
                val currentGain = gains.getOrNull(index) ?: 0f
                EqBandColumn(
                    bandIndex = index,
                    freqLabel = labels.getOrElse(index) { "${index}k" },
                    gainDb = currentGain,
                    isEnabled = isEnabled,
                    onGainChanged = { newGain ->
                        onGainChanged(index, newGain)
                    }
                )
            }
        }
    }
}

@Composable
private fun EqBandColumn(
    bandIndex: Int,
    freqLabel: String,
    gainDb: Float,
    isEnabled: Boolean,
    onGainChanged: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DolbyDarkSurface.copy(alpha = 0.7f))
            .border(1.dp, DolbyDarkCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("eq_band_col_$bandIndex")
    ) {
        // Gain readout dB
        val gainText = if (gainDb > 0) "+${String.format("%.1f", gainDb)}" else "${String.format("%.1f", gainDb)}"
        Text(
            text = gainText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) DolbyCyanPrimary else DolbyTextTertiary,
                fontSize = 10.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Custom Compact Vertical Slider Emulation / Step Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Plus button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DolbyDarkSurfaceElevated)
                    .clickable(enabled = isEnabled && gainDb < 12f) {
                        onGainChanged((gainDb + 1.0f).coerceAtMost(12f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = if (isEnabled) DolbyCyanPrimary else DolbyTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Visual bar fill
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(DolbyDarkSurfaceElevated),
                contentAlignment = Alignment.BottomCenter
            ) {
                val fillRatio = ((gainDb.coerceIn(-12f, 12f) + 12f) / 24f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fillRatio)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isEnabled) {
                                Brush.verticalGradient(
                                    listOf(DolbyCyanPrimary, DolbyElectricBlue)
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(DolbyTextTertiary, DolbyDarkCardBorder)
                                )
                            }
                        )
                )
            }

            // Minus button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DolbyDarkSurfaceElevated)
                    .clickable(enabled = isEnabled && gainDb > -12f) {
                        onGainChanged((gainDb - 1.0f).coerceAtLeast(-12f))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    color = if (isEnabled) DolbyAmberAccent else DolbyTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Frequency Label
        Text(
            text = freqLabel,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = DolbyTextSecondary,
                fontSize = 10.sp
            )
        )
    }
}
