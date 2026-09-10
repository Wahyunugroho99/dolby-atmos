package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.AboutDialog
import com.example.ui.components.DolbyHeader
import com.example.ui.components.EnhancementsCard
import com.example.ui.components.EqualizerCurveView
import com.example.ui.components.IntelligentEqSelector
import com.example.ui.components.OutputDeviceDialog
import com.example.ui.components.ProfileSelector
import com.example.ui.components.SessionLinkDialog
import com.example.ui.components.SpatialDemoPlayerCard
import com.example.ui.theme.DolbyAmberAccent
import com.example.ui.theme.DolbyCyanPrimary
import com.example.ui.theme.DolbyDarkBackground
import com.example.ui.theme.DolbyDarkCardBorder
import com.example.ui.theme.DolbyDarkSurface
import com.example.ui.theme.DolbyDarkSurfaceElevated
import com.example.ui.theme.DolbyDarkSurfaceVariant
import com.example.ui.theme.DolbyElectricBlue
import com.example.ui.theme.DolbyTextPrimary
import com.example.ui.theme.DolbyTextSecondary
import com.example.ui.viewmodel.DolbyAtmosViewModel
import com.example.ui.viewmodel.DolbyTab

@Composable
fun DolbyMainScreen(
    viewModel: DolbyAtmosViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDeviceDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DolbyDarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            DolbyBottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DolbyDarkBackground,
                            Color(0xFF070B10)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 700.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Dolby Header
                DolbyHeader(
                    isEnabled = uiState.settings.isEnabled,
                    onTogglePower = { viewModel.toggleMasterPower(it) },
                    currentDevice = uiState.settings.outputDevice,
                    onDeviceClick = { isDeviceDialogOpen = true },
                    onSessionLinkClick = { viewModel.showSessionDialog(true) },
                    onAboutClick = { viewModel.showAboutDialog(true) },
                    onResetClick = { viewModel.resetAllSettings() }
                )

                // Tab Specific Content
                when (uiState.selectedTab) {
                    DolbyTab.DOLBY_ATMOS -> {
                        // Sound Profiles Grid
                        ProfileSelector(
                            selectedProfile = uiState.settings.currentProfile,
                            onProfileSelected = { viewModel.selectProfile(it) },
                            isEnabled = uiState.settings.isEnabled
                        )

                        // Intelligent EQ
                        IntelligentEqSelector(
                            selectedMode = uiState.settings.intelligentEq,
                            onModeSelected = { viewModel.selectIntelligentEq(it) },
                            isEnabled = uiState.settings.isEnabled
                        )

                        // Quick Spatial Demo Player card
                        SpatialDemoPlayerCard(
                            playerState = uiState.demoPlayerState,
                            onTrackSelected = { viewModel.selectDemoTrack(it) },
                            onLocalFileSelected = { uri, name -> viewModel.playDemoLocalFile(uri, name) },
                            onTogglePlayPause = { viewModel.toggleDemoPlayback() },
                            onStopPlayback = { viewModel.stopDemoPlayback() },
                            isDolbyEnabled = uiState.settings.isEnabled
                        )
                    }

                    DolbyTab.EQUALIZER -> {
                        EqualizerCurveView(
                            eqGains = uiState.settings.eqGains,
                            currentPreset = uiState.settings.currentEqPreset,
                            onGainChanged = { band, gain -> viewModel.setEqBandGain(band, gain) },
                            onPresetSelected = { viewModel.selectEqPreset(it) },
                            onResetEq = { viewModel.resetEq() },
                            isEnabled = uiState.settings.isEnabled
                        )

                        IntelligentEqSelector(
                            selectedMode = uiState.settings.intelligentEq,
                            onModeSelected = { viewModel.selectIntelligentEq(it) },
                            isEnabled = uiState.settings.isEnabled
                        )
                    }

                    DolbyTab.ENHANCEMENTS -> {
                        EnhancementsCard(
                            surroundVirtualizer = uiState.settings.surroundVirtualizer,
                            onSurroundChanged = { viewModel.setSurroundStrength(it) },
                            dialogueEnhancer = uiState.settings.dialogueEnhancer,
                            onDialogueChanged = { viewModel.setDialogueEnhancer(it) },
                            bassEnhancer = uiState.settings.bassEnhancer,
                            onBassChanged = { viewModel.setBassEnhancer(it) },
                            volumeLeveler = uiState.settings.volumeLeveler,
                            onVolumeLevelerToggled = { viewModel.toggleVolumeLeveler(it) },
                            isEnabled = uiState.settings.isEnabled
                        )
                    }

                    DolbyTab.SPATIAL_DEMO -> {
                        SpatialDemoPlayerCard(
                            playerState = uiState.demoPlayerState,
                            onTrackSelected = { viewModel.selectDemoTrack(it) },
                            onLocalFileSelected = { uri, name -> viewModel.playDemoLocalFile(uri, name) },
                            onTogglePlayPause = { viewModel.toggleDemoPlayback() },
                            onStopPlayback = { viewModel.stopDemoPlayback() },
                            isDolbyEnabled = uiState.settings.isEnabled
                        )

                        EnhancementsCard(
                            surroundVirtualizer = uiState.settings.surroundVirtualizer,
                            onSurroundChanged = { viewModel.setSurroundStrength(it) },
                            dialogueEnhancer = uiState.settings.dialogueEnhancer,
                            onDialogueChanged = { viewModel.setDialogueEnhancer(it) },
                            bassEnhancer = uiState.settings.bassEnhancer,
                            onBassChanged = { viewModel.setBassEnhancer(it) },
                            volumeLeveler = uiState.settings.volumeLeveler,
                            onVolumeLevelerToggled = { viewModel.toggleVolumeLeveler(it) },
                            isEnabled = uiState.settings.isEnabled
                        )
                    }

                    DolbyTab.SESSION_LINK -> {
                        SessionLinkInfoCard(
                            activeSessionId = uiState.settings.activeSessionId,
                            connectedPackageName = uiState.settings.connectedPackageName,
                            detectedSessions = uiState.detectedSessions,
                            onOpenDialog = { viewModel.showSessionDialog(true) }
                        )

                        SpatialDemoPlayerCard(
                            playerState = uiState.demoPlayerState,
                            onTrackSelected = { viewModel.selectDemoTrack(it) },
                            onLocalFileSelected = { uri, name -> viewModel.playDemoLocalFile(uri, name) },
                            onTogglePlayPause = { viewModel.toggleDemoPlayback() },
                            onStopPlayback = { viewModel.stopDemoPlayback() },
                            isDolbyEnabled = uiState.settings.isEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (uiState.isAboutDialogOpen) {
        AboutDialog(onDismiss = { viewModel.showAboutDialog(false) })
    }

    if (uiState.isSessionDialogOpen) {
        SessionLinkDialog(
            activeSessionId = uiState.settings.activeSessionId,
            connectedPackageName = uiState.settings.connectedPackageName,
            detectedSessions = uiState.detectedSessions,
            onAttachSession = { id, pkg ->
                viewModel.attachSessionId(id, pkg)
                viewModel.showSessionDialog(false)
            },
            onDetachSession = {
                viewModel.detachSessionId()
                viewModel.showSessionDialog(false)
            },
            onDismiss = { viewModel.showSessionDialog(false) }
        )
    }

    if (isDeviceDialogOpen) {
        OutputDeviceDialog(
            currentDevice = uiState.settings.outputDevice,
            autoRouting = uiState.settings.autoDeviceRouting,
            onDeviceSelected = {
                viewModel.selectOutputDevice(it)
                isDeviceDialogOpen = false
            },
            onAutoRoutingToggled = { viewModel.toggleAutoDeviceRouting(it) },
            onDismiss = { isDeviceDialogOpen = false }
        )
    }
}

@Composable
private fun DolbyBottomNavigationBar(
    selectedTab: DolbyTab,
    onTabSelected: (DolbyTab) -> Unit
) {
    NavigationBar(
        containerColor = DolbyDarkSurfaceVariant,
        contentColor = DolbyTextPrimary,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_navigation_bar")
    ) {
        for (tab in DolbyTab.values()) {
            val isSelected = tab == selectedTab
            val icon = when (tab) {
                DolbyTab.DOLBY_ATMOS -> Icons.Default.AutoAwesome
                DolbyTab.EQUALIZER -> Icons.Default.GraphicEq
                DolbyTab.ENHANCEMENTS -> Icons.Default.Equalizer
                DolbyTab.SPATIAL_DEMO -> Icons.Default.SpatialAudio
                DolbyTab.SESSION_LINK -> Icons.Default.Link
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(tab.titleRes),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(tab.titleRes),
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DolbyCyanPrimary,
                    selectedTextColor = DolbyCyanPrimary,
                    unselectedIconColor = DolbyTextSecondary,
                    unselectedTextColor = DolbyTextSecondary,
                    indicatorColor = DolbyDarkSurfaceElevated
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
private fun SessionLinkInfoCard(
    activeSessionId: Int,
    connectedPackageName: String?,
    detectedSessions: List<Pair<Int, String>>,
    onOpenDialog: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DolbyDarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DolbyDarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = DolbyCyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.session_control),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyTextPrimary
                        )
                    )
                }

                androidx.compose.material3.Button(
                    onClick = onOpenDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = DolbyCyanPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Configure", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.session_control_desc),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DolbyTextSecondary,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DolbyDarkSurface)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Connection Status",
                        style = MaterialTheme.typography.labelSmall.copy(color = DolbyTextSecondary)
                    )
                    Text(
                        text = if (activeSessionId == 0) {
                            "Global Audio Output Session (Session 0) Active"
                        } else {
                            "Linked to Session $activeSessionId (${connectedPackageName ?: "Music Player"})"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DolbyCyanPrimary
                        )
                    )
                }
            }
        }
    }
}
