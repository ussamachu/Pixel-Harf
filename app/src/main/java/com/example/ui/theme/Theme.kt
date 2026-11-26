package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.ui.components.rememberReduceMotion

val LocalPalette = staticCompositionLocalOf { buildPixelHarfPalette(darkVariant = false, colorblindSafe = false) }
val LocalReduceMotion = staticCompositionLocalOf { false }
val LocalPixelIconDotMode = staticCompositionLocalOf { false }

@Composable
fun PixelHarfTheme(
  darkVariant: Boolean = false,
  colorblindSafe: Boolean = false,
  largeText: Boolean = false,
  useDotPixels: Boolean = false,
  content: @Composable () -> Unit,
) {
  val palette = buildPixelHarfPalette(darkVariant = darkVariant, colorblindSafe = colorblindSafe)
  val reduceMotion = rememberReduceMotion()

  val colorScheme =
    if (darkVariant) {
      darkColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        primaryContainer = palette.accentSoft,
        onPrimaryContainer = palette.ink,
        secondary = palette.ink,
        onSecondary = palette.surface,
        background = palette.canvas,
        onBackground = palette.ink,
        surface = palette.surface,
        onSurface = palette.ink,
        surfaceVariant = palette.surfacePressed,
        onSurfaceVariant = palette.mutedInk,
        outline = palette.hairline,
      )
    } else {
      lightColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        primaryContainer = palette.accentSoft,
        onPrimaryContainer = palette.ink,
        secondary = palette.ink,
        onSecondary = Color.White,
        background = palette.canvas,
        onBackground = palette.ink,
        surface = palette.surface,
        onSurface = palette.ink,
        surfaceVariant = palette.surfacePressed,
        onSurfaceVariant = palette.mutedInk,
        outline = palette.hairline,
      )
    }

  CompositionLocalProvider(
    LocalPalette provides palette,
    LocalReduceMotion provides reduceMotion,
    LocalPixelIconDotMode provides useDotPixels,
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = buildAppTypography(largeText),
      shapes = PixelHarfShapes,
      content = content,
    )
  }
}
