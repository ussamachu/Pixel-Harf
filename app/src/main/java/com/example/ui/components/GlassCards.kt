package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.model.ArabicFontSpec
import com.example.ui.Dimens
import com.example.ui.theme.AppShapes
import com.example.ui.theme.IconTint
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import kotlin.math.max
import kotlin.math.roundToInt

enum class CardSize {
  SQUARE_1X1,
  WIDE_2X1,
  TALL_1X2,
}

@Immutable
sealed interface StripSpec {
  val semanticSummary: String

  data class Ticks(
    val progress: Int,
    val total: Int,
    val currentIndex: Int,
    val startLabel: String = "1",
    val endLabel: String = total.toString(),
    override val semanticSummary: String,
  ) : StripSpec

  data class Bars(
    val values: List<Float>,
    val activeIndex: Int = values.lastIndex,
    val startLabel: String = "",
    val endLabel: String = "",
    override val semanticSummary: String,
  ) : StripSpec

  data class Dots(
    val count: Int,
    val activeIndex: Int,
    val filledCount: Int = activeIndex,
    val startLabel: String = "",
    val endLabel: String = "",
    override val semanticSummary: String,
  ) : StripSpec
}

/**
 * Frosted glass card container: 28dp corners, soft diffused shadow, 1dp hairline border,
 * and 1dp top white specular highlight.
 */
@Composable
fun FrostedCard(
  modifier: Modifier = Modifier,
  shape: Shape = AppShapes.GlassCard,
  cornerRadius: Dp = Dimens.GlassCardRadius,
  backgroundColor: Color = LocalPalette.current.cardSurface,
  pressedBackgroundColor: Color = LocalPalette.current.pressedFill,
  borderColor: Color = LocalPalette.current.hairline,
  borderWidth: Dp = Dimens.HairlineWidth,
  hapticsEnabled: Boolean = true,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val palette = LocalPalette.current
  val interactionSource = remember { MutableInteractionSource() }
  val pressed =
    if (onClick != null) rememberPressHaptic(interactionSource, hapticsEnabled) else false
  val effectiveBg = if (pressed) pressedBackgroundColor else backgroundColor
  val topHighlightPath = remember { Path() }

  val baseModifier =
    modifier
      .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val yOffset = 4.dp.toPx()
        // Multi-ring soft shadow (~6% peak ink, large spread, zero opaque backing)
        for (layer in 1..3) {
          val spread = (layer * 3.5f).dp.toPx()
          val alphaFactor =
            when (layer) {
              1 -> 0.45f
              2 -> 0.35f
              else -> 0.20f
            }
          drawRoundRect(
            color = palette.cardShadow.copy(alpha = palette.cardShadow.alpha * alphaFactor),
            topLeft = Offset(-spread * 0.35f, yOffset + spread * 0.25f),
            size = Size(size.width + spread * 0.70f, size.height + spread * 0.55f),
            cornerRadius = CornerRadius(radiusPx + spread * 0.3f, radiusPx + spread * 0.3f),
          )
        }
      }
      .clip(shape)
      .background(effectiveBg)
      .border(BorderStroke(borderWidth, borderColor), shape)
      .drawWithContent {
        drawContent()
        val r = cornerRadius.toPx().coerceAtMost(size.width * 0.5f)
        val strokePx = 1.dp.toPx()
        val y = strokePx * 0.5f
        topHighlightPath.reset()
        topHighlightPath.moveTo(r * 0.72f, y + r * 0.14f)
        topHighlightPath.lineTo(r, y)
        topHighlightPath.lineTo(size.width - r, y)
        topHighlightPath.lineTo(size.width - r * 0.72f, y + r * 0.14f)
        drawPath(
          path = topHighlightPath,
          color = palette.topHighlight,
          style = Stroke(width = strokePx),
        )
      }
      .let {
        if (onClick != null) {
          it.minimumInteractiveComponentSize().clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
          )
        } else it
      }

  Box(modifier = baseModifier, content = content)
}

/**
 * Small vertical pill (28x56dp) at the top-end holding a 16dp PixelIcon above a 10sp unit label.
 * Automatically mirrors side placement in RTL while keeping the unit text upright/LTR.
 */
