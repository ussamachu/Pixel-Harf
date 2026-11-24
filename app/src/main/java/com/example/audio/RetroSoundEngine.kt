package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.sin

class RetroSoundEngine {

  private val executor = Executors.newSingleThreadExecutor()
  @Volatile var enabled: Boolean = true

  fun playEat(combo: Int) {
    if (!enabled) return
    val baseFreq = (420.0 + (combo.coerceAtMost(10) * 45.0))
    playToneSequence(listOf(baseFreq to 42))
  }

  fun playBonusDot() {
    if (!enabled) return
    playToneSequence(listOf(660.0 to 45, 880.0 to 70))
  }

  fun playPowerUp() {
    if (!enabled) return
    playToneSequence(listOf(523.25 to 45, 659.25 to 45, 783.99 to 65, 1046.5 to 90))
  }

  fun playHurt() {
    if (!enabled) return
    playToneSequence(listOf(190.0 to 80, 130.0 to 120), square = true)
  }

  fun playLevelComplete() {
    if (!enabled) return
    playToneSequence(
      listOf(523.25 to 70, 659.25 to 70, 783.99 to 70, 1046.50 to 160),
      square = false,
    )
  }

  fun playGameOver() {
    if (!enabled) return
    playToneSequence(listOf(392.0 to 90, 329.63 to 90, 246.94 to 170), square = true)
  }

  private fun playToneSequence(notes: List<Pair<Double, Int>>, square: Boolean = true) {
    executor.execute {
      try {
        val sampleRate = 22050
        val totalSamples = notes.sumOf { (sampleRate * it.second) / 1000 }
        if (totalSamples <= 0) return@execute
        val pcm = ShortArray(totalSamples)
        var offset = 0

        for ((freq, durationMs) in notes) {
          val count = (sampleRate * durationMs) / 1000
          for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env =
              when {
                i < count * 0.08 -> i / (count * 0.08)
                i > count * 0.72 -> (count - i) / (count * 0.28)
                else -> 1.0
              }.coerceIn(0.0, 1.0)
            val wave =
              if (square) {
                if (sin(2.0 * PI * freq * t) >= 0.0) 0.35 else -0.35
              } else {
                0.45 * sin(2.0 * PI * freq * t)
              }
            pcm[offset + i] = (wave * env * Short.MAX_VALUE).toInt().toShort()
          }
          offset += count
        }

        val track =
          AudioTrack.Builder()
            .setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
            .setAudioFormat(
              AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcm, 0, pcm.size)
        track.play()
        val totalMs = notes.sumOf { it.second }.toLong()
        Thread.sleep(totalMs + 25L)
        track.stop()
        track.release()
      } catch (_: Throwable) {
        // Ignore audio hardware failures on restricted devices
      }
    }
  }

  fun release() {
    executor.shutdownNow()
  }
}
