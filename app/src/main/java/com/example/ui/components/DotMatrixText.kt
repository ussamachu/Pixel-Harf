package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.LruCache
import androidx.annotation.FontRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import kotlin.math.max

class DotGridRaster(
  val cols: Int,
  val rows: Int,
  val packedCoords: IntArray,
)

private object DotRasterCache {
  private val cache = LruCache<String, DotGridRaster>(180)
  private val typefaceCache = mutableMapOf<Int, Typeface>()

  fun getOrRasterize(
    context: Context,
    text: String,
    @FontRes fontResId: Int,
    targetRows: Int,
  ): DotGridRaster {
    val key = "$text|$fontResId|$targetRows"
    cache.get(key)?.let { return it }

    val typeface =
      typefaceCache.getOrPut(fontResId) {
        ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT_BOLD
      }

    val safeText = text.ifBlank { " " }
    val paint =
      Paint().apply {
        isAntiAlias = false
        isSubpixelText = false
        color = AndroidColor.BLACK
        this.typeface = typeface
        textSize = targetRows.toFloat() * 1.15f
        textAlign = Paint.Align.LEFT
      }

    val bounds = Rect()
    paint.getTextBounds(safeText, 0, safeText.length, bounds)
    val measuredW = max(paint.measureText(safeText).toInt(), bounds.width())
    val bmpW = (measuredW + 8).coerceIn(12, 320)
    val bmpH = (targetRows + 10).coerceIn(14, 72)

    val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val fm = paint.fontMetrics
    val baseline = (bmpH - (fm.descent - fm.ascent)) * 0.5f - fm.ascent
    val drawX = ((bmpW - bounds.width()) * 0.5f) - bounds.left
    canvas.drawText(safeText, drawX, baseline, paint)

    var minX = bmpW
    var maxX = -1
    var minY = bmpH
    var maxY = -1

    for (y in 0 until bmpH) {
      for (x in 0 until bmpW) {
        if (AndroidColor.alpha(bitmap.getPixel(x, y)) >= 92) {
          if (x < minX) minX = x
          if (x > maxX) maxX = x
          if (y < minY) minY = y
          if (y > maxY) maxY = y
        }
      }
    }

    val coordsList = ArrayList<Int>(bmpW * 4)
    val finalCols: Int
    val finalRows: Int
    if (maxX < minX || maxY < minY) {
      finalCols = 4
      finalRows = targetRows
    } else {
      finalCols = (maxX - minX + 1).coerceAtLeast(1)
      finalRows = (maxY - minY + 1).coerceAtLeast(1)
      for (y in minY..maxY) {
        for (x in minX..maxX) {
          if (AndroidColor.alpha(bitmap.getPixel(x, y)) >= 92) {
            coordsList.add(x - minX)
            coordsList.add(y - minY)
          }
        }
      }
    }
    bitmap.recycle()

    val result = DotGridRaster(finalCols, finalRows, coordsList.toIntArray())
    cache.put(key, result)
    return result
  }
}

/**
 * Arabic pixel/dot-matrix lettering composable (used sparingly: max 1 per screen).
 */
@Composable
fun DotText(
  text: String,
  modifier: Modifier = Modifier,
  @FontRes fontResId: Int = R.font.noto_kufi_arabic,
  dotSize: Dp = 2.0.dp,
  color: Color = LocalPalette.current.ink,
  targetRows: Int = 15,
  animateScanIn: Boolean = false,
  scanKey: Any? = text,
) {
  val context = LocalContext.current
  val reduceMotion = LocalReduceMotion.current

  val raster =
    remember(text, fontResId, targetRows) {
      DotRasterCache.getOrRasterize(context, text, fontResId, targetRows)
    }

  val scanAnim = remember(scanKey, animateScanIn, reduceMotion) {
    Animatable(if (animateScanIn && !reduceMotion) 0f else 1f)
  }

  LaunchedEffect(scanKey, animateScanIn, reduceMotion) {
    if (animateScanIn && !reduceMotion) {
      scanAnim.snapTo(0f)
      scanAnim.animateTo(1f, animationSpec = tween(durationMillis = 240))
    } else {
      scanAnim.snapTo(1f)
    }
  }

  val pitchRatio = 1.35f
  val widthDp = dotSize * (raster.cols * pitchRatio)
  val heightDp = dotSize * (raster.rows * pitchRatio)

  Canvas(
    modifier =
      modifier
        .size(width = widthDp, height = heightDp)
        .semantics { contentDescription = text }
  ) {
    val pitch = dotSize.toPx() * pitchRatio
    val radius = (dotSize.toPx() * 0.5f).coerceAtLeast(1f)
    val coords = raster.packedCoords
    val visibleMaxRow = (raster.rows * scanAnim.value).toInt() + 1

    var i = 0
    while (i + 1 < coords.size) {
      val gx = coords[i]
      val gy = coords[i + 1]
      if (!animateScanIn || gy <= visibleMaxRow) {
        drawCircle(
          color = color,
          radius = radius,
          center = Offset(gx * pitch + pitch * 0.5f, gy * pitch + pitch * 0.5f),
        )
      }
      i += 2
    }
  }
}