@Composable
fun UnitChip(
  icon: PixelIcon,
  unit: String,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
  isSuccess: Boolean = false,
) {
  val palette = LocalPalette.current
  Column(
    modifier =
      modifier
        .size(width = Dimens.UnitChipWidth, height = Dimens.UnitChipHeight)
        .clip(AppShapes.UnitChip)
        .background(palette.unitChipBg)
        .padding(vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
  ) {
    PixelIcon(
      type = icon,
      size = 16.dp,
      isActive = isActive,
      isSuccess = isSuccess,
      contentDescription = null,
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
      Text(
        text = unit,
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.UnitSp,
        color = palette.textMed,
        maxLines = 1,
      )
    }
  }
}

/**
 * Micro-visual strip: thin vertical tick marks with completed (ink), current (orange), and remaining (faint).
 */
@Composable
fun TickStrip(
  progress: Int,
  total: Int,
  currentIndex: Int,
  semanticSummary: String,
  modifier: Modifier = Modifier,
  startLabel: String = "1",
  endLabel: String = total.toString(),
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val safeTotal = total.coerceAtLeast(1)
  val tickCount = safeTotal.coerceAtMost(30)

  val fillAnim = remember(progress, safeTotal, reduceMotion) {
    Animatable(if (reduceMotion) 1f else 0f)
  }
  LaunchedEffect(progress, safeTotal, reduceMotion) {
    if (reduceMotion) {
      fillAnim.snapTo(1f)
    } else {
      fillAnim.snapTo(0f)
      fillAnim.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 360, easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)),
      )
    }
  }

  val filledRatio = (progress.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f) * fillAnim.value
  val filledTickLimit = (filledRatio * tickCount).roundToInt().coerceIn(0, tickCount)
  val mappedCurrent =
    ((currentIndex.toFloat() / (safeTotal - 1).coerceAtLeast(1).toFloat()) * (tickCount - 1))
      .roundToInt()
      .coerceIn(0, tickCount - 1)

  Column(
    modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = semanticSummary },
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Canvas(modifier = Modifier.fillMaxWidth().height(Dimens.StripHeight)) {
      val step = if (tickCount > 1) size.width / (tickCount - 1).toFloat() else size.width
      val tickW = 2.dp.toPx().coerceAtMost((step * 0.48f).coerceAtLeast(1.2f))
      val baseH = size.height * 0.62f
      val currentH = size.height * 0.92f
      val bottomY = size.height

      for (i in 0 until tickCount) {
        val x = if (tickCount > 1) (i * step - tickW * 0.5f).coerceIn(0f, size.width - tickW) else 0f
        val isCurrent = i == mappedCurrent
        val isCompleted = i < filledTickLimit
        val barH = if (isCurrent) currentH else baseH
        val c =
          when {
            isCurrent -> palette.accent
            isCompleted -> palette.textHigh
            else -> palette.faintInk
          }
        drawRoundRect(
          color = c,
          topLeft = Offset(x, bottomY - barH),
          size = Size(tickW, barH),
          cornerRadius = CornerRadius(tickW * 0.5f, tickW * 0.5f),
        )
      }
    }

    if (startLabel.isNotEmpty() || endLabel.isNotEmpty()) {
      StripEndLabels(startLabel = startLabel, endLabel = endLabel)
    }
  }
}

/**
 * Micro-visual strip: vertical mini-bars with active bar highlighted in orange.
 */
@Composable
fun BarStrip(
  values: List<Float>,
  semanticSummary: String,
  modifier: Modifier = Modifier,
  activeIndex: Int = values.lastIndex,
  startLabel: String = "",
  endLabel: String = "",
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val safeValues = if (values.isEmpty()) listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f) else values

  val growAnim = remember(safeValues, reduceMotion) {
    Animatable(if (reduceMotion) 1f else 0f)
  }
  LaunchedEffect(safeValues, reduceMotion) {
    if (reduceMotion) {
      growAnim.snapTo(1f)
    } else {
      growAnim.snapTo(0f)
      growAnim.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 320, easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)),
      )
    }
  }

  Column(
    modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = semanticSummary },
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Canvas(modifier = Modifier.fillMaxWidth().height(Dimens.BarStripHeight)) {
      val count = safeValues.size
      val gap = 4.dp.toPx()
      val barW = ((size.width - gap * (count - 1)) / count.toFloat()).coerceAtLeast(3.dp.toPx())
      val maxVal = max(1f, safeValues.maxOrNull() ?: 1f)
      val minBarH = 3.dp.toPx()

      for (i in 0 until count) {
        val norm = (safeValues[i] / maxVal).coerceIn(0f, 1f) * growAnim.value
        val barH = max(minBarH, size.height * norm)
        val x = i * (barW + gap)
        val isActive = i == activeIndex && safeValues[i] > 0f
        val color =
          when {
            isActive -> palette.accent
            safeValues[i] > 0f -> palette.textHigh
            else -> palette.faintInk
          }
        drawRoundRect(
          color = color,
          topLeft = Offset(x, size.height - barH),
          size = Size(barW, barH),
          cornerRadius = CornerRadius(barW * 0.45f, barW * 0.45f),
        )
      }
    }

    if (startLabel.isNotEmpty() || endLabel.isNotEmpty()) {
      StripEndLabels(startLabel = startLabel, endLabel = endLabel)
    }
  }
}

