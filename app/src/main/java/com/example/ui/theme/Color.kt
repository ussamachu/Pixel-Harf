package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Light monochrome Nothing OS palette
val NothingBgLightTop = Color(0xFFD9DBD4)
val NothingBgLightMid = Color(0xFFC8CBC3)
val NothingBgLightBottom = Color(0xFFB5B8AF)
val NothingWidgetLight = Color(0xFFEEF1E6)
val NothingWidgetLightPressed = Color(0xFFE3E7DA)
val NothingInnerShadowLight = Color(0x1C000000)
val NothingInkDark = Color(0xFF1A1A1A)
val NothingMutedLight = Color(0xFF4D5047)
val NothingFaintDotLight = Color(0x331A1A1A)

// Dark monochrome Nothing OS variant
val NothingBgDarkTop = Color(0xFF171717)
val NothingBgDarkMid = Color(0xFF111111)
val NothingBgDarkBottom = Color(0xFF0A0A0A)
val NothingWidgetDark = Color(0xFF222222)
val NothingWidgetDarkPressed = Color(0xFF2B2B2B)
val NothingInnerShadowDark = Color(0x45000000)
val NothingInkLight = Color(0xFFF2F3EC)
val NothingMutedDark = Color(0xFFB4B7AD)
val NothingFaintDotDark = Color(0x38F2F3EC)

// Single gadget accent
val NothingOrange = Color(0xFFF57C00)
val NothingOrangeSoftLight = Color(0x26F57C00)
val NothingOrangeSoftDark = Color(0x33F57C00)

// Colorblind-safe Okabe-Ito accents (paired with distinct dot/shape geometry)
val SafeBlue = Color(0xFF0072B2)
val SafeCyan = Color(0xFF56B4E9)
val SafeVermilion = Color(0xFFD55E00)
val SafeYellow = Color(0xFFE69F00)

@Immutable
data class NothingPalette(
  val isDark: Boolean,
  val bgTop: Color,
  val bgMid: Color,
  val bgBottom: Color,
  val widgetSurface: Color,
  val widgetPressed: Color,
  val innerShadow: Color,
  val ink: Color,
  val mutedInk: Color,
  val faintDot: Color,
  val accent: Color,
  val accentSoft: Color,
  val snakeHead: Color,
  val snakeHeadArrow: Color,
  val snakeTail: Color,
  val dotBonus: Color,
  val powerUp: Color,
  val ghost: Color,
  val baselineDot: Color,
  val confettiAlt: Color,
  val colorblindShapes: Boolean,
)

fun buildNothingPalette(darkVariant: Boolean, colorblindSafe: Boolean): NothingPalette {
  val bgTop = if (darkVariant) NothingBgDarkTop else NothingBgLightTop
  val bgMid = if (darkVariant) NothingBgDarkMid else NothingBgLightMid
  val bgBottom = if (darkVariant) NothingBgDarkBottom else NothingBgLightBottom
  val widget = if (darkVariant) NothingWidgetDark else NothingWidgetLight
  val pressed = if (darkVariant) NothingWidgetDarkPressed else NothingWidgetLightPressed
  val innerShadow = if (darkVariant) NothingInnerShadowDark else NothingInnerShadowLight
  val ink = if (darkVariant) NothingInkLight else NothingInkDark
  val muted = if (darkVariant) NothingMutedDark else NothingMutedLight
  val faint = if (darkVariant) NothingFaintDotDark else NothingFaintDotLight
  val accentSoft = if (darkVariant) NothingOrangeSoftDark else NothingOrangeSoftLight

  return if (colorblindSafe) {
    NothingPalette(
      isDark = darkVariant,
      bgTop = bgTop,
      bgMid = bgMid,
      bgBottom = bgBottom,
      widgetSurface = widget,
      widgetPressed = pressed,
      innerShadow = innerShadow,
      ink = ink,
      mutedInk = muted,
      faintDot = faint,
      accent = NothingOrange,
      accentSoft = accentSoft,
      snakeHead = SafeBlue,
      snakeHeadArrow = Color.White,
      snakeTail = SafeCyan,
      dotBonus = SafeVermilion,
      powerUp = SafeVermilion,
      ghost = SafeYellow,
      baselineDot = faint,
      confettiAlt = SafeYellow,
      colorblindShapes = true,
    )
  } else {
    NothingPalette(
      isDark = darkVariant,
      bgTop = bgTop,
      bgMid = bgMid,
      bgBottom = bgBottom,
      widgetSurface = widget,
      widgetPressed = pressed,
      innerShadow = innerShadow,
      ink = ink,
      mutedInk = muted,
      faintDot = faint,
      accent = NothingOrange,
      accentSoft = accentSoft,
      snakeHead = NothingOrange,
      snakeHeadArrow = Color.White,
      snakeTail = if (darkVariant) Color(0xFFBDBFB6) else Color(0xFF50524B),
      dotBonus = ink,
      powerUp = NothingOrange,
      ghost = if (darkVariant) Color(0xFF9EA299) else Color(0xFF66695F),
      baselineDot = faint,
      confettiAlt = if (darkVariant) Color(0xFFD5D7CE) else Color(0xFF474942),
      colorblindShapes = false,
    )
  }
}
