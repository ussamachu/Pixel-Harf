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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.components.DotIcon
import com.example.ui.components.DotIconType
import com.example.ui.components.DotStarRow
import com.example.ui.components.DotText
import com.example.ui.components.WidgetCard
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.NotoKufiFamily
import com.example.ui.theme.WidgetRadius

@Composable
fun LevelMapScreen(
  strings: AppStrings,
  progress: LevelProgress,
  hapticsEnabled: Boolean,
  onSelectLevel: (Int) -> Unit,
  onBack: () -> Unit,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current
  val levelsByWorld = remember { LevelCatalog.levels.groupBy { it.world } }
  val totalStars = progress.starsByLevel.values.sum()
  val maxStars = LevelCatalog.levels.size * 3

  val pulseTransition = rememberInfiniteTransition(label = "current_level_pulse")
  val pulseAlphaRaw by
    pulseTransition.animateFloat(
      initialValue = 0.35f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(animation = tween(850), repeatMode = RepeatMode.Reverse),
      label = "current_pulse_alpha",
    )
  val pulseAlpha = if (reduceMotion) 0.9f else pulseAlphaRaw

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.widthIn(max = 620.dp).fillMaxSize()) {
      // Top Navigation Bar
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
            iconType = DotIconType.BACK,
            contentDescription = strings.backHome,
            onClick = onBack,
            size = Dimens.SmallCircleButtonSize,
            dotSize = 2.0.dp,
            mirrorForRtl = true,
            hapticsEnabled = hapticsEnabled,
            modifier = Modifier.testTag("map_back_button"),
          )
          Text(
            text = strings.levelMap,
            style = MaterialTheme.typography.headlineMedium,
            color = palette.ink,
          )
        }

        WidgetCard(
          shape = WidgetRadius.Pill,
          cornerRadiusDp = 24.dp,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            DotStarRow(stars = 1, dotSize = 1.6.dp, spacing = 0.dp, filledColor = palette.accent)
            Text(
              text = strings.starsTotal(totalStars, maxStars),
              fontFamily = DotoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = palette.ink,
            )
          }
        }
      }

      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columns = if (maxWidth > 520.dp) 5 else 4
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceMd),
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXl),
        ) {
          for (world in 1..4) {
            val worldLevels = levelsByWorld[world].orEmpty()
            item(key = "world_$world") {
              Column(
                modifier = Modifier.widgetStaggerEntrance(world - 1),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
              ) {
                // Compact 2-line header per world (~22sp dot-text title + 12sp subtitle)
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs)) {
                  DotText(
                    text = strings.worldTitle(world),
                    fontResId = R.font.noto_kufi_arabic,
                    dotSize = Dimens.DotWorldTitleDotSize,
                    color = palette.ink,
                    targetRows = 13,
                  )
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
  val palette = LocalNothingPalette.current
  val completed = stars > 0
  val accentColor = palette.accent

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
  ) {
    WidgetCard(
      modifier =
        Modifier.fillMaxWidth()
          .aspectRatio(1f)
          .alpha(if (unlocked) 1f else 0.55f)
          .let { m ->
            if (isCurrent) {
              m.drawBehind {
                drawCircle(
                  color = accentColor.copy(alpha = pulseAlpha),
                  radius = size.minDimension * 0.49f,
                  style = Stroke(width = 3.5f),
                )
              }
            } else m
          }
          .testTag("level_tile_${spec.levelNumber}"),
      shape = CircleShape,
      cornerRadiusDp = 48.dp,
      backgroundColor = if (isCurrent) palette.accent else palette.widgetSurface,
      hapticsEnabled = hapticsEnabled,
      onClick = if (unlocked) onClick else null,
    ) {
      Box(modifier = Modifier.fillMaxSize().padding(6.dp)) {
        if (unlocked) {
          Text(
            text = levelLabel,
            fontFamily = DotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (isCurrent) Color.White.copy(alpha = 0.9f) else palette.mutedInk,
            modifier = Modifier.align(Alignment.TopCenter),
          )
          Text(
            text = spec.letter.glyph,
            fontFamily = NotoKufiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = if (isCurrent) Color.White else palette.ink,
            modifier = Modifier.align(Alignment.Center),
          )
        } else {
          // Locked levels: faint lock icon only, no ghost letter
          DotIcon(
            type = DotIconType.LOCK,
            dotSize = 1.9.dp,
            color = palette.mutedInk.copy(alpha = 0.6f),
            contentDescription = levelLabel,
            modifier = Modifier.align(Alignment.Center),
          )
        }
      }
    }

    if (completed) {
      DotStarRow(
        stars = stars,
        dotSize = 1.5.dp,
        spacing = 3.dp,
        filledColor = palette.ink,
        emptyColor = palette.faintDot,
      )
    } else {
      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}
