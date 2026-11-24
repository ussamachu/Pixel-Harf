package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.ControlScheme
import com.example.model.GameMode
import com.example.model.UserSettings
import com.example.ui.theme.LocalBoardPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  strings: AppStrings,
  settings: UserSettings,
  onSetLanguage: (AppLanguage) -> Unit,
  onSetControlScheme: (ControlScheme) -> Unit,
  onSetGameMode: (GameMode) -> Unit,
  onSetSound: (Boolean) -> Unit,
  onSetHaptics: (Boolean) -> Unit,
  onSetTts: (Boolean) -> Unit,
  onSetColorblind: (Boolean) -> Unit,
  onSetLargeText: (Boolean) -> Unit,
  onBack: () -> Unit,
) {
  val palette = LocalBoardPalette.current

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = strings.settings,
            style = MaterialTheme.typography.titleLarge,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("settings_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = strings.backHome,
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
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.widthIn(max = 560.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        // Language selector
        SettingSectionTitle(strings.settingsLanguage)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          AppLanguage.entries.forEach { lang ->
            SelectablePill(
              label = lang.nativeLabel,
              selected = settings.language == lang,
              onClick = { onSetLanguage(lang) },
              modifier = Modifier.weight(1f).testTag("lang_${lang.code}"),
            )
          }
        }

        // Control scheme selector
        SettingSectionTitle(strings.settingsControls)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          SelectablePill(
            label = strings.controlSwipe,
            selected = settings.controlScheme == ControlScheme.SWIPE,
            onClick = { onSetControlScheme(ControlScheme.SWIPE) },
            modifier = Modifier.weight(1f).testTag("control_swipe"),
          )
          SelectablePill(
            label = strings.controlTapZones,
            selected = settings.controlScheme == ControlScheme.TAP_ZONES,
            onClick = { onSetControlScheme(ControlScheme.TAP_ZONES) },
            modifier = Modifier.weight(1f).testTag("control_tap_zones"),
          )
        }

        // Mode selector
        SettingSectionTitle(strings.settingsMode)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          SelectablePill(
            label = strings.modeKids,
            selected = settings.gameMode == GameMode.KIDS,
            onClick = { onSetGameMode(GameMode.KIDS) },
            modifier = Modifier.weight(1f),
          )
          SelectablePill(
            label = strings.modeClassic,
            selected = settings.gameMode == GameMode.CLASSIC,
            onClick = { onSetGameMode(GameMode.CLASSIC) },
            modifier = Modifier.weight(1f),
          )
          SelectablePill(
            label = strings.modeHard,
            selected = settings.gameMode == GameMode.HARD,
            onClick = { onSetGameMode(GameMode.HARD) },
            modifier = Modifier.weight(1f),
          )
        }

        // Toggles card
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = palette.cardBackground,
          border = BorderStroke(1.dp, palette.cardBorder),
        ) {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SettingToggleRow(
              label = strings.settingsSound,
              checked = settings.soundEnabled,
              onCheckedChange = onSetSound,
              testTag = "toggle_sound",
            )
            SettingToggleRow(
              label = strings.settingsHaptics,
              checked = settings.hapticsEnabled,
              onCheckedChange = onSetHaptics,
              testTag = "toggle_haptics",
            )
            SettingToggleRow(
              label = strings.settingsTts,
              checked = settings.ttsEnabled,
              onCheckedChange = onSetTts,
              testTag = "toggle_tts",
            )
            SettingToggleRow(
              label = strings.settingsColorblind,
              checked = settings.colorblindSafe,
              onCheckedChange = onSetColorblind,
              testTag = "toggle_colorblind",
            )
            SettingToggleRow(
              label = strings.settingsLargeText,
              checked = settings.largeText,
              onCheckedChange = onSetLargeText,
              testTag = "toggle_large_text",
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun SettingSectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    color = MaterialTheme.colorScheme.onBackground,
  )
}

@Composable
private fun SelectablePill(
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

@Composable
private fun SettingToggleRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  val palette = LocalBoardPalette.current
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clickable { onCheckedChange(!checked) }
        .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f).padding(end = 12.dp),
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.testTag(testTag),
      colors =
        SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
          checkedTrackColor = palette.accent,
        ),
    )
  }
}
