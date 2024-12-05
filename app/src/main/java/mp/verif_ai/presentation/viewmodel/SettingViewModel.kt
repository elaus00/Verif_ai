package mp.verif_ai.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import mp.verif_ai.presentation.screens.settings.notification.toggleFlash
import mp.verif_ai.presentation.screens.settings.notification.toggleNotifications
import mp.verif_ai.presentation.screens.settings.notification.toggleVibration

data class SettingsUiState(
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

    fun subscribeToProfileUpdates(
        userId: String,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }
                snapshot?.getString("profileImageUrl")?.let { imageUrl ->
                    _uiState.update { it.copy(otherUserProfileImageUri = imageUrl) }
                }
            }
    }

    fun fetchUserProfileImageFromFirestore(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userDocument = firestore.collection("users").document(userId)

        userDocument.get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profileImageUrl")
                if (imageUrl != null) {
                    _uiState.update { it.copy(otherUserProfileImageUri = imageUrl) }
                    onSuccess()
                } else {
                    onFailure(Exception("No profile image found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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
