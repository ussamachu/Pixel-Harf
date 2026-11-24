package com.example.raster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.example.model.ArabicFontSpec
import com.example.model.GridPoint
import kotlin.math.max
import kotlin.math.min

data class RasterizedLetter(
  val cols: Int,
  val rows: Int,
  val baselineRow: Int,
  val letterPixels: List<GridPoint>,
  val dotPixels: Set<GridPoint>,
  val baselinePixels: List<GridPoint>,
)

class LetterRasterizer(private val context: Context) {

  private val cache = mutableMapOf<String, RasterizedLetter>()
  private val typefaceCache = mutableMapOf<Int, Typeface>()

  fun rasterize(
    glyph: String,
    fontSpec: ArabicFontSpec,
    cols: Int,
    rows: Int,
  ): RasterizedLetter {
    val cacheKey = "${glyph}|${fontSpec.id}|${cols}x${rows}"
    cache[cacheKey]?.let { return it }

    val typeface = getTypeface(fontSpec.fontResId)
    val result = renderToGrid(glyph, typeface, cols, rows)
    cache[cacheKey] = result
    return result
  }

  private fun getTypeface(fontResId: Int): Typeface {
    typefaceCache[fontResId]?.let { return it }
    val loaded = ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT_BOLD
    typefaceCache[fontResId] = loaded
    return loaded
  }

