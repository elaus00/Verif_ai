package mp.verif_ai.presentation.screens.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var uploadStatus by remember { mutableStateOf("Idle") }

    // 이미지 업로드를 위한 갤러리 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                uploadStatus = "Uploading..."
                viewModel.uploadProfileImageToFirestore(
                    uri = it,
                    userId = "currentUserId", // 사용자 ID 필요 시 변경
                    onSuccess = { uploadStatus = "Upload successful!" },
                    onFailure = { exception ->
                        uploadStatus = "Upload failed: ${exception.message}"
                    }
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF171A1F)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 업로드 상태 표시
                Text(
                    text = "Upload Status: $uploadStatus",
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 프로필 이미지 업로드 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color(0xFFD9D9D9), RoundedCornerShape(10.dp))
                        .clickable { galleryLauncher.launch(Intent(Intent.ACTION_PICK).setType("image/*")) },
                    contentAlignment = Alignment.Center
                ) {
                    uiState.profileImageUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Text(
                        text = "Tap to select a profile picture",
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 설정 토글
                SettingToggleItem(
                    title = "Flash on Notification",
                    isChecked = uiState.isFlashOn,
                    onCheckedChange = { isChecked ->
                        viewModel.setFlashState(isChecked)
                        saveToPreferences(context, "isFlashOn", isChecked)
                    }
                )

                SettingToggleItem(
                    title = "Vibration on Notification",
                    isChecked = uiState.isVibrationOn,
                    onCheckedChange = { isChecked ->
                        viewModel.setVibrationState(isChecked)
                        saveToPreferences(context, "isVibrationOn", isChecked)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 섹션
                SettingsButton("Change Info",)
                Spacer(modifier = Modifier.height(16.dp))
                SettingsButton("Logout")
                Spacer(modifier = Modifier.height(16.dp))
                SettingsButton("Payment") {
                    navController.navigate(Screen.MainNav.Settings.Payment.Methods.route)
                }
            }
        }
    )
}

@Composable
fun SettingToggleItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsButton(title: String,onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable (enabled = onClick != null) { onClick?.invoke() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF171A1F)
            )
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

fun saveToPreferences(context: Context, key: String, value: Boolean) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putBoolean(key, value).apply()
}

