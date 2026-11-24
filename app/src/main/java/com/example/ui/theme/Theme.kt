package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.ui.components.rememberReduceMotion

val LocalNothingPalette = staticCompositionLocalOf { buildNothingPalette(darkVariant = false, colorblindSafe = false) }
val LocalReduceMotion = staticCompositionLocalOf { false }

@Composable
fun NothingTheme(
  darkVariant: Boolean = false,
  colorblindSafe: Boolean = false,
  largeText: Boolean = false,
  content: @Composable () -> Unit,
) {
  val palette = buildNothingPalette(darkVariant = darkVariant, colorblindSafe = colorblindSafe)
  val reduceMotion = rememberReduceMotion()

  val colorScheme =
    if (darkVariant) {
      darkColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        primaryContainer = palette.accentSoft,
        onPrimaryContainer = palette.ink,
        secondary = palette.ink,
        onSecondary = palette.widgetSurface,
        background = palette.bgMid,
        onBackground = palette.ink,
        surface = palette.widgetSurface,
        onSurface = palette.ink,
        surfaceVariant = palette.widgetPressed,
        onSurfaceVariant = palette.mutedInk,
        outline = palette.faintDot,
      )
    } else {
      lightColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        primaryContainer = palette.accentSoft,
        onPrimaryContainer = palette.ink,
        secondary = palette.ink,
        onSecondary = Color.White,
        background = palette.bgMid,
        onBackground = palette.ink,
        surface = palette.widgetSurface,
        onSurface = palette.ink,
        surfaceVariant = palette.widgetPressed,
        onSurfaceVariant = palette.mutedInk,
        outline = palette.faintDot,
      )
    }

  CompositionLocalProvider(
    LocalNothingPalette provides palette,
    LocalReduceMotion provides reduceMotion,
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = buildNothingTypography(largeText),
      shapes = NothingShapes,
      content = content,
    )
  }
}
