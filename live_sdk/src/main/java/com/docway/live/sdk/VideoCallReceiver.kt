package com.docway.live.sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

internal class VideoCallReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.action?.let {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (it.equals(Intent.ACTION_HEADSET_PLUG)) {
                val state = intent.getIntExtra("state", -1)

                when (state) {
                    0 -> {
                        audioManager.isSpeakerphoneOn = true
                    }
                    1 -> {
                        audioManager.isSpeakerphoneOn = false
                    }
                }
            }
        }
    }
}