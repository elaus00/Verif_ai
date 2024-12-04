package mp.verif_ai.presentation.screens.auth

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.PassKeyEvent
import mp.verif_ai.presentation.viewmodel.PassKeyUiState
import mp.verif_ai.presentation.viewmodel.PassKeyViewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    passKeyViewModel: PassKeyViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authUiState by authViewModel.uiState.collectAsState()
    val passKeyUiState by passKeyViewModel.uiState.collectAsState()
    val context = LocalContext.current as ComponentActivity
    val snackbarHostState = remember { SnackbarHostState() }

    // Initial PassKey sign-in attempt
    LaunchedEffect(Unit) {
        Log.d("SignInScreen", "Attempting PassKey sign-in")
        passKeyViewModel.signInWithPassKey(context)
    }

    // Auth state handling
    LaunchedEffect(passKeyUiState) {
        when (passKeyUiState) {
            is PassKeyUiState.SignedIn -> {
                navController.navigateToMain()
            }
            is PassKeyUiState.Error -> {
                // PassKey 인증 실패시 에러 메시지 표시
                snackbarHostState.showSnackbar(
                    message = (passKeyUiState as PassKeyUiState.Error).error.message
                        ?: "Please sign in with email"
                )
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        passKeyViewModel.events.collect { event ->
            when (event) {
                is PassKeyEvent.NoCredentialAvailable -> {
                    // 아무 처리 하지 않음
                }
                is PassKeyEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(snackbarData = data)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (passKeyUiState is PassKeyUiState.Loading || authUiState is AuthUiState.Loading) {
                LoadingScreen(paddingValues)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 24.dp), // 좌우 패딩 증가
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WelcomeText(
                        mainText = "Welcome Back",
                        subText = "Sign in to continue",
                        modifier = Modifier.padding(bottom = 48.dp) // 간격 증가
                    )

                    SignInContent(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onSignInClick = {
                            authViewModel.signIn(activity = context, email = email, password = password)
                        },
                        onForgotPasswordClick = {
                            // Navigate to password reset
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SignInContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 일반 로그인 버튼
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Sign In",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // PassKey 로그인 버튼
        OutlinedButton(
            onClick = onSignInClick,  // 동일한 메서드 사용
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "PassKey",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Sign in with PassKey",
                style = MaterialTheme.typography.titleMedium
            )
        }

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                "Forgot Password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PassKeyDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    userEmail: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Use your screen lock",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sign in to Verif AI")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Use PIN")
            }
        }
    )
}
