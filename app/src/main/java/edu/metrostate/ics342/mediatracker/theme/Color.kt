package edu.metrostate.ics342.mediatracker.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF6366F1)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFE0E0FF)
val OnPrimaryContainer = Color(0xFF3730A3)

val Secondary = Color(0xFFDB2777)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFFCE7F3)
val OnSecondaryContainer = Color(0xFF9D174D)

val Tertiary = Color(0xFFD97706)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFEF3C7)
val OnTertiaryContainer = Color(0xFF78350F)

val WantTo = Color(0xFF7C3AED)
val WantToContainer = Color(0xFFEDE9FE)

val InProgress = Color(0xFF2563EB)
val InProgressContainer = Color(0xFFDBEAFE)

val Finished = Color(0xFF059669)
val FinishedContainer = Color(0xFFD1FAE5)

val Background = Color(0xFFFFFFFF)
val OnBackground = Color(0xFF111827)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF111827)
val SurfaceVariant = Color(0xFFF8FAFC)
val OnSurfaceVariant = Color(0xFF64748B)
val Outline = Color(0xFFE5E7EB)
val OutlineVariant = Color(0xFFF1F5F9)

val AvatarColors = listOf(Primary, Secondary, Tertiary, WantTo, InProgress, Finished)

fun avatarColor(key: String): Color =
    AvatarColors[(key.hashCode() and Int.MAX_VALUE) % AvatarColors.size]

val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF1A1A2E)
val DarkOnSurface = Color(0xFFE8E8F0)
val DarkPrimary = Color(0xFF818CF8)
val DarkOnPrimary = Color(0xFF1E1B4B)
val DarkPrimaryContainer = Color(0xFF2D2B6E)
val DarkOnPrimaryContainer = Color(0xFFE0E7FF)