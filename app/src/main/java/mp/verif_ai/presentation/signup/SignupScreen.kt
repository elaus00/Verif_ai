package mp.verif_ai.presentation.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mp.verif_ai.R
import mp.verif_ai.presentation.theme.OnBoardingButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import mp.verif_ai.presentation.theme.InputField

@Composable
fun SignUpScreen() {
    Column(
        verticalArrangement = Arrangement.spacedBy(107.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .shadow(6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp)) // 상단 공백 추가
        SignUpContent()

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

@Composable
fun SignUpContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and Subtitle
        WelcomeText(
            mainText = "Sign Up",
            subText = "Create an account to continue"
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Apple Login Button
        LoginButton(
            text = "Continue with Apple",
            backgroundColor = Color(0xFF565E6D),
            iconRes = R.drawable.apple,
            onClick = { /* TODO: Add Apple login action */ }
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Google Login Button
        LoginButton(
            text = "Continue with Google",
            backgroundColor = Color(0xFFBCC1CA),
            iconRes = R.drawable.google,
            textColor = Color(0xFF565E6D),
            onClick = { /* TODO: Add Google login action */ }
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Or Text
        Text(
            text = "Or",
            style = TextStyle(fontSize = 16.sp, color = Color(0xFF171A1F), textAlign = TextAlign.Center),
            modifier = Modifier.padding(10.dp)
        )

        // Email Input Placeholder
        InputField(placeholder = "Input your email")

        Spacer(modifier = Modifier.height(15.dp))

        // Continue Button
        OnBoardingButton(text = "Continue", onClick = {})
    }
}

@Composable
fun LoginButton(
    text: String,
    backgroundColor: Color,
    iconRes: Int,
    textColor: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(1.dp, Color.Transparent, shape = RoundedCornerShape(10.dp))
            .background(backgroundColor, shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                contentScale = ContentScale.None,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = TextStyle(fontSize = 18.sp, color = textColor),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    SignUpScreen()
}
