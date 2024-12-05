package mp.verif_ai.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mp.verif_ai.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isFlashOn = sharedPreferences.getBoolean("isFlashOn", false)
        val isVibrationOn = sharedPreferences.getBoolean("isVibrationOn", false)

        // 알림 데이터 처리
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val postId = data["postId"] ?: "알 수 없는 게시글"
            val replyContent = data["replyContent"] ?: "답변 내용 없음"

            Log.d("FCM", "Post ID: $postId, Reply Content: $replyContent")
            showNotification(
                title = "새 답변 알림!",
                message = "게시글 '$postId'에 답변이 달렸습니다: $replyContent"
            )
        } else {
            remoteMessage.notification?.let {
                showNotification(
                    title = it.title ?: "알림",
                    message = it.body ?: "내용 없음"
                )
            }
        }

        // 플래시와 진동 처리
        if (isFlashOn) triggerFlash()
        if (isVibrationOn) triggerVibration()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "reply_notifications_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.apple) // sample icon resource
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun triggerFlash() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_BACK
            }
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, true)
                Thread.sleep(500) // 0.5초 대기
                cameraManager.setTorchMode(cameraId, false)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun triggerVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(500) // 0.5초 진동
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reply_notifications_channel"
            val channelName = "답변 알림"
            val channelDescription = "게시글에 대한 답변 알림을 표시합니다."
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
}