/**
 * Micro-visual strip: row of dots with completed dots in ink, active dot enlarged in orange, remaining faint.
 */
@Composable
fun DotRowStrip(
  count: Int,
  activeIndex: Int,
  semanticSummary: String,
  modifier: Modifier = Modifier,
  filledCount: Int = activeIndex,
  startLabel: String = "",
  endLabel: String = "",
) {
  val palette = LocalPalette.current
  val safeCount = count.coerceAtLeast(1)

  Column(
    modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = semanticSummary },
    verticalArrangement = Arrangement.spacedBy(3.dp),
  ) {
    Canvas(modifier = Modifier.fillMaxWidth().height(Dimens.StripHeight)) {
      val step = if (safeCount > 1) size.width / (safeCount - 1).toFloat() else size.width
      val baseR = 3.dp.toPx()
      val activeR = 4.6.dp.toPx()
      val cy = size.height * 0.5f

      for (i in 0 until safeCount) {
        val cx =
          if (safeCount > 1) (activeR + i * ((size.width - activeR * 2f) / (safeCount - 1)))
          else size.width * 0.5f
        val isActive = i == activeIndex && filledCount > 0
        val isFilled = i < filledCount
        drawCircle(
          color =
            when {
              isActive -> palette.accent
              isFilled -> palette.textHigh
              else -> palette.faintInk
            },
          radius = if (isActive) activeR else baseR,
          center = Offset(cx, cy),
        )
      }
    }

    if (startLabel.isNotEmpty() || endLabel.isNotEmpty()) {
      StripEndLabels(startLabel = startLabel, endLabel = endLabel)
    }
  }
}

@Composable
private fun StripEndLabels(
  startLabel: String,
  endLabel: String,
) {
  val palette = LocalPalette.current
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = startLabel,
        fontFamily = JetBrainsMonoFamily,
        fontSize = Dimens.UnitSp,
        color = palette.textMed,
        maxLines = 1,
      )
      Text(
        text = endLabel,
        fontFamily = JetBrainsMonoFamily,
        fontSize = Dimens.UnitSp,
        color = palette.textMed,
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun RenderStrip(strip: StripSpec) {
  when (strip) {
    is StripSpec.Ticks ->
      TickStrip(
        progress = strip.progress,
        total = strip.total,
        currentIndex = strip.currentIndex,
        startLabel = strip.startLabel,
        endLabel = strip.endLabel,
        semanticSummary = strip.semanticSummary,
      )
    is StripSpec.Bars ->
      BarStrip(
        values = strip.values,
        activeIndex = strip.activeIndex,
        startLabel = strip.startLabel,
        endLabel = strip.endLabel,
        semanticSummary = strip.semanticSummary,
      )
    is StripSpec.Dots ->
      DotRowStrip(
        count = strip.count,
        activeIndex = strip.activeIndex,
        filledCount = strip.filledCount,
        startLabel = strip.startLabel,
        endLabel = strip.endLabel,
        semanticSummary = strip.semanticSummary,
      )
  }
}

/**
 * Reusable health-widget style stat card:
 * 1. Clean sans/Arabic title (14sp) + subtitle (12sp) at top-start
 * 2. One big dot-matrix hero number (round dots, ~36-40sp)
 * 3. Vertical UnitChip pill (28x56dp) at top-end
 * 4. Optional micro-visual strip at the bottom
 */
@Composable
fun StatCard(
  title: String,
  subtitle: String,
  heroText: String,
  chipIcon: PixelIcon,
  chipUnit: String,
  strip: StripSpec? = null,
  modifier: Modifier = Modifier,
  cardSize: CardSize = CardSize.SQUARE_1X1,
  isActiveAccent: Boolean = false,
  animateHero: Boolean = true,
  hapticsEnabled: Boolean = true,
  onClick: (() -> Unit)? = null,
) {
  val palette = LocalPalette.current
  val sizeModifier =
    when (cardSize) {
      CardSize.SQUARE_1X1 -> Modifier.fillMaxWidth().aspectRatio(1f)
      CardSize.WIDE_2X1 -> Modifier.fillMaxWidth().heightIn(min = 148.dp)
      CardSize.TALL_1X2 -> Modifier.fillMaxWidth().aspectRatio(0.68f)
    }

  val dotSize =
    when {
      heroText.length >= 5 -> 2.7.dp
      heroText.length == 4 -> 3.1.dp
      heroText.length == 3 -> 3.4.dp
      else -> Dimens.HeroDotSize
    }

  FrostedCard(
    modifier = modifier.then(sizeModifier),
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Column(
      modifier =
        Modifier
          .let { if (cardSize == CardSize.WIDE_2X1) it.fillMaxWidth() else it.fillMaxSize() }
          .padding(Dimens.CardPadding),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      if (cardSize == CardSize.WIDE_2X1) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top,
        ) {
          Column(
            modifier = Modifier.weight(1f).padding(end = Dimens.SpaceSm),
            verticalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontSize = Dimens.CardTitleSp,
              fontWeight = FontWeight.Medium,
              color = palette.textHigh,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodyMedium,
              fontSize = Dimens.CaptionSp,
              color = palette.textMed,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            DotDigitText(
              text = heroText,
              dotSize = dotSize,
              color = if (isActiveAccent) palette.accent else palette.textHigh,
              animateScanIn = animateHero,
            )
            UnitChip(
              icon = chipIcon,
              unit = chipUnit,
              isActive = isActiveAccent,
            )
          }
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontSize = Dimens.CardTitleSp,
            fontWeight = FontWeight.Medium,
            color = palette.textHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = Dimens.CaptionSp,
            color = palette.textMed,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          DotDigitText(
            text = heroText,
            dotSize = dotSize,
            color = if (isActiveAccent) palette.accent else palette.textHigh,
            animateScanIn = animateHero,
          )
          UnitChip(
            icon = chipIcon,
            unit = chipUnit,
            isActive = isActiveAccent,
          )
        }
      }

      if (strip != null) {
        Spacer(modifier = Modifier.height(Dimens.SpaceSm))
        RenderStrip(strip)
      }
    }
  }
}

