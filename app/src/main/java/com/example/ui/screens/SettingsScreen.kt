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
import com.example.R
import com.example.i18n.AppStrings
import com.example.model.AppLanguage
import com.example.model.ControlScheme
import com.example.model.GameMode
import com.example.model.UserSettings
import com.example.ui.Dimens
import com.example.ui.components.CircleButton
import com.example.ui.components.DotIconType
import com.example.ui.components.DotText
import com.example.ui.components.NothingSwitch
import com.example.ui.components.UiAppearanceState
import com.example.ui.components.WidgetCard
import com.example.ui.components.widgetStaggerEntrance
import com.example.ui.theme.LocalNothingPalette
import com.example.ui.theme.WidgetRadius

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
  val palette = LocalNothingPalette.current

  Box(
    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 560.dp)
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceMd),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg),
    ) {
      // Header: single DotText title (<= 28sp) + 48dp Back CircleButton
      Row(
        modifier = Modifier.fillMaxWidth().height(Dimens.TopBarHeight).widgetStaggerEntrance(0),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
      ) {
        CircleButton(
          iconType = DotIconType.BACK,
          contentDescription = strings.backHome,
          onClick = onBack,
          size = Dimens.SmallCircleButtonSize,
          dotSize = 2.0.dp,
          mirrorForRtl = true,
          hapticsEnabled = settings.hapticsEnabled,
          modifier = Modifier.testTag("settings_back_button"),
        )
        DotText(
          text = strings.settings,
          fontResId = R.font.noto_kufi_arabic,
          dotSize = Dimens.DotTitleDotSize,
          color = palette.ink,
          targetRows = 14,
        )
      }

      SettingsGroupCard(index = 1, title = strings.settingsLanguage) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          AppLanguage.entries.forEach { lang ->
            SettingOptionPill(
              label = lang.nativeLabel,
              selected = settings.language == lang,
              onClick = { onSetLanguage(lang) },
              hapticsEnabled = settings.hapticsEnabled,
              modifier = Modifier.weight(1f).testTag("lang_${lang.code}"),
            )
          }
        }
      }

      SettingsGroupCard(index = 2, title = strings.settingsPixelStyle) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          SettingOptionPill(
            label = strings.styleDots,
            selected = appearance.useDotPixels,
            onClick = { onSetUseDotPixels(true) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f).testTag("style_dots"),
          )
          SettingOptionPill(
            label = strings.styleSquares,
            selected = !appearance.useDotPixels,
            onClick = { onSetUseDotPixels(false) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f).testTag("style_squares"),
          )
        }
      }

      SettingsGroupCard(index = 3, title = strings.settingsControls) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          SettingOptionPill(
            label = strings.controlSwipe,
            selected = settings.controlScheme == ControlScheme.SWIPE,
            onClick = { onSetControlScheme(ControlScheme.SWIPE) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f).testTag("control_swipe"),
          )
          SettingOptionPill(
            label = strings.controlTapZones,
            selected = settings.controlScheme == ControlScheme.TAP_ZONES,
            onClick = { onSetControlScheme(ControlScheme.TAP_ZONES) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f).testTag("control_tap_zones"),
          )
        }
      }

      SettingsGroupCard(index = 4, title = strings.settingsMode) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm),
        ) {
          SettingOptionPill(
            label = strings.modeKids,
            selected = settings.gameMode == GameMode.KIDS,
            onClick = { onSetGameMode(GameMode.KIDS) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f),
          )
          SettingOptionPill(
            label = strings.modeClassic,
            selected = settings.gameMode == GameMode.CLASSIC,
            onClick = { onSetGameMode(GameMode.CLASSIC) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f),
          )
          SettingOptionPill(
            label = strings.modeHard,
            selected = settings.gameMode == GameMode.HARD,
            onClick = { onSetGameMode(GameMode.HARD) },
            hapticsEnabled = settings.hapticsEnabled,
            modifier = Modifier.weight(1f),
          )
        }
      }

      WidgetCard(
        modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(5),
        shape = WidgetRadius.Card,
        cornerRadiusDp = Dimens.CardRadius,
      ) {
        Column(
          modifier = Modifier.padding(horizontal = Dimens.SpaceLg, vertical = Dimens.SpaceMd),
          verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
        ) {
          NothingToggleRow(
            label = strings.settingsDarkVariant,
            checked = appearance.darkVariant,
            onCheckedChange = onSetDarkVariant,
            testTag = "toggle_dark_variant",
          )
          NothingToggleRow(
            label = strings.settingsGlyphPulse,
            checked = appearance.glyphPulseEnabled,
            onCheckedChange = onSetGlyphPulse,
            testTag = "toggle_glyph_pulse",
          )
          NothingToggleRow(
            label = strings.settingsSound,
            checked = settings.soundEnabled,
            onCheckedChange = onSetSound,
            testTag = "toggle_sound",
          )
          NothingToggleRow(
            label = strings.settingsHaptics,
            checked = settings.hapticsEnabled,
            onCheckedChange = onSetHaptics,
            testTag = "toggle_haptics",
          )
          NothingToggleRow(
            label = strings.settingsTts,
            checked = settings.ttsEnabled,
            onCheckedChange = onSetTts,
            testTag = "toggle_tts",
          )
          NothingToggleRow(
            label = strings.settingsColorblind,
            checked = settings.colorblindSafe,
            onCheckedChange = onSetColorblind,
            testTag = "toggle_colorblind",
          )
          NothingToggleRow(
            label = strings.settingsLargeText,
            checked = settings.largeText,
            onCheckedChange = onSetLargeText,
            testTag = "toggle_large_text",
          )
        }
      }

      Spacer(modifier = Modifier.height(Dimens.SpaceMd))
    }
  }
}

@Composable
private fun SettingsGroupCard(
  index: Int,
  title: String,
  content: @Composable () -> Unit,
) {
  val palette = LocalNothingPalette.current
  WidgetCard(
    modifier = Modifier.fillMaxWidth().widgetStaggerEntrance(index),
    shape = WidgetRadius.Card,
    cornerRadiusDp = Dimens.CardRadius,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(Dimens.SpaceLg),
      verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMd),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontSize = Dimens.BodySp,
        fontWeight = FontWeight.SemiBold,
        color = palette.ink,
      )
      content()
    }
  }
}

@Composable
private fun SettingOptionPill(
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
    backgroundColor = if (selected) palette.accent else palette.widgetPressed,
    hapticsEnabled = hapticsEnabled,
    onClick = onClick,
  ) {
    Box(modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.SpaceSm), contentAlignment = Alignment.Center) {
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
private fun NothingToggleRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  val palette = LocalNothingPalette.current
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpaceXs),
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
    NothingSwitch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      contentDescription = label,
      modifier = Modifier.testTag(testTag),
    )
  }
}
