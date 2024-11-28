package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import mp.verif_ai.R
import mp.verif_ai.domain.model.passkey.PassKeyStatus
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.expertsignup.GroupImage
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.PassKeyUiState
import mp.verif_ai.presentation.viewmodel.PassKeyViewModel

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Authenticated -> {
                (navController as NavHostController).navigateToMain()
            }
            is AuthUiState.Error -> {
                val error = (authUiState as AuthUiState.Error).exception.message ?: "An error occurred"
                snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short,
                )
            }
            else -> { }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    CustomSnackbar(snackbarData = snackbarData)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        ) {
            // Logo Section - 가운데 정렬
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GroupImage(R.drawable.group)
                Spacer(modifier = Modifier.height(48.dp))
                WelcomeText(
                    mainText = "Verif AI",
                    subText = "Verify your AI-conversation\nby professionals!"
                )
            }

            // Button Section - 하단 고정
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(Screen.Auth.SignUp.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    contentPadding = PaddingValues(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Text(
                    text = "By continuing, you agree to our \n" +
                            " Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}