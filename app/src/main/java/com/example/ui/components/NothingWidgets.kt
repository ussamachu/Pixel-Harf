package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.WidgetRadius
import kotlinx.coroutines.delay

/**
 * Core Nothing OS widget container: soft #EEF1E6 (or dark #222) card, large corner radius,
 * no outer border or drop shadow, subtle inner shadow, optional tactile press scale.
 */
@Composable
fun WidgetCard(
  modifier: Modifier = Modifier,
  shape: Shape = WidgetRadius.Card,
  cornerRadiusDp: Dp = 32.dp,
  backgroundColor: Color = LocalNothingPalette.current.widgetSurface,
  hapticsEnabled: Boolean = true,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val palette = LocalNothingPalette.current
  val interactionSource = remember { MutableInteractionSource() }

  val baseModifier =
    modifier
      .let { if (onClick != null) it.mechanicalPress(interactionSource, hapticsEnabled) else it }
      .clip(shape)
      .background(backgroundColor)
      .drawBehind {
        val rPx = cornerRadiusDp.toPx().coerceAtMost(size.minDimension * 0.5f)
        // Faint inner shadow along top/left edges for gadget display depth
        drawRoundRect(
          color = palette.innerShadow,
          topLeft = Offset(1.5f, 1.5f),
          size = Size(size.width - 3f, size.height - 3f),
          cornerRadius = CornerRadius(rPx, rPx),
          style = Stroke(width = 3.5f),
        )
      }
      .let {
        if (onClick != null) {
          it.minimumInteractiveComponentSize().clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = onClick,
          )
        } else it
      }

  Box(modifier = baseModifier, content = content)
}

@Composable
fun PillButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimaryOrange: Boolean = false,
  useDotLabel: Boolean = true,
  showAnimatedArrow: Boolean = false,
  hapticsEnabled: Boolean = true,
  leadingContent: (@Composable () -> Unit)? = null,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current
  val bg = if (isPrimaryOrange) palette.accent else palette.widgetSurface
  val fg = if (isPrimaryOrange) Color.White else palette.ink

  var arrowTick by remember { mutableIntStateOf(0) }
  LaunchedEffect(showAnimatedArrow, reduceMotion) {
    if (showAnimatedArrow && !reduceMotion) {
      while (true) {
        delay(650L)
        arrowTick = (arrowTick + 1) % 3
      }
    }
  }
  val arrowShift by
    animateFloatAsState(
      targetValue = if (arrowTick == 1) 4f else 0f,
      animationSpec = tween(220),
      label = "pill_arrow_shift",
    )

  WidgetCard(
    modifier = modifier.height(58.dp),
    shape = WidgetRadius.Pill,
    cornerRadiusDp = 30.dp,
    backgroundColor = bg,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      if (leadingContent != null) {
        leadingContent()
        Spacer(modifier = Modifier.width(10.dp))
      }
      if (useDotLabel) {
        DotText(
          text = label,
          fontResId = R.font.noto_kufi_arabic,
          dotSize = 2.2.dp,
          color = fg,
          targetRows = 14,
        )
      } else {
        Text(
          text = label,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = fg,
          maxLines = 1,
        )
      }
      if (showAnimatedArrow) {
        Spacer(modifier = Modifier.width(12.dp))
        DotIcon(
          type = DotIconType.ARROW_RIGHT,
          dotSize = 2.3.dp,
          color = fg,
          mirrorForRtl = true,
          modifier = Modifier.offset(x = arrowShift.dp),
        )
      }
    }
  }
}

@Composable
fun CircleButton(
  iconType: DotIconType,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 56.dp,
  dotSize: Dp = 2.4.dp,
  isHighlighted: Boolean = false,
  mirrorForRtl: Boolean = false,
  hapticsEnabled: Boolean = true,
) {
  val palette = LocalNothingPalette.current
  val bg = if (isHighlighted) palette.accent else palette.widgetSurface
  val fg = if (isHighlighted) Color.White else palette.ink

  WidgetCard(
    modifier = modifier.size(size),
    shape = WidgetRadius.Circle,
    cornerRadiusDp = size / 2,
    backgroundColor = bg,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    DotIcon(
      type = iconType,
      dotSize = dotSize,
      color = fg,
      mirrorForRtl = mirrorForRtl,
      contentDescription = contentDescription,
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

/**
 * Nothing OS style toggle switch: pill track, circular thumb, orange when ON.
 */
@Composable
fun NothingSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current
  val interactionSource = remember { MutableInteractionSource() }

  val thumbOffset by
    animateDpAsState(
      targetValue = if (checked) 24.dp else 4.dp,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 180),
      label = "switch_thumb",
    )

  Box(
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(50))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(bounded = false),
          role = Role.Switch,
          onClick = { onCheckedChange(!checked) },
        )
        .semantics { this.contentDescription = contentDescription }
        .padding(4.dp),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier =
        Modifier.width(52.dp)
          .height(30.dp)
          .clip(RoundedCornerShape(50))
          .background(if (checked) palette.accent else palette.faintDot),
    ) {
      Box(
        modifier =
          Modifier.align(Alignment.CenterStart)
            .offset(x = thumbOffset)
            .size(22.dp)
            .clip(CircleShape)
            .background(if (checked) Color.White else palette.ink),
      )
    }
  }
}

/**
 * Animated flipping dot-matrix numeric counter using Doto font.
 */
@Composable
fun DotDigitText(
  value: String,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 20.sp,
  color: Color = LocalNothingPalette.current.ink,
) {
  val reduceMotion = LocalReduceMotion.current
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    value.forEachIndexed { idx, ch ->
      if (reduceMotion) {
        Text(
          text = ch.toString(),
          fontFamily = DotoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = fontSize,
          color = color,
        )
      } else {
        AnimatedContent(
          targetState = ch,
          transitionSpec = {
            (slideInVertically(tween(150)) { -it / 2 } + fadeIn(tween(150))) togetherWith
              (slideOutVertically(tween(120)) { it / 2 } + fadeOut(tween(120)))
          },
          label = "digit_$idx",
        ) { digit ->
          Text(
            text = digit.toString(),
            fontFamily = DotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = color,
          )
        }
      }
    }
  }
}
