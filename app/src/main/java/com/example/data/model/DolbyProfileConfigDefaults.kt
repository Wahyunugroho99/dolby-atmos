package com.example.data.model

data class DolbyProfilePreset(
    val intelligentEq: IntelligentEqMode,
    val surroundVirtualizer: Int,
    val dialogueEnhancer: Int,
    val bassEnhancer: Int,
    val volumeLeveler: Boolean,
    val currentEqPreset: EqPreset,
    val eqGains: List<Float>
)

object DolbyProfileConfigDefaults {
    val FREQUENCY_BANDS = listOf(
        32, 64, 125, 250, 500, 1000, 2000, 4000, 8000, 16000
    )
    val FREQUENCY_LABELS = listOf(
        "32Hz", "64Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz"
    )

    fun getConfigForProfile(profile: DolbySoundProfile): DolbyProfilePreset {
        return when (profile) {
            DolbySoundProfile.DYNAMIC -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.BALANCED,
                surroundVirtualizer = 80,
                dialogueEnhancer = 60,
                bassEnhancer = 70,
                volumeLeveler = true,
                currentEqPreset = EqPreset.POP,
                eqGains = listOf(2.5f, 3.0f, 1.5f, 0.0f, 1.0f, 2.5f, 3.0f, 2.0f, 3.5f, 4.0f)
            )
            DolbySoundProfile.MOVIE -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.DETAILED,
                surroundVirtualizer = 95,
                dialogueEnhancer = 85,
                bassEnhancer = 90,
                volumeLeveler = true,
                currentEqPreset = EqPreset.ROCK,
                eqGains = listOf(5.5f, 4.5f, 2.0f, -0.5f, 1.0f, 3.0f, 3.5f, 2.5f, 4.0f, 5.0f)
            )
            DolbySoundProfile.MUSIC -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.WARM,
                surroundVirtualizer = 60,
                dialogueEnhancer = 40,
                bassEnhancer = 65,
                volumeLeveler = false,
                currentEqPreset = EqPreset.ELECTRONIC,
                eqGains = listOf(3.5f, 2.5f, 1.0f, 0.0f, -0.5f, 0.5f, 1.5f, 2.5f, 3.5f, 4.0f)
            )
            DolbySoundProfile.VOICE -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.FOCUSED,
                surroundVirtualizer = 25,
                dialogueEnhancer = 95,
                bassEnhancer = 30,
                volumeLeveler = true,
                currentEqPreset = EqPreset.VOCAL,
                eqGains = listOf(-2.0f, -1.5f, 0.0f, 2.5f, 4.5f, 5.0f, 3.5f, 2.0f, 0.5f, -1.0f)
            )
            DolbySoundProfile.GAME -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.OPEN,
                surroundVirtualizer = 90,
                dialogueEnhancer = 70,
                bassEnhancer = 80,
                volumeLeveler = false,
                currentEqPreset = EqPreset.HIPHOP,
                eqGains = listOf(4.5f, 3.5f, 1.5f, 0.5f, 0.0f, 1.5f, 3.0f, 4.5f, 5.0f, 4.5f)
            )
            DolbySoundProfile.CUSTOM -> DolbyProfilePreset(
                intelligentEq = IntelligentEqMode.DETAILED,
                surroundVirtualizer = 75,
                dialogueEnhancer = 50,
                bassEnhancer = 60,
                volumeLeveler = true,
                currentEqPreset = EqPreset.CUSTOM,
                eqGains = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            )
        }
    }
}
