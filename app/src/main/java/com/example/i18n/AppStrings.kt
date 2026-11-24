package com.example.i18n

import com.example.model.AppLanguage
import com.example.model.DeathReason
import com.example.model.PowerUpType

data class AppStrings(
  val appTitle: String,
  val appSubtitle: String,
  val playWord: String,
  val levelBadge: (Int) -> String,
  val modePillLabel: (String) -> String,
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
  val letterTitle: (glyph: String, name: String) -> String,
  val tapToStartHint: (bestScore: Int) -> String,
  val scoreLabel: String,
  val bestLabel: String,
  val comboBadge: (Int) -> String,
  val pausedTitle: String,
  val pauseAction: String,
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
  val settingsPixelStyle: String,
  val styleDots: String,
  val styleSquares: String,
  val settingsDarkVariant: String,
  val settingsGlyphPulse: String,
  val progressLabel: String,
  val soundLabel: String,
  val levelNumLabel: (Int) -> String,
  val starsTotal: (Int, Int) -> String,
  val coachmarkPauseTitle: String,
  val coachmarkPowerUpDesc: String,
  val coachmarkDismiss: String,
  val livesContentDesc: String,
)

fun stringsFor(language: AppLanguage): AppStrings =
  when (language) {
    AppLanguage.AR ->
      AppStrings(
        appTitle = "حرف بيكسل",
        appSubtitle = "الْتَهِم نقاط الحرف العربي وأَحيِ الخط",
        playWord = "العب",
        levelBadge = { lvl -> "المستوى $lvl" },
        modePillLabel = { mode -> "الوضع: $mode" },
        dailyChallenge = "تحدي اليوم",
        dailyCompleted = "مكتمل ✓",
        dailyStreak = { s -> "$s أيام" },
        levelMap = "الخريطة",
        settings = "الإعدادات",
        modesTitle = "الوضع",
        modeKids = "أطفال",
        modeKidsDesc = "بدون خسارة · سرعة هادئة · تلميح النقطة التالية",
        modeClassic = "كلاسيكي",
        modeClassicDesc = "تحدٍ متوازن مع نجوم التقييم",
        modeHard = "صعب",
        modeHardDesc = "سرعة عالية · حياتان فقط",
        letterTitle = { glyph, name -> "حرف «$glyph» · $name" },
        tapToStartHint = { best -> "اضغط لتبدأ · الأسهم ← → · أفضل نتيجة $best" },
        scoreLabel = "النقاط",
        bestLabel = "الأفضل",
        comboBadge = { c -> "×$c" },
        pausedTitle = "متوقف",
        pauseAction = "إيقاف",
        resume = "استئناف",
        restart = "إعادة",
        backToMap = "الخريطة",
        backHome = "الرئيسية",
        levelCompleteTitle = "أحسنت!",
        nextLevel = "التالي",
        replay = "إعادة",
        gameOverTitle = "انتهت المحاولات",
        deathReasonLabel = { reason ->
          when (reason) {
            DeathReason.WALL -> "اصطدام بالجدار"
            DeathReason.TAIL -> "اصطدام بالذيل"
            DeathReason.TIME_UP -> "انتهى الوقت"
            null -> "حاول مجدداً"
          }
        },
        worldTitle = { w -> "العالم $w" },
        worldDesc = { w ->
          when (w) {
            1 -> "الحروف البسيطة · جدران ملتفة"
            2 -> "الحروف المتوسطة · النقاط تمنح مكافأة"
            3 -> "الحروف المركبة · اصطدام الذيل مفعل"
            else -> "الإتقان والأشكال · خطوط متنوعة وشبح متحرك"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "إبطاء"
            PowerUpType.MAGNET -> "مغناطيس"
            PowerUpType.GHOST -> "شبح"
            PowerUpType.SHRINK -> "تقليص"
          }
        },
        tapTurnLeft = "يسار",
        tapTurnRight = "يمين",
        settingsLanguage = "اللغة",
        settingsControls = "التحكم",
        controlSwipe = "سحب باللمس",
        controlTapZones = "زران يمين / يسار",
        settingsMode = "مستوى اللعب",
        settingsSound = "مؤثرات 8-بت الصوتية",
        settingsHaptics = "الاهتزاز",
        settingsTts = "نطق الحرف بعد الفوز",
        settingsColorblind = "ألوان وأشكال لعمى الألوان",
        settingsLargeText = "تكبير النص المقروء",
        settingsPixelStyle = "شكل النقاط في اللعب",
        styleDots = "نقاط دائرية",
        styleSquares = "مربعات",
        settingsDarkVariant = "المظهر الداكن",
        settingsGlyphPulse = "نبض الحروف في الرئيسية",
        progressLabel = "الإنجاز",
        soundLabel = "الصوت",
        levelNumLabel = { n -> "$n" },
        starsTotal = { earned, total -> "$earned/$total" },
        coachmarkPauseTitle = "زر الإيقاف المؤقت هنا للراحة أو الإعادة",
        coachmarkPowerUpDesc = "تظهر قدرات التعزيز هنا أثناء اللعب عند التقاطها",
        coachmarkDismiss = "فهمت",
        livesContentDesc = "المحاولات المتبقية",
      )
    AppLanguage.EN ->
      AppStrings(
        appTitle = "Pixel Harf",
        appSubtitle = "Eat every dot of the Arabic letter",
        playWord = "Play",
        levelBadge = { lvl -> "Level $lvl" },
        modePillLabel = { mode -> "Mode: $mode" },
        dailyChallenge = "Daily",
        dailyCompleted = "Done ✓",
        dailyStreak = { s -> "${s}d" },
        levelMap = "Map",
        settings = "Settings",
        modesTitle = "Modes",
        modeKids = "Kids",
        modeKidsDesc = "No death · gentle pace · next-dot hints",
        modeClassic = "Classic",
        modeClassicDesc = "Balanced progression & 3-star mastery",
        modeHard = "Hard",
        modeHardDesc = "High speed · 2 lives only",
        letterTitle = { glyph, name -> "Letter «$glyph» · $name" },
        tapToStartHint = { best -> "Tap to start · Arrows ← → · Best $best" },
        scoreLabel = "SCORE",
        bestLabel = "BEST",
        comboBadge = { c -> "x$c" },
        pausedTitle = "Paused",
        pauseAction = "Pause",
        resume = "Resume",
        restart = "Restart",
        backToMap = "Map",
        backHome = "Home",
        levelCompleteTitle = "Cleared!",
        nextLevel = "Next",
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
        worldTitle = { w -> "World $w" },
        worldDesc = { w ->
          when (w) {
            1 -> "Simple shapes · wrapping borders"
            2 -> "Medium letters · bonus dot targets"
            3 -> "Complex letters · tail collision active"
            else -> "Master forms · varied fonts & moving ghost dot"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "Slow"
            PowerUpType.MAGNET -> "Magnet"
            PowerUpType.GHOST -> "Ghost"
            PowerUpType.SHRINK -> "Shrink"
          }
        },
        tapTurnLeft = "Left",
        tapTurnRight = "Right",
        settingsLanguage = "Language",
        settingsControls = "Controls",
        controlSwipe = "Swipe",
        controlTapZones = "Left / Right Tap Zones",
        settingsMode = "Difficulty Mode",
        settingsSound = "8-Bit Sound Effects",
        settingsHaptics = "Haptics",
        settingsTts = "Speak Letter Name (Arabic TTS)",
        settingsColorblind = "Colorblind-Safe Shapes & Palette",
        settingsLargeText = "Large Readable Text",
        settingsPixelStyle = "In-Game Pixel Style",
        styleDots = "Dots",
        styleSquares = "Squares",
        settingsDarkVariant = "Dark Gadget Theme",
        settingsGlyphPulse = "Home Glyph Pulse",
        progressLabel = "Progress",
        soundLabel = "Sound",
        levelNumLabel = { n -> "$n" },
        starsTotal = { earned, total -> "$earned/$total" },
        coachmarkPauseTitle = "Tap Pause anytime to rest or restart",
        coachmarkPowerUpDesc = "Active power-ups and timers appear here",
        coachmarkDismiss = "Got it",
        livesContentDesc = "Remaining lives",
      )
    AppLanguage.FR ->
      AppStrings(
        appTitle = "Pixel Harf",
        appSubtitle = "Mangez chaque point de la lettre arabe",
        playWord = "Jouer",
        levelBadge = { lvl -> "Niveau $lvl" },
        modePillLabel = { mode -> "Mode : $mode" },
        dailyChallenge = "Défi",
        dailyCompleted = "Fait ✓",
        dailyStreak = { s -> "${s}j" },
        levelMap = "Carte",
        settings = "Réglages",
        modesTitle = "Modes",
        modeKids = "Enfants",
        modeKidsDesc = "Sans défaite · rythme doux · indices",
        modeClassic = "Classique",
        modeClassicDesc = "Progression équilibrée · 3 étoiles",
        modeHard = "Difficile",
        modeHardDesc = "Haute vitesse · 2 vies seulement",
        letterTitle = { glyph, name -> "Lettre «$glyph» · $name" },
        tapToStartHint = { best -> "Touchez pour jouer · Flèches ← → · Record $best" },
        scoreLabel = "SCORE",
        bestLabel = "RECORD",
        comboBadge = { c -> "x$c" },
        pausedTitle = "Pause",
        pauseAction = "Pause",
        resume = "Reprendre",
        restart = "Rejouer",
        backToMap = "Carte",
        backHome = "Accueil",
        levelCompleteTitle = "Bravo !",
        nextLevel = "Suivant",
        replay = "Rejouer",
        gameOverTitle = "Terminé",
        deathReasonLabel = { reason ->
          when (reason) {
            DeathReason.WALL -> "Collision bordure"
            DeathReason.TAIL -> "Collision queue"
            DeathReason.TIME_UP -> "Temps écoulé"
            null -> "Réessayez"
          }
        },
        worldTitle = { w -> "Monde $w" },
        worldDesc = { w ->
          when (w) {
            1 -> "Formes simples · bords traversants"
            2 -> "Lettres moyennes · points bonus"
            3 -> "Lettres complexes · collision queue active"
            else -> "Maître · polices variées & point fantôme"
          }
        },
        powerUpLabel = { p ->
          when (p) {
            PowerUpType.SLOW_MO -> "Ralenti"
            PowerUpType.MAGNET -> "Aimant"
            PowerUpType.GHOST -> "Fantôme"
            PowerUpType.SHRINK -> "Réduire"
          }
        },
        tapTurnLeft = "Gauche",
        tapTurnRight = "Droite",
        settingsLanguage = "Langue",
        settingsControls = "Contrôles",
        controlSwipe = "Glisser (Swipe)",
        controlTapZones = "Zones Gauche / Droite",
        settingsMode = "Mode de Jeu",
        settingsSound = "Effets Sonores 8-Bit",
        settingsHaptics = "Vibrations",
        settingsTts = "Prononcer la lettre (Arabe)",
        settingsColorblind = "Palette & Formes Daltonien",
        settingsLargeText = "Grand Texte Lisible",
        settingsPixelStyle = "Style des Pixels",
        styleDots = "Points",
        styleSquares = "Carrés",
        settingsDarkVariant = "Thème Sombre",
        settingsGlyphPulse = "Animation Glyphes Accueil",
        progressLabel = "Progrès",
        soundLabel = "Son",
        levelNumLabel = { n -> "$n" },
        starsTotal = { earned, total -> "$earned/$total" },
        coachmarkPauseTitle = "Touchez Pause pour faire une pause ou rejouer",
        coachmarkPowerUpDesc = "Les bonus actifs s'affichent ici",
        coachmarkDismiss = "Compris",
        livesContentDesc = "Vies restantes",
      )
  }
