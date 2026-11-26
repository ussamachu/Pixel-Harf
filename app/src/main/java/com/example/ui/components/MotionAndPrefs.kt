package com.example.ui.components

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.ui.theme.LocalReduceMotion
import kotlinx.coroutines.delay

@Immutable
data class UiAppearanceState(
  val useDotPixels: Boolean = false, // Square pixels by default; dot mode optional in Settings
  val darkVariant: Boolean = false,
  val glyphPulseEnabled: Boolean = true,
  val hasSeenGameCoachmark: Boolean = false,
)

class UiAppearanceStore(context: Context) {
  private val prefs =
    context.applicationContext.getSharedPreferences("pixel_harf_ui_appearance", Context.MODE_PRIVATE)

  fun load(): UiAppearanceState =
    UiAppearanceState(
      useDotPixels = prefs.getBoolean("use_dot_pixels", false),
      darkVariant = prefs.getBoolean("dark_variant", false),
      glyphPulseEnabled = prefs.getBoolean("glyph_pulse", true),
      hasSeenGameCoachmark = prefs.getBoolean("seen_game_coachmark", false),
    )

  fun save(state: UiAppearanceState) {
    prefs
      .edit()
      .putBoolean("use_dot_pixels", state.useDotPixels)
      .putBoolean("dark_variant", state.darkVariant)
      .putBoolean("glyph_pulse", state.glyphPulseEnabled)
      .putBoolean("seen_game_coachmark", state.hasSeenGameCoachmark)
      .apply()
  }
}

@Composable
fun rememberReduceMotion(): Boolean {
  val context = LocalContext.current
  return remember(context) {
    try {
      val scale =
        Settings.Global.getFloat(
          context.contentResolver,
          Settings.Global.ANIMATOR_DURATION_SCALE,
          1.0f,
        )
      scale == 0f
    } catch (_: Throwable) {
      false
    }
  }
}

private val EaseOut = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

/**
 * Subtle staggered entrance respecting system reduce-motion.
 */
fun Modifier.widgetStaggerEntrance(index: Int): Modifier = composed {
  val reduceMotion = LocalReduceMotion.current
  var visible by remember { mutableStateOf(reduceMotion) }

  LaunchedEffect(reduceMotion) {
    if (reduceMotion) {
      visible = true
    } else {
      delay((index.coerceIn(0, 6) * 45L))
      visible = true
    }
  }

  val alpha by
    animateFloatAsState(
      targetValue = if (visible) 1f else 0f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 200, easing = EaseOut),
      label = "entrance_alpha",
    )

  this.graphicsLayer { this.alpha = alpha }
}

/**
 * Tactile haptic tick on press without glow/ripple.
 */
@Composable
fun rememberPressHaptic(
  interactionSource: MutableInteractionSource,
  hapticsEnabled: Boolean,
): Boolean {
  val pressed by interactionSource.collectIsPressedAsState()
  val haptic = LocalHapticFeedback.current

  LaunchedEffect(pressed) {
    if (pressed && hapticsEnabled) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }
  return pressed
}
