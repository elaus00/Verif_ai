package mp.verif_ai.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.BuildConfig
import mp.verif_ai.R
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.theme.OnBoardingButton

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val verificationCode by remember { mutableStateOf("") }
    val isDebug = true

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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Image
            GroupImage(R.drawable.group)

            Spacer(modifier = Modifier.height(48.dp))

            // Welcome Text
            WelcomeText(
                mainText = "Verif AI",
                subText = "Verify your AI-conversation\n by professionals!"
            )
        }

        // Buttons Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sign Up Button
            OnBoardingButton(
                text = "Get Started",
                onClick = { navController.navigate(Screen.Auth.SignUp.route) },
                enabled = verificationCode.isNotEmpty()
            )

            // Sign In Button
            OnBoardingButton(
                text = "Already have an account? Sign In",
                onClick = { navController.navigate(Screen.Auth.SignIn.route) },
                enabled = verificationCode.isNotEmpty(),
            )

            // Test Button (개발 중일 때만 표시)
            if (isDebug) {
                TextButton(
                    onClick = { navController.navigate(Screen.MainNav.Home.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = "[TEST] Skip to Main",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}