package com.example.i18n

import com.example.model.AppLanguage
import com.example.model.DeathReason
import com.example.model.GameMode
import com.example.model.PowerUpType

data class AppStrings(
  val appTitle: String,
  val appSubtitle: String,
  val playNow: String,
  val continueLevel: (Int) -> String,
  val dailyChallenge: String,
  val dailyCompleted: String,
  val dailyStreak: (Int) -> String,
  val levelMap: String,
  val settings: String,
  val modesTitle: String,
  val modeKids: String,
  val modeKidsDesc: String,
  val modeClassic: String,
  val modeClassicDesc: String,
  val modeHard: String,
  val modeHardDesc: String,
  val levelHeader: (glyph: String, fontName: String) -> String,
  val bossBadge: String,
  val mirroredBadge: String,
  val tapToStartHint: (bestScore: Int) -> String,
  val scoreLabel: String,
  val pixelsLeft: (left: Int, total: Int) -> String,
  val comboBadge: (Int) -> String,
  val pausedTitle: String,
  val resume: String,
  val restart: String,
  val backToMap: String,
  val backHome: String,
  val levelCompleteTitle: String,
  val nextLevel: String,
  val replay: String,
  val gameOverTitle: String,
  val deathReasonLabel: (DeathReason?) -> String,
  val worldTitle: (Int) -> String,
  val worldDesc: (Int) -> String,
  val powerUpLabel: (PowerUpType) -> String,
  val tapTurnLeft: String,
  val tapTurnRight: String,
  val settingsLanguage: String,
  val settingsControls: String,
  val controlSwipe: String,
  val controlTapZones: String,
  val settingsMode: String,
  val settingsSound: String,
  val settingsHaptics: String,
  val settingsTts: String,
  val settingsColorblind: String,
  val settingsLargeText: String,
  val levelNumLabel: (Int) -> String,
  val starsTotal: (Int, Int) -> String,
)

