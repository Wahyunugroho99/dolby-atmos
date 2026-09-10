package com.example.data.model

import androidx.annotation.StringRes
import com.example.R

enum class AudioDemoType(
    @StringRes val titleRes: Int,
    val durationSeconds: Int,
    val description: String
) {
    CINEMATIC_ATMOS(
        titleRes = R.string.demo_track_cinema,
        durationSeconds = 45,
        description = "Atmospheric cinema spatial soundstage with sub-bass pulses and dynamic stereo imaging."
    ),
    SPATIAL_8D(
        titleRes = R.string.demo_track_surround,
        durationSeconds = 40,
        description = "360-degree rotating binaural spatial motion orbiting around listener ears."
    ),
    ACOUSTIC_MUSIC(
        titleRes = R.string.demo_track_music,
        durationSeconds = 50,
        description = "High-definition multi-harmonic musical chord progression showcasing EQ separation."
    ),
    SUB_BASS_TEST(
        titleRes = R.string.demo_track_bass,
        durationSeconds = 35,
        description = "Deep low-frequency sub-bass impact sweeps (30Hz - 120Hz) to test bass enhancer."
    ),
    VOICE_CLARITY(
        titleRes = R.string.demo_track_voice,
        durationSeconds = 30,
        description = "Vocal formant resonance tones specifically engineered to test dialogue enhancer."
    ),
    LOCAL_FILE(
        titleRes = R.string.play_local_file,
        durationSeconds = 0,
        description = "Play and process your own audio track with real-time Dolby Atmos effects."
    )
}
