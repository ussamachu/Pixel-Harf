package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Warm off-white flat palette (no blue anywhere)
val CanvasLight = Color(0xFFFDFCFC)
val SurfaceLight = Color(0xFFF1EEEE)
val SurfaceLightPressed = Color(0xFFE5E0E0)
val InkLight = Color(0xFF201D1D)
val MutedLight = Color(0xFF5D5656)
val HairlineLight = Color(0x1F0F0000) // rgba(15,0,0,0.12)
val FaintInkLight = Color(0x33201D1D)

// Dark flat variant (#201D1D / #302C2C)
val CanvasDark = Color(0xFF201D1D)
val SurfaceDark = Color(0xFF302C2C)
val SurfaceDarkPressed = Color(0xFF3D3838)
val InkDark = Color(0xFFFDFCFC)
val MutedDark = Color(0xFFBDB7B7)
val HairlineDark = Color(0x29FDFCFC)
val FaintInkDark = Color(0x3DFDFCFC)

// Single accent color + pressed shift
val AccentOrange = Color(0xFFF57C00)
val AccentOrangePressed = Color(0xFFE65100)
val AccentOrangeSoftLight = Color(0x1FF57C00)
val AccentOrangeSoftDark = Color(0x2EF57C00)

// Lives heart red
val HeartRed = Color(0xFFE53935)

// Icon semantic accents (used strictly for their designated icon states)
val SuccessGreen = Color(0xFF30D158)
val SlowMoBlueGrayLight = Color(0xFF607D8B)
val SlowMoBlueGrayDark = Color(0xFF90A4AE)
val GhostVioletLight = Color(0xFF7E57C2)
val GhostVioletDark = Color(0xFFB39DDB)

// High-contrast monochrome/amber/vermilion tokens (no blue anywhere)
val SafeAmber = Color(0xFFD98200)
val SafeVermilion = Color(0xFFC63D00)

sealed interface IconTint {
  data object Auto : IconTint
  data class Color(val color: androidx.compose.ui.graphics.Color) : IconTint
}

@Immutable
data class IconColors(
  val defaultInk: Color,
  val mutedInk: Color,
  val disabledInk: Color,
  val activeOrange: Color,
  val heartFilled: Color,
  val heartOutline: Color,
  val starFilled: Color,
  val starOutline: Color,
  val successGreen: Color,
  val powerSlowMo: Color,
  val powerMagnet: Color,
  val powerGhost: Color,
  val powerShrink: Color,
)

fun lightIconColors(): IconColors =
  IconColors(
    defaultInk = InkLight,
    mutedInk = MutedLight,
    disabledInk = InkLight.copy(alpha = 0.38f),
    activeOrange = AccentOrange,
    heartFilled = HeartRed,
    heartOutline = HeartRed.copy(alpha = 0.35f),
    starFilled = AccentOrange,
    starOutline = MutedLight.copy(alpha = 0.35f),
    successGreen = SuccessGreen,
    powerSlowMo = SlowMoBlueGrayLight,
    powerMagnet = HeartRed,
    powerGhost = GhostVioletLight,
    powerShrink = SuccessGreen,
  )

fun darkIconColors(): IconColors =
  IconColors(
    defaultInk = InkDark,
    mutedInk = MutedDark,
    disabledInk = InkDark.copy(alpha = 0.38f),
    activeOrange = AccentOrange,
    heartFilled = HeartRed,
    heartOutline = HeartRed.copy(alpha = 0.35f),
    starFilled = AccentOrange,
    starOutline = MutedDark.copy(alpha = 0.35f),
    successGreen = SuccessGreen,
    powerSlowMo = SlowMoBlueGrayDark,
    powerMagnet = HeartRed,
    powerGhost = GhostVioletDark,
    powerShrink = SuccessGreen,
  )

@Immutable
data class PixelHarfPalette(
  val isDark: Boolean,
  val canvas: Color,
  val surface: Color,
  val surfacePressed: Color,
  val ink: Color,
  val mutedInk: Color,
  val hairline: Color,
  val faintInk: Color,
  val accent: Color,
  val accentPressed: Color,
  val accentSoft: Color,
  val snakeHead: Color,
  val snakeHeadArrow: Color,
  val snakeTail: Color,
  val dotBonus: Color,
  val powerUp: Color,
  val ghost: Color,
  val baselinePixel: Color,
  val confettiAlt: Color,
  val colorblindShapes: Boolean,
  val iconColors: IconColors,
)

fun buildPixelHarfPalette(darkVariant: Boolean, colorblindSafe: Boolean): PixelHarfPalette {
  val canvas = if (darkVariant) CanvasDark else CanvasLight
  val surface = if (darkVariant) SurfaceDark else SurfaceLight
  val surfacePressed = if (darkVariant) SurfaceDarkPressed else SurfaceLightPressed
  val ink = if (darkVariant) InkDark else InkLight
  val muted = if (darkVariant) MutedDark else MutedLight
  val hairline = if (darkVariant) HairlineDark else HairlineLight
  val faint = if (darkVariant) FaintInkDark else FaintInkLight
  val accentSoft = if (darkVariant) AccentOrangeSoftDark else AccentOrangeSoftLight

  return PixelHarfPalette(
    isDark = darkVariant,
    canvas = canvas,
    surface = surface,
    surfacePressed = surfacePressed,
    ink = ink,
    mutedInk = muted,
    hairline = hairline,
    faintInk = faint,
    accent = AccentOrange,
    accentPressed = AccentOrangePressed,
    accentSoft = accentSoft,
    snakeHead = AccentOrange,
    snakeHeadArrow = Color.White,
    snakeTail = if (darkVariant) Color(0xFFBDB7B7) else Color(0xFF4B4646),
    dotBonus = if (colorblindSafe) SafeVermilion else ink,
    powerUp = if (colorblindSafe) SafeAmber else AccentOrange,
    ghost = if (colorblindSafe) SafeAmber else if (darkVariant) Color(0xFF9F9999) else Color(0xFF6B6565),
    baselinePixel = faint,
    confettiAlt = if (darkVariant) Color(0xFFDCD6D6) else Color(0xFF4B4646),
    colorblindShapes = colorblindSafe,
    iconColors = if (darkVariant) darkIconColors() else lightIconColors(),
  )
}
