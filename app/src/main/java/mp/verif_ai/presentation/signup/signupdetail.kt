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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import mp.verif_ai.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Signupdetail() {
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
                .padding(top = 80.dp)
                .width(114.dp)
                .height(48.dp),
            text = "Sign up",
            style = TextStyle(
                fontSize = 32.sp,
                lineHeight = 48.sp,
                fontFamily = FontFamily(Font(R.font.archivo_regular)),
                fontWeight = FontWeight(700),
                color = Color(0xFF171A1F),
                textAlign = TextAlign.Center,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp) // 전체 Row를 왼쪽으로 이동
        ) {
            Text(
                modifier = Modifier
                    .width(102.dp)
                    .height(15.dp),
                text = "Email",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                )
            )
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .padding(0.dp)
                .width(349.dp)
                .height(51.dp)
                .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 20.dp, top = 10.dp, end = 165.dp, bottom = 11.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(164.dp)
                    .height(28.dp),
                text = "abcdef@gmail.com",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_extralight)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            text = "Password",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontFamily = FontFamily(Font(R.font.inter_light)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .padding(0.dp)
                .width(349.dp)
                .height(51.dp)
                .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 20.dp, top = 12.dp, end = 153.dp, bottom = 11.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(176.dp)
                    .height(28.dp),
                text = "Enter your Password",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_extralight)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFBCC1CA),
                )
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            text = "User Name",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontFamily = FontFamily(Font(R.font.inter_light)),
                fontWeight = FontWeight(700),
                color = Color(0xFF000000),
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .padding(0.dp)
                .width(349.dp)
                .height(51.dp)
                .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 20.dp, top = 12.dp, end = 141.dp, bottom = 11.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(188.dp)
                    .height(28.dp),
                text = "Enter your User Name",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_extralight)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFBCC1CA),
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .width(350.dp)
                .height(52.dp)
                .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                .padding(top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(67.dp)
                    .height(28.dp),
                text = "Sign Up",
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
