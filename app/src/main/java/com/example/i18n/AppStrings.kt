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
  val starsLabel: String,
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
  val worldTag: (Int) -> String,
  val worldTitle: (Int) -> String,
  val worldDesc: (Int) -> String,
  val powerUpLabel: (PowerUpType) -> String,
  val tapTurnLeft: String,
  val tapTurnRight: String,
  val sectionControlsTag: String,
  val sectionAppearanceTag: String,
  val sectionLanguageTag: String,
  val sectionSoundTag: String,
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
  val styleSquares: String,
  val styleDots: String,
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
        appSubtitle = "التهم مربعات الحرف العربي وأتمم الشكل",
        playWord = "العب",
        levelBadge = { lvl -> "المستوى $lvl" },
        modePillLabel = { mode -> "الوضع: $mode" },
        dailyChallenge = "تحدي اليوم",
        dailyCompleted = "مكتمل",
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
        starsLabel = "النجوم",
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
        worldTag = { w -> "WORLD $w" },
        worldTitle = { w ->
          when (w) {
            1 -> "الحروف البسيطة"
            2 -> "الحروف المتوسطة"
            3 -> "الحروف المركبة"
            else -> "الإتقان والأشكال"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "جدران ملتفة · بدون اصطدام بالذيل"
            2 -> "جدران صلبة · النقاط تمنح مكافأة"
            3 -> "اصطدام الذيل مفعل · دقة أعلى"
            else -> "خطوط متنوعة · أشكال متصلة · شبح متحرك"
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
        sectionControlsTag = "CONTROLS",
        sectionAppearanceTag = "APPEARANCE",
        sectionLanguageTag = "LANGUAGE",
        sectionSoundTag = "SOUND",
        settingsLanguage = "اللغة",
        settingsControls = "التحكم واللعب",
        controlSwipe = "سحب باللمس",
        controlTapZones = "زران يمين / يسار",
        settingsMode = "مستوى الصعوبة",
        settingsSound = "مؤثرات 8-بت الصوتية",
        settingsHaptics = "الاهتزاز",
        settingsTts = "نطق الحرف بعد الفوز",
        settingsColorblind = "ألوان وأشكال لعمى الألوان",
        settingsLargeText = "تكبير النص المقروء",
        settingsPixelStyle = "نمط الرسم",
        styleSquares = "مربعات",
        styleDots = "نقاط",
        settingsDarkVariant = "المظهر الداكن",
        settingsGlyphPulse = "نبض الحروف في الرئيسية",
        progressLabel = "التقدم",
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
        appSubtitle = "Eat every pixel of the Arabic letter",
        playWord = "Play",
        levelBadge = { lvl -> "Level $lvl" },
        modePillLabel = { mode -> "Mode: $mode" },
        dailyChallenge = "Daily",
        dailyCompleted = "Done",
        dailyStreak = { s -> "${s}d" },
        levelMap = "Map",
        settings = "Settings",
        modesTitle = "Modes",
        modeKids = "Kids",
        modeKidsDesc = "No death · gentle pace · next-pixel hints",
        modeClassic = "Classic",
        modeClassicDesc = "Balanced progression & 3-star mastery",
        modeHard = "Hard",
        modeHardDesc = "High speed · 2 lives only",
        letterTitle = { glyph, name -> "Letter «$glyph» · $name" },
        tapToStartHint = { best -> "Tap to start · Arrows ← → · Best $best" },
        scoreLabel = "SCORE",
        bestLabel = "BEST",
        starsLabel = "STARS",
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
        worldTag = { w -> "WORLD $w" },
        worldTitle = { w ->
          when (w) {
            1 -> "Simple Shapes"
            2 -> "Medium Letters"
            3 -> "Complex Letters"
            else -> "Master Forms"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "Wrapping borders · tail safe"
            2 -> "Solid borders · bonus dot targets"
            3 -> "Tail collision active · denser grid"
            else -> "Varied fonts · connected forms · moving ghost"
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
        sectionControlsTag = "CONTROLS",
        sectionAppearanceTag = "APPEARANCE",
        sectionLanguageTag = "LANGUAGE",
        sectionSoundTag = "SOUND",
        settingsLanguage = "Language",
        settingsControls = "Controls & Difficulty",
        controlSwipe = "Swipe",
        controlTapZones = "Left / Right Tap Zones",
        settingsMode = "Difficulty Mode",
        settingsSound = "8-Bit Sound Effects",
        settingsHaptics = "Haptics",
        settingsTts = "Speak Letter Name (Arabic TTS)",
        settingsColorblind = "Colorblind-Safe Shapes & Palette",
        settingsLargeText = "Large Readable Text",
        settingsPixelStyle = "Pixel Style",
        styleSquares = "Squares",
        styleDots = "Dots",
        settingsDarkVariant = "Dark Theme",
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
        appSubtitle = "Mangez chaque pixel de la lettre arabe",
        playWord = "Jouer",
        levelBadge = { lvl -> "Niveau $lvl" },
        modePillLabel = { mode -> "Mode : $mode" },
        dailyChallenge = "Défi",
        dailyCompleted = "Fait",
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
        starsLabel = "ÉTOILES",
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
        worldTag = { w -> "WORLD $w" },
        worldTitle = { w ->
          when (w) {
            1 -> "Formes Simples"
            2 -> "Lettres Moyennes"
            3 -> "Lettres Complexes"
            else -> "Maître"
          }
        },
        worldDesc = { w ->
          when (w) {
            1 -> "Bords traversants · queue sûre"
            2 -> "Bords solides · points bonus"
            3 -> "Collision queue active · grille fine"
            else -> "Polices variées · formes liées · fantôme"
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
        sectionControlsTag = "CONTROLS",
        sectionAppearanceTag = "APPEARANCE",
        sectionLanguageTag = "LANGUAGE",
        sectionSoundTag = "SOUND",
        settingsLanguage = "Langue",
        settingsControls = "Contrôles et Mode",
        controlSwipe = "Glisser (Swipe)",
        controlTapZones = "Zones Gauche / Droite",
        settingsMode = "Mode de Jeu",
        settingsSound = "Effets Sonores 8-Bit",
        settingsHaptics = "Vibrations",
        settingsTts = "Prononcer la lettre (Arabe)",
        settingsColorblind = "Palette & Formes Daltonien",
        settingsLargeText = "Grand Texte Lisible",
        settingsPixelStyle = "Style des Pixels",
        styleSquares = "Carrés",
        styleDots = "Points",
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
