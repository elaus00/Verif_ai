package mp.verif_ai.presentation.screens.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

object VerifAiColor {
    // Existing colors
    val Primary = Color(0xFF2A5AB3)
    val Secondary = Color(0xFFF8F9FA)
    val TextPrimary = Color(0xFF171A1F)
    val TextSecondary = Color(0xFF9095A0)
    val TextTertiary = Color(0xFF7F7F7F)
    val DividerColor = Color(0x1A000000)
    val SearchBarBg = Color(0xFFBCC1CA).copy(alpha = 0.2f)

    // Status Background Colors
    val StatusDraftBg = Color(0xFF64748B)        // Slate gray for drafts
    val StatusPublishedBg = Color(0xFF22C55E)    // Green for active/published
    val StatusClosedBg = Color(0xFF6B7280)       // Gray for closed
    val StatusDeletedBg = Color(0xFFEF4444)      // Red for deleted
    val StatusControversialBg = Color(0xFFF59E0B) // Amber for controversial

    // Status Text Colors (lighter/darker variants for contrast)
    val StatusDraftText = Color(0xFFF8FAFC)      // Light slate
    val StatusPublishedText = Color(0xFFDCFCE7)  // Light green
    val StatusClosedText = Color(0xFFF9FAFB)     // Light gray
    val StatusDeletedText = Color(0xFFFEE2E2)    // Light red
    val StatusControversialText = Color(0xFFFEF3C7) // Light amber

    // Additional utility colors
    val RewardBg = Color(0xFFFEF3C7)            // Light amber for reward background
    val RewardText = Color(0xFFD97706)           // Darker amber for reward text
}