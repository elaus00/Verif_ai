package mp.verif_ai.presentation.screens.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import mp.verif_ai.R

data object SignUpOptions {
    enum class Option {
        GOOGLE,
        PHONE,
        PASSWORD
    }

    data class SignUpOption(
        val option: Option,
        val title: String,
        val description: String,
        val icon: ImageVector
    )

    val options = listOf(
        SignUpOption(
            option = Option.GOOGLE,
            title = "Continue with Google",
            description = "Create account using your Google account",
            icon = Icons.Default.Phone // 임시로
        ),
        SignUpOption(
            option = Option.PHONE,
            title = "Use phone number",
            description = "Create account using your phone number",
            icon = Icons.Default.Phone
        ),
        SignUpOption(
            option = Option.PASSWORD,
            title = "Use password",
            description = "Create account using email and password",
            icon = Icons.Default.Lock
        )
    )
}