package mp.verif_ai.domain.model.question

data class TrendingQuestion(
    val id: String,
    val title: String,
    val viewCount: Int,
    val commentCount: Int
)