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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.DotText
import com.example.ui.components.FlatButton
import com.example.ui.components.FlatCard
import com.example.ui.components.PixelIcon
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.AppShapes
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CalligraphicDissolveOverlay(
  gameState: GameState,
  settings: UserSettings,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current

  val visible =
    gameState.status == GameStatus.LEVEL_COMPLETE && gameState.calligraphicRevealActive

  val assembleAnim = remember(visible) { Animatable(if (reduceMotion) 1f else 0f) }
  LaunchedEffect(visible, reduceMotion) {
    if (visible && !reduceMotion) {
      assembleAnim.snapTo(0f)
      assembleAnim.animateTo(1f, animationSpec = tween(durationMillis = 700))
    } else if (visible) {
      assembleAnim.snapTo(1f)
    }
  }

  val dotVectors = remember {
    val rnd = Random(77)
    FloatArray(44) { idx ->
      if (idx % 2 == 0) rnd.nextFloat() * 6.283f else 0.35f + rnd.nextFloat() * 0.65f
    }
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(140)),
    exit = fadeOut(tween(160)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.40f)),
      contentAlignment = Alignment.Center,
    ) {
      FlatCard(
        modifier = Modifier.padding(Dimens.SpaceLg),
        shape = AppShapes.Dialog,
        backgroundColor = palette.canvas,
      ) {
        Box(
          modifier = Modifier.padding(horizontal = 36.dp, vertical = 24.dp),
          contentAlignment = Alignment.Center,
        ) {
          val t = assembleAnim.value
          val dotAlpha = (1f - t).coerceIn(0f, 1f)
          if (dotAlpha > 0.02f) {
            Canvas(modifier = Modifier.size(136.dp)) {
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
                  radius = 3.dp.toPx(),
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
              fontSize = 82.sp,
              color = palette.ink,
            )
            Text(
              text = gameState.levelSpec.letter.localizedName(settings.language),
              style = MaterialTheme.typography.titleLarge,
              fontSize = Dimens.TitleSp,
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
  val palette = LocalPalette.current
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
          delay(180L)
          revealedStars = s
        }
      }
    }
  }

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(160)),
    exit = fadeOut(tween(140)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)),
      contentAlignment = Alignment.Center,
    ) {
      FlatCard(
        modifier = Modifier.fillMaxWidth(0.76f).widthIn(max = 330.dp),
        shape = AppShapes.Dialog,
        backgroundColor = palette.canvas,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          // Modest pixel title «أحسنت!» + audio replay icon
          Box(modifier = Modifier.fillMaxWidth()) {
            DotText(
              text = strings.levelCompleteTitle,
              fontResId = R.font.noto_kufi_arabic,
              dotSize = 1.45.dp,
              color = palette.ink,
              targetRows = 14,
              modifier = Modifier.align(Alignment.Center),
            )
            IconButton(
              onClick = { onReplayLetterAudio(gameState.levelSpec.letter.nameAr) },
              modifier = Modifier.align(Alignment.CenterEnd).minimumInteractiveComponentSize(),
            ) {
              PixelIcon(
                type = PixelIcon.VOLUME,
                size = 24.dp,
                contentDescription = strings.settingsTts,
              )
            }
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = gameState.levelSpec.letter.isolatedGlyph,
              fontFamily = AmiriFamily,
              fontSize = 54.sp,
              color = palette.ink,
            )
            Text(
              text = gameState.levelSpec.letter.localizedName(settings.language),
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              color = palette.mutedInk,
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
            for (i in 1..3) {
              val filled = i <= revealedStars
              PixelIcon(
                type = if (filled) PixelIcon.STAR else PixelIcon.STAR_OUTLINE,
                size = 32.dp,
                contentDescription = null,
              )
            }
          }

          Text(
            text = "${strings.scoreLabel}  ${gameState.score}",
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = palette.ink,
          )

          val hasNext =
            !gameState.isDailyChallenge && gameState.levelSpec.levelNumber < LevelCatalog.levels.size
          if (hasNext) {
            FlatButton(
              label = strings.nextLevel,
              isPrimaryOrange = true,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = { onNextLevel(gameState.levelSpec.levelNumber + 1) },
              modifier = Modifier.fillMaxWidth().testTag("next_level_button"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            FlatButton(
              label = strings.replay,
              isPrimaryOrange = !hasNext,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = onRestart,
              modifier = Modifier.weight(1f).testTag("replay_button"),
            )
            FlatButton(
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
  val palette = LocalPalette.current

  AnimatedVisibility(
    visible = gameState.status == GameStatus.PAUSED,
    enter = fadeIn(tween(140)),
    exit = fadeOut(tween(120)),
  ) {
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
      FlatCard(
        modifier = Modifier.fillMaxWidth(0.70f).widthIn(max = 300.dp),
        shape = AppShapes.Dialog,
        backgroundColor = palette.canvas,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          Text(
            text = strings.pausedTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = Dimens.TitleSp,
            fontWeight = FontWeight.Bold,
            color = palette.ink,
          )
          FlatButton(
            label = strings.resume,
            isPrimaryOrange = true,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onResume,
            modifier = Modifier.fillMaxWidth().testTag("resume_button"),
          )
          FlatButton(
            label = strings.restart,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().testTag("pause_restart_button"),
          )
          FlatButton(
            label = strings.backToMap,
            isPrimaryOrange = false,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onOpenMap,
            modifier = Modifier.fillMaxWidth().testTag("pause_map_button"),
          )
          FlatButton(
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
  val palette = LocalPalette.current

  AnimatedVisibility(
    visible = gameState.status == GameStatus.GAME_OVER,
    enter = fadeIn(tween(150)),
    exit = fadeOut(tween(130)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)),
      contentAlignment = Alignment.Center,
    ) {
      FlatCard(
        modifier = Modifier.fillMaxWidth(0.70f).widthIn(max = 310.dp),
        shape = AppShapes.Dialog,
        backgroundColor = palette.canvas,
      ) {
        Column(
          modifier = Modifier.padding(Dimens.SpaceLg),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
        ) {
          Text(
            text = strings.gameOverTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = Dimens.TitleSp,
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
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = palette.ink,
          )
          FlatButton(
            label = strings.restart,
            isPrimaryOrange = true,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().testTag("retry_button"),
          )
          FlatButton(
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

@Composable
fun GameCoachmarkOverlay(
  visible: Boolean,
  strings: AppStrings,
  hapticsEnabled: Boolean,
  onDismiss: () -> Unit,
) {
  val palette = LocalPalette.current

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(tween(160)),
    exit = fadeOut(tween(140)),
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(Color.Black.copy(alpha = 0.50f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss,
          )
          .padding(Dimens.ScreenPadding),
    ) {
      FlatCard(
        modifier =
          Modifier.align(Alignment.BottomCenter)
            .padding(bottom = 84.dp)
            .fillMaxWidth()
            .widthIn(max = 360.dp),
        shape = AppShapes.Dialog,
        backgroundColor = palette.canvas,
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
            PixelIcon(type = PixelIcon.PAUSE, size = 24.dp)
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
            PixelIcon(type = PixelIcon.SLOW_MO, size = 24.dp)
            Text(
              text = strings.coachmarkPowerUpDesc,
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              color = palette.ink,
              modifier = Modifier.weight(1f),
            )
          }
          FlatButton(
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
