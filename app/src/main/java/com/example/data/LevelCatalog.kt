package com.example.data

import com.example.R
import com.example.model.ArabicFontSpec
import com.example.model.ArabicLetter
import com.example.model.LevelSpec
import java.util.Calendar
import kotlin.math.abs

object LevelCatalog {

  val fonts: List<ArabicFontSpec> =
    listOf(
      ArabicFontSpec(
        id = "noto_kufi",
        fontResId = R.font.noto_kufi_arabic,
        displayNameAr = "كوفي حديث",
        displayNameEn = "Noto Kufi",
        displayNameFr = "Noto Kufi",
      ),
      ArabicFontSpec(
        id = "reem_kufi",
        fontResId = R.font.reem_kufi,
        displayNameAr = "ريم كوفي",
        displayNameEn = "Reem Kufi",
        displayNameFr = "Reem Kufi",
      ),
      ArabicFontSpec(
        id = "noto_naskh",
        fontResId = R.font.noto_naskh_arabic,
        displayNameAr = "نسخ أصيل",
        displayNameEn = "Noto Naskh",
        displayNameFr = "Noto Naskh",
      ),
      ArabicFontSpec(
        id = "amiri",
        fontResId = R.font.amiri,
        displayNameAr = "أميري",
        displayNameEn = "Amiri",
        displayNameFr = "Amiri",
      ),
    )

  val calligraphicFont: ArabicFontSpec = fonts[3] // Amiri

  private val world1Letters =
    listOf(
      ArabicLetter("alif", "ا", "ا", "ألف", "Alif", "Alif", 1, false),
      ArabicLetter("dal", "د", "د", "دال", "Dal", "Dal", 1, false),
      ArabicLetter("dhal", "ذ", "ذ", "ذال", "Dhal", "Dhal", 1, true),
      ArabicLetter("ra", "ر", "ر", "راء", "Ra", "Ra", 1, false),
      ArabicLetter("zay", "ز", "ز", "زاي", "Zay", "Zay", 1, true),
      ArabicLetter("waw", "و", "و", "واو", "Waw", "Waw", 1, false),
      ArabicLetter("lam", "ل", "ل", "لام", "Lam", "Lam", 1, false),
    )

  private val world2Letters =
    listOf(
      ArabicLetter("ba", "ب", "ب", "باء", "Ba", "Ba", 2, true),
      ArabicLetter("ta", "ت", "ت", "تاء", "Ta", "Ta", 2, true),
      ArabicLetter("tha", "ث", "ث", "ثاء", "Tha", "Tha", 2, true),
      ArabicLetter("nun", "ن", "ن", "نون", "Nun", "Noun", 2, true),
      ArabicLetter("ya", "ي", "ي", "ياء", "Ya", "Ya", 2, true),
      ArabicLetter("sin", "س", "س", "سين", "Sin", "Sin", 2, false),
      ArabicLetter("shin", "ش", "ش", "شين", "Shin", "Shin", 2, true),
      ArabicLetter("mim", "م", "م", "ميم", "Mim", "Mim", 2, false),
    )

  private val world3Letters =
    listOf(
      ArabicLetter("jim", "ج", "ج", "جيم", "Jim", "Jim", 3, true),
      ArabicLetter("ha", "ح", "ح", "حاء", "Ha", "Ha", 3, false),
      ArabicLetter("kha", "خ", "خ", "خاء", "Kha", "Kha", 3, true),
      ArabicLetter("ayn", "ع", "ع", "عين", "Ayn", "Ayn", 3, false),
      ArabicLetter("ghayn", "غ", "غ", "غين", "Ghayn", "Ghayn", 3, true),
      ArabicLetter("fa", "ف", "ف", "فاء", "Fa", "Fa", 3, true),
      ArabicLetter("qaf", "ق", "ق", "قاف", "Qaf", "Qaf", 3, true),
      ArabicLetter("kaf", "ك", "ك", "كاف", "Kaf", "Kaf", 3, false),
      ArabicLetter("he", "ه", "ه", "هاء", "Ha (H)", "Ha (H)", 3, false),
      ArabicLetter("sad", "ص", "ص", "صاد", "Sad", "Sad", 3, false),
      ArabicLetter("dad", "ض", "ض", "ضاد", "Dad", "Dad", 3, true),
      ArabicLetter("taa", "ط", "ط", "طاء", "Ta (T)", "Ta (T)", 3, false),
      ArabicLetter("dhaa", "ظ", "ظ", "ظاء", "Zha", "Zha", 3, true),
    )

