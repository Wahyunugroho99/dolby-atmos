package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.IntelligentEqMode
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@Composable
fun IntelligentEqSelector(
    selectedMode: IntelligentEqMode,
    onModeSelected: (IntelligentEqMode) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = DolbyAmberAccent,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = stringResource(R.string.intelligent_eq),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DolbyTextPrimary
                    )
                )
            }

            Text(
                text = stringResource(selectedMode.labelRes),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEnabled) DolbyAmberAccent else DolbyTextSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal scrollable chip selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (mode in IntelligentEqMode.values()) {
                val isSelected = mode == selectedMode
                val chipBorderColor by animateColorAsState(
                    targetValue = if (isSelected && isEnabled) DolbyAmberAccent else DolbyDarkCardBorder,
                    label = "ieq_border"
                )
                val chipBgColor = if (isSelected && isEnabled) {
                    DolbyAmberAccent.copy(alpha = 0.18f)
                } else {
                    DolbyDarkSurface
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipBgColor)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = chipBorderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = isEnabled) { onModeSelected(mode) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("ieq_chip_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(mode.labelRes),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected && isEnabled) DolbyAmberAccent else DolbyTextSecondary
                        )
                    )
                }
            }
        }
    }
}
