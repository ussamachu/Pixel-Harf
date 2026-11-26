package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ArabicFontSpec
import com.example.ui.Dimens
import com.example.ui.theme.PixelHarfTheme

private val previewFontSpec =
  ArabicFontSpec(
    id = "noto_kufi",
    fontResId = R.font.noto_kufi_arabic,
    displayNameAr = "كوفي حديث",
    displayNameEn = "Noto Kufi",
    displayNameFr = "Noto Kufi",
  )

@Composable
private fun CardGalleryContent(isRtl: Boolean) {
  val layoutDir = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
  CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
    Box(modifier = Modifier.fillMaxWidth()) {
      FrostedGradientBackground(modifier = Modifier.matchParentSize())

      Column(
        modifier = Modifier.fillMaxWidth().padding(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.CardGap),
      ) {
        HeroGlyphCard(
          title = if (isRtl) "تحدي اليوم" else "Daily Letter",
          subtitle = if (isRtl) "باء · كوفي حديث" else "Ba · Noto Kufi",
          glyph = "ب",
          glyphName = "Ba",
          fontSpec = previewFontSpec,
          chipIcon = PixelIcon.DAILY,
          chipUnit = "7d",
          streakDays = 5,
          todayCompleted = false,
          stripStartLabel = "1",
          stripEndLabel = "7",
          stripSemanticSummary = "5 of 7 days",
          onClick = {},
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.CardGap),
        ) {
          StatCard(
            title = if (isRtl) "التقدم" else "Progress",
            subtitle = "24%",
            heroText = "29",
            chipIcon = PixelIcon.MAP,
            chipUnit = "/120",
            strip =
              StripSpec.Ticks(
                progress = 29,
                total = 120,
                currentIndex = 28,
                startLabel = "1",
                endLabel = "120",
                semanticSummary = "29 of 120 letters done",
              ),
            cardSize = CardSize.SQUARE_1X1,
            animateHero = false,
            modifier = Modifier.weight(1f),
          )
          StatCard(
            title = if (isRtl) "النجوم" else "Stars",
            subtitle = "72 / 360",
            heroText = "72",
            chipIcon = PixelIcon.STAR,
            chipUnit = "/360",
            strip =
              StripSpec.Dots(
                count = 4,
                filledCount = 2,
                activeIndex = 1,
                startLabel = "W1",
                endLabel = "W4",
                semanticSummary = "72 stars",
              ),
            cardSize = CardSize.SQUARE_1X1,
            animateHero = false,
            modifier = Modifier.weight(1f),
          )
        }

        StatCard(
          title = if (isRtl) "الأفضل" else "Best Score",
          subtitle = if (isRtl) "آخر 7 جلسات" else "Last 7 sessions",
          heroText = "840",
          chipIcon = PixelIcon.TROPHY,
          chipUnit = "XP",
          strip =
            StripSpec.Bars(
              values = listOf(220f, 390f, 410f, 620f, 540f, 710f, 840f),
              activeIndex = 6,
              startLabel = "S1",
              endLabel = "S7",
              semanticSummary = "Best score 840",
            ),
          cardSize = CardSize.WIDE_2X1,
          animateHero = false,
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.CardGap),
        ) {
          StatCard(
            title = if (isRtl) "السلسلة" else "Streak",
            subtitle = if (isRtl) "أيام متتالية" else "Active days",
            heroText = "13",
            chipIcon = PixelIcon.DAILY,
            chipUnit = "DAYS",
            strip =
              StripSpec.Dots(
                count = 7,
                filledCount = 5,
                activeIndex = 4,
                startLabel = "M",
                endLabel = "S",
                semanticSummary = "Streak 13",
              ),
            cardSize = CardSize.TALL_1X2,
            animateHero = false,
            modifier = Modifier.weight(1f),
          )
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.CardGap),
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
              MiniStatPill(
                valueText = "0450",
                icon = PixelIcon.STAR,
                contentDescription = "Score 450",
              )
              MiniStatPill(
                valueText = "x3",
                icon = PixelIcon.HEART,
                contentDescription = "Combo 3",
              )
            }
            PrimaryButton(
              label = if (isRtl) "العب" else "Play",
              leadingIcon = PixelIcon.PLAY,
              trailingDotValue = "11",
              onClick = {},
            )
            SecondaryButton(
              label = if (isRtl) "الخريطة" else "Map",
              onClick = {},
              modifier = Modifier.fillMaxWidth(),
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              GlassIconButton(
                icon = PixelIcon.MAP,
                contentDescription = "Map",
                label = if (isRtl) "الخريطة" else "Map",
                isActive = true,
                onClick = {},
              )
              ToggleSwitch(
                checked = true,
                onCheckedChange = {},
                contentDescription = "Toggle",
              )
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 860, name = "Light LTR")
@Composable
private fun CardGalleryLightLtrPreview() {
  PixelHarfTheme(darkVariant = false) {
    CardGalleryContent(isRtl = false)
  }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 860, name = "Dark LTR")
@Composable
private fun CardGalleryDarkLtrPreview() {
  PixelHarfTheme(darkVariant = true) {
    CardGalleryContent(isRtl = false)
  }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 860, name = "Light RTL")
@Composable
private fun CardGalleryLightRtlPreview() {
  PixelHarfTheme(darkVariant = false) {
    CardGalleryContent(isRtl = true)
  }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 860, name = "Dark RTL")
@Composable
private fun CardGalleryDarkRtlPreview() {
  PixelHarfTheme(darkVariant = true) {
    CardGalleryContent(isRtl = true)
  }
}
