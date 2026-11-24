package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val PaperBg = Color(0xFFF6F4EE)
val CardSurface = Color(0xFFEBE8E0)
val CardSurfaceLight = Color(0xFFF2F0EA)
val CardInnerShadow = Color(0xFFD6D2C7)
val CardBorder = Color(0xFFCDC8BC)
val InkBlack = Color(0xFF141414)
val InkMuted = Color(0xFF5D5A52)
val PixelOrange = Color(0xFFF57C00)
val PixelOrangeSoft = Color(0xFFFFE0B2)
val PixelOrangeDeep = Color(0xFFE65100)
val BaselineGray = Color(0xFFB5B0A4)
val GrainDot = Color(0x14000000)

// Colorblind-safe Okabe-Ito accents
val SafeBlue = Color(0xFF0072B2)
val SafeBlueSoft = Color(0xFFCCE5F5)
val SafeVermilion = Color(0xFFD55E00)
val SafeYellow = Color(0xFFE69F00)
val SafeCyan = Color(0xFF56B4E9)

@Immutable
data class GameBoardPalette(
  val cardBackground: Color,
  val cardInnerShadow: Color,
  val cardBorder: Color,
  val letterPixel: Color,
  val dotBonusPixel: Color,
  val baselinePixel: Color,
  val snakeHead: Color,
  val snakeHeadArrow: Color,
  val snakeTail: Color,
  val powerUpPixel: Color,
  val ghostPixel: Color,
  val hintOutline: Color,
  val accent: Color,
  val accentSoft: Color,
  val confettiAlt: Color,
)

fun standardBoardPalette(): GameBoardPalette =
  GameBoardPalette(
    cardBackground = CardSurface,
    cardInnerShadow = CardInnerShadow,
    cardBorder = CardBorder,
    letterPixel = InkBlack,
    dotBonusPixel = Color(0xFF2B2A27),
    baselinePixel = BaselineGray,
    snakeHead = PixelOrange,
    snakeHeadArrow = Color(0xFFFFF8E1),
    snakeTail = Color(0xFFFB8C00),
    powerUpPixel = PixelOrangeDeep,
    ghostPixel = Color(0xFF00897B),
    hintOutline = PixelOrange,
    accent = PixelOrange,
    accentSoft = PixelOrangeSoft,
    confettiAlt = Color(0xFF26A69A),
  )

fun colorblindBoardPalette(): GameBoardPalette =
  GameBoardPalette(
    cardBackground = CardSurface,
    cardInnerShadow = CardInnerShadow,
    cardBorder = CardBorder,
    letterPixel = InkBlack,
    dotBonusPixel = Color(0xFF2B2A27),
    baselinePixel = BaselineGray,
    snakeHead = SafeBlue,
    snakeHeadArrow = Color.White,
    snakeTail = SafeCyan,
    powerUpPixel = SafeVermilion,
    ghostPixel = SafeYellow,
    hintOutline = SafeVermilion,
    accent = SafeBlue,
    accentSoft = SafeBlueSoft,
    confettiAlt = SafeYellow,
  )
