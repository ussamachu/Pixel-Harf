package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalBoardPalette = staticCompositionLocalOf { standardBoardPalette() }

@Composable
fun PixelHarfTheme(
  colorblindSafe: Boolean = false,
  largeText: Boolean = false,
  content: @Composable () -> Unit,
) {
  val boardPalette = if (colorblindSafe) colorblindBoardPalette() else standardBoardPalette()

  val colorScheme =
    lightColorScheme(
      primary = boardPalette.accent,
      onPrimary = Color.White,
      primaryContainer = boardPalette.accentSoft,
      onPrimaryContainer = InkBlack,
      secondary = InkBlack,
      onSecondary = Color.White,
      secondaryContainer = CardSurface,
      onSecondaryContainer = InkBlack,
      background = PaperBg,
      onBackground = InkBlack,
      surface = CardSurfaceLight,
      onSurface = InkBlack,
      surfaceVariant = CardSurface,
      onSurfaceVariant = InkMuted,
      outline = CardBorder,
    )

  CompositionLocalProvider(LocalBoardPalette provides boardPalette) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = buildAppTypography(largeText),
      content = content,
    )
  }
}
