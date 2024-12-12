package mp.verif_ai.presentation.screens.auth.expertsignup

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import mp.verif_ai.R
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import mp.verif_ai.presentation.navigation.navigateToMain
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.OnBoardingButton
import mp.verif_ai.presentation.screens.auth.WelcomeText

@Composable
fun ExpertOnboardingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GroupImage(R.drawable.group)
            Spacer(modifier = Modifier.height(48.dp))
            WelcomeText(
                mainText = "Verif AI",
                subText = "Verify your AI-conversation \n by professionals!"
            )
        }

        // Buttons at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            OnBoardingButton(
                text = "Start as a respondent",
                onClick = {
                    navController.navigate(Screen.Auth.ExpertCertification.createRoute(userId = "test"))
                }
            )
            OnBoardingButton(
                text = "Start as a questioner",
                onClick = {
                    navController.navigateToMain()
                }
            )
        }
    }
}

@Composable
fun GroupImage(imageResId: Int) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "group image",
        contentScale = ContentScale.None,
        modifier = Modifier
            .wrapContentSize()
    )
}

@Preview
@Composable
fun PreviewCertification() {
    ExpertOnboardingScreen(navController = NavHostController(LocalContext.current))
}
