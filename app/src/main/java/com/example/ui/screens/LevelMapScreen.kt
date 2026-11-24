package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelCatalog
import com.example.i18n.AppStrings
import com.example.model.LevelProgress
import com.example.model.LevelSpec
import com.example.ui.theme.LocalBoardPalette
import com.example.ui.theme.NotoKufiFamily
import com.example.ui.theme.PixelScoreFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelMapScreen(
  strings: AppStrings,
  progress: LevelProgress,
  onSelectLevel: (Int) -> Unit,
  onBack: () -> Unit,
) {
  val palette = LocalBoardPalette.current
  val levelsByWorld = remember { LevelCatalog.levels.groupBy { it.world } }
  val totalStars = progress.starsByLevel.values.sum()
  val maxStars = LevelCatalog.levels.size * 3

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = strings.levelMap,
            style = MaterialTheme.typography.titleLarge,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("map_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = strings.backHome,
            )
          }
        },
        actions = {
          Row(
            modifier = Modifier.padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Star,
              contentDescription = null,
              tint = palette.accent,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = "$totalStars/$maxStars",
              style = MaterialTheme.typography.labelSmall,
              fontFamily = PixelScoreFamily,
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
          ),
      )
    },
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      val columns = if (maxWidth > 540.dp) 5 else 4
      LazyColumn(
        modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        for (world in 1..4) {
          val worldLevels = levelsByWorld[world].orEmpty()
          item(key = "world_$world") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Column {
                Text(
                  text = strings.worldTitle(world),
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                  text = strings.worldDesc(world),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }

              val rows = worldLevels.chunked(columns)
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEach { rowItems ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    rowItems.forEach { spec ->
                      val unlocked = spec.levelNumber <= progress.unlockedLevel
                      val stars = progress.starsByLevel[spec.levelNumber] ?: 0
                      LevelTile(
                        spec = spec,
                        stars = stars,
                        unlocked = unlocked,
                        bossBadge = strings.bossBadge,
                        levelLabel = strings.levelNumLabel(spec.levelNumber),
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

@Composable
private fun LevelTile(
  spec: LevelSpec,
  stars: Int,
  unlocked: Boolean,
  bossBadge: String,
  levelLabel: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val palette = LocalBoardPalette.current
  Surface(
    modifier =
      modifier
        .alpha(if (unlocked) 1f else 0.52f)
        .clip(RoundedCornerShape(14.dp))
        .clickable(enabled = unlocked, onClick = onClick)
        .testTag("level_tile_${spec.levelNumber}"),
    shape = RoundedCornerShape(14.dp),
    color = palette.cardBackground,
    border =
      BorderStroke(
        width = if (spec.isBoss && unlocked) 1.8.dp else 1.dp,
        color = if (spec.isBoss && unlocked) palette.accent else palette.cardBorder,
      ),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = levelLabel,
          style = MaterialTheme.typography.labelSmall,
          fontFamily = PixelScoreFamily,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
        )
        if (spec.isBoss) {
          Text(
            text = bossBadge,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = palette.accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      if (unlocked) {
        Text(
          text = spec.letter.glyph,
          fontFamily = NotoKufiFamily,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      } else {
        Box(
          modifier = Modifier.height(36.dp),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..3) {
          Icon(
            imageVector = if (i <= stars) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            contentDescription = null,
            tint = if (i <= stars) palette.accent else palette.baselinePixel,
            modifier = Modifier.size(14.dp),
          )
        }
      }
    }
  }
}
