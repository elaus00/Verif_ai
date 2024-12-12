package mp.verif_ai.presentation.screens.auth.signup

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.viewmodel.AuthViewModel

@Composable
fun SignUpFormScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onSignUpComplete: (String) -> Unit
) {}