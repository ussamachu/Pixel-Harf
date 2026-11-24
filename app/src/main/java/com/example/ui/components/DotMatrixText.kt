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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class DotGridRaster(
  val cols: Int,
  val rows: Int,
  // Packed x, y coordinates: x = packed[i * 2], y = packed[i * 2 + 1]
  val packedCoords: IntArray,
) {
  val dotCount: Int get() = packedCoords.size / 2
}

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
 * Reusable dot-matrix Arabic/Latin text renderer.
 * Rasterizes the given font into a cached grid and draws each "on" pixel as a round dot.
 * Zero allocations inside Canvas draw lambda.
 */
@Composable
fun DotText(
  text: String,
  modifier: Modifier = Modifier,
  @FontRes fontResId: Int = R.font.noto_kufi_arabic,
  dotSize: Dp = 2.6.dp,
  color: Color = LocalNothingPalette.current.ink,
  targetRows: Int = 18,
  outlineOnly: Boolean = false,
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
      scanAnim.animateTo(1f, animationSpec = tween(durationMillis = 260))
    } else {
      scanAnim.snapTo(1f)
    }
  }

  val pitchRatio = 1.38f
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
        val cx = gx * pitch + pitch * 0.5f
        val cy = gy * pitch + pitch * 0.5f
        if (outlineOnly) {
          if ((gx + gy) % 2 == 0) {
            drawCircle(
              color = color,
              radius = radius * 0.72f,
              center = Offset(cx, cy),
            )
          }
        } else {
          drawCircle(
            color = color,
            radius = radius,
            center = Offset(cx, cy),
          )
        }
      }
      i += 2
    }
  }
}

enum class DotIconType {
  MAP,
  MODES,
  SETTINGS,
  SOUND_ON,
  SOUND_OFF,
  PAUSE,
  PLAY,
  BACK,
  ARROW_RIGHT,
  ARROW_LEFT,
  LOCK,
  SLOW_MO,
  MAGNET,
  GHOST,
  SHRINK,
  HEART,
  DAILY,
  CHECK,
}

private object DotIconBitmaps {
  // 7x7 crisp pixel-dot bitmasks
  private val grids: Map<DotIconType, Array<String>> =
    mapOf(
      DotIconType.PLAY to
        arrayOf(
          ".#.....",
          ".##....",
          ".###...",
          ".####..",
          ".###...",
          ".##....",
          ".#.....",
        ),
      DotIconType.PAUSE to
        arrayOf(
          ".##.##.",
          ".##.##.",
          ".##.##.",
          ".##.##.",
          ".##.##.",
          ".##.##.",
          ".##.##.",
        ),
      DotIconType.ARROW_RIGHT to
        arrayOf(
          "...#...",
          "....#..",
          ".....#.",
          "#######",
          ".....#.",
          "....#..",
          "...#...",
        ),
      DotIconType.ARROW_LEFT to
        arrayOf(
          "...#...",
          "..#....",
          ".#.....",
          "#######",
          ".#.....",
          "..#....",
          "...#...",
        ),
      DotIconType.BACK to
        arrayOf(
          "...#...",
          "..#....",
          ".#.....",
          "#######",
          ".#.....",
          "..#....",
          "...#...",
        ),
      DotIconType.MAP to
        arrayOf(
          "##.##.#",
          "##.##.#",
          ".......",
          "#.##.##",
          "#.##.##",
          ".......",
          "##.##.#",
        ),
      DotIconType.MODES to
        arrayOf(
          "..###..",
          ".#...#.",
          "#..#..#",
          "#######",
          "#..#..#",
          ".#...#.",
          "..###..",
        ),
      DotIconType.SETTINGS to
        arrayOf(
          "..#.#..",
          ".#####.",
          "##...##",
          "#..#..#",
          "##...##",
          ".#####.",
          "..#.#..",
        ),
      DotIconType.SOUND_ON to
        arrayOf(
          "...#...",
          "..##.#.",
          "####..#",
          "####..#",
          "####..#",
          "..##.#.",
          "...#...",
        ),
      DotIconType.SOUND_OFF to
        arrayOf(
          "#..#...",
          ".###...",
          "####.#.",
          "###.#..",
          "####.#.",
          "..##..#",
          "...#...",
        ),
      DotIconType.LOCK to
        arrayOf(
          "..###..",
          ".#...#.",
          ".#...#.",
          "#######",
          "###.###",
          "###.###",
          "#######",
        ),
      DotIconType.SLOW_MO to
        arrayOf(
          "#######",
          ".#...#.",
          "..#.#..",
          "...#...",
          "..#.#..",
          ".#...#.",
          "#######",
        ),
      DotIconType.MAGNET to
        arrayOf(
          "##...##",
          "##...##",
          "##...##",
          "##...##",
          ".##.##.",
          "..###..",
          ".......",
        ),
      DotIconType.GHOST to
        arrayOf(
          "..###..",
          ".#####.",
          "##.#.##",
          "#######",
          "#######",
          "#######",
          "#.#.#.#",
        ),
      DotIconType.SHRINK to
        arrayOf(
          "#.....#",
          ".#...#.",
          "..#.#..",
          ".......",
          "..#.#..",
          ".#...#.",
          "#.....#",
        ),
      DotIconType.HEART to
        arrayOf(
          ".##.##.",
          "#######",
          "#######",
          "#######",
          ".#####.",
          "..###..",
          "...#...",
        ),
      DotIconType.DAILY to
        arrayOf(
          ".#...#.",
          "#######",
          "#.....#",
          "#.###.#",
          "#.###.#",
          "#.....#",
          "#######",
        ),
      DotIconType.CHECK to
        arrayOf(
          "......#",
          ".....#.",
          "....#..",
          "#..#...",
          ".##....",
          ".......",
          ".......",
        ),
    )

