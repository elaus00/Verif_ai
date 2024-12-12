package mp.verif_ai.presentation.screens.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = VerifAiColor.Navy.Deep,
    onPrimary = VerifAiColor.Surface,
    primaryContainer = VerifAiColor.Navy.BaseAlpha,
    onPrimaryContainer = VerifAiColor.Navy.Dark,

    secondary = VerifAiColor.Secondary,
    onSecondary = VerifAiColor.Surface,
    secondaryContainer = VerifAiColor.Navy.DeepAlpha,
    onSecondaryContainer = VerifAiColor.Navy.Dark,

    background = VerifAiColor.Background,
    onBackground = VerifAiColor.TextPrimary,

    surface = VerifAiColor.Surface,
    onSurface = VerifAiColor.TextPrimary,
    surfaceVariant = VerifAiColor.SurfaceVariant,
    onSurfaceVariant = VerifAiColor.TextSecondary,

    onError = VerifAiColor.Surface
)

// 다크 테마가 필요한 경우 추가
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = VerifAiColor.Navy.Light,  // 더 밝은 네이비 사용
    onPrimary = Color(0xFF1C1C1C),     // 어두운 배경
    primaryContainer = VerifAiColor.Navy.Medium,
    onPrimaryContainer = VerifAiColor.Navy.Light,

    // Secondary colors
    secondary = VerifAiColor.Secondary,
    onSecondary = Color(0xFF1C1C1C),
    secondaryContainer = VerifAiColor.Navy.Medium.copy(alpha = 0.3f),
    onSecondaryContainer = VerifAiColor.Navy.Light,

    // Background colors
    background = Color(0xFF1C1C1C),
    onBackground = Color(0xFFE1E1E1),

    // Surface colors
    surface = Color(0xFF242424),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),

    // Error colors
    onError = Color(0xFF1C1C1C)
)


@Composable
fun VerifAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 상태바 색상 설정
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            Surface(
                color = Color.White  // 앱 전체 배경색 설정
            ) {
                content()
            }
        }
    )
}