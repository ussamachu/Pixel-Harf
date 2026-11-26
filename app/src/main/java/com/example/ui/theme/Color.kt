package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Light theme tokens (default)
val CanvasLight = Color(0xFFF4F2EE)
val BlobSandLight = Color(0xFFE8DFD1)
val BlobSageLight = Color(0xFFD7E0D3)
val BlobGlowLight = Color(0x1CF57C00)
val CardLight = Color(0xE6FDFCFB) // #FDFCFB at ~90% opacity
val SurfaceLight = Color(0xE6FDFCFB)
val SurfaceLightPressed = Color(0x0D1A1A1A) // ink at 5%
val InkLight = Color(0xFF1A1A1A)
val TextHighLight = Color(0xFF1A1A1A) // 100%
val TextMedLight = Color(0x9E1A1A1A) // 62%
val TextLowLight = Color(0x611A1A1A) // 38%
val MutedLight = TextMedLight
val HairlineLight = Color(0x141A1A1A) // ink at 8%
val BorderHairlineLight = Color(0x1F1A1A1A) // ink at 12% for secondary buttons / off switches
val FaintInkLight = Color(0x291A1A1A) // 16% for unfilled ticks/dots
val UnitChipLight = Color(0x0D1A1A1A) // ink at 5%
val CardShadowLight = Color(0x0F1A1A1A) // ink at 6%
val TopHighlightLight = Color(0xCCFFFFFF)

// Dark theme tokens (optional Settings toggle)
val CanvasDark = Color(0xFF121110)
val BlobCharcoalDark = Color(0xFF23201E)
val BlobSageDark = Color(0xFF1E261E)
val BlobGlowDark = Color(0x1EF57C00)
val CardDark = Color(0xEB1C1A19) // #1C1A19 at 92% opacity
val SurfaceDark = Color(0xEB1C1A19)
val SurfaceDarkPressed = Color(0x14FFFFFF)
val InkDark = Color(0xFFFFFFFF)
val TextHighDark = Color(0xFFFFFFFF) // 100%
val TextMedDark = Color(0x99FFFFFF) // 60%
val TextLowDark = Color(0x61FFFFFF) // 38%
val MutedDark = TextMedDark
val HairlineDark = Color(0x14FFFFFF) // white at 8%
val BorderHairlineDark = Color(0x1FFFFFFF) // white at 12%
val FaintInkDark = Color(0x2EFFFFFF) // 18% for unfilled ticks/dots
val UnitChipDark = Color(0x12FFFFFF) // white at 7%
val CardShadowDark = Color(0x2E000000)
val TopHighlightDark = Color(0x24FFFFFF)

// Single accent color + pressed shift
val Accent = Color(0xFFF57C00)
val AccentOrange = Accent
val AccentPressed = Color(0xFFE65100)
val AccentOrangePressed = AccentPressed
val AccentOrangeSoftLight = Color(0x1FF57C00)
val AccentOrangeSoftDark = Color(0x2EF57C00)

// Lives heart red & icon semantic accents
val HeartRed = Color(0xFFE53935)
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
    defaultInk = TextHighLight,
    mutedInk = TextMedLight,
    disabledInk = TextLowLight,
    activeOrange = Accent,
    heartFilled = HeartRed,
    heartOutline = HeartRed.copy(alpha = 0.35f),
    starFilled = Accent,
    starOutline = TextMedLight.copy(alpha = 0.35f),
    successGreen = SuccessGreen,
    powerSlowMo = SlowMoBlueGrayLight,
    powerMagnet = HeartRed,
    powerGhost = GhostVioletLight,
    powerShrink = SuccessGreen,
  )

fun darkIconColors(): IconColors =
  IconColors(
    defaultInk = TextHighDark,
    mutedInk = TextMedDark,
    disabledInk = TextLowDark,
    activeOrange = Accent,
    heartFilled = HeartRed,
    heartOutline = HeartRed.copy(alpha = 0.35f),
    starFilled = Accent,
    starOutline = TextMedDark.copy(alpha = 0.35f),
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
  val blobSand: Color,
  val blobSage: Color,
  val blobGlow: Color,
  val cardSurface: Color,
  val surface: Color,
  val surfacePressed: Color,
  val pressedFill: Color,
  val ink: Color,
  val textHigh: Color,
  val textMed: Color,
  val textLow: Color,
  val mutedInk: Color,
  val hairline: Color,
  val hairline12: Color,
  val topHighlight: Color,
  val cardShadow: Color,
  val unitChipBg: Color,
  val faintInk: Color,
  val scrim: Color,
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
  val card = if (darkVariant) CardDark else CardLight
  val pressed = if (darkVariant) SurfaceDarkPressed else SurfaceLightPressed
  val textHigh = if (darkVariant) TextHighDark else TextHighLight
  val textMed = if (darkVariant) TextMedDark else TextMedLight
  val textLow = if (darkVariant) TextLowDark else TextLowLight
  val hairline = if (darkVariant) HairlineDark else HairlineLight
  val hairline12 = if (darkVariant) BorderHairlineDark else BorderHairlineLight
  val faint = if (darkVariant) FaintInkDark else FaintInkLight
  val accentSoft = if (darkVariant) AccentOrangeSoftDark else AccentOrangeSoftLight

  return PixelHarfPalette(
    isDark = darkVariant,
    canvas = canvas,
    blobSand = if (darkVariant) BlobCharcoalDark else BlobSandLight,
    blobSage = if (darkVariant) BlobSageDark else BlobSageLight,
    blobGlow = if (darkVariant) BlobGlowDark else BlobGlowLight,
    cardSurface = card,
    surface = card,
    surfacePressed = pressed,
    pressedFill = pressed,
    ink = textHigh,
    textHigh = textHigh,
    textMed = textMed,
    textLow = textLow,
    mutedInk = textMed,
    hairline = hairline,
    hairline12 = hairline12,
    topHighlight = if (darkVariant) TopHighlightDark else TopHighlightLight,
    cardShadow = if (darkVariant) CardShadowDark else CardShadowLight,
    unitChipBg = if (darkVariant) UnitChipDark else UnitChipLight,
    faintInk = faint,
    scrim = Color(0x661A1A1A), // ink at 40%
    accent = Accent,
    accentPressed = AccentPressed,
    accentSoft = accentSoft,
    snakeHead = Accent,
    snakeHeadArrow = Color(0xFF1A1A1A),
    snakeTail = if (darkVariant) Color(0xFFBDB7B7) else Color(0xFF4B4646),
    dotBonus = if (colorblindSafe) SafeVermilion else textHigh,
    powerUp = if (colorblindSafe) SafeAmber else Accent,
    ghost = if (colorblindSafe) SafeAmber else if (darkVariant) Color(0xFF9F9999) else Color(0xFF6B6565),
    baselinePixel = faint,
    confettiAlt = if (darkVariant) Color(0xFFDCD6D6) else Color(0xFF4B4646),
    colorblindShapes = colorblindSafe,
    iconColors = if (darkVariant) darkIconColors() else lightIconColors(),
  )
}