  private val world4Forms =
    listOf(
      ArabicLetter("ba_init", "بـ", "ب", "باء أولية", "Initial Ba", "Ba initial", 4, true),
      ArabicLetter("ba_med", "ـبـ", "ب", "باء وسطية", "Medial Ba", "Ba médian", 4, true),
      ArabicLetter("ba_fin", "ـب", "ب", "باء متصلة", "Final Ba", "Ba final", 4, true),
      ArabicLetter("jim_init", "جـ", "ج", "جيم أولية", "Initial Jim", "Jim initial", 4, true),
      ArabicLetter("ayn_med", "ـعـ", "ع", "عين وسطية", "Medial Ayn", "Ayn médian", 4, false),
      ArabicLetter("qaf_fin", "ـق", "ق", "قاف متصلة", "Final Qaf", "Qaf final", 4, true),
      ArabicLetter("kaf_init", "كـ", "ك", "كاف أولية", "Initial Kaf", "Kaf initial", 4, false),
      ArabicLetter("he_med", "ـهـ", "ه", "هاء وسطية", "Medial Ha", "Ha médian", 4, false),
      ArabicLetter("mim_fin", "ـم", "م", "ميم متصلة", "Final Mim", "Mim final", 4, false),
      ArabicLetter("shin_init", "شـ", "ش", "شين أولية", "Initial Shin", "Shin initial", 4, true),
      ArabicLetter("nun_med", "ـنـ", "ن", "نون وسطية", "Medial Nun", "Noun médian", 4, true),
      ArabicLetter("ya_fin", "ـي", "ي", "ياء متصلة", "Final Ya", "Ya final", 4, true),
    )

  val levels: List<LevelSpec> by lazy { buildLevels() }

  private fun buildLevels(): List<LevelSpec> {
    val result = mutableListOf<LevelSpec>()
    var num = 1

    // World 1: Simple shapes (Levels 1..7)
    world1Letters.forEachIndexed { idx, letter ->
      val isBoss = num % 5 == 0
      val baseGrid = if (isBoss) 22 else (16 + (idx / 3) * 2)
      val font = fonts[0]
      result +=
        LevelSpec(
          levelNumber = num,
          world = 1,
          letter = letter,
          fontSpec = font,
          isBoss = isBoss,
          gridCols = baseGrid,
          gridRows = baseGrid,
          tickMs = (168L - idx * 4L).coerceAtLeast(136L),
          wallsWrap = true,
          tailCollision = false,
          hasGhostPixels = false,
          timeLimitSec = if (isBoss) 65 else null,
        )
      num++
    }

    // World 2: Medium (Levels 8..15)
    world2Letters.forEachIndexed { idx, letter ->
      val isBoss = num % 5 == 0
      val baseGrid = if (isBoss) 24 else (19 + (idx / 3) * 2)
      val font = fonts[if (idx % 3 == 0) 1 else 0]
      result +=
        LevelSpec(
          levelNumber = num,
          world = 2,
          letter = letter,
          fontSpec = font,
          isBoss = isBoss,
          gridCols = baseGrid,
          gridRows = baseGrid,
          tickMs = (146L - idx * 3L).coerceAtLeast(118L),
          wallsWrap = false,
          tailCollision = false,
          hasGhostPixels = false,
          timeLimitSec = if (isBoss) 75 else null,
        )
      num++
    }

    // World 3: Complex (Levels 16..28)
    world3Letters.forEachIndexed { idx, letter ->
      val isBoss = num % 5 == 0
      val baseGrid = if (isBoss) 26 else (21 + (idx / 4) * 2)
      val font = fonts[(idx + 1) % 3]
      result +=
        LevelSpec(
          levelNumber = num,
          world = 3,
          letter = letter,
          fontSpec = font,
          isBoss = isBoss,
          gridCols = baseGrid,
          gridRows = baseGrid,
          tickMs = (128L - idx * 2L).coerceAtLeast(98L),
          wallsWrap = false,
          tailCollision = true,
          hasGhostPixels = false,
          timeLimitSec = if (isBoss) 85 else null,
        )
      num++
    }

    // World 4: Master (Levels 29..40) - different fonts + initial/medial/final forms + ghost pixels
    world4Forms.forEachIndexed { idx, letter ->
      val isBoss = num % 5 == 0
      val baseGrid = if (isBoss) 28 else (23 + (idx / 4) * 2)
      val font = fonts[(idx + 1) % fonts.size]
      result +=
        LevelSpec(
          levelNumber = num,
          world = 4,
          letter = letter,
          fontSpec = font,
          isBoss = isBoss,
          gridCols = baseGrid,
          gridRows = baseGrid,
          tickMs = (112L - idx * 2L).coerceAtLeast(86L),
          wallsWrap = false,
          tailCollision = true,
          hasGhostPixels = true,
          timeLimitSec = if (isBoss) 95 else null,
        )
      num++
    }

    return result
  }

  fun levelByNumber(levelNumber: Int): LevelSpec =
    levels.firstOrNull { it.levelNumber == levelNumber } ?: levels.first()

  fun todayKey(): String {
    val cal = Calendar.getInstance()
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(y, m, d)
  }

  data class DailyChallengeSpec(
    val dateKey: String,
    val levelSpec: LevelSpec,
    val mirroredControls: Boolean,
  )

  fun dailyChallenge(): DailyChallengeSpec {
    val key = todayKey()
    val seed = abs(key.hashCode())
    val allLetters = world1Letters + world2Letters + world3Letters
    val letter = allLetters[seed % allLetters.size]
    val font = fonts[(seed / 7) % fonts.size]
    val mirrored = (seed / 13) % 2 == 0
    val spec =
      LevelSpec(
        levelNumber = 0,
        world = 4,
        letter = letter,
        fontSpec = font,
        isBoss = true,
        gridCols = 24,
        gridRows = 24,
        tickMs = 115L,
        wallsWrap = false,
        tailCollision = true,
        hasGhostPixels = true,
        timeLimitSec = 90,
      )
    return DailyChallengeSpec(dateKey = key, levelSpec = spec, mirroredControls = mirrored)
  }
}
