package mp.verif_ai.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.verif_ai.R
import mp.verif_ai.presentation.theme.customTypography

@Composable
fun OnboardingScreen2() {
    Column(
        verticalArrangement = Arrangement.spacedBy(126.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28), ambientColor = Color(0x1F120F28))
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp)
    ) {
        HeaderRow()
        LogoColumn()
        WelcomeText()
        GetStartedButton()
    }
}

@Composable
fun HeaderRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(222.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .border(width = 1.dp, color = Color(0xFFF3F4F6))
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Transparent)
    ) {
        // Header content if needed
    }
}

@Composable
fun LogoColumn() {
    Column(
        modifier = Modifier
            .border(width = 1.dp, color = Color.White)
            .size(width = 70.dp, height = 75.9429.dp)
            .background(Color.White)
            .padding(1.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.group),
            contentDescription = "App Logo",
            contentScale = ContentScale.None,
            modifier = Modifier
                .size(width = 218.93733.dp, height = 67.9429.dp)
        )
    }
}

@Composable
fun WelcomeText() {
    Column(
        modifier = Modifier
            .width(350.dp)
            .height(234.dp)
            .padding(1.dp),
        verticalArrangement = Arrangement.spacedBy(126.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome!",
            style = customTypography.certificationButton,
            modifier = Modifier.size(width = 197.dp, height = 56.dp)
        )
        Text(
            text = "Verify your conversation\nwith ChatGPT easily!",
            style = customTypography.subWelcome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun GetStartedButton() {
    Row(
        modifier = Modifier
            .border(width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp))
            .size(width = 348.dp, height = 52.dp)
            .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 124.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Get Started",
            style = customTypography.certificationButton,
            modifier = Modifier.size(width = 100.dp, height = 28.dp)
        )
    }
}

@Preview
@Composable
fun OnboardingPreview() {
    OnboardingScreen()
}
