package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PixelHarfShapes =
  Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

object AppShapes {
  val GlassCard = RoundedCornerShape(28.dp)
  val PrimaryPill = CircleShape
  val SecondaryPill = RoundedCornerShape(24.dp)
  val UnitChip = CircleShape
  val Card = RoundedCornerShape(28.dp)
  val Button = RoundedCornerShape(24.dp)
  val Board = RoundedCornerShape(8.dp)
  val Dialog = RoundedCornerShape(28.dp)
  val Circle = CircleShape
}
