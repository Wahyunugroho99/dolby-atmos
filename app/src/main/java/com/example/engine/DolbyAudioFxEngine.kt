package com.example.engine

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.util.Log
import com.example.data.model.DolbySettings
import com.example.data.model.IntelligentEqMode

class DolbyAudioFxEngine(private val context: Context) {

    private var currentSessionId: Int = 0
    private var equalizer: Equalizer? = null
    private var virtualizer: Virtualizer? = null
    private var bassBoost: BassBoost? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var presetReverb: PresetReverb? = null

    private var isEngineActive: Boolean = false

    fun attachSession(sessionId: Int) {
        if (currentSessionId == sessionId && isEngineActive) {
            return
        }
        releaseSession()
        currentSessionId = sessionId
        initEffects(sessionId)
    }

    private fun initEffects(sessionId: Int) {
        try {
            // Initialize Equalizer
            try {
                equalizer = Equalizer(1000, sessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Native Equalizer not available for session $sessionId: ${e.message}")
            }

            // Initialize Virtualizer (Surround 3D)
            try {
                virtualizer = Virtualizer(1000, sessionId).apply {
                    if (strengthSupported) {
                        enabled = true
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Native Virtualizer not available for session $sessionId: ${e.message}")
            }

            // Initialize BassBoost
            try {
                bassBoost = BassBoost(1000, sessionId).apply {
                    if (strengthSupported) {
                        enabled = true
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Native BassBoost not available for session $sessionId: ${e.message}")
            }

            // Initialize LoudnessEnhancer
            try {
                loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Native LoudnessEnhancer not available for session $sessionId: ${e.message}")
            }

            // Initialize Spatial Reverb
            try {
                presetReverb = PresetReverb(1000, sessionId).apply {
                    preset = PresetReverb.PRESET_SMALLROOM
                    enabled = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Native PresetReverb not available: ${e.message}")
            }

            isEngineActive = true
            Log.d(TAG, "Dolby AudioFx Engine successfully attached to session $sessionId")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing audio effects: ${e.message}")
        }
    }

    fun applySettings(settings: DolbySettings) {
        if (!isEngineActive) {
            initEffects(currentSessionId)
        }

        val masterEnabled = settings.isEnabled

        // Apply Equalizer
        equalizer?.let { eq ->
            try {
                eq.enabled = masterEnabled
                if (masterEnabled) {
                    val numBands = eq.numberOfBands.toInt()
                    val bandLevelRange = eq.bandLevelRange // [min, max] in millibels e.g. [-1500, 1500]
                    val minMb = bandLevelRange.getOrNull(0)?.toInt() ?: -1200
                    val maxMb = bandLevelRange.getOrNull(1)?.toInt() ?: 1200

                    // Intelligent EQ dynamic offsets
                    val ieqOffset = when (settings.intelligentEq) {
                        IntelligentEqMode.DETAILED -> listOf(0.5f, 0.0f, -0.5f, 0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f)
                        IntelligentEqMode.WARM -> listOf(2.0f, 2.0f, 1.5f, 1.0f, 0.5f, 0.0f, -0.5f, -1.0f, -1.5f, -2.0f)
                        IntelligentEqMode.BALANCED -> listOf(1.0f, 1.0f, 0.5f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.5f, 1.5f)
                        IntelligentEqMode.OPEN -> listOf(1.5f, 0.5f, 0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.0f)
                        IntelligentEqMode.FOCUSED -> listOf(-1.0f, -0.5f, 0.0f, 1.5f, 3.0f, 3.0f, 2.0f, 1.0f, 0.0f, -0.5f)
                        IntelligentEqMode.OFF -> List(10) { 0f }
                    }

                    // Map our 10 user bands to device EQ bands
                    for (i in 0 until numBands) {
                        val mappedUserIndex = ((i.toFloat() / (numBands - 1).coerceAtLeast(1)) * 9).toInt().coerceIn(0, 9)
                        val userGainDb = (settings.eqGains.getOrNull(mappedUserIndex) ?: 0f) + (ieqOffset.getOrNull(mappedUserIndex) ?: 0f)
                        // Dialogue enhancer mid-frequency boost
                        val dialogueBoost = if (mappedUserIndex in 3..6) {
                            (settings.dialogueEnhancer / 100f) * 3.5f
                        } else 0f
                        val finalGainDb = userGainDb + dialogueBoost
                        val gainMb = (finalGainDb * 100f).toInt().coerceIn(minMb, maxMb)
                        eq.setBandLevel(i.toShort(), gainMb.toShort())
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to apply Equalizer settings: ${e.message}")
            }
        }

        // Apply Virtualizer (Surround 3D)
        virtualizer?.let { virt ->
            try {
                virt.enabled = masterEnabled && settings.surroundVirtualizer > 0
                if (masterEnabled && virt.strengthSupported) {
                    val strength = (settings.surroundVirtualizer * 10).coerceIn(0, 1000).toShort()
                    virt.setStrength(strength)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to apply Virtualizer: ${e.message}")
            }
        }

        // Apply BassBoost
        bassBoost?.let { bb ->
            try {
                bb.enabled = masterEnabled && settings.bassEnhancer > 0
                if (masterEnabled && bb.strengthSupported) {
                    val strength = (settings.bassEnhancer * 10).coerceIn(0, 1000).toShort()
                    bb.setStrength(strength)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to apply BassBoost: ${e.message}")
            }
        }

        // Apply LoudnessEnhancer (Volume Leveler)
        loudnessEnhancer?.let { le ->
            try {
                le.enabled = masterEnabled && settings.volumeLeveler
                if (masterEnabled && settings.volumeLeveler) {
                    le.setTargetGain(300) // 300 mB = 3dB gentle volume leveling headroom
                } else {
                    le.setTargetGain(0)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to apply LoudnessEnhancer: ${e.message}")
            }
        }

        // Apply PresetReverb based on profile
        presetReverb?.let { rev ->
            try {
                if (masterEnabled && settings.surroundVirtualizer > 50) {
                    rev.enabled = true
                    rev.preset = when (settings.currentProfile) {
                        com.example.data.model.DolbySoundProfile.MOVIE -> PresetReverb.PRESET_LARGEHALL
                        com.example.data.model.DolbySoundProfile.GAME -> PresetReverb.PRESET_MEDIUMROOM
                        com.example.data.model.DolbySoundProfile.MUSIC -> PresetReverb.PRESET_SMALLROOM
                        else -> PresetReverb.PRESET_NONE
                    }
                } else {
                    rev.enabled = false
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to apply PresetReverb: ${e.message}")
            }
        }
    }

    fun releaseSession() {
        try {
            equalizer?.release()
            equalizer = null
            virtualizer?.release()
            virtualizer = null
            bassBoost?.release()
            bassBoost = null
            loudnessEnhancer?.release()
            loudnessEnhancer = null
            presetReverb?.release()
            presetReverb = null
            isEngineActive = false
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio effects: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "DolbyAudioFxEngine"
    }
}
