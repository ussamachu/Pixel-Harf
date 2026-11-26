package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.DotText
import com.example.ui.components.FlatCard
import com.example.ui.components.HairlineDivider
import com.example.ui.components.PixelIcon
import com.example.ui.components.UiAppearanceState
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AppShapes
import com.example.ui.theme.IconTint
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
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
  val palette = LocalPalette.current
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

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 540.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceLg),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      // 1. Pixel wordmark (single DotText on Home) + one-line subtitle
      Column(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(0),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
      ) {
        DotText(
          text = strings.appTitle,
          fontResId = R.font.noto_kufi_arabic,
          dotSize = Dimens.DotWordmarkSize,
          color = palette.ink,
          targetRows = 14,
        )
        Text(
          text = strings.appSubtitle,
          style = MaterialTheme.typography.bodyLarge,
          fontSize = Dimens.BodySp,
          color = palette.mutedInk,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceXl))

      // 2. Today's Letter as a large flat card (8dp radius, 1dp hairline border; tap = daily)
      FlatCard(
        modifier =
          Modifier.fillMaxWidth()
            .weight(1f, fill = true)
            .widgetStaggerEntrance(1)
            .testTag("daily_button"),
        shape = AppShapes.Card,
        hapticsEnabled = settings.hapticsEnabled,
        onClick = onPlayDaily,
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              PixelIcon(
                type = if (todayDone) PixelIcon.CHECK else PixelIcon.DAILY,
                size = 16.dp,
                isSuccess = todayDone,
              )
              Text(
                text = strings.dailyChallenge,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                fontWeight = FontWeight.SemiBold,
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
            )
          }

          AnimatedContent(
            targetState = pulseLetters[pulseIdx],
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
            label = "daily_glyph_pulse",
          ) { glyph ->
            Text(
              text = glyph,
              fontFamily = AmiriFamily,
              fontSize = 84.sp,
              color = palette.ink,
            )
          }

          Text(
            text = dailySpec.levelSpec.letter.localizedName(settings.language),
            style = MaterialTheme.typography.titleMedium,
            fontSize = Dimens.TitleSp,
            fontWeight = FontWeight.SemiBold,
            color = palette.ink,
          )
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceXl))

      // 3. Rows for Mode, Progress (thin orange bar), and Stars / Best score (flat hairline container)
      FlatCard(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(2),
        shape = AppShapes.Card,
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Mode row
          FlatCard(
            modifier = Modifier.fillMaxWidth().testTag("mode_pill_button"),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            borderWidth = 0.dp,
            borderColor = Color.Transparent,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = { showModesPanel = !showModesPanel },
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.SpaceLg, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = strings.modesTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.mutedInk,
              )
              Text(
                text = activeModeLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                fontWeight = FontWeight.SemiBold,
                color = palette.ink,
              )
            }
          }

          AnimatedVisibility(
            visible = showModesPanel,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceSm),
              verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
              ) {
                ModeRowOption(
                  label = strings.modeKids,
                  selected = settings.gameMode == GameMode.KIDS,
                  onClick = { onSelectMode(GameMode.KIDS) },
                  hapticsEnabled = settings.hapticsEnabled,
                  modifier = Modifier.weight(1f).testTag("mode_kids"),
                )
                ModeRowOption(
                  label = strings.modeClassic,
                  selected = settings.gameMode == GameMode.CLASSIC,
                  onClick = { onSelectMode(GameMode.CLASSIC) },
                  hapticsEnabled = settings.hapticsEnabled,
                  modifier = Modifier.weight(1f).testTag("mode_classic"),
                )
                ModeRowOption(
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

          HairlineDivider(modifier = Modifier.fillMaxWidth())

          // Progress row with thin orange bar
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .padding(horizontal = Dimens.SpaceLg, vertical = 12.dp)
                .testTag("progress_widget"),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = strings.progressLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.mutedInk,
              )
              Text(
                text = "$completionPercent%  ($completedCount/$totalLevels)",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.CaptionSp,
                color = palette.ink,
              )
            }
            LinearProgressIndicator(
              progress = { completionRatio },
              modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
              color = palette.accent,
              trackColor = palette.canvas,
            )
          }

          HairlineDivider(modifier = Modifier.fillMaxWidth())

          // Stars + Best Score row
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.SpaceLg, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
            ) {
              PixelIcon(
                type = PixelIcon.STAR,
                size = 16.dp,
                contentDescription = strings.starsLabel,
              )
              Text(
                text = strings.starsLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.mutedInk,
              )
              Text(
                text = "$totalStars",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.BodySp,
                color = palette.ink,
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              Text(
                text = strings.bestLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.mutedInk,
              )
              Text(
                text = "$bestOverallScore",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = Dimens.BodySp,
                color = palette.ink,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceLg))

      // 4. Primary Orange Play Button «العب · المستوى N» (8dp radius, #F57C00 / pressed #E65100)
      FlatCard(
        modifier =
          Modifier.fillMaxWidth()
            .height(Dimens.PrimaryPlayHeight)
            .widgetStaggerEntrance(3)
            .testTag("play_button"),
        shape = AppShapes.Button,
        backgroundColor = palette.accent,
        pressedBackgroundColor = palette.accentPressed,
        borderColor = palette.accent,
        hapticsEnabled = settings.hapticsEnabled,
        onClick = { onPlayContinue(progress.unlockedLevel) },
      ) {
        Row(
          modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceLg),
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
              fontSize = Dimens.TitleSp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
            )
            Text(
              text = "·",
              fontFamily = JetBrainsMonoFamily,
              fontWeight = FontWeight.Bold,
              fontSize = Dimens.BodySp,
              color = Color.White.copy(alpha = 0.85f),
            )
            Text(
              text = strings.levelBadge(progress.unlockedLevel),
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              color = Color.White.copy(alpha = 0.94f),
            )
          }
          PixelIcon(
            type = PixelIcon.ARROW_RIGHT,
            size = 24.dp,
            tint = IconTint.Color(Color.White),
            mirrorForRtl = true,
          )
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceLg))

      HairlineDivider(modifier = Modifier.fillMaxWidth())

      // 5. Bottom nav of 4 items (Map, Modes, Settings, Sound), pixel icon + label
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.SpaceMd).widgetStaggerEntrance(4),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
      ) {
        HomeNavItem(
          icon = PixelIcon.MAP,
          label = strings.levelMap,
          onClick = onOpenMap,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "map_button",
        )
        HomeNavItem(
          icon = PixelIcon.MODES,
          label = strings.modesTitle,
          isActive = showModesPanel,
          onClick = { showModesPanel = !showModesPanel },
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "modes_button",
        )
        HomeNavItem(
          icon = PixelIcon.SETTINGS,
          label = strings.settings,
          onClick = onOpenSettings,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.weight(1f),
          buttonTag = "settings_button",
        )
        HomeNavItem(
          icon = if (settings.soundEnabled) PixelIcon.SOUND_ON else PixelIcon.SOUND_OFF,
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
private fun HomeNavItem(
  icon: PixelIcon,
  label: String,
  onClick: () -> Unit,
  hapticsEnabled: Boolean,
  buttonTag: String,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
) {
  val palette = LocalPalette.current
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
      iconSize = 24.dp,
      isActive = isActive,
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
private fun ModeRowOption(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  hapticsEnabled: Boolean,
  modifier: Modifier = Modifier,
) {
  val palette = LocalPalette.current
  FlatCard(
    modifier = modifier.height(Dimens.ButtonHeight),
    shape = AppShapes.Button,
    backgroundColor = if (selected) palette.accent else palette.canvas,
    pressedBackgroundColor = if (selected) palette.accentPressed else palette.surfacePressed,
    borderColor = if (selected) palette.accent else palette.hairline,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontSize = Dimens.BodySp,
        color = if (selected) Color.White else palette.ink,
        maxLines = 1,
      )
    }
  }
}
