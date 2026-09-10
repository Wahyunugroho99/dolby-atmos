package com.example.data.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R

enum class DolbySoundProfile(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val icon: ImageVector
) {
    DYNAMIC(
        titleRes = R.string.profile_dynamic,
        descRes = R.string.profile_dynamic_desc,
        icon = Icons.Default.AutoAwesome
    ),
    MOVIE(
        titleRes = R.string.profile_movie,
        descRes = R.string.profile_movie_desc,
        icon = Icons.Default.Movie
    ),
    MUSIC(
        titleRes = R.string.profile_music,
        descRes = R.string.profile_music_desc,
        icon = Icons.Default.MusicNote
    ),
    VOICE(
        titleRes = R.string.profile_voice,
        descRes = R.string.profile_voice_desc,
        icon = Icons.Default.RecordVoiceOver
    ),
    GAME(
        titleRes = R.string.profile_game,
        descRes = R.string.profile_game_desc,
        icon = Icons.Default.SportsEsports
    ),
    CUSTOM(
        titleRes = R.string.profile_custom,
        descRes = R.string.profile_custom_desc,
        icon = Icons.Default.Tune
    )
}

enum class IntelligentEqMode(
    @StringRes val labelRes: Int
) {
    DETAILED(R.string.ieq_detailed),
    WARM(R.string.ieq_warm),
    BALANCED(R.string.ieq_balanced),
    OPEN(R.string.ieq_open),
    FOCUSED(R.string.ieq_focused),
    OFF(R.string.ieq_off)
}

enum class EqPreset(
    @StringRes val labelRes: Int,
    val bandGains: List<Float>
) {
    FLAT(R.string.preset_flat, listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    ROCK(R.string.preset_rock, listOf(4.5f, 3.0f, 1.5f, 0.0f, -1.0f, -0.5f, 1.5f, 3.0f, 4.0f, 4.5f)),
    POP(R.string.preset_pop, listOf(-1.0f, 1.0f, 3.0f, 4.0f, 3.5f, 1.5f, -0.5f, -1.0f, 1.0f, 2.0f)),
    JAZZ(R.string.preset_jazz, listOf(3.0f, 2.0f, 1.0f, 1.5f, -1.5f, -1.5f, 0.0f, 1.5f, 2.5f, 3.0f)),
    ELECTRONIC(R.string.preset_electronic, listOf(5.0f, 4.0f, 2.0f, 0.0f, -1.5f, 1.0f, 1.5f, 3.0f, 4.5f, 5.0f)),
    CLASSICAL(R.string.preset_classical, listOf(4.0f, 3.0f, 2.0f, 1.5f, -1.0f, -1.0f, 0.0f, 2.0f, 3.5f, 4.0f)),
    HIPHOP(R.string.preset_hiphop, listOf(6.0f, 5.0f, 2.5f, 1.0f, -1.0f, -0.5f, 1.0f, 2.0f, 3.5f, 4.0f)),
    VOCAL(R.string.preset_vocal, listOf(-2.0f, -1.5f, -0.5f, 2.0f, 4.0f, 4.5f, 3.0f, 1.5f, 0.0f, -1.0f)),
    BASS(R.string.preset_bass, listOf(7.0f, 6.0f, 4.5f, 2.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
    CUSTOM(R.string.preset_custom, listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
}

enum class AudioOutputDevice(
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    SPEAKER(R.string.device_speaker, Icons.Default.Tune),
    WIRED_HEADSET(R.string.device_wired, Icons.Default.Headphones),
    BLUETOOTH_A2DP(R.string.device_bluetooth, Icons.Default.Headphones),
    USB_DAC(R.string.device_usb, Icons.Default.Tune)
}

data class EqBand(
    val index: Int,
    val frequencyLabel: String,
    val frequencyHz: Int,
    val gainDb: Float,
    val minDb: Float = -12f,
    val maxDb: Float = 12f
)

data class DolbySettings(
    val isEnabled: Boolean = true,
    val currentProfile: DolbySoundProfile = DolbySoundProfile.DYNAMIC,
    val intelligentEq: IntelligentEqMode = IntelligentEqMode.BALANCED,
    val surroundVirtualizer: Int = 80, // 0 - 100%
    val dialogueEnhancer: Int = 60, // 0 - 100%
    val bassEnhancer: Int = 70, // 0 - 100%
    val volumeLeveler: Boolean = true,
    val currentEqPreset: EqPreset = EqPreset.POP,
    val eqGains: List<Float> = listOf(2.5f, 3.0f, 1.5f, 0.0f, 1.0f, 2.5f, 3.0f, 2.0f, 3.5f, 4.0f),
    val outputDevice: AudioOutputDevice = AudioOutputDevice.SPEAKER,
    val autoDeviceRouting: Boolean = true,
    val activeSessionId: Int = 0,
    val connectedPackageName: String? = null
)
