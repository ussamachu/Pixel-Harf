package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.DotIcon
import com.example.ui.components.DotIconType
import com.example.ui.components.WidgetCard
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.LocalReduceMotion
import com.example.ui.theme.WidgetRadius
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CalligraphicDissolveOverlay(
  gameState: GameState,
  settings: UserSettings,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current

  val visible =
    gameState.status == GameStatus.LEVEL_COMPLETE && gameState.calligraphicRevealActive

  val assembleAnim = remember(visible) { Animatable(if (reduceMotion) 1f else 0f) }
  LaunchedEffect(visible, reduceMotion) {
    if (visible && !reduceMotion) {
      assembleAnim.snapTo(0f)
      assembleAnim.animateTo(1f, animationSpec = tween(durationMillis = 720))
    } else if (visible) {
      assembleAnim.snapTo(1f)
    }
  }

  val dotVectors = remember {
    val rnd = Random(77)
    FloatArray(48) { idx ->
      if (idx % 2 == 0) rnd.nextFloat() * 6.283f else 0.35f + rnd.nextFloat() * 0.65f
    }
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(150)),
    exit = fadeOut(tween(180)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
      contentAlignment = Alignment.Center,
    ) {
      WidgetCard(
        modifier = Modifier.padding(Dimens.SpaceLg),
        shape = WidgetRadius.Card,
        cornerRadiusDp = Dimens.DialogRadius,
      ) {
        Box(
          modifier = Modifier.padding(horizontal = 36.dp, vertical = 24.dp),
          contentAlignment = Alignment.Center,
        ) {
          val t = assembleAnim.value
          val dotAlpha = (1f - t).coerceIn(0f, 1f)
          if (dotAlpha > 0.02f) {
            Canvas(modifier = Modifier.size(140.dp)) {
              val cx = size.width * 0.5f
              val cy = size.height * 0.5f
              val maxSpread = size.minDimension * 0.45f
              var i = 0
              while (i + 1 < dotVectors.size) {
                val angle = dotVectors[i]
                val dist = dotVectors[i + 1] * maxSpread * (1f - t)
                val x = cx + cos(angle) * dist
                val y = cy + sin(angle) * dist
                drawCircle(
                  color = (if (i % 4 == 0) palette.accent else palette.ink).copy(alpha = dotAlpha),
                  radius = 3.5.dp.toPx(),
                  center = Offset(x, y),
                )
                i += 2
              }
            }
          }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { alpha = (0.25f + 0.75f * t).coerceIn(0f, 1f) },
          ) {
            Text(
              text = gameState.levelSpec.letter.isolatedGlyph,
              fontFamily = AmiriFamily,
              fontSize = 84.sp,
              color = palette.ink,
            )
            Text(
              text = gameState.levelSpec.letter.localizedName(settings.language),
              style = MaterialTheme.typography.titleLarge,
              color = palette.accent,
            )
          }
        }
      }
    }
  }
}

