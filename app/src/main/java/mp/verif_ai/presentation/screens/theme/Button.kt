package mp.verif_ai.presentation.screens.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource


@Composable
fun OnBoardingButton(
    text: String,
    onClick: () -> Unit = {},
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF174EB4),
        contentColor = Color.White,
        disabledContainerColor = Color(0xFF2A5AB3),
        disabledContentColor = Color.White,
    ),
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .background(color = colors.containerColor, shape = RoundedCornerShape(size = 10.dp)),
        colors = colors,
        onClick = { onClick() }) {
            Text(
                text = text,
                style = CustomTypography.certificationButton,
                color = colors.contentColor
            )
        }
    }

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color,
    iconRes: Int,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$text icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}