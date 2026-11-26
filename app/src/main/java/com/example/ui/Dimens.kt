package com.example.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized spacing, sizing, radii, and typography tokens on an 8dp grid.
 */
object Dimens {
  // 8dp grid spacing
  val SpaceXs = 4.dp
  val SpaceSm = 8.dp
  val SpaceMd = 12.dp
  val SpaceLg = 16.dp
  val SpaceXl = 24.dp
  val SectionGap = 32.dp

  // Screen & layout dimensions
  val ScreenPadding = 16.dp
  val BoardHorizontalMargin = 12.dp
  val TopBarHeight = 56.dp
  val BottomBarHeight = 76.dp
  val MinTouchTarget = 48.dp
  val HairlineWidth = 1.dp

  // Component dimensions
  val ButtonHeight = 48.dp
  val PrimaryPlayHeight = 52.dp
  val CircleButtonSize = 48.dp
  val PowerUpCircleSize = 56.dp
  val MapCellCircleSize = 56.dp

  // Tighter flat radii (8dp everywhere except circles for snake/hearts/power-ups/map cells)
  val CardRadius = 8.dp
  val ButtonRadius = 8.dp
  val BoardRadius = 8.dp
  val DialogRadius = 8.dp

  // Modest dot-matrix sizing (max 1 DotText per screen)
  val DotWordmarkSize = 1.4.dp
  val DotWorldTitleSize = 1.2.dp

  // Capped type scale: display 28sp, title 20sp, body 14sp, caption 12sp
  val DisplaySp = 28.sp
  val TitleSp = 20.sp
  val BodySp = 14.sp
  val CaptionSp = 12.sp
}
