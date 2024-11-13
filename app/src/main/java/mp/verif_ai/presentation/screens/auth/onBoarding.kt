package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnBoardingScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToMain: () -> Unit,  // 추가
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Welcome Text Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
        ) {
            Text(
                text = "Verif AI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "전문가의 답변으로 \n 인공지능 답변을 검증하세요",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Buttons Section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onNavigateToSignUp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("회원가입")
            }

            OutlinedButton(
                onClick = onNavigateToSignIn,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("로그인")
            }

            // 테스트용 버튼
            TextButton(
                onClick = onNavigateToMain,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = "[TEST] 메인 화면으로 바로가기",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}