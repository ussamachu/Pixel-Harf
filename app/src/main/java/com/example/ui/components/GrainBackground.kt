package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.theme.LocalPalette

/**
 * Flat warm monochrome canvas (#FDFCFC light / #201D1D dark) with zero grain, noise, or gradients.
 */
@Composable
fun FlatCanvasBackground(modifier: Modifier = Modifier) {
  val palette = LocalPalette.current
  Box(modifier = modifier.fillMaxSize().background(palette.canvas))
}