@Composable
fun LevelCompleteOverlay(
  gameState: GameState,
  settings: UserSettings,
  strings: AppStrings,
  onNextLevel: (Int) -> Unit,
  onRestart: () -> Unit,
  onOpenMap: () -> Unit,
  onReplayLetterAudio: (String) -> Unit,
) {
  val palette = LocalNothingPalette.current
  val reduceMotion = LocalReduceMotion.current
  val visible =
    gameState.status == GameStatus.LEVEL_COMPLETE && !gameState.calligraphicRevealActive

  var revealedStars by remember(visible, gameState.starsEarned) {
    mutableIntStateOf(if (reduceMotion) gameState.starsEarned else 0)
  }
  LaunchedEffect(visible, gameState.starsEarned, reduceMotion) {
    if (visible) {
      if (reduceMotion) {
        revealedStars = gameState.starsEarned
      } else {
        revealedStars = 0
        for (s in 1..gameState.starsEarned) {
          delay(190L)
          revealedStars = s
        }
      }
    }
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(180)),
    exit = fadeOut(tween(140)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)),
      contentAlignment = Alignment.Center,
    ) {
      WidgetCard(
        modifier = Modifier.fillMaxWidth(0.84f).widthIn(max = 360.dp),
        shape = WidgetRadius.Card,
        cornerRadiusDp = Dimens.DialogRadius,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          // Title «أحسنت!» at 28sp + TTS audio icon
          Box(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = strings.levelCompleteTitle,
              style = MaterialTheme.typography.headlineMedium,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = palette.ink,
              modifier = Modifier.align(Alignment.Center),
            )
            IconButton(
              onClick = { onReplayLetterAudio(gameState.levelSpec.letter.nameAr) },
              modifier = Modifier.align(Alignment.CenterEnd).minimumInteractiveComponentSize(),
            ) {
              Icon(
                imageVector = Icons.Rounded.VolumeUp,
                contentDescription = strings.settingsTts,
                tint = palette.ink,
              )
            }
          }

          // Letter + Name
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = gameState.levelSpec.letter.isolatedGlyph,
              fontFamily = AmiriFamily,
              fontSize = 56.sp,
              color = palette.ink,
            )
            Text(
              text = gameState.levelSpec.letter.localizedName(settings.language),
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              color = palette.mutedInk,
            )
          }

          // 3 Orange Filled Stars
          Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
            for (i in 1..3) {
              val filled = i <= revealedStars
              Icon(
                imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = if (filled) palette.accent else palette.faintDot,
                modifier = Modifier.size(34.dp),
              )
            }
          }

          // Score
          Text(
            text = "${strings.scoreLabel}  ${gameState.score}",
            fontFamily = DotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = palette.ink,
          )

          // Primary Next button + 2 secondary buttons (no scrolling)
          val hasNext =
            !gameState.isDailyChallenge && gameState.levelSpec.levelNumber < LevelCatalog.levels.size
          if (hasNext) {
            DialogActionButton(
              label = strings.nextLevel,
              isPrimaryOrange = true,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = { onNextLevel(gameState.levelSpec.levelNumber + 1) },
              modifier = Modifier.fillMaxWidth().testTag("next_level_button"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
          ) {
            DialogActionButton(
              label = strings.replay,
              isPrimaryOrange = !hasNext,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = onRestart,
              modifier = Modifier.weight(1f).testTag("replay_button"),
            )
            DialogActionButton(
              label = strings.backToMap,
              isPrimaryOrange = false,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = onOpenMap,
              modifier = Modifier.weight(1f).testTag("complete_map_button"),
            )
          }
        }
      }
    }
  }
}

@Composable
fun PausedOverlay(
  gameState: GameState,
  settings: UserSettings,
  strings: AppStrings,
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onOpenMap: () -> Unit,
  onBackHome: () -> Unit,
) {
  val palette = LocalNothingPalette.current

  AnimatedVisibility(
    visible = gameState.status == GameStatus.PAUSED,
    enter = fadeIn(tween(150)),
    exit = fadeOut(tween(140)),
  ) {
    // Dim the game behind the compact dialog instead of hiding the board
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(Color.Black.copy(alpha = 0.48f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {},
          ),
      contentAlignment = Alignment.Center,
    ) {
      WidgetCard(
        modifier = Modifier.fillMaxWidth(0.70f).widthIn(max = 310.dp),
        shape = WidgetRadius.Card,
        cornerRadiusDp = Dimens.DialogRadius,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          // Title «متوقف» in readable font 22sp (no giant dot text)
          Text(
            text = strings.pausedTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = Dimens.PauseTitleSp,
            fontWeight = FontWeight.Bold,
            color = palette.ink,
          )

          // 4 buttons in order: استئناف (orange), إعادة, الخريطة, الرئيسية — same width, 48dp tall, 12dp spacing
          DialogActionButton(
            label = strings.resume,
            isPrimaryOrange = true,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onResume,
            modifier = Modifier.fillMaxWidth().testTag("resume_button"),
          )
          DialogActionButton(
            label = strings.restart,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().testTag("pause_restart_button"),
          )
          DialogActionButton(
            label = strings.backToMap,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onOpenMap,
            modifier = Modifier.fillMaxWidth().testTag("pause_map_button"),
          )
          DialogActionButton(
            label = strings.backHome,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth().testTag("pause_home_button"),
          )
        }
      }
    }
  }
}

