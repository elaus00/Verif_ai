package mp.verif_ai.presentation.screens.auth.signup

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.auth.OnBoardingButton
import mp.verif_ai.presentation.screens.auth.WelcomeText
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import mp.verif_ai.presentation.viewmodel.AuthEvent
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val events by authViewModel.events.collectAsState(initial = null)
    val context = LocalContext.current as ComponentActivity
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    LaunchedEffect(events) {
        when (events) {
            is AuthEvent.NavigateToMain -> onNavigateToMain()
            is AuthEvent.ShowError -> {
                snackbarHostState.showSnackbar(
                    message = (events as AuthEvent.ShowError).message,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(snackbarData = data)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .shadow(6.dp, spotColor = VerifAiColor.TextPrimary.copy(alpha = 0.12f))
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    WelcomeText(
                        mainText = "Create Account",
                        subText = "Sign up securely with modern authentication"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nickname Input
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OnBoardingButton(
                        text = "Continue with Credential Manager",
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty() && nickname.isNotEmpty()) {
                                authViewModel.signUpWithCredentialManager(email, password, nickname, context)
                            }
                        },
                        enabled = uiState !is AuthUiState.Loading &&
                                email.isNotEmpty() &&
                                password.isNotEmpty() &&
                                nickname.isNotEmpty(),
                        isLoading = uiState is AuthUiState.Loading
                    )

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).exception.message
                                ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}