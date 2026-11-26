package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PixelHarfShapes =
  Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp),
  )

object AppShapes {
  val Card = RoundedCornerShape(8.dp)
  val Button = RoundedCornerShape(8.dp)
  val Board = RoundedCornerShape(8.dp)
  val Dialog = RoundedCornerShape(8.dp)
  val Circle = CircleShape
}
