package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.GameMode
import com.example.model.LevelProgress
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.CircleButton
import com.example.ui.components.DotDigitText
import com.example.ui.components.DotIcon
import com.example.ui.components.DotIconType
import com.example.ui.components.DotRing
import com.example.ui.components.DotStarRow
import com.example.ui.components.DotText
import com.example.ui.components.UiAppearanceState
import com.example.ui.components.WidgetCard
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.WidgetRadius
import kotlinx.coroutines.delay

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
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current
  val dailySpec = remember { LevelCatalog.dailyChallenge() }
  val todayDone = progress.dailyCompletedDate == LevelCatalog.todayKey()

  val pulseLetters =
    remember(dailySpec.levelSpec.letter.glyph) {
      listOf(dailySpec.levelSpec.letter.glyph, "ن", "ق", "ش", "ع", "ب")
    }
  var pulseIdx by remember { mutableIntStateOf(0) }
  LaunchedEffect(appearance.glyphPulseEnabled, reduceMotion) {
    if (appearance.glyphPulseEnabled && !reduceMotion) {
      while (true) {
        delay(2800L)
        pulseIdx = (pulseIdx + 1) % pulseLetters.size
      }
    } else {
      pulseIdx = 0
    }
  }

  var showModesPanel by remember { mutableStateOf(false) }

  val totalLevels = LevelCatalog.levels.size.coerceAtLeast(1)
  val completedCount = progress.starsByLevel.count { it.value > 0 }
  val completionRatio = completedCount.toFloat() / totalLevels.toFloat()
  val completionPercent = ((completionRatio * 100f).toInt()).coerceIn(0, 100)
  val totalStars = progress.starsByLevel.values.sum()
  val bestOverallScore =
    maxOf(progress.dailyBestScore, progress.bestScoreByLevel.values.maxOrNull() ?: 0)

  val activeModeLabel =
    when (settings.gameMode) {
      GameMode.KIDS -> strings.modeKids
      GameMode.CLASSIC -> strings.modeClassic
      GameMode.HARD -> strings.modeHard
    }

  var arrowTick by remember { mutableIntStateOf(0) }
  LaunchedEffect(reduceMotion) {
    if (!reduceMotion) {
      while (true) {
        delay(700L)
        arrowTick = (arrowTick + 1) % 3
      }
    }
  }
  val arrowShift by
    animateFloatAsState(
      targetValue = if (arrowTick == 1) 4f else 0f,
      animationSpec = tween(220),
      label = "play_arrow_shift",
    )

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 540.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceMd),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // 1. Top Header: Small DotText logo (max 28sp) + horizontal Mode pill + 1-line subtitle
      Column(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(0),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Single DotText seasoning on Home (<= 28sp height)
          DotText(
            text = strings.appTitle,
            fontResId = R.font.noto_kufi_arabic,
            dotSize = Dimens.DotTitleDotSize,
            color = palette.ink,
            targetRows = 14,
          )
          // Horizontal Mode pill — never wraps or stacks vertically
          WidgetCard(
            shape = WidgetRadius.Pill,
            cornerRadiusDp = 24.dp,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = { showModesPanel = !showModesPanel },
            modifier = Modifier.testTag("mode_pill_button"),
          ) {
            Text(
              text = strings.modePillLabel(activeModeLabel),
              style = MaterialTheme.typography.bodyMedium,
              fontSize = Dimens.CaptionSp,
              fontWeight = FontWeight.SemiBold,
              color = palette.ink,
              maxLines = 1,
              softWrap = false,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            )
          }
        }
        Text(
          text = strings.appSubtitle,
          style = MaterialTheme.typography.bodyLarge,
          fontSize = Dimens.BodySp,
          color = palette.mutedInk,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      // 2. Centered Today's Letter Circle Card (Daily Challenge)
      WidgetCard(
        modifier =
          Modifier.size(Dimens.DailyCircleSize)
            .widgetStaggerEntrance(1)
            .testTag("daily_button"),
        shape = WidgetRadius.Circle,
        cornerRadiusDp = Dimens.DailyCircleSize / 2,
        hapticsEnabled = settings.hapticsEnabled,
        onClick = onPlayDaily,
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(Dimens.SpaceMd),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            DotIcon(
              type = if (todayDone) DotIconType.CHECK else DotIconType.DAILY,
              dotSize = 1.4.dp,
              color = palette.mutedInk,
            )
            Text(
              text = strings.dailyChallenge,
              style = MaterialTheme.typography.bodyMedium,
              fontSize = Dimens.CaptionSp,
              color = palette.mutedInk,
              maxLines = 1,
            )
          }

          AnimatedContent(
            targetState = pulseLetters[pulseIdx],
            transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(180)) },
            label = "daily_glyph_pulse",
          ) { glyph ->
            Text(
              text = glyph,
              fontFamily = AmiriFamily,
              fontSize = 54.sp,
              color = palette.ink,
            )
          }

          Text(
            text =
              if (todayDone) strings.dailyCompleted
              else if (progress.dailyStreak > 0) strings.dailyStreak(progress.dailyStreak)
              else dailySpec.levelSpec.fontSpec.localizedName(settings.language),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = Dimens.CaptionSp,
            color = palette.mutedInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      // 3. Progress Ring Card + Stats Pill Row
      Row(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(2),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        WidgetCard(
          modifier = Modifier.weight(1f).height(96.dp).testTag("progress_widget"),
          shape = WidgetRadius.Card,
          cornerRadiusDp = Dimens.CardRadius,
          hapticsEnabled = settings.hapticsEnabled,
          onClick = onOpenMap,
        ) {
          Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
          ) {
            Box(contentAlignment = Alignment.Center) {
              DotRing(
                progress = completionRatio,
                size = 66.dp,
                dotCount = 24,
                dotSize = 2.6.dp,
                activeColor = palette.ink,
                inactiveColor = palette.faintDot,
              )
              DotDigitText(
                value = "$completionPercent%",
                fontSize = 14.sp,
                color = palette.ink,
              )
            }
            Text(
              text = strings.progressLabel,
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              fontWeight = FontWeight.SemiBold,
              color = palette.ink,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        WidgetCard(
          modifier = Modifier.weight(1f).height(96.dp),
          shape = WidgetRadius.Card,
          cornerRadiusDp = Dimens.CardRadius,
        ) {
          Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
            verticalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              DotStarRow(stars = 1, dotSize = 1.8.dp, spacing = 0.dp, filledColor = palette.accent)
              Text(
                text = "$totalStars",
                fontFamily = DotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = palette.ink,
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = strings.bestLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = Dimens.CaptionSp,
                color = palette.mutedInk,
                maxLines = 1,
              )
              Text(
                text = "$bestOverallScore",
                fontFamily = DotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = palette.ink,
              )
            }
          }
        }
      }

      // 4. Primary Orange Play Button: «العب» 20sp bold + «المستوى N» 14sp (Latin digits)
      WidgetCard(
        modifier =
          Modifier.fillMaxWidth()
            .height(Dimens.PrimaryPlayHeight)
            .widgetStaggerEntrance(3)
            .testTag("play_button"),
        shape = WidgetRadius.Pill,
        cornerRadiusDp = 28.dp,
        backgroundColor = palette.accent,
        hapticsEnabled = settings.hapticsEnabled,
        onClick = { onPlayContinue(progress.unlockedLevel) },
      ) {
        Row(
          modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceXl),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            Text(
              text = strings.playWord,
              style = MaterialTheme.typography.titleLarge,
              fontSize = Dimens.PlayTitleSp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
            )
            Text(
              text = "·  ${strings.levelBadge(progress.unlockedLevel)}",
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              color = Color.White.copy(alpha = 0.92f),
            )
          }
          DotIcon(
            type = DotIconType.ARROW_RIGHT,
            dotSize = 2.0.dp,
            color = Color.White,
            mirrorForRtl = true,
            modifier = Modifier.offset(x = arrowShift.dp),
          )
        }
      }

      // Inline Mode Picker (opens from Mode pill or Modes circle button)
      AnimatedVisibility(
        visible = showModesPanel,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
        WidgetCard(
          modifier = Modifier.fillMaxWidth(),
          shape = WidgetRadius.Card,
          cornerRadiusDp = Dimens.CardRadius,
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(Dimens.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              ModeWidgetPill(
                label = strings.modeKids,
                selected = settings.gameMode == GameMode.KIDS,
                onClick = { onSelectMode(GameMode.KIDS) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("mode_kids"),
              )
              ModeWidgetPill(
                label = strings.modeClassic,
                selected = settings.gameMode == GameMode.CLASSIC,
                onClick = { onSelectMode(GameMode.CLASSIC) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("mode_classic"),
              )
              ModeWidgetPill(
                label = strings.modeHard,
                selected = settings.gameMode == GameMode.HARD,
                onClick = { onSelectMode(GameMode.HARD) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("mode_hard"),
              )
            }
            val desc =
              when (settings.gameMode) {
                GameMode.KIDS -> strings.modeKidsDesc
                GameMode.CLASSIC -> strings.modeClassicDesc
                GameMode.HARD -> strings.modeHardDesc
              }
            Text(
              text = desc,
              style = MaterialTheme.typography.bodyMedium,
              fontSize = Dimens.CaptionSp,
              color = palette.mutedInk,
            )
          }
        }
      }

      // 5. Bottom Row: 4 Consistent Circle Buttons (icon + label under each, same size)
      Row(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(4),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
      ) {
        DockCircleAction(
          icon = DotIconType.MAP,
          label = strings.levelMap,
          onClick = onOpenMap,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "map_button",
        )
        DockCircleAction(
          icon = DotIconType.MODES,
          label = strings.modesTitle,
          onClick = { showModesPanel = !showModesPanel },
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "modes_button",
        )
        DockCircleAction(
          icon = DotIconType.SETTINGS,
          label = strings.settings,
          onClick = onOpenSettings,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "settings_button",
        )
        DockCircleAction(
          icon = if (settings.soundEnabled) DotIconType.SOUND_ON else DotIconType.SOUND_OFF,
          label = strings.soundLabel,
          onClick = onToggleSound,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "home_sound_button",
        )
      }
    }
  }
}

@Composable
private fun DockCircleAction(
  icon: DotIconType,
  label: String,
  onClick: () -> Unit,
  hapticsEnabled: Boolean,
  buttonTag: String,
  modifier: Modifier = Modifier,
) {
  val palette = LocalNothingPalette.current
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
  ) {
    CircleButton(
      iconType = icon,
      contentDescription = label,
      onClick = onClick,
      size = Dimens.CircleButtonSize,
      dotSize = 2.2.dp,
      hapticsEnabled = hapticsEnabled,
      modifier = Modifier.testTag(buttonTag),
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      fontSize = Dimens.CaptionSp,
      color = palette.ink,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun ModeWidgetPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  hapticsEnabled: Boolean,
  modifier: Modifier = Modifier,
) {
  val palette = LocalNothingPalette.current
  WidgetCard(
    modifier = modifier.height(Dimens.ButtonHeight),
    shape = WidgetRadius.Pill,
    cornerRadiusDp = 24.dp,
    backgroundColor = if (selected) palette.ink else palette.widgetPressed,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontSize = Dimens.BodySp,
        color = if (selected) palette.widgetSurface else palette.ink,
        maxLines = 1,
      )
    }
  }
}
