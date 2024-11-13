package mp.verif_ai.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import mp.verif_ai.presentation.screens.auth.login.SignInScreen
import mp.verif_ai.presentation.screens.auth.login.SignUpScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Verif AI") }
            )
        }
    ) { paddingValues ->
//        SignInScreen(
//            modifier = Modifier.padding(paddingValues),
//            onSignInSuccess = { /* Handle successful sign-in */ }
//        )
        SignUpScreen(
            modifier = Modifier.padding(paddingValues),
            onSignUpSuccess = { /*TODO*/ },
            onNavigateBack = { /*TODO*/ })
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Verif_aiTheme {
        MainScreen()
    }
}