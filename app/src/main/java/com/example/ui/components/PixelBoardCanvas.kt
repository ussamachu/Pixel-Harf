package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.model.Direction
import com.example.model.GameState
import com.example.model.GridPoint
import com.example.model.PowerUpType
import com.example.ui.theme.GrainDot
import com.example.ui.theme.LocalBoardPalette
import kotlin.math.min
import kotlin.random.Random

/**
 * High-performance 60fps Canvas board with zero object allocations inside DrawScope.
 */
@Composable
fun PixelBoardCanvas(
  gameState: GameState,
  modifier: Modifier = Modifier,
) {
  val palette = LocalBoardPalette.current

  // Deterministic paper-grain texture points pre-allocated once
  val grainPoints = remember {
    val rnd = Random(4242)
    FloatArray(240) { rnd.nextFloat() }
  }

  // Reusable Path for snake head directional arrow (zero allocations in onDraw)
  val arrowPath = remember { Path() }

  val pulseTransition = rememberInfiniteTransition(label = "board_pulse")
  val pulse by
    pulseTransition.animateFloat(
      initialValue = 0.45f,
      targetValue = 1.0f,
      animationSpec =
        infiniteRepeatable(animation = tween(650), repeatMode = RepeatMode.Reverse),
      label = "pulse_alpha",
    )

  val shakeStrength = (gameState.screenShakeMs / 280f).coerceIn(0f, 1f)
  val shakeX =
    if (shakeStrength > 0f) {
      ((gameState.screenShakeMs % 37L) - 18L) * 0.45f * shakeStrength
    } else 0f
  val shakeY =
    if (shakeStrength > 0f) {
      ((gameState.screenShakeMs % 29L) - 14L) * 0.45f * shakeStrength
    } else 0f

  val cols = gameState.gridCols.coerceAtLeast(8)
  val rows = gameState.gridRows.coerceAtLeast(8)
  val aspect = cols.toFloat() / rows.toFloat()

  Canvas(
    modifier =
      modifier
        .fillMaxWidth()
        .aspectRatio(aspect)
        .graphicsLayer {
          translationX = shakeX
          translationY = shakeY
        }
        .clip(RoundedCornerShape(22.dp))
  ) {
    val boardW = size.width
    val boardH = size.height
    val cornerPx = 22.dp.toPx()

    // 1. Soft light-gray paper card background
    drawRoundRect(
      color = palette.cardBackground,
      topLeft = Offset.Zero,
      size = size,
      cornerRadius = CornerRadius(cornerPx, cornerPx),
    )

    // Subtle inner shadow + border
    drawRoundRect(
      color = palette.cardInnerShadow,
      topLeft = Offset(2f, 2f),
      size = Size(boardW - 4f, boardH - 4f),
      cornerRadius = CornerRadius(cornerPx - 1f, cornerPx - 1f),
      style = Stroke(width = 6f),
    )
    drawRoundRect(
      color = palette.cardBorder,
      topLeft = Offset(1f, 1f),
      size = Size(boardW - 2f, boardH - 2f),
      cornerRadius = CornerRadius(cornerPx, cornerPx),
      style = Stroke(width = 2.5f),
    )

    // Subtle paper grain dots (120 pre-computed pairs)
    var gi = 0
    while (gi + 1 < grainPoints.size) {
      val gx = grainPoints[gi] * boardW
      val gy = grainPoints[gi + 1] * boardH
      drawRect(
        color = GrainDot,
        topLeft = Offset(gx, gy),
        size = Size(2.2f, 2.2f),
      )
      gi += 2
    }

    val pad = boardW * 0.045f
    val innerW = boardW - pad * 2f
    val innerH = boardH - pad * 2f
    val cell = min(innerW / cols.toFloat(), innerH / rows.toFloat())
    val originX = (boardW - cell * cols) * 0.5f
    val originY = (boardH - cell * rows) * 0.5f
    val gap = cell * 0.14f
    val pxSize = (cell - gap).coerceAtLeast(1f)
    val halfGap = gap * 0.5f

    // 2. Baseline row under the Arabic letter
    val baseline = gameState.baselinePixels
    for (i in 0 until baseline.size) {
      val p = baseline[i]
      drawRect(
        color = palette.baselinePixel,
        topLeft = Offset(originX + p.x * cell + halfGap, originY + p.y * cell + halfGap),
        size = Size(pxSize, pxSize),
      )
    }

    // 3. Remaining letter pixels & dot bonus targets
    val remaining = gameState.remainingPixels
    val dots = gameState.dotPixels
    for (i in 0 until remaining.size) {
      val p = remaining[i]
      val left = originX + p.x * cell + halfGap
      val top = originY + p.y * cell + halfGap
      val isDot = dots.contains(p)
      drawRect(
        color = if (isDot) palette.dotBonusPixel else palette.letterPixel,
        topLeft = Offset(left, top),
        size = Size(pxSize, pxSize),
      )
      if (isDot) {
        val innerPad = pxSize * 0.28f
        drawRect(
          color = palette.accent,
          topLeft = Offset(left + innerPad, top + innerPad),
          size = Size(pxSize - innerPad * 2f, pxSize - innerPad * 2f),
        )
      }
    }

    // 4. Kids mode next-pixel hint outline
    val hint = gameState.hintPixel
    if (hint != null) {
      val left = originX + hint.x * cell + halfGap
      val top = originY + hint.y * cell + halfGap
      drawRect(
        color = palette.hintOutline.copy(alpha = pulse),
        topLeft = Offset(left - 2f, top - 2f),
        size = Size(pxSize + 4f, pxSize + 4f),
        style = Stroke(width = 3f),
      )
    }

    // 5. Power-up pixel
    val powerUp = gameState.activePowerUp
    if (powerUp != null) {
      val left = originX + powerUp.point.x * cell + halfGap
      val top = originY + powerUp.point.y * cell + halfGap
      drawRect(
        color = palette.powerUpPixel.copy(alpha = 0.25f + 0.75f * pulse),
        topLeft = Offset(left, top),
        size = Size(pxSize, pxSize),
      )
      val inset = pxSize * 0.25f
      drawRect(
        color = Color.White,
        topLeft = Offset(left + inset, top + inset),
        size = Size(pxSize - inset * 2f, pxSize - inset * 2f),
      )
    }

    // 6. World 4 moving ghost pixel
    val ghost = gameState.ghostPixel
    if (ghost != null) {
      val left = originX + ghost.point.x * cell + halfGap
      val top = originY + ghost.point.y * cell + halfGap
      val ratio = (ghost.remainingMs.toFloat() / ghost.totalMs.toFloat()).coerceIn(0.2f, 1f)
      drawRect(
        color = palette.ghostPixel.copy(alpha = 0.35f + 0.65f * pulse * ratio),
        topLeft = Offset(left, top),
        size = Size(pxSize, pxSize),
      )
      drawRect(
        color = palette.ghostPixel,
        topLeft = Offset(left - 2f, top - 2f),
        size = Size(pxSize + 4f, pxSize + 4f),
        style = Stroke(width = 2.2f),
      )
    }

    // 7. Snake tail (index 1..last)
    val snake = gameState.snake
    val isGhostMode = gameState.activeEffect == PowerUpType.GHOST
    val tailAlpha = if (isGhostMode) 0.45f else 0.88f
    for (i in snake.size - 1 downTo 1) {
      val seg = snake[i]
      val shrink = if (i == snake.lastIndex && snake.size > 2) pxSize * 0.14f else pxSize * 0.06f
      drawRect(
        color = palette.snakeTail.copy(alpha = tailAlpha),
        topLeft = Offset(originX + seg.x * cell + halfGap + shrink, originY + seg.y * cell + halfGap + shrink),
        size = Size(pxSize - shrink * 2f, pxSize - shrink * 2f),
      )
    }

    // 8. Snake head (index 0) + direction arrow
    if (snake.isNotEmpty()) {
      val head = snake[0]
      val left = originX + head.x * cell + halfGap
      val top = originY + head.y * cell + halfGap
      drawRect(
        color = palette.snakeHead,
        topLeft = Offset(left, top),
        size = Size(pxSize, pxSize),
      )

      arrowPath.reset()
      val cx = left + pxSize * 0.5f
      val cy = top + pxSize * 0.5f
      val r = pxSize * 0.28f
      when (gameState.direction) {
        Direction.RIGHT -> {
          arrowPath.moveTo(cx + r, cy)
          arrowPath.lineTo(cx - r * 0.75f, cy - r * 0.85f)
          arrowPath.lineTo(cx - r * 0.75f, cy + r * 0.85f)
        }
        Direction.LEFT -> {
          arrowPath.moveTo(cx - r, cy)
          arrowPath.lineTo(cx + r * 0.75f, cy - r * 0.85f)
          arrowPath.lineTo(cx + r * 0.75f, cy + r * 0.85f)
        }
        Direction.UP -> {
          arrowPath.moveTo(cx, cy - r)
          arrowPath.lineTo(cx - r * 0.85f, cy + r * 0.75f)
          arrowPath.lineTo(cx + r * 0.85f, cy + r * 0.75f)
        }
        Direction.DOWN -> {
          arrowPath.moveTo(cx, cy + r)
          arrowPath.lineTo(cx - r * 0.85f, cy - r * 0.75f)
          arrowPath.lineTo(cx + r * 0.85f, cy - r * 0.75f)
        }
      }
      arrowPath.close()
      drawPath(path = arrowPath, color = palette.snakeHeadArrow)
    }

    // 9. Pixel-pop & confetti particles
    val particles = gameState.particles
    for (i in 0 until particles.size) {
      val pt = particles[i]
      val alpha = (pt.life / pt.maxLife).coerceIn(0f, 1f)
      val pColor =
        when (pt.colorKind) {
          1 -> palette.snakeHead.copy(alpha = alpha)
          2 -> palette.confettiAlt.copy(alpha = alpha)
          3 -> palette.powerUpPixel.copy(alpha = alpha)
          else -> palette.letterPixel.copy(alpha = alpha)
        }
      val pSize = (pxSize * pt.sizeFactor).coerceAtLeast(2f)
      drawRect(
        color = pColor,
        topLeft = Offset(originX + pt.x * cell - pSize * 0.5f, originY + pt.y * cell - pSize * 0.5f),
        size = Size(pSize, pSize),
      )
    }
  }
}

