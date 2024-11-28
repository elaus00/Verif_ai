package mp.verif_ai.presentation.screens.auth

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import mp.verif_ai.R
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.passkey.PassKeyStatus
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
    val scope = rememberCoroutineScope()

    // 인증 상태 관찰
    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Authenticated -> {
                (navController as NavHostController).navigateToMain()
            }
            else -> {}
        }
    }

    // PassKey 상태 관찰
    LaunchedEffect(passKeyUiState) {
        when (passKeyUiState) {
            is PassKeyUiState.SignedIn -> {
                (navController as NavHostController).navigateToMain()
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // PassKey Sign In Button (if available)
        when (passKeyUiState) {
            is PassKeyUiState.StatusChecked -> {
                if ((passKeyUiState as PassKeyUiState.StatusChecked).status == PassKeyStatus.AVAILABLE) {
                    OutlinedButton(
                        onClick = { authViewModel.signIn(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "PassKey Sign In",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue with PassKey")
                    }
                }
            }
            else -> Unit
        }

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        // Sign In Button
        Button(
            onClick = {
                authViewModel.signIn(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Sign In")
        }

        // Create Account Link
        TextButton(
            onClick = { navController.navigate(Screen.Auth.SignUp.route) }
        ) {
            Text(
                text = "Don't have an account? Create one",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Error Messages
        when {
            authUiState is AuthUiState.Error -> {
                Text(
                    text = (authUiState as AuthUiState.Error).exception.message
                        ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            passKeyUiState is PassKeyUiState.Error -> {
                Text(
                    text = (passKeyUiState as PassKeyUiState.Error).error.message ?: "PassKey error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Loading Indicators
        when {
            authUiState is AuthUiState.Loading ||
                    passKeyUiState is PassKeyUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}