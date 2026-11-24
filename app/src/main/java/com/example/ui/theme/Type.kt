package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoKufiFamily = FontFamily(Font(R.font.noto_kufi_arabic, FontWeight.Normal))
val NotoNaskhFamily = FontFamily(Font(R.font.noto_naskh_arabic, FontWeight.Normal))
val AmiriFamily = FontFamily(Font(R.font.amiri, FontWeight.Normal))
val ReemKufiFamily = FontFamily(Font(R.font.reem_kufi, FontWeight.Normal))
val PixelScoreFamily = FontFamily(Font(R.font.silkscreen, FontWeight.Normal))

fun buildAppTypography(largeText: Boolean): Typography {
  val scale = if (largeText) 1.18f else 1.0f
  return Typography(
    displayLarge =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (32 * scale).sp,
        lineHeight = (40 * scale).sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (22 * scale).sp,
        lineHeight = (30 * scale).sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = (18 * scale).sp,
        lineHeight = (26 * scale).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (16 * scale).sp,
        lineHeight = (22 * scale).sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (15 * scale).sp,
        lineHeight = (22 * scale).sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (13 * scale).sp,
        lineHeight = (19 * scale).sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = NotoKufiFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = (14 * scale).sp,
        lineHeight = (20 * scale).sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = PixelScoreFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (11 * scale).sp,
        lineHeight = (15 * scale).sp,
      ),
  )
}
