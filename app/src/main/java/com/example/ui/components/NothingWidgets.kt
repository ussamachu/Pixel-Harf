package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Dimens
import com.example.ui.theme.AppShapes
import com.example.ui.theme.IconTint
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion

/**
 * Flat card with 1dp hairline border and zero shadows.
 * Pressed state shifts surface color cleanly without glow or ripple.
 */
@Composable
fun FlatCard(
  modifier: Modifier = Modifier,
  shape: Shape = AppShapes.Card,
  backgroundColor: Color = LocalPalette.current.surface,
  pressedBackgroundColor: Color = LocalPalette.current.surfacePressed,
  borderColor: Color = LocalPalette.current.hairline,
  borderWidth: Dp = Dimens.HairlineWidth,
  hapticsEnabled: Boolean = true,
  onClick: (() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed =
    if (onClick != null) rememberPressHaptic(interactionSource, hapticsEnabled) else false
  val effectiveBg = if (pressed) pressedBackgroundColor else backgroundColor

  val baseModifier =
    modifier
      .clip(shape)
      .background(effectiveBg)
      .border(BorderStroke(borderWidth, borderColor), shape)
      .let {
        if (onClick != null) {
          it.minimumInteractiveComponentSize().clickable(
            interactionSource = interactionSource,
            indication = null, // Surface color shift only — no glow or ripple
            onClick = onClick,
          )
        } else it
      }

  Box(modifier = baseModifier, content = content)
}

/**
 * Flat 8dp button: orange primary (#F57C00 / pressed #E65100) or neutral hairline card.
 */
@Composable
fun FlatButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isPrimaryOrange: Boolean = false,
  hapticsEnabled: Boolean = true,
) {
  val palette = LocalPalette.current
  val bg = if (isPrimaryOrange) palette.accent else palette.surface
  val bgPressed = if (isPrimaryOrange) palette.accentPressed else palette.surfacePressed
  val border = if (isPrimaryOrange) palette.accent else palette.hairline
  val fg = if (isPrimaryOrange) Color.White else palette.ink

  FlatCard(
    modifier = modifier.height(Dimens.ButtonHeight),
    shape = AppShapes.Button,
    backgroundColor = bg,
    pressedBackgroundColor = bgPressed,
    borderColor = border,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = fg,
        textAlign = TextAlign.Center,
        maxLines = 1,
      )
    }
  }
}

/**
 * Circular pixel-icon button (48dp+ touch target) with 1dp hairline and surface color shift on press.
 */
@Composable
fun CircleButton(
  iconType: PixelIcon,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = Dimens.CircleButtonSize,
  iconSize: Dp = 24.dp,
  isHighlighted: Boolean = false,
  isActive: Boolean = false,
  mirrorForRtl: Boolean = false,
  hapticsEnabled: Boolean = true,
) {
  val palette = LocalPalette.current
  val bg = if (isHighlighted) palette.accent else palette.surface
  val bgPressed = if (isHighlighted) palette.accentPressed else palette.surfacePressed
  val border = if (isHighlighted) palette.accent else palette.hairline
  val tint = if (isHighlighted) IconTint.Color(Color.White) else IconTint.Auto

  FlatCard(
    modifier = modifier.size(size),
    shape = AppShapes.Circle,
    backgroundColor = bg,
    pressedBackgroundColor = bgPressed,
    borderColor = border,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    PixelIcon(
      type = iconType,
      size = iconSize,
      tint = tint,
      isActive = isActive,
      mirrorForRtl = mirrorForRtl,
      contentDescription = contentDescription,
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

/**
 * Flat hairline toggle switch with orange ON state (no glow/ripple).
 */
@Composable
fun FlatSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val interactionSource = remember { MutableInteractionSource() }

  val thumbOffset by
    animateDpAsState(
      targetValue = if (checked) 24.dp else 4.dp,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 150),
      label = "switch_thumb",
    )

  Box(
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          role = Role.Switch,
          onClick = { onCheckedChange(!checked) },
        )
        .semantics { this.contentDescription = contentDescription }
        .padding(4.dp),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier =
        Modifier.width(48.dp)
          .height(28.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (checked) palette.accent else palette.surface)
          .border(
            width = Dimens.HairlineWidth,
            color = if (checked) palette.accent else palette.hairline,
            shape = RoundedCornerShape(8.dp),
          ),
    ) {
      Box(
        modifier =
          Modifier.align(Alignment.CenterStart)
            .offset(x = thumbOffset)
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (checked) Color.White else palette.ink),
      )
    }
  }
}

/**
 * Monospace numeric counter using JetBrains Mono (weights 400/700).
 */
@Composable
fun MonoDigitText(
  value: String,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 16.sp,
  fontWeight: FontWeight = FontWeight.Bold,
  color: Color = LocalPalette.current.ink,
) {
  val reduceMotion = LocalReduceMotion.current
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    value.forEachIndexed { idx, ch ->
      if (reduceMotion) {
        Text(
          text = ch.toString(),
          fontFamily = JetBrainsMonoFamily,
          fontWeight = fontWeight,
          fontSize = fontSize,
          color = color,
        )
      } else {
        AnimatedContent(
          targetState = ch,
          transitionSpec = {
            (slideInVertically(tween(130)) { -it / 2 } + fadeIn(tween(130))) togetherWith
              (slideOutVertically(tween(100)) { it / 2 } + fadeOut(tween(100)))
          },
          label = "mono_digit_$idx",
        ) { digit ->
          Text(
            text = digit.toString(),
            fontFamily = JetBrainsMonoFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = color,
          )
        }
      }
    }
  }
}

@Composable
fun HairlineDivider(modifier: Modifier = Modifier) {
  val palette = LocalPalette.current
  Box(
    modifier =
      modifier
        .height(Dimens.HairlineWidth)
        .background(palette.hairline),
  )
}
