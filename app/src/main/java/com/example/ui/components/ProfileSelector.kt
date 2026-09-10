package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DolbySoundProfile
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyElectricBlue
import com.example.ui.theme.DolbyGlowCyan
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSelector(
    selectedProfile: DolbySoundProfile,
    onProfileSelected: (DolbySoundProfile) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sound Profile",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DolbyTextPrimary
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 3x2 Grid for the 6 profiles
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val profiles = DolbySoundProfile.values()
            for (row in profiles.toList().chunked(3)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (profile in row) {
                        ProfileCardItem(
                            profile = profile,
                            isSelected = profile == selectedProfile,
                            isEnabled = isEnabled,
                            onClick = { onProfileSelected(profile) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic description box of the active profile
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = DolbyDarkSurfaceVariant.copy(alpha = 0.8f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                DolbyDarkCardBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DolbyCyanPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = selectedProfile.icon,
                        contentDescription = stringResource(selectedProfile.titleRes),
                        tint = DolbyCyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(selectedProfile.titleRes),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyCyanPrimary
                        )
                    )
                    Text(
                        text = stringResource(selectedProfile.descRes),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DolbyTextSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCardItem(
    profile: DolbySoundProfile,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected && isEnabled) DolbyCyanPrimary else DolbyDarkCardBorder,
        label = "borderColor"
    )

    val backgroundColor = if (isSelected && isEnabled) {
        Brush.verticalGradient(
            colors = listOf(
                DolbyElectricBlue.copy(alpha = 0.25f),
                DolbyDarkSurfaceElevated
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                DolbyDarkSurface,
                DolbyDarkSurfaceVariant
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .testTag("profile_card_${profile.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = profile.icon,
                contentDescription = stringResource(profile.titleRes),
                tint = if (isSelected && isEnabled) DolbyCyanPrimary else DolbyTextSecondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(profile.titleRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected && isEnabled) DolbyTextPrimary else DolbyTextSecondary
                )
            )
        }
    }
}
