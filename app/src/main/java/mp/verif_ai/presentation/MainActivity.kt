package mp.verif_ai.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import mp.verif_ai.presentation.navigation.AppNavigation
import mp.verif_ai.presentation.screens.theme.Verif_aiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Verif_aiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Verif_aiTheme {
        MainScreen()
    }
}