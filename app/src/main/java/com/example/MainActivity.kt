package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.i18n.stringsFor
import com.example.ui.components.GrainBackground
import com.example.ui.components.UiAppearanceStore
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelMapScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.NothingTheme
import com.example.viewmodel.MainViewModel

object AppRoutes {
  const val HOME = "home"
  const val LEVEL_MAP = "level_map"
  const val GAME = "game"
  const val SETTINGS = "settings"
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val settings by viewModel.settings.collectAsStateWithLifecycle()
      val progress by viewModel.progress.collectAsStateWithLifecycle()
      val gameState by viewModel.gameState.collectAsStateWithLifecycle()

      val appearanceStore = remember { UiAppearanceStore(applicationContext) }
      var appearance by remember { mutableStateOf(appearanceStore.load()) }

      val strings = stringsFor(settings.language)
      val layoutDirection = if (settings.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

      CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        NothingTheme(
          darkVariant = appearance.darkVariant,
          colorblindSafe = settings.colorblindSafe,
          largeText = settings.largeText,
        ) {
          val navController = rememberNavController()

          Box(modifier = Modifier.fillMaxSize()) {
            GrainBackground()

            NavHost(
              navController = navController,
              startDestination = AppRoutes.HOME,
            ) {
              composable(AppRoutes.HOME) {
                HomeScreen(
                  strings = strings,
                  settings = settings,
                  progress = progress,
                  appearance = appearance,
                  onPlayContinue = { levelNumber ->
                    viewModel.startLevel(levelNumber)
                    navController.navigate(AppRoutes.GAME)
                  },
                  onPlayDaily = {
                    viewModel.startDailyChallenge()
                    navController.navigate(AppRoutes.GAME)
                  },
                  onOpenMap = { navController.navigate(AppRoutes.LEVEL_MAP) },
                  onOpenSettings = { navController.navigate(AppRoutes.SETTINGS) },
                  onToggleSound = { viewModel.setSoundEnabled(!settings.soundEnabled) },
                  onSelectMode = { mode -> viewModel.setGameMode(mode) },
                )
              }

              composable(AppRoutes.LEVEL_MAP) {
                LevelMapScreen(
                  strings = strings,
                  progress = progress,
                  hapticsEnabled = settings.hapticsEnabled,
                  onSelectLevel = { levelNumber ->
                    viewModel.startLevel(levelNumber)
                    navController.navigate(AppRoutes.GAME)
                  },
                  onBack = { navController.popBackStack() },
                )
              }

              composable(AppRoutes.GAME) {
                GameScreen(
                  gameState = gameState,
                  settings = settings,
                  appearance = appearance,
                  strings = strings,
                  onStartOrResume = { viewModel.startOrResumeGame() },
                  onPause = { viewModel.pauseGame() },
                  onRestart = { viewModel.restartGame() },
                  onSwipeDirection = { dir -> viewModel.onSwipeDirection(dir) },
                  onTapTurn = { left -> viewModel.onTapTurn(left) },
                  onNextLevel = { nextNum -> viewModel.startLevel(nextNum) },
                  onReplayLetterAudio = { nameAr -> viewModel.speakLetter(nameAr) },
                  onOpenMap = {
                    viewModel.stopGame()
                    navController.navigate(AppRoutes.LEVEL_MAP) {
                      popUpTo(AppRoutes.HOME)
                    }
                  },
                  onBackHome = {
                    viewModel.stopGame()
                    navController.popBackStack(AppRoutes.HOME, inclusive = false)
                  },
                  onDismissCoachmark = {
                    appearance =
                      appearance.copy(hasSeenGameCoachmark = true).also(appearanceStore::save)
                  },
                )
              }

              composable(AppRoutes.SETTINGS) {
                SettingsScreen(
                  strings = strings,
                  settings = settings,
                  appearance = appearance,
                  onSetLanguage = { viewModel.setLanguage(it) },
                  onSetControlScheme = { viewModel.setControlScheme(it) },
                  onSetGameMode = { viewModel.setGameMode(it) },
                  onSetSound = { viewModel.setSoundEnabled(it) },
                  onSetHaptics = { viewModel.setHapticsEnabled(it) },
                  onSetTts = { viewModel.setTtsEnabled(it) },
                  onSetColorblind = { viewModel.setColorblindSafe(it) },
                  onSetLargeText = { viewModel.setLargeText(it) },
                  onSetUseDotPixels = { enabled ->
                    appearance = appearance.copy(useDotPixels = enabled).also(appearanceStore::save)
                  },
                  onSetDarkVariant = { enabled ->
                    appearance = appearance.copy(darkVariant = enabled).also(appearanceStore::save)
                  },
                  onSetGlyphPulse = { enabled ->
                    appearance =
                      appearance.copy(glyphPulseEnabled = enabled).also(appearanceStore::save)
                  },
                  onBack = { navController.popBackStack() },
                )
              }
            }
          }
        }
      }
    }
  }
}
