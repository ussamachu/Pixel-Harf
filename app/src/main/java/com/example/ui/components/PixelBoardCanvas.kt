package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.model.Direction
import com.example.model.GameState
import com.example.model.PowerUpType
import com.example.ui.Dimens
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.WidgetRadius
import kotlin.math.min
import kotlin.random.Random

/**
 * Hero 60fps Canvas board filling its parent container (single card, no nested frames).
 * Zero allocations inside onDraw.
 */
@Composable
fun PixelBoardCanvas(
  gameState: GameState,
  useDotPixels: Boolean,
  modifier: Modifier = Modifier,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current

  val grainPoints = remember {
    val rnd = Random(4242)
    FloatArray(220) { rnd.nextFloat() }
  }
  val arrowPath = remember { Path() }

  val pulseTransition = rememberInfiniteTransition(label = "board_pulse")
  val pulseRaw by
    pulseTransition.animateFloat(
      initialValue = 0.45f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(animation = tween(680), repeatMode = RepeatMode.Reverse),
      label = "pulse_alpha",
    )
  val pulse = if (reduceMotion) 0.85f else pulseRaw

  val shakeStrength = if (reduceMotion) 0f else (gameState.screenShakeMs / 280f).coerceIn(0f, 1f)
  val shakeX =
    if (shakeStrength > 0f) ((gameState.screenShakeMs % 37L) - 18L) * 0.45f * shakeStrength else 0f
  val shakeY =
    if (shakeStrength > 0f) ((gameState.screenShakeMs % 29L) - 14L) * 0.45f * shakeStrength else 0f

  val cols = gameState.gridCols.coerceAtLeast(8)
  val rows = gameState.gridRows.coerceAtLeast(8)

  Canvas(
    modifier =
      modifier
        .fillMaxSize()
        .graphicsLayer {
          translationX = shakeX
          translationY = shakeY
        }
        .clip(WidgetRadius.BoardCard)
  ) {
    val boardW = size.width
    val boardH = size.height
    val cornerPx = Dimens.BoardRadius.toPx()

    // 1. Single rounded widget card background + faint inner shadow
    drawRoundRect(
      color = palette.widgetSurface,
      topLeft = Offset.Zero,
      size = size,
      cornerRadius = CornerRadius(cornerPx, cornerPx),
    )
    drawRoundRect(
      color = palette.innerShadow,
      topLeft = Offset(2f, 2f),
      size = Size(boardW - 4f, boardH - 4f),
      cornerRadius = CornerRadius(cornerPx - 1f, cornerPx - 1f),
      style = Stroke(width = 4.5f),
    )

    // Subtle monochrome grain
    val grainColor = palette.ink.copy(alpha = 0.04f)
    var gi = 0
    while (gi + 1 < grainPoints.size) {
      drawCircle(
        color = grainColor,
        radius = 1.1f,
        center = Offset(grainPoints[gi] * boardW, grainPoints[gi + 1] * boardH),
      )
      gi += 2
    }

    val pad = min(boardW, boardH) * 0.045f
    val innerW = (boardW - pad * 2f).coerceAtLeast(1f)
    val innerH = (boardH - pad * 2f).coerceAtLeast(1f)
    val cell = min(innerW / cols.toFloat(), innerH / rows.toFloat())
    val originX = (boardW - cell * cols) * 0.5f
    val originY = (boardH - cell * rows) * 0.5f
    val gap = cell * 0.16f
    val pxSize = (cell - gap).coerceAtLeast(1.5f)
    val halfGap = gap * 0.5f
    val dotR = (pxSize * 0.48f).coerceAtLeast(1f)

    // 2. Baseline row under the Arabic letter
    val baseline = gameState.baselinePixels
    for (i in 0 until baseline.size) {
      val p = baseline[i]
      val cx = originX + p.x * cell + cell * 0.5f
      val cy = originY + p.y * cell + cell * 0.5f
      if (useDotPixels) {
        drawCircle(color = palette.baselineDot, radius = dotR * 0.68f, center = Offset(cx, cy))
      } else {
        drawRect(
          color = palette.baselineDot,
          topLeft = Offset(originX + p.x * cell + halfGap, originY + p.y * cell + halfGap),
          size = Size(pxSize, pxSize),
        )
      }
    }

    // 3. Remaining letter pixels & dot bonus targets
    val remaining = gameState.remainingPixels
    val dots = gameState.dotPixels
    for (i in 0 until remaining.size) {
      val p = remaining[i]
      val isDot = dots.contains(p)
      val cx = originX + p.x * cell + cell * 0.5f
      val cy = originY + p.y * cell + cell * 0.5f
      val pxColor = if (isDot) palette.dotBonus else palette.ink

      if (useDotPixels) {
        drawCircle(color = pxColor, radius = dotR, center = Offset(cx, cy))
        if (isDot) {
          drawCircle(color = palette.accent, radius = dotR * 0.42f, center = Offset(cx, cy))
          if (palette.colorblindShapes) {
            drawCircle(
              color = palette.accent,
              radius = dotR * 1.15f,
              center = Offset(cx, cy),
              style = Stroke(width = 2f),
            )
          }
        }
      } else {
        val left = originX + p.x * cell + halfGap
        val top = originY + p.y * cell + halfGap
        drawRect(color = pxColor, topLeft = Offset(left, top), size = Size(pxSize, pxSize))
        if (isDot) {
          val inset = pxSize * 0.28f
          drawRect(
            color = palette.accent,
            topLeft = Offset(left + inset, top + inset),
            size = Size(pxSize - inset * 2f, pxSize - inset * 2f),
          )
        }
      }
    }

    // 4. Kids mode next-pixel hint outline
    val hint = gameState.hintPixel
    if (hint != null) {
      val cx = originX + hint.x * cell + cell * 0.5f
      val cy = originY + hint.y * cell + cell * 0.5f
      if (useDotPixels) {
        drawCircle(
          color = palette.accent.copy(alpha = pulse),
          radius = dotR * 1.32f,
          center = Offset(cx, cy),
          style = Stroke(width = 2.6f),
        )
      } else {
        val left = originX + hint.x * cell + halfGap
        val top = originY + hint.y * cell + halfGap
        drawRect(
          color = palette.accent.copy(alpha = pulse),
          topLeft = Offset(left - 2f, top - 2f),
          size = Size(pxSize + 4f, pxSize + 4f),
          style = Stroke(width = 2.6f),
        )
      }
    }

    // 5. Power-up item
    val powerUp = gameState.activePowerUp
    if (powerUp != null) {
      val cx = originX + powerUp.point.x * cell + cell * 0.5f
      val cy = originY + powerUp.point.y * cell + cell * 0.5f
      val puAlpha = 0.4f + 0.6f * pulse
      if (useDotPixels && !palette.colorblindShapes) {
        drawCircle(color = palette.powerUp.copy(alpha = puAlpha), radius = dotR * 1.05f, center = Offset(cx, cy))
        drawCircle(color = Color.White, radius = dotR * 0.4f, center = Offset(cx, cy))
      } else {
        val left = originX + powerUp.point.x * cell + halfGap
        val top = originY + powerUp.point.y * cell + halfGap
        drawRect(color = palette.powerUp.copy(alpha = puAlpha), topLeft = Offset(left, top), size = Size(pxSize, pxSize))
        val inset = pxSize * 0.28f
        drawRect(
          color = Color.White,
          topLeft = Offset(left + inset, top + inset),
          size = Size(pxSize - inset * 2f, pxSize - inset * 2f),
        )
      }
    }

    // 6. World 4 moving ghost pixel
    val ghost = gameState.ghostPixel
    if (ghost != null) {
      val cx = originX + ghost.point.x * cell + cell * 0.5f
      val cy = originY + ghost.point.y * cell + cell * 0.5f
      val ratio = (ghost.remainingMs.toFloat() / ghost.totalMs.toFloat()).coerceIn(0.2f, 1f)
      drawCircle(
        color = palette.ghost.copy(alpha = 0.35f + 0.65f * pulse * ratio),
        radius = dotR * 0.9f,
        center = Offset(cx, cy),
      )
      drawCircle(
        color = palette.ghost,
        radius = dotR * 1.28f,
        center = Offset(cx, cy),
        style = Stroke(width = if (palette.colorblindShapes) 3f else 2f),
      )
    }

    // 7. Snake tail
    val snake = gameState.snake
    val isGhostMode = gameState.activeEffect == PowerUpType.GHOST
    val tailAlpha = if (isGhostMode) 0.40f else 0.86f
    for (i in snake.size - 1 downTo 1) {
      val seg = snake[i]
      if (useDotPixels) {
        val cx = originX + seg.x * cell + cell * 0.5f
        val cy = originY + seg.y * cell + cell * 0.5f
        val taper = if (i == snake.lastIndex && snake.size > 2) 0.72f else 0.88f
        drawCircle(color = palette.snakeTail.copy(alpha = tailAlpha), radius = dotR * taper, center = Offset(cx, cy))
      } else {
        val shrink = if (i == snake.lastIndex && snake.size > 2) pxSize * 0.14f else pxSize * 0.06f
        drawRect(
          color = palette.snakeTail.copy(alpha = tailAlpha),
          topLeft = Offset(originX + seg.x * cell + halfGap + shrink, originY + seg.y * cell + halfGap + shrink),
          size = Size(pxSize - shrink * 2f, pxSize - shrink * 2f),
        )
      }
    }

    // 8. Snake head + directional arrow
    if (snake.isNotEmpty()) {
      val head = snake[0]
      val cx = originX + head.x * cell + cell * 0.5f
      val cy = originY + head.y * cell + cell * 0.5f

      if (useDotPixels) {
        drawCircle(color = palette.snakeHead, radius = dotR * 1.12f, center = Offset(cx, cy))
      } else {
        drawRect(
          color = palette.snakeHead,
          topLeft = Offset(originX + head.x * cell + halfGap, originY + head.y * cell + halfGap),
          size = Size(pxSize, pxSize),
        )
      }

      arrowPath.reset()
      val r = pxSize * 0.27f
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

    // 9. Particles
    val particles = gameState.particles
    for (i in 0 until particles.size) {
      val pt = particles[i]
      val alpha = (pt.life / pt.maxLife).coerceIn(0f, 1f)
      val pColor =
        when (pt.colorKind) {
          1 -> palette.accent.copy(alpha = alpha)
          2 -> palette.confettiAlt.copy(alpha = alpha)
          3 -> palette.powerUp.copy(alpha = alpha)
          else -> palette.ink.copy(alpha = alpha)
        }
      val pCenterX = originX + pt.x * cell
      val pCenterY = originY + pt.y * cell
      if (useDotPixels) {
        drawCircle(
          color = pColor,
          radius = (dotR * pt.sizeFactor).coerceAtLeast(1.5f),
          center = Offset(pCenterX, pCenterY),
        )
      } else {
        val pSize = (pxSize * pt.sizeFactor).coerceAtLeast(2f)
        drawRect(
          color = pColor,
          topLeft = Offset(pCenterX - pSize * 0.5f, pCenterY - pSize * 0.5f),
          size = Size(pSize, pSize),
        )
      }
    }
  }
}
