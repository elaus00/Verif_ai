package mp.verif_ai.presentation.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import mp.verif_ai.R
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.viewmodel.AuthUiState
import mp.verif_ai.presentation.viewmodel.AuthViewModel

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // One Tap Client
    val oneTapClient = remember { Identity.getSignInClient(context) }

    // 결과 처리를 위한 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            credential.googleIdToken?.let { token ->
                viewModel.signInWithCredential(AuthCredential.Google(token))
            }
        } catch (e: Exception) {
            viewModel.handleError(e)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.SignedIn -> {
                (navController as NavHostController).navigateToMain()
            }
            else -> {} // 다른 상태는 처리하지 않음
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Image
            GroupImage(R.drawable.group)

            Spacer(modifier = Modifier.height(48.dp))

            // Welcome Text
            WelcomeText(
                mainText = "Verif AI",
                subText = "Verify your AI-conversation\nby professionals!"
            )
        }

        // Buttons Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Email Button
            OnBoardingButton(
                text = "Continue with Email",
                onClick = { navController.navigate(Screen.Auth.SignIn.route) },
                icon = R.drawable.vector
            )

            // Google Button
            OnBoardingButton(
                text = "Continue with Google",
                onClick = {
                    val signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(
                            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
//                                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                        )
                        .setAutoSelectEnabled(true)
                        .build()

                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            try {
                                launcher.launch(
                                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                                )
                            } catch (e: Exception) {
                                viewModel.handleError(e)
                            }
                        }
                        .addOnFailureListener { e ->
                            viewModel.handleError(e)
                        }
                },
                icon = R.drawable.google
            )

            // Terms Text
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun OnBoardingButton(
    text: String,
    onClick: () -> Unit,
    icon: Int? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}