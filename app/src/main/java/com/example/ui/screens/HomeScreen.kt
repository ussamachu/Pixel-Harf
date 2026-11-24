package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.raster.LetterRasterizer
import com.example.ui.components.PixelLetterPreviewCanvas
import com.example.ui.theme.LocalBoardPalette
import com.example.ui.theme.PixelScoreFamily
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
  strings: AppStrings,
  settings: UserSettings,
  progress: LevelProgress,
  rasterizer: LetterRasterizer,
  onPlayContinue: (Int) -> Unit,
  onPlayDaily: () -> Unit,
  onOpenMap: () -> Unit,
  onOpenSettings: () -> Unit,
  onSelectMode: (GameMode) -> Unit,
) {
  val palette = LocalBoardPalette.current
  val previewGlyphs = remember { listOf("ن", "ب", "ق", "ش", "ع") }
  var previewIndex by remember { mutableIntStateOf(0) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(2200L)
      previewIndex = (previewIndex + 1) % previewGlyphs.size
    }
  }

  val previewRaster =
    remember(previewIndex) {
      rasterizer.rasterize(
        glyph = previewGlyphs[previewIndex],
        fontSpec = LevelCatalog.fonts[0],
        cols = 16,
        rows = 16,
      )
    }

  val totalStars = progress.starsByLevel.values.sum()
  val maxStars = LevelCatalog.levels.size * 3
  val todayDone = progress.dailyCompletedDate == LevelCatalog.todayKey()
  val dailySpec = remember { LevelCatalog.dailyChallenge() }

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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = strings.appTitle,
              style = MaterialTheme.typography.displayLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = strings.appSubtitle,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = onOpenSettings,
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("settings_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Settings,
              contentDescription = strings.settings,
              tint = MaterialTheme.colorScheme.onBackground,
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Live Pixel Letter Hero Card
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center,
        ) {
          PixelLetterPreviewCanvas(
            letterPixels = previewRaster.letterPixels,
            baselinePixels = previewRaster.baselinePixels,
            cols = previewRaster.cols,
            rows = previewRaster.rows,
            modifier = Modifier.size(216.dp),
          )
          // Total stars pill in corner
          Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, palette.cardBorder),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = strings.starsTotal(totalStars, maxStars),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = PixelScoreFamily,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Play CTA
        Button(
          onClick = { onPlayContinue(progress.unlockedLevel) },
          modifier =
            Modifier.fillMaxWidth()
              .height(58.dp)
              .testTag("play_button"),
          shape = RoundedCornerShape(16.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = palette.accent,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
          Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = strings.continueLevel(progress.unlockedLevel),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary Actions: Daily + Level Map
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Surface(
            modifier =
              Modifier.weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onPlayDaily)
                .testTag("daily_button"),
            shape = RoundedCornerShape(16.dp),
            color = palette.cardBackground,
            border = BorderStroke(1.dp, palette.cardBorder),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.CalendarToday,
                  contentDescription = null,
                  tint = palette.accent,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = strings.dailyChallenge,
                  style = MaterialTheme.typography.labelLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text =
                  if (todayDone) strings.dailyCompleted
                  else "${dailySpec.levelSpec.letter.glyph} · ${
                    if (progress.dailyStreak > 0) strings.dailyStreak(progress.dailyStreak)
                    else dailySpec.levelSpec.fontSpec.localizedName(settings.language)
                  }",
                style = MaterialTheme.typography.bodyMedium,
                color = if (todayDone) palette.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }

          OutlinedButton(
            onClick = onOpenMap,
            modifier =
              Modifier.weight(1f)
                .height(74.dp)
                .testTag("map_button"),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, palette.cardBorder),
          ) {
            Icon(
              imageVector = Icons.Rounded.GridView,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = strings.levelMap,
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onBackground,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mode Selector (Kids / Classic / Hard)
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = strings.modesTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            ModeChip(
              label = strings.modeKids,
              selected = settings.gameMode == GameMode.KIDS,
              onClick = { onSelectMode(GameMode.KIDS) },
              modifier = Modifier.weight(1f).testTag("mode_kids"),
            )
            ModeChip(
              label = strings.modeClassic,
              selected = settings.gameMode == GameMode.CLASSIC,
              onClick = { onSelectMode(GameMode.CLASSIC) },
              modifier = Modifier.weight(1f).testTag("mode_classic"),
            )
            ModeChip(
              label = strings.modeHard,
              selected = settings.gameMode == GameMode.HARD,
              onClick = { onSelectMode(GameMode.HARD) },
              modifier = Modifier.weight(1f).testTag("mode_hard"),
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          val modeDesc =
            when (settings.gameMode) {
              GameMode.KIDS -> strings.modeKidsDesc
              GameMode.CLASSIC -> strings.modeClassicDesc
              GameMode.HARD -> strings.modeHardDesc
            }
          Text(
            text = modeDesc,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun ModeChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val palette = LocalBoardPalette.current
  Surface(
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(12.dp))
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    color = if (selected) palette.accent else palette.cardBackground,
    border = BorderStroke(1.dp, if (selected) palette.accent else palette.cardBorder),
  ) {
    Box(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color =
          if (selected) MaterialTheme.colorScheme.onPrimary
          else MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
