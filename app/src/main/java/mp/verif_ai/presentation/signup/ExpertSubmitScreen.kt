package mp.verif_ai.presentation.signup

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.verif_ai.R
import mp.verif_ai.presentation.signup.GroupImage
import mp.verif_ai.presentation.theme.OnBoardingButton

@Composable
fun ExpertSubmitScreen() {
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
            GroupImage(R.drawable.check_circle)
            Spacer(modifier = Modifier.height(48.dp))
            mp.verif_ai.presentation.signup.WelcomeText(
                mainText = "Request Submitted !",
                subText = "You will be notified via Push notification\nonce approved by the administrator."
            )
        }
    }
}

@Preview
@Composable
fun PreviewExpertSubmit() {
    ExpertSubmitScreen()
}
