package mp.verif_ai.domain.util

import android.content.Context
import android.content.Intent

// utils/ShareUtils.kt
fun Context.shareText(text: String, title: String? = null) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    startActivity(shareIntent)
}