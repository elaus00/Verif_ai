package mp.verif_ai.domain.util.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data object DateUtils {
    private val fullDateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
    private val shortDateFormatter = SimpleDateFormat("MM/dd HH:mm", Locale.KOREA)

    fun formatFullDate(timestamp: Long): String = fullDateFormatter.format(Date(timestamp))
    fun formatShortDate(timestamp: Long): String = shortDateFormatter.format(Date(timestamp))

    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "방금 전"
            diff < 3600_000 -> "${diff / 60_000}분 전"
            diff < 86400_000 -> "${diff / 3600_000}시간 전"
            diff < 604800_000 -> "${diff / 86400_000}일 전"
            else -> formatShortDate(timestamp)
        }
    }
}