package mp.verif_ai.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import mp.verif_ai.presentation.navigation.AppNavigation
import mp.verif_ai.presentation.screens.theme.Verif_aiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // FCM 토큰 요청
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // 서버로 토큰 전송 (예: Retrofit API 호출)
                Log.d("FCM", "FCM Token: $token")
            } else {
                Log.e("FCM", "Token generation failed", task.exception)
            }
        }

//        // 토큰 발급 테스트용
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val token = task.result
//                Log.d("FCM", "Token: $token")
//            } else {
//                Log.e("FCM", "Token generation failed", task.exception)
//            }
//        }

        setContent {
            Verif_aiTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Verif_aiTheme {
        MainScreen()
    }
}