fun stringsFor(language: AppLanguage): AppStrings =
  when (language) {
    AppLanguage.AR ->
      AppStrings(
        appTitle = "حرف بيكسل",
        appSubtitle = "الْتَهِم نقاط الحرف العربي وأَحيِ الخط",
        playNow = "ابدأ اللعب",
        continueLevel = { lvl -> "متابعة · المستوى $lvl" },
        dailyChallenge = "التحدي اليومي",
        dailyCompleted = "مكتمل اليوم ✓",
        dailyStreak = { s -> "سلسلة $s أيام" },
        levelMap = "خريطة الحروف",
        settings = "الإعدادات",
        modesTitle = "وضع اللعب",
        modeKids = "الأطفال",
        modeKidsDesc = "بدون خسارة · سرعة هادئة · تلميح النقطة التالية",
        modeClassic = "كلاسيكي",
        modeClassicDesc = "تحدٍ متوازن مع نجوم التقييم",
        modeHard = "صعب",
        modeHardDesc = "سرعة عالية · حياتان فقط",
        levelHeader = { glyph, fontName -> "حرف «$glyph» — من خط $fontName" },
        bossBadge = "زعيم",
        mirroredBadge = "تحكم معكوس",
        tapToStartHint = { best -> "اضغط لتبدأ · الأسهم ← → · أفضل نتيجة $best" },
        scoreLabel = "النقاط",
        pixelsLeft = { left, total -> "$left / $total" },
        comboBadge = { c -> "×$c" },
        pausedTitle = "استراحة قصيرة",
        resume = "استئناف",
        restart = "إعادة",
        backToMap = "خريطة الحروف",
        backHome = "الرئيسية",
        levelCompleteTitle = "أحسنت!",
        nextLevel = "المستوى التالي",
        replay = "إعادة اللعب",
        gameOverTitle = "انتهت المحاولات",
        deathReasonLabel = { reason ->
          when (reason) {
            DeathReason.WALL -> "اصطدام بالجدار"
            DeathReason.TAIL -> "اصطدام بالذيل"
            DeathReason.TIME_UP -> "انتهى الوقت"
            null -> "حاول مجدداً"
          }
        },
        worldTitle = { w ->
          when (w) {
            1 -> "العالم ١ · الحروف البسيطة"
            2 -> "العالم ٢ · الحروف المتوسطة"
            3 -> "العالم ٣ · الحروف المركّبة"
            else -> "العالم ٤ · الإتقان والأشكال"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "جدران ملتفة · بدون اصطدام بالذيل"
            2 -> "جدران صلبة · النقاط تمنح مكافأة"
            3 -> "اصطدام الذيل مفعّل · دقة أعلى"
            else -> "خطوط متنوعة · أشكال متصلة · بيكسل شبح متحرك"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "إبطاء"
            PowerUpType.MAGNET -> "مغناطيس"
            PowerUpType.GHOST -> "شبح ٥ث"
            PowerUpType.SHRINK -> "تقصير الذيل"
          }
        },
        tapTurnLeft = "يسار ↶",
        tapTurnRight = "يمين ↷",
        settingsLanguage = "اللغة",
        settingsControls = "التحكم",
        controlSwipe = "سحب باللمس",
        controlTapZones = "زران يمين / يسار",
        settingsMode = "مستوى اللعب",
        settingsSound = "مؤثرات 8-بت الصوتية",
        settingsHaptics = "الاهتزاز",
        settingsTts = "نطق الحرف بعد الفوز",
        settingsColorblind = "ألوان مناسبة لعمى الألوان",
        settingsLargeText = "تكبير الخط",
        levelNumLabel = { n -> "م$n" },
        starsTotal = { earned, total -> "$earned / $total ★" },
      )
    AppLanguage.EN ->
      AppStrings(
        appTitle = "Pixel Harf",
        appSubtitle = "Eat every pixel of the Arabic letter",
        playNow = "Play",
        continueLevel = { lvl -> "Continue · Level $lvl" },
        dailyChallenge = "Daily Challenge",
        dailyCompleted = "Done Today ✓",
        dailyStreak = { s -> "$s day streak" },
        levelMap = "Letter Map",
        settings = "Settings",
        modesTitle = "Game Mode",
        modeKids = "Kids",
        modeKidsDesc = "No death · gentle speed · next-pixel hints",
        modeClassic = "Classic",
        modeClassicDesc = "Balanced progression & 3-star mastery",
        modeHard = "Hard",
        modeHardDesc = "High speed · 2 lives only",
        levelHeader = { glyph, fontName -> "Letter «$glyph» — $fontName" },
        bossBadge = "BOSS",
        mirroredBadge = "Mirrored",
        tapToStartHint = { best -> "Tap to start · Arrows ← → · Best $best" },
        scoreLabel = "SCORE",
        pixelsLeft = { left, total -> "$left / $total" },
        comboBadge = { c -> "×$c" },
        pausedTitle = "Paused",
        resume = "Resume",
        restart = "Restart",
        backToMap = "Level Map",
        backHome = "Home",
        levelCompleteTitle = "Letter Cleared!",
        nextLevel = "Next Level",
        replay = "Replay",
        gameOverTitle = "Game Over",
        deathReasonLabel = { reason ->
          when (reason) {
            DeathReason.WALL -> "Hit the border"
            DeathReason.TAIL -> "Hit your tail"
            DeathReason.TIME_UP -> "Time ran out"
            null -> "Try again"
          }
        },
        worldTitle = { w ->
          when (w) {
            1 -> "World 1 · Simple Shapes"
            2 -> "World 2 · Medium Letters"
            3 -> "World 3 · Complex Letters"
            else -> "World 4 · Master Forms"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "Walls wrap · tail safe"
            2 -> "Solid borders · bonus dot targets"
            3 -> "Tail collision active · denser grid"
            else -> "Multiple fonts · connected forms · moving ghost pixel"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "Slow-Mo"
            PowerUpType.MAGNET -> "Magnet"
            PowerUpType.GHOST -> "Ghost 5s"
            PowerUpType.SHRINK -> "Shrink"
          }
        },
        tapTurnLeft = "↶ Left",
        tapTurnRight = "Right ↷",
        settingsLanguage = "Language",
        settingsControls = "Controls",
        controlSwipe = "Swipe",
        controlTapZones = "Left / Right Tap Zones",
        settingsMode = "Difficulty Mode",
        settingsSound = "8-Bit Sound Effects",
        settingsHaptics = "Haptics",
        settingsTts = "Speak Letter Name (Arabic TTS)",
        settingsColorblind = "Colorblind-Safe Palette",
        settingsLargeText = "Large Text",
        levelNumLabel = { n -> "L$n" },
        starsTotal = { earned, total -> "$earned / $total ★" },
      )
    AppLanguage.FR ->
      AppStrings(
        appTitle = "Pixel Harf",
        appSubtitle = "Mangez chaque pixel de la lettre arabe",
        playNow = "Jouer",
        continueLevel = { lvl -> "Continuer · Niveau $lvl" },
        dailyChallenge = "Défi du Jour",
        dailyCompleted = "Terminé ✓",
        dailyStreak = { s -> "Série de $s j" },
        levelMap = "Carte des Lettres",
        settings = "Paramètres",
        modesTitle = "Mode de Jeu",
        modeKids = "Enfants",
        modeKidsDesc = "Sans défaite · vitesse douce · indices",
        modeClassic = "Classique",
        modeClassicDesc = "Progression équilibrée · 3 étoiles",
        modeHard = "Difficile",
        modeHardDesc = "Haute vitesse · 2 vies seulement",
        levelHeader = { glyph, fontName -> "Lettre «$glyph» — $fontName" },
        bossBadge = "BOSS",
        mirroredBadge = "Inversé",
        tapToStartHint = { best -> "Touchez pour jouer · Flèches ← → · Record $best" },
        scoreLabel = "SCORE",
        pixelsLeft = { left, total -> "$left / $total" },
        comboBadge = { c -> "×$c" },
        pausedTitle = "Pause",
        resume = "Reprendre",
        restart = "Recommencer",
        backToMap = "Carte",
        backHome = "Accueil",
        levelCompleteTitle = "Lettre Terminée !",
        nextLevel = "Niveau Suivant",
        replay = "Rejouer",
        gameOverTitle = "Partie Terminée",
        deathReasonLabel = { reason ->
          when (reason) {
            DeathReason.WALL -> "Collision bordure"
            DeathReason.TAIL -> "Collision queue"
            DeathReason.TIME_UP -> "Temps écoulé"
            null -> "Réessayez"
          }
        },
        worldTitle = { w ->
          when (w) {
            1 -> "Monde 1 · Formes Simples"
            2 -> "Monde 2 · Lettres Moyennes"
            3 -> "Monde 3 · Lettres Complexes"
            else -> "Monde 4 · Maître"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "Bords traversants · queue sûre"
            2 -> "Bords solides · points bonus"
            3 -> "Collision queue active · grille fine"
            else -> "Polices variées · formes liées · pixel fantôme"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "Ralenti"
            PowerUpType.MAGNET -> "Aimant"
            PowerUpType.GHOST -> "Fantôme 5s"
            PowerUpType.SHRINK -> "Rétrécir"
          }
        },
        tapTurnLeft = "↶ Gauche",
        tapTurnRight = "Droite ↷",
        settingsLanguage = "Langue",
        settingsControls = "Contrôles",
        controlSwipe = "Glisser (Swipe)",
        controlTapZones = "Zones Gauche / Droite",
        settingsMode = "Mode de Difficulté",
        settingsSound = "Effets Sonores 8-Bit",
        settingsHaptics = "Vibrations",
        settingsTts = "Prononcer la lettre (Arabe)",
        settingsColorblind = "Palette Daltonien",
        settingsLargeText = "Grand Texte",
        levelNumLabel = { n -> "N$n" },
        starsTotal = { earned, total -> "$earned / $total ★" },
      )
  }
