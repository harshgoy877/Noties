package com.noties.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Shapes following the latest design guidelines
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Custom shapes for specific components
object NotiesShapes {
    val NoteCard = RoundedCornerShape(8.dp)
    val SearchBar = RoundedCornerShape(24.dp)
    val FloatingActionButton = RoundedCornerShape(16.dp)
    val BottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val Dialog = RoundedCornerShape(16.dp)
}