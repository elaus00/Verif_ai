package mp.verif_ai.presentation.screens.auth.signup

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import mp.verif_ai.presentation.screens.auth.SignUpOptionsBottomSheet

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

    // 상태 관리
    var showSignUpOptions by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 이벤트 처리
    LaunchedEffect(events) {
        when (events) {
            is AuthEvent.NavigateToMain -> onNavigateToMain()
            is AuthEvent.ShowError -> {
                snackbarHostState.showSnackbar(
                    message = (events as AuthEvent.ShowError).message,
                    duration = SnackbarDuration.Short
                )
            }
            is AuthEvent.ShowPhoneInput -> showPhoneDialog = true
            is AuthEvent.ShowPasswordInput -> showPasswordDialog = true
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sign up securely with PassKey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Fields
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Action Button (PassKey)
                Button(
                    onClick = {
                        authViewModel.signUpWithCredentialManager(
                            email = email,
                            password = password,
                            nickname = "",  // 닉네임 제거로 빈 문자열 전달
                            context = context
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() &&
                            uiState !is AuthUiState.Loading
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Continue with PassKey")
                    }
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                // Secondary Action Button
                OutlinedButton(
                    onClick = { showSignUpOptions = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Other Options")
                }
            }
        }

        // Dialogs & Bottom Sheet
        if (showSignUpOptions) {
            SignUpOptionsBottomSheet(
                onDismiss = { showSignUpOptions = false },
                onOptionSelected = { option ->
                    authViewModel.handleSignUpOption(option, context)
                }
            )
        }

        if (showPhoneDialog) {
            PhoneSignUpDialog(
                onDismiss = { showPhoneDialog = false },
                onConfirm = { phoneNumber ->
                    authViewModel.signUpWithPhone(phoneNumber, context)
                    showPhoneDialog = false
                }
            )
        }

        if (showPasswordDialog) {
            PasswordSignUpDialog(
                onDismiss = { showPasswordDialog = false },
                onConfirm = { password ->
                    authViewModel.signUpWithPassword(password, context)
                    showPasswordDialog = false
                }
            )
        }
    }
}