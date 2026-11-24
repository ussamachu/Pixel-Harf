package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.ui.theme.LocalNothingPalette
import kotlin.random.Random

/**
 * Pre-rendered monochrome grayscale blob gradient + subtle noise grain bitmap.
 * Zero allocations inside onDraw.
 */
@Composable
fun GrainBackground(modifier: Modifier = Modifier) {
  val palette = LocalNothingPalette.current

  // Pre-render a tileable 128x128 grayscale noise bitmap once
  val noiseBitmap: ImageBitmap = remember(palette.isDark) {
    val size = 128
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val rnd = Random(2026)
    val baseAlpha = if (palette.isDark) 14 else 11
    val pixels = IntArray(size * size)
    for (i in pixels.indices) {
      val bright = rnd.nextBoolean()
      val alpha = rnd.nextInt(3, baseAlpha)
      val rgb = if (bright) 0xFF else 0x00
      pixels[i] = (alpha shl 24) or (rgb shl 16) or (rgb shl 8) or rgb
    }
    bmp.setPixels(pixels, 0, size, 0, 0, size, size)
    bmp.asImageBitmap()
  }

  val topLightBlob =
    remember(palette.isDark) {
      if (palette.isDark) Color(0xFF282828) else Color(0xFFE6E8E0)
    }
  val lowerDarkBlob =
    remember(palette.isDark) {
      if (palette.isDark) Color(0xFF060606) else Color(0xFFA7ABA1)
    }

  Canvas(modifier = modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    // Base soft vertical gradient
    drawRect(
      brush =
        Brush.verticalGradient(
          colors = listOf(palette.bgTop, palette.bgMid, palette.bgBottom),
        ),
    )

    // Soft monochrome radial blobs
    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(topLightBlob.copy(alpha = 0.65f), Color.Transparent),
          center = Offset(w * 0.2f, h * 0.14f),
          radius = w * 0.85f,
        ),
      radius = w * 0.85f,
      center = Offset(w * 0.2f, h * 0.14f),
    )

    drawCircle(
      brush =
        Brush.radialGradient(
          colors = listOf(lowerDarkBlob.copy(alpha = 0.55f), Color.Transparent),
          center = Offset(w * 0.85f, h * 0.82f),
          radius = w * 0.9f,
        ),
      radius = w * 0.9f,
      center = Offset(w * 0.85f, h * 0.82f),
    )

    // Tile pre-rendered noise bitmap across screen
    val tile = noiseBitmap.width
    var y = 0
    while (y < h.toInt()) {
      var x = 0
      while (x < w.toInt()) {
        drawImage(
          image = noiseBitmap,
          dstOffset = IntOffset(x, y),
          dstSize = IntSize(tile, tile),
        )
        x += tile
      }
      y += tile
    }
  }
}
