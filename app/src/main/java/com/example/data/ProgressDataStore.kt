package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AppLanguage
import com.example.model.ControlScheme
import com.example.model.GameMode
import com.example.model.LevelProgress
import com.example.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by
  preferencesDataStore(name = "pixel_harf_prefs")

class ProgressDataStore(private val context: Context) {

  private object Keys {
    val LANGUAGE = stringPreferencesKey("language")
    val CONTROL_SCHEME = stringPreferencesKey("control_scheme")
    val GAME_MODE = stringPreferencesKey("game_mode")
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
    val COLORBLIND_SAFE = booleanPreferencesKey("colorblind_safe")
    val LARGE_TEXT = booleanPreferencesKey("large_text")

    val UNLOCKED_LEVEL = intPreferencesKey("unlocked_level")
    val STARS_MAP = stringPreferencesKey("stars_map")
    val SCORES_MAP = stringPreferencesKey("scores_map")
    val DAILY_COMPLETED_DATE = stringPreferencesKey("daily_completed_date")
    val DAILY_BEST_SCORE = intPreferencesKey("daily_best_score")
    val DAILY_STREAK = intPreferencesKey("daily_streak")
  }

  val settingsFlow: Flow<UserSettings> =
    context.dataStore.data.map { prefs ->
      UserSettings(
        language =
          prefs[Keys.LANGUAGE]?.let { code -> AppLanguage.entries.firstOrNull { it.name == code } }
            ?: AppLanguage.AR,
        controlScheme =
          prefs[Keys.CONTROL_SCHEME]?.let { name ->
            ControlScheme.entries.firstOrNull { it.name == name }
          } ?: ControlScheme.SWIPE,
        gameMode =
          prefs[Keys.GAME_MODE]?.let { name -> GameMode.entries.firstOrNull { it.name == name } }
            ?: GameMode.CLASSIC,
        soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
        hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
        ttsEnabled = prefs[Keys.TTS_ENABLED] ?: true,
        colorblindSafe = prefs[Keys.COLORBLIND_SAFE] ?: false,
        largeText = prefs[Keys.LARGE_TEXT] ?: false,
      )
    }

  val progressFlow: Flow<LevelProgress> =
    context.dataStore.data.map { prefs ->
      LevelProgress(
        unlockedLevel = (prefs[Keys.UNLOCKED_LEVEL] ?: 1).coerceAtLeast(1),
        starsByLevel = decodeIntMap(prefs[Keys.STARS_MAP]),
        bestScoreByLevel = decodeIntMap(prefs[Keys.SCORES_MAP]),
        dailyCompletedDate = prefs[Keys.DAILY_COMPLETED_DATE],
        dailyBestScore = prefs[Keys.DAILY_BEST_SCORE] ?: 0,
        dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0,
      )
    }

  suspend fun updateSettings(transform: (UserSettings) -> UserSettings) {
    context.dataStore.edit { prefs ->
      val current =
        UserSettings(
          language =
            prefs[Keys.LANGUAGE]?.let { code ->
              AppLanguage.entries.firstOrNull { it.name == code }
            } ?: AppLanguage.AR,
          controlScheme =
            prefs[Keys.CONTROL_SCHEME]?.let { name ->
              ControlScheme.entries.firstOrNull { it.name == name }
            } ?: ControlScheme.SWIPE,
          gameMode =
            prefs[Keys.GAME_MODE]?.let { name -> GameMode.entries.firstOrNull { it.name == name } }
              ?: GameMode.CLASSIC,
          soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
          hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
          ttsEnabled = prefs[Keys.TTS_ENABLED] ?: true,
          colorblindSafe = prefs[Keys.COLORBLIND_SAFE] ?: false,
          largeText = prefs[Keys.LARGE_TEXT] ?: false,
        )
      val next = transform(current)
      prefs[Keys.LANGUAGE] = next.language.name
      prefs[Keys.CONTROL_SCHEME] = next.controlScheme.name
      prefs[Keys.GAME_MODE] = next.gameMode.name
      prefs[Keys.SOUND_ENABLED] = next.soundEnabled
      prefs[Keys.HAPTICS_ENABLED] = next.hapticsEnabled
      prefs[Keys.TTS_ENABLED] = next.ttsEnabled
      prefs[Keys.COLORBLIND_SAFE] = next.colorblindSafe
      prefs[Keys.LARGE_TEXT] = next.largeText
    }
  }

  suspend fun recordLevelResult(
    levelNumber: Int,
    stars: Int,
    score: Int,
    completed: Boolean,
    totalLevels: Int,
  ) {
    context.dataStore.edit { prefs ->
      val scores = decodeIntMap(prefs[Keys.SCORES_MAP]).toMutableMap()
      val prevScore = scores[levelNumber] ?: 0
      if (score > prevScore) {
        scores[levelNumber] = score
        prefs[Keys.SCORES_MAP] = encodeIntMap(scores)
      }

      if (completed) {
        val starsMap = decodeIntMap(prefs[Keys.STARS_MAP]).toMutableMap()
        val prevStars = starsMap[levelNumber] ?: 0
        if (stars > prevStars) {
          starsMap[levelNumber] = stars
          prefs[Keys.STARS_MAP] = encodeIntMap(starsMap)
        }
        val currentUnlocked = prefs[Keys.UNLOCKED_LEVEL] ?: 1
        val nextLevel = (levelNumber + 1).coerceAtMost(totalLevels)
        if (nextLevel > currentUnlocked) {
          prefs[Keys.UNLOCKED_LEVEL] = nextLevel
        }
      }
    }
  }

  suspend fun recordDailyResult(dateKey: String, score: Int, completed: Boolean) {
    context.dataStore.edit { prefs ->
      val prevBest = prefs[Keys.DAILY_BEST_SCORE] ?: 0
      if (score > prevBest) {
        prefs[Keys.DAILY_BEST_SCORE] = score
      }
      if (completed) {
        val prevDate = prefs[Keys.DAILY_COMPLETED_DATE]
        if (prevDate != dateKey) {
          prefs[Keys.DAILY_COMPLETED_DATE] = dateKey
          prefs[Keys.DAILY_STREAK] = (prefs[Keys.DAILY_STREAK] ?: 0) + 1
        }
      }
    }
  }

  private fun encodeIntMap(map: Map<Int, Int>): String =
    map.entries.joinToString(",") { "${it.key}:${it.value}" }

  private fun decodeIntMap(raw: String?): Map<Int, Int> {
    if (raw.isNullOrBlank()) return emptyMap()
    val result = mutableMapOf<Int, Int>()
    raw.split(",").forEach { pair ->
      val parts = pair.split(":")
      if (parts.size == 2) {
        val k = parts[0].toIntOrNull()
        val v = parts[1].toIntOrNull()
        if (k != null && v != null) result[k] = v
      }
    }
    return result
  }
}