  private fun renderToGrid(
    glyph: String,
    typeface: Typeface,
    cols: Int,
    rows: Int,
  ): RasterizedLetter {
    // Render glyph into an intermediate bitmap (28-40px tall) for crisp pixel quantization
    val bmpSize = (max(cols, rows) * 2).coerceIn(28, 56)
    val bitmap = Bitmap.createBitmap(bmpSize, bmpSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint =
      Paint().apply {
        isAntiAlias = false
        isSubpixelText = false
        color = Color.BLACK
        this.typeface = typeface
        textAlign = Paint.Align.CENTER
        textSize = bmpSize * 0.72f
      }

    val bounds = Rect()
    paint.getTextBounds(glyph, 0, glyph.length, bounds)

    // Scale down if glyph is wider/taller than target inner margin
    val maxDrawW = bmpSize * 0.80f
    val maxDrawH = bmpSize * 0.72f
    if (bounds.width() > maxDrawW || bounds.height() > maxDrawH) {
      val ratioW = if (bounds.width() > 0) maxDrawW / bounds.width() else 1f
      val ratioH = if (bounds.height() > 0) maxDrawH / bounds.height() else 1f
      paint.textSize = paint.textSize * min(ratioW, ratioH)
      paint.getTextBounds(glyph, 0, glyph.length, bounds)
    }

    val baselineY = bmpSize * 0.68f
    val centerX = bmpSize * 0.5f - bounds.exactCenterX()
    // Draw centered horizontally using bounds offset, positioned on baselineY
    paint.textAlign = Paint.Align.LEFT
    canvas.drawText(glyph, centerX, baselineY, paint)

    // Threshold alpha into active cells
    val rawPoints = mutableListOf<GridPoint>()
    var minX = bmpSize
    var maxX = -1
    var minY = bmpSize
    var maxY = -1

    for (y in 0 until bmpSize) {
      for (x in 0 until bmpSize) {
        val alpha = Color.alpha(bitmap.getPixel(x, y))
        if (alpha >= 85) {
          rawPoints.add(GridPoint(x, y))
          if (x < minX) minX = x
          if (x > maxX) maxX = x
          if (y < minY) minY = y
          if (y > maxY) maxY = y
        }
      }
    }
    bitmap.recycle()

    // Fallback if glyph was unexpectedly empty
    if (rawPoints.isEmpty()) {
      val cx = cols / 2
      val fallback =
        (3 until rows - 3).map { y -> GridPoint(cx, y) }
      val baselineRow = rows - 2
      val baseline = (2 until cols - 2).map { x -> GridPoint(x, baselineRow) }
      return RasterizedLetter(
        cols = cols,
        rows = rows,
        baselineRow = baselineRow,
        letterPixels = fallback,
        dotPixels = emptySet(),
        baselinePixels = baseline,
      )
    }

    // Map raw pixels into playable inner region of (cols x rows), leaving margin for snake movement
    val marginX = 2
    val marginTop = 2
    val baselineRow = rows - 2
    val usableW = (cols - marginX * 2).coerceAtLeast(6)
    val usableH = (rows - marginTop - 3).coerceAtLeast(6)

    val srcW = (maxX - minX + 1).coerceAtLeast(1)
    val srcH = (maxY - minY + 1).coerceAtLeast(1)

    // Preserve aspect ratio when fitting into usable grid
    val scale = min(usableW.toFloat() / srcW.toFloat(), usableH.toFloat() / srcH.toFloat())
    val targetW = (srcW * scale).coerceAtLeast(1f)
    val targetH = (srcH * scale).coerceAtLeast(1f)

    val offsetX = marginX + ((usableW - targetW) / 2f)
    // Align baseline proportionally so glyphs with descenders sit cleanly across the baseline
    val baselineNormalized = ((baselineY - minY) * scale)
    val desiredBaselineRow = (marginTop + usableH - 1).toFloat()
    val shiftY =
      if (baselineNormalized > desiredBaselineRow) {
        marginTop.toFloat()
      } else {
        (marginTop + (usableH - targetH) * 0.45f)
      }

    val occupied = Array(rows) { BooleanArray(cols) }
    val mapped = linkedSetOf<GridPoint>()

    // Sample each target cell from source bitmap coordinates to prevent holes or double scaling
    for (gy in marginTop until (rows - 2)) {
      for (gx in marginX until (cols - marginX)) {
        val sx = ((gx - offsetX) / scale + minX).toInt()
        val sy = ((gy - shiftY) / scale + minY).toInt()
        if (sx in minX..maxX && sy in minY..maxY) {
          // Check if any raw pixel fell near (sx, sy)
          val hit = rawPoints.any { kotlin.math.abs(it.x - sx) <= 0 && kotlin.math.abs(it.y - sy) <= 0 }
          if (hit) {
            occupied[gy][gx] = true
            mapped.add(GridPoint(gx, gy))
          }
        }
      }
    }

    // Guarantee thin strokes survive sampling by also projecting rawPoints directly
    if (mapped.size < 6) {
      rawPoints.forEach { p ->
        val gx = (offsetX + (p.x - minX) * scale).toInt().coerceIn(marginX, cols - marginX - 1)
        val gy = (shiftY + (p.y - minY) * scale).toInt().coerceIn(marginTop, rows - 3)
        occupied[gy][gx] = true
        mapped.add(GridPoint(gx, gy))
      }
    }

    val dotPixels = detectDotComponents(occupied, cols, rows)
    val baselinePixels =
      (2 until (cols - 2)).map { x -> GridPoint(x, baselineRow) }
        .filterNot { occupied[it.y][it.x] }

    return RasterizedLetter(
      cols = cols,
      rows = rows,
      baselineRow = baselineRow,
      letterPixels = mapped.toList(),
      dotPixels = dotPixels,
      baselinePixels = baselinePixels,
    )
  }

  /**
   * Finds connected components using 8-way adjacency. The largest component is the main letter
   * body; small detached clusters (dots / hamza) become bonus dot targets.
   */
  private fun detectDotComponents(
    occupied: Array<BooleanArray>,
    cols: Int,
    rows: Int,
  ): Set<GridPoint> {
    val visited = Array(rows) { BooleanArray(cols) }
    val components = mutableListOf<List<GridPoint>>()
    val queue = ArrayDeque<GridPoint>()

    for (y in 0 until rows) {
      for (x in 0 until cols) {
        if (occupied[y][x] && !visited[y][x]) {
          val cluster = mutableListOf<GridPoint>()
          visited[y][x] = true
          queue.addLast(GridPoint(x, y))
          while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            cluster.add(cur)
            for (dy in -1..1) {
              for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = cur.x + dx
                val ny = cur.y + dy
                if (nx in 0 until cols && ny in 0 until rows && occupied[ny][nx] && !visited[ny][nx]) {
                  visited[ny][nx] = true
                  queue.addLast(GridPoint(nx, ny))
                }
              }
            }
          }
          components.add(cluster)
        }
      }
    }

    if (components.size <= 1) return emptySet()
    val maxSize = components.maxOf { it.size }
    val dots = mutableSetOf<GridPoint>()
    for (cluster in components) {
      if (cluster.size < maxSize && cluster.size <= (maxSize * 0.35f).coerceAtLeast(6f)) {
        dots.addAll(cluster)
      }
    }
    return dots
  }
}
