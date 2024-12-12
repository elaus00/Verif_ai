package mp.verif_ai.presentation.screens.settings.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

object ProfileUtils {
    const val REQUEST_IMAGE_PICK = 1001

    fun openGallery(context: Context): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }

    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        return try {
            uri?.let {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
