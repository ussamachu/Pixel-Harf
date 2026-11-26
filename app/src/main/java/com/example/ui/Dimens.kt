package com.example.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized spacing, sizing, radii, card/button tokens, and typography scale on an 8dp grid.
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
  val CardGap = 12.dp
  val CardPadding = 16.dp
  val BoardHorizontalMargin = 12.dp
  val TopBarHeight = 56.dp
  val BottomBarHeight = 76.dp
  val MinTouchTarget = 48.dp
  val HairlineWidth = 1.dp

  // Frosted card & widget dimensions
  val GlassCardRadius = 28.dp
  val UnitChipWidth = 28.dp
  val UnitChipHeight = 56.dp
  val StripHeight = 16.dp
  val BarStripHeight = 18.dp
  val HeroDotSize = 3.8.dp
  val ButtonDotSize = 1.6.dp
  val MiniDotSize = 1.5.dp

  // Button & control dimensions
  val PrimaryButtonHeight = 56.dp
  val SecondaryButtonHeight = 48.dp
  val SecondaryButtonRadius = 24.dp
  val ButtonHeight = 48.dp
  val PrimaryPlayHeight = 56.dp
  val CircleButtonSize = 48.dp
  val PowerUpCircleSize = 56.dp
  val MapCellCircleSize = 56.dp
  val SwitchTrackWidth = 52.dp
  val SwitchTrackHeight = 30.dp
  val SwitchThumbSize = 24.dp

  // Legacy board/dialog radii (board keeps crisp 8dp corner for pixel alignment)
  val CardRadius = 28.dp
  val ButtonRadius = 24.dp
  val BoardRadius = 8.dp
  val DialogRadius = 28.dp

  // Modest dot-matrix sizing
  val DotWordmarkSize = 1.4.dp
  val DotWorldTitleSize = 1.2.dp

  // Capped type scale: hero 40sp (dots), title 14-20sp, subtitle/captions 10-12sp, button 16sp
  val HeroCapSp = 40.sp
  val DisplaySp = 28.sp
  val TitleSp = 20.sp
  val ButtonSp = 16.sp
  val CardTitleSp = 14.sp
  val BodySp = 14.sp
  val CaptionSp = 12.sp
  val UnitSp = 10.sp
}
