package mp.verif_ai.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputField(placeholder: String) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(fontSize = 18.sp, color = Color(0xFFBCC1CA))
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(20.dp)), // 반경을 더 둥글게 설정
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black)
    )
}