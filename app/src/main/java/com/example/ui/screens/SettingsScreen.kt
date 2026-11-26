package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.ControlScheme
import com.example.model.GameMode
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.CircleButton
import com.example.ui.components.FlatCard
import com.example.ui.components.FlatSwitch
import com.example.ui.components.HairlineDivider
import com.example.ui.components.PixelIcon
import com.example.ui.components.UiAppearanceState
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.AppShapes
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalPalette

@Composable
fun SettingsScreen(
  strings: AppStrings,
  settings: UserSettings,
  appearance: UiAppearanceState,
  onSetLanguage: (AppLanguage) -> Unit,
  onSetControlScheme: (ControlScheme) -> Unit,
  onSetGameMode: (GameMode) -> Unit,
  onSetSound: (Boolean) -> Unit,
  onSetHaptics: (Boolean) -> Unit,
  onSetTts: (Boolean) -> Unit,
  onSetColorblind: (Boolean) -> Unit,
  onSetLargeText: (Boolean) -> Unit,
  onSetUseDotPixels: (Boolean) -> Unit,
  onSetDarkVariant: (Boolean) -> Unit,
  onSetGlyphPulse: (Boolean) -> Unit,
  onBack: () -> Unit,
) {
  val palette = LocalPalette.current

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.widthIn(max = 560.dp).fillMaxSize()) {
      // Top bar
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .height(Dimens.TopBarHeight)
            .padding(horizontal = Dimens.ScreenPadding)
            .widgetStaggerEntrance(0),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
      ) {
        CircleButton(
          iconType = PixelIcon.BACK,
          contentDescription = strings.backHome,
          onClick = onBack,
          size = Dimens.CircleButtonSize,
          iconSize = 24.dp,
          mirrorForRtl = true,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.testTag("settings_back_button"),
        )
        Text(
          text = strings.settings,
          style = MaterialTheme.typography.titleLarge,
          fontSize = Dimens.TitleSp,
          color = palette.ink,
        )
      }

      HairlineDivider(modifier = Modifier.fillMaxWidth())

      Column(
        modifier =
          Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceLg),
        verticalArrangement = Arrangement.spacedBy(Dimens.SectionGap),
      ) {
        // 1. CONTROLS
        SettingsSection(
          index = 1,
          monoTag = strings.sectionControlsTag,
          arabicTitle = strings.settingsControls,
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              FlatOptionButton(
                label = strings.controlSwipe,
                selected = settings.controlScheme == ControlScheme.SWIPE,
                onClick = { onSetControlScheme(ControlScheme.SWIPE) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("control_swipe"),
              )
              FlatOptionButton(
                label = strings.controlTapZones,
                selected = settings.controlScheme == ControlScheme.TAP_ZONES,
                onClick = { onSetControlScheme(ControlScheme.TAP_ZONES) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("control_tap_zones"),
              )
            }
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            Text(
              text = strings.settingsMode,
              style = MaterialTheme.typography.bodyMedium,
              fontSize = Dimens.CaptionSp,
              color = palette.mutedInk,
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              FlatOptionButton(
                label = strings.modeKids,
                selected = settings.gameMode == GameMode.KIDS,
                onClick = { onSetGameMode(GameMode.KIDS) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f),
              )
              FlatOptionButton(
                label = strings.modeClassic,
                selected = settings.gameMode == GameMode.CLASSIC,
                onClick = { onSetGameMode(GameMode.CLASSIC) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f),
              )
              FlatOptionButton(
                label = strings.modeHard,
                selected = settings.gameMode == GameMode.HARD,
                onClick = { onSetGameMode(GameMode.HARD) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f),
              )
            }
          }
        }

        // 2. APPEARANCE
        SettingsSection(
          index = 2,
          monoTag = strings.sectionAppearanceTag,
          arabicTitle = strings.settingsPixelStyle,
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
            ) {
              FlatOptionButton(
                label = strings.styleSquares,
                selected = !appearance.useDotPixels,
                onClick = { onSetUseDotPixels(false) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("style_squares"),
              )
              FlatOptionButton(
                label = strings.styleDots,
                selected = appearance.useDotPixels,
                onClick = { onSetUseDotPixels(true) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("style_dots"),
              )
            }
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsDarkVariant,
              checked = appearance.darkVariant,
              onCheckedChange = onSetDarkVariant,
              testTag = "toggle_dark_variant",
            )
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsGlyphPulse,
              checked = appearance.glyphPulseEnabled,
              onCheckedChange = onSetGlyphPulse,
              testTag = "toggle_glyph_pulse",
            )
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsColorblind,
              checked = settings.colorblindSafe,
              onCheckedChange = onSetColorblind,
              testTag = "toggle_colorblind",
            )
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsLargeText,
              checked = settings.largeText,
              onCheckedChange = onSetLargeText,
              testTag = "toggle_large_text",
            )
          }
        }

        // 3. LANGUAGE
        SettingsSection(
          index = 3,
          monoTag = strings.sectionLanguageTag,
          arabicTitle = strings.settingsLanguage,
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
          ) {
            AppLanguage.entries.forEach { lang ->
              FlatOptionButton(
                label = lang.nativeLabel,
                selected = settings.language == lang,
                onClick = { onSetLanguage(lang) },
                hapticsEnabled = settings.hapticsEnabled,
                modifier = Modifier.weight(1f).testTag("lang_${lang.code}"),
              )
            }
          }
        }

        // 4. SOUND
        SettingsSection(
          index = 4,
          monoTag = strings.sectionSoundTag,
          arabicTitle = strings.soundLabel,
        ) {
          Column {
            FlatToggleRow(
              label = strings.settingsSound,
              checked = settings.soundEnabled,
              onCheckedChange = onSetSound,
              testTag = "toggle_sound",
            )
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsHaptics,
              checked = settings.hapticsEnabled,
              onCheckedChange = onSetHaptics,
              testTag = "toggle_haptics",
            )
            HairlineDivider(modifier = Modifier.fillMaxWidth())
            FlatToggleRow(
              label = strings.settingsTts,
              checked = settings.ttsEnabled,
              onCheckedChange = onSetTts,
              testTag = "toggle_tts",
            )
          }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLg))
      }
    }
  }
}

@Composable
private fun SettingsSection(
  index: Int,
  monoTag: String,
  arabicTitle: String,
  content: @Composable () -> Unit,
) {
  val palette = LocalPalette.current
  Column(
    modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(index),
    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
    ) {
      Text(
        text = monoTag,
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.CaptionSp,
        color = palette.accent,
      )
      Text(
        text = arabicTitle,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = Dimens.BodySp,
        fontWeight = FontWeight.SemiBold,
        color = palette.ink,
      )
    }
    FlatCard(
      modifier = Modifier.fillMaxWidth(),
      shape = AppShapes.Card,
    ) {
      Box(modifier = Modifier.fillMaxWidth().padding(Dimens.SpaceLg)) {
        content()
      }
    }
  }
}

@Composable
private fun FlatOptionButton(
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
    Box(
      modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceSm),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontSize = Dimens.BodySp,
        color = if (selected) Color.White else palette.ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun FlatToggleRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  val palette = LocalPalette.current
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      fontSize = Dimens.BodySp,
      color = palette.ink,
      modifier = Modifier.weight(1f).padding(end = Dimens.SpaceMd),
    )
    FlatSwitch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      contentDescription = label,
      modifier = Modifier.testTag(testTag),
    )
  }
}
