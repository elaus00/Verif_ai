package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.theme.InputField
import mp.verif_ai.presentation.screens.theme.OnBoardingButton
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.UiState

@Composable
fun SignUpFormScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onSignUpComplete: (String) -> Unit
) {
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
                onSignUpComplete(viewModel.email.toString())
            }
            is UiState.Error -> {
                errorMessage = (authState as UiState.Error).toString()
            }
            else -> { /* Loading 또는 Initial 상태 처리 */ }
        }
    }

    Column(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomeText(
                mainText = "Complete Sign Up",
                subText = "Please fill in your details"
            )

            Spacer(modifier = Modifier.height(48.dp))

            InputField(
                placeholder = "Password",
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage != null || (!passwordsMatch && confirmPassword.isNotEmpty())
            )

            Spacer(modifier = Modifier.height(15.dp))

            InputField(
                placeholder = "Confirm Password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                visualTransformation = PasswordVisualTransformation(),
                isError = !passwordsMatch && confirmPassword.isNotEmpty()
            )

            if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            InputField(
                placeholder = "Nickname",
                value = nickname,
                onValueChange = {
                    nickname = it
                    errorMessage = null
                },
                isError = errorMessage != null
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OnBoardingButton(
                text = if (authState is UiState.Loading) "Loading..." else "Continue",
                onClick = {
                    when {
                        password.length < 6 -> {
                            errorMessage = "Password must be at least 6 characters"
                        }
                        !passwordsMatch -> {
                            errorMessage = "Passwords do not match"
                        }
                        nickname.isBlank() -> {
                            errorMessage = "Please enter your nickname"
                        }
                        else -> {
                            errorMessage = null
                            viewModel.signUp(viewModel.email.toString(), password, nickname)
                        }
                    }
                },
                enabled = password.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        nickname.isNotBlank() &&
                        passwordsMatch &&
                        authState !is UiState.Loading
            )
        }
    }
}