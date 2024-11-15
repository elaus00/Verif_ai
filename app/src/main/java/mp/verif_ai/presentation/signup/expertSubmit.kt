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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource



@Composable
fun expertSubmit(){
    Column(//Expert Submit
        verticalArrangement = Arrangement.spacedBy(104.dp, Alignment.Top),
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
            // Child views.
        }
        Row(//picture
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(78.dp)
                .height(78.dp)
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
            Image(
                painter = painterResource(id = R.drawable.check_circle),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(1.dp)
                    .width(78.dp)
                    .height(78.dp)
            )
        }
        Column(//text
            verticalArrangement = Arrangement.spacedBy(104.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(353.dp)
                .height(213.dp)
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
            Text(
                text = "Request Submitted !",
                style = TextStyle(
                    fontSize = 32.sp,
                    lineHeight = 56.sp,
                    //fontFamily = FontFamily(Font(R.font.archivo)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF171A1F),
                ),
                modifier = Modifier
                    .width(308.dp)
                    .height(56.dp)
            )
            Text(
                text = " You will be notified via Push notification \nonce approved by the administrator.",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 21.6.sp,
                    //fontFamily = FontFamily(Font(R.font.archivo)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(353.dp)
                    .height(53.dp)
            )
        }
    }
}

@Preview
@Composable
fun expertSubmitPreview() {
    expertSubmit()
}