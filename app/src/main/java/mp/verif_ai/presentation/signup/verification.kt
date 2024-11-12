package mp.verif_ai.presentation.signup

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

object Variables {
    val spacing0: Dp = 1.dp
    val spacingSmall: Dp = 8.dp
    val spacingMedium: Dp = 12.dp
}

@Composable
fun Verification() {
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
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top),
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

        Text(
            modifier = Modifier
                .padding(top = 100.dp)
                .width(296.dp)
                .height(48.dp),
            text = "Verification",
            style = TextStyle(
                fontSize = 32.sp,
                lineHeight = 48.sp,
                fontFamily = FontFamily(Font(R.font.archivo_regular)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )
        )
        Text(
            modifier = Modifier
                .width(262.dp)
                .height(60.dp),  // height 값을 늘려 여러 줄 텍스트가 보이도록 설정
            text = "Please enter the verification code we sent to your email address",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontFamily = FontFamily(Font(R.font.inter_extralight)),
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center
            ),
            maxLines = 2,  // 최대 두 줄까지 표시
            softWrap = true  // 줄바꿈 허용
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .padding(0.5.dp)
                .width(349.dp)
                .height(51.dp)
                .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 20.dp, top = 12.dp, end = 104.dp, bottom = 11.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(225.dp)
                    .height(28.dp),
                text = "Enter the verification code",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFBCC1CA),
                )
            )
        }

        Row(
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .width(350.dp)
                .height(52.dp)
                .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .width(50.dp)
                    .height(28.dp),
                text = "Verify",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
        }


    }
}

@Preview
@Composable
fun PreviewVerification(){
    Verification()
}
