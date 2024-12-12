package mp.verif_ai.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.model.auth.UserType
import mp.verif_ai.presentation.screens.settings.notification.toggleNotifications

data class SettingsUiState(
    val user: User = User(),
    val profileImage: Bitmap? = null,
    val profileImageUri: Uri? = null,
    val isFlashOn: Boolean = false,
    val isVibrationOn: Boolean = false,
    val areNotificationsEnabled: Boolean = false,
    val otherUserProfileImageUri: String? = null,
    val isUploadComplete: Boolean = false
)

open class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference.child("profile_images")
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // 프로필 이미지 업로드
    fun uploadProfileImageToFirestore(
        uri: Uri,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val imageRef = storageReference.child(fileName)

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveImageUrlToFirestore(userId, downloadUrl.toString(), {
                        _uiState.update { state ->
                            state.copy(
                                profileImageUri = Uri.parse(downloadUrl.toString()),
                                otherUserProfileImageUri = downloadUrl.toString(),
                                isUploadComplete = true
                            )
                        }
                        onSuccess()
                    }, onFailure)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun saveImageUrlToFirestore(
        userId: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocument = firestore.collection("users").document(userId)

        userDocument.set(mapOf("profileImageUrl" to imageUrl), SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // 업로드 후 유저별
    fun uploadPdfToFirestore(
        uri: Uri,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
            .child("verification_files/$userId/${System.currentTimeMillis()}.pdf")

        storageReference.putFile(uri)
            .addOnSuccessListener {
                onSuccess()
                // PDF 업로드 성공 후 일정 시간 후 UserType 업데이트
                Handler(Looper.getMainLooper()).postDelayed({
                    updateUserType(userId, UserType.EXPERT)
                }, 5000) // 5초 후 업데이트
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun updateUserType(userId: String, newType: UserType) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("type", newType.name)
            .addOnSuccessListener {
                // Firestore 업데이트 성공 시 UI 상태도 업데이트
                _uiState.value = _uiState.value.copy(
                    user = _uiState.value.user.copy(type = newType)
                )
            }
            .addOnFailureListener { exception ->
                // 업데이트 실패 시 로깅
                Log.e("SettingsViewModel", "Failed to update user type: ${exception.message}")
            }
    }



    // 플래시 제어
    fun setFlashState(isEnabled: Boolean) {
        _uiState.update { it.copy(isFlashOn = isEnabled) }
    }

    // 진동 제어
    fun setVibrationState(isEnabled: Boolean) {
        _uiState.update { it.copy(isVibrationOn = isEnabled) }
    }

    // 알림 제어
    fun toggleNotifications(context: Context) {
        val success = toggleNotifications(context, !_uiState.value.areNotificationsEnabled)
        if (success) {
            _uiState.update { it.copy(areNotificationsEnabled = !_uiState.value.areNotificationsEnabled) }
        }
    }
}
