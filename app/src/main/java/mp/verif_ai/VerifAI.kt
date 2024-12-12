package mp.verif_ai

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.auth.FirebaseAuth

@HiltAndroidApp
class VerifAI : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
//        FirebaseAuth.getInstance().signOut()
//        FirebaseAuth.getInstance().signInAnonymously()
    }
}