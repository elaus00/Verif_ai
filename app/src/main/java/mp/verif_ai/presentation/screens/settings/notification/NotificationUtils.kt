package mp.verif_ai.presentation.screens.settings.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast

fun toggleFlash(context: Context, isFlashOn: Boolean): Boolean {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    return try {
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).run {
                get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }
        }
        if (cameraId != null) {
            cameraManager.setTorchMode(cameraId, isFlashOn)
            true
        } else {
            false
        }
    } catch (e: CameraAccessException) {
        e.printStackTrace()
        false
    }
}

fun toggleVibration(context: Context, isVibrationOn: Boolean): Boolean {
    return try {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (isVibrationOn) {
            vibrator.cancel()
        } else {
            vibrator.vibrate(500) // 0.5초 진동
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun toggleNotifications(context: Context, areNotificationsEnabled: Boolean): Boolean {
    return try {
        if (areNotificationsEnabled) {
            Toast.makeText(context, "Please disable notifications from system settings.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
        } else {
            Toast.makeText(context, "Please enable notifications from system settings.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
