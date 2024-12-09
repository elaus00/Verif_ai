package mp.verif_ai.domain.model.search

import kotlinx.serialization.Serializable
import mp.verif_ai.domain.model.question.QuestionStatus

@Serializable
data class SearchQuery(
    val query: String,
    val filters: SearchFilters,
    val page: Int = 1,
    val pageSize: Int = 20
)

@Serializable
data class SearchFilters(
    val categories: List<String> = emptyList(),
    val status: List<QuestionStatus> = emptyList(),
    val dateRange: DateRange? = null,
    val hasAcceptedAnswer: Boolean? = null,
    val minPoints: Int? = null
)

@Serializable
data class DateRange(
    val from: Long,
    val to: Long
)