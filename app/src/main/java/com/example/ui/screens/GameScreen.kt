package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppStrings
import com.example.model.ControlScheme
import com.example.model.Direction
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.PowerUpType
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.FlatCard
import com.example.ui.components.HairlineDivider
import com.example.ui.components.MonoDigitText
import com.example.ui.components.PixelBoardCanvas
import com.example.ui.components.PixelIcon
import com.example.ui.components.UiAppearanceState
import com.example.ui.theme.AppShapes
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette
import com.example.ui.theme.LocalReduceMotion
import kotlin.math.abs

@Composable
fun GameScreen(
  gameState: GameState,
  settings: UserSettings,
  appearance: UiAppearanceState,
  strings: AppStrings,
  onStartOrResume: () -> Unit,
  onPause: () -> Unit,
  onRestart: () -> Unit,
  onSwipeDirection: (Direction) -> Unit,
  onTapTurn: (Boolean) -> Unit,
  onNextLevel: (Int) -> Unit,
  onReplayLetterAudio: (String) -> Unit,
  onOpenMap: () -> Unit,
  onBackHome: () -> Unit,
  onDismissCoachmark: () -> Unit,
) {
  val palette = LocalPalette.current
  val reduceMotion = LocalReduceMotion.current

  BackHandler {
    if (gameState.status == GameStatus.PLAYING) {
      onPause()
    } else {
      onOpenMap()
    }
  }

  var prevLives by remember { mutableIntStateOf(gameState.lives) }
  val heartPulse = remember { Animatable(1f) }
  LaunchedEffect(gameState.lives) {
    if (gameState.lives < prevLives && !reduceMotion) {
      heartPulse.snapTo(1.30f)
      heartPulse.animateTo(1f, animationSpec = tween(240))
    }
    prevLives = gameState.lives
  }

  val totalPx = gameState.totalLetterPixels.coerceAtLeast(1)
  val eatenPx = (totalPx - gameState.remainingPixels.size).coerceAtLeast(0)
  val progressFraction = eatenPx.toFloat() / totalPx.toFloat()

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier.widthIn(max = 580.dp).fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // 1. TOP BAR (56dp): [score + combo] | [letter name + thin orange progress] | [red hearts], separated by hairline
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .height(Dimens.TopBarHeight)
              .padding(horizontal = Dimens.ScreenPadding),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          // Score + combo in JetBrains Mono
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            MonoDigitText(
              value = gameState.score.toString().padStart(4, '0'),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = palette.ink,
            )
            if (gameState.combo >= 2) {
              Text(
                text = strings.comboBadge(gameState.combo),
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = palette.accent,
              )
            }
          }

          // Letter name (IBM Plex Arabic) + thin orange progress bar
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text =
                strings.letterTitle(
                  gameState.levelSpec.letter.glyph,
                  gameState.levelSpec.letter.localizedName(settings.language),
                ),
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              fontWeight = FontWeight.SemiBold,
              color = palette.ink,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Center,
            )
            LinearProgressIndicator(
              progress = { progressFraction },
              modifier = Modifier.fillMaxWidth(0.78f).height(3.dp).clip(RoundedCornerShape(2.dp)),
              color = palette.accent,
              trackColor = palette.surface,
            )
          }

          // Red hearts + optional Boss timer
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.semantics { contentDescription = strings.livesContentDesc },
          ) {
            if (gameState.timeRemainingMs != null) {
              val sec = (gameState.timeRemainingMs / 1000L).toInt()
              Text(
                text = "${sec}s",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (sec <= 15) palette.accent else palette.ink,
                modifier = Modifier.padding(end = 2.dp),
              )
            }
            if (gameState.mode != GameMode.KIDS) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier =
                  Modifier.graphicsLayer {
                    scaleX = heartPulse.value
                    scaleY = heartPulse.value
                  },
              ) {
                for (i in 1..gameState.maxLives) {
                  val alive = i <= gameState.lives
                  PixelIcon(
                    type = if (alive) PixelIcon.HEART else PixelIcon.HEART_OUTLINE,
                    size = 16.dp,
                    contentDescription = null,
                  )
                }
              }
            }
          }
        }
      }

      HairlineDivider(modifier = Modifier.fillMaxWidth())

      Spacer(modifier = Modifier.height(Dimens.SpaceMd))

      // 2. HERO BOARD: fills remaining space with 12dp margin and 1dp hairline border
      var dragX by remember { mutableFloatStateOf(0f) }
      var dragY by remember { mutableFloatStateOf(0f) }
      var turnedInGesture by remember { mutableStateOf(false) }

      Box(
        modifier =
          Modifier.weight(1f)
            .fillMaxWidth()
            .padding(horizontal = Dimens.BoardHorizontalMargin)
            .pointerInput(settings.controlScheme, gameState.status) {
              detectTapGestures {
                if (gameState.status == GameStatus.READY) {
                  onStartOrResume()
                }
              }
            }
            .pointerInput(settings.controlScheme, gameState.mirroredControls) {
              detectDragGestures(
                onDragStart = {
                  dragX = 0f
                  dragY = 0f
                  turnedInGesture = false
                },
                onDrag = { change, dragAmount ->
                  change.consume()
                  dragX += dragAmount.x
                  dragY += dragAmount.y
                  val threshold = 26f
                  if (!turnedInGesture && (abs(dragX) > threshold || abs(dragY) > threshold)) {
                    val dir =
                      if (abs(dragX) > abs(dragY)) {
                        if (dragX > 0f) Direction.RIGHT else Direction.LEFT
                      } else {
                        if (dragY > 0f) Direction.DOWN else Direction.UP
                      }
                    onSwipeDirection(dir)
                    dragX = 0f
                    dragY = 0f
                    turnedInGesture = true
                  } else if (turnedInGesture && (abs(dragX) > 44f || abs(dragY) > 44f)) {
                    val dir =
                      if (abs(dragX) > abs(dragY)) {
                        if (dragX > 0f) Direction.RIGHT else Direction.LEFT
                      } else {
                        if (dragY > 0f) Direction.DOWN else Direction.UP
                      }
                    onSwipeDirection(dir)
                    dragX = 0f
                    dragY = 0f
                  }
                },
              )
            },
        contentAlignment = Alignment.Center,
      ) {
        PixelBoardCanvas(
          gameState = gameState,
          useDotPixels = appearance.useDotPixels,
        )

        androidx.compose.animation.AnimatedVisibility(
          visible = gameState.status == GameStatus.READY,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          FlatCard(
            modifier = Modifier.padding(Dimens.SpaceLg).testTag("tap_to_start_hint"),
            shape = AppShapes.Card,
            backgroundColor = palette.canvas.copy(alpha = 0.94f),
            borderColor = palette.accent,
            hapticsEnabled = settings.hapticsEnabled,
            onClick = onStartOrResume,
          ) {
            Text(
              text = strings.tapToStartHint(gameState.bestScore),
              style = MaterialTheme.typography.bodyLarge,
              fontSize = Dimens.BodySp,
              fontWeight = FontWeight.Bold,
              color = palette.accent,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
            )
          }
        }
      }

      if (settings.controlScheme == ControlScheme.TAP_ZONES) {
        Spacer(modifier = Modifier.height(Dimens.SpaceSm))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.ScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
          ) {
            FlatCard(
              modifier = Modifier.weight(1f).height(Dimens.ButtonHeight).testTag("tap_zone_left"),
              shape = AppShapes.Button,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = { onTapTurn(true) },
            ) {
              Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
              ) {
                PixelIcon(type = PixelIcon.ARROW_LEFT, size = 16.dp)
                Text(
                  text = strings.tapTurnLeft,
                  style = MaterialTheme.typography.bodyLarge,
                  fontSize = Dimens.BodySp,
                  fontWeight = FontWeight.SemiBold,
                  color = palette.ink,
                )
              }
            }
            FlatCard(
              modifier = Modifier.weight(1f).height(Dimens.ButtonHeight).testTag("tap_zone_right"),
              shape = AppShapes.Button,
              hapticsEnabled = settings.hapticsEnabled,
              onClick = { onTapTurn(false) },
            ) {
              Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
              ) {
                Text(
                  text = strings.tapTurnRight,
                  style = MaterialTheme.typography.bodyLarge,
                  fontSize = Dimens.BodySp,
                  fontWeight = FontWeight.SemiBold,
                  color = palette.ink,
                )
                PixelIcon(type = PixelIcon.ARROW_RIGHT, size = 16.dp)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceSm))
      HairlineDivider(modifier = Modifier.fillMaxWidth())

      // 3. BOTTOM BAR: Labeled Pause button (two-bar icon) + active power-up chip with countdown ring & label
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .height(Dimens.BottomBarHeight)
            .padding(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val activeEffect = gameState.activeEffect
        if (activeEffect != null && gameState.effectRemainingMs > 0L) {
          ActivePowerUpChip(
            type = activeEffect,
            remainingMs = gameState.effectRemainingMs,
            label = strings.powerUpLabel(activeEffect),
          )
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        FlatCard(
          modifier = Modifier.height(Dimens.ButtonHeight).testTag("pause_button"),
          shape = AppShapes.Button,
          hapticsEnabled = settings.hapticsEnabled,
          onClick = {
            if (gameState.status == GameStatus.PLAYING || gameState.status == GameStatus.READY) {
              onPause()
            }
          },
        ) {
          Row(
            modifier = Modifier.padding(horizontal = Dimens.SpaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            PixelIcon(
              type = PixelIcon.PAUSE,
              size = 16.dp,
              contentDescription = strings.pauseAction,
            )
            Text(
              text = strings.pauseAction,
              style = MaterialTheme.typography.labelLarge,
              fontSize = Dimens.BodySp,
              fontWeight = FontWeight.SemiBold,
              color = palette.ink,
              maxLines = 1,
            )
          }
        }
      }
    }

    CalligraphicDissolveOverlay(
      gameState = gameState,
      settings = settings,
    )

    LevelCompleteOverlay(
      gameState = gameState,
      settings = settings,
      strings = strings,
      onNextLevel = onNextLevel,
      onRestart = onRestart,
      onOpenMap = onOpenMap,
      onReplayLetterAudio = onReplayLetterAudio,
    )

    PausedOverlay(
      gameState = gameState,
      settings = settings,
      strings = strings,
      onResume = onStartOrResume,
      onRestart = onRestart,
      onOpenMap = onOpenMap,
      onBackHome = onBackHome,
    )

    GameOverOverlay(
      gameState = gameState,
      settings = settings,
      strings = strings,
      onRestart = onRestart,
      onOpenMap = onOpenMap,
    )

    GameCoachmarkOverlay(
      visible = !appearance.hasSeenGameCoachmark && gameState.status == GameStatus.READY,
      strings = strings,
      hapticsEnabled = settings.hapticsEnabled,
      onDismiss = onDismissCoachmark,
    )
  }
}

