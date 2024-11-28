package mp.verif_ai.presentation.screens.auth.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mp.verif_ai.presentation.screens.auth.OnBoardingButton
import mp.verif_ai.presentation.screens.auth.WelcomeText
import mp.verif_ai.presentation.screens.theme.InputField

@Composable
fun EmailVerificationScreen(
    email: String? = null,
    onVerificationComplete: (String?) -> Unit
) {
    var verificationCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomeText(
                mainText = "Verification",
                subText = "Please enter the verification code\nwe sent to your e-mail address"
            )

            Spacer(modifier = Modifier.height(30.dp))

            InputField(
                placeholder = "Enter the verification code",
                value = verificationCode,
                onValueChange = { newValue ->
                    // Only allow numeric input with maximum 6 characters
                    if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                        verificationCode = newValue
                        isError = false
                    }
                },
                isError = isError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (verificationCode.length == 6) {
                            // TODO: 실제 이메일 인증 로직 구현 필요
                            onVerificationComplete("dummy_user_id")
                        } else {
                            isError = true
                        }
                    }
                )
            )
        }

        // Buttons at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
        ) {
            OnBoardingButton(
                text = "Verify",
                onClick = {
                    if (verificationCode.length == 6) {
                        // TODO: 실제 이메일 인증 로직 구현 필요
                        onVerificationComplete("dummy_user_id")
                    } else {
                        isError = true
                    }
                },
                enabled = verificationCode.isNotEmpty()
            )
        }
    }
}
