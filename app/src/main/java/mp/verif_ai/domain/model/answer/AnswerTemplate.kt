package mp.verif_ai.domain.model.answer

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AnswerTemplate(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val title: String,
    val structure: List<TemplateSection>,
    val isDefault: Boolean = false
)


@Serializable
data class TemplateSection(
    val title: String,
    val description: String,
    val isRequired: Boolean = true,
    val placeholderText: String = ""
)