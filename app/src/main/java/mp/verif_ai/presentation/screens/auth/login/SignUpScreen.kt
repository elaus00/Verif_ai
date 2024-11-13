package mp.verif_ai.presentation.screens.auth.login

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.viewmodel.AuthViewModel
import mp.verif_ai.presentation.viewmodel.common.UiState

/**
 * 회원가입 화면을 구성하는 Composable 함수입니다.
 * 사용자의 이메일, 비밀번호, 닉네임을 입력받아 계정을 생성합니다.
 *
 * @param onSignUpSuccess 회원가입 성공 시 호출될 콜백
 * @param onNavigateBack 뒤로가기(로그인 화면으로) 시 호출될 콜백
 * @param viewModel 회원가입 처리를 담당하는 ViewModel
 */
@Composable
fun SignUpScreen(
    modifier: Modifier,
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // 입력 필드 상태 관리
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    // 에러 메시지 상태 관리
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 비밀번호 일치 여부 확인
    val passwordsMatch by remember(password, confirmPassword) {
        derivedStateOf { password == confirmPassword }
    }

    // 인증 상태 수집
    val authState by viewModel.authState.collectAsState()

    // 인증 상태에 따른 처리
    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                onSignUpSuccess()
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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 제목
            Text(
                text = "Create Account",
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
                isError = errorMessage != null || (!passwordsMatch && confirmPassword.isNotEmpty())
            )

            // 비밀번호 확인 입력 필드
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

            // 닉네임 입력 필드
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
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

            // 회원가입 버튼
            Button(
                onClick = {
                    errorMessage = null // 에러 메시지 초기화
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

            // 로그인 화면으로 돌아가기
            TextButton(
                onClick = onNavigateBack
            ) {
                Text("Already have an account? Sign In")
            }
        }
    }
}

/**
 * SignUpScreen의 미리보기를 제공하는 Composable입니다.
 * 디자인 시스템 테마가 적용된 상태로 미리보기를 제공합니다.
 */
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(
            modifier = Modifier,
            onSignUpSuccess = {},
            onNavigateBack = {}
        )
    }
}