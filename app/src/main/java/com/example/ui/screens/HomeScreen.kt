package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.GameMode
import com.example.model.LevelProgress
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.CardSize
import com.example.ui.components.FrostedCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.HeroGlyphCard
import com.example.ui.components.PixelIcon
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatCard
import com.example.ui.components.StripSpec
import com.example.ui.components.UiAppearanceState
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.LocalPalette

@Composable
fun HomeScreen(
  strings: AppStrings,
  settings: UserSettings,
  progress: LevelProgress,
  appearance: UiAppearanceState,
  onPlayContinue: (Int) -> Unit,
  onPlayDaily: () -> Unit,
  onOpenMap: () -> Unit,
  onOpenSettings: () -> Unit,
  onToggleSound: () -> Unit,
  onSelectMode: (GameMode) -> Unit,
) {
  val palette = LocalPalette.current
  val dailySpec = remember { LevelCatalog.dailyChallenge() }
  val todayDone = progress.dailyCompletedDate == LevelCatalog.todayKey()

  var showModesPanel by remember { mutableStateOf(false) }

  val totalLevels = LevelCatalog.levels.size.coerceAtLeast(1)
  val completedCount = progress.starsByLevel.count { it.value > 0 }
  val completionPercent = ((completedCount.toFloat() / totalLevels.toFloat()) * 100f).toInt().coerceIn(0, 100)
  val totalStars = progress.starsByLevel.values.sum()
  val maxStars = totalLevels * 3
  val bestOverallScore =
    maxOf(progress.dailyBestScore, progress.bestScoreByLevel.values.maxOrNull() ?: 0)

  val worldCompletionCount =
    remember(progress.starsByLevel) {
      (1..4).count { world ->
        LevelCatalog.levels.any { spec ->
          spec.world == world && (progress.starsByLevel[spec.levelNumber] ?: 0) > 0
        }
      }
    }
  val activeWorldIdx =
    remember(progress.unlockedLevel) {
      ((LevelCatalog.levels.getOrNull((progress.unlockedLevel - 1).coerceIn(0, totalLevels - 1))?.world ?: 1) - 1)
        .coerceIn(0, 3)
    }

  val recentSessionBars =
    remember(progress.bestScoreByLevel, progress.dailyBestScore) {
      val sorted =
        progress.bestScoreByLevel.entries
          .sortedBy { it.key }
          .map { it.value.toFloat() }
          .takeLast(7)
      val padded =
        if (sorted.size < 7) List(7 - sorted.size) { 0f } + sorted else sorted
      if (progress.dailyBestScore > 0 && padded.lastOrNull() == 0f) {
        padded.dropLast(1) + progress.dailyBestScore.toFloat()
      } else {
        padded
      }
    }
  val activeBarIdx = recentSessionBars.indexOfLast { it > 0f }.coerceAtLeast(6)

  val activeModeLabel =
    when (settings.gameMode) {
      GameMode.KIDS -> strings.modeKids
      GameMode.CLASSIC -> strings.modeClassic
      GameMode.HARD -> strings.modeHard
    }
  val activeModeDesc =
    when (settings.gameMode) {
      GameMode.KIDS -> strings.modeKidsDesc
      GameMode.CLASSIC -> strings.modeClassicDesc
      GameMode.HARD -> strings.modeHardDesc
    }

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier
          .widthIn(max = 560.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceLg),
      verticalArrangement = Arrangement.spacedBy(Dimens.CardGap),
    ) {
      // Header: clean readable Arabic/Latin typography (no dot-matrix body text)
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).widgetStaggerEntrance(0),
        verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        Text(
          text = strings.appTitle,
          style = MaterialTheme.typography.titleLarge,
          fontSize = Dimens.TitleSp,
          fontWeight = FontWeight.Bold,
          color = palette.textHigh,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = strings.appSubtitle,
          style = MaterialTheme.typography.bodyMedium,
          fontSize = Dimens.CaptionSp,
          color = palette.textMed,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      // 1. Today's Letter: HeroGlyphCard 2x1
      HeroGlyphCard(
        title = strings.dailyChallenge,
        subtitle =
          "${dailySpec.levelSpec.letter.localizedName(settings.language)} · ${dailySpec.levelSpec.fontSpec.localizedName(settings.language)}",
        glyph = dailySpec.levelSpec.letter.glyph,
        glyphName = dailySpec.levelSpec.letter.localizedName(settings.language),
        fontSpec = dailySpec.levelSpec.fontSpec,
        chipIcon = if (todayDone) PixelIcon.CHECK else PixelIcon.DAILY,
        chipUnit = if (todayDone) strings.unitDoneShort else strings.unitStreakDays,
        streakDays = progress.dailyStreak,
        todayCompleted = todayDone,
        stripStartLabel = "1",
        stripEndLabel = "7",
        stripSemanticSummary = strings.streakSemantic(progress.dailyStreak),
        hapticsEnabled = settings.hapticsEnabled,
        onClick = onPlayDaily,
        modifier = Modifier.widgetStaggerEntrance(1).testTag("daily_button"),
      )

      // 2. Two 1x1 cards: Progress + Stars
      Row(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(2),
        horizontalArrangement = Arrangement.spacedBy(Dimens.CardGap),
      ) {
        StatCard(
          title = strings.progressLabel,
          subtitle = "$completionPercent%",
          heroText = "$completedCount",
          chipIcon = PixelIcon.MAP,
          chipUnit = "/$totalLevels",
          strip =
            StripSpec.Ticks(
              progress = completedCount,
              total = totalLevels,
              currentIndex = (progress.unlockedLevel - 1).coerceIn(0, totalLevels - 1),
              startLabel = "1",
              endLabel = "$totalLevels",
              semanticSummary = strings.progressSemantic(completedCount, totalLevels),
            ),
          cardSize = CardSize.SQUARE_1X1,
          hapticsEnabled = settings.hapticsEnabled,
          onClick = onOpenMap,
          modifier = Modifier.weight(1f).testTag("progress_widget"),
        )

        StatCard(
          title = strings.starsLabel,
          subtitle = "$totalStars / $maxStars",
          heroText = "$totalStars",
          chipIcon = PixelIcon.STAR,
          chipUnit = "/$maxStars",
          strip =
            StripSpec.Dots(
              count = 4,
              filledCount = worldCompletionCount,
              activeIndex = activeWorldIdx,
              startLabel = "W1",
              endLabel = "W4",
              semanticSummary = strings.starsSemantic(totalStars, maxStars),
            ),
          cardSize = CardSize.SQUARE_1X1,
          hapticsEnabled = settings.hapticsEnabled,
          onClick = onOpenMap,
          modifier = Modifier.weight(1f).testTag("stars_widget"),
        )
      }

      // 3. Best Score: 2x1 card with BarStrip of last 7 sessions
      StatCard(
        title = strings.bestLabel,
        subtitle = strings.levelBadge(progress.unlockedLevel),
        heroText = "$bestOverallScore",
        chipIcon = PixelIcon.TROPHY,
        chipUnit = strings.unitPoints,
        strip =
          StripSpec.Bars(
            values = recentSessionBars,
            activeIndex = activeBarIdx,
            startLabel = "S1",
            endLabel = "S7",
            semanticSummary = strings.bestScoreSemantic(bestOverallScore),
          ),
        cardSize = CardSize.WIDE_2X1,
        hapticsEnabled = settings.hapticsEnabled,
        modifier = Modifier.widgetStaggerEntrance(3),
      )

      // 4. Mode as a small wide card (title "الوضع", subtitle mode name, expandable pills)
      FrostedCard(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(4).testTag("mode_pill_button"),
        hapticsEnabled = settings.hapticsEnabled,
        onClick = { showModesPanel = !showModesPanel },
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(Dimens.CardPadding),
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(
              modifier = Modifier.weight(1f).padding(end = Dimens.SpaceSm),
              verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
              Text(
                text = strings.modesTitle,
                style = MaterialTheme.typography.titleMedium,
                fontSize = Dimens.CardTitleSp,
                fontWeight = FontWeight.Medium,
                color = palette.textHigh,
              )
              Text(
                text = activeModeLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = Dimens.CaptionSp,
                fontWeight = FontWeight.SemiBold,
                color = palette.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
            PixelIcon(
              type = PixelIcon.MODES,
              size = 24.dp,
              isActive = showModesPanel,
              contentDescription = strings.modesTitle,
            )
          }

          AnimatedVisibility(
            visible = showModesPanel,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(top = Dimens.SpaceXs),
              verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
              ) {
                ModePillOption(
                  label = strings.modeKids,
                  selected = settings.gameMode == GameMode.KIDS,
                  onClick = { onSelectMode(GameMode.KIDS) },
                  modifier = Modifier.weight(1f).testTag("mode_kids"),
                )
                ModePillOption(
                  label = strings.modeClassic,
                  selected = settings.gameMode == GameMode.CLASSIC,
                  onClick = { onSelectMode(GameMode.CLASSIC) },
                  modifier = Modifier.weight(1f).testTag("mode_classic"),
                )
                ModePillOption(
                  label = strings.modeHard,
                  selected = settings.gameMode == GameMode.HARD,
                  onClick = { onSelectMode(GameMode.HARD) },
                  modifier = Modifier.weight(1f).testTag("mode_hard"),
                )
              }
              Text(
                text = activeModeDesc,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = Dimens.CaptionSp,
                color = palette.textMed,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceXs))

      // 5. PrimaryButton (max ONE primary button on screen)
      PrimaryButton(
        label = strings.playWord,
        leadingIcon = PixelIcon.PLAY,
        trailingDotValue = "${progress.unlockedLevel}",
        hapticsEnabled = settings.hapticsEnabled,
        onClick = { onPlayContinue(progress.unlockedLevel) },
        modifier = Modifier.widgetStaggerEntrance(5).testTag("play_button"),
      )

      // 6. Bottom Nav Row of 4 GlassIconButtons
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.SpaceXs).widgetStaggerEntrance(6),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
      ) {
        GlassIconButton(
          icon = PixelIcon.MAP,
          contentDescription = strings.levelMap,
          label = strings.levelMap,
          onClick = onOpenMap,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f).testTag("map_button"),
        )
        GlassIconButton(
          icon = PixelIcon.MODES,
          contentDescription = strings.modesTitle,
          label = strings.modesTitle,
          isActive = showModesPanel,
          onClick = { showModesPanel = !showModesPanel },
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f).testTag("modes_button"),
        )
        GlassIconButton(
          icon = PixelIcon.SETTINGS,
          contentDescription = strings.settings,
          label = strings.settings,
          onClick = onOpenSettings,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f).testTag("settings_button"),
        )
        GlassIconButton(
          icon = if (settings.soundEnabled) PixelIcon.SOUND_ON else PixelIcon.SOUND_OFF,
          contentDescription = strings.soundLabel,
          label = strings.soundLabel,
          isActive = settings.soundEnabled,
          onClick = onToggleSound,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f).testTag("home_sound_button"),
        )
      }
    }
  }
}

@Composable
private fun ModePillOption(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val palette = LocalPalette.current
  val interactionSource = remember { MutableInteractionSource() }
  Box(
    modifier =
      modifier
        .height(48.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(if (selected) palette.accent else palette.unitChipBg)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick,
        )
        .padding(horizontal = Dimens.SpaceSm),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge,
      fontSize = Dimens.CaptionSp,
      fontWeight = FontWeight.SemiBold,
      color = if (selected) com.example.ui.theme.TextHighLight else palette.textHigh,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
