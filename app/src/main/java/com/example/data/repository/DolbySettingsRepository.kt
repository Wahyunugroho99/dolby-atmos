package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AudioOutputDevice
import com.example.data.model.DolbyProfileConfigDefaults
import com.example.data.model.DolbySettings
import com.example.data.model.DolbySoundProfile
import com.example.data.model.EqPreset
import com.example.data.model.IntelligentEqMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DolbySettingsRepository {
    val settingsFlow: StateFlow<DolbySettings>
    fun setEnabled(enabled: Boolean)
    fun setProfile(profile: DolbySoundProfile)
    fun setIntelligentEq(mode: IntelligentEqMode)
    fun setSurroundVirtualizer(strength: Int)
    fun setDialogueEnhancer(strength: Int)
    fun setBassEnhancer(strength: Int)
    fun setVolumeLeveler(enabled: Boolean)
    fun setEqPreset(preset: EqPreset)
    fun setEqBandGain(bandIndex: Int, gainDb: Float)
    fun setAllEqGains(gains: List<Float>)
    fun setOutputDevice(device: AudioOutputDevice)
    fun setAutoDeviceRouting(enabled: Boolean)
    fun setActiveSession(sessionId: Int, packageName: String?)
    fun resetToDefaults()
    fun saveCustomProfilePreset()
}

