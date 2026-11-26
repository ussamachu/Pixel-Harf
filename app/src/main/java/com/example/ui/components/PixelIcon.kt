package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IconTint
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalPixelIconDotMode
import com.example.ui.theme.PixelHarfTheme
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

enum class PixelIcon {
  MAP,
  MODES,
  SETTINGS,
  SOUND_ON,
  SOUND_OFF,
  VOLUME,
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
  DAILY,
  CHECK,
  HEART,
  HEART_OUTLINE,
  STAR,
  STAR_OUTLINE,
  CLOSE,
  HOME,
  REPLAY,
  NEXT,
  TROPHY,
}

/**
 * Single registry of 12x12 pixel-art bitmaps ('#' = on).
 * Every bitmap preserves a 1-pixel safe margin around all edges (rows/cols 0 and 11 are empty)
 * and uses 1-logical-pixel strokes with notched corners so square and dot modes share identical geometry.
 */
object PixelIconBitmaps {
  private val soundOnGrid =
    arrayOf(
      "............",
      ".....#......",
      "....##..#...",
      "...#.#...#..",
      ".###.#.#..#.",
      ".#...#..#.#.",
      ".#...#..#.#.",
      ".###.#.#..#.",
      "...#.#...#..",
      "....##..#...",
      ".....#......",
      "............",
    )

  private val arrowLeftGrid =
    arrayOf(
      "............",
      "......#.....",
      ".....#......",
      "....#.......",
      "...#........",
      "..########..",
      "...#........",
      "....#.......",
      ".....#......",
      "......#.....",
      "............",
      "............",
    )

