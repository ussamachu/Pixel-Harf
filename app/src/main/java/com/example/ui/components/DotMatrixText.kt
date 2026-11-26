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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.example.model.ArabicFontSpec
import com.example.raster.LetterRasterizer
import com.example.raster.RasterizedLetter
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import kotlin.math.max
import kotlin.math.min

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

private object SharedGlyphRasterCache {
  private var rasterizer: LetterRasterizer? = null
  private val packedCache = LruCache<String, PackedArabicGlyph>(64)

  class PackedArabicGlyph(
    val cols: Int,
    val rows: Int,
    val bodyCoords: IntArray,
    val dotCoords: IntArray,
  )

  fun get(context: Context, glyph: String, fontSpec: ArabicFontSpec): PackedArabicGlyph {
    val key = "$glyph|${fontSpec.id}"
    packedCache.get(key)?.let { return it }

    val r = rasterizer ?: LetterRasterizer(context.applicationContext).also { rasterizer = it }
    val rasterized: RasterizedLetter = r.rasterize(glyph, fontSpec, cols = 16, rows = 16)

    var minX = rasterized.cols
    var maxX = -1
    var minY = rasterized.rows
    var maxY = -1
    for (p in rasterized.letterPixels) {
      if (p.x < minX) minX = p.x
      if (p.x > maxX) maxX = p.x
      if (p.y < minY) minY = p.y
      if (p.y > maxY) maxY = p.y
    }

    val packed =
      if (maxX < minX || maxY < minY) {
        PackedArabicGlyph(6, 8, IntArray(0), IntArray(0))
      } else {
        val body = ArrayList<Int>(rasterized.letterPixels.size * 2)
        val dots = ArrayList<Int>(rasterized.dotPixels.size * 2)
        for (p in rasterized.letterPixels) {
          val cx = p.x - minX
          val cy = p.y - minY
          if (rasterized.dotPixels.contains(p)) {
            dots.add(cx)
            dots.add(cy)
          } else {
            body.add(cx)
            body.add(cy)
          }
        }
        PackedArabicGlyph(
          cols = (maxX - minX + 1).coerceAtLeast(1),
          rows = (maxY - minY + 1).coerceAtLeast(1),
          bodyCoords = body.toIntArray(),
          dotCoords = dots.toIntArray(),
        )
      }
    packedCache.put(key, packed)
    return packed
  }
}

/**
 * Arabic dot-matrix lettering composable backed by cached rasterized glyphs.
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
      scanAnim.animateTo(1f, animationSpec = tween(durationMillis = 260))
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

/**
 * Hero Arabic letter rendered through the existing LetterRasterizer as crisp round dots,
 * with diacritic dots highlighted in accent orange and row-by-row scan-in animation.
 */
@Composable
fun DotGlyphHero(
  glyph: String,
  fontSpec: ArabicFontSpec,
  contentDescription: String,
  modifier: Modifier = Modifier,
  boxSize: Dp = 52.dp,
  color: Color = LocalPalette.current.ink,
  dotAccentColor: Color = LocalPalette.current.accent,
  animateScanIn: Boolean = true,
) {
  val context = LocalContext.current
  val reduceMotion = LocalReduceMotion.current

  val packed =
    remember(glyph, fontSpec.id) {
      SharedGlyphRasterCache.get(context, glyph, fontSpec)
    }

  val scanAnim = remember(glyph, fontSpec.id, animateScanIn, reduceMotion) {
    Animatable(if (animateScanIn && !reduceMotion) 0f else 1f)
  }

  LaunchedEffect(glyph, fontSpec.id, animateScanIn, reduceMotion) {
    if (animateScanIn && !reduceMotion) {
      scanAnim.snapTo(0f)
      scanAnim.animateTo(1f, animationSpec = tween(durationMillis = 320))
    } else {
      scanAnim.snapTo(1f)
    }
  }

  Canvas(
    modifier = modifier.size(boxSize).semantics { this.contentDescription = contentDescription }
  ) {
    val pitch = min(size.width / packed.cols.coerceAtLeast(1), size.height / packed.rows.coerceAtLeast(1))
    val radius = (pitch * 0.40f).coerceAtLeast(1.2f)
    val originX = (size.width - packed.cols * pitch) * 0.5f
    val originY = (size.height - packed.rows * pitch) * 0.5f
    val visibleMaxRow = (packed.rows * scanAnim.value).toInt() + 1

    val body = packed.bodyCoords
    var i = 0
    while (i + 1 < body.size) {
      val gx = body[i]
      val gy = body[i + 1]
      if (!animateScanIn || gy <= visibleMaxRow) {
        drawCircle(
          color = color,
          radius = radius,
          center = Offset(originX + gx * pitch + pitch * 0.5f, originY + gy * pitch + pitch * 0.5f),
        )
      }
      i += 2
    }

    val dots = packed.dotCoords
    var j = 0
    while (j + 1 < dots.size) {
      val gx = dots[j]
      val gy = dots[j + 1]
      if (!animateScanIn || gy <= visibleMaxRow) {
        drawCircle(
          color = dotAccentColor,
          radius = radius * 1.08f,
          center = Offset(originX + gx * pitch + pitch * 0.5f, originY + gy * pitch + pitch * 0.5f),
        )
      }
      j += 2
    }
  }
}