class DolbySettingsRepositoryImpl(
    private val context: Context
) : DolbySettingsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dolby_atmos_magic_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    override val settingsFlow: StateFlow<DolbySettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): DolbySettings {
        val enabled = prefs.getBoolean(KEY_ENABLED, true)
        val profileName = prefs.getString(KEY_PROFILE, DolbySoundProfile.DYNAMIC.name)
        val profile = try {
            DolbySoundProfile.valueOf(profileName ?: DolbySoundProfile.DYNAMIC.name)
        } catch (_: Exception) {
            DolbySoundProfile.DYNAMIC
        }

        val ieqName = prefs.getString(KEY_IEQ, IntelligentEqMode.BALANCED.name)
        val intelligentEq = try {
            IntelligentEqMode.valueOf(ieqName ?: IntelligentEqMode.BALANCED.name)
        } catch (_: Exception) {
            IntelligentEqMode.BALANCED
        }

        val surround = prefs.getInt(KEY_SURROUND, 80)
        val dialogue = prefs.getInt(KEY_DIALOGUE, 60)
        val bass = prefs.getInt(KEY_BASS, 70)
        val volumeLeveler = prefs.getBoolean(KEY_VOLUME_LEVELER, true)

        val presetName = prefs.getString(KEY_EQ_PRESET, EqPreset.POP.name)
        val eqPreset = try {
            EqPreset.valueOf(presetName ?: EqPreset.POP.name)
        } catch (_: Exception) {
            EqPreset.POP
        }

        val eqString = prefs.getString(KEY_EQ_GAINS, null)
        val eqGains = if (eqString != null) {
            try {
                eqString.split(",").map { it.toFloat() }
            } catch (_: Exception) {
                eqPreset.bandGains
            }
        } else {
            eqPreset.bandGains
        }

        val deviceName = prefs.getString(KEY_OUTPUT_DEVICE, AudioOutputDevice.SPEAKER.name)
        val device = try {
            AudioOutputDevice.valueOf(deviceName ?: AudioOutputDevice.SPEAKER.name)
        } catch (_: Exception) {
            AudioOutputDevice.SPEAKER
        }

        val autoRouting = prefs.getBoolean(KEY_AUTO_ROUTING, true)

        return DolbySettings(
            isEnabled = enabled,
            currentProfile = profile,
            intelligentEq = intelligentEq,
            surroundVirtualizer = surround,
            dialogueEnhancer = dialogue,
            bassEnhancer = bass,
            volumeLeveler = volumeLeveler,
            currentEqPreset = eqPreset,
            eqGains = if (eqGains.size == 10) eqGains else EqPreset.FLAT.bandGains,
            outputDevice = device,
            autoDeviceRouting = autoRouting,
            activeSessionId = 0,
            connectedPackageName = null
        )
    }

    private fun persistSettings(settings: DolbySettings) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, settings.isEnabled)
            .putString(KEY_PROFILE, settings.currentProfile.name)
            .putString(KEY_IEQ, settings.intelligentEq.name)
            .putInt(KEY_SURROUND, settings.surroundVirtualizer)
            .putInt(KEY_DIALOGUE, settings.dialogueEnhancer)
            .putInt(KEY_BASS, settings.bassEnhancer)
            .putBoolean(KEY_VOLUME_LEVELER, settings.volumeLeveler)
            .putString(KEY_EQ_PRESET, settings.currentEqPreset.name)
            .putString(KEY_EQ_GAINS, settings.eqGains.joinToString(","))
            .putString(KEY_OUTPUT_DEVICE, settings.outputDevice.name)
            .putBoolean(KEY_AUTO_ROUTING, settings.autoDeviceRouting)
            .apply()
    }

    override fun setEnabled(enabled: Boolean) {
        _settingsFlow.update {
            val updated = it.copy(isEnabled = enabled)
            persistSettings(updated)
            updated
        }
    }

    override fun setProfile(profile: DolbySoundProfile) {
        _settingsFlow.update { current ->
            // Load preset defaults for this profile if switching profiles
            val defaults = DolbyProfileConfigDefaults.getConfigForProfile(profile)
            val updated = current.copy(
                currentProfile = profile,
                intelligentEq = defaults.intelligentEq,
                surroundVirtualizer = defaults.surroundVirtualizer,
                dialogueEnhancer = defaults.dialogueEnhancer,
                bassEnhancer = defaults.bassEnhancer,
                volumeLeveler = defaults.volumeLeveler,
                currentEqPreset = defaults.currentEqPreset,
                eqGains = defaults.eqGains
            )
            persistSettings(updated)
            updated
        }
    }

    override fun setIntelligentEq(mode: IntelligentEqMode) {
        _settingsFlow.update {
            val updated = it.copy(intelligentEq = mode)
            persistSettings(updated)
            updated
        }
    }

    override fun setSurroundVirtualizer(strength: Int) {
        _settingsFlow.update {
            val updated = it.copy(surroundVirtualizer = strength.coerceIn(0, 100))
            persistSettings(updated)
            updated
        }
    }

    override fun setDialogueEnhancer(strength: Int) {
        _settingsFlow.update {
            val updated = it.copy(dialogueEnhancer = strength.coerceIn(0, 100))
            persistSettings(updated)
            updated
        }
    }

    override fun setBassEnhancer(strength: Int) {
        _settingsFlow.update {
            val updated = it.copy(bassEnhancer = strength.coerceIn(0, 100))
            persistSettings(updated)
            updated
        }
    }

    override fun setVolumeLeveler(enabled: Boolean) {
        _settingsFlow.update {
            val updated = it.copy(volumeLeveler = enabled)
            persistSettings(updated)
            updated
        }
    }

    override fun setEqPreset(preset: EqPreset) {
        _settingsFlow.update {
            val updated = it.copy(
                currentEqPreset = preset,
                eqGains = if (preset != EqPreset.CUSTOM) preset.bandGains else it.eqGains
            )
            persistSettings(updated)
            updated
        }
    }

    override fun setEqBandGain(bandIndex: Int, gainDb: Float) {
        _settingsFlow.update { current ->
            val updatedGains = current.eqGains.toMutableList()
            if (bandIndex in updatedGains.indices) {
                updatedGains[bandIndex] = gainDb.coerceIn(-12f, 12f)
            }
            val updated = current.copy(
                eqGains = updatedGains,
                currentEqPreset = EqPreset.CUSTOM
            )
            persistSettings(updated)
            updated
        }
    }

    override fun setAllEqGains(gains: List<Float>) {
        _settingsFlow.update {
            val updated = it.copy(
                eqGains = gains,
                currentEqPreset = EqPreset.CUSTOM
            )
            persistSettings(updated)
            updated
        }
    }

    override fun setOutputDevice(device: AudioOutputDevice) {
        _settingsFlow.update {
            val updated = it.copy(outputDevice = device)
            persistSettings(updated)
            updated
        }
    }

    override fun setAutoDeviceRouting(enabled: Boolean) {
        _settingsFlow.update {
            val updated = it.copy(autoDeviceRouting = enabled)
            persistSettings(updated)
            updated
        }
    }

    override fun setActiveSession(sessionId: Int, packageName: String?) {
        _settingsFlow.update {
            it.copy(activeSessionId = sessionId, connectedPackageName = packageName)
        }
    }

    override fun resetToDefaults() {
        setProfile(DolbySoundProfile.DYNAMIC)
    }

    override fun saveCustomProfilePreset() {
        persistSettings(_settingsFlow.value)
    }

    companion object {
        private const val KEY_ENABLED = "key_dolby_enabled"
        private const val KEY_PROFILE = "key_dolby_profile"
        private const val KEY_IEQ = "key_dolby_ieq"
        private const val KEY_SURROUND = "key_dolby_surround"
        private const val KEY_DIALOGUE = "key_dolby_dialogue"
        private const val KEY_BASS = "key_dolby_bass"
        private const val KEY_VOLUME_LEVELER = "key_dolby_volume_leveler"
        private const val KEY_EQ_PRESET = "key_dolby_eq_preset"
        private const val KEY_EQ_GAINS = "key_dolby_eq_gains"
        private const val KEY_OUTPUT_DEVICE = "key_dolby_output_device"
        private const val KEY_AUTO_ROUTING = "key_dolby_auto_routing"
    }
}
