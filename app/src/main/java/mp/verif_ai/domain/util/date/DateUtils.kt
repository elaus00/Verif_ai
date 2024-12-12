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

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val oneDayMillis = 86400000 // 24시간을 밀리초로
        val oneWeekMillis = 604800000 // 7일을 밀리초로
        val oneMonthMillis = 2592000000 // 30일을 밀리초로
        val oneYearMillis = 31536000000 // 365일을 밀리초로

        return when {
            // 1분 이내
            diff < 60000 -> "방금 전"
            // 1시간 이내
            diff < 3600000 -> "${diff / 60000}분 전"
            // 하루 이내
            diff < oneDayMillis -> "${diff / 3600000}시간 전"
            // 일주일 이내
            diff < oneWeekMillis -> "${diff / oneDayMillis}일 전"
            // 한달 이내
            diff < oneMonthMillis -> "${diff / oneWeekMillis}주 전"
            // 1년 이내
            diff < oneYearMillis -> "${diff / oneMonthMillis}개월 전"
            // 1년 이상
            else -> "${diff / oneYearMillis}년 전"
        }
    }
}