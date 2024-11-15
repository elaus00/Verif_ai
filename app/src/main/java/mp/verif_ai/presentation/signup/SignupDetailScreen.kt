package mp.verif_ai.presentation.signup

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
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import mp.verif_ai.presentation.theme.InputField
import mp.verif_ai.presentation.theme.OnBoardingButton
import mp.verif_ai.presentation.theme.customTypography
@Composable
fun SignupDetailScreen() {
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
                mainText = "Sign Up",
                subText = " "
            )

            // Email Label and InputField Group
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "Email",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_light)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000)
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InputField(placeholder = "Enter your e-mail")

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_light)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000)
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InputField(placeholder = "Enter your password")

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "User Name",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.inter_light)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000)
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InputField(placeholder = "Enter your name")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
            ) {
                OnBoardingButton(text = "Verify", onClick = {})
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignupDetail() {
    SignupDetailScreen()
}
