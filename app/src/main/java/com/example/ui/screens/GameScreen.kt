package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.ControlScheme
import com.example.model.Direction
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.UserSettings
import com.example.ui.components.PixelBoardCanvas
import com.example.ui.theme.AmiriFamily
import com.example.ui.theme.LocalBoardPalette
import com.example.ui.theme.PixelScoreFamily
import kotlin.math.abs

@Composable
fun GameScreen(
  gameState: GameState,
  settings: UserSettings,
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
) {
  val palette = LocalBoardPalette.current

  BackHandler {
    if (gameState.status == GameStatus.PLAYING) {
      onPause()
    } else {
      onOpenMap()
    }
  }

  val totalPx = gameState.totalLetterPixels.coerceAtLeast(1)
  val eatenPx = (totalPx - gameState.remainingPixels.size).coerceAtLeast(0)
  val progressFraction = eatenPx.toFloat() / totalPx.toFloat()

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 560.dp)
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Top Bar: Explicitly LTR so pixel-font score stays in top-left corner
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            // Top-left pixel-font score + combo
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = palette.cardBackground,
                border = BorderStroke(1.dp, palette.cardBorder),
              ) {
                Text(
                  text = gameState.score.toString().padStart(4, '0'),
                  fontFamily = PixelScoreFamily,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
              }
              if (gameState.combo >= 2) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = palette.accentSoft,
                ) {
                  Text(
                    text = strings.comboBadge(gameState.combo),
                    fontFamily = PixelScoreFamily,
                    fontSize = 13.sp,
                    color = palette.accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  )
                }
              }
            }

            // Center lives or Boss timer
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              if (gameState.timeRemainingMs != null) {
                val sec = (gameState.timeRemainingMs / 1000L).toInt()
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (sec <= 15) palette.accentSoft else palette.cardBackground,
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.Timer,
                      contentDescription = null,
                      tint = if (sec <= 15) palette.accent else MaterialTheme.colorScheme.onSurface,
                      modifier = Modifier.size(15.dp),
                    )
                    Text(
                      text = "${sec}s",
                      fontFamily = PixelScoreFamily,
                      fontSize = 13.sp,
                      color =
                        if (sec <= 15) palette.accent else MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }

              if (gameState.mode != GameMode.KIDS) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                  for (i in 1..gameState.maxLives) {
                    Icon(
                      imageVector =
                        if (i <= gameState.lives) Icons.Rounded.Favorite
                        else Icons.Rounded.FavoriteBorder,
                      contentDescription = null,
                      tint = if (i <= gameState.lives) palette.accent else palette.baselinePixel,
                      modifier = Modifier.size(18.dp),
                    )
                  }
                }
              }
            }

            // Top-right Pause + Back
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = {
                  if (gameState.status == GameStatus.PLAYING) onPause() else onOpenMap()
                },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("pause_button"),
              ) {
                Icon(
                  imageVector =
                    if (gameState.status == GameStatus.PLAYING) Icons.Rounded.Pause
                    else Icons.AutoMirrored.Rounded.ArrowBack,
                  contentDescription = strings.pausedTitle,
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Letter & Font Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text =
              strings.levelHeader(
                gameState.levelSpec.letter.glyph,
                gameState.levelSpec.fontSpec.localizedName(settings.language),
              ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            if (gameState.mirroredControls) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.accentSoft,
              ) {
                Text(
                  text = strings.mirroredBadge,
                  style = MaterialTheme.typography.labelSmall,
                  color = palette.accent,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                )
              }
            }
            if (gameState.levelSpec.isBoss) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.accent,
              ) {
                Text(
                  text = strings.bossBadge,
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                )
              }
            }
            Text(
              text = strings.pixelsLeft(eatenPx, totalPx),
              fontFamily = PixelScoreFamily,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        // Active power-up indicator
        AnimatedVisibility(visible = gameState.activeEffect != null) {
          val effect = gameState.activeEffect
          if (effect != null) {
            val secLeft = ((gameState.effectRemainingMs + 999L) / 1000L).toInt()
            Surface(
              modifier = Modifier.padding(top = 6.dp),
              shape = RoundedCornerShape(10.dp),
              color = palette.accentSoft,
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Bolt,
                  contentDescription = null,
                  tint = palette.accent,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = "${strings.powerUpLabel(effect)} · ${secLeft}s",
                  style = MaterialTheme.typography.labelLarge,
                  color = palette.accent,
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Board Card + Gestures + Overlays
        var dragX by remember { mutableFloatStateOf(0f) }
        var dragY by remember { mutableFloatStateOf(0f) }
        var turnedInGesture by remember { mutableStateOf(false) }

        Box(
          modifier =
            Modifier.fillMaxWidth()
              .weight(1f, fill = false)
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
                      // Allow chained swipes without lifting finger
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
          PixelBoardCanvas(gameState = gameState)

          // Tap-to-start hint overlay
          androidx.compose.animation.AnimatedVisibility(
            visible = gameState.status == GameStatus.READY,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(16.dp))
                  .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                  .clickable(onClick = onStartOrResume)
                  .padding(horizontal = 18.dp, vertical = 14.dp)
                  .testTag("tap_to_start_hint"),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = strings.tapToStartHint(gameState.bestScore),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = palette.accent,
                textAlign = TextAlign.Center,
              )
            }
          }

          // 1-second Calligraphic Reveal right upon level completion
          androidx.compose.animation.AnimatedVisibility(
            visible =
              gameState.status == GameStatus.LEVEL_COMPLETE && gameState.calligraphicRevealActive,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Surface(
              shape = RoundedCornerShape(22.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
              border = BorderStroke(2.dp, palette.accent),
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 38.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Text(
                  text = gameState.levelSpec.letter.isolatedGlyph,
                  fontFamily = AmiriFamily,
                  fontSize = 104.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = gameState.levelSpec.letter.localizedName(settings.language),
                  style = MaterialTheme.typography.headlineMedium,
                  color = palette.accent,
                )
              }
            }
          }

          // Level Complete Summary Overlay
          androidx.compose.animation.AnimatedVisibility(
            visible =
              gameState.status == GameStatus.LEVEL_COMPLETE && !gameState.calligraphicRevealActive,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Surface(
              modifier = Modifier.padding(18.dp).fillMaxWidth(),
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
              border = BorderStroke(1.5.dp, palette.cardBorder),
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Text(
                    text = gameState.levelSpec.letter.isolatedGlyph,
                    fontFamily = AmiriFamily,
                    fontSize = 44.sp,
                    color = palette.accent,
                  )
                  Column {
                    Text(
                      text = strings.levelCompleteTitle,
                      style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                      text = gameState.levelSpec.letter.localizedName(settings.language),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                  IconButton(
                    onClick = { onReplayLetterAudio(gameState.levelSpec.letter.nameAr) },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.VolumeUp,
                      contentDescription = strings.settingsTts,
                      tint = palette.accent,
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3-Star Rating
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  for (i in 1..3) {
                    Icon(
                      imageVector =
                        if (i <= gameState.starsEarned) Icons.Rounded.Star
                        else Icons.Rounded.StarBorder,
                      contentDescription = null,
                      tint =
                        if (i <= gameState.starsEarned) palette.accent else palette.baselinePixel,
                      modifier = Modifier.size(36.dp),
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = "${strings.scoreLabel}: ${gameState.score}",
                  fontFamily = PixelScoreFamily,
                  fontSize = 18.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val hasNext =
                  !gameState.isDailyChallenge &&
                    gameState.levelSpec.levelNumber < LevelCatalog.levels.size
                if (hasNext) {
                  Button(
                    onClick = { onNextLevel(gameState.levelSpec.levelNumber + 1) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("next_level_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.accent),
                  ) {
                    Text(
                      text = strings.nextLevel,
                      style = MaterialTheme.typography.labelLarge,
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.weight(1f).height(48.dp).testTag("replay_button"),
                    shape = RoundedCornerShape(12.dp),
                  ) {
                    Text(text = strings.replay, maxLines = 1)
                  }
                  OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1f).height(48.dp).testTag("complete_map_button"),
                    shape = RoundedCornerShape(12.dp),
                  ) {
                    Text(text = strings.backToMap, maxLines = 1)
                  }
                }
              }
            }
          }

          // Paused Overlay
          androidx.compose.animation.AnimatedVisibility(
            visible = gameState.status == GameStatus.PAUSED,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Surface(
              modifier = Modifier.padding(22.dp).fillMaxWidth(),
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
              border = BorderStroke(1.5.dp, palette.cardBorder),
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Text(
                  text = strings.pausedTitle,
                  style = MaterialTheme.typography.headlineMedium,
                )
                Button(
                  onClick = onStartOrResume,
                  modifier = Modifier.fillMaxWidth().height(50.dp).testTag("resume_button"),
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = palette.accent),
                ) {
                  Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = strings.resume)
                }
                OutlinedButton(
                  onClick = onRestart,
                  modifier = Modifier.fillMaxWidth().height(48.dp),
                  shape = RoundedCornerShape(12.dp),
                ) {
                  Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = strings.restart)
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                  ) {
                    Text(text = strings.backToMap, maxLines = 1)
                  }
                  OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                  ) {
                    Text(text = strings.backHome, maxLines = 1)
                  }
                }
              }
            }
          }

          // Game Over Overlay
          androidx.compose.animation.AnimatedVisibility(
            visible = gameState.status == GameStatus.GAME_OVER,
            enter = fadeIn(),
            exit = fadeOut(),
          ) {
            Surface(
              modifier = Modifier.padding(22.dp).fillMaxWidth(),
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
              border = BorderStroke(1.5.dp, palette.cardBorder),
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Text(
                  text = strings.gameOverTitle,
                  style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                  text = strings.deathReasonLabel(gameState.deathReason),
                  style = MaterialTheme.typography.bodyLarge,
                  color = palette.accent,
                )
                Text(
                  text = "${strings.scoreLabel}: ${gameState.score}",
                  fontFamily = PixelScoreFamily,
                  fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                  onClick = onRestart,
                  modifier = Modifier.fillMaxWidth().height(50.dp).testTag("retry_button"),
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = palette.accent),
                ) {
                  Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = strings.restart)
                }
                OutlinedButton(
                  onClick = onOpenMap,
                  modifier = Modifier.fillMaxWidth().height(48.dp),
                  shape = RoundedCornerShape(12.dp),
                ) {
                  Text(text = strings.backToMap)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar under the card
        LinearProgressIndicator(
          progress = { progressFraction },
          modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
          color = palette.accent,
          trackColor = palette.cardBackground,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Controls: Big Left/Right tap zones when selected
        if (settings.controlScheme == ControlScheme.TAP_ZONES) {
          CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Surface(
                modifier =
                  Modifier.weight(1f)
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTapTurn(true) }
                    .testTag("tap_zone_left"),
                shape = RoundedCornerShape(16.dp),
                color = palette.cardBackground,
                border = BorderStroke(1.2.dp, palette.cardBorder),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = strings.tapTurnLeft,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                  )
                }
              }
              Surface(
                modifier =
                  Modifier.weight(1f)
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTapTurn(false) }
                    .testTag("tap_zone_right"),
                shape = RoundedCornerShape(16.dp),
                color = palette.cardBackground,
                border = BorderStroke(1.2.dp, palette.cardBorder),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = strings.tapTurnRight,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