@Composable
private fun ActivePowerUpChip(
  type: PowerUpType,
  remainingMs: Long,
  label: String,
) {
  val palette = LocalPalette.current
  val icon =
    when (type) {
      PowerUpType.SLOW_MO -> PixelIcon.SLOW_MO
      PowerUpType.MAGNET -> PixelIcon.MAGNET
      PowerUpType.GHOST -> PixelIcon.GHOST
      PowerUpType.SHRINK -> PixelIcon.SHRINK
    }
  val fraction = (remainingMs.toFloat() / 5500f).coerceIn(0f, 1f)
  val secLeft = ((remainingMs + 999L) / 1000L).toInt()

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
  ) {
    Box(
      modifier = Modifier.size(Dimens.PowerUpCircleSize),
      contentAlignment = Alignment.Center,
    ) {
      FlatCard(
        modifier = Modifier.fillMaxSize(),
        shape = AppShapes.Circle,
      ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          PixelIcon(
            type = icon,
            size = 24.dp,
            isActive = true,
            contentDescription = label,
          )
        }
      }
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
          color = palette.accent,
          startAngle = -90f,
          sweepAngle = 360f * fraction,
          useCenter = false,
          style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )
      }
    }
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = Dimens.BodySp,
        fontWeight = FontWeight.SemiBold,
        color = palette.ink,
        maxLines = 1,
      )
      Text(
        text = "${secLeft}s",
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.CaptionSp,
        color = palette.accent,
      )
    }
  }
}
