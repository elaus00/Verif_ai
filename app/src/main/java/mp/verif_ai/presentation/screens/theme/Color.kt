package mp.verif_ai.presentation.screens.theme

import androidx.compose.ui.graphics.Color

data object VerifAiColor {
    // Primary Colors
    val Primary = Color(0xFF1E3A5F)       // Deep Navy
    val PrimaryVariant = Color(0xFF2C5282) // Bright Navy
    val Secondary = Color(0xFF4A90E2)     // Sky Blue

    // Background Colors
    val Background = Color(0xFFF8FAFC)    // Light Gray Blue
    val Surface = Color(0xFFFFFFFF)       // White
    val SurfaceVariant = Color(0xFFF1F5F9) // Light Blue Gray
    val CardBackground = Color(0xFFFFFFFF) // Card Background

    // Text Colors
    val TextPrimary = Color(0xFF1A202C)   // Dark Navy
    val TextSecondary = Color(0xFF4A5568) // Medium Gray
    val TextTertiary = Color(0xFF718096)  // Light Gray

    // Divider & Border
    val DividerColor = Color(0xFFE2E8F0)  // Light Gray
    val BorderColor = Color(0xFFCBD5E1)   // Medium Gray

    // Status Colors
    data object Status {
        // Draft - 작성 중
        val DraftBg = Color(0xFFF1F5F9)
        val DraftText = Color(0xFF475569)

        // Published - 게시됨
        val PublishedBg = Color(0xFFDBEAFE)
        val PublishedText = Color(0xFF1E40AF)

        // Closed - 종료됨
        val ClosedBg = Color(0xFFE2E8F0)
        val ClosedText = Color(0xFF475569)

        // Deleted - 삭제됨
        val DeletedBg = Color(0xFFFEE2E2)
        val DeletedText = Color(0xFFB91C1C)
    }

    data object Navy {
        // Main Navy Shades
        val Dark = Color(0xFF0F172A)      // Darkest Navy
        val Deep = Color(0xFF1E3A5F)      // Deep Navy
        val Base = Color(0xFF2C5282)      // Navy Base
        val Medium = Color(0xFF3B82F6)    // Medium Navy
        val Light = Color(0xFF60A5FA)     // Light Navy

        // Navy with Opacity
        val DarkAlpha = Dark.copy(alpha = 0.1f)
        val DeepAlpha = Deep.copy(alpha = 0.1f)
        val BaseAlpha = Base.copy(alpha = 0.1f)

        // Special Purpose Navy
        val SearchBarBg = Dark.copy(alpha = 0.04f)
        val CardBorder = Base.copy(alpha = 0.08f)
        val IconTint = Base
    }

    // Component Specific Colors
    data object Component {
        // Button Colors
        data object Button {
            val Primary = Navy.Deep
            val PrimaryText = Surface
            val Secondary = Navy.Light
            val SecondaryText = Navy.Deep
            val Disabled = TextTertiary.copy(alpha = 0.6f)
        }

        // Card Colors
        data object Card {
            val Background = Surface
            val Border = Navy.CardBorder
            val HeaderText = TextPrimary
            val ContentText = TextSecondary
            val MetaText = TextTertiary
        }

        // SearchBar Colors
        data object SearchBar {
            val Background = Navy.SearchBarBg
            val PlaceholderText = TextPrimary.copy(alpha = 0.64f)
            val IconTint = Navy.IconTint
        }

        // Icon Colors
        data object Icon {
            val Primary = Navy.IconTint
            val Secondary = TextTertiary
            val Disabled = TextTertiary.copy(alpha = 0.4f)
        }
    }

    // Interaction States
    data object Interaction {
        val Hover = Navy.BaseAlpha
        val Press = Navy.DeepAlpha
        val Focus = Navy.DarkAlpha
        val Disabled = TextTertiary.copy(alpha = 0.6f)
    }
}