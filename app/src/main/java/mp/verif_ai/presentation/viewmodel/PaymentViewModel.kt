package mp.verif_ai.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PaymentViewModel : ViewModel() {

    private val _userPoints = mutableStateOf(0) // 포인트 상태 저장
    val userPoints: State<Int> = _userPoints

    private val firestore = Firebase.firestore

    init {
        initializeUserPoints()
    }

    // 사용자 포인트 초기화 또는 동기화
    private fun initializeUserPoints() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val initialPoints = 2000 // domain.model.auth.User의 초기값과 동일하게 설정

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.contains("points")) {
                    // Firestore에 points 필드가 있으면 값 가져오기
                    val points = document.getLong("points")?.toInt() ?: 0
                    _userPoints.value = points
                } else {
                    // Firestore에 points 필드가 없으면 초기값 추가
                    firestore.collection("users").document(userId)
                        .set(mapOf("points" to initialPoints), SetOptions.merge())
                        .addOnSuccessListener {
                            _userPoints.value = initialPoints
                        }
                        .addOnFailureListener {
                            Log.e("PaymentViewModel", "Failed to initialize user points: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("PaymentViewModel", "Failed to fetch user data: ${it.message}")
            }
    }

    // Firestore의 사용자 포인트 업데이트
    fun updateUserPoints(pointsToAdd: Int) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val newPoints = _userPoints.value + pointsToAdd

        firestore.collection("users").document(userId)
            .update("points", newPoints)
            .addOnSuccessListener {
                _userPoints.value = newPoints
            }
            .addOnFailureListener {
                Log.e("PaymentViewModel", "Failed to update user points: ${it.message}")
            }
    }
}

