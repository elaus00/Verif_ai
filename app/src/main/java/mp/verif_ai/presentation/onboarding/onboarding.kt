package mp.verif_ai.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import mp.verif_ai.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

object Variables {
    val spacing0: Dp = 1.dp
}

object Variables2 {
    val spacingMedium: Dp = 12.dp
}

@Composable
fun Onboarding() {
    Column(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28), ambientColor = Color(0x1F120F28))
            .width(390.dp)
            .height(844.dp)
            .background(color = Color(0xFFFFFFFF))
            .padding(
                start = Variables.spacing0,
                top = Variables.spacing0,
                end = Variables.spacing0,
                bottom = Variables.spacing0
            ),
        verticalArrangement = Arrangement.spacedBy(126.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .border(width = Variables.spacing0, color = Color(0xFFFFFFFF))
                .width(390.dp)
                .height(40.dp)
                .background(color = Color(0x00FFFFFF)),
            horizontalArrangement = Arrangement.spacedBy(222.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top,
        ) {
            Image(
                modifier = Modifier
                    .border(width = 0.dp, color = Color(0xFFFFFFFF))
                    .padding(0.dp)
                    .width(72.dp)
                    .height(40.dp),
                painter = painterResource(R.drawable.image_14),
                contentDescription = "image description",
                contentScale = ContentScale.None
            )
            Image(
                modifier = Modifier
                    .border(width = 0.dp, color = Color(0xFFFFFFFF))
                    .padding(0.dp)
                    .width(96.dp)
                    .height(40.dp),
                painter = painterResource(id = R.drawable.image_15),
                contentDescription = "image description",
                contentScale = ContentScale.None
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(222.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ) {
            }
        }
        Column(//image16
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .border(width = Variables.spacing0, color = Color(0xFFFFFFFF))
                .width(70.dp)
                .height(75.9429.dp)
                .background(color = Color(0xFFFFFFFF))
                .padding(start = Variables.spacing0, top = 4.dp, end = Variables.spacing0, bottom = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.group),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(0.dp)
                    .width(218.93733.dp)
                    .height(67.9429.dp)
            )
        }
        Column(//frame1(button)
            verticalArrangement = Arrangement.spacedBy(126.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(350.dp)
                .height(234.dp)
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
            Text(
                text = "Welcome !",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 40.sp,
                    lineHeight = 56.sp,
                    //fontFamily = FontFamily(Font(R.font.archivo)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF171A1F),
                ),
                modifier = Modifier
                    .width(197.dp)
                    .height(56.dp)
            )
            Text(
                text = "Verify your conversation\nwith ChatGPT easily!",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    //fontFamily = FontFamily(Font(R.font.inter)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF171A1F),
                    textAlign = TextAlign.Center,
                )
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .width(348.dp)
                .height(52.dp)
                .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 124.dp, top = Variables2.spacingMedium, end = 124.dp, bottom = Variables2.spacingMedium)
        ) {
            Text(
                text = "Get Started",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    //fontFamily = FontFamily(Font(R.font.inter)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(28.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewOnboarding(){
    Onboarding()
}

