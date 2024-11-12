package mp.verif_ai.presentation.signup


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import mp.verif_ai.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import mp.verif_ai.presentation.onboarding.onboarding

object Variables {
    val spacing0: Dp = 1.dp

}

@Composable
fun signup(){
    Column(//signup
        verticalArrangement = Arrangement.spacedBy(107.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28), ambientColor = Color(0x1F120F28))
            .width(390.dp)
            .height(844.dp)
            .background(color = Color(0xFFFFFFFF))
            .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
    ) {
        Row(//container 3
            horizontalArrangement = Arrangement.spacedBy(222.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top,
        ) {
            // Child views.
        }

        Column(//frame 10
            verticalArrangement = Arrangement.spacedBy(107.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(350.dp)
                .height(493.dp)
                .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
        ) {
            Column(//frame 2
                verticalArrangement = Arrangement.spacedBy(Variables.spacing0, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(229.dp)
                    .height(74.dp)
                    .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
            ) {
                Text(
                    text = "Sign up",
                    style = TextStyle(
                        fontSize = 32.sp,
                        lineHeight = 48.sp,
                        //fontFamily = FontFamily(Font(R.font.archivo)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF171A1F),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(229.dp)
                        .height(48.dp)
                )
                Text(
                    text = "Create an account to continue",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF171A1F),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(229.dp)
                        .height(26.dp)
                )
            }
            Column(//frame 9
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(350.dp)
                    .height(312.dp)
                    .padding(start = Variables.spacing0, top = Variables.spacing0, end = Variables.spacing0, bottom = Variables.spacing0)
            ) {
                Row(//button 2(apple login)
                    horizontalArrangement = Arrangement.spacedBy(Variables.spacing0, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                        .width(341.dp)
                        .height(52.dp)
                        .background(color = Color(0xFF565E6D), shape = RoundedCornerShape(size = 10.dp))
                        .padding(start = 61.dp, top = 2.dp, end = 61.dp, bottom = 2.dp)
                ) {
                    Column(//apple
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(24.dp)
                            .height(23.8307.dp)
                            .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vector),
                            contentDescription = "image description",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .padding(0.85714.dp)
                                .width(3.51454.dp)
                                .height(4.08372.dp)
                                .background(color = Color(0xFFFFFFFF))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.vector__1_),
                            contentDescription = "image description",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .border(width = 2.05714.dp, color = Color(0xFFFFFFFF))
                                .padding(2.05714.dp)
                                .width(15.42879.dp)
                                .height(13.74698.dp)
                        )
                    }
                    Row(//frame4
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(195.dp)
                            .height(48.dp)
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = " Continue with apple",
                            style = TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                //fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            ),
                            modifier = Modifier
                                .width(175.dp)
                                .height(28.dp)
                        )
                    }
                }
                Row(//button3(google)
                    horizontalArrangement = Arrangement.spacedBy(Variables.spacing0, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                        .width(340.11215.dp)
                        .height(52.dp)
                        .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                        .padding(start = 54.dp, top = 2.dp, end = 54.dp, bottom = 2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(23.11216.dp)
                            .height(22.90355.dp)
                            .padding(start = 2.dp, top = 5.dp, end = 2.dp, bottom = 5.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vector2),
                            contentDescription = "image description",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .padding(0.6.dp)
                                .width(12.71216.dp)
                                .height(12.90355.dp)
                                .background(color = Color(0xFF565E6D))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.vector3),
                            contentDescription = "image description",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .padding(0.6.dp)
                                .width(5.4.dp)
                                .height(5.4.dp)
                                .background(color = Color(0xFF565E6D))
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(209.dp)
                            .height(48.dp)
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = " Continue with Google",
                            style = TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                //fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF565E6D),
                            ),
                            modifier = Modifier
                                .width(189.dp)
                                .height(28.dp)
                        )
                    }
                }
                Column(//frame6(or)
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(39.dp)
                        .height(46.dp)
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "Or",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            //fontFamily = FontFamily(Font(R.font.inter)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF171A1F),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .width(19.dp)
                            .height(26.dp)
                    )
                }
                Column(//textbox1-input email
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                        .padding(0.5.dp)
                        .width(350.dp)
                        .height(50.dp)
                        .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(size = 10.dp))
                        .padding(start = 10.dp, top = 1.dp, end = 10.dp, bottom = 1.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(330.dp)
                            .height(48.dp)
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Input your email",
                            style = TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                //fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFFBCC1CA),
                            ),
                            modifier = Modifier
                                .width(136.dp)
                                .height(28.dp)
                        )
                    }
                }
                Column(//button4-continue
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0x00000000), shape = RoundedCornerShape(size = 10.dp))
                        .width(350.dp)
                        .height(52.dp)
                        .background(color = Color(0xFF2A5AB3), shape = RoundedCornerShape(size = 10.dp))
                        .padding(start = 124.dp, top = 2.dp, end = 124.dp, bottom = 2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(102.dp)
                            .height(48.dp)
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = " Continue",
                            style = TextStyle(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                //fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                            ),
                            modifier = Modifier
                                .width(82.dp)
                                .height(28.dp)
                        )
                    }
                }
            }
        }
        Row(//frame3
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(333.dp)
                .height(64.dp)
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
        ) {
            Text(
                text = "By tapping continue, you accept our Terms and\nConditions and Privacy Policy",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    //fontFamily = FontFamily(Font(R.font.inter)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF171A1F),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(313.dp)
                    .height(44.dp)
            )
        }

    }
}

@Preview
@Composable
fun signupPreview() {
    signup()
}

