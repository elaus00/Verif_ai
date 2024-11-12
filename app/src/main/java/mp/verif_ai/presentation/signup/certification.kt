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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Certification() {
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
        verticalArrangement = Arrangement.spacedBy(95.dp, Alignment.Top),
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
                modifier = Modifier
                    .offset(y = -100.dp)
                    .width(353.dp)
                    .height(53.dp),
                text = "If you want to be qualified as a respondent, please authenticate the additional information",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 26.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(y = -40.dp)
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .width(350.dp)
                .height(52.dp)
                .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 92.dp, top = 12.dp, end = 92.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(166.dp)
                    .height(28.dp),
                text = "Expert Certification",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter_light)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(y = -100.dp)
                .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                .width(350.dp)
                .height(52.dp)
                .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                .padding(start = 86.dp, top = 12.dp, end = 86.dp, bottom = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(178.dp)
                    .height(28.dp),
                text = "Start as a questioner",
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
fun PreviewCertification(){
    Certification()
}

