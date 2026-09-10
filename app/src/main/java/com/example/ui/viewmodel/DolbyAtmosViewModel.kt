package com.example.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.AudioDemoType
import com.example.data.model.AudioOutputDevice
import com.example.data.model.DolbyProfileConfigDefaults
import com.example.data.model.DolbySettings
import com.example.data.model.DolbySoundProfile
import com.example.data.model.EqPreset
import com.example.data.model.IntelligentEqMode
import com.example.data.repository.DolbySettingsRepository
import com.example.data.repository.DolbySettingsRepositoryImpl
import com.example.engine.DemoPlayerState
import com.example.engine.DolbyAudioFxEngine
import com.example.engine.SpatialDemoAudioEngine
import com.example.receiver.SessionEventsHub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DolbyTab(
    @StringRes val titleRes: Int
) {
    DOLBY_ATMOS(R.string.tab_main),
    EQUALIZER(R.string.tab_equalizer),
    ENHANCEMENTS(R.string.tab_enhancements),
    SPATIAL_DEMO(R.string.tab_demo_player),
    SESSION_LINK(R.string.tab_session)
}

data class DolbyUiState(
    val settings: DolbySettings = DolbySettings(),
    val demoPlayerState: DemoPlayerState = DemoPlayerState(),
    val selectedTab: DolbyTab = DolbyTab.DOLBY_ATMOS,
    val isAboutDialogOpen: Boolean = false,
    val isSessionDialogOpen: Boolean = false,
    val snackbarMessage: String? = null,
    val detectedSessions: List<Pair<Int, String>> = emptyList()
)

class DolbyAtmosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: DolbySettingsRepository = DolbySettingsRepositoryImpl(application)
    private val audioFxEngine: DolbyAudioFxEngine = DolbyAudioFxEngine(application)
    private val demoPlayerEngine: SpatialDemoAudioEngine = SpatialDemoAudioEngine(application, audioFxEngine)

    private val _uiState = MutableStateFlow(DolbyUiState())
    val uiState: StateFlow<DolbyUiState> = _uiState.asStateFlow()

    private val sessionListener: (Int, String?, Boolean) -> Unit = { sessionId, pkg, isOpened ->
        viewModelScope.launch {
            if (isOpened) {
                val newEntry = Pair(sessionId, pkg ?: "Music Player")
                _uiState.update { current ->
                    val list = current.detectedSessions.filter { it.first != sessionId }.toMutableList()
                    list.add(0, newEntry)
                    current.copy(
                        detectedSessions = list,
                        snackbarMessage = "Attached to ${pkg ?: "Audio Session $sessionId"}"
                    )
                }
                repository.setActiveSession(sessionId, pkg)
                audioFxEngine.attachSession(sessionId)
                audioFxEngine.applySettings(repository.settingsFlow.value)
            } else {
                _uiState.update { current ->
                    current.copy(
                        detectedSessions = current.detectedSessions.filter { it.first != sessionId }
                    )
                }
            }
        }
    }

    private val headsetReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                if (state == 1) {
                    if (_uiState.value.settings.autoDeviceRouting) {
                        repository.setOutputDevice(AudioOutputDevice.WIRED_HEADSET)
                    }
                } else if (state == 0) {
                    if (_uiState.value.settings.autoDeviceRouting) {
                        repository.setOutputDevice(AudioOutputDevice.SPEAKER)
                    }
                }
            }
        }
    }

    init {
        // Collect settings updates and sync to DSP audio engine
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
                demoPlayerEngine.updateSettings(settings)
                audioFxEngine.applySettings(settings)
            }
        }

        // Collect demo player state
        viewModelScope.launch {
            demoPlayerEngine.playerState.collect { playerState ->
                _uiState.update { it.copy(demoPlayerState = playerState) }
            }
        }

        // Register session receiver events
        SessionEventsHub.addListener(sessionListener)

        // Register headset plug detection
        try {
            val filter = IntentFilter(AudioManager.ACTION_HEADSET_PLUG)
            application.registerReceiver(headsetReceiver, filter)
        } catch (_: Exception) {}

        // Initial audio device check
        detectCurrentAudioDevice()
    }

    private fun detectCurrentAudioDevice() {
        val audioManager = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager != null) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            var currentDevice = AudioOutputDevice.SPEAKER
            for (device in devices) {
                when (device.type) {
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> {
                        currentDevice = AudioOutputDevice.BLUETOOTH_A2DP
                        break
                    }
                    AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> {
                        currentDevice = AudioOutputDevice.WIRED_HEADSET
                        break
                    }
                    AudioDeviceInfo.TYPE_USB_DEVICE, AudioDeviceInfo.TYPE_USB_HEADSET -> {
                        currentDevice = AudioOutputDevice.USB_DAC
                        break
                    }
                }
            }
            if (_uiState.value.settings.autoDeviceRouting) {
                repository.setOutputDevice(currentDevice)
            }
        }
    }

    fun selectTab(tab: DolbyTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleMasterPower(enabled: Boolean) {
        repository.setEnabled(enabled)
    }

    fun selectProfile(profile: DolbySoundProfile) {
        repository.setProfile(profile)
    }

    fun selectIntelligentEq(mode: IntelligentEqMode) {
        repository.setIntelligentEq(mode)
    }

    fun setSurroundStrength(strength: Int) {
        repository.setSurroundVirtualizer(strength)
    }

    fun setDialogueEnhancer(strength: Int) {
        repository.setDialogueEnhancer(strength)
    }

    fun setBassEnhancer(strength: Int) {
        repository.setBassEnhancer(strength)
    }

    fun toggleVolumeLeveler(enabled: Boolean) {
        repository.setVolumeLeveler(enabled)
    }

    fun selectEqPreset(preset: EqPreset) {
        repository.setEqPreset(preset)
    }

    fun setEqBandGain(bandIndex: Int, gainDb: Float) {
        repository.setEqBandGain(bandIndex, gainDb)
    }

    fun resetEq() {
        val currentProfile = _uiState.value.settings.currentProfile
        val defaults = DolbyProfileConfigDefaults.getConfigForProfile(currentProfile)
        repository.setEqPreset(defaults.currentEqPreset)
        repository.setAllEqGains(defaults.eqGains)
        _uiState.update { it.copy(snackbarMessage = "Equalizer reset to $currentProfile defaults") }
    }

    fun selectOutputDevice(device: AudioOutputDevice) {
        repository.setOutputDevice(device)
    }

    fun toggleAutoDeviceRouting(enabled: Boolean) {
        repository.setAutoDeviceRouting(enabled)
    }

    // Spatial Demo Audio Player controls
    fun selectDemoTrack(track: AudioDemoType) {
        demoPlayerEngine.selectTrack(track)
    }

    fun playDemoLocalFile(uri: Uri, fileName: String) {
        demoPlayerEngine.playLocalFile(uri, fileName)
    }

    fun toggleDemoPlayback() {
        demoPlayerEngine.togglePlayPause()
    }

    fun stopDemoPlayback() {
        demoPlayerEngine.stop()
    }

    // Audio Session link controls
    fun attachSessionId(sessionId: Int, packageName: String? = null) {
        repository.setActiveSession(sessionId, packageName)
        audioFxEngine.attachSession(sessionId)
        audioFxEngine.applySettings(_uiState.value.settings)
        _uiState.update {
            it.copy(snackbarMessage = "Connected to Audio Session $sessionId")
        }
    }

    fun detachSessionId() {
        audioFxEngine.releaseSession()
        repository.setActiveSession(0, null)
        _uiState.update {
            it.copy(snackbarMessage = "Detached from custom audio session")
        }
    }

    fun showAboutDialog(show: Boolean) {
        _uiState.update { it.copy(isAboutDialogOpen = show) }
    }

    fun showSessionDialog(show: Boolean) {
        _uiState.update { it.copy(isSessionDialogOpen = show) }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun resetAllSettings() {
        repository.resetToDefaults()
        _uiState.update { it.copy(snackbarMessage = "All Dolby Atmos settings reset to defaults") }
    }

    override fun onCleared() {
        super.onCleared()
        SessionEventsHub.removeListener(sessionListener)
        try {
            getApplication<Application>().unregisterReceiver(headsetReceiver)
        } catch (_: Exception) {}
        demoPlayerEngine.release()
        audioFxEngine.releaseSession()
    }
}
