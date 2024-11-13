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
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.UiState

/**
 * 로그인 화면을 구성하는 Composable 함수입니다.
 * 사용자의 이메일과 비밀번호를 입력받아 로그인을 처리합니다.
 *
 * @param onSignInSuccess 로그인 성공 시 호출될 콜백
 * @param viewModel 로그인 처리를 담당하는 ViewModel (기본값으로 Hilt가 제공)
 */
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignInSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    onSignUpClick: () -> Unit
) {
    // 이메일과 비밀번호 상태 관리
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 에러 메시지 상태 관리
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 인증 상태 수집
    val authState by viewModel.authState.collectAsState()

    // 인증 상태에 따른 처리
    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                onSignInSuccess()
            }
            is UiState.Error -> {
                errorMessage = (authState as UiState.Error).message
            }
            else -> { /* Loading 또는 Initial 상태 처리 필요 없음 */ }
        }
    }

    // UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 제목
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.headlineMedium
            )

            // 이메일 입력 필드
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            // 비밀번호 입력 필드
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            // 에러 메시지 표시
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 로그인 버튼
            Button(
                onClick = {
                    errorMessage = null // 에러 메시지 초기화
                    viewModel.signIn(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank() &&
                        authState !is UiState.Loading
            ) {
                if (authState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In")
                }
            }

            // 회원가입 링크
            TextButton(
                onClick = { /* TODO: 회원가입 화면으로 이동 */ }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}


/**
 * SignInScreen의 미리보기를 제공하는 Composable입니다.
 * 디자인 시스템 테마가 적용된 상태로 미리보기를 제공합니다.
 */
@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen(
            onSignInSuccess = {},
            onSignUpClick = {}
        )
    }
}