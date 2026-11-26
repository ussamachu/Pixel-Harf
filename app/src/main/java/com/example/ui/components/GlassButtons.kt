package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Dimens
import com.example.ui.theme.Accent
import com.example.ui.theme.AccentPressed
import com.example.ui.theme.AppShapes
import com.example.ui.theme.IconTint
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.TextHighLight

/**
 * Primary orange pill button (56dp tall, #F57C00 fill, #E65100 + 0.97 scale on press).
 * Uses #1A1A1A dark text (6.44:1 contrast on #F57C00, 4.60:1 on #E65100 >= 4.5:1 WCAG AA).
 * Max ONE primary button per screen.
 */
@Composable
fun PrimaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: PixelIcon? = null,
  trailingDotValue: String? = null,
  hapticsEnabled: Boolean = true,
) {
  val reduceMotion = LocalReduceMotion.current
  val interactionSource = remember { MutableInteractionSource() }
  val pressed = rememberPressHaptic(interactionSource, hapticsEnabled)
  val scale by
    animateFloatAsState(
      targetValue = if (pressed && !reduceMotion) 0.97f else 1f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 100),
      label = "primary_btn_scale",
    )
  val bg = if (pressed) AccentPressed else Accent
  val fg = TextHighLight

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(Dimens.PrimaryButtonHeight)
        .minimumInteractiveComponentSize()
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .clip(AppShapes.PrimaryPill)
        .background(bg)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          role = Role.Button,
          onClick = onClick,
        )
        .padding(horizontal = 22.dp),
    contentAlignment = Alignment.Center,
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
      ) {
        if (leadingIcon != null) {
          PixelIcon(
            type = leadingIcon,
            size = 20.dp,
            tint = IconTint.Color(fg),
            mirrorForRtl = true,
            contentDescription = null,
          )
        }
        Text(
          text = label,
          style = MaterialTheme.typography.titleMedium,
          fontSize = Dimens.ButtonSp,
          fontWeight = FontWeight.Bold,
          color = fg,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      if (trailingDotValue != null) {
        DotDigitText(
          text = trailingDotValue,
          dotSize = Dimens.ButtonDotSize,
          color = fg,
          animateScanIn = false,
          contentDescription = null,
        )
      } else {
        Spacer(modifier = Modifier.width(1.dp))
      }
    }
  }
}

/**
 * Secondary frosted pill button (48dp tall, 24dp radius, 12% ink hairline, ink@5% on press).
 */
@Composable
fun SecondaryButton(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: PixelIcon? = null,
  hapticsEnabled: Boolean = true,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val interactionSource = remember { MutableInteractionSource() }
  val pressed = rememberPressHaptic(interactionSource, hapticsEnabled)
  val scale by
    animateFloatAsState(
      targetValue = if (pressed && !reduceMotion) 0.98f else 1f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 100),
      label = "secondary_btn_scale",
    )

  FrostedCard(
    modifier =
      modifier
        .height(Dimens.SecondaryButtonHeight)
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        },
    shape = AppShapes.SecondaryPill,
    cornerRadius = Dimens.SecondaryButtonRadius,
    backgroundColor = palette.cardSurface,
    pressedBackgroundColor = palette.pressedFill,
    borderColor = palette.hairline12,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      if (leadingIcon != null) {
        PixelIcon(
          type = leadingIcon,
          size = 16.dp,
          mirrorForRtl = true,
          contentDescription = null,
          modifier = Modifier.padding(end = Dimens.SpaceSm),
        )
      }
      Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        fontSize = Dimens.ButtonSp,
        fontWeight = FontWeight.SemiBold,
        color = palette.textHigh,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

/**
 * 48dp circular frosted button with 24dp PixelIcon and optional nav label + active orange dot.
 */
@Composable
fun GlassIconButton(
  icon: PixelIcon,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null,
  isActive: Boolean = false,
  mirrorForRtl: Boolean = false,
  hapticsEnabled: Boolean = true,
) {
  val palette = LocalPalette.current
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
  ) {
    FrostedCard(
      modifier = Modifier.size(Dimens.CircleButtonSize),
      shape = CircleShape,
      cornerRadius = 24.dp,
      borderColor = if (isActive) palette.accent.copy(alpha = 0.45f) else palette.hairline12,
      hapticsEnabled = hapticsEnabled,
      onClick = onClick,
    ) {
      PixelIcon(
        type = icon,
        size = 24.dp,
        isActive = isActive,
        mirrorForRtl = mirrorForRtl,
        contentDescription = contentDescription,
        modifier = Modifier.align(Alignment.Center),
      )
    }

    if (label != null) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 11.sp,
        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
        color = if (isActive) palette.textHigh else palette.textMed,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Box(
        modifier =
          Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(if (isActive) palette.accent else Color.Transparent),
      )
    }
  }
}

/**
 * 52x30dp pill toggle switch with circular thumb, orange ON state, ink@12% OFF state,
 * and a small pixel checkmark inside the thumb when ON.
 */
@Composable
fun ToggleSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val interactionSource = remember { MutableInteractionSource() }

  val thumbOffsetX by
    animateFloatAsState(
      targetValue = if (checked) 25f else 3f,
      animationSpec = tween(durationMillis = if (reduceMotion) 0 else 160),
      label = "toggle_thumb_offset",
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
        .semantics { this.contentDescription = contentDescription },
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier =
        Modifier
          .size(width = Dimens.SwitchTrackWidth, height = Dimens.SwitchTrackHeight)
          .clip(CircleShape)
          .background(if (checked) palette.accent else palette.hairline12)
          .border(
            BorderStroke(Dimens.HairlineWidth, if (checked) palette.accent else palette.hairline12),
            CircleShape,
          ),
    ) {
      Box(
        modifier =
          Modifier
            .align(Alignment.CenterStart)
            .offset(x = thumbOffsetX.dp)
            .size(Dimens.SwitchThumbSize)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
      ) {
        if (checked) {
          PixelIcon(
            type = PixelIcon.CHECK,
            size = 14.dp,
            tint = IconTint.Color(palette.accent),
            contentDescription = null,
          )
        }
      }
    }
  }
}
