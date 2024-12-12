package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import mp.verif_ai.R
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.expertsignup.GroupImage
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel

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
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Main Content
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 30.dp, vertical = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GroupImage(R.drawable.group)
                Spacer(modifier = Modifier.height(30.dp))
                WelcomeTitle()
                Spacer(modifier = Modifier.height(30.dp))
                WelcomeSubtitle()
            }

            // Bottom Buttons Section
            BottomSection(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                onLoginClick = { navController.navigate(Screen.Auth.SignIn.route) },
                onSignUpClick = { navController.navigate(Screen.Auth.SignUp.route) }
            )
        }
    }
}

@Composable
private fun WelcomeTitle() {
    Text(
        text = "Welcome !",
        style = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 56.sp
        )
    )
}

@Composable
private fun WelcomeSubtitle() {
    Text(
        text = "Verify your conversation\nwith ChatGPT easily!",
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        ),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun BottomSection(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.25.sp
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        TextButton(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create an account",
                style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 0.25.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}