package com.example.engine

import com.example.data.LevelCatalog
import com.example.model.DeathReason
import com.example.model.Direction
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.GameStatus
import com.example.model.GhostPixel
import com.example.model.GridPoint
import com.example.model.LevelSpec
import com.example.model.Particle
import com.example.model.PowerUpItem
import com.example.model.PowerUpType
import com.example.raster.LetterRasterizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

interface EngineFeedback {
  fun onEat(combo: Int, isDot: Boolean)
  fun onPowerUp()
  fun onHurt()
  fun onLevelComplete(letterNameAr: String)
  fun onGameOver()
}

class GameEngine(
  private val scope: CoroutineScope,
  private val rasterizer: LetterRasterizer,
  private val feedback: EngineFeedback,
  private val onPersistResult: (levelSpec: LevelSpec, isDaily: Boolean, stars: Int, score: Int, completed: Boolean) -> Unit,
) {

  private val _state =
    MutableStateFlow(
      GameState(
        levelSpec = LevelCatalog.levelByNumber(1),
        mode = GameMode.CLASSIC,
      )
    )
  val state: StateFlow<GameState> = _state.asStateFlow()

  private var loopJob: Job? = null
  private var revealJob: Job? = null
  private val directionQueue = ArrayDeque<Direction>(3)
  private var stepAccumulatorMs = 0L
  private var powerUpSpawnTimerMs = 6000L
  private var ghostSpawnTimerMs = 5000L
  private var ghostHopAccumulatorMs = 0L

  fun loadLevel(
    levelSpec: LevelSpec,
    mode: GameMode,
    bestScore: Int,
    isDailyChallenge: Boolean = false,
    mirroredControls: Boolean = false,
  ) {
    stopLoops()
    directionQueue.clear()
    stepAccumulatorMs = 0L
    powerUpSpawnTimerMs = 6500L
    ghostSpawnTimerMs = 4500L
    ghostHopAccumulatorMs = 0L

    val raster =
      rasterizer.rasterize(
        glyph = levelSpec.letter.glyph,
        fontSpec = levelSpec.fontSpec,
        cols = levelSpec.gridCols,
        rows = levelSpec.gridRows,
      )

    val remaining = raster.letterPixels.toMutableList()
    val maxLives =
      when (mode) {
        GameMode.KIDS -> 9
        GameMode.CLASSIC -> 3
        GameMode.HARD -> 2
      }
    val startSnake = buildSafeSpawnSnake(raster.cols, raster.rows, remaining)
    val hint = if (mode == GameMode.KIDS) findNearestPixel(startSnake.first(), remaining) else null
    val timeLimitMs =
      levelSpec.timeLimitSec?.let { base ->
        when (mode) {
          GameMode.KIDS -> null
          GameMode.CLASSIC -> base * 1000L
          GameMode.HARD -> (base * 0.8f).toLong() * 1000L
        }
      }

    _state.value =
      GameState(
        levelSpec = levelSpec,
        mode = mode,
        isDailyChallenge = isDailyChallenge,
        mirroredControls = mirroredControls,
        gridCols = raster.cols,
        gridRows = raster.rows,
        baselineRow = raster.baselineRow,
        totalLetterPixels = remaining.size,
        remainingPixels = remaining,
        dotPixels = raster.dotPixels,
        baselinePixels = raster.baselinePixels,
        snake = startSnake,
        direction = Direction.RIGHT,
        lives = maxLives,
        maxLives = maxLives,
        score = 0,
        bestScore = bestScore,
        combo = 0,
        maxCombo = 0,
        comboTimerMs = 0L,
        activePowerUp = null,
        activeEffect = null,
        effectRemainingMs = 0L,
        ghostPixel = null,
        hintPixel = hint,
        particles = emptyList(),
        elapsedMs = 0L,
        timeRemainingMs = timeLimitMs,
        screenShakeMs = 0L,
        status = GameStatus.READY,
        starsEarned = 0,
        deathReason = null,
        calligraphicRevealActive = false,
      )
  }

  fun startOrResume() {
    val cur = _state.value
    if (cur.status == GameStatus.READY || cur.status == GameStatus.PAUSED) {
      _state.value = cur.copy(status = GameStatus.PLAYING)
      startLoop()
    }
  }

  fun pause() {
    val cur = _state.value
    if (cur.status == GameStatus.PLAYING) {
      loopJob?.cancel()
      _state.value = cur.copy(status = GameStatus.PAUSED)
    }
  }

  fun restart() {
    val cur = _state.value
    loadLevel(
      levelSpec = cur.levelSpec,
      mode = cur.mode,
      bestScore = maxOf(cur.bestScore, cur.score),
      isDailyChallenge = cur.isDailyChallenge,
      mirroredControls = cur.mirroredControls,
    )
    startOrResume()
  }

  fun queueDirection(requested: Direction) {
    val cur = _state.value
    if (cur.status == GameStatus.READY) {
      startOrResume()
    }
    if (_state.value.status != GameStatus.PLAYING) return

    val mapped = if (cur.mirroredControls) requested.opposite() else requested
    val lastDir = directionQueue.lastOrNull() ?: cur.direction
    if (mapped == lastDir || mapped == lastDir.opposite()) return
    if (directionQueue.size < 2) {
      directionQueue.addLast(mapped)
    }
  }

  fun turnRelative(turnLeft: Boolean) {
    val cur = _state.value
    if (cur.status == GameStatus.READY) {
      startOrResume()
      return
    }
    if (cur.status != GameStatus.PLAYING) return

    val effectiveLeft = if (cur.mirroredControls) !turnLeft else turnLeft
    val baseDir = directionQueue.lastOrNull() ?: cur.direction
    val nextDir = if (effectiveLeft) baseDir.turnLeft() else baseDir.turnRight()
    if (directionQueue.size < 2) {
      directionQueue.addLast(nextDir)
    }
  }

  fun stopLoops() {
    loopJob?.cancel()
    loopJob = null
    revealJob?.cancel()
    revealJob = null
  }

  private fun startLoop() {
    loopJob?.cancel()
    loopJob =
      scope.launch {
        val frameMs = 16L
        var lastTime = System.nanoTime()
        while (isActive) {
          delay(frameMs)
          val now = System.nanoTime()
          val dtMs = ((now - lastTime) / 1_000_000L).coerceIn(1L, 64L)
          lastTime = now
          tickFrame(dtMs)
        }
      }
  }

  private fun tickFrame(dtMs: Long) {
    var s = _state.value
    if (s.status != GameStatus.PLAYING) {
      // Keep animating particles/shake even after completion or game over
      if (s.particles.isNotEmpty() || s.screenShakeMs > 0L) {
        val updatedParticles = stepParticles(s.particles, dtMs)
        val nextShake = (s.screenShakeMs - dtMs).coerceAtLeast(0L)
        _state.value = s.copy(particles = updatedParticles, screenShakeMs = nextShake)
      }
      return
    }

    // Update timers & particles every frame (60fps smooth)
    val nextParticles = stepParticles(s.particles, dtMs)
    val nextShake = (s.screenShakeMs - dtMs).coerceAtLeast(0L)
    val nextComboTimer = (s.comboTimerMs - dtMs).coerceAtLeast(0L)
    val nextCombo = if (nextComboTimer == 0L) 0 else s.combo
    val nextEffectRemaining = (s.effectRemainingMs - dtMs).coerceAtLeast(0L)
    val nextEffect = if (nextEffectRemaining == 0L) null else s.activeEffect
    val nextPowerUp =
      s.activePowerUp?.let { item ->
        val rem = item.remainingMs - dtMs
        if (rem <= 0L) null else item.copy(remainingMs = rem)
      }

    var nextGhost =
      s.ghostPixel?.let { gp ->
        val rem = gp.remainingMs - dtMs
        if (rem <= 0L) null else gp.copy(remainingMs = rem)
      }

    val nextTimeRem = s.timeRemainingMs?.let { (it - dtMs).coerceAtLeast(0L) }
    if (nextTimeRem == 0L) {
      triggerGameOver(
        s.copy(
          particles = nextParticles,
          screenShakeMs = 260L,
          timeRemainingMs = 0L,
          deathReason = DeathReason.TIME_UP,
        )
      )
      return
    }

    s =
      s.copy(
        elapsedMs = s.elapsedMs + dtMs,
        timeRemainingMs = nextTimeRem,
        particles = nextParticles,
        screenShakeMs = nextShake,
        combo = nextCombo,
        comboTimerMs = nextComboTimer,
        activeEffect = nextEffect,
        effectRemainingMs = nextEffectRemaining,
        activePowerUp = nextPowerUp,
        ghostPixel = nextGhost,
      )

    // Power-up spawning
    if (s.activePowerUp == null) {
      powerUpSpawnTimerMs -= dtMs
      if (powerUpSpawnTimerMs <= 0L) {
        powerUpSpawnTimerMs = Random.nextLong(9500L, 14500L)
        spawnPowerUp(s)?.let { spawned -> s = s.copy(activePowerUp = spawned) }
      }
    }

    // World 4 moving ghost pixels
    if (s.levelSpec.hasGhostPixels && s.ghostPixel == null) {
      ghostSpawnTimerMs -= dtMs
      if (ghostSpawnTimerMs <= 0L) {
        ghostSpawnTimerMs = Random.nextLong(6500L, 10500L)
        spawnGhostPixel(s)?.let { gp -> s = s.copy(ghostPixel = gp) }
      }
    }

    // Fixed-timestep snake movement
    val effectiveStepMs = computeStepIntervalMs(s)
    stepAccumulatorMs += dtMs
    if (stepAccumulatorMs >= effectiveStepMs) {
      stepAccumulatorMs -= effectiveStepMs
      s = stepSnake(s)
    } else if (s.ghostPixel != null) {
      ghostHopAccumulatorMs += dtMs
      if (ghostHopAccumulatorMs >= 780L) {
        ghostHopAccumulatorMs = 0L
        s = s.copy(ghostPixel = moveGhostPixel(s, s.ghostPixel!!))
      }
    }

    _state.value = s
  }

  private fun computeStepIntervalMs(s: GameState): Long {
    val base = s.levelSpec.tickMs
    val modeFactor =
      when (s.mode) {
        GameMode.KIDS -> 1.42f
        GameMode.CLASSIC -> 1.0f
        GameMode.HARD -> 0.80f
      }
    val slowMoFactor = if (s.activeEffect == PowerUpType.SLOW_MO) 1.65f else 1.0f
    return (base * modeFactor * slowMoFactor).toLong().coerceIn(68L, 280L)
  }

  private fun stepSnake(current: GameState): GameState {
    var s = current
    val nextDir = if (directionQueue.isNotEmpty()) directionQueue.removeFirst() else s.direction
    val head = s.snake.first()
    var nx = head.x + nextDir.dx
    var ny = head.y + nextDir.dy

    val wallsWrap = s.levelSpec.wallsWrap || s.mode == GameMode.KIDS
    if (nx < 0 || nx >= s.gridCols || ny < 0 || ny >= s.gridRows) {
      if (wallsWrap) {
        nx = (nx + s.gridCols) % s.gridCols
        ny = (ny + s.gridRows) % s.gridRows
      } else {
        return handleCollision(s, DeathReason.WALL)
      }
    }

    val nextHead = GridPoint(nx, ny)
    val tailCheckEnabled =
      s.levelSpec.tailCollision &&
        s.mode != GameMode.KIDS &&
        s.activeEffect != PowerUpType.GHOST

    val willEatLetter = s.remainingPixels.contains(nextHead)
    val bodyToCheck = if (willEatLetter) s.snake else s.snake.dropLast(1)
    if (tailCheckEnabled && bodyToCheck.contains(nextHead)) {
      return handleCollision(s, DeathReason.TAIL)
    }

    val newSnake = mutableListOf(nextHead)
    newSnake.addAll(s.snake)
    if (!willEatLetter) {
      newSnake.removeAt(newSnake.lastIndex)
    }

    var remaining = s.remainingPixels
    var score = s.score
    var combo = s.combo
    var maxCombo = s.maxCombo
    var comboTimer = s.comboTimerMs
    var particles = s.particles
    var activePowerUp = s.activePowerUp
    var activeEffect = s.activeEffect
    var effectRemainingMs = s.effectRemainingMs
    var ghostPixel = s.ghostPixel
    var finalSnake: List<GridPoint> = newSnake

    // Eat letter pixel
    if (willEatLetter) {
      val mutableRemaining = remaining.toMutableList()
      mutableRemaining.remove(nextHead)
      val isDot = s.dotPixels.contains(nextHead)
      combo = if (comboTimer > 0L) combo + 1 else 1
      maxCombo = maxOf(maxCombo, combo)
      comboTimer = 2400L
      val multiplier = (1 + (combo - 1) / 3).coerceAtMost(5)
      val basePoints = if (isDot) 30 else 10
      score += basePoints * multiplier

      particles = particles + spawnBurst(nextHead, count = if (isDot) 10 else 6, colorKind = if (isDot) 2 else 0)
      feedback.onEat(combo, isDot)

      // Magnet pulls 1 adjacent/nearby pixel automatically when active
      if (activeEffect == PowerUpType.MAGNET && mutableRemaining.isNotEmpty()) {
        val pulled =
          mutableRemaining.firstOrNull { abs(it.x - nextHead.x) + abs(it.y - nextHead.y) <= 2 }
        if (pulled != null) {
          mutableRemaining.remove(pulled)
          score += 12 * multiplier
          particles = particles + spawnBurst(pulled, count = 5, colorKind = 1)
        }
      }

      remaining = mutableRemaining
    } else if (activeEffect == PowerUpType.MAGNET && remaining.isNotEmpty()) {
      // Magnet also catches pixel immediately adjacent on step
      val near = remaining.firstOrNull { abs(it.x - nextHead.x) + abs(it.y - nextHead.y) <= 1 }
      if (near != null) {
        val mutableRemaining = remaining.toMutableList()
        mutableRemaining.remove(near)
        remaining = mutableRemaining
        combo = if (comboTimer > 0L) combo + 1 else 1
        maxCombo = maxOf(maxCombo, combo)
        comboTimer = 2400L
        score += 12 * (1 + (combo - 1) / 3).coerceAtMost(5)
        particles = particles + spawnBurst(near, count = 5, colorKind = 1)
        feedback.onEat(combo, false)
      }
    }

    // Eat power-up
    if (activePowerUp != null && activePowerUp.point == nextHead) {
      val picked = activePowerUp.type
      activePowerUp = null
      feedback.onPowerUp()
      particles = particles + spawnBurst(nextHead, count = 10, colorKind = 1)
      score += 25
      if (picked == PowerUpType.SHRINK) {
        val targetLen = (finalSnake.size / 2).coerceAtLeast(2)
        finalSnake = finalSnake.take(targetLen)
      } else {
        activeEffect = picked
        effectRemainingMs = 5500L
      }
    }

    // Eat moving ghost pixel (World 4 bonus)
    if (ghostPixel != null && ghostPixel.point == nextHead) {
      score += 60
      particles = particles + spawnBurst(nextHead, count = 12, colorKind = 2)
      ghostPixel = null
      feedback.onEat(combo, true)
    } else if (ghostPixel != null) {
      ghostHopAccumulatorMs += computeStepIntervalMs(s)
      if (ghostHopAccumulatorMs >= 720L) {
        ghostHopAccumulatorMs = 0L
        ghostPixel = moveGhostPixel(s, ghostPixel)
      }
    }

    val bestScore = maxOf(s.bestScore, score)
    val hint = if (s.mode == GameMode.KIDS) findNearestPixel(nextHead, remaining) else null

    val updated =
      s.copy(
        snake = finalSnake,
        direction = nextDir,
        remainingPixels = remaining,
        score = score,
        bestScore = bestScore,
        combo = combo,
        maxCombo = maxCombo,
        comboTimerMs = comboTimer,
        activePowerUp = activePowerUp,
        activeEffect = activeEffect,
        effectRemainingMs = effectRemainingMs,
        ghostPixel = ghostPixel,
        hintPixel = hint,
        particles = particles,
      )

    if (remaining.isEmpty()) {
      return completeLevel(updated)
    }
    return updated
  }

  private fun handleCollision(s: GameState, reason: DeathReason): GameState {
    if (s.mode == GameMode.KIDS) {
      // Kids mode never dies; gently bounce/wrap to safe center
      val safeSnake = buildSafeSpawnSnake(s.gridCols, s.gridRows, s.remainingPixels)
      return s.copy(snake = safeSnake, direction = Direction.RIGHT, screenShakeMs = 120L)
    }

    val remainingLives = s.lives - 1
    feedback.onHurt()
    if (remainingLives <= 0) {
      return triggerGameOver(
        s.copy(
          lives = 0,
          screenShakeMs = 280L,
          deathReason = reason,
        )
      )
    }

    directionQueue.clear()
    val respawnSnake = buildSafeSpawnSnake(s.gridCols, s.gridRows, s.remainingPixels)
    return s.copy(
      lives = remainingLives,
      snake = respawnSnake,
      direction = Direction.RIGHT,
      combo = 0,
      comboTimerMs = 0L,
      screenShakeMs = 240L,
      particles = s.particles + spawnBurst(s.snake.first(), count = 8, colorKind = 1),
    )
  }

  private fun completeLevel(s: GameState): GameState {
    val stars = calculateStars(s)
    val confetti = spawnConfetti(s.gridCols, s.gridRows)
    val finalScore = s.score + (s.lives * 40) + (stars * 50)
    val finalBest = maxOf(s.bestScore, finalScore)

    feedback.onLevelComplete(s.levelSpec.letter.nameAr)
    onPersistResult(s.levelSpec, s.isDailyChallenge, stars, finalScore, true)

    val completedState =
      s.copy(
        score = finalScore,
        bestScore = finalBest,
        starsEarned = stars,
        particles = s.particles + confetti,
        status = GameStatus.LEVEL_COMPLETE,
        calligraphicRevealActive = true,
      )

    revealJob?.cancel()
    revealJob =
      scope.launch {
        delay(1100L)
        if (_state.value.status == GameStatus.LEVEL_COMPLETE) {
          _state.value = _state.value.copy(calligraphicRevealActive = false)
        }
      }

    return completedState
  }

  private fun triggerGameOver(s: GameState): GameState {
    feedback.onGameOver()
    onPersistResult(s.levelSpec, s.isDailyChallenge, 0, s.score, false)
    return s.copy(
      bestScore = maxOf(s.bestScore, s.score),
      status = GameStatus.GAME_OVER,
    )
  }

  private fun calculateStars(s: GameState): Int {
    if (s.mode == GameMode.KIDS) {
      return when {
        s.maxCombo >= 5 -> 3
        s.maxCombo >= 2 -> 2
        else -> 1
      }
    }
    val lifeRatio = s.lives.toFloat() / s.maxLives.toFloat()
    val avgSecPerPixel = (s.elapsedMs / 1000f) / s.totalLetterPixels.coerceAtLeast(1)
    return when {
      lifeRatio >= 0.99f && (s.maxCombo >= 4 || avgSecPerPixel < 1.35f) -> 3
      lifeRatio >= 0.5f || s.maxCombo >= 3 -> 2
      else -> 1
    }
  }

  private fun buildSafeSpawnSnake(
    cols: Int,
    rows: Int,
    occupied: List<GridPoint>,
  ): List<GridPoint> {
    val occupiedSet = occupied.toHashSet()
    val candidateRows = listOf(1, rows - 2, 0, rows - 1, rows / 2)
    for (y in candidateRows) {
      for (x in 2 until (cols - 2)) {
        val p0 = GridPoint(x, y)
        val p1 = GridPoint(x - 1, y)
        if (!occupiedSet.contains(p0) && !occupiedSet.contains(p1)) {
          return listOf(p0, p1)
        }
      }
    }
    return listOf(GridPoint(1, 0), GridPoint(0, 0))
  }

  private fun findNearestPixel(head: GridPoint, pixels: List<GridPoint>): GridPoint? {
    if (pixels.isEmpty()) return null
    return pixels.minByOrNull { abs(it.x - head.x) + abs(it.y - head.y) }
  }

  private fun spawnPowerUp(s: GameState): PowerUpItem? {
    val blocked = (s.snake + s.remainingPixels).toHashSet()
    val candidates = mutableListOf<GridPoint>()
    for (y in 1 until (s.gridRows - 1)) {
      for (x in 1 until (s.gridCols - 1)) {
        val p = GridPoint(x, y)
        if (!blocked.contains(p)) candidates.add(p)
      }
    }
    if (candidates.isEmpty()) return null
    val chosen = candidates[Random.nextInt(candidates.size)]
    val allowedTypes =
      if (s.mode == GameMode.KIDS || !s.levelSpec.tailCollision) {
        listOf(PowerUpType.SLOW_MO, PowerUpType.MAGNET, PowerUpType.SHRINK)
      } else {
        PowerUpType.entries
      }
    val type = allowedTypes[Random.nextInt(allowedTypes.size)]
    return PowerUpItem(point = chosen, type = type, remainingMs = 8000L)
  }

  private fun spawnGhostPixel(s: GameState): GhostPixel? {
    val blocked = (s.snake + s.remainingPixels).toHashSet()
    val candidates = mutableListOf<GridPoint>()
    for (y in 1 until (s.gridRows - 1)) {
      for (x in 1 until (s.gridCols - 1)) {
        val p = GridPoint(x, y)
        if (!blocked.contains(p)) candidates.add(p)
      }
    }
    if (candidates.isEmpty()) return null
    val chosen = candidates[Random.nextInt(candidates.size)]
    return GhostPixel(point = chosen, remainingMs = 6500L, totalMs = 6500L)
  }

  private fun moveGhostPixel(s: GameState, ghost: GhostPixel): GhostPixel {
    val dirs = Direction.entries.shuffled()
    val blocked = s.snake.toHashSet()
    for (d in dirs) {
      val nx = (ghost.point.x + d.dx).coerceIn(1, s.gridCols - 2)
      val ny = (ghost.point.y + d.dy).coerceIn(1, s.gridRows - 2)
      val cand = GridPoint(nx, ny)
      if (!blocked.contains(cand)) {
        return ghost.copy(point = cand)
      }
    }
    return ghost
  }

  private fun spawnBurst(point: GridPoint, count: Int, colorKind: Int): List<Particle> {
    val out = ArrayList<Particle>(count)
    for (i in 0 until count) {
      val angle = (i.toFloat() / count.toFloat()) * (2f * Math.PI.toFloat()) + Random.nextFloat() * 0.5f
      val speed = 1.8f + Random.nextFloat() * 3.4f
      out.add(
        Particle(
          x = point.x + 0.5f,
          y = point.y + 0.5f,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          life = 0.42f,
          maxLife = 0.42f,
          colorKind = colorKind,
          sizeFactor = 0.45f + Random.nextFloat() * 0.35f,
        )
      )
    }
    return out
  }

  private fun spawnConfetti(cols: Int, rows: Int): List<Particle> {
    val out = ArrayList<Particle>(44)
    val cx = cols * 0.5f
    val cy = rows * 0.45f
    for (i in 0 until 44) {
      val angle = Random.nextFloat() * (2f * Math.PI.toFloat())
      val speed = 2.5f + Random.nextFloat() * 7.5f
      out.add(
        Particle(
          x = cx,
          y = cy,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed - 2.0f,
          life = 1.25f,
          maxLife = 1.25f,
          colorKind = if (i % 3 == 0) 1 else if (i % 3 == 1) 2 else 3,
          sizeFactor = 0.5f + Random.nextFloat() * 0.5f,
        )
      )
    }
    return out
  }

  private fun stepParticles(particles: List<Particle>, dtMs: Long): List<Particle> {
    if (particles.isEmpty()) return emptyList()
    val dt = dtMs / 1000f
    val next = ArrayList<Particle>(particles.size)
    for (p in particles) {
      val nl = p.life - dt
      if (nl > 0f) {
        next.add(
          p.copy(
            x = p.x + p.vx * dt,
            y = p.y + p.vy * dt,
            vy = p.vy + 4.5f * dt,
            life = nl,
          )
        )
      }
    }
    return next
  }
}
