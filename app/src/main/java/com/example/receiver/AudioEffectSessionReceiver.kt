package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.util.Log

class AudioEffectSessionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, AudioEffect.ERROR)
        val packageName = intent.getStringExtra(AudioEffect.EXTRA_PACKAGE_NAME)

        Log.d(TAG, "AudioEffect session broadcast received: action=$action, sessionId=$sessionId, pkg=$packageName")

        if (sessionId != AudioEffect.ERROR && sessionId > 0) {
            when (action) {
                AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                    SessionEventsHub.emitSessionOpened(sessionId, packageName)
                }
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                    SessionEventsHub.emitSessionClosed(sessionId)
                }
            }
        }
    }

    companion object {
        private const val TAG = "AudioEffectReceiver"
    }
}

object SessionEventsHub {
    private val listeners = mutableListOf<(sessionId: Int, packageName: String?, isOpened: Boolean) -> Unit>()

    fun addListener(listener: (sessionId: Int, packageName: String?, isOpened: Boolean) -> Unit) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: (sessionId: Int, packageName: String?, isOpened: Boolean) -> Unit) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun emitSessionOpened(sessionId: Int, packageName: String?) {
        synchronized(listeners) {
            listeners.forEach { it(sessionId, packageName, true) }
        }
    }

    fun emitSessionClosed(sessionId: Int) {
        synchronized(listeners) {
            listeners.forEach { it(sessionId, null, false) }
        }
    }
}
