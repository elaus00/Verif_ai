package mp.verif_ai.presentation.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import mp.verif_ai.presentation.theme.InputField
import mp.verif_ai.presentation.theme.OnBoardingButton
import mp.verif_ai.presentation.theme.customTypography

@Composable
fun ExpertCertificationScreen() {
    Column(
        modifier = Modifier
            .shadow(elevation = 6.dp, spotColor = Color(0x1F120F28))
            .fillMaxSize()
            .padding(top = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomeText(
                mainText = "Expert Certification",
                subText = "Please upload a file that can\nprove your qualification"
            )
            Spacer(modifier = Modifier.height(30.dp))
            // 파일 업로드 필드를 추가
            FileUploadField(
                placeholder = "Select a file",
                onFileSelected = { uri ->
                    // 파일 선택 후 처리할 로직 추가 (예: 업로드 요청)
                }
            )

        }



        // Buttons at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            OnBoardingButton(text = "Submit", onClick = {})
        }
    }
}

@Composable
fun FileUploadField(
    placeholder: String,
    onFileSelected: (Uri?) -> Unit
) {
    var fileName by remember { mutableStateOf("") } // 선택된 파일명을 표시할 변수
    val context = LocalContext.current

    // 파일 선택기를 호출하기 위한 런처 설정
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            fileName = uri?.lastPathSegment ?: "No file selected" // 파일명을 표시
            onFileSelected(uri) // 선택된 파일의 URI 반환
        }
    }

    // 파일 선택기 호출 함수
    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    // 기존 InputField 스타일과 동일하게 구현
    OutlinedTextField(
        value = fileName,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text(text = placeholder) },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.Black),
        trailingIcon = {
            Image(
                painter = painterResource(id = R.drawable.upload),
                contentDescription = "Upload Button",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { openFilePicker() } // 클릭 시 파일 선택기 호출
            )
        }
    )
}

@Preview
@Composable
fun PreviewExpertCertification() {
    ExpertCertificationScreen()
}
