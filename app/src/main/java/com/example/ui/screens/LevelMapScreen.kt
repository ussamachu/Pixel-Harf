package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.LevelProgress
import com.example.model.LevelSpec
import com.example.ui.Dimens
import com.example.ui.components.CircleButton
import com.example.ui.components.DotText
import com.example.ui.components.FlatCard
import com.example.ui.components.HairlineDivider
import com.example.ui.components.PixelIcon
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.AppShapes
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.NotoKufiFamily

@Composable
fun LevelMapScreen(
  strings: AppStrings,
  progress: LevelProgress,
  hapticsEnabled: Boolean,
  onSelectLevel: (Int) -> Unit,
  onBack: () -> Unit,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current
  val levelsByWorld = remember { LevelCatalog.levels.groupBy { it.world } }
  val totalStars = progress.starsByLevel.values.sum()
  val maxStars = LevelCatalog.levels.size * 3

  val pulseTransition = rememberInfiniteTransition(label = "current_level_pulse")
  val pulseAlphaRaw by
    pulseTransition.animateFloat(
      initialValue = 0.45f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(animation = tween(850), repeatMode = RepeatMode.Reverse),
      label = "current_pulse_alpha",
    )
  val pulseAlpha = if (reduceMotion) 1f else pulseAlphaRaw

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.widthIn(max = 620.dp).fillMaxSize()) {
      // Top bar
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .height(Dimens.TopBarHeight)
            .padding(horizontal = Dimens.ScreenPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          CircleButton(
            iconType = PixelIcon.BACK,
            contentDescription = strings.backHome,
            onClick = onBack,
            size = Dimens.CircleButtonSize,
            iconSize = 24.dp,
            mirrorForRtl = true,
            hapticsEnabled = hapticsEnabled,
            modifier = Modifier.testTag("map_back_button"),
          )
          Text(
            text = strings.levelMap,
            style = MaterialTheme.typography.titleLarge,
            fontSize = Dimens.TitleSp,
            color = palette.ink,
          )
        }

        FlatCard(shape = AppShapes.Card) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            PixelIcon(
              type = PixelIcon.STAR,
              size = 16.dp,
              contentDescription = strings.starsLabel,
            )
            Text(
              text = strings.starsTotal(totalStars, maxStars),
              fontFamily = JetBrainsMonoFamily,
              fontWeight = FontWeight.Bold,
              fontSize = Dimens.CaptionSp,
              color = palette.ink,
            )
          }
        }
      }

      HairlineDivider(modifier = Modifier.fillMaxWidth())

      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columns = if (maxWidth > 520.dp) 5 else 4
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceLg),
          verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap),
        ) {
          for (world in 1..4) {
            val worldLevels = levelsByWorld[world].orEmpty()
            item(key = "world_$world") {
              Column(
                modifier = Modifier.widgetStaggerEntrance(world - 1),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
              ) {
                // Mono section tag ("WORLD 1") + Arabic world title + short description
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
                  ) {
                    Text(
                      text = strings.worldTag(world),
                      fontFamily = JetBrainsMonoFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = Dimens.CaptionSp,
                      color = palette.accent,
                    )
                    DotText(
                      text = strings.worldTitle(world),
                      fontResId = R.font.noto_kufi_arabic,
                      dotSize = Dimens.DotWorldTitleSize,
                      color = palette.ink,
                      targetRows = 13,
                    )
                  }
                  Text(
                    text = strings.worldDesc(world),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = Dimens.CaptionSp,
                    color = palette.mutedInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }

                val rows = worldLevels.chunked(columns)
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
                  rows.forEach { rowItems ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
                    ) {
                      rowItems.forEach { spec ->
                        val unlocked = spec.levelNumber <= progress.unlockedLevel
                        val isCurrent = spec.levelNumber == progress.unlockedLevel
                        val stars = progress.starsByLevel[spec.levelNumber] ?: 0
                        LevelCircleTile(
                          spec = spec,
                          stars = stars,
                          unlocked = unlocked,
                          isCurrent = isCurrent,
                          pulseAlpha = pulseAlpha,
                          levelLabel = strings.levelNumLabel(spec.levelNumber),
                          hapticsEnabled = hapticsEnabled,
                          onClick = { if (unlocked) onSelectLevel(spec.levelNumber) },
                          modifier = Modifier.weight(1f),
                        )
                      }
                      repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LevelCircleTile(
  spec: LevelSpec,
  stars: Int,
  unlocked: Boolean,
  isCurrent: Boolean,
  pulseAlpha: Float,
  levelLabel: String,
  hapticsEnabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val palette = LocalPalette.current
  val completed = stars > 0

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
  ) {
    FlatCard(
      modifier =
        Modifier.fillMaxWidth()
          .aspectRatio(1f)
          .alpha(if (unlocked) 1f else 0.55f)
          .testTag("level_tile_${spec.levelNumber}"),
      shape = CircleShape,
      backgroundColor = palette.surface,
      pressedBackgroundColor = palette.surfacePressed,
      borderColor = if (isCurrent) palette.accent.copy(alpha = pulseAlpha) else palette.hairline,
      borderWidth = if (isCurrent) 2.dp else Dimens.HairlineWidth,
      hapticsEnabled = hapticsEnabled,
      onClick = if (unlocked) onClick else null,
    ) {
      Box(modifier = Modifier.fillMaxSize().padding(6.dp)) {
        if (unlocked) {
          Text(
            text = levelLabel,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (isCurrent) palette.accent else palette.mutedInk,
            modifier = Modifier.align(Alignment.TopCenter),
          )
          Text(
            text = spec.letter.glyph,
            fontFamily = NotoKufiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = palette.ink,
            modifier = Modifier.align(Alignment.Center),
          )
        } else {
          PixelIcon(
            type = PixelIcon.LOCK,
            size = 24.dp,
            contentDescription = levelLabel,
            modifier = Modifier.align(Alignment.Center),
          )
        }
      }
    }

    if (completed) {
      Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..3) {
          PixelIcon(
            type = if (i <= stars) PixelIcon.STAR else PixelIcon.STAR_OUTLINE,
            size = 16.dp,
            contentDescription = null,
          )
        }
      }
    } else {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
