package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.model.AudioDemoType
import com.example.data.model.DolbySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class DemoPlayerState(
    val isPlaying: Boolean = false,
    val currentTrack: AudioDemoType = AudioDemoType.CINEMATIC_ATMOS,
    val currentPositionSec: Int = 0,
    val durationSec: Int = 45,
    val localUri: Uri? = null,
    val localFileName: String? = null,
    val spectrumLevels: List<Float> = List(16) { 0.1f },
    val audioSessionId: Int = 0
)

class SpatialDemoAudioEngine(
    private val context: Context,
    private val audioFxEngine: DolbyAudioFxEngine
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _playerState = MutableStateFlow(DemoPlayerState())
    val playerState: StateFlow<DemoPlayerState> = _playerState.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var synthJob: Job? = null
    private var spectrumJob: Job? = null

    private var currentSettings: DolbySettings? = null

    fun updateSettings(settings: DolbySettings) {
        currentSettings = settings
        audioFxEngine.applySettings(settings)
    }

    fun selectTrack(trackType: AudioDemoType) {
        stop()
        _playerState.update {
            it.copy(
                currentTrack = trackType,
                durationSec = trackType.durationSeconds.coerceAtLeast(1),
                currentPositionSec = 0
            )
        }
    }

    fun playLocalFile(uri: Uri, fileName: String) {
        stop()
        _playerState.update {
            it.copy(
                currentTrack = AudioDemoType.LOCAL_FILE,
                localUri = uri,
                localFileName = fileName,
                currentPositionSec = 0
            )
        }
        play()
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val state = _playerState.value
        if (state.currentTrack == AudioDemoType.LOCAL_FILE && state.localUri != null) {
            playLocalMedia(state.localUri)
        } else {
            playSyntheticSpatialTrack(state.currentTrack)
        }
    }

    fun pause() {
        synthJob?.cancel()
        spectrumJob?.cancel()
        try {
            audioTrack?.pause()
            mediaPlayer?.pause()
        } catch (e: Exception) {
            Log.w(TAG, "Error pausing audio: ${e.message}")
        }
        _playerState.update { it.copy(isPlaying = false) }
    }

    fun stop() {
        synthJob?.cancel()
        spectrumJob?.cancel()
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}

        _playerState.update {
            it.copy(
                isPlaying = false,
                currentPositionSec = 0,
                spectrumLevels = List(16) { 0.05f }
            )
        }
    }

    private fun playLocalMedia(uri: Uri) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                prepare()
                val session = audioSessionId
                audioFxEngine.attachSession(session)
                currentSettings?.let { audioFxEngine.applySettings(it) }
                start()

                setOnCompletionListener {
                    stop()
                }

                val durSec = (duration / 1000).coerceAtLeast(1)
                _playerState.update {
                    it.copy(
                        isPlaying = true,
                        durationSec = durSec,
                        audioSessionId = session
                    )
                }
            }
            startSpectrumAndProgressTimer()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing local media: ${e.message}")
            _playerState.update { it.copy(isPlaying = false) }
        }
    }

    private fun playSyntheticSpatialTrack(trackType: AudioDemoType) {
        synthJob?.cancel()
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        try {
            audioTrack?.release()
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            val session = track.audioSessionId
            audioFxEngine.attachSession(session)
            currentSettings?.let { audioFxEngine.applySettings(it) }

            track.play()

            _playerState.update {
                it.copy(
                    isPlaying = true,
                    durationSec = trackType.durationSeconds,
                    audioSessionId = session
                )
            }

            synthJob = scope.launch(Dispatchers.Default) {
                val chunkSamples = 2048
                val pcmBuffer = ShortArray(chunkSamples * 2) // Stereo
                var sampleIndex = 0L

                while (isActive) {
                    val trackDurationSamples = trackType.durationSeconds * sampleRate.toLong()
                    if (sampleIndex >= trackDurationSamples) {
                        sampleIndex = 0L // Loop demo track
                    }

                    // Generate stereo audio PCM samples based on track type
                    for (i in 0 until chunkSamples) {
                        val t = (sampleIndex + i).toDouble() / sampleRate
                        val stereo = generateSyntheticSpatialSample(trackType, t)

                        // Apply master volume scaling
                        val leftSample = (stereo.first.coerceIn(-1.0, 1.0) * 26000).toInt().toShort()
                        val rightSample = (stereo.second.coerceIn(-1.0, 1.0) * 26000).toInt().toShort()

                        pcmBuffer[i * 2] = leftSample
                        pcmBuffer[i * 2 + 1] = rightSample
                    }

                    audioTrack?.write(pcmBuffer, 0, pcmBuffer.size)
                    sampleIndex += chunkSamples
                }
            }

            startSpectrumAndProgressTimer()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting synthetic spatial audio: ${e.message}")
            _playerState.update { it.copy(isPlaying = false) }
        }
    }

    private fun generateSyntheticSpatialSample(trackType: AudioDemoType, t: Double): Pair<Double, Double> {
        return when (trackType) {
            AudioDemoType.CINEMATIC_ATMOS -> {
                // Majestic cinematic atmosphere with sub-bass drone and spatial pad chords
                val subBass = sin(2.0 * PI * 45.0 * t) * 0.45 * (0.8 + 0.2 * sin(2.0 * PI * 0.5 * t))
                val chordA = sin(2.0 * PI * 220.0 * t) * 0.2
                val chordB = sin(2.0 * PI * 277.18 * t) * 0.18 // C#
                val chordC = sin(2.0 * PI * 329.63 * t) * 0.18 // E
                val sparkle = sin(2.0 * PI * 880.0 * t + sin(2.0 * PI * 4.0 * t)) * 0.1

                // Spatial pan L/R
                val panAngle = 2.0 * PI * 0.2 * t
                val panL = 0.5 + 0.5 * cos(panAngle)
                val panR = 0.5 + 0.5 * sin(panAngle)

                val left = subBass + (chordA + chordB) * panL + sparkle * panL * 0.7
                val right = subBass + (chordB + chordC) * panR + sparkle * panR * 0.7
                Pair(left * 0.75, right * 0.75)
            }

            AudioDemoType.SPATIAL_8D -> {
                // 8D rotating frequency sphere circling around left/right ears with Doppler
                val rotFreq = 0.35 // Rotation speed in Hz
                val angle = 2.0 * PI * rotFreq * t
                val panL = (cos(angle) + 1.0) * 0.5
                val panR = (sin(angle) + 1.0) * 0.5

                val lead = sin(2.0 * PI * (440.0 + 30.0 * sin(angle)) * t) * 0.3
                val harmonic = sin(2.0 * PI * 880.0 * t) * 0.15
                val bass = sin(2.0 * PI * 65.0 * t) * 0.35

                val left = (lead + harmonic) * panL + bass * 0.5
                val right = (lead + harmonic) * panR + bass * 0.5
                Pair(left * 0.8, right * 0.8)
            }

            AudioDemoType.ACOUSTIC_MUSIC -> {
                // Acoustic chord progression (C - G - Am - F)
                val measure = (t % 8.0)
                val rootFreq = when {
                    measure < 2.0 -> 261.63 // C4
                    measure < 4.0 -> 196.00 // G3
                    measure < 6.0 -> 220.00 // A3
                    else -> 174.61 // F3
                }
                val noteA = sin(2.0 * PI * rootFreq * t) * 0.3
                val noteB = sin(2.0 * PI * (rootFreq * 1.25) * t) * 0.25 // Major third
                val noteC = sin(2.0 * PI * (rootFreq * 1.5) * t) * 0.25 // Fifth
                val acousticShimmer = sin(2.0 * PI * 4000.0 * t) * 0.05 * sin(2.0 * PI * 8.0 * t)

                val left = noteA + noteB + acousticShimmer
                val right = noteA + noteC + acousticShimmer
                Pair(left * 0.7, right * 0.7)
            }

            AudioDemoType.SUB_BASS_TEST -> {
                // Low-end frequency sweep 30Hz - 90Hz
                val sweepFreq = 35.0 + 45.0 * (sin(2.0 * PI * 0.2 * t) + 1.0) * 0.5
                val subTone = sin(2.0 * PI * sweepFreq * t) * 0.65
                val punch = if (t % 1.0 < 0.1) sin(2.0 * PI * 80.0 * t) * 0.35 else 0.0
                Pair(subTone + punch, subTone + punch)
            }

            AudioDemoType.VOICE_CLARITY -> {
                // Vocal formant resonance (F1 ~ 500Hz, F2 ~ 1500Hz, F3 ~ 2500Hz)
                val f0 = 140.0 + 20.0 * sin(2.0 * PI * 1.5 * t) // Pitch modulation
                val f1 = sin(2.0 * PI * (f0 * 3.5) * t) * 0.35
                val f2 = sin(2.0 * PI * (f0 * 10.0) * t) * 0.25
                val f3 = sin(2.0 * PI * (f0 * 18.0) * t) * 0.15
                val speechLike = (f1 + f2 + f3) * (0.6 + 0.4 * sin(2.0 * PI * 3.0 * t))
                Pair(speechLike * 0.7, speechLike * 0.7)
            }

            AudioDemoType.LOCAL_FILE -> Pair(0.0, 0.0)
        }
    }

    private fun startSpectrumAndProgressTimer() {
        spectrumJob?.cancel()
        spectrumJob = scope.launch(Dispatchers.Default) {
            var tick = 0
            while (isActive && _playerState.value.isPlaying) {
                delay(80)
                tick++

                // Update current position in seconds
                if (tick % 12 == 0) {
                    _playerState.update { current ->
                        val nextPos = if (mediaPlayer != null) {
                            try {
                                (mediaPlayer?.currentPosition ?: 0) / 1000
                            } catch (_: Exception) {
                                current.currentPositionSec + 1
                            }
                        } else {
                            (current.currentPositionSec + 1) % current.durationSec.coerceAtLeast(1)
                        }
                        current.copy(currentPositionSec = nextPos)
                    }
                }

                // Generate 16 animated spectrum bars reflecting the active EQ profile & audio track
                val settings = currentSettings
                val isDolbyOn = settings?.isEnabled ?: true
                val bassBoostFactor = if (isDolbyOn) ((settings?.bassEnhancer ?: 50) / 100f) * 0.6f else 0f
                val virtualizerFactor = if (isDolbyOn) ((settings?.surroundVirtualizer ?: 50) / 100f) * 0.4f else 0f

                val newSpectrum = List(16) { index ->
                    val baseFreqWave = sin(tick * 0.2 + index * 0.45).toFloat() * 0.35f + 0.5f
                    val eqGain = if (isDolbyOn && settings != null) {
                        val mappedBand = (index * 9 / 15).coerceIn(0, 9)
                        ((settings.eqGains.getOrNull(mappedBand) ?: 0f) + 12f) / 24f
                    } else 0.5f

                    val extraBoost = if (index < 4) bassBoostFactor else if (index > 11) virtualizerFactor else 0.2f
                    (baseFreqWave * 0.5f + eqGain * 0.35f + extraBoost * 0.25f).coerceIn(0.08f, 0.98f)
                }

                _playerState.update { it.copy(spectrumLevels = newSpectrum) }
            }
        }
    }

    fun release() {
        stop()
    }

    companion object {
        private const val TAG = "SpatialDemoAudioEngine"
    }
}
