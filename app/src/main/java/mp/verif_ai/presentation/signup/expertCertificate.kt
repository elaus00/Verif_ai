package mp.verif_ai.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import mp.verif_ai.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource


@Composable
fun expertCertificate() {
    Column(//expert certificate
        verticalArrangement = Arrangement.spacedBy(185.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(390.dp)
            .height(844.dp)
            .background(color = Color(0xFFFFFFFF))
            .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
    ) {
        Row(//top bar
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(width = Variables.spacing0, color = Color(0xFFFFFFFF))
                .width(390.dp)
                .height(40.dp)
                .background(color = Color(0x00FFFFFF))
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
        }
        Column(//main
            verticalArrangement = Arrangement.spacedBy(34.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(350.dp)
                .height(282.dp)
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
            Column(//text
                verticalArrangement = Arrangement.spacedBy(Variables.spacingSmall, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(296.dp)
                    .height(112.dp)
                    .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
            ) {
                Text(
                    text = "Expert Certification",
                    style = TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 48.sp,
                        //fontFamily = FontFamily(Font(R.font.archivo)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF171A1F),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(296.dp)
                        .height(48.dp)
                )
                Text(
                    text = "Please upload a file that can prove your qualifications",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(296.dp)
                        .height(56.dp)
                )
            }
            Column(//upload
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                    .padding(0.5.dp)
                    .width(350.dp)
                    .height(50.dp)
                    .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                    .padding(start = 15.dp, top = 11.dp, end = 15.dp, bottom = 11.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(320.dp)
                        .height(28.dp)
                        .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
                ) {
                    Text(
                        text = "Upload your file ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            //fontFamily = FontFamily(Font(R.font.inter)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFFBCC1CA),
                        ),
                        modifier = Modifier
                            .width(134.dp)
                            .height(28.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.upload),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier
                            .padding(1.dp)
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }
            Row(//submit button
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                    .width(350.dp)
                    .height(52.dp)
                    .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                    .padding(start = Variables.spacing0, top = Variables.spacingMedium, end = Variables.spacing0, bottom = Variables.spacingMedium)
            ) {
                Text(
                    text = "Submit",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    ),
                    modifier = Modifier
                        .width(60.dp)
                        .height(28.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun expertCertificatePreview() {
    expertCertificate()
}