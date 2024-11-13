package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.UiState

@Composable
fun SignUpScreen(
    modifier: Modifier,
    onSignUpSuccess: (Any?) -> Unit,
    onNavigateBack: () -> Unit,
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val passwordsMatch by remember(password, confirmPassword) {
        derivedStateOf { password == confirmPassword }
    }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                onSignUpSuccess(email)
            }
            is UiState.Error -> {
                errorMessage = (authState as UiState.Error).message
            }
            else -> { /* Loading 또는 Initial 상태 처리 필요 없음 */ }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorMessage != null
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorMessage != null || (!passwordsMatch && confirmPassword.isNotEmpty())
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = !passwordsMatch && confirmPassword.isNotEmpty(),
                    supportingText = {
                        if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                            Text(
                                text = "Passwords do not match",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Nickname") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorMessage != null
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        errorMessage = null
                        viewModel.signUp(email, password, nickname)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotBlank() &&
                            password.isNotBlank() &&
                            nickname.isNotBlank() &&
                            passwordsMatch &&
                            password == confirmPassword &&
                            authState !is UiState.Loading
                ) {
                    if (authState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Sign Up")
                    }
                }

                TextButton(
                    onClick = onNavigateBack
                ) {
                    Text("Already have an account? Sign In")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(
            modifier = Modifier,
            onSignUpSuccess = {},
            onNavigateBack = {},
            navController = rememberNavController()
        )
    }
}