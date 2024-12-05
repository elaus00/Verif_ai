package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mp.verif_ai.R
import mp.verif_ai.presentation.screens.theme.LoginButton
import mp.verif_ai.presentation.screens.theme.OnBoardingButton
import mp.verif_ai.presentation.theme.InputField

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onContinue: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(107.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .shadow(6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            WelcomeText(
                mainText = "Sign Up",
                subText = "Create an account to continue"
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Social Login Buttons
            LoginButton(
                text = "Continue with Apple",
                backgroundColor = Color(0xFF565E6D),
                iconRes = R.drawable.apple,
                onClick = { /* TODO: Add Apple login action */ }
            )

            Spacer(modifier = Modifier.height(15.dp))

            LoginButton(
                text = "Continue with Google",
                backgroundColor = Color(0xFFBCC1CA),
                iconRes = R.drawable.google,
                textColor = Color(0xFF565E6D),
                onClick = { /* TODO: Add Google login action */ }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Or",
                style = TextStyle(fontSize = 16.sp, color = Color(0xFF171A1F), textAlign = TextAlign.Center),
                modifier = Modifier.padding(10.dp)
            )

            InputField(
                placeholder = "Input your email",
                value = email,
                onValueChange = {
                    email = it
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

            Spacer(modifier = Modifier.height(15.dp))

            OnBoardingButton(
                text = "Continue",
                onClick = {
                    if (email.contains("@")) {
                        onContinue(email)
                    } else {
                        errorMessage = "Please enter a valid email"
                    }
                },
                enabled = email.isNotBlank()
            )
        }

        Text(
            text = "By tapping continue, you accept our Terms and\nConditions and Privacy Policy",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF171A1F),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}