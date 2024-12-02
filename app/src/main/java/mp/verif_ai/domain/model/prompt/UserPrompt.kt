package mp.verif_ai.domain.model.prompt

import java.util.UUID

data class UserPrompt(
    val id: String = UUID.randomUUID().toString(),  // id 추가
    val content: String,
    val templateId: String? = null,
    val parameters: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)