  private val grids: Map<PixelIcon, Array<String>> =
    mapOf(
      PixelIcon.MAP to
        arrayOf(
          "............",
          "..###..##...",
          ".#...##..#..",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          "..#..##...#.",
          "...##..###..",
          "............",
        ),
      PixelIcon.MODES to
        arrayOf(
          "............",
          "..##....##..",
          ".#..#..#..#.",
          ".#..#..#..#.",
          "..##....##..",
          "............",
          "............",
          "..##....##..",
          ".#..#..#..#.",
          ".#..#..#..#.",
          "..##....##..",
          "............",
        ),
      PixelIcon.SETTINGS to
        arrayOf(
          "............",
          ".....##.....",
          "..#.#..#.#..",
          "...#....#...",
          "..#.####.#..",
          ".#..#..#..#.",
          ".#..#..#..#.",
          "..#.####.#..",
          "...#....#...",
          "..#.#..#.#..",
          ".....##.....",
          "............",
        ),
      PixelIcon.SOUND_ON to soundOnGrid,
      PixelIcon.VOLUME to soundOnGrid,
      PixelIcon.SOUND_OFF to
        arrayOf(
          "............",
          ".....#......",
          "....##......",
          "...#.#......",
          ".###.#.#..#.",
          ".#...#..##..",
          ".#...#..##..",
          ".###.#.#..#.",
          "...#.#......",
          "....##......",
          ".....#......",
          "............",
        ),
      PixelIcon.PAUSE to
        arrayOf(
          "............",
          "............",
          "..###..###..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..###..###..",
          "............",
          "............",
        ),
      PixelIcon.PLAY to
        arrayOf(
          "............",
          "...#........",
          "...##.......",
          "...#.#......",
          "...#..#.....",
          "...#...##...",
          "...#...##...",
          "...#..#.....",
          "...#.#......",
          "...##.......",
          "...#........",
          "............",
        ),
      PixelIcon.ARROW_RIGHT to
        arrayOf(
          "............",
          ".....#......",
          "......#.....",
          ".......#....",
          "........#...",
          "..########..",
          "........#...",
          ".......#....",
          "......#.....",
          ".....#......",
          "............",
          "............",
        ),
      PixelIcon.ARROW_LEFT to arrowLeftGrid,
      PixelIcon.BACK to arrowLeftGrid,
      PixelIcon.LOCK to
        arrayOf(
          "............",
          "....####....",
          "...#....#...",
          "...#....#...",
          "...#....#...",
          "..########..",
          ".#........#.",
          ".#...##...#.",
          ".#...##...#.",
          ".#........#.",
          "..########..",
          "............",
        ),
      PixelIcon.SLOW_MO to
        arrayOf(
          "............",
          "..########..",
          "..#......#..",
          "...#....#...",
          "....#..#....",
          ".....##.....",
          ".....##.....",
          "....#..#....",
          "...#....#...",
          "..#......#..",
          "..########..",
          "............",
        ),
      PixelIcon.MAGNET to
        arrayOf(
          "............",
          "..##....##..",
          ".#..#..#..#.",
          ".###....###.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#..#..#..#.",
          ".#...##...#.",
          ".#........#.",
          "..#......#..",
          "...######...",
          "............",
        ),
      PixelIcon.GHOST to
        arrayOf(
          "............",
          "....####....",
          "...#....#...",
          "..#......#..",
          "..#.#..#.#..",
          "..#......#..",
          "..#......#..",
          "..#......#..",
          "..#......#..",
          "..#.#..#.#..",
          "...#.##.#...",
          "............",
        ),
      PixelIcon.SHRINK to
        arrayOf(
          "............",
          ".#........#.",
          "..#.#..#.#..",
          "...##..##...",
          "..###..###..",
          "............",
          "............",
          "..###..###..",
          "...##..##...",
          "..#.#..#.#..",
          ".#........#.",
          "............",
        ),
      PixelIcon.DAILY to
        arrayOf(
          "............",
          "...#....#...",
          "..########..",
          ".#........#.",
          ".##########.",
          ".#........#.",
          ".#...##...#.",
          ".#...##...#.",
          ".#........#.",
          ".#........#.",
          "..########..",
          "............",
        ),
      PixelIcon.CHECK to
        arrayOf(
          "............",
          "............",
          "............",
          "..........#.",
          ".........#..",
          "........#...",
          ".#.....#....",
          "..#...#.....",
          "...#.#......",
          "....#.......",
          "............",
          "............",
        ),
      PixelIcon.HEART to
        arrayOf(
          "............",
          "............",
          "..##....##..",
          ".####..####.",
          ".##########.",
          ".##########.",
          ".##########.",
          "..########..",
          "...######...",
          "....####....",
          ".....##.....",
          "............",
        ),
      PixelIcon.HEART_OUTLINE to
        arrayOf(
          "............",
          "............",
          "..##....##..",
          ".#..#..#..#.",
          ".#...##...#.",
          ".#........#.",
          ".#........#.",
          "..#......#..",
          "...#....#...",
          "....#..#....",
          ".....##.....",
          "............",
        ),
      PixelIcon.STAR to
        arrayOf(
          "............",
          ".....##.....",
          "....####....",
          "....####....",
          ".##########.",
          ".##########.",
          "..########..",
          "...######...",
          "..###..###..",
          "..##....##..",
          "............",
          "............",
        ),
      PixelIcon.STAR_OUTLINE to
        arrayOf(
          "............",
          ".....##.....",
          "....#..#....",
          "....#..#....",
          ".###....###.",
          ".#........#.",
          "..#......#..",
          "...#....#...",
          "..#..##..#..",
          "..##....##..",
          "............",
          "............",
        ),
      PixelIcon.CLOSE to
        arrayOf(
          "............",
          "............",
          "..#......#..",
          "...#....#...",
          "....#..#....",
          ".....##.....",
          ".....##.....",
          "....#..#....",
          "...#....#...",
          "..#......#..",
          "............",
          "............",
        ),
      PixelIcon.HOME to
        arrayOf(
          "............",
          ".....##.....",
          "....#..#....",
          "...#....#...",
          "..#......#..",
          ".#........#.",
          "..#......#..",
          "..#..##..#..",
          "..#.#..#.#..",
          "..#.#..#.#..",
          "..###..###..",
          "............",
        ),
      PixelIcon.REPLAY to
        arrayOf(
          "............",
          "....####....",
          "...#....#...",
          "..#......#..",
          ".#......#.#.",
          ".#.......#..",
          ".#..........",
          ".#..........",
          "..#......#..",
          "...#....#...",
          "....####....",
          "............",
        ),
      PixelIcon.NEXT to
        arrayOf(
          "............",
          "............",
          "..#......#..",
          "..##.....#..",
          "..#.#....#..",
          "..#..#...#..",
          "..#..#...#..",
          "..#.#....#..",
          "..##.....#..",
          "..#......#..",
          "............",
          "............",
        ),
      PixelIcon.TROPHY to
        arrayOf(
          "............",
          "...######...",
          "..##....##..",
          ".#.#....#.#.",
          "..##....##..",
          "...#....#...",
          "....#..#....",
          ".....##.....",
          ".....##.....",
          "....####....",
          "...######...",
          "............",
        ),
    )