@Composable
fun GameOverOverlay(
  gameState: GameState,
  settings: UserSettings,
  strings: AppStrings,
  onRestart: () -> Unit,
  onOpenMap: () -> Unit,
) {
  val palette = LocalNothingPalette.current

  AnimatedVisibility(
    visible = gameState.status == GameStatus.GAME_OVER,
    enter = fadeIn(tween(160)),
    exit = fadeOut(tween(140)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)),
      contentAlignment = Alignment.Center,
    ) {
      WidgetCard(
        modifier = Modifier.fillMaxWidth(0.76f).widthIn(max = 320.dp),
        shape = WidgetRadius.Card,
        cornerRadiusDp = Dimens.DialogRadius,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          Text(
            text = strings.gameOverTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = Dimens.PauseTitleSp,
            fontWeight = FontWeight.Bold,
            color = palette.ink,
          )
          Text(
            text = strings.deathReasonLabel(gameState.deathReason),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = Dimens.BodySp,
            color = palette.accent,
          )
          Text(
            text = "${strings.scoreLabel}  ${gameState.score}",
            fontFamily = DotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = palette.ink,
          )
          DialogActionButton(
            label = strings.restart,
            isPrimaryOrange = true,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().testTag("retry_button"),
          )
          DialogActionButton(
            label = strings.backToMap,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onOpenMap,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

/**
 * One-time dismissible coachmark on first game pointing to Pause & Power-ups.
 */
@Composable
fun GameCoachmarkOverlay(
  visible: Boolean,
  strings: AppStrings,
  hapticsEnabled: Boolean,
  onDismiss: () -> Unit,
) {
  val palette = LocalNothingPalette.current

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(180)),
    exit = fadeOut(tween(150)),
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(Color.Black.copy(alpha = 0.55f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss,
          )
          .padding(Dimens.ScreenPadding),
    ) {
      Column(
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        WidgetCard(
          modifier = Modifier.fillMaxWidth().widthIn(max = 360.dp),
          shape = WidgetRadius.Card,
          cornerRadiusDp = Dimens.DialogRadius,
        ) {
          Column(
            modifier = Modifier.padding(Dimens.SpaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
            ) {
              DotIcon(type = DotIconType.PAUSE, dotSize = 2.0.dp, color = palette.accent)
              Text(
                text = strings.coachmarkPauseTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.ink,
                modifier = Modifier.weight(1f),
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
            ) {
              DotIcon(type = DotIconType.SLOW_MO, dotSize = 2.0.dp, color = palette.accent)
              Text(
                text = strings.coachmarkPowerUpDesc,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = Dimens.BodySp,
                color = palette.ink,
                modifier = Modifier.weight(1f),
              )
            }
            DialogActionButton(
              label = strings.coachmarkDismiss,
              isPrimaryOrange = true,
              hapticsEnabled = hapticsEnabled,
              onClick = onDismiss,
              modifier = Modifier.fillMaxWidth().testTag("coachmark_dismiss_button"),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun DialogActionButton(
  label: String,
  isPrimaryOrange: Boolean,
  hapticsEnabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val palette = LocalNothingPalette.current
  WidgetCard(
    modifier = modifier.height(Dimens.ButtonHeight),
    shape = WidgetRadius.Pill,
    cornerRadiusDp = 24.dp,
    backgroundColor = if (isPrimaryOrange) palette.accent else palette.widgetPressed,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontSize = Dimens.ButtonSp,
        fontWeight = FontWeight.SemiBold,
        color = if (isPrimaryOrange) Color.White else palette.ink,
        textAlign = TextAlign.Center,
        maxLines = 1,
      )
    }
  }
}
