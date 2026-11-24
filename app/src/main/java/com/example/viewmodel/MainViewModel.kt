package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticHelper
import com.example.audio.LetterSpeaker
import com.example.audio.RetroSoundEngine
import com.example.data.LevelCatalog
import com.example.data.ProgressDataStore
import com.example.engine.EngineFeedback
import com.example.engine.GameEngine
import com.example.model.AppLanguage
import com.example.model.ControlScheme
import com.example.model.Direction
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.LevelProgress
import com.example.model.LevelSpec
import com.example.model.UserSettings
import com.example.raster.LetterRasterizer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val dataStore = ProgressDataStore(application)
  val rasterizer = LetterRasterizer(application)
  private val soundEngine = RetroSoundEngine()
  private val haptics = HapticHelper(application)
  private val speaker = LetterSpeaker(application)

  val settings: StateFlow<UserSettings> =
    dataStore.settingsFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UserSettings(),
    )

  val progress: StateFlow<LevelProgress> =
    dataStore.progressFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = LevelProgress(),
    )

  private val engineFeedback =
    object : EngineFeedback {
      override fun onEat(combo: Int, isDot: Boolean) {
        if (isDot) {
          soundEngine.playBonusDot()
          haptics.powerUp()
        } else {
          soundEngine.playEat(combo)
          haptics.tick()
        }
      }

      override fun onPowerUp() {
        soundEngine.playPowerUp()
        haptics.powerUp()
      }

      override fun onHurt() {
        soundEngine.playHurt()
        haptics.hurt()
      }

      override fun onLevelComplete(letterNameAr: String) {
        soundEngine.playLevelComplete()
        haptics.complete()
        if (settings.value.ttsEnabled) {
          speaker.speakLetter(letterNameAr)
        }
      }

      override fun onGameOver() {
        soundEngine.playGameOver()
        haptics.hurt()
      }
    }

  private val engine =
    GameEngine(
      scope = viewModelScope,
      rasterizer = rasterizer,
      feedback = engineFeedback,
      onPersistResult = { spec, isDaily, stars, score, completed ->
        viewModelScope.launch {
          if (isDaily) {
            dataStore.recordDailyResult(LevelCatalog.todayKey(), score, completed)
          } else {
            dataStore.recordLevelResult(
              levelNumber = spec.levelNumber,
              stars = stars,
              score = score,
              completed = completed,
              totalLevels = LevelCatalog.levels.size,
            )
          }
        }
      },
    )

  val gameState: StateFlow<GameState> = engine.state

  init {
    viewModelScope.launch {
      settings.collect { s ->
        soundEngine.enabled = s.soundEnabled
        haptics.enabled = s.hapticsEnabled
      }
    }
  }

  fun startLevel(levelNumber: Int) {
    val spec = LevelCatalog.levelByNumber(levelNumber)
    val best = progress.value.bestScoreByLevel[levelNumber] ?: 0
    engine.loadLevel(
      levelSpec = spec,
      mode = settings.value.gameMode,
      bestScore = best,
      isDailyChallenge = false,
      mirroredControls = false,
    )
  }

  fun startDailyChallenge() {
    val daily = LevelCatalog.dailyChallenge()
    val best = progress.value.dailyBestScore
    engine.loadLevel(
      levelSpec = daily.levelSpec,
      mode = settings.value.gameMode,
      bestScore = best,
      isDailyChallenge = true,
      mirroredControls = daily.mirroredControls,
    )
  }

  fun startOrResumeGame() = engine.startOrResume()

  fun pauseGame() = engine.pause()

  fun restartGame() = engine.restart()

  fun stopGame() = engine.stopLoops()

  fun onSwipeDirection(direction: Direction) = engine.queueDirection(direction)

  fun onTapTurn(left: Boolean) = engine.turnRelative(left)

  fun speakLetter(letterNameAr: String) {
    if (settings.value.ttsEnabled) {
      speaker.speakLetter(letterNameAr)
    }
  }

  fun setLanguage(lang: AppLanguage) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(language = lang) } }

  fun setControlScheme(scheme: ControlScheme) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(controlScheme = scheme) } }

  fun setGameMode(mode: GameMode) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(gameMode = mode) } }

  fun setSoundEnabled(enabled: Boolean) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(soundEnabled = enabled) } }

  fun setHapticsEnabled(enabled: Boolean) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(hapticsEnabled = enabled) } }

  fun setTtsEnabled(enabled: Boolean) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(ttsEnabled = enabled) } }

  fun setColorblindSafe(enabled: Boolean) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(colorblindSafe = enabled) } }

  fun setLargeText(enabled: Boolean) =
    viewModelScope.launch { dataStore.updateSettings { it.copy(largeText = enabled) } }

  override fun onCleared() {
    super.onCleared()
    engine.stopLoops()
    soundEngine.release()
    speaker.shutdown()
  }
}