  private val packed: Map<DotIconType, IntArray> =
    grids.mapValues { (_, rows) ->
      val list = ArrayList<Int>(48)
      rows.forEachIndexed { y, row ->
        row.forEachIndexed { x, ch ->
          if (ch == '#') {
            list.add(x)
            list.add(y)
          }
        }
      }
      list.toIntArray()
    }

  fun coords(type: DotIconType): IntArray = packed[type] ?: IntArray(0)
}

@Composable
fun DotIcon(
  type: DotIconType,
  modifier: Modifier = Modifier,
  dotSize: Dp = 2.6.dp,
  color: Color = LocalNothingPalette.current.ink,
  mirrorForRtl: Boolean = false,
  contentDescription: String? = null,
) {
  val layoutDir = LocalLayoutDirection.current
  val mirror = mirrorForRtl && layoutDir == LayoutDirection.Rtl
  val coords = remember(type) { DotIconBitmaps.coords(type) }
  val pitchRatio = 1.38f
  val total = dotSize * (7f * pitchRatio)

  Canvas(
    modifier =
      modifier
        .size(total)
        .let { m ->
          if (contentDescription != null) m.semantics { this.contentDescription = contentDescription }
          else m
        }
  ) {
    val pitch = dotSize.toPx() * pitchRatio
    val radius = dotSize.toPx() * 0.48f
    var i = 0
    while (i + 1 < coords.size) {
      val gx = if (mirror) 6 - coords[i] else coords[i]
      val gy = coords[i + 1]
      drawCircle(
        color = color,
        radius = radius,
        center = Offset(gx * pitch + pitch * 0.5f, gy * pitch + pitch * 0.5f),
      )
      i += 2
    }
  }
}

/**
 * Thin progress ring composed of evenly spaced circular dots. Zero allocations in draw lambda.
 */
@Composable
fun DotRing(
  progress: Float,
  modifier: Modifier = Modifier,
  size: Dp = 86.dp,
  dotCount: Int = 28,
  dotSize: Dp = 3.2.dp,
  activeColor: Color = LocalNothingPalette.current.accent,
  inactiveColor: Color = LocalNothingPalette.current.faintDot,
) {
  val clamped = progress.coerceIn(0f, 1f)
  val filledDots = (clamped * dotCount).toInt()
  val layoutDir = LocalLayoutDirection.current
  val isRtl = layoutDir == LayoutDirection.Rtl

  Canvas(modifier = modifier.size(size)) {
    val rDot = dotSize.toPx() * 0.5f
    val ringR = (min(this.size.width, this.size.height) * 0.5f) - rDot * 2f
    val cx = this.size.width * 0.5f
    val cy = this.size.height * 0.5f
    val stepAngle = (2.0 * PI) / dotCount.toDouble()

    for (i in 0 until dotCount) {
      val dirSign = if (isRtl) -1.0 else 1.0
      val angle = -PI * 0.5 + dirSign * i * stepAngle
      val dx = cx + (cos(angle) * ringR).toFloat()
      val dy = cy + (sin(angle) * ringR).toFloat()
      val isOn = i < filledDots
      drawCircle(
        color = if (isOn) activeColor else inactiveColor,
        radius = if (isOn) rDot * 1.08f else rDot * 0.85f,
        center = Offset(dx, dy),
      )
    }
  }
}

/**
 * 3 dot-matrix stars for level tiles & completion screen.
 */
@Composable
fun DotStarRow(
  stars: Int,
  modifier: Modifier = Modifier,
  dotSize: Dp = 2.dp,
  spacing: Dp = 4.dp,
  filledColor: Color = LocalNothingPalette.current.accent,
  emptyColor: Color = LocalNothingPalette.current.faintDot,
) {
  // 5x5 crisp dot-star mask
  val starCoords = remember {
    intArrayOf(
      2, 0,
      1, 1, 2, 1, 3, 1,
      0, 2, 1, 2, 2, 2, 3, 2, 4, 2,
      1, 3, 2, 3, 3, 3,
      0, 4, 4, 4,
    )
  }
  val pitchRatio = 1.32f
  val singleSize = dotSize * (5f * pitchRatio)

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(spacing),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    for (idx in 1..3) {
      val active = idx <= stars
      Canvas(modifier = Modifier.size(singleSize)) {
        val pitch = dotSize.toPx() * pitchRatio
        val r = dotSize.toPx() * 0.46f
        val c = if (active) filledColor else emptyColor
        var i = 0
        while (i + 1 < starCoords.size) {
          drawCircle(
            color = c,
            radius = r,
            center = Offset(starCoords[i] * pitch + pitch * 0.5f, starCoords[i + 1] * pitch + pitch * 0.5f),
          )
          i += 2
        }
      }
    }
  }
}
