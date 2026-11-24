package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NothingShapes =
  Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(24.dp),
    medium = RoundedCornerShape(28.dp),
    large = RoundedCornerShape(34.dp),
    extraLarge = RoundedCornerShape(40.dp),
  )

object WidgetRadius {
  val SmallPill = RoundedCornerShape(24.dp)
  val Card = RoundedCornerShape(32.dp)
  val BoardCard = RoundedCornerShape(38.dp)
  val Pill = RoundedCornerShape(50)
  val Circle = CircleShape
}
