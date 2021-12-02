package com.bignerdranch.android.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

private const val TAG = "BeatBox"
private const val SOUNDS_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5

class BeatBox(private var assets: AssetManager) {
    var progressValue = 0
    var rate = 0.0f

    val sounds: List<Sound>
    private var soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS)
        .build()
    init {
        sounds = loadSounds()
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 0.1f, 0.1f, 1, 0, rate)
            Log.i("roland", "Rate: $rate, progressValue: $progressValue")
        }
    }

    fun release() {
        soundPool.release()
    }

    private fun loadSounds(): List<Sound> {
        val soundNames: Array<String>
        try {
            soundNames = assets.list(SOUNDS_FOLDER)!!
            Log.d(TAG, "Found ${soundNames.size} sounds!")
        } catch (e: Exception) {
            Log.d(TAG, "Could not list assets!", e)
            return emptyList()
        }
        val sounds = mutableListOf<Sound>()
        soundNames.forEach { filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                load(sound)
                sounds.add(sound)
            } catch(ioe: IOException) {
                Log.e(TAG, "Could not load sound $filename", ioe)
            }
        }
        return sounds
    }

    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(afd, 1)
        sound.soundId = soundId
    }
}