  private val packed: Map<PixelIcon, IntArray> =
    grids.mapValues { (_, rows) ->
      val list = ArrayList<Int>(64)
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

  fun coords(type: PixelIcon): IntArray = packed[type] ?: IntArray(0)
}

@Composable
private fun resolvePixelIconColor(
  type: PixelIcon,
  tint: IconTint,
  isActive: Boolean,
  isSuccess: Boolean,
  enabled: Boolean,
): Color {
  val colors = LocalPalette.current.iconColors
  if (!enabled) {
    return when (tint) {
      IconTint.Auto -> colors.disabledInk
      is IconTint.Color -> tint.color.copy(alpha = 0.38f)
    }
  }
  if (tint is IconTint.Color) {
    return tint.color
  }
  return when (type) {
    PixelIcon.HEART -> colors.heartFilled
    PixelIcon.HEART_OUTLINE -> colors.heartOutline
    PixelIcon.STAR -> colors.starFilled
    PixelIcon.STAR_OUTLINE -> colors.starOutline
    PixelIcon.LOCK -> colors.mutedInk
    PixelIcon.CHECK ->
      when {
        isSuccess -> colors.successGreen
        isActive -> colors.activeOrange
        else -> colors.defaultInk
      }
    PixelIcon.SLOW_MO -> if (isActive) colors.powerSlowMo else colors.defaultInk
    PixelIcon.MAGNET -> if (isActive) colors.powerMagnet else colors.defaultInk
    PixelIcon.GHOST -> if (isActive) colors.powerGhost else colors.defaultInk
    PixelIcon.SHRINK -> if (isActive) colors.powerShrink else colors.defaultInk
    else -> if (isActive) colors.activeOrange else colors.defaultInk
  }
}

/**
 * Unified 12x12 pixel-art icon rendered from cached integer coordinates with zero draw allocations
 * and whole-device-pixel snapping. Allowed sizes: 16.dp (inline), 24.dp (default), 32.dp (large).
 */
@Composable
fun PixelIcon(
  type: PixelIcon,
  size: Dp = 24.dp,
  tint: IconTint = IconTint.Auto,
  contentDescription: String? = null,
  modifier: Modifier = Modifier,
  dotMode: Boolean = LocalPixelIconDotMode.current,
  isActive: Boolean = false,
  isSuccess: Boolean = false,
  enabled: Boolean = true,
  mirrorForRtl: Boolean = false,
) {
  val layoutDir = LocalLayoutDirection.current
  val mirror = mirrorForRtl && layoutDir == LayoutDirection.Rtl
  val coords = remember(type) { PixelIconBitmaps.coords(type) }
  val resolvedColor =
    resolvePixelIconColor(
      type = type,
      tint = tint,
      isActive = isActive,
      isSuccess = isSuccess,
      enabled = enabled,
    )

  Canvas(
    modifier =
      modifier
        .size(size)
        .let { m ->
          if (contentDescription != null) m.semantics { this.contentDescription = contentDescription }
          else m
        }
  ) {
    val unitPx = floor(min(this.size.width, this.size.height) / 12f).coerceAtLeast(1f)
    val gridPx = unitPx * 12f
    val originX = round((this.size.width - gridPx) * 0.5f)
    val originY = round((this.size.height - gridPx) * 0.5f)
    val radius = (unitPx * 0.44f).coerceAtLeast(0.5f)
    val halfUnit = unitPx * 0.5f

    var i = 0
    while (i + 1 < coords.size) {
      val rawX = coords[i]
      val gx = if (mirror) 11 - rawX else rawX
      val gy = coords[i + 1]
      if (dotMode) {
        drawCircle(
          color = resolvedColor,
          radius = radius,
          center = Offset(originX + gx * unitPx + halfUnit, originY + gy * unitPx + halfUnit),
        )
      } else {
        drawRect(
          color = resolvedColor,
          topLeft = Offset(originX + gx * unitPx, originY + gy * unitPx),
          size = Size(unitPx, unitPx),
        )
      }
      i += 2
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconGallerySection(
  title: String,
  darkVariant: Boolean,
  dotMode: Boolean,
) {
  PixelHarfTheme(darkVariant = darkVariant, useDotPixels = dotMode) {
    val palette = LocalPalette.current
    Column(
      modifier = Modifier.fillMaxWidth().background(palette.canvas).padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        text = title,
        fontFamily = JetBrainsMonoFamily,
        fontSize = 11.sp,
        color = palette.ink,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        PixelIcon.entries.forEach { icon ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            PixelIcon(
              type = icon,
              size = 24.dp,
              isActive =
                icon == PixelIcon.SLOW_MO ||
                  icon == PixelIcon.MAGNET ||
                  icon == PixelIcon.GHOST ||
                  icon == PixelIcon.SHRINK,
              isSuccess = icon == PixelIcon.CHECK,
              contentDescription = icon.name,
            )
            Text(
              text = icon.name.take(6),
              fontFamily = JetBrainsMonoFamily,
              fontSize = 8.sp,
              color = palette.mutedInk,
            )
          }
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 1100)
@Composable
private fun PixelIconGalleryPreview() {
  Column {
    IconGallerySection(title = "LIGHT / SQUARE", darkVariant = false, dotMode = false)
    IconGallerySection(title = "LIGHT / DOT", darkVariant = false, dotMode = true)
    IconGallerySection(title = "DARK / SQUARE", darkVariant = true, dotMode = false)
    IconGallerySection(title = "DARK / DOT", darkVariant = true, dotMode = true)
  }
}
