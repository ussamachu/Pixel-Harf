package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val JetBrainsMonoFamily = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))
val IbmPlexArabicFamily = FontFamily(Font(R.font.ibm_plex_sans_arabic, FontWeight.Normal))
val NotoKufiFamily = FontFamily(Font(R.font.noto_kufi_arabic, FontWeight.Normal))
val NotoNaskhFamily = FontFamily(Font(R.font.noto_naskh_arabic, FontWeight.Normal))
val AmiriFamily = FontFamily(Font(R.font.amiri, FontWeight.Normal))
val ReemKufiFamily = FontFamily(Font(R.font.reem_kufi, FontWeight.Normal))

// Capped type scale: display 28sp, title 20sp, body 14sp, caption 12sp.
// Arabic text always uses IBM Plex Sans Arabic / Noto Kufi; JetBrains Mono is for numbers & mono section tags.
fun buildAppTypography(largeText: Boolean): Typography {
  val bodyScale = if (largeText) 1.15f else 1.0f
  return Typography(
    displayLarge =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = (16 * bodyScale).sp,
        lineHeight = (22 * bodyScale).sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (14 * bodyScale).sp,
        lineHeight = (20 * bodyScale).sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (12 * bodyScale).sp,
        lineHeight = (16 * bodyScale).sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = IbmPlexArabicFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = (14 * bodyScale).sp,
        lineHeight = (18 * bodyScale).sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
      ),
  )
}
