package com.example.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticHelper(context: Context) {

  private val vibrator: Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

  @Volatile var enabled: Boolean = true

  fun tick() = vibrate(14L, 70)

  fun powerUp() = vibrate(32L, 140)

  fun hurt() = vibrate(85L, 210)

  fun complete() = vibrate(55L, 170)

  private fun vibrate(durationMs: Long, amplitude: Int) {
    if (!enabled) return
    val vib = vibrator ?: return
    if (!vib.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vib.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
      } else {
        @Suppress("DEPRECATION")
        vib.vibrate(durationMs)
      }
    } catch (_: Throwable) {
    }
  }
}