/**
 * Lightweight static pixel-art letter preview card used on Home screen.
 */
@Composable
fun PixelLetterPreviewCanvas(
  letterPixels: List<GridPoint>,
  baselinePixels: List<GridPoint>,
  cols: Int,
  rows: Int,
  modifier: Modifier = Modifier,
) {
  val palette = LocalBoardPalette.current
  val snakePreview = remember(cols, rows) { listOf(GridPoint(cols - 3, 2), GridPoint(cols - 2, 2)) }

  Canvas(modifier = modifier.clip(RoundedCornerShape(18.dp))) {
    val w = size.width
    val h = size.height
    val corner = 18.dp.toPx()

    drawRoundRect(
      color = palette.cardBackground,
      topLeft = Offset.Zero,
      size = size,
      cornerRadius = CornerRadius(corner, corner),
    )
    drawRoundRect(
      color = palette.cardBorder,
      topLeft = Offset(1f, 1f),
      size = Size(w - 2f, h - 2f),
      cornerRadius = CornerRadius(corner, corner),
      style = Stroke(width = 2.5f),
    )

    val pad = w * 0.08f
    val cell = min((w - pad * 2f) / cols, (h - pad * 2f) / rows)
    val ox = (w - cell * cols) * 0.5f
    val oy = (h - cell * rows) * 0.5f
    val gap = cell * 0.14f
    val px = (cell - gap).coerceAtLeast(1f)
    val hg = gap * 0.5f

    for (i in 0 until baselinePixels.size) {
      val p = baselinePixels[i]
      drawRect(
        color = palette.baselinePixel,
        topLeft = Offset(ox + p.x * cell + hg, oy + p.y * cell + hg),
        size = Size(px, px),
      )
    }
    for (i in 0 until letterPixels.size) {
      val p = letterPixels[i]
      drawRect(
        color = palette.letterPixel,
        topLeft = Offset(ox + p.x * cell + hg, oy + p.y * cell + hg),
        size = Size(px, px),
      )
    }
    for (i in 0 until snakePreview.size) {
      val p = snakePreview[i]
      drawRect(
        color = if (i == 0) palette.snakeHead else palette.snakeTail,
        topLeft = Offset(ox + p.x * cell + hg, oy + p.y * cell + hg),
        size = Size(px, px),
      )
    }
  }
}
