package com.example.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized spacing, sizing, and typography tokens on an 8dp grid.
 */
object Dimens {
  // 8dp grid spacing
  val SpaceXs = 4.dp
  val SpaceSm = 8.dp
  val SpaceMd = 12.dp
  val SpaceLg = 16.dp
  val SpaceXl = 24.dp

  // Screen & layout dimensions
  val ScreenPadding = 16.dp
  val BoardHorizontalMargin = 12.dp
  val TopBarHeight = 56.dp
  val BottomBarHeight = 80.dp
  val MinTouchTarget = 48.dp

  // Component dimensions
  val ButtonHeight = 48.dp
  val PrimaryPlayHeight = 56.dp
  val CircleButtonSize = 56.dp
  val PowerUpCircleSize = 56.dp
  val SmallCircleButtonSize = 48.dp
  val DailyCircleSize = 156.dp

  // Corner radii
  val CardRadius = 28.dp
  val BoardRadius = 32.dp
  val DialogRadius = 28.dp

  // Dot-matrix sizing (seasoning scale)
  val DotTitleDotSize = 1.4.dp
  val DotWorldTitleDotSize = 1.25.dp

  // Typography scale
  val DotTitleMaxSp = 28.sp
  val WorldTitleSp = 22.sp
  val PauseTitleSp = 22.sp
  val PlayTitleSp = 20.sp
  val ButtonSp = 18.sp
  val BodySp = 14.sp
  val CaptionSp = 12.sp
}
