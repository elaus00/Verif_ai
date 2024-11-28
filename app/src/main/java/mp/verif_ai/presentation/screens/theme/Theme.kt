package mp.verif_ai.presentation.screens.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
    // 다크 테마 색상 정의
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
        content = content
    )
}