private object DotDigitBitmaps {
  private val glyphs: Map<Char, Array<String>> =
    mapOf(
      '0' to arrayOf(".###.", "#...#", "#..##", "#.#.#", "##..#", "#...#", ".###."),
      '1' to arrayOf("..#..", ".##..", "..#..", "..#..", "..#..", "..#..", ".###."),
      '2' to arrayOf(".###.", "#...#", "....#", "..##.", ".#...", "#....", "#####"),
      '3' to arrayOf(".###.", "#...#", "....#", "..##.", "....#", "#...#", ".###."),
      '4' to arrayOf("...#.", "..##.", ".#.#.", "#..#.", "#####", "...#.", "...#."),
      '5' to arrayOf("#####", "#....", "####.", "....#", "....#", "#...#", ".###."),
      '6' to arrayOf(".###.", "#....", "#....", "####.", "#...#", "#...#", ".###."),
      '7' to arrayOf("#####", "....#", "...#.", "..#..", ".#...", ".#...", ".#..."),
      '8' to arrayOf(".###.", "#...#", "#...#", ".###.", "#...#", "#...#", ".###."),
      '9' to arrayOf(".###.", "#...#", "#...#", ".####", "....#", "....#", ".###."),
      ':' to arrayOf(".....", "..#..", "..#..", ".....", "..#..", "..#..", "....."),
      '/' to arrayOf("....#", "...#.", "...#.", "..#..", ".#...", ".#...", "#...."),
      '+' to arrayOf(".....", "..#..", "..#..", "#####", "..#..", "..#..", "....."),
      '-' to arrayOf(".....", ".....", ".....", "#####", ".....", ".....", "....."),
      '%' to arrayOf("##..#", "##.#.", "..#..", "..#..", ".#...", "#.##.", "#..##"),
      '.' to arrayOf(".....", ".....", ".....", ".....", ".....", "..#..", "..#.."),
      'x' to arrayOf(".....", ".....", "#...#", ".#.#.", "..#..", ".#.#.", "#...#"),
      ' ' to arrayOf(".....", ".....", ".....", ".....", ".....", ".....", "....."),
    )

  private val narrowChars = setOf(':', '.', ' ')

  private val packed: Map<Char, IntArray> =
    glyphs.mapValues { (_, rows) ->
      val list = ArrayList<Int>(24)
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

  fun coords(ch: Char): IntArray = packed[ch] ?: packed[' ']!!

  fun colsFor(ch: Char): Int = if (ch in narrowChars) 3 else 5
}

/**
 * Crisp 5x7 round-dot Latin digit renderer with zero draw allocations, optional row scan-in,
 * and per-digit flip transition when numeric values update. Always renders LTR.
 */
@Composable
fun DotDigitText(
  text: String,
  modifier: Modifier = Modifier,
  dotSize: Dp = 3.8.dp,
  color: Color = LocalPalette.current.ink,
  animateScanIn: Boolean = true,
  animateDigitFlip: Boolean = true,
  contentDescription: String? = text,
) {
  val reduceMotion = LocalReduceMotion.current
  val scanAnim = remember(animateScanIn, reduceMotion) {
    Animatable(if (animateScanIn && !reduceMotion) 0f else 1f)
  }

  LaunchedEffect(animateScanIn, reduceMotion) {
    if (animateScanIn && !reduceMotion) {
      scanAnim.snapTo(0f)
      scanAnim.animateTo(1f, animationSpec = tween(durationMillis = 280))
    } else {
      scanAnim.snapTo(1f)
    }
  }

  val pitchRatio = 1.40f
  val charGap = dotSize * 1.15f

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    Row(
      modifier =
        modifier.let { m ->
          if (contentDescription != null) m.semantics { this.contentDescription = contentDescription }
          else m
        },
      horizontalArrangement = Arrangement.spacedBy(charGap),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      text.forEachIndexed { idx, ch ->
        if (animateDigitFlip && !reduceMotion) {
          AnimatedContent(
            targetState = ch,
            transitionSpec = {
              (slideInVertically(tween(150)) { -it / 2 } + fadeIn(tween(150))) togetherWith
                (slideOutVertically(tween(110)) { it / 2 } + fadeOut(tween(110)))
            },
            label = "dot_digit_$idx",
          ) { digit ->
            DotDigitGlyphCanvas(
              ch = digit,
              dotSize = dotSize,
              pitchRatio = pitchRatio,
              color = color,
              visibleMaxRow = if (animateScanIn) (7f * scanAnim.value).toInt() + 1 else 7,
            )
          }
        } else {
          DotDigitGlyphCanvas(
            ch = ch,
            dotSize = dotSize,
            pitchRatio = pitchRatio,
            color = color,
            visibleMaxRow = if (animateScanIn && !reduceMotion) (7f * scanAnim.value).toInt() + 1 else 7,
          )
        }
      }
    }
  }
}

@Composable
private fun DotDigitGlyphCanvas(
  ch: Char,
  dotSize: Dp,
  pitchRatio: Float,
  color: Color,
  visibleMaxRow: Int,
) {
  val coords = remember(ch) { DotDigitBitmaps.coords(ch) }
  val cols = remember(ch) { DotDigitBitmaps.colsFor(ch) }
  val offsetX = if (cols == 3) 1 else 0
  val widthDp = dotSize * (cols * pitchRatio)
  val heightDp = dotSize * (7 * pitchRatio)

  Canvas(modifier = Modifier.size(width = widthDp, height = heightDp)) {
    val pitch = dotSize.toPx() * pitchRatio
    val radius = (dotSize.toPx() * 0.48f).coerceAtLeast(0.8f)
    var i = 0
    while (i + 1 < coords.size) {
      val gx = coords[i] - offsetX
      val gy = coords[i + 1]
      if (gy <= visibleMaxRow && gx in 0 until cols) {
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
