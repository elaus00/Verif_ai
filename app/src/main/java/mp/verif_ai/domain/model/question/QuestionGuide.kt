package mp.verif_ai.domain.model.question

import kotlinx.serialization.Serializable

@Serializable
data class QuestionGuide(
    val category: String,
    val title: String,
    val description: String,
    val exampleQuestions: List<String>,
    val tips: List<String>,
    val doList: List<String>,
    val dontList: List<String>
)