/**
 * Wide 2x1 card for Today's Letter:
 * Hero = the Arabic letter rasterized as big round dots via LetterRasterizer,
 * Chip = DAILY or CHECK icon, Strip = 7-day streak DotRowStrip.
 */
@Composable
fun HeroGlyphCard(
  title: String,
  subtitle: String,
  glyph: String,
  glyphName: String,
  fontSpec: ArabicFontSpec,
  chipIcon: PixelIcon,
  chipUnit: String,
  streakDays: Int,
  todayCompleted: Boolean,
  stripStartLabel: String,
  stripEndLabel: String,
  stripSemanticSummary: String,
  modifier: Modifier = Modifier,
  hapticsEnabled: Boolean = true,
  onClick: () -> Unit,
) {
  val palette = LocalPalette.current
  val clampedStreak = streakDays.coerceIn(0, 7)

  FrostedCard(
    modifier = modifier.fillMaxWidth(),
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
          modifier = Modifier.weight(1f).padding(end = Dimens.SpaceSm),
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontSize = Dimens.CardTitleSp,
            fontWeight = FontWeight.Medium,
            color = palette.textHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = Dimens.CaptionSp,
            color = palette.textMed,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          DotGlyphHero(
            glyph = glyph,
            fontSpec = fontSpec,
            contentDescription = glyphName,
            boxSize = 54.dp,
            color = palette.textHigh,
            dotAccentColor = palette.accent,
          )
          UnitChip(
            icon = chipIcon,
            unit = chipUnit,
            isSuccess = todayCompleted,
          )
        }
      }

      DotRowStrip(
        count = 7,
        filledCount = clampedStreak,
        activeIndex = (clampedStreak - 1).coerceAtLeast(0),
        startLabel = stripStartLabel,
        endLabel = stripEndLabel,
        semanticSummary = stripSemanticSummary,
      )
    }
  }
}

/**
 * Compact frosted pill for the game top bar (dot-matrix 16sp equivalent value + tiny icon, no title).
 */
@Composable
fun MiniStatPill(
  valueText: String,
  icon: PixelIcon,
  contentDescription: String,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
) {
  val palette = LocalPalette.current
  FrostedCard(
    modifier = modifier.height(36.dp),
    shape = RoundedCornerShape(18.dp),
    cornerRadius = 18.dp,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      PixelIcon(
        type = icon,
        size = 16.dp,
        isActive = isActive,
        contentDescription = contentDescription,
      )
      DotDigitText(
        text = valueText,
        dotSize = Dimens.MiniDotSize,
        color = if (isActive) palette.accent else palette.textHigh,
        animateScanIn = false,
        contentDescription = null,
      )
    }
  }
}
