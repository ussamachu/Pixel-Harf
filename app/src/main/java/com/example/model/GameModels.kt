package com.example.model

import androidx.annotation.FontRes

enum class AppLanguage(val code: String, val isRtl: Boolean, val nativeLabel: String) {
  AR("ar", true, "العربية"),
  EN("en", false, "English"),
  FR("fr", false, "Français"),
}

enum class GameMode {
  KIDS,
  CLASSIC,
  HARD,
}

enum class ControlScheme {
  SWIPE,
  TAP_ZONES,
}

enum class Direction(val dx: Int, val dy: Int) {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1, 0);

  fun opposite(): Direction =
    when (this) {
      UP -> DOWN
      DOWN -> UP
      LEFT -> RIGHT
      RIGHT -> LEFT
    }

  fun turnLeft(): Direction =
    when (this) {
      UP -> LEFT
      LEFT -> DOWN
      DOWN -> RIGHT
      RIGHT -> UP
    }

  fun turnRight(): Direction =
    when (this) {
      UP -> RIGHT
      RIGHT -> DOWN
      DOWN -> LEFT
      LEFT -> UP
    }
}

data class GridPoint(val x: Int, val y: Int)

data class ArabicFontSpec(
  val id: String,
  @param:FontRes val fontResId: Int,
  val displayNameAr: String,
  val displayNameEn: String,
  val displayNameFr: String,
) {
  fun localizedName(language: AppLanguage): String =
    when (language) {
      AppLanguage.AR -> displayNameAr
      AppLanguage.EN -> displayNameEn
      AppLanguage.FR -> displayNameFr
    }
}

data class ArabicLetter(
  val id: String,
  val glyph: String,
  val isolatedGlyph: String,
  val nameAr: String,
  val nameEn: String,
  val nameFr: String,
  val world: Int,
  val hasDots: Boolean,
) {
  fun localizedName(language: AppLanguage): String =
    when (language) {
      AppLanguage.AR -> nameAr
      AppLanguage.EN -> nameEn
      AppLanguage.FR -> nameFr
    }
}

data class LevelSpec(
  val levelNumber: Int,
  val world: Int,
  val letter: ArabicLetter,
  val fontSpec: ArabicFontSpec,
  val isBoss: Boolean,
  val gridCols: Int,
  val gridRows: Int,
  val tickMs: Long,
  val wallsWrap: Boolean,
  val tailCollision: Boolean,
  val hasGhostPixels: Boolean,
  val timeLimitSec: Int?,
)

enum class PowerUpType {
  SLOW_MO,
  MAGNET,
  GHOST,
  SHRINK,
}

data class PowerUpItem(
  val point: GridPoint,
  val type: PowerUpType,
  val remainingMs: Long,
)

data class GhostPixel(
  val point: GridPoint,
  val remainingMs: Long,
  val totalMs: Long,
)

data class Particle(
  val x: Float,
  val y: Float,
  val vx: Float,
  val vy: Float,
  val life: Float,
  val maxLife: Float,
  val colorKind: Int, // 0 = ink/black, 1 = orange accent, 2 = bonus gold/blue, 3 = confetti
  val sizeFactor: Float,
)

enum class GameStatus {
  READY,
  PLAYING,
  PAUSED,
  LEVEL_COMPLETE,
  GAME_OVER,
}

enum class DeathReason {
  WALL,
  TAIL,
  TIME_UP,
}

data class GameState(
  val levelSpec: LevelSpec,
  val mode: GameMode,
  val isDailyChallenge: Boolean = false,
  val mirroredControls: Boolean = false,
  val gridCols: Int = levelSpec.gridCols,
  val gridRows: Int = levelSpec.gridRows,
  val baselineRow: Int = levelSpec.gridRows - 2,
  val totalLetterPixels: Int = 0,
  val remainingPixels: List<GridPoint> = emptyList(),
  val dotPixels: Set<GridPoint> = emptySet(),
  val baselinePixels: List<GridPoint> = emptyList(),
  val snake: List<GridPoint> = emptyList(),
  val direction: Direction = Direction.RIGHT,
  val lives: Int = 3,
  val maxLives: Int = 3,
  val score: Int = 0,
  val bestScore: Int = 0,
  val combo: Int = 0,
  val maxCombo: Int = 0,
  val comboTimerMs: Long = 0L,
  val activePowerUp: PowerUpItem? = null,
  val activeEffect: PowerUpType? = null,
  val effectRemainingMs: Long = 0L,
  val ghostPixel: GhostPixel? = null,
  val hintPixel: GridPoint? = null,
  val particles: List<Particle> = emptyList(),
  val elapsedMs: Long = 0L,
  val timeRemainingMs: Long? = null,
  val screenShakeMs: Long = 0L,
  val status: GameStatus = GameStatus.READY,
  val starsEarned: Int = 0,
  val deathReason: DeathReason? = null,
  val calligraphicRevealActive: Boolean = false,
)

data class UserSettings(
  val language: AppLanguage = AppLanguage.AR,
  val controlScheme: ControlScheme = ControlScheme.SWIPE,
  val gameMode: GameMode = GameMode.CLASSIC,
  val soundEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true,
  val ttsEnabled: Boolean = true,
  val colorblindSafe: Boolean = false,
  val largeText: Boolean = false,
)

data class LevelProgress(
  val unlockedLevel: Int = 1,
  val starsByLevel: Map<Int, Int> = emptyMap(),
  val bestScoreByLevel: Map<Int, Int> = emptyMap(),
  val dailyCompletedDate: String? = null,
  val dailyBestScore: Int = 0,
  val dailyStreak: Int = 0,
)
