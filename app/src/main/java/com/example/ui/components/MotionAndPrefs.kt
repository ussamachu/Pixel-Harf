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
  val useDotPixels: Boolean = true,
  val darkVariant: Boolean = false,
  val glyphPulseEnabled: Boolean = true,
  val hasSeenGameCoachmark: Boolean = false,
)

class UiAppearanceStore(context: Context) {
  private val prefs =
    context.applicationContext.getSharedPreferences("pixel_harf_ui_appearance", Context.MODE_PRIVATE)

  fun load(): UiAppearanceState =
    UiAppearanceState(
      useDotPixels = prefs.getBoolean("use_dot_pixels", true),
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

private val NothingEaseOut = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

/**
 * Staggered widget entrance: soft slide + fade (150-220ms, ease-out), respecting reduce motion.
 */
fun Modifier.widgetStaggerEntrance(index: Int): Modifier = composed {
  val reduceMotion = LocalReduceMotion.current
  var visible by remember { mutableStateOf(reduceMotion) }

  LaunchedEffect(reduceMotion) {
    if (reduceMotion) {
      visible = true
    } else {
      delay((index.coerceIn(0, 8) * 28L))
      visible = true
    }
  }

  val alpha by
    animateFloatAsState(
      targetValue = if (visible) 1f else 0f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 190, easing = NothingEaseOut),
      label = "widget_alpha",
    )
  val offsetY by
    animateFloatAsState(
      targetValue = if (visible) 0f else 14f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 200, easing = NothingEaseOut),
      label = "widget_slide",
    )

  this.graphicsLayer {
    this.alpha = alpha
    translationY = if (reduceMotion) 0f else offsetY
  }
}

/**
 * Mechanical press feedback: scales to 0.96 with a tiny haptic tick when pressed.
 */
fun Modifier.mechanicalPress(
  interactionSource: MutableInteractionSource,
  hapticsEnabled: Boolean = true,
): Modifier = composed {
  val reduceMotion = LocalReduceMotion.current
  val pressed by interactionSource.collectIsPressedAsState()
  val haptic = LocalHapticFeedback.current

  LaunchedEffect(pressed) {
    if (pressed && hapticsEnabled) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }

  val scale by
    animateFloatAsState(
      targetValue = if (pressed && !reduceMotion) 0.96f else 1f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 90, easing = NothingEaseOut),
      label = "press_scale",
    )

  this.graphicsLayer {
    scaleX = scale
    scaleY = scale
  }
}
