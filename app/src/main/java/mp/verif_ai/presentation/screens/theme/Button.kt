package mp.verif_ai.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mp.verif_ai.R
import mp.verif_ai.presentation.screens.theme.customTypography

@Composable
fun OnBoardingButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
    enabled: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF174EB4),
        contentColor = Color.White,
        disabledContainerColor = Color(0xFF2A5AB3),
        disabledContentColor = Color.White,
    ),
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
        onClick = { /*TODO*/ }) {
            Text(
                text = text,
                style = customTypography.certificationButton,
                color = colors.contentColor
            )
        }
    }

