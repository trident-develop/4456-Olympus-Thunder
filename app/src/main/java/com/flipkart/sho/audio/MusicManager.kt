package com.flipkart.sho.audio

import android.content.Context
import android.media.MediaPlayer
import com.flipkart.sho.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isPrepared = false

    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.music_bg).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
            isPrepared = true
        }
        if (isPrepared && mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        if (enabled) start(context) else pause()
    }
}
