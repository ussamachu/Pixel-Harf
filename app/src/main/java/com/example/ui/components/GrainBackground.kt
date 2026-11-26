package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LocalPalette
import kotlin.math.max

/**
 * Soft blurred gradient background built from 4 large radial-gradient blobs drawn once on a Canvas.
 * Zero per-frame work, no blur shader required, and zero grain.
 */
@Composable
fun FrostedGradientBackground(modifier: Modifier = Modifier) {
  val palette = LocalPalette.current

  Canvas(modifier = modifier.fillMaxSize()) {
    drawRect(color = palette.canvas)

    val maxDim = max(size.width, size.height)

    // Top-start warm sand blob
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(palette.blobSand, Color.Transparent),
          center = Offset(size.width * 0.16f, size.height * 0.14f),
          radius = maxDim * 0.72f,
        ),
      radius = maxDim * 0.72f,
      center = Offset(size.width * 0.16f, size.height * 0.14f),
    )

    // Mid-end pale sage blob
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(palette.blobSage, Color.Transparent),
          center = Offset(size.width * 0.88f, size.height * 0.42f),
          radius = maxDim * 0.76f,
        ),
      radius = maxDim * 0.76f,
      center = Offset(size.width * 0.88f, size.height * 0.42f),
    )

    // Bottom-start faint orange glow
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(palette.blobGlow, Color.Transparent),
          center = Offset(size.width * 0.24f, size.height * 0.78f),
          radius = maxDim * 0.58f,
        ),
      radius = maxDim * 0.58f,
      center = Offset(size.width * 0.24f, size.height * 0.78f),
    )

    // Bottom-end soft sand anchor
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(palette.blobSand.copy(alpha = 0.70f), Color.Transparent),
          center = Offset(size.width * 0.82f, size.height * 0.88f),
          radius = maxDim * 0.65f,
        ),
      radius = maxDim * 0.65f,
      center = Offset(size.width * 0.82f, size.height * 0.88f),
    )
  }
}

@Composable
fun FlatCanvasBackground(modifier: Modifier = Modifier) {
  FrostedGradientBackground(modifier = modifier